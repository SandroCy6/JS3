package com.tuapp.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "boletas")
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_boleta", nullable = false, unique = true, length = 30)
    private String numeroBoleta;

    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento; // "DNI" o "RUC"

    @Column(name = "numero_documento", nullable = false, length = 15)
    private String numeroDocumento;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 3)
    private String moneda; // "PEN" o "USD"

    @Column(name = "total_soles", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSoles;

    @Column(name = "total_dolares", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDolares;

    @Column(name = "tipo_cambio", nullable = false, precision = 10, scale = 3)
    private BigDecimal tipoCambio; // PEN por 1 USD

    @Lob
    @Column(name = "detalle_json", nullable = false, columnDefinition = "TEXT")
    private String detalleJson; // JSON con cliente + productos

    @Column(name = "tipo_comprobante", nullable = false, length = 10)
    private String tipoComprobante; // "BOLETA" o "FACTURA"


    // Getters y setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNumeroBoleta() { return numeroBoleta; }

    public void setNumeroBoleta(String numeroBoleta) { this.numeroBoleta = numeroBoleta; }

    public String getTipoDocumento() { return tipoDocumento; }

    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }

    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public LocalDateTime getFecha() { return fecha; }

    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getMoneda() { return moneda; }

    public void setMoneda(String moneda) { this.moneda = moneda; }

    public BigDecimal getTotalSoles() { return totalSoles; }

    public void setTotalSoles(BigDecimal totalSoles) { this.totalSoles = totalSoles; }

    public BigDecimal getTotalDolares() { return totalDolares; }

    public void setTotalDolares(BigDecimal totalDolares) { this.totalDolares = totalDolares; }

    public BigDecimal getTipoCambio() { return tipoCambio; }

    public void setTipoCambio(BigDecimal tipoCambio) { this.tipoCambio = tipoCambio; }

    public String getDetalleJson() { return detalleJson; }

    public void setDetalleJson(String detalleJson) { this.detalleJson = detalleJson; }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }
}
