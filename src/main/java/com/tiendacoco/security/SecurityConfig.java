package com.tiendacoco.security;

import com.tiendacoco.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 0) Activa CORS con la fuente que definimos más abajo
                .cors(Customizer.withDefaults())

                // 1) Deshabilita CSRF (no lo necesitas cuando trabajas con JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // 2) Stateless: no mantenemos sesiones en el servidor
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3) Rutas públicas y protegidas
                .authorizeHttpRequests(auth ->
                        auth
                                // Estas URLs quedan abiertas
                                .requestMatchers(
                                        "/api/login",
                                        "/api/registro",
                                        "/api/recuperar",
                                        "/api/validar-temporal",
                                        "/api/cambiar-contrasena"
                                ).permitAll()
                                // El resto exige un token válido
                                .anyRequest().authenticated()
                )

                // 4) Nuestro filtro de JWT antes que el de formulario
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Definimos aquí el bean que expone la configuración de CORS para
     * todas las rutas que empiecen por /api/
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"
        ));    // tu React App
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        // Aplica CORS a todas las rutas bajo /api/
        src.registerCorsConfiguration("/api/**", cfg);
        return src;
    }
}
