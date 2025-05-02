package com.tiendacoco.security;

import com.tiendacoco.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) Deshabilitamos CSRF (puesto que usamos JWT)
                .csrf(csrf -> csrf.disable())

                // 2) Sin sesión HTTP (stateless)
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3) Configuramos quién accede a qué
                .authorizeHttpRequests(auth -> auth
                        // endpoints públicos:
                        .requestMatchers(
                                "/api/login",
                                "/api/registro",
                                "/api/recuperar",
                                "/api/validar-temporal",
                                "/api/cambiar-contrasena"
                        ).permitAll()
                        // cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )

                // 4) Añadimos nuestro filtro JWT **antes** del filtro de formulario
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}
