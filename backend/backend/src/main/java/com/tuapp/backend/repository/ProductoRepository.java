package com.tuapp.backend.repository;

import com.tuapp.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    Optional<Producto> findByNombreIgnoreCase(String nombre);
}
