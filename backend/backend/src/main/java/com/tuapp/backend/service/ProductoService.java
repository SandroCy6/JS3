package com.tuapp.backend.service;

import com.tuapp.backend.dto.producto.ProductoRequest;
import com.tuapp.backend.dto.producto.ProductoResponse;
import com.tuapp.backend.model.Producto;
import com.tuapp.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoResponse crear(ProductoRequest req) {
        // nombre único (también se reforzará con la unique constraint)
        if (productoRepository.existsByNombreIgnoreCase(req.nombre())) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }
        var p = new Producto();
        p.setNombre(req.nombre().trim());
        p.setPrecio(req.precio());
        p.setStock(req.stock());
        p.setDescripcion(req.descripcion());
        p.setActivo(req.activo() == null ? true : req.activo());

        try {
            return toResponse(productoRepository.save(p));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Nombre de producto duplicado");
        }
    }

    public List<ProductoResponse> listar() {
        return productoRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    public ProductoResponse obtener(Long id) {
        var p = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        return toResponse(p);
    }

    public ProductoResponse actualizar(Long id, ProductoRequest req) {
        var p = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Si cambia el nombre, validar duplicados
        if (!p.getNombre().equalsIgnoreCase(req.nombre())
                && productoRepository.existsByNombreIgnoreCase(req.nombre())) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }

        p.setNombre(req.nombre().trim());
        p.setPrecio(req.precio());
        p.setStock(req.stock());
        p.setDescripcion(req.descripcion());
        if (req.activo() != null) {
            p.setActivo(req.activo());
        }

        try {
            return toResponse(productoRepository.save(p));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Nombre de producto duplicado");
        }
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    // (Opcional) activar/desactivar rápido
    public ProductoResponse cambiarEstado(Long id, boolean activo) {
        var p = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        p.setActivo(activo);
        return toResponse(productoRepository.save(p));
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock(),
                p.getDescripcion(),
                p.isActivo()
        );
    }
}

