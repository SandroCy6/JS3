package com.tuapp.backend.dto.boleta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record GenerarBoletaRequest(
        @NotBlank
        @Pattern(regexp = "DNI|RUC", message = "El tipoDocumento debe ser DNI o RUC")
        String tipoDocumento,

        @NotBlank
        String numeroDocumento,

        @NotBlank
        @Pattern(regexp = "PEN|USD", message = "La moneda debe ser PEN o USD")
        String moneda,

        @NotEmpty
        List<ItemBoletaRequest> items
) {
}
