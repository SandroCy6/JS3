package com.tuapp.backend.controller;

import com.tuapp.backend.dto.boleta.BoletaResponse;
import com.tuapp.backend.dto.boleta.GenerarBoletaRequest;
import com.tuapp.backend.service.BoletaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/boletas")
@RequiredArgsConstructor
public class BoletaController {

    private final BoletaService boletaService;

    @PostMapping
    public ResponseEntity<BoletaResponse> generar(@Valid @RequestBody GenerarBoletaRequest req) {
        return ResponseEntity.ok(boletaService.generarBoleta(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.obtenerBoleta(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        byte[] pdf = boletaService.generarPdfBoleta(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // inline → lo abre en el navegador, attachment → lo descarga
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename("comprobante-" + id + ".pdf")
                        .build()
        );

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    // Manejo simple de errores de negocio (igual que en UsuarioController)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
