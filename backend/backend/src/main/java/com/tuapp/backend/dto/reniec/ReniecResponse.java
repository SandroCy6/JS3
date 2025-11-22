package com.tuapp.backend.dto.reniec;
//Data Transfer Objects

//esta es una plantilla ya de por si , ya que es practicamente que siempre la usamos RECORD
public record ReniecResponse(
        String document_number,
        String first_name,
        String first_last_name,
        String second_last_name
) {}