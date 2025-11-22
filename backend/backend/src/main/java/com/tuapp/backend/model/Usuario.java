package com.tuapp.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_dni", columnNames = "dni"),
                @UniqueConstraint(name = "uk_usuario_correo", columnNames = "correo"),
                @UniqueConstraint(name = "uk_usuario_username", columnNames = "username")
        })
public class Usuario {

    @Id
    @Column(length = 8, nullable = false)
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe ser numérico de 8 dígitos")
    private String dni;

    @Column(nullable = false, length = 80)
    @NotBlank
    private String nombres;

    @Column(nullable = false, length = 80)
    @NotBlank
    private String apellidos;

    @Column(nullable = false, length = 120)
    @Email
    @NotBlank
    private String correo;

    @Column(name = "username", nullable = false, length = 40)
    @NotBlank
    private String username; // auto = DNI

    @Column(nullable = false, length = 120)
    @NotBlank
    private String contrasena; // la encriptaremos en el paso de Security

   //hacemos los getters y setters
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
