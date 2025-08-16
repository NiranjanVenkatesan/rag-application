package com.mystudypartner.rag.exception;

/**
 * Exception thrown when file upload fails.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
public class FileUploadException extends RuntimeException {
    
    private final String originalFilename;
    private final String errorCode;
    
    /**
     * Constructor with original filename and error code.
     * 
     * @param originalFilename the original filename that failed to upload
     * @param errorCode the error code indicating the type of failure
     */
    public FileUploadException(String originalFilename, String errorCode) {
        super(String.format("File upload failed for '%s' with error code: %s", originalFilename, errorCode));
        this.originalFilename = originalFilename;
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor with original filename, error code, and custom message.
     * 
     * @param originalFilename the original filename that failed to upload
     * @param errorCode the error code indicating the type of failure
     * @param message the custom error message
     */
    public FileUploadException(String originalFilename, String errorCode, String message) {
        super(message);
        this.originalFilename = originalFilename;
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor with original filename, error code, message, and cause.
     * 
     * @param originalFilename the original filename that failed to upload
     * @param errorCode the error code indicating the type of failure
     * @param message the custom error message
     * @param cause the cause of the exception
     */
    public FileUploadException(String originalFilename, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.originalFilename = originalFilename;
        this.errorCode = errorCode;
    }
    
    /**
     * Get the original filename that failed to upload.
     * 
     * @return the original filename
     */
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    /**
     * Get the error code indicating the type of failure.
     * 
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
