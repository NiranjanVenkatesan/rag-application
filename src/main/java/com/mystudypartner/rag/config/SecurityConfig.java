package com.mystudypartner.rag.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for Spring Security.
 * Provides security settings, CORS configuration, and authentication setup.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
// Temporarily disabled to troubleshoot startup issues
@Slf4j
// @Configuration
// @EnableWebSecurity
public class SecurityConfig {
    
    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;
    
    @Value("${app.security.cors.allowed-origins:*}")
    private String allowedOrigins;
    
    @Value("${app.security.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;
    
    @Value("${app.security.cors.allowed-headers:*}")
    private String allowedHeaders;
    
    @Value("${app.security.cors.max-age:3600}")
    private long maxAge;
    
    /**
     * Configure security filter chain.
     * 
     * @param http the HttpSecurity object
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        if (!securityEnabled) {
            log.warn("Security is disabled - this should only be used in development");
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
        
        http
            // Disable CSRF for REST API
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure session management
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/health/**").permitAll()
                .requestMatchers("/api/v1/documents/upload").permitAll()
                .requestMatchers("/api/v1/documents/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                
                // Admin endpoints (future use)
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure headers
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000))
            );
        
        log.info("Security filter chain configured with CORS enabled");
        
        return http.build();
    }
    
    /**
     * Configure CORS settings.
     * 
     * @return configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse allowed origins
        if ("*".equals(allowedOrigins)) {
            configuration.setAllowedOriginPatterns(List.of("*"));
        } else {
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        }
        
        // Parse allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        
        // Parse allowed headers
        if ("*".equals(allowedHeaders)) {
            configuration.setAllowedHeaders(List.of("*"));
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        }
        
        // Set additional CORS settings
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(maxAge);
        
        // Set exposed headers
        configuration.setExposedHeaders(Arrays.asList(
            "Content-Disposition",
            "X-Total-Count",
            "X-Page-Number",
            "X-Page-Size"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("CORS configured: origins={}, methods={}, maxAge={}",
                allowedOrigins, allowedMethods, maxAge);
        
        return source;
    }
    
    /**
     * Configure password encoder (for future authentication).
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
    
    /**
     * Configure authentication manager (for future use).
     * 
     * @param http the HttpSecurity object
     * @return configured AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            HttpSecurity http) throws Exception {
        return http.getSharedObject(org.springframework.security.authentication.AuthenticationManager.class);
    }
}
