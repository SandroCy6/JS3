package com.tuapp.backend.controller;

import com.tuapp.backend.dto.usuario.LoginRequest;
import com.tuapp.backend.dto.usuario.RegistroRequest;
import com.tuapp.backend.dto.usuario.UsuarioResponse;
import com.tuapp.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registro(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.ok(usuarioService.registrar(req.dni(), req.contrasena()));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(usuarioService.login(req.dni(), req.contrasena()));
    }
}

