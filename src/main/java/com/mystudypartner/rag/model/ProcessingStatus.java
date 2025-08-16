package com.mystudypartner.rag.model;

/**
 * Enum representing the processing status of a document in the RAG system.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
public enum ProcessingStatus {
    
    /**
     * Document has been uploaded but processing has not started yet.
     */
    PENDING("PENDING", "Document is queued for processing"),
    
    /**
     * Document is currently being processed (extracting text, creating sections, etc.).
     */
    PROCESSING("PROCESSING", "Document is currently being processed"),
    
    /**
     * Document processing has completed successfully.
     */
    COMPLETED("COMPLETED", "Document processing completed successfully"),
    
    /**
     * Document processing failed due to an error.
     */
    FAILED("FAILED", "Document processing failed"),
    
    /**
     * Document processing was cancelled by user or system.
     */
    CANCELLED("CANCELLED", "Document processing was cancelled");
    
    private final String code;
    private final String description;
    
    ProcessingStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if the status indicates the document is in a final state.
     * 
     * @return true if the status is COMPLETED, FAILED, or CANCELLED
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
    
    /**
     * Check if the status indicates the document is in an active processing state.
     * 
     * @return true if the status is PENDING or PROCESSING
     */
    public boolean isActive() {
        return this == PENDING || this == PROCESSING;
    }
    
    /**
     * Check if the status indicates the document processing was successful.
     * 
     * @return true if the status is COMPLETED
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    
    /**
     * Check if the status indicates the document processing failed.
     * 
     * @return true if the status is FAILED or CANCELLED
     */
    public boolean isFailed() {
        return this == FAILED || this == CANCELLED;
    }
}
