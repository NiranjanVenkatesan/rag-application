package com.mystudypartner.rag.exception;

import java.util.UUID;

/**
 * Exception thrown when a document is not found in the system.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
public class DocumentNotFoundException extends RuntimeException {
    
    private final UUID documentId;
    
    /**
     * Constructor with document ID.
     * 
     * @param documentId the ID of the document that was not found
     */
    public DocumentNotFoundException(UUID documentId) {
        super(String.format("Document with ID '%s' not found", documentId));
        this.documentId = documentId;
    }
    
    /**
     * Constructor with document ID and custom message.
     * 
     * @param documentId the ID of the document that was not found
     * @param message the custom error message
     */
    public DocumentNotFoundException(UUID documentId, String message) {
        super(message);
        this.documentId = documentId;
    }
    
    /**
     * Constructor with document ID, message, and cause.
     * 
     * @param documentId the ID of the document that was not found
     * @param message the custom error message
     * @param cause the cause of the exception
     */
    public DocumentNotFoundException(UUID documentId, String message, Throwable cause) {
        super(message, cause);
        this.documentId = documentId;
    }
    
    /**
     * Get the document ID that was not found.
     * 
     * @return the document ID
     */
    public UUID getDocumentId() {
        return documentId;
    }
}
