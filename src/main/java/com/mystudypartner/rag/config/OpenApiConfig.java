package com.mystudypartner.rag.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Swagger documentation.
 * Provides API documentation and interactive testing interface.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${spring.application.name:RAG Application}")
    private String applicationName;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
    
    /**
     * Configure OpenAPI documentation.
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API")
                        .version("1.0.0")
                        .description("""
                                REST API for the RAG (Retrieval-Augmented Generation) Application.
                                
                                This API provides endpoints for:
                                - Document upload and management
                                - Document processing status tracking
                                - Document search and retrieval
                                - Processing statistics and monitoring
                                
                                ## Features
                                - **Document Upload**: Upload PDF, Word, and text documents
                                - **Processing Pipeline**: Track document processing status
                                - **Search & Retrieval**: Find documents by various criteria
                                - **Statistics**: Monitor system performance and usage
                                
                                ## Authentication
                                This API currently supports basic authentication for development.
                                Production deployments should use proper authentication mechanisms.
                                """)
                        .contact(new Contact()
                                .name("MyStudyPartner Development Team")
                                .email("dev@mystudypartner.com")
                                .url("https://mystudypartner.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.mystudypartner.com")
                                .description("Production Server")
                ));
    }
}
