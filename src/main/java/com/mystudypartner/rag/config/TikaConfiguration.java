package com.mystudypartner.rag.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.pdf.PDFParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration class for Apache Tika components.
 * Provides bean definitions for document processing and text extraction.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
// Temporarily disabled to get application running
@Slf4j
// @Configuration
public class TikaConfiguration {
    
    @Value("${app.tika.max-text-length:100000}")
    private int maxTextLength;
    
    @Value("${app.tika.enable-pdf-parsing:true}")
    private boolean enablePdfParsing;
    
    @Value("${app.tika.enable-ocr:false}")
    private boolean enableOcr;
    
    /**
     * Configure Tika instance for general text extraction.
     * 
     * @return configured Tika instance
     */
    @Bean
    public Tika tika() {
    Tika tika = new Tika();
    log.info("Tika configured with maxTextLength={}, enablePdfParsing={}, enableOcr={}",
        maxTextLength, enablePdfParsing, enableOcr);
    return tika;
    }
    
    /**
     * Configure TikaConfig for advanced parsing options.
     * 
     * @return configured TikaConfig
     */
    @Bean
    public TikaConfig tikaConfig() {
        try {
            // Load default Tika configuration
            TikaConfig config = TikaConfig.getDefaultConfig();
            
            log.info("TikaConfig loaded with default configuration");
            
            return config;
        } catch (Exception e) {
            log.error("Failed to load TikaConfig, using default", e);
            return TikaConfig.getDefaultConfig();
        }
    }
    
    /**
     * Configure AutoDetectParser for automatic format detection.
     * 
     * @return configured AutoDetectParser
     */
    @Bean
    public AutoDetectParser autoDetectParser() {
    AutoDetectParser parser = new AutoDetectParser();
    log.info("AutoDetectParser configured");
    return parser;
    }
    
    /**
     * Configure PDF parser specifically for PDF documents.
     * 
     * @return configured PDFParser
     */
    @Bean
    public PDFParser pdfParser() {
        PDFParser parser = new PDFParser();
        
        // Configure PDF-specific settings
        parser.setExtractUniqueInlineImagesOnly(false);
        parser.setExtractInlineImages(true);
        
        log.info("PDFParser configured for inline image extraction");
        
        return parser;
    }
    
    /**
     * Configure general parser for document processing.
     * 
     * @return configured Parser
     */
    @Bean
    public Parser parser() {
        return new AutoDetectParser();
    }
    
    /**
     * Configure detector for MIME type detection.
     * 
     * @return configured Detector
     */
    @Bean
    public Detector detector() {
        return new org.apache.tika.detect.DefaultDetector();
    }
    
    /**
     * Create a metadata instance for document processing.
     * 
     * @return new Metadata instance
     */
    @Bean
    public Metadata metadata() {
        return new Metadata();
    }
    
    /**
     * Validate Tika configuration on startup.
     * Tests basic functionality to ensure proper setup.
     */
    @PostConstruct
    public void validateTikaConfiguration() {
        try {
            Tika tika = tika();
            Detector detector = detector();
            
            // Test with a simple text string
            String testText = "Hello, this is a test document.";
            String detectedType = tika.detect(testText.getBytes());
            
            log.info("Tika configuration validated successfully. Detected type for test text: {}", detectedType);
            
        } catch (Exception e) {
            log.error("Tika configuration validation failed", e);
            throw new RuntimeException("Tika configuration validation failed", e);
        }
    }
    
    /**
     * Get the maximum text length configuration.
     * 
     * @return maximum text length
     */
    public int getMaxTextLength() {
        return maxTextLength;
    }
    
    /**
     * Check if PDF parsing is enabled.
     * 
     * @return true if PDF parsing is enabled
     */
    public boolean isPdfParsingEnabled() {
        return enablePdfParsing;
    }
    
    /**
     * Check if OCR is enabled.
     * 
     * @return true if OCR is enabled
     */
    public boolean isOcrEnabled() {
        return enableOcr;
    }
}
