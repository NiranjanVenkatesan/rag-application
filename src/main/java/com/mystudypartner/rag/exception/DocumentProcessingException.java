package com.mystudypartner.rag.exception;

import java.util.UUID;

/**
 * Exception thrown when document processing fails.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
public class DocumentProcessingException extends RuntimeException {
    
    private final UUID documentId;
    private final String processingStep;
    
    /**
     * Constructor with document ID and processing step.
     * 
     * @param documentId the ID of the document that failed processing
     * @param processingStep the step where processing failed
     */
    public DocumentProcessingException(UUID documentId, String processingStep) {
        super(String.format("Document processing failed for document '%s' at step: %s", documentId, processingStep));
        this.documentId = documentId;
        this.processingStep = processingStep;
    }
    
    /**
     * Constructor with document ID, processing step, and custom message.
     * 
     * @param documentId the ID of the document that failed processing
     * @param processingStep the step where processing failed
     * @param message the custom error message
     */
    public DocumentProcessingException(UUID documentId, String processingStep, String message) {
        super(message);
        this.documentId = documentId;
        this.processingStep = processingStep;
    }
    
    /**
     * Constructor with document ID, processing step, message, and cause.
     * 
     * @param documentId the ID of the document that failed processing
     * @param processingStep the step where processing failed
     * @param message the custom error message
     * @param cause the cause of the exception
     */
    public DocumentProcessingException(UUID documentId, String processingStep, String message, Throwable cause) {
        super(message, cause);
        this.documentId = documentId;
        this.processingStep = processingStep;
    }
    
    /**
     * Get the document ID that failed processing.
     * 
     * @return the document ID
     */
    public UUID getDocumentId() {
        return documentId;
    }
    
    /**
     * Get the processing step where the failure occurred.
     * 
     * @return the processing step
     */
    public String getProcessingStep() {
        return processingStep;
    }
}
