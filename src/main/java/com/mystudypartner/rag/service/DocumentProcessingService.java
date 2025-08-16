package com.mystudypartner.rag.service;

import com.mystudypartner.rag.model.Document;
import com.mystudypartner.rag.model.ProcessingStatus;

import java.util.UUID;

/**
 * Service interface for document processing operations.
 * Handles PDF processing, text extraction, and section creation using Apache Tika.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
public interface DocumentProcessingService {
    
    /**
     * Process a document asynchronously.
     * Orchestrates the complete PDF processing pipeline including text extraction,
     * section creation, and hierarchy detection.
     * 
     * @param documentId the document ID to process
     * @return the updated document with processing status
     */
    Document processDocument(UUID documentId);
    
    /**
     * Process a document synchronously (for testing or immediate processing).
     * 
     * @param documentId the document ID to process
     * @return the updated document with processing status
     */
    Document processDocumentSync(UUID documentId);
    
    /**
     * Extract text content from a PDF document using Apache Tika.
     * 
     * @param documentId the document ID
     * @return the extracted text content
     */
    String extractTextFromDocument(UUID documentId);
    
    /**
     * Create document sections from extracted text with hierarchy detection.
     * 
     * @param documentId the document ID
     * @param extractedText the text content to process
     * @return the number of sections created
     */
    int createDocumentSections(UUID documentId, String extractedText);
    
    /**
     * Detect and create hierarchy structure from text patterns.
     * Identifies chapters, sections, subsections based on text formatting.
     * 
     * @param documentId the document ID
     * @param extractedText the text content to analyze
     * @return the number of hierarchical sections created
     */
    int detectAndCreateHierarchy(UUID documentId, String extractedText);
    
    /**
     * Update document processing status.
     * 
     * @param documentId the document ID
     * @param status the new processing status
     * @param errorMessage optional error message
     * @return the updated document
     */
    Document updateProcessingStatus(UUID documentId, ProcessingStatus status, String errorMessage);
    
    /**
     * Retry processing for a failed document.
     * 
     * @param documentId the document ID
     * @return the updated document
     */
    Document retryProcessing(UUID documentId);
    
    /**
     * Cancel document processing.
     * 
     * @param documentId the document ID
     * @return the updated document
     */
    Document cancelProcessing(UUID documentId);
    
    /**
     * Get processing statistics for monitoring.
     * 
     * @return map containing processing statistics
     */
    java.util.Map<String, Object> getProcessingStatistics();
    
    /**
     * Clean up processing artifacts for a document.
     * Removes temporary files and processing artifacts.
     * 
     * @param documentId the document ID
     */
    void cleanupProcessingArtifacts(UUID documentId);
}
