package com.mystudypartner.rag.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mystudypartner.rag.converter.JsonMetadataConverter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing a document in the RAG system.
 * This is the main entity for storing document metadata and managing the document lifecycle.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_documents_filename", columnList = "filename"),
    @Index(name = "idx_documents_original_filename", columnList = "original_filename"),
    @Index(name = "idx_documents_processing_status", columnList = "processing_status"),
    @Index(name = "idx_documents_uploaded_at", columnList = "uploaded_at"),
    @Index(name = "idx_documents_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = {"sections", "metadata"})
@EqualsAndHashCode(of = {"id"})
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Model objects are managed by the application and not exposed to external clients")
public class Document {
    
    /**
     * Unique identifier for the document.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    
    /**
     * System-generated filename for storage.
     */
    @NotBlank(message = "Filename is required")
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;
    
    /**
     * Original filename as uploaded by the user.
     */
    @NotBlank(message = "Original filename is required")
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;
    
    /**
     * Size of the file in bytes.
     */
    @Positive(message = "File size must be positive")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    /**
     * MIME type of the document.
     */
    @NotBlank(message = "MIME type is required")
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;
    
    /**
     * Current processing status of the document.
     */
    @NotNull(message = "Processing status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 20)
    private ProcessingStatus processingStatus;
    
    /**
     * Timestamp when the document was uploaded.
     */
    @NotNull(message = "Upload timestamp is required")
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
    
    /**
     * Timestamp when processing started.
     */
    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;
    
    /**
     * Timestamp when processing completed.
     */
    @Column(name = "processing_completed_at")
    private LocalDateTime processingCompletedAt;
    
    /**
     * Error message if processing failed.
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    /**
     * Additional metadata stored as JSONB for flexibility.
     */
    @Convert(converter = JsonMetadataConverter.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
    /**
     * Timestamp when the record was created.
     */
    @NotNull(message = "Created timestamp is required")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the record was last updated.
     */
    @NotNull(message = "Updated timestamp is required")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Version for optimistic locking.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    
    /**
     * One-to-Many relationship with document sections.
     * Cascade operations are configured to maintain data integrity.
     */
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("sectionOrder ASC")
    @JsonIgnore
    @Builder.Default
    private List<DocumentSection> sections = new ArrayList<>();

    public Map<String, Object> getMetadata() {
        return metadata == null ? null : new HashMap<>(metadata);
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata == null ? null : new HashMap<>(metadata);
    }

    public List<DocumentSection> getSections() {
        return sections == null ? null : new ArrayList<>(sections);
    }

    public void setSections(List<DocumentSection> sections) {
        this.sections = sections == null ? null : new ArrayList<>(sections);
    }
    
    /**
     * Pre-persist callback to set creation and update timestamps.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.uploadedAt == null) {
            this.uploadedAt = now;
        }
        if (this.processingStatus == null) {
            this.processingStatus = ProcessingStatus.PENDING;
        }
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
    }
    
    /**
     * Pre-update callback to set update timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Add a section to this document.
     * 
     * @param section the section to add
     */
    public void addSection(DocumentSection section) {
        sections.add(section);
        section.setDocument(this);
    }
    
    /**
     * Remove a section from this document.
     * 
     * @param section the section to remove
     */
    public void removeSection(DocumentSection section) {
        sections.remove(section);
        section.setDocument(null);
    }
    
    /**
     * Get the total number of sections in this document.
     * 
     * @return the number of sections
     */
    @JsonProperty("sectionCount")
    public int getSectionCount() {
        return sections.size();
    }
    
    /**
     * Get the total word count across all sections.
     * 
     * @return the total word count
     */
    @JsonProperty("totalWordCount")
    public long getTotalWordCount() {
        return sections.stream()
                .mapToLong(DocumentSection::getWordCount)
                .sum();
    }
    
    /**
     * Get the total character count across all sections.
     * 
     * @return the total character count
     */
    @JsonProperty("totalCharCount")
    public long getTotalCharCount() {
        return sections.stream()
                .mapToLong(DocumentSection::getCharCount)
                .sum();
    }
    
    /**
     * Check if the document processing is in a final state.
     * 
     * @return true if processing is completed, failed, or cancelled
     */
    @JsonProperty("isProcessingComplete")
    public boolean isProcessingComplete() {
        return processingStatus.isFinal();
    }
    
    /**
     * Check if the document processing was successful.
     * 
     * @return true if processing completed successfully
     */
    @JsonProperty("isProcessingSuccessful")
    public boolean isProcessingSuccessful() {
        return processingStatus.isSuccessful();
    }
    
    /**
     * Get the processing duration in milliseconds.
     * 
     * @return the processing duration, or null if not completed
     */
    @JsonProperty("processingDurationMs")
    public Long getProcessingDurationMs() {
        if (processingStartedAt != null && processingCompletedAt != null) {
            return java.time.Duration.between(processingStartedAt, processingCompletedAt).toMillis();
        }
        return null;
    }
    
    /**
     * Add metadata to the document.
     * 
     * @param key the metadata key
     * @param value the metadata value
     */
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }
    
    /**
     * Get metadata value by key.
     * 
     * @param key the metadata key
     * @return the metadata value, or null if not found
     */
    public Object getMetadata(String key) {
        return this.metadata != null ? this.metadata.get(key) : null;
    }
    
    /**
     * Remove metadata by key.
     * 
     * @param key the metadata key to remove
     */
    public void removeMetadata(String key) {
        if (this.metadata != null) {
            this.metadata.remove(key);
        }
    }
}
