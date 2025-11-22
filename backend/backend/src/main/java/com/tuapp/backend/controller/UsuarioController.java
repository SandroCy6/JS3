package com.tuapp.backend.controller;

import com.tuapp.backend.dto.usuario.UsuarioRequest;
import com.tuapp.backend.dto.usuario.UsuarioResponse;
import com.tuapp.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest req) {
        return ResponseEntity.ok(usuarioService.crear(req));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{dni}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable String dni) {
        return ResponseEntity.ok(usuarioService.obtener(dni));
    }

    @PutMapping("/{dni}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable String dni,
                                                      @Valid @RequestBody UsuarioRequest req) {
        return ResponseEntity.ok(usuarioService.actualizar(dni, req));
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> eliminar(@PathVariable String dni) {
        usuarioService.eliminar(dni);
        return ResponseEntity.noContent().build();
    }

    // Manejo simple de errores de negocio
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
