package com.mercaduca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Configuración de seguridad a nivel de método (@PreAuthorize, @PostAuthorize).
 *
 * Separada de SecurityConfig intencionalmente para evitar el conflicto de
 * inicialización en Spring Security 6.x: cuando @EnableMethodSecurity y el
 * MethodSecurityExpressionHandler @Bean están en la misma clase, Spring puede
 * crear dos instancias del handler y el orden de precedencia es indeterminado.
 *
 * Al mover @EnableMethodSecurity aquí:
 * - El MethodSecurityExpressionHandler se crea en la misma clase que lo necesita.
 * - La jerarquía ROLE_SELLER > ROLE_BUYER se aplica a @PreAuthorize en todos
 *   los controllers, permitiendo que SELLER acceda a endpoints de BUYER sin
 *   necesidad de hasAnyRole('BUYER','SELLER') explícito.
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

    /**
     * Define la jerarquía de roles del sistema:
     *   ROLE_SELLER hereda todos los permisos de ROLE_BUYER.
     *   ROLE_ADMIN no hereda nada (tiene su propio conjunto de permisos).
     *
     * Con esta jerarquía:
     *   hasRole('BUYER')  → pasa para BUYER y SELLER
     *   hasRole('SELLER') → pasa solo para SELLER
     *   hasRole('ADMIN')  → pasa solo para ADMIN
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_SELLER > ROLE_BUYER");
        return hierarchy;
    }

    /**
     * Registra la jerarquía en el handler de expresiones de método.
     * Sin esto, @PreAuthorize("hasRole('BUYER')") ignoraría la jerarquía
     * y SELLER no podría acceder a endpoints marcados para BUYER.
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }
}
