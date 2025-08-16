package com.mystudypartner.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application class for the RAG (Retrieval-Augmented Generation) system.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAsync
public class RagApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }
}
