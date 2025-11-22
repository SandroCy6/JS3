package com.tuapp.backend.service;

import com.tuapp.backend.dto.reniec.ReniecResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ReniecClient {

    //WebClient que usaremos para conectarnos a la API de Decolecta
    private final WebClient reniecWebClient;

    @Value("${reniec.dni-path:/dni}")
    private String dniPath;
// Lee el valor del archivo application-prod.properties → reniec.dni-path
    // Si no lo encuentra, usa "/dni" por defecto.


    // Lee el tiempo máximo de espera (timeout) definido en milisegundos.
    @Value("${reniec.timeout-ms:3000}")
    private long timeoutMs;

    // Método principal: consulta la API de RENIEC pasando el número de DNI
    public ReniecResponse consultarPorDni(String dni) {
        return reniecWebClient.get()
                .uri(uri -> uri.path(dniPath).queryParam("numero", dni).build())
                .retrieve()// Ejecuta la petición y obtiene la respuesta HTTP
                .bodyToMono(ReniecResponse.class)
                // Convierte el cuerpo JSON a un objeto ReniecResponse (reactivo)
                .block(Duration.ofMillis(timeoutMs));//lo finaliza hasta recibir el final o que pase el tiempo
    }
}