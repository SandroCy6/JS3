package com.tuapp.backend.dto.boleta;

import java.math.BigDecimal;
import java.util.List;

public record BoletaResponse(
        Long id,
        String numeroBoleta,
        String fecha,                 // "yyyy-MM-dd HH:mm:ss"
        String moneda,
        BigDecimal tipoCambio,
        BigDecimal totalSoles,
        BigDecimal totalDolares,
        String tipoDocumento,         // DNI / RUC
        String tipoComprobante,       // BOLETA / FACTURA
        ClienteBoletaResponse cliente,
        List<ProductoBoletaResponse> productos
) {}
