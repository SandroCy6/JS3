package com.tuapp.backend.dto.boleta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemBoletaRequest(
        @NotNull Long productoId,
        @NotNull @Min(1) Integer cantidad
) {
}

