package com.mystudypartner.rag.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.MultipartConfigElement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration class for file upload settings.
 * Configures multipart handling, file size limits, and upload directory setup.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class FileUploadConfig {
    
    @Value("${app.file.upload.max-size:100MB}")
    private String maxFileSize;
    
    @Value("${app.file.upload.max-request-size:100MB}")
    private String maxRequestSize;
    
    @Value("${app.file.upload.directory:uploads}")
    private String uploadDirectory;
    
    @Value("${app.file.upload.temp-directory:temp}")
    private String tempDirectory;
    
    /**
     * Configure multipart resolver for file uploads.
     * 
     * @return configured MultipartResolver
     */
    @Bean
    public MultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        
        // Enable multipart processing
        resolver.setResolveLazily(true);
        
        log.info("Multipart resolver configured with lazy resolution");
        
        return resolver;
    }
    
    /**
     * Configure multipart configuration for file uploads.
     * 
     * @return configured MultipartConfigElement
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Set maximum file size
        factory.setMaxFileSize(DataSize.parse(maxFileSize));
        
        // Set maximum request size (total size of all files in a request)
        factory.setMaxRequestSize(DataSize.parse(maxRequestSize));
        
        // Set file size threshold for writing to disk
        factory.setFileSizeThreshold(DataSize.parse("2MB"));
        
        // Set location for temporary files
        factory.setLocation(getTempDirectoryPath().toString());
        
        MultipartConfigElement config = factory.createMultipartConfig();
        
        log.info("Multipart config configured: maxFileSize={}, maxRequestSize={}, tempLocation={}",
                maxFileSize, maxRequestSize, getTempDirectoryPath());
        
        return config;
    }
    
    /**
     * Initialize upload directories on application startup.
     * Creates necessary directories if they don't exist.
     */
    @PostConstruct
    public void initializeUploadDirectories() {
        try {
            // Create main upload directory
            Path uploadPath = getUploadDirectoryPath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }
            
            // Create temp directory
            Path tempPath = getTempDirectoryPath();
            if (!Files.exists(tempPath)) {
                Files.createDirectories(tempPath);
                log.info("Created temp directory: {}", tempPath);
            }
            
            // Create subdirectories for better organization
            createSubdirectories(uploadPath);
            
            log.info("Upload directories initialized successfully");
            
        } catch (Exception e) {
            log.error("Failed to initialize upload directories", e);
            throw new RuntimeException("Failed to initialize upload directories", e);
        }
    }
    
    /**
     * Create subdirectories for better file organization.
     * 
     * @param basePath the base upload path
     */
    private void createSubdirectories(Path basePath) {
        try {
            // Create subdirectories for different file types
            String[] subdirs = {"pdf", "documents", "processed", "failed"};
            
            for (String subdir : subdirs) {
                Path subdirPath = basePath.resolve(subdir);
                if (!Files.exists(subdirPath)) {
                    Files.createDirectories(subdirPath);
                    log.debug("Created subdirectory: {}", subdirPath);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to create some subdirectories", e);
        }
    }
    
    /**
     * Get the upload directory path.
     * 
     * @return Path to upload directory
     */
    public Path getUploadDirectoryPath() {
        return Paths.get(uploadDirectory);
    }
    
    /**
     * Get the temp directory path.
     * 
     * @return Path to temp directory
     */
    public Path getTempDirectoryPath() {
        return Paths.get(uploadDirectory, tempDirectory);
    }
    
    /**
     * Get the maximum file size as DataSize.
     * 
     * @return maximum file size
     */
    public DataSize getMaxFileSize() {
        return DataSize.parse(maxFileSize);
    }
    
    /**
     * Get the maximum request size as DataSize.
     * 
     * @return maximum request size
     */
    public DataSize getMaxRequestSize() {
        return DataSize.parse(maxRequestSize);
    }
}
