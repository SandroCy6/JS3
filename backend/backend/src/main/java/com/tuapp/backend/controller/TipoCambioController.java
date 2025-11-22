package com.tuapp.backend.controller;

import com.tuapp.backend.service.TipoCambioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/tipo-cambio")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TipoCambioController {

    private final TipoCambioClient tipoCambioClient;

    @GetMapping("/venta-hoy")
    public BigDecimal obtenerTipoCambioHoy() {
        return tipoCambioClient.obtenerTipoCambioVentaHoy();
    }
}
