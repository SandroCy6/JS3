package com.tuapp.backend.dto.producto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        String descripcion,
        boolean activo
) { }
