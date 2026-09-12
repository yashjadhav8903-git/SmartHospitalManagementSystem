package com.example.HospitalManagement.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    // Default value set ho jayegi http://localhost:8080 agar properties me key na ho
    @Value("${app.server.url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI mySwaggerOpenAPIConfig() {

        final String securitySchemeName = "bearerAuth";

        // 1. Production DuckDNS Server
        Server productionServer = new Server()
                .url("https://smart-hms-yash.duckdns.org")
                .description("Production AWS Server (DuckDNS)");

        // 2 Primary Load Balancer Server (Nginx on Port 80)
        Server loadBalancerServer = new Server()
                .url("http://localhost")
                .description("Nginx Load Balancer (Port 80 - Multi Instance)");

        // 3 Direct Local Instance Server (Port 8080)
        Server directServer = new Server()
                .url("http://localhost:8080")
                .description("Direct Local Server (Port 8080 - Single Instance)");

        return new OpenAPI()
                // 1. App Info
                .info(new Info()
                        .title("Hospital Management API")
                        .version("1.0")
                        .description("HMS Monolith API Documentation."))


                // Swagger UI Dropdown me dono servers visible rahenge
                .servers(List.of(productionServer,loadBalancerServer, directServer))


                //Global JWT Security Setup for Swagger UI
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components( new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
