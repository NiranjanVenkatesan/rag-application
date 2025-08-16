package com.mystudypartner.rag.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for file upload response.
 * Used for REST API responses when uploading documents to the RAG system.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "DTOs are short-lived and used for serialization")
public class UploadResponse {
    
    /**
     * Unique identifier for the uploaded document.
     */
    @JsonProperty("documentId")
    private UUID documentId;
    
    /**
     * Success status of the upload operation.
     */
    @JsonProperty("success")
    private Boolean success;
    
    /**
     * Human-readable message about the upload result.
     */
    @JsonProperty("message")
    private String message;
    
    /**
     * Original filename as uploaded by the user.
     */
    @JsonProperty("originalFilename")
    private String originalFilename;
    
    /**
     * System-generated filename for storage.
     */
    @JsonProperty("filename")
    private String filename;
    
    /**
     * Size of the uploaded file in bytes.
     */
    @JsonProperty("fileSize")
    private Long fileSize;
    
    /**
     * Formatted file size for display (e.g., "1.5 MB").
     */
    @JsonProperty("fileSizeFormatted")
    private String fileSizeFormatted;
    
    /**
     * MIME type of the uploaded file.
     */
    @JsonProperty("mimeType")
    private String mimeType;
    
    /**
     * Timestamp when the file was uploaded.
     */
    @JsonProperty("uploadedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime uploadedAt;
    
    /**
     * Error code if the upload failed.
     */
    @JsonProperty("errorCode")
    private String errorCode;
    
    /**
     * Detailed error message if the upload failed.
     */
    @JsonProperty("errorMessage")
    private String errorMessage;
    
    /**
     * Error details for debugging purposes.
     */
    @JsonProperty("errorDetails")
    private String errorDetails;
    
    /**
     * Processing status of the uploaded document.
     */
    @JsonProperty("processingStatus")
    private String processingStatus;
    
    /**
     * Estimated processing time in milliseconds.
     */
    @JsonProperty("estimatedProcessingTimeMs")
    private Long estimatedProcessingTimeMs;
    
    /**
     * Formatted estimated processing time for display (e.g., "2m 30s").
     */
    @JsonProperty("estimatedProcessingTimeFormatted")
    private String estimatedProcessingTimeFormatted;
    
    /**
     * Validation warnings (non-blocking issues).
     */
    @JsonProperty("warnings")
    private String[] warnings;
    
    /**
     * Additional metadata about the upload.
     */
    @JsonProperty("metadata")
    private Object metadata;
    
    /**
     * Constructor for successful upload response.
     *
     * @param documentId the document ID
     * @param originalFilename the original filename
     * @param filename the system filename
     * @param fileSize the file size in bytes
     * @param mimeType the MIME type
     * @param uploadedAt the upload timestamp
     */
    public UploadResponse(UUID documentId, Boolean success, String message, String originalFilename, String filename, Long fileSize, String fileSizeFormatted, String mimeType, LocalDateTime uploadedAt, String errorCode, String errorMessage, String errorDetails, String processingStatus, Long estimatedProcessingTimeMs, String estimatedProcessingTimeFormatted, String[] warnings, Object metadata) {
        this.documentId = documentId;
        this.success = success;
        this.message = message;
        this.originalFilename = originalFilename;
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileSizeFormatted = fileSizeFormatted;
        this.mimeType = mimeType;
        this.uploadedAt = uploadedAt;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
        this.processingStatus = processingStatus;
        this.estimatedProcessingTimeMs = estimatedProcessingTimeMs;
        this.estimatedProcessingTimeFormatted = estimatedProcessingTimeFormatted;
        this.setWarnings(warnings);
        this.metadata = metadata;
    }

    public String[] getWarnings() {
        return warnings == null ? null : warnings.clone();
    }

    public void setWarnings(String[] warnings) {
        this.warnings = warnings == null ? null : warnings.clone();
    }

    /**
     * Constructor for successful upload response.
     * 
     * @param documentId the document ID
     * @param originalFilename the original filename
     * @param filename the system filename
     * @param fileSize the file size in bytes
     * @param mimeType the MIME type
     * @param uploadedAt the upload timestamp
     */
    public UploadResponse(UUID documentId, String originalFilename, String filename, 
                         Long fileSize, String mimeType, LocalDateTime uploadedAt) {
        this.documentId = documentId;
        this.success = true;
        this.message = "File uploaded successfully";
        this.originalFilename = originalFilename;
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileSizeFormatted = formatFileSize(fileSize);
        this.mimeType = mimeType;
        this.uploadedAt = uploadedAt;
        this.processingStatus = "PENDING";
        this.estimatedProcessingTimeMs = estimateProcessingTime(fileSize, mimeType);
        this.estimatedProcessingTimeFormatted = formatDuration(this.estimatedProcessingTimeMs);
    }
    
    /**
     * Constructor for failed upload response.
     * 
     * @param errorCode the error code
     * @param errorMessage the error message
     * @param errorDetails the error details
     * @param originalFilename the original filename (if available)
     */
    public UploadResponse(String errorCode, String errorMessage, String errorDetails, String originalFilename) {
        this.success = false;
        this.message = "File upload failed";
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
        this.originalFilename = originalFilename;
        this.uploadedAt = LocalDateTime.now();
    }
    
    /**
     * Create a successful upload response.
     * 
     * @param documentId the document ID
     * @param originalFilename the original filename
     * @param filename the system filename
     * @param fileSize the file size in bytes
     * @param mimeType the MIME type
     * @param uploadedAt the upload timestamp
     * @return the UploadResponse
     */
    public static UploadResponse success(UUID documentId, String originalFilename, String filename, 
                                       Long fileSize, String mimeType, LocalDateTime uploadedAt) {
        return new UploadResponse(documentId, originalFilename, filename, fileSize, mimeType, uploadedAt);
    }
    
    /**
     * Create a successful upload response with custom message.
     * 
     * @param documentId the document ID
     * @param originalFilename the original filename
     * @param filename the system filename
     * @param fileSize the file size in bytes
     * @param mimeType the MIME type
     * @param uploadedAt the upload timestamp
     * @param message the custom success message
     * @return the UploadResponse
     */
    public static UploadResponse success(UUID documentId, String originalFilename, String filename, 
                                       Long fileSize, String mimeType, LocalDateTime uploadedAt, String message) {
        UploadResponse response = new UploadResponse(documentId, originalFilename, filename, fileSize, mimeType, uploadedAt);
        response.setMessage(message);
        return response;
    }
    
    /**
     * Create a failed upload response.
     * 
     * @param errorCode the error code
     * @param errorMessage the error message
     * @param errorDetails the error details
     * @return the UploadResponse
     */
    public static UploadResponse failure(String errorCode, String errorMessage, String errorDetails) {
        return new UploadResponse(errorCode, errorMessage, errorDetails, null);
    }
    
    /**
     * Create a failed upload response with filename.
     * 
     * @param errorCode the error code
     * @param errorMessage the error message
     * @param errorDetails the error details
     * @param originalFilename the original filename
     * @return the UploadResponse
     */
    public static UploadResponse failure(String errorCode, String errorMessage, String errorDetails, String originalFilename) {
        return new UploadResponse(errorCode, errorMessage, errorDetails, originalFilename);
    }
    
    /**
     * Create a failed upload response with custom message.
     * 
     * @param errorCode the error code
     * @param errorMessage the error message
     * @param errorDetails the error details
     * @param originalFilename the original filename
     * @param message the custom failure message
     * @return the UploadResponse
     */
    public static UploadResponse failure(String errorCode, String errorMessage, String errorDetails, 
                                       String originalFilename, String message) {
        UploadResponse response = new UploadResponse(errorCode, errorMessage, errorDetails, originalFilename);
        response.setMessage(message);
        return response;
    }
    
    /**
     * Add warnings to the response.
     * 
     * @param warnings the warnings to add
     * @return this UploadResponse for chaining
     */
    public UploadResponse withWarnings(String... warnings) {
        this.setWarnings(warnings);
        return this;
    }
    
    /**
     * Add metadata to the response.
     * 
     * @param metadata the metadata to add
     * @return this UploadResponse for chaining
     */
    public UploadResponse withMetadata(Object metadata) {
        this.metadata = metadata;
        return this;
    }
    
    /**
     * Set custom processing status.
     * 
     * @param processingStatus the processing status
     * @return this UploadResponse for chaining
     */
    public UploadResponse withProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
        return this;
    }
    
    /**
     * Set custom estimated processing time.
     * 
     * @param estimatedProcessingTimeMs the estimated processing time in milliseconds
     * @return this UploadResponse for chaining
     */
    public UploadResponse withEstimatedProcessingTime(Long estimatedProcessingTimeMs) {
        this.estimatedProcessingTimeMs = estimatedProcessingTimeMs;
        this.estimatedProcessingTimeFormatted = formatDuration(estimatedProcessingTimeMs);
        return this;
    }
    
    /**
     * Format file size in bytes to human-readable format.
     * 
     * @param bytes the file size in bytes
     * @return formatted file size string
     */
    private String formatFileSize(Long bytes) {
        if (bytes == null) {
            return null;
        }
        
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }
    
    /**
     * Estimate processing time based on file size and MIME type.
     * 
     * @param fileSize the file size in bytes
     * @param mimeType the MIME type
     * @return estimated processing time in milliseconds
     */
    private Long estimateProcessingTime(Long fileSize, String mimeType) {
        if (fileSize == null) {
            return 30000L; // Default 30 seconds
        }
        
        // Base processing time per MB
        long baseTimePerMB = 10000L; // 10 seconds per MB
        
        // Adjust based on MIME type
        double multiplier = 1.0;
        if (mimeType != null) {
            if (mimeType.contains("pdf")) {
                multiplier = 1.5; // PDFs take longer to process
            } else if (mimeType.contains("image")) {
                multiplier = 2.0; // Images take even longer
            } else if (mimeType.contains("text")) {
                multiplier = 0.5; // Text files are faster
            }
        }
        
        long sizeInMB = fileSize / (1024 * 1024);
        return (long) (sizeInMB * baseTimePerMB * multiplier);
    }
    
    /**
     * Format duration in milliseconds to human-readable format.
     * 
     * @param durationMs the duration in milliseconds
     * @return formatted duration string
     */
    private String formatDuration(Long durationMs) {
        if (durationMs == null) {
            return null;
        }
        
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
}
