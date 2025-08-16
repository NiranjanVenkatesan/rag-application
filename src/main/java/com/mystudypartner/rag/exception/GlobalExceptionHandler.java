package com.mystudypartner.rag.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the RAG application.
 * Provides centralized error handling and consistent error responses.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle DocumentNotFoundException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentNotFoundException(
            DocumentNotFoundException ex, WebRequest request) {
        
        log.warn("Document not found: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
                "DOCUMENT_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false)
        );
        
        errorResponse.put("documentId", ex.getDocumentId());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle DocumentProcessingException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentProcessingException(
            DocumentProcessingException ex, WebRequest request) {
        
        log.error("Document processing failed: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                "DOCUMENT_PROCESSING_FAILED",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );
        
        errorResponse.put("documentId", ex.getDocumentId());
        errorResponse.put("processingStep", ex.getProcessingStep());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handle FileUploadException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String, Object>> handleFileUploadException(
            FileUploadException ex, WebRequest request) {
        
        log.error("File upload failed: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)
        );
        
        errorResponse.put("originalFilename", ex.getOriginalFilename());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle MaxUploadSizeExceededException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, WebRequest request) {
        
        log.warn("File size exceeded maximum limit: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
                "FILE_TOO_LARGE",
                "File size exceeds maximum allowed limit",
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
    }
    
    /**
     * Handle IllegalArgumentException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Invalid argument provided: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle generic RuntimeException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handle generic Exception.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                "UNEXPECTED_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Create a standardized error response map.
     * 
     * @param errorCode the error code
     * @param message the error message
     * @param status the HTTP status code
     * @param path the request path
     * @return Map containing error response details
     */
    private Map<String, Object> createErrorResponse(String errorCode, String message, 
                                                   int status, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        return errorResponse;
    }
}
