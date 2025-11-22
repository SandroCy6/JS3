package com.tuapp.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuapp.backend.dto.boleta.*;
import com.tuapp.backend.dto.reniec.ReniecResponse;
import com.tuapp.backend.dto.sunat.SunatRucResponse;
import com.tuapp.backend.model.Boleta;
import com.tuapp.backend.model.Producto;
import com.tuapp.backend.repository.BoletaRepository;
import com.tuapp.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class BoletaService {

    private final ProductoRepository productoRepository;
    private final BoletaRepository boletaRepository;
    private final ReniecClient reniecClient;
    private final SunatClient sunatClient;
    private final TipoCambioClient tipoCambioClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BoletaResponse generarBoleta(GenerarBoletaRequest req) {

        // 1. Obtener datos del cliente
        ClienteBoletaResponse cliente = obtenerDatosCliente(
                req.tipoDocumento(),
                req.numeroDocumento()
        );

        // 2. Procesar productos y calcular total en soles
        BigDecimal totalSoles = BigDecimal.ZERO;
        var productosRespuesta = new java.util.ArrayList<ProductoBoletaResponse>();
        var productosParaActualizar = new java.util.ArrayList<Producto>();

        for (ItemBoletaRequest item : req.items()) {
            Producto p = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.productoId()));

            if (!p.isActivo()) {
                throw new IllegalArgumentException("El producto " + p.getNombre() + " está inactivo");
            }
            if (p.getStock() < item.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto " + p.getNombre());
            }

            BigDecimal cantidad = BigDecimal.valueOf(item.cantidad());
            BigDecimal subtotal = p.getPrecio().multiply(cantidad);

            totalSoles = totalSoles.add(subtotal);

            productosRespuesta.add(new ProductoBoletaResponse(
                    p.getId(),
                    p.getNombre(),
                    item.cantidad(),
                    p.getPrecio(),
                    subtotal
            ));

            // Restar stock
            p.setStock(p.getStock() - item.cantidad());
            productosParaActualizar.add(p);
        }

        // 3. Obtener tipo de cambio (venta) de hoy
        BigDecimal tipoCambio = tipoCambioClient.obtenerTipoCambioVentaHoy();

        // 4. Calcular totales en ambas monedas
        String moneda = req.moneda().toUpperCase();
        BigDecimal totalSolesFinal = totalSoles.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDolaresFinal;

        if (moneda.equals("PEN")) {
            totalDolaresFinal = totalSolesFinal
                    .divide(tipoCambio, 2, RoundingMode.HALF_UP);
        } else if (moneda.equals("USD")) {
            // Los precios están en soles, pero el cliente paga en dólares → mostramos ambos
            totalDolaresFinal = totalSolesFinal
                    .divide(tipoCambio, 2, RoundingMode.HALF_UP);
        } else {
            throw new IllegalArgumentException("Moneda no soportada: " + moneda);
        }

        // 5. Actualizar stock en BD
        productoRepository.saveAll(productosParaActualizar);

        // 6. Construir detalleJson (cliente + productos)
        String detalleJson = construirDetalleJson(cliente, productosRespuesta);

        // 7. Crear y guardar Boleta
        Boleta boleta = new Boleta();

        String tipoComprobante = req.tipoDocumento().equalsIgnoreCase("DNI")
                ? "BOLETA"
                : "FACTURA";

        boleta.setTipoDocumento(req.tipoDocumento());
        boleta.setNumeroDocumento(req.numeroDocumento());
        boleta.setFecha(LocalDateTime.now());
        boleta.setMoneda(moneda);
        boleta.setTotalSoles(totalSolesFinal);
        boleta.setTotalDolares(totalDolaresFinal);
        boleta.setTipoCambio(tipoCambio);
        boleta.setDetalleJson(detalleJson);
        boleta.setNumeroBoleta(generarNumeroBoleta());
        boleta.setTipoComprobante(tipoComprobante);

        Boleta guardada = boletaRepository.save(boleta);

        // SOLO este return, el otro lo eliminamos
        return toResponse(guardada, cliente, productosRespuesta);
    }

    private ClienteBoletaResponse obtenerDatosCliente(String tipoDocumento, String numeroDocumento) {
        tipoDocumento = tipoDocumento.toUpperCase();
        if (tipoDocumento.equals("DNI")) {
            ReniecResponse r = reniecClient.consultarPorDni(numeroDocumento);
            if (r == null || r.first_name() == null) {
                throw new IllegalArgumentException("DNI no encontrado en RENIEC");
            }
            String nombres = r.first_name();
            String apellidos = (r.first_last_name() + " " +
                    (r.second_last_name() == null ? "" : r.second_last_name())).trim();

            return new ClienteBoletaResponse(
                    "DNI",
                    numeroDocumento,
                    nombres,
                    apellidos,
                    null
            );
        } else if (tipoDocumento.equals("RUC")) {
            SunatRucResponse r = sunatClient.consultarPorRuc(numeroDocumento);
            if (r == null || r.razon_social() == null) {
                throw new IllegalArgumentException("RUC no encontrado en SUNAT");
            }
            return new ClienteBoletaResponse(
                    "RUC",
                    numeroDocumento,
                    null,
                    null,
                    r.razon_social()
            );
        } else {
            throw new IllegalArgumentException("Tipo de documento no soportado: " + tipoDocumento);
        }
    }

    private String construirDetalleJson(ClienteBoletaResponse cliente,
                                        List<ProductoBoletaResponse> productos) {
        try {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("cliente", cliente);
            mapa.put("productos", productos);
            return objectMapper.writeValueAsString(mapa);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al construir el detalle de la boleta", e);
        }
    }

    private String generarNumeroBoleta() {
        long consecutivo = boletaRepository.count() + 1;
        return String.format("BOL-%06d", consecutivo);
    }

    public BoletaResponse obtenerBoleta(Long id) {
        Boleta b = boletaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada"));

        // Parsear detalleJson
        Map<String, Object> detalle = leerDetalleJson(b.getDetalleJson());

        // Convertir "cliente" del mapa al DTO
        ClienteBoletaResponse cliente = objectMapper.convertValue(
                detalle.get("cliente"),
                ClienteBoletaResponse.class
        );

        // Convertir "productos" del mapa a lista de DTO
        List<ProductoBoletaResponse> productos = ((List<?>) detalle.get("productos"))
                .stream()
                .map(obj -> objectMapper.convertValue(obj, ProductoBoletaResponse.class))
                .toList();

        // Formatear fecha
        String fechaFormateada = b.getFecha()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new BoletaResponse(
                b.getId(),
                b.getNumeroBoleta(),
                fechaFormateada,
                b.getMoneda(),
                b.getTipoCambio(),
                b.getTotalSoles(),
                b.getTotalDolares(),
                b.getTipoDocumento(),
                b.getTipoComprobante(),
                cliente,
                productos
        );
    }

    private BoletaResponse toResponse(
            Boleta b,
            ClienteBoletaResponse cliente,
            List<ProductoBoletaResponse> productos
    ) {
        String fechaFormateada = b.getFecha()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new BoletaResponse(
                b.getId(),
                b.getNumeroBoleta(),
                fechaFormateada,
                b.getMoneda(),
                b.getTipoCambio(),
                b.getTotalSoles(),
                b.getTotalDolares(),
                b.getTipoDocumento(),
                b.getTipoComprobante(),
                cliente,
                productos
        );
    }
    private Map<String, Object> leerDetalleJson(String detalleJson) {
        try {
            return objectMapper.readValue(detalleJson, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al leer detalleJson", e);
        }
    }
    public byte[] generarPdfBoleta(Long id) {
        // 1. Traer boleta de BD
        Boleta b = boletaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada"));

        // 2. Parsear detalleJson → cliente y productos
        Map<String, Object> detalle = leerDetalleJson(b.getDetalleJson());

        ClienteBoletaResponse cliente = objectMapper.convertValue(
                detalle.get("cliente"),
                ClienteBoletaResponse.class
        );

        List<ProductoBoletaResponse> productos = ((List<?>) detalle.get("productos"))
                .stream()
                .map(obj -> objectMapper.convertValue(obj, ProductoBoletaResponse.class))
                .toList();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);

            document.open();

            // ==== Fuentes básicas ====
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font negritaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            // ==== Título ====
            String titulo = "COMPROBANTE DE " + b.getTipoComprobante();
            Paragraph pTitulo = new Paragraph(titulo, tituloFont);
            pTitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitulo);

            document.add(new Paragraph(" ")); // espacio

            // ==== Datos de la empresa (hardcode por ahora, luego lo mejoramos) ====
            Paragraph empresa = new Paragraph(
                    "TiendaTechB\n" +
                            "RUC: 12345678901\n" +        // luego pones tu RUC real
                            "Dirección: San Martin Fonavi",
                    normalFont
            );
            document.add(empresa);

            document.add(new Paragraph(" ")); // espacio

            // ==== Datos de la boleta/factura ====
            String fechaFormateada = b.getFecha()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Paragraph datosBoleta = new Paragraph(
                    "Número: " + b.getNumeroBoleta() + "\n" +
                            "Tipo Doc. Cliente: " + b.getTipoDocumento() + "\n" +
                            "Fecha: " + fechaFormateada + "\n" +
                            "Moneda: " + b.getMoneda() + "\n" +
                            "Tipo de cambio: " + b.getTipoCambio(),
                    normalFont
            );
            document.add(datosBoleta);

            document.add(new Paragraph(" ")); // espacio

            // ==== Datos del cliente ====
            String lineaCliente;
            if ("DNI".equalsIgnoreCase(cliente.tipoDocumento())) {
                lineaCliente = "Cliente: " + cliente.nombres() + " " + cliente.apellidos() +
                        "\nDNI: " + cliente.numeroDocumento();
            } else {
                lineaCliente = "Razón Social: " + cliente.razonSocial() +
                        "\nRUC: " + cliente.numeroDocumento();
            }
            Paragraph datosCliente = new Paragraph(lineaCliente, normalFont);
            document.add(datosCliente);

            document.add(new Paragraph(" ")); // espacio

            // ==== Tabla de productos ====
            PdfPTable table = new PdfPTable(4); // columnas: Producto, Cantidad, Precio, Subtotal
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 1.5f, 2, 2});

            // Encabezados
            addHeaderCell(table, "Producto", negritaFont);
            addHeaderCell(table, "Cantidad", negritaFont);
            addHeaderCell(table, "Precio", negritaFont);
            addHeaderCell(table, "Subtotal", negritaFont);

            // Filas
            for (ProductoBoletaResponse pr : productos) {
                addBodyCell(table, pr.nombre(), normalFont);
                addBodyCell(table, String.valueOf(pr.cantidad()), normalFont);
                addBodyCell(table, pr.precioUnitario().setScale(2, RoundingMode.HALF_UP).toString(), normalFont);
                addBodyCell(table, pr.subtotal().setScale(2, RoundingMode.HALF_UP).toString(), normalFont);
            }

            document.add(table);

            document.add(new Paragraph(" ")); // espacio



            BigDecimal totalSoles = b.getTotalSoles().setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalDolares = b.getTotalDolares().setScale(2, RoundingMode.HALF_UP);

// Base imponible = total / 1.18
            BigDecimal baseSoles = totalSoles.divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
            BigDecimal igvSoles  = totalSoles.subtract(baseSoles);

            BigDecimal baseDolares = totalDolares.divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
            BigDecimal igvDolares  = totalDolares.subtract(baseDolares);

            String textoTotales;

            if ("PEN".equalsIgnoreCase(b.getMoneda())) {
                textoTotales =
                        "OP. GRAVADA: " + baseSoles + " PEN\n" +
                                "IGV (18%): " + igvSoles + " PEN\n" +
                                "TOTAL A PAGAR: " + totalSoles + " PEN\n\n" +
                                "Equivalente en USD: " + totalDolares + " USD";
            } else { // Moneda principal USD
                textoTotales =
                        "OP. GRAVADA: " + baseDolares + " USD\n" +
                                "IGV (18%): " + igvDolares + " USD\n" +
                                "TOTAL A PAGAR: " + totalDolares + " USD\n\n" +
                                "Equivalente en PEN: " + totalSoles + " PEN";
            }

            Paragraph totales = new Paragraph(textoTotales, negritaFont);
            totales.setAlignment(Element.ALIGN_RIGHT);
            document.add(totales);

            document.add(new Paragraph(" "));

            Paragraph gracias = new Paragraph("Gracias por su compra.", normalFont);
            gracias.setAlignment(Element.ALIGN_CENTER);
            document.add(gracias);

            document.close();

            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar PDF de boleta", e);
        }
    }

    // Helpers para celdas de tabla
    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }
}
