package com.tuapp.backend.controller;

import com.tuapp.backend.dto.reniec.ReniecResponse;
import com.tuapp.backend.service.ReniecClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
//Marca esta clase como un controlador REST.
//// Spring la registrar,recibir y responder solicitudes HTTP (GET, POST, PUT, DELETE)
@RequestMapping("/auth")
//Definimos como un prefijo antes de cada busqueda empezara /auuth
@RequiredArgsConstructor
public class ReniecController {

    private final ReniecClient reniecClient;
//Dependencia inyectada automáticamente. Ya que esta como import en reniecclient

    @GetMapping("/validar-dni")
    public Object validar(@RequestParam String dni) {
        ReniecResponse r = reniecClient.consultarPorDni(dni);
        //Llama al servicio ReniecClient para conectarse con Decolecta
        // obtiene la respuesta (ReniecResponse) con los datos del ciudadano.
        if (r == null) return null;
        //Esto sirve si no funciona correctamente
        return new Object() {
            // Esto transforma los nombres del JSON en algo más claro para el cliente.
            public final String dni = r.document_number();
            public final String nombres = r.first_name();
            public final String apellidoPaterno = r.first_last_name();
            public final String apellidoMaterno = r.second_last_name();
        };
    }
}