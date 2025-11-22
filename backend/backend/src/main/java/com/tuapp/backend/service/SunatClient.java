package com.tuapp.backend.service;

import com.tuapp.backend.dto.sunat.SunatRucResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class SunatClient {

    private final WebClient sunatWebClient;

    @Value("${sunat.ruc-path:/ruc}")
    private String rucPath;

    @Value("${sunat.timeout-ms:3000}")
    private long timeoutMs;

    public SunatRucResponse consultarPorRuc(String ruc) {
        return sunatWebClient.get()
                .uri(uri -> uri
                        .path(rucPath)              // /ruc
                        .queryParam("numero", ruc)  // ?numero=XXXXXXXXXXX
                        .build()
                )
                .retrieve()
                .bodyToMono(SunatRucResponse.class)
                .block(Duration.ofMillis(timeoutMs));
    }
}
