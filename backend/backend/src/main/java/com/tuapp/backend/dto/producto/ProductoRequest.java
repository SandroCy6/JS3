package com.tuapp.backend.dto.producto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank
        String nombre,
        @NotNull
        @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
        BigDecimal precio,
        @NotNull
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,
        String descripcion,
        Boolean activo // opcional; si viene null, lo asumimos true al crear
) { }
