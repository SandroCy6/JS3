package com.tuapp.backend.dto.boleta;

import java.math.BigDecimal;

public record ProductoBoletaResponse(
        Long productoId,
        String nombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
