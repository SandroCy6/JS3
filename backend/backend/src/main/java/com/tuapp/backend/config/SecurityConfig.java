package com.tuapp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // desactiva CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/swagger/**", "/api-docs/**", "/ping").permitAll()
                        .requestMatchers("/productos/**").permitAll()
                        .requestMatchers("/boletas/**").permitAll()
                        .requestMatchers("/tipo-cambio/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {}); // activa autenticación básica (usuario/contraseña)
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
