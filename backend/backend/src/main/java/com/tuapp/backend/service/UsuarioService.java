package com.tuapp.backend.service;

import com.tuapp.backend.dto.usuario.UsuarioRequest;
import com.tuapp.backend.dto.usuario.UsuarioResponse;
import com.tuapp.backend.model.Usuario;
import com.tuapp.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ReniecClient reniecClient;

    // Registro simple (DNI + contraseña) → nombres/apellidos desde RENIEC
    public UsuarioResponse registrar(String dni, String contrasenaPlano) {
        var r = reniecClient.consultarPorDni(dni);
        if (r == null || r.first_name() == null) {
            throw new IllegalArgumentException("DNI no encontrado en RENIEC");
        }
        if (usuarioRepository.existsById(dni)) {
            throw new IllegalArgumentException("El DNI ya está registrado");
        }

        String nombres = r.first_name();
        String apellidos = (r.first_last_name() + " " +
                (r.second_last_name() == null ? "" : r.second_last_name())).trim();

        Usuario u = new Usuario();
        u.setDni(dni);
        u.setNombres(nombres);
        u.setApellidos(apellidos);
        u.setCorreo(dni + "@demo.local");
        u.setUsername(dni); // <- clave: username = DNI
        u.setContrasena(passwordEncoder.encode(contrasenaPlano));

        return toResponse(usuarioRepository.save(u));
    }

    public UsuarioResponse login(String dni, String contrasenaPlano) {
        var u = usuarioRepository.findById(dni)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        if (!passwordEncoder.matches(contrasenaPlano, u.getContrasena())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return toResponse(u);
    }

    // CRUD (opcional)
    public UsuarioResponse crear(UsuarioRequest req) {
        Usuario u = new Usuario();
        u.setDni(req.dni());
        u.setNombres(req.nombres());
        u.setApellidos(req.apellidos());
        u.setCorreo(req.correo());
        u.setUsername(req.dni()); // ← forzado = DNI
        u.setContrasena(passwordEncoder.encode(req.contrasena()));
        var guardado = usuarioRepository.save(u);
        return toResponse(guardado);
    }

    public UsuarioResponse actualizar(String dni, UsuarioRequest req) {
        Usuario u = usuarioRepository.findById(dni)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        u.setNombres(req.nombres());
        u.setApellidos(req.apellidos());
        u.setCorreo(req.correo());
        u.setUsername(dni); // ← permanecemos coherentes
        if (req.contrasena() != null && !req.contrasena().isBlank()) {
            u.setContrasena(passwordEncoder.encode(req.contrasena()));
        }
        return toResponse(usuarioRepository.save(u));
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UsuarioResponse obtener(String dni) {
        var u = usuarioRepository.findById(dni)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return toResponse(u);
    }

    public void eliminar(String dni) {
        if (!usuarioRepository.existsById(dni)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(dni);
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getDni(), u.getNombres(), u.getApellidos(), u.getCorreo(), u.getUsername()
        );
    }
}
