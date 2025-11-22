package com.tuapp.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    //Este método generaremos un Bean (un objeto administrado por Spring)
    // para que luego podamos usarlo en otras partes (por ejemplo, en ReniecClient)
    @Bean
    public WebClient reniecWebClient(
            @Value("${reniec.base-url}") String baseUrl,
            @Value("${reniec.token}") String token) {
        //Value inyecta los valores definidos en application-prod.properties , que ya configurmos

        return WebClient.builder()
                //  Crea una instancia personalizada de WebClient usando el patrón Builder.
                .baseUrl(baseUrl)
                //  Define la URL base de todas las peticiones (en este caso, la API de Decolecta).
                .defaultHeader("Authorization", "Bearer " + token)
                //  Agrega automáticamente el encabezado de autenticación (Bearer Token)
                // para cada solicitud que se haga con este WebClient.
                .defaultHeader("Content-Type", "application/json")
                // Define que todas las peticiones y respuestas serán en formato JSON.
                .build();
        // Finalmente, construye el objeto WebClient listo para usarse.
    }
    //al final cuando todo esta defenido con el bean y lo usamos
// === SUNAT  ===
    @Bean
    public WebClient sunatWebClient(
            @Value("${sunat.base-url}") String baseUrl,
            @Value("${sunat.token}") String token) {

        return WebClient.builder()
                .baseUrl(baseUrl) // https://api.decolecta.com/v1/sunat
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient tipoCambioWebClient(
            @Value("${tc.base-url}") String baseUrl,
            @Value("${tc.token}") String token) {

        return WebClient.builder()
                .baseUrl(baseUrl) // https://api.decolecta.com/v1/tipo-cambio
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}

