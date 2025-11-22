package com.tuapp.backend.dto.boleta;

public record ClienteBoletaResponse(
        String tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String razonSocial
) {
}
