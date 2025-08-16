import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
package com.mystudypartner.rag.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for health check operations.
 * Provides endpoints for monitoring application and database health.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
// Removed @RequiredArgsConstructor to add explicit constructor with defensive copies
@Tag(name = "Health Check", description = "APIs for monitoring application health")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthController {
    private final DataSource dataSource;
    private final HealthIndicator[] healthIndicators;

        /**
         * Constructor for HealthController.
         *
         * <p>SpotBugs EI_EXPOSE_REP2 suppressed: dataSource is a Spring-managed bean, lifecycle controlled by the framework.
         * Defensive copying is not feasible or necessary. healthIndicators array is defensively copied.</p>
         */
        @SuppressFBWarnings(value = {"EI_EXPOSE_REP2"}, justification = "Spring-managed beans; lifecycle controlled by framework.")
    public HealthController(DataSource dataSource, HealthIndicator[] healthIndicators) {
        this.dataSource = dataSource;
        // Defensive copy of array to avoid exposing internal representation
        this.healthIndicators = healthIndicators != null ? healthIndicators.clone() : null;
    }
    
    /**
     * Get application and database health status.
     * 
     * @return health status response
     */
    @GetMapping
    @Operation(
        summary = "Get Application Health",
        description = "Check the health status of the application and database"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Application is healthy",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Application is unhealthy"
        )
    })
    public ResponseEntity<Map<String, Object>> getHealth() {
        log.debug("Health check requested");
        
        Map<String, Object> healthStatus = new HashMap<>();
        boolean isHealthy = true;
        
        // Application health
        healthStatus.put("application", "UP");
        healthStatus.put("timestamp", LocalDateTime.now().toString());
        healthStatus.put("version", "1.0.0");
        
        // Database health
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                healthStatus.put("database", "UP");
                healthStatus.put("databaseUrl", connection.getMetaData().getURL());
                healthStatus.put("databaseProduct", connection.getMetaData().getDatabaseProductName());
                healthStatus.put("databaseVersion", connection.getMetaData().getDatabaseProductVersion());
            } else {
                healthStatus.put("database", "DOWN");
                healthStatus.put("databaseError", "Database connection is not valid");
                isHealthy = false;
            }
        } catch (SQLException e) {
            log.error("Database health check failed", e);
            healthStatus.put("database", "DOWN");
            healthStatus.put("databaseError", e.getMessage());
            isHealthy = false;
        }
        
        // Check Spring Boot Actuator health indicators
        Map<String, Object> indicators = new HashMap<>();
        for (HealthIndicator indicator : healthIndicators) {
            try {
                Health health = indicator.health();
                indicators.put(indicator.getClass().getSimpleName(), health.getStatus().getCode());
                if ("DOWN".equalsIgnoreCase(health.getStatus().getCode())) {
                    isHealthy = false;
                }
            } catch (Exception e) {
                log.error("Health indicator check failed: {}", indicator.getClass().getSimpleName(), e);
                indicators.put(indicator.getClass().getSimpleName(), "DOWN");
                isHealthy = false;
            }
        }
        healthStatus.put("indicators", indicators);
        
        // Overall status
        healthStatus.put("status", isHealthy ? "UP" : "DOWN");
        
        log.debug("Health check completed. Status: {}", isHealthy ? "UP" : "DOWN");
        
        return ResponseEntity
                .status(isHealthy ? 200 : 503)
                .body(healthStatus);
    }
    
    /**
     * Get database health status only.
     * 
     * @return database health status
     */
    @GetMapping("/database")
    @Operation(
        summary = "Get Database Health",
        description = "Check the health status of the database connection"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Database is healthy",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Database is unhealthy"
        )
    })
    public ResponseEntity<Map<String, Object>> getDatabaseHealth() {
        log.debug("Database health check requested");
        
        Map<String, Object> dbHealth = new HashMap<>();
        boolean isHealthy = true;
        
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                dbHealth.put("status", "UP");
                dbHealth.put("timestamp", LocalDateTime.now().toString());
                dbHealth.put("url", connection.getMetaData().getURL());
                dbHealth.put("product", connection.getMetaData().getDatabaseProductName());
                dbHealth.put("version", connection.getMetaData().getDatabaseProductVersion());
                dbHealth.put("driver", connection.getMetaData().getDriverName());
                dbHealth.put("driverVersion", connection.getMetaData().getDriverVersion());
            } else {
                dbHealth.put("status", "DOWN");
                dbHealth.put("error", "Database connection is not valid");
                isHealthy = false;
            }
        } catch (SQLException e) {
            log.error("Database health check failed", e);
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
            isHealthy = false;
        }
        
        log.debug("Database health check completed. Status: {}", isHealthy ? "UP" : "DOWN");
        
        return ResponseEntity
                .status(isHealthy ? 200 : 503)
                .body(dbHealth);
    }
    
    /**
     * Get application health status only.
     * 
     * @return application health status
     */
    @GetMapping("/application")
    @Operation(
        summary = "Get Application Health",
        description = "Check the health status of the application"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Application is healthy",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    public ResponseEntity<Map<String, Object>> getApplicationHealth() {
        log.debug("Application health check requested");
        
        Map<String, Object> appHealth = new HashMap<>();
        
        appHealth.put("status", "UP");
        appHealth.put("timestamp", LocalDateTime.now().toString());
        appHealth.put("version", "1.0.0");
        appHealth.put("name", "RAG Application");
        appHealth.put("environment", System.getProperty("spring.profiles.active", "default"));
        
        // Add JVM information
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> jvmInfo = new HashMap<>();
        jvmInfo.put("javaVersion", System.getProperty("java.version"));
        jvmInfo.put("javaVendor", System.getProperty("java.vendor"));
        jvmInfo.put("maxMemory", runtime.maxMemory());
        jvmInfo.put("totalMemory", runtime.totalMemory());
        jvmInfo.put("freeMemory", runtime.freeMemory());
        jvmInfo.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        appHealth.put("jvm", jvmInfo);
        
        log.debug("Application health check completed");
        
        return ResponseEntity.ok(appHealth);
    }
}
