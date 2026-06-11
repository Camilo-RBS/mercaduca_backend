package com.mercaduca.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mercaduca API")
                        .description("Enterprise Marketplace Platform — Spring Boot 3 + Java 21")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mercaduca Team")
                                .email("dev@mercaduca.com"))
                        .license(new License().name("Proprietary")))
                .servers(List.of(
                        new Server().url("http://localhost:8080" + contextPath).description("Local"),
                        new Server().url("https://api.mercaduca.com" + contextPath).description("Production")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from /auth/login")));
    }
}
