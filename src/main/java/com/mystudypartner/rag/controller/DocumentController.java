package com.mystudypartner.rag.controller;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.mystudypartner.rag.dto.DocumentDto;
import com.mystudypartner.rag.dto.DocumentSectionDto;
import com.mystudypartner.rag.dto.UploadResponse;
import com.mystudypartner.rag.model.ProcessingStatus;
import com.mystudypartner.rag.service.DocumentService;
import com.mystudypartner.rag.service.DocumentSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Document management operations.
 * Provides endpoints for document upload, retrieval, and management.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
// Removed @RequiredArgsConstructor to add explicit constructor with defensive copies
@Tag(name = "Document Management", description = "APIs for managing documents in the RAG system")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentController {
    private final DocumentService documentService;
    private final DocumentSectionService documentSectionService;

        /**
         * Constructor for DocumentController.
         *
         * <p>SpotBugs EI_EXPOSE_REP2 suppressed: documentService and documentSectionService are Spring-managed beans,
         * and their lifecycle is controlled by the framework. Defensive copying is not feasible or necessary.</p>
         */
        @SuppressFBWarnings(value = {"EI_EXPOSE_REP2"}, justification = "Spring-managed beans; lifecycle controlled by framework.")
    public DocumentController(DocumentService documentService, DocumentSectionService documentSectionService) {
        // Defensive assignment (if these are mutable, wrap or copy; if not, assign directly)
        this.documentService = documentService;
        this.documentSectionService = documentSectionService;
    }
    
    /**
     * Upload a document for processing.
     * 
     * @param file the file to upload
     * @return UploadResponse with upload details
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload Document",
        description = "Upload a document file for processing in the RAG system. Only PDF files are supported."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Document uploaded successfully",
            content = @Content(schema = @Schema(implementation = UploadResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid file or upload error"
        ),
        @ApiResponse(
            responseCode = "413",
            description = "File too large"
        )
    })
    public ResponseEntity<UploadResponse> uploadDocument(
            @Parameter(description = "Document file to upload (PDF only)", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("Received document upload request for file: {}", file.getOriginalFilename());
        
        // Validate file type (PDF only as per requirements)
    if (file.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(UploadResponse.failure(null, "File is empty", null, file.getOriginalFilename()));
    }

    if (!"application/pdf".equals(file.getContentType())) {
        return ResponseEntity.badRequest()
            .body(UploadResponse.failure(null, "Only PDF files are supported", null, file.getOriginalFilename()));
    }
        
        UploadResponse response = documentService.uploadDocument(file);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get a document by its ID.
     * 
     * @param documentId the document ID
     * @param includeSections whether to include sections in the response
     * @return the document DTO
     */
    @GetMapping("/{documentId}")
    @Operation(
        summary = "Get Document by ID",
        description = "Retrieve a document by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Document found",
            content = @Content(schema = @Schema(implementation = DocumentDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document not found"
        )
    })
    public ResponseEntity<DocumentDto> getDocumentById(
            @Parameter(description = "Document ID", required = true)
            @PathVariable UUID documentId,
            @Parameter(description = "Include document sections in response")
            @RequestParam(defaultValue = "false") boolean includeSections) {
        
        log.debug("Fetching document by ID: {} with sections: {}", documentId, includeSections);
        
        DocumentDto document = documentService.getDocumentById(documentId, includeSections);
        
        return ResponseEntity.ok(document);
    }
    
    /**
     * Get all documents with pagination.
     * 
     * @param page page number (0-based)
     * @param size page size
     * @param sortBy sort field
     * @param sortDir sort direction
     * @param includeSections whether to include sections in the response
     * @return page of document DTOs
     */
    @GetMapping
    @Operation(
        summary = "Get All Documents",
        description = "Retrieve all documents with pagination and sorting"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documents retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<DocumentDto>> getAllDocuments(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Include document sections in response")
            @RequestParam(defaultValue = "false") boolean includeSections) {
        
        log.debug("Fetching all documents with pagination: page={}, size={}, sortBy={}, sortDir={}", 
                page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<DocumentDto> documents = documentService.getAllDocuments(pageable, includeSections);
        
        return ResponseEntity.ok(documents);
    }
    
    /**
     * Get documents by processing status.
     * 
     * @param status the processing status
     * @param page page number (0-based)
     * @param size page size
     * @param includeSections whether to include sections in the response
     * @return page of document DTOs
     */
    @GetMapping("/status/{status}")
    @Operation(
        summary = "Get Documents by Status",
        description = "Retrieve documents filtered by processing status"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documents retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<DocumentDto>> getDocumentsByStatus(
            @Parameter(description = "Processing status", required = true)
            @PathVariable ProcessingStatus status,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Include document sections in response")
            @RequestParam(defaultValue = "false") boolean includeSections) {
        
        log.debug("Fetching documents by status: {} with pagination: page={}, size={}", status, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        
        Page<DocumentDto> documents = documentService.getDocumentsByStatus(status, pageable, includeSections);
        
        return ResponseEntity.ok(documents);
    }
    
    /**
     * Get documents by upload date range.
     * 
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @param page page number (0-based)
     * @param size page size
     * @param includeSections whether to include sections in the response
     * @return page of document DTOs
     */
    @GetMapping("/date-range")
    @Operation(
        summary = "Get Documents by Date Range",
        description = "Retrieve documents uploaded within a specific date range"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documents retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<DocumentDto>> getDocumentsByDateRange(
            @Parameter(description = "Start date (inclusive)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (inclusive)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Include document sections in response")
            @RequestParam(defaultValue = "false") boolean includeSections) {
        
        log.debug("Fetching documents by date range: {} to {} with pagination: page={}, size={}", 
                startDate, endDate, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        
        Page<DocumentDto> documents = documentService.getDocumentsByUploadDateRange(startDate, endDate, pageable, includeSections);
        
        return ResponseEntity.ok(documents);
    }
    
    /**
     * Search documents by filename pattern.
     * 
     * @param filenamePattern the filename pattern to search for
     * @param page page number (0-based)
     * @param size page size
     * @param includeSections whether to include sections in the response
     * @return page of document DTOs
     */
    @GetMapping("/search/filename")
    @Operation(
        summary = "Search Documents by Filename",
        description = "Search documents by filename pattern (case-insensitive)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documents retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<DocumentDto>> searchDocumentsByFilename(
            @Parameter(description = "Filename pattern to search for", required = true)
            @RequestParam String filenamePattern,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Include document sections in response")
            @RequestParam(defaultValue = "false") boolean includeSections) {
        
        log.debug("Searching documents by filename pattern: {} with pagination: page={}, size={}", 
                filenamePattern, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        
        Page<DocumentDto> documents = documentService.searchDocumentsByFilename(filenamePattern, pageable, includeSections);
        
        return ResponseEntity.ok(documents);
    }
    
    /**
     * Get document processing statistics.
     * 
     * @return map containing processing statistics
     */
    @GetMapping("/statistics")
    @Operation(
        summary = "Get Processing Statistics",
        description = "Retrieve document processing statistics"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    public ResponseEntity<Map<String, Object>> getProcessingStatistics() {
        log.debug("Fetching document processing statistics");
        
        Map<String, Object> statistics = documentService.getProcessingStatistics();
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Update document processing status.
     * 
     * @param documentId the document ID
     * @param status the new processing status
     * @param errorMessage optional error message for failed status
     * @return the updated document DTO
     */
    @PutMapping("/{documentId}/status")
    @Operation(
        summary = "Update Processing Status",
        description = "Update the processing status of a document"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Status updated successfully",
            content = @Content(schema = @Schema(implementation = DocumentDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document not found"
        )
    })
    public ResponseEntity<DocumentDto> updateProcessingStatus(
            @Parameter(description = "Document ID", required = true)
            @PathVariable UUID documentId,
            @Parameter(description = "New processing status", required = true)
            @RequestParam ProcessingStatus status,
            @Parameter(description = "Error message (required for failed status)")
            @RequestParam(required = false) String errorMessage) {
        
        log.info("Updating processing status for document {} to {} with error: {}", documentId, status, errorMessage);
        
        DocumentDto document = documentService.updateProcessingStatus(documentId, status, errorMessage);
        
        return ResponseEntity.ok(document);
    }
    
    /**
     * Delete a document.
     * 
     * @param documentId the document ID
     * @return no content response
     */
    @DeleteMapping("/{documentId}")
    @Operation(
        summary = "Delete Document",
        description = "Delete a document and all its associated sections"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Document deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document not found"
        )
    })
    public ResponseEntity<Void> deleteDocument(
            @Parameter(description = "Document ID", required = true)
            @PathVariable UUID documentId) {
        
        log.info("Deleting document with ID: {}", documentId);
        
        documentService.deleteDocument(documentId);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all sections for a document.
     * 
     * @param documentId the document ID
     * @param page page number (0-based)
     * @param size page size
     * @return page of document section DTOs
     */
    @GetMapping("/{documentId}/sections")
    @Operation(
        summary = "Get Document Sections",
        description = "Retrieve all sections for a specific document"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sections retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document not found"
        )
    })
    public ResponseEntity<Page<DocumentSectionDto>> getDocumentSections(
            @Parameter(description = "Document ID", required = true)
            @PathVariable UUID documentId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Fetching sections for document: {} with pagination: page={}, size={}", documentId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sectionOrder"));
        
        Page<DocumentSectionDto> sections = documentSectionService.getDocumentSections(documentId, pageable);
        
        return ResponseEntity.ok(sections);
    }
    
    /**
     * Get sections by hierarchy level for a document.
     * 
     * @param documentId the document ID
     * @param level the hierarchy level
     * @param page page number (0-based)
     * @param size page size
     * @return page of document section DTOs
     */
    @GetMapping("/{documentId}/sections/hierarchy/{level}")
    @Operation(
        summary = "Get Sections by Hierarchy Level",
        description = "Retrieve sections at a specific hierarchy level for a document"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sections retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document not found"
        )
    })
    public ResponseEntity<Page<DocumentSectionDto>> getSectionsByHierarchyLevel(
            @Parameter(description = "Document ID", required = true)
            @PathVariable UUID documentId,
            @Parameter(description = "Hierarchy level", required = true)
            @PathVariable Integer level,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Fetching sections for document: {} at hierarchy level: {} with pagination: page={}, size={}", 
                documentId, level, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sectionOrder"));
        
        Page<DocumentSectionDto> sections = documentSectionService.getSectionsByHierarchyLevel(documentId, level, pageable);
        
        return ResponseEntity.ok(sections);
    }
    
    /**
     * Trigger document processing.
     * 
     * @param documentId the document ID
     * @return the updated document DTO
     */
    @PostMapping("/{documentId}/process")
    @Operation(
        summary = "Trigger Document Processing",
        description = "Trigger asynchronous processing of a document"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Processing triggered successfully",
            content = @Content(schema = @Schema(implementation = DocumentDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document not found"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Document already being processed or completed"
        )
    })
    public ResponseEntity<DocumentDto> triggerDocumentProcessing(
            @Parameter(description = "Document ID", required = true)
            @PathVariable UUID documentId) {
        
        log.info("Triggering document processing for document: {}", documentId);
        
        DocumentDto document = documentService.triggerDocumentProcessing(documentId);
        
        return ResponseEntity.ok(document);
    }
    
    /**
     * Search documents by content.
     * 
     * @param query the search query
     * @param page page number (0-based)
     * @param size page size
     * @return page of document section DTOs
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search Documents by Content",
        description = "Search document sections by content across all documents"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<DocumentSectionDto>> searchDocumentsByContent(
            @Parameter(description = "Search query", required = true)
            @RequestParam String query,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Searching documents by content with query: '{}' with pagination: page={}, size={}", 
                query, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "wordCount"));
        
        Page<DocumentSectionDto> results = documentSectionService.searchAllSectionsByContent(query, pageable);
        
        return ResponseEntity.ok(results);
    }
}
