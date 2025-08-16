package com.mystudypartner.rag.repository;

import com.mystudypartner.rag.model.Document;
import com.mystudypartner.rag.model.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Document entity operations.
 * Provides data access methods for document management in the RAG system.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
    /**
     * Find documents by processing status.
     * 
     * @param status the processing status to search for
     * @return list of documents with the specified status
     */
    List<Document> findByProcessingStatus(ProcessingStatus status);
    
    /**
     * Find documents by processing status with pagination.
     * 
     * @param status the processing status to search for
     * @param pageable pagination information
     * @return page of documents with the specified status
     */
    Page<Document> findByProcessingStatus(ProcessingStatus status, Pageable pageable);
    
    /**
     * Find documents uploaded within a date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of documents uploaded in the specified range
     */
    List<Document> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find documents uploaded within a date range with pagination.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return page of documents uploaded in the specified range
     */
    Page<Document> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find documents by original filename containing the specified pattern.
     * 
     * @param filenamePattern the filename pattern to search for
     * @return list of documents matching the filename pattern
     */
    List<Document> findByOriginalFilenameContainingIgnoreCase(String filenamePattern);
    
    /**
     * Find documents by original filename containing the specified pattern with pagination.
     * 
     * @param filenamePattern the filename pattern to search for
     * @param pageable pagination information
     * @return page of documents matching the filename pattern
     */
    Page<Document> findByOriginalFilenameContainingIgnoreCase(String filenamePattern, Pageable pageable);
    
    /**
     * Find documents by system filename.
     * 
     * @param filename the system filename to search for
     * @return optional document with the specified filename
     */
    Optional<Document> findByFilename(String filename);
    
    /**
     * Find documents by MIME type.
     * 
     * @param mimeType the MIME type to search for
     * @return list of documents with the specified MIME type
     */
    List<Document> findByMimeType(String mimeType);
    
    /**
     * Find documents by MIME type with pagination.
     * 
     * @param mimeType the MIME type to search for
     * @param pageable pagination information
     * @return page of documents with the specified MIME type
     */
    Page<Document> findByMimeType(String mimeType, Pageable pageable);
    
    /**
     * Find documents with file size greater than the specified value.
     * 
     * @param minSize the minimum file size in bytes
     * @return list of documents with file size greater than the specified value
     */
    List<Document> findByFileSizeGreaterThan(Long minSize);
    
    /**
     * Find documents with file size between the specified values.
     * 
     * @param minSize the minimum file size in bytes
     * @param maxSize the maximum file size in bytes
     * @return list of documents with file size in the specified range
     */
    List<Document> findByFileSizeBetween(Long minSize, Long maxSize);
    
    /**
     * Find documents that have error messages (failed processing).
     * 
     * @return list of documents with error messages
     */
    List<Document> findByErrorMessageIsNotNull();
    
    /**
     * Find documents that have error messages with pagination.
     * 
     * @param pageable pagination information
     * @return page of documents with error messages
     */
    Page<Document> findByErrorMessageIsNotNull(Pageable pageable);
    
    /**
     * Find documents by error message containing the specified pattern.
     * 
     * @param errorPattern the error message pattern to search for
     * @return list of documents with matching error messages
     */
    List<Document> findByErrorMessageContainingIgnoreCase(String errorPattern);
    
    /**
     * Count documents by processing status.
     * 
     * @param status the processing status to count
     * @return the count of documents with the specified status
     */
    long countByProcessingStatus(ProcessingStatus status);
    
    /**
     * Count documents uploaded within a date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the count of documents uploaded in the specified range
     */
    long countByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Check if a document exists by filename.
     * 
     * @param filename the filename to check
     * @return true if a document with the specified filename exists
     */
    boolean existsByFilename(String filename);
    
    /**
     * Check if a document exists by original filename.
     * 
     * @param originalFilename the original filename to check
     * @return true if a document with the specified original filename exists
     */
    boolean existsByOriginalFilename(String originalFilename);
    
    /**
     * Find documents with their sections using JOIN FETCH.
     * 
     * @param pageable pagination information
     * @return page of documents with sections loaded
     */
    @Query("SELECT DISTINCT d FROM Document d LEFT JOIN FETCH d.sections ORDER BY d.uploadedAt DESC")
    Page<Document> findAllWithSections(Pageable pageable);
    
    /**
     * Find a document by ID with its sections using JOIN FETCH.
     * 
     * @param id the document ID
     * @return optional document with sections loaded
     */
    @Query("SELECT DISTINCT d FROM Document d LEFT JOIN FETCH d.sections WHERE d.id = :id")
    Optional<Document> findByIdWithSections(@Param("id") UUID id);
    
    /**
     * Find documents by processing status with sections using JOIN FETCH.
     * 
     * @param status the processing status
     * @param pageable pagination information
     * @return page of documents with sections loaded
     */
    @Query("SELECT DISTINCT d FROM Document d LEFT JOIN FETCH d.sections WHERE d.processingStatus = :status ORDER BY d.uploadedAt DESC")
    Page<Document> findByProcessingStatusWithSections(@Param("status") ProcessingStatus status, Pageable pageable);
    
    /**
     * Find documents that have completed processing within a date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of completed documents in the specified range
     */
    @Query("SELECT d FROM Document d WHERE d.processingStatus = 'COMPLETED' AND d.processingCompletedAt BETWEEN :startDate AND :endDate ORDER BY d.processingCompletedAt DESC")
    List<Document> findCompletedDocumentsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find documents that are currently being processed (stuck in processing state).
     * 
     * @param thresholdTime the threshold time to consider a document as stuck
     * @return list of documents that might be stuck in processing
     */
    @Query("SELECT d FROM Document d WHERE d.processingStatus = 'PROCESSING' AND d.processingStartedAt < :thresholdTime ORDER BY d.processingStartedAt ASC")
    List<Document> findStuckProcessingDocuments(@Param("thresholdTime") LocalDateTime thresholdTime);
    
    /**
     * Find documents with the highest file sizes.
     * 
     * @param limit the maximum number of documents to return
     * @return list of documents ordered by file size (descending)
     */
    @Query("SELECT d FROM Document d ORDER BY d.fileSize DESC")
    List<Document> findLargestDocuments(Pageable pageable);
    
    /**
     * Find documents with processing duration longer than the specified threshold.
     * 
     * @param thresholdMs the processing duration threshold in milliseconds
     * @return list of documents with long processing times
     */
    @Query("SELECT d FROM Document d WHERE d.processingStatus = 'COMPLETED' AND (d.processingCompletedAt - d.processingStartedAt) > :thresholdMs ORDER BY (d.processingCompletedAt - d.processingStartedAt) DESC")
    List<Document> findDocumentsWithLongProcessingTime(@Param("thresholdMs") Long thresholdMs);
    
    /**
     * Find documents by metadata JSON field.
     * 
     * @param key the metadata key
     * @param value the metadata value
     * @return list of documents with matching metadata
     */
    // @Query("SELECT d FROM Document d WHERE d.metadata ->> :key = :value")
    // List<Document> findByMetadataKeyValue(@Param("key") String key, @Param("value") String value);
    
    /**
     * Find documents with metadata containing a specific key.
     * 
     * @param key the metadata key to search for
     * @return list of documents with the specified metadata key
     */
    // @Query("SELECT d FROM Document d WHERE d.metadata ? :key")
    // List<Document> findByMetadataKey(@Param("key") String key);
    
    /**
     * Update processing status for a document.
     * 
     * @param id the document ID
     * @param status the new processing status
     * @return the number of updated records
     */
    @Modifying
    @Query("UPDATE Document d SET d.processingStatus = :status, d.updatedAt = CURRENT_TIMESTAMP WHERE d.id = :id")
    int updateProcessingStatus(@Param("id") UUID id, @Param("status") ProcessingStatus status);
    
    /**
     * Update processing start time for a document.
     * 
     * @param id the document ID
     * @param startTime the processing start time
     * @return the number of updated records
     */
    @Modifying
    @Query("UPDATE Document d SET d.processingStartedAt = :startTime, d.updatedAt = CURRENT_TIMESTAMP WHERE d.id = :id")
    int updateProcessingStartTime(@Param("id") UUID id, @Param("startTime") LocalDateTime startTime);
    
    /**
     * Update processing completion time for a document.
     * 
     * @param id the document ID
     * @param completionTime the processing completion time
     * @return the number of updated records
     */
    @Modifying
    @Query("UPDATE Document d SET d.processingCompletedAt = :completionTime, d.updatedAt = CURRENT_TIMESTAMP WHERE d.id = :id")
    int updateProcessingCompletionTime(@Param("id") UUID id, @Param("completionTime") LocalDateTime completionTime);
    
    /**
     * Update error message for a document.
     * 
     * @param id the document ID
     * @param errorMessage the error message
     * @return the number of updated records
     */
    @Modifying
    @Query("UPDATE Document d SET d.errorMessage = :errorMessage, d.updatedAt = CURRENT_TIMESTAMP WHERE d.id = :id")
    int updateErrorMessage(@Param("id") UUID id, @Param("errorMessage") String errorMessage);
    
    /**
     * Get processing statistics by status.
     * 
     * @return list of status counts
     */
    @Query("SELECT d.processingStatus, COUNT(d) FROM Document d GROUP BY d.processingStatus")
    List<Object[]> getProcessingStatusStatistics();
    
    /**
     * Get file size statistics.
     * 
     * @return array with [min, max, avg, total] file sizes
     */
    @Query("SELECT MIN(d.fileSize), MAX(d.fileSize), AVG(d.fileSize), SUM(d.fileSize) FROM Document d")
    Object[] getFileSizeStatistics();
    
    /**
     * Get upload statistics by date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of daily upload counts
     */
    @Query("SELECT DATE(d.uploadedAt), COUNT(d) FROM Document d WHERE d.uploadedAt BETWEEN :startDate AND :endDate GROUP BY DATE(d.uploadedAt) ORDER BY DATE(d.uploadedAt)")
    List<Object[]> getUploadStatisticsByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Get a map of processing statistics.
     *
     * @return map containing processing statistics
     */
    default Map<String, Object> getProcessingStatisticsMap() {
        Map<String, Object> statistics = new HashMap<>();

        statistics.put("totalDocuments", count());
        statistics.put("pendingDocuments", countByProcessingStatus(ProcessingStatus.PENDING));
        statistics.put("processingDocuments", countByProcessingStatus(ProcessingStatus.PROCESSING));
        statistics.put("completedDocuments", countByProcessingStatus(ProcessingStatus.COMPLETED));
        statistics.put("failedDocuments", countByProcessingStatus(ProcessingStatus.FAILED));
        statistics.put("cancelledDocuments", countByProcessingStatus(ProcessingStatus.CANCELLED));

        return statistics;
    }
}
