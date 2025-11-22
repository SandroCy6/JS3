package com.tuapp.backend.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegistroRequest(
        @NotBlank
        @Pattern(regexp="\\d{8}", message="DNI debe tener 8 dígitos")
        String dni,
        @NotBlank
        String contrasena
) { }

