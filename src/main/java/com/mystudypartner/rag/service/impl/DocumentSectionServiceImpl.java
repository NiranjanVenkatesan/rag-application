package com.mystudypartner.rag.service.impl;

import com.mystudypartner.rag.dto.DocumentSectionDto;
import com.mystudypartner.rag.exception.DocumentNotFoundException;
import com.mystudypartner.rag.mapper.DocumentSectionMapper;
// import com.mystudypartner.rag.model.Document;
import com.mystudypartner.rag.model.DocumentSection;
import com.mystudypartner.rag.model.SectionType;
import com.mystudypartner.rag.repository.DocumentRepository;
import com.mystudypartner.rag.repository.DocumentSectionRepository;
import com.mystudypartner.rag.service.DocumentSectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of DocumentSectionService for managing document sections.
 * Provides business logic for document section operations and hierarchical structure management.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentSectionServiceImpl implements DocumentSectionService {
    
    private final DocumentSectionRepository documentSectionRepository;
    private final DocumentRepository documentRepository;
    private final DocumentSectionMapper documentSectionMapper;
    
    @Override
    public Page<DocumentSectionDto> getDocumentSections(UUID documentId, Pageable pageable) {
        log.debug("Fetching sections for document: {} with pagination: {}", documentId, pageable);
        
        // Verify document exists
        if (!documentRepository.existsById(documentId)) {
            throw new DocumentNotFoundException(documentId);
        }
        
        Page<DocumentSectionDto> sections = documentSectionRepository
                .findByDocumentId(documentId, pageable)
                .map(documentSectionMapper::toDto);
        
        log.debug("Found {} sections for document: {}", sections.getTotalElements(), documentId);
        return sections;
    }
    
    @Override
    public Page<DocumentSectionDto> getSectionsByHierarchyLevel(UUID documentId, Integer hierarchyLevel, Pageable pageable) {
        log.debug("Fetching sections for document: {} at hierarchy level: {} with pagination: {}", 
                documentId, hierarchyLevel, pageable);
        
        // Verify document exists
        if (!documentRepository.existsById(documentId)) {
            throw new DocumentNotFoundException(documentId);
        }
        
        List<DocumentSection> sectionList = documentSectionRepository
                .findByDocumentIdAndHierarchyLevel(documentId, hierarchyLevel);
        
        // Convert to page manually since the repository method returns a List
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sectionList.size());
        
        List<DocumentSectionDto> sectionDtos = sectionList.subList(start, end)
                .stream()
                .map(documentSectionMapper::toDto)
                .toList();
        
        Page<DocumentSectionDto> sections = new org.springframework.data.domain.PageImpl<>(
                sectionDtos, pageable, sectionList.size());
        
        log.debug("Found {} sections for document: {} at hierarchy level: {}", 
                sections.getTotalElements(), documentId, hierarchyLevel);
        return sections;
    }
    
    @Override
    public Page<DocumentSectionDto> searchSectionsByContent(UUID documentId, String searchTerm, Pageable pageable) {
        log.debug("Searching sections for document: {} with term: '{}' with pagination: {}", 
                documentId, searchTerm, pageable);
        
        // Verify document exists
        if (!documentRepository.existsById(documentId)) {
            throw new DocumentNotFoundException(documentId);
        }
        
        Page<DocumentSectionDto> sections = documentSectionRepository
                .findByDocumentIdAndContentSimilarity(documentId, searchTerm, pageable)
                .map(documentSectionMapper::toDto);
        
        log.debug("Found {} sections for document: {} matching search term: '{}'", 
                sections.getTotalElements(), documentId, searchTerm);
        return sections;
    }
    
    @Override
    public Page<DocumentSectionDto> getSectionsByType(UUID documentId, SectionType sectionType, Pageable pageable) {
        log.debug("Fetching sections for document: {} of type: {} with pagination: {}", 
                documentId, sectionType, pageable);
        
        // Verify document exists
        if (!documentRepository.existsById(documentId)) {
            throw new DocumentNotFoundException(documentId);
        }
        
        Page<DocumentSectionDto> sections = documentSectionRepository
                .findByDocumentIdAndSectionType(documentId, sectionType, pageable)
                .map(documentSectionMapper::toDto);
        
        log.debug("Found {} sections for document: {} of type: {}", 
                sections.getTotalElements(), documentId, sectionType);
        return sections;
    }
    
    @Override
    public List<DocumentSectionDto> getRootSections(UUID documentId) {
        log.debug("Fetching root sections for document: {}", documentId);
        
        // Verify document exists
        if (!documentRepository.existsById(documentId)) {
            throw new DocumentNotFoundException(documentId);
        }
        
        List<DocumentSectionDto> rootSections = documentSectionRepository
                .findRootSectionsByDocumentId(documentId)
                .stream()
                .map(documentSectionMapper::toDto)
                .toList();
        
        log.debug("Found {} root sections for document: {}", rootSections.size(), documentId);
        return rootSections;
    }
    
    @Override
    public Page<DocumentSectionDto> getChildSections(UUID sectionId, Pageable pageable) {
        log.debug("Fetching child sections for section: {} with pagination: {}", sectionId, pageable);
        
        Page<DocumentSectionDto> childSections = documentSectionRepository
                .findByParentSectionId(sectionId, pageable)
                .map(documentSectionMapper::toDto);
        
        log.debug("Found {} child sections for section: {}", childSections.getTotalElements(), sectionId);
        return childSections;
    }
    
    @Override
    public List<DocumentSectionDto> getDocumentHierarchy(UUID documentId) {
        log.debug("Fetching complete hierarchy for document: {}", documentId);
        
        // Verify document exists
        if (!documentRepository.existsById(documentId)) {
            throw new DocumentNotFoundException(documentId);
        }
        
        List<DocumentSectionDto> hierarchy = documentSectionRepository
                .findByDocumentIdWithHierarchy(documentId)
                .stream()
                .map(section -> documentSectionMapper.toDtoWithHierarchy(section, true))
                .toList();
        
        log.debug("Found {} sections in hierarchy for document: {}", hierarchy.size(), documentId);
        return hierarchy;
    }
    
    @Override
    public Page<DocumentSectionDto> searchAllSectionsByContent(String searchTerm, Pageable pageable) {
        log.debug("Searching all sections with term: '{}' with pagination: {}", searchTerm, pageable);
        
        Page<DocumentSectionDto> sections = documentSectionRepository
                .findByContentSimilarity(searchTerm, pageable)
                .map(documentSectionMapper::toDto);
        
        log.debug("Found {} sections matching search term: '{}'", sections.getTotalElements(), searchTerm);
        return sections;
    }
}
