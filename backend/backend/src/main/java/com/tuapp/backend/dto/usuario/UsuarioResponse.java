package com.tuapp.backend.dto.usuario;

public record UsuarioResponse(
        String dni,
        String nombres,
        String apellidos,
        String correo,
        String username
) {}
