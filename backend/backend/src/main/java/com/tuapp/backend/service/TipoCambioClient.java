package com.tuapp.backend.service;

import com.tuapp.backend.dto.tipocambio.TipoCambioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TipoCambioClient {

    private final WebClient tipoCambioWebClient;

    @Value("${tc.sunat-path:/sunat}")
    private String sunatPath;

    @Value("${tc.timeout-ms:3000}")
    private long timeoutMs;

    /**
     * Obtiene el tipo de cambio de venta (sell_price) de HOY desde SUNAT vía Decolecta.
     */
    public BigDecimal obtenerTipoCambioVentaHoy() {
        TipoCambioResponse resp = tipoCambioWebClient.get()
                .uri(uri -> uri
                        .path(sunatPath)   // /sunat
                        .build()
                )
                .retrieve()
                .bodyToMono(TipoCambioResponse.class)
                .block(Duration.ofMillis(timeoutMs));

        if (resp == null || resp.sell_price() == null) {
            throw new IllegalStateException("No se pudo obtener el tipo de cambio de SUNAT");
        }

        return new BigDecimal(resp.sell_price());
    }
}
