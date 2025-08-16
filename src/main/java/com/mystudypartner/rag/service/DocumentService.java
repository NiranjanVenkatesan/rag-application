package com.mystudypartner.rag.service;

import com.mystudypartner.rag.dto.DocumentDto;
import com.mystudypartner.rag.dto.UploadResponse;
import com.mystudypartner.rag.model.Document;
import com.mystudypartner.rag.model.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Document business operations.
 * Defines the contract for document management operations following clean architecture principles.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
public interface DocumentService {
    
    /**
     * Upload and process a document.
     * 
     * @param file the file to upload
     * @return UploadResponse with upload details
     */
    UploadResponse uploadDocument(MultipartFile file);
    
    /**
     * Get a document by its ID.
     * 
     * @param documentId the document ID
     * @param includeSections whether to include sections in the response
     * @return the document DTO
     */
    DocumentDto getDocumentById(UUID documentId, boolean includeSections);
    
    /**
     * Get all documents with pagination.
     * 
     * @param pageable pagination parameters
     * @param includeSections whether to include sections in the response
     * @return page of document DTOs
     */
    Page<DocumentDto> getAllDocuments(Pageable pageable, boolean includeSections);
    
    /**
     * Get documents by processing status.
     * 
     * @param status the processing status
     * @param pageable pagination parameters
     * @param includeSections whether to include sections in the response
     * @return page of document DTOs
     */
    Page<DocumentDto> getDocumentsByStatus(ProcessingStatus status, Pageable pageable, boolean includeSections);
    
    /**
     * Get documents uploaded within a date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination parameters
     * @param includeSections whether to include sections in the response
     * @return page of document DTOs
     */
    Page<DocumentDto> getDocumentsByUploadDateRange(LocalDateTime startDate, LocalDateTime endDate, 
                                                   Pageable pageable, boolean includeSections);
    
    /**
     * Search documents by filename pattern.
     * 
     * @param filenamePattern the filename pattern to search for
     * @param pageable pagination parameters
     * @param includeSections whether to include sections in the response
     * @return page of document DTOs
     */
    Page<DocumentDto> searchDocumentsByFilename(String filenamePattern, Pageable pageable, boolean includeSections);
    
    /**
     * Get document processing statistics.
     * 
     * @return map containing processing statistics
     */
    java.util.Map<String, Object> getProcessingStatistics();
    
    /**
     * Update document processing status.
     * 
     * @param documentId the document ID
     * @param status the new processing status
     * @param errorMessage optional error message for failed status
     * @return the updated document DTO
     */
    DocumentDto updateProcessingStatus(UUID documentId, ProcessingStatus status, String errorMessage);
    
    /**
     * Delete a document and all its sections.
     * 
     * @param documentId the document ID
     */
    void deleteDocument(UUID documentId);
    
    /**
     * Get documents that need processing.
     * 
     * @param limit maximum number of documents to return
     * @return list of documents that need processing
     */
    List<Document> getDocumentsForProcessing(int limit);
    
    /**
     * Mark document processing as started.
     * 
     * @param documentId the document ID
     * @return the updated document
     */
    Document markProcessingStarted(UUID documentId);
    
    /**
     * Mark document processing as completed.
     * 
     * @param documentId the document ID
     * @return the updated document
     */
    Document markProcessingCompleted(UUID documentId);
    
    /**
     * Mark document processing as failed.
     * 
     * @param documentId the document ID
     * @param errorMessage the error message
     * @return the updated document
     */
    Document markProcessingFailed(UUID documentId, String errorMessage);
    
    /**
     * Cancel document processing.
     * 
     * @param documentId the document ID
     * @return the updated document
     */
    Document cancelProcessing(UUID documentId);
    
    /**
     * Get document with sections for processing.
     * 
     * @param documentId the document ID
     * @return the document with sections
     */
    Document getDocumentWithSections(UUID documentId);
    
    /**
     * Save document entity.
     * 
     * @param document the document to save
     * @return the saved document
     */
    Document saveDocument(Document document);
    
    /**
     * Check if document exists.
     * 
     * @param documentId the document ID
     * @return true if document exists
     */
    boolean documentExists(UUID documentId);
    
    /**
     * Get document count by status.
     * 
     * @param status the processing status
     * @return the count of documents with the given status
     */
    long getDocumentCountByStatus(ProcessingStatus status);
    
    /**
     * Get total document count.
     * 
     * @return the total number of documents
     */
    long getTotalDocumentCount();
    
    /**
     * Trigger document processing asynchronously.
     * 
     * @param documentId the document ID
     * @return the updated document DTO
     */
    DocumentDto triggerDocumentProcessing(UUID documentId);
}
