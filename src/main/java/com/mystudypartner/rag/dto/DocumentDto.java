package com.mystudypartner.rag.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mystudypartner.rag.model.Document;
import com.mystudypartner.rag.model.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for Document entity.
 * Used for REST API responses with proper JSON serialization and formatting.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentDto {
    
    /**
     * Unique identifier for the document.
     */
    @JsonProperty("id")
    private UUID id;
    
    /**
     * System-generated filename for storage.
     */
    @JsonProperty("filename")
    private String filename;
    
    /**
     * Original filename as uploaded by the user.
     */
    @JsonProperty("originalFilename")
    private String originalFilename;
    
    /**
     * Size of the file in bytes.
     */
    @JsonProperty("fileSize")
    private Long fileSize;
    
    /**
     * Formatted file size for display (e.g., "1.5 MB").
     */
    @JsonProperty("fileSizeFormatted")
    private String fileSizeFormatted;
    
    /**
     * MIME type of the document.
     */
    @JsonProperty("mimeType")
    private String mimeType;
    
    /**
     * Current processing status of the document.
     */
    @JsonProperty("processingStatus")
    private ProcessingStatus processingStatus;
    
    /**
     * Human-readable processing status description.
     */
    @JsonProperty("processingStatusDescription")
    private String processingStatusDescription;
    
    /**
     * Timestamp when the document was uploaded.
     */
    @JsonProperty("uploadedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime uploadedAt;
    
    /**
     * Timestamp when processing started.
     */
    @JsonProperty("processingStartedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime processingStartedAt;
    
    /**
     * Timestamp when processing completed.
     */
    @JsonProperty("processingCompletedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime processingCompletedAt;
    
    /**
     * Error message if processing failed.
     */
    @JsonProperty("errorMessage")
    private String errorMessage;
    
    /**
     * Additional metadata stored as JSON.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
    
    /**
     * Timestamp when the record was created.
     */
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the record was last updated.
     */
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * Version for optimistic locking.
     */
    @JsonProperty("version")
    private Long version;
    
    /**
     * Number of sections in this document.
     */
    @JsonProperty("sectionCount")
    private Integer sectionCount;
    
    /**
     * Total word count across all sections.
     */
    @JsonProperty("totalWordCount")
    private Long totalWordCount;
    
    /**
     * Total character count across all sections.
     */
    @JsonProperty("totalCharCount")
    private Long totalCharCount;
    
    /**
     * Processing duration in milliseconds.
     */
    @JsonProperty("processingDurationMs")
    private Long processingDurationMs;
    
    /**
     * Formatted processing duration for display (e.g., "2m 30s").
     */
    @JsonProperty("processingDurationFormatted")
    private String processingDurationFormatted;
    
    /**
     * Check if the document processing is in a final state.
     */
    @JsonProperty("isProcessingComplete")
    private Boolean isProcessingComplete;
    
    /**
     * Check if the document processing was successful.
     */
    @JsonProperty("isProcessingSuccessful")
    private Boolean isProcessingSuccessful;
    
    /**
     * List of document sections (optional, for detailed responses).
     */
    @JsonProperty("sections")
    private List<DocumentSectionDto> sections;

    // Lombok builder customization for defensive copy
    public static class DocumentDtoBuilder {
        public DocumentDtoBuilder metadata(Map<String, Object> metadata) {
            this.metadata = metadata == null ? null : new java.util.HashMap<>(metadata);
            return this;
        }
        public DocumentDtoBuilder sections(List<DocumentSectionDto> sections) {
            this.sections = sections == null ? null : new java.util.ArrayList<>(sections);
            return this;
        }
    }
    public Map<String, Object> getMetadata() {
        return metadata == null ? null : new java.util.HashMap<>(metadata);
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata == null ? null : new java.util.HashMap<>(metadata);
    }

    public List<DocumentSectionDto> getSections() {
        return sections == null ? null : new java.util.ArrayList<>(sections);
    }

    public void setSections(List<DocumentSectionDto> sections) {
        this.sections = sections == null ? null : new java.util.ArrayList<>(sections);
    }
    
    /**
     * Constructor for mapping from Document entity.
     * 
     * @param document the document entity to map from
     */
    public DocumentDto(Document document) {
        this.id = document.getId();
        this.filename = document.getFilename();
        this.originalFilename = document.getOriginalFilename();
        this.fileSize = document.getFileSize();
        this.fileSizeFormatted = formatFileSize(document.getFileSize());
        this.mimeType = document.getMimeType();
        this.processingStatus = document.getProcessingStatus();
        this.processingStatusDescription = document.getProcessingStatus() != null ? 
            document.getProcessingStatus().getDescription() : null;
        this.uploadedAt = document.getUploadedAt();
        this.processingStartedAt = document.getProcessingStartedAt();
        this.processingCompletedAt = document.getProcessingCompletedAt();
        this.errorMessage = document.getErrorMessage();
        this.metadata = document.getMetadata();
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();
        this.version = document.getVersion();
        this.sectionCount = document.getSectionCount();
        this.totalWordCount = document.getTotalWordCount();
        this.totalCharCount = document.getTotalCharCount();
        this.processingDurationMs = document.getProcessingDurationMs();
        this.processingDurationFormatted = formatDuration(document.getProcessingDurationMs());
        this.isProcessingComplete = document.isProcessingComplete();
        this.isProcessingSuccessful = document.isProcessingSuccessful();
    }
    
    /**
     * Constructor for mapping from Document entity with sections.
     * 
     * @param document the document entity to map from
     * @param includeSections whether to include sections in the DTO
     */
    public DocumentDto(Document document, boolean includeSections) {
        this(document);
        if (includeSections && document.getSections() != null) {
            this.sections = document.getSections().stream()
                    .map(DocumentSectionDto::new)
                    .toList();
        }
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
    
    /**
     * Create a DocumentDto from Document entity.
     * 
     * @param document the document entity
     * @return the DocumentDto
     */
    public static DocumentDto fromEntity(Document document) {
        return new DocumentDto(document);
    }
    
    /**
     * Create a DocumentDto from Document entity with sections.
     * 
     * @param document the document entity
     * @param includeSections whether to include sections
     * @return the DocumentDto
     */
    public static DocumentDto fromEntity(Document document, boolean includeSections) {
        return new DocumentDto(document, includeSections);
    }
    
    /**
     * Create a list of DocumentDto from a list of Document entities.
     * 
     * @param documents the list of document entities
     * @return the list of DocumentDto
     */
    public static List<DocumentDto> fromEntities(List<Document> documents) {
        return documents.stream()
                .map(DocumentDto::new)
                .toList();
    }
    
    /**
     * Create a list of DocumentDto from a list of Document entities with sections.
     * 
     * @param documents the list of document entities
     * @param includeSections whether to include sections
     * @return the list of DocumentDto
     */
    public static List<DocumentDto> fromEntities(List<Document> documents, boolean includeSections) {
        return documents.stream()
                .map(doc -> new DocumentDto(doc, includeSections))
                .toList();
    }
}
