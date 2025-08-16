package com.mystudypartner.rag.service;

import com.mystudypartner.rag.dto.DocumentSectionDto;
import com.mystudypartner.rag.model.SectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for DocumentSection business operations.
 * Provides methods for managing document sections and their hierarchical structure.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
public interface DocumentSectionService {
    
    /**
     * Get all sections for a document.
     * 
     * @param documentId the document ID
     * @param pageable pagination information
     * @return page of document section DTOs
     */
    Page<DocumentSectionDto> getDocumentSections(UUID documentId, Pageable pageable);
    
    /**
     * Get sections by hierarchy level for a document.
     * 
     * @param documentId the document ID
     * @param hierarchyLevel the hierarchy level
     * @param pageable pagination information
     * @return page of document section DTOs
     */
    Page<DocumentSectionDto> getSectionsByHierarchyLevel(UUID documentId, Integer hierarchyLevel, Pageable pageable);
    
    /**
     * Search sections by content for a document.
     * 
     * @param documentId the document ID
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of document section DTOs
     */
    Page<DocumentSectionDto> searchSectionsByContent(UUID documentId, String searchTerm, Pageable pageable);
    
    /**
     * Get sections by type for a document.
     * 
     * @param documentId the document ID
     * @param sectionType the section type
     * @param pageable pagination information
     * @return page of document section DTOs
     */
    Page<DocumentSectionDto> getSectionsByType(UUID documentId, SectionType sectionType, Pageable pageable);
    
    /**
     * Get root sections (level 0) for a document.
     * 
     * @param documentId the document ID
     * @return list of root document section DTOs
     */
    List<DocumentSectionDto> getRootSections(UUID documentId);
    
    /**
     * Get child sections for a specific section.
     * 
     * @param sectionId the parent section ID
     * @param pageable pagination information
     * @return page of child document section DTOs
     */
    Page<DocumentSectionDto> getChildSections(UUID sectionId, Pageable pageable);
    
    /**
     * Get section hierarchy for a document.
     * 
     * @param documentId the document ID
     * @return list of document section DTOs with hierarchy information
     */
    List<DocumentSectionDto> getDocumentHierarchy(UUID documentId);
    
    /**
     * Search sections across all documents by content.
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of document section DTOs
     */
    Page<DocumentSectionDto> searchAllSectionsByContent(String searchTerm, Pageable pageable);
}
