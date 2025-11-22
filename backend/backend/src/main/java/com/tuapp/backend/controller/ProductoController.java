package com.tuapp.backend.controller;

import com.tuapp.backend.dto.producto.ProductoRequest;
import com.tuapp.backend.dto.producto.ProductoResponse;
import com.tuapp.backend.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://34.46.167.111:3000")
@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // Públicos (catálogo)
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtener(id));
    }

    // Protegidos (CRUD)
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest req) {
        return ResponseEntity.ok(productoService.crear(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ProductoRequest req) {
        return ResponseEntity.ok(productoService.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Activar/Desactivar rápido (opcional)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProductoResponse> cambiarEstado(@PathVariable Long id,
                                                          @RequestParam boolean activo) {
        return ResponseEntity.ok(productoService.cambiarEstado(id, activo));
    }
}
