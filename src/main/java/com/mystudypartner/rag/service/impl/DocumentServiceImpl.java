package com.mystudypartner.rag.service.impl;

import com.mystudypartner.rag.dto.DocumentDto;
import com.mystudypartner.rag.dto.UploadResponse;
import com.mystudypartner.rag.exception.DocumentNotFoundException;
import com.mystudypartner.rag.exception.DocumentProcessingException;
import com.mystudypartner.rag.exception.FileUploadException;
import com.mystudypartner.rag.mapper.DocumentMapper;
import com.mystudypartner.rag.model.Document;
import com.mystudypartner.rag.model.ProcessingStatus;
import com.mystudypartner.rag.repository.DocumentRepository;
import com.mystudypartner.rag.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of DocumentService interface.
 * Provides business logic for document management operations.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {
    
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    
    // Configuration properties (should be externalized)
    private static final String UPLOAD_DIR = "uploads";
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final String[] ALLOWED_MIME_TYPES = {
        "application/pdf",
        "text/plain",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };
    
    @Override
    public UploadResponse uploadDocument(MultipartFile file) {
        log.info("Starting document upload for file: {}", file.getOriginalFilename());
        
        try {
            // Validate file
            validateFile(file);
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);
            String systemFilename = UUID.randomUUID().toString() + "." + fileExtension;
            
            // Save file to disk
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(systemFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // Create document entity
            Document document = new Document();
            document.setFilename(systemFilename);
            document.setOriginalFilename(originalFilename);
            document.setFileSize(file.getSize());
            document.setMimeType(file.getContentType());
            document.setProcessingStatus(ProcessingStatus.PENDING);
            document.setUploadedAt(LocalDateTime.now());
            
            // Save document
            Document savedDocument = documentRepository.save(document);
            
            log.info("Document uploaded successfully with ID: {}", savedDocument.getId());
            
            return UploadResponse.success(
                    savedDocument.getId(),
                    originalFilename,
                    systemFilename,
                    file.getSize(),
                    file.getContentType(),
                    savedDocument.getUploadedAt()
            );
            
        } catch (IOException e) {
            log.error("Failed to save file: {}", file.getOriginalFilename(), e);
            throw new FileUploadException(
                    file.getOriginalFilename(),
                    "FILE_SAVE_ERROR",
                    "Failed to save file to disk",
                    e
            );
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", file.getOriginalFilename(), e);
            throw new FileUploadException(
                    file.getOriginalFilename(),
                    "UPLOAD_ERROR",
                    "Unexpected error during file upload",
                    e
            );
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public DocumentDto getDocumentById(UUID documentId, boolean includeSections) {
        log.debug("Fetching document by ID: {} with sections: {}", documentId, includeSections);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        if (includeSections) {
            document = documentRepository.findByIdWithSections(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));
        }
        
        return includeSections ? 
                documentMapper.toDtoWithSections(document, true) : 
                documentMapper.toDto(document);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentDto> getAllDocuments(Pageable pageable, boolean includeSections) {
        log.debug("Fetching all documents with pagination: {}", pageable);
        
        Page<Document> documents = documentRepository.findAll(pageable);
        
        return documents.map(document -> includeSections ? 
                documentMapper.toDtoWithSections(document, true) : 
                documentMapper.toDto(document));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentDto> getDocumentsByStatus(ProcessingStatus status, Pageable pageable, boolean includeSections) {
        log.debug("Fetching documents by status: {} with pagination: {}", status, pageable);
        
        Page<Document> documents = documentRepository.findByProcessingStatus(status, pageable);
        
        return documents.map(document -> includeSections ? 
                documentMapper.toDtoWithSections(document, true) : 
                documentMapper.toDto(document));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentDto> getDocumentsByUploadDateRange(LocalDateTime startDate, LocalDateTime endDate, 
                                                         Pageable pageable, boolean includeSections) {
        log.debug("Fetching documents by upload date range: {} to {} with pagination: {}", startDate, endDate, pageable);
        
    Page<Document> documents = documentRepository.findByUploadedAtBetween(startDate, endDate, pageable);
        
        return documents.map(document -> includeSections ? 
                documentMapper.toDtoWithSections(document, true) : 
                documentMapper.toDto(document));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentDto> searchDocumentsByFilename(String filenamePattern, Pageable pageable, boolean includeSections) {
        log.debug("Searching documents by filename pattern: {} with pagination: {}", filenamePattern, pageable);
        
    Page<Document> documents = documentRepository.findByOriginalFilenameContainingIgnoreCase(filenamePattern, pageable);
        
        return documents.map(document -> includeSections ? 
                documentMapper.toDtoWithSections(document, true) : 
                documentMapper.toDto(document));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProcessingStatistics() {
        log.debug("Fetching document processing statistics");
        
        return documentRepository.getProcessingStatisticsMap();
    }
    
    @Override
    public DocumentDto updateProcessingStatus(UUID documentId, ProcessingStatus status, String errorMessage) {
        log.info("Updating processing status for document {} to {} with error: {}", documentId, status, errorMessage);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        document.setProcessingStatus(status);
        
        if (status == ProcessingStatus.PROCESSING) {
            document.setProcessingStartedAt(LocalDateTime.now());
        } else if (status == ProcessingStatus.COMPLETED) {
            document.setProcessingCompletedAt(LocalDateTime.now());
        } else if (status == ProcessingStatus.FAILED) {
            document.setProcessingCompletedAt(LocalDateTime.now());
            document.setErrorMessage(errorMessage);
        }
        
        Document updatedDocument = documentRepository.save(document);
        
        return documentMapper.toDto(updatedDocument);
    }
    
    @Override
    public void deleteDocument(UUID documentId) {
        log.info("Deleting document with ID: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        // Delete file from disk
        try {
            Path filePath = Paths.get(UPLOAD_DIR, document.getFilename());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file from disk: {}", document.getFilename(), e);
        }
        
        documentRepository.delete(document);
        
        log.info("Document deleted successfully: {}", documentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Document> getDocumentsForProcessing(int limit) {
    log.debug("Fetching {} documents for processing", limit);
    Page<Document> page = documentRepository.findByProcessingStatus(ProcessingStatus.PENDING, 
        org.springframework.data.domain.PageRequest.of(0, limit));
    return page.getContent();
    }
    
    @Override
    public Document markProcessingStarted(UUID documentId) {
        log.info("Marking document processing as started: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        document.setProcessingStatus(ProcessingStatus.PROCESSING);
        document.setProcessingStartedAt(LocalDateTime.now());
        
        return documentRepository.save(document);
    }
    
    @Override
    public Document markProcessingCompleted(UUID documentId) {
        log.info("Marking document processing as completed: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        document.setProcessingStatus(ProcessingStatus.COMPLETED);
        document.setProcessingCompletedAt(LocalDateTime.now());
        
        return documentRepository.save(document);
    }
    
    @Override
    public Document markProcessingFailed(UUID documentId, String errorMessage) {
        log.error("Marking document processing as failed: {} with error: {}", documentId, errorMessage);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        document.setProcessingStatus(ProcessingStatus.FAILED);
        document.setProcessingCompletedAt(LocalDateTime.now());
        document.setErrorMessage(errorMessage);
        
        return documentRepository.save(document);
    }
    
    @Override
    public Document cancelProcessing(UUID documentId) {
        log.info("Cancelling document processing: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        document.setProcessingStatus(ProcessingStatus.CANCELLED);
        document.setProcessingCompletedAt(LocalDateTime.now());
        
        return documentRepository.save(document);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Document getDocumentWithSections(UUID documentId) {
        log.debug("Fetching document with sections: {}", documentId);
        
        return documentRepository.findByIdWithSections(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
    }
    
    @Override
    public Document saveDocument(Document document) {
        log.debug("Saving document: {}", document.getId());
        
        return documentRepository.save(document);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean documentExists(UUID documentId) {
        return documentRepository.existsById(documentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getDocumentCountByStatus(ProcessingStatus status) {
        return documentRepository.countByProcessingStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalDocumentCount() {
        return documentRepository.count();
    }
    
    @Override
    public DocumentDto triggerDocumentProcessing(UUID documentId) {
        log.info("Triggering document processing for document: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        // Check if document is in a state that allows processing
        if (document.getProcessingStatus() == ProcessingStatus.PROCESSING) {
            throw new DocumentProcessingException(documentId, "ALREADY_PROCESSING", 
                    "Document is already being processed");
        }
        
        if (document.getProcessingStatus() == ProcessingStatus.COMPLETED) {
            throw new DocumentProcessingException(documentId, "ALREADY_COMPLETED", 
                    "Document has already been processed");
        }
        
        // Mark processing as started
        document.setProcessingStatus(ProcessingStatus.PROCESSING);
        document.setProcessingStartedAt(LocalDateTime.now());
        document.setErrorMessage(null); // Clear any previous error messages
        
        Document savedDocument = documentRepository.save(document);
        
        // TODO: In a real implementation, this would trigger an async processing task
        // For now, we'll just return the updated document
        log.info("Document processing triggered for document: {}", documentId);
        
        return documentMapper.toDto(savedDocument);
    }
    
    /**
     * Validate uploaded file.
     * 
     * @param file the file to validate
     * @throws FileUploadException if validation fails
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException(
                    file.getOriginalFilename(),
                    "EMPTY_FILE",
                    "File is empty"
            );
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException(
                    file.getOriginalFilename(),
                    "FILE_TOO_LARGE",
                    String.format("File size %d exceeds maximum allowed size %d", file.getSize(), MAX_FILE_SIZE)
            );
        }
        
        String contentType = file.getContentType();
        boolean isValidMimeType = false;
        for (String allowedType : ALLOWED_MIME_TYPES) {
            if (allowedType.equals(contentType)) {
                isValidMimeType = true;
                break;
            }
        }
        
        if (!isValidMimeType) {
            throw new FileUploadException(
                    file.getOriginalFilename(),
                    "INVALID_MIME_TYPE",
                    String.format("MIME type %s is not allowed", contentType)
            );
        }
    }
}
