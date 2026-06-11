package com.mercaduca.config;

import com.mercaduca.security.filters.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración del SecurityFilterChain (seguridad a nivel de URL/HTTP).
 *
 * Nota: @EnableMethodSecurity y la jerarquía de roles han sido movidos a
 * MethodSecurityConfig para evitar conflictos de inicialización en Spring Security 6.x.
 *
 * Reglas de autorización de URLs:
 * - Rutas públicas: sin autenticación
 * - /admin/**     : solo ADMIN
 * - /seller/**    : SELLER o ADMIN (operaciones de gestión de tienda)
 * - /sellers/**   : cualquier usuario autenticado puede solicitar ser vendedor
 * - Resto         : cualquier usuario autenticado
 *
 * NOTA: La jerarquía ROLE_SELLER > ROLE_BUYER aplica a @PreAuthorize (method security).
 * Para las reglas de URL abajo usamos hasAnyRole explícito donde aplica.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    private static final String[] PUBLIC_URLS = {
        "/auth/**",
        "/categories/**", "/categories",
        "/swagger-ui/**", "/swagger-ui.html", "/api-docs/**",
        "/actuator/health"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(ex -> ex
                // Devuelve 401 (no 403) para solicitudes sin autenticación válida.
                // Sin esto Spring Security usa Http403ForbiddenEntryPoint por defecto,
                // lo que impide que el frontend distinga "no autenticado" de "sin permiso"
                // y el interceptor de Axios nunca hace refresh del token expirado.
                .authenticationEntryPoint((req, res, e) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required"))
            )
            .authorizeHttpRequests(auth -> auth
                // Rutas completamente públicas
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/reviews/product/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/questions/products/**").permitAll()

                // Admin exclusivo
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Herramientas de vendedor (gestión de tienda)
                // hasAnyRole explícito — no dependemos de jerarquía en URL rules
                .requestMatchers("/seller/**").hasAnyRole("SELLER", "ADMIN")

                // Solicitud de ser vendedor: cualquier usuario autenticado puede solicitarlo
                // (BUYER para primera vez, SELLER para re-solicitar tras rechazo)
                .requestMatchers("/sellers/**").authenticated()

                // Todo lo demás requiere estar autenticado
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
