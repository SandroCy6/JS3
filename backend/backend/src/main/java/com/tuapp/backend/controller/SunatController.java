package com.tuapp.backend.controller;

import com.tuapp.backend.dto.sunat.SunatRucResponse;
import com.tuapp.backend.service.SunatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SunatController {

    private final SunatClient sunatClient;

    @GetMapping("/validar-ruc")
    public Object validarRuc(@RequestParam String ruc) {
        SunatRucResponse r = sunatClient.consultarPorRuc(ruc);
        if (r == null) return null;

        // Devolvemos algo amigable para el frontend (igual estilo que ReniecController)
        return new Object() {
            public final String numeroDocumento = r.numero_documento();
            public final String razonSocial = r.razon_social();
            public final String direccion = r.direccion();
            public final String estado = r.estado();
            public final String condicion = r.condicion();
            public final String distrito = r.distrito();
            public final String provincia = r.provincia();
            public final String departamento = r.departamento();
        };
    }
}
