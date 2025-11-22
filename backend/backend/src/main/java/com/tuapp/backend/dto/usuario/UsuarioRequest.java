package com.tuapp.backend.dto.usuario;

import jakarta.validation.constraints.*;

public record UsuarioRequest(
        @NotBlank @Pattern(regexp="\\d{8}") String dni,
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @Email @NotBlank String correo,
        @NotBlank String username,
        @NotBlank String contrasena
) {}

