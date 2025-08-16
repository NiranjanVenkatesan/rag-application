package com.mystudypartner.rag.service.impl;

import com.mystudypartner.rag.exception.DocumentNotFoundException;
import com.mystudypartner.rag.exception.DocumentProcessingException;
import com.mystudypartner.rag.model.Document;
import com.mystudypartner.rag.model.DocumentSection;
import com.mystudypartner.rag.model.ProcessingStatus;
import com.mystudypartner.rag.model.SectionType;
import com.mystudypartner.rag.repository.DocumentRepository;
import com.mystudypartner.rag.repository.DocumentSectionRepository;
import com.mystudypartner.rag.service.DocumentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of DocumentProcessingService for PDF processing and text extraction.
 * Provides Apache Tika integration for document processing and hierarchy detection.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentProcessingServiceImpl implements DocumentProcessingService {
    
    private final DocumentRepository documentRepository;
    private final DocumentSectionRepository documentSectionRepository;
    
    // Configuration constants
    private static final String UPLOAD_DIR = "uploads";
    private static final int MAX_TEXT_LENGTH = 100000; // Tika handler limit
    
    // Hierarchy detection patterns
    private static final Pattern CHAPTER_PATTERN = Pattern.compile("^\\s*Chapter\\s+(\\d+)\\s*[:\\-]?\\s*(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SECTION_PATTERN = Pattern.compile("^\\s*(\\d+)\\.\\s*(.+)$");
    private static final Pattern SUBSECTION_PATTERN = Pattern.compile("^\\s*(\\d+)\\.(\\d+)\\s*(.+)$");
    
    @Override
    @Async
    public Document processDocument(UUID documentId) {
        log.info("Starting async document processing for document: {}", documentId);
        try {
            return processDocumentSync(documentId);
        } catch (Exception e) {
            log.error("Error during async document processing for document: {}", documentId, e);
            updateProcessingStatus(documentId, ProcessingStatus.FAILED, e.getMessage());
            throw new DocumentProcessingException(documentId, "ASYNC_PROCESSING_ERROR", e.getMessage(), e);
        }
    }
    
    @Override
    public Document processDocumentSync(UUID documentId) {
        log.info("Starting sync document processing for document: {}", documentId);
        
        try {
            updateProcessingStatus(documentId, ProcessingStatus.PROCESSING, null);
            
            String extractedText = extractTextFromDocument(documentId);
            log.debug("Extracted {} characters from document: {}", extractedText.length(), documentId);
            
            int sectionsCreated = createDocumentSections(documentId, extractedText);
            log.info("Created {} sections for document: {}", sectionsCreated, documentId);
            
            Document document = updateProcessingStatus(documentId, ProcessingStatus.COMPLETED, null);
            
            log.info("Document processing completed successfully for document: {}", documentId);
            return document;
            
        } catch (Exception e) {
            log.error("Error during document processing for document: {}", documentId, e);
            updateProcessingStatus(documentId, ProcessingStatus.FAILED, e.getMessage());
            throw new DocumentProcessingException(documentId, "PROCESSING_ERROR", e.getMessage(), e);
        }
    }
    
    @Override
    public String extractTextFromDocument(UUID documentId) {
        log.debug("Extracting text from document: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        Path filePath = Paths.get(UPLOAD_DIR, document.getFilename());
        
        if (!Files.exists(filePath)) {
            throw new DocumentProcessingException(documentId, "FILE_NOT_FOUND", 
                    "Document file not found: " + document.getFilename());
        }
        
        try {
            Tika tika = new Tika();
            
            String extractedText = tika.parseToString(filePath.toFile());
            
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new DocumentProcessingException(documentId, "NO_TEXT_EXTRACTED", 
                        "No text content could be extracted from the document");
            }
            
            log.debug("Successfully extracted {} characters from document: {}", extractedText.length(), documentId);
            return extractedText.trim();
            
        } catch (IOException | org.apache.tika.exception.TikaException e) {
            log.error("Error extracting text from document: {}", documentId, e);
            throw new DocumentProcessingException(documentId, "TEXT_EXTRACTION_ERROR", 
                    "Failed to extract text from document: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int createDocumentSections(UUID documentId, String extractedText) {
        log.debug("Creating document sections for document: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        int hierarchicalSections = detectAndCreateHierarchy(documentId, extractedText);
        
        if (hierarchicalSections == 0) {
            DocumentSection contentSection = new DocumentSection();
            contentSection.setDocument(document);
            contentSection.setSectionType(SectionType.CONTENT);
            contentSection.setTitle("Document Content");
            contentSection.setContent(extractedText);
            contentSection.setHierarchyPath("1");
            contentSection.setHierarchyLevel(0);
            contentSection.setSectionOrder(1);
            contentSection.setWordCount(countWords(extractedText));
            contentSection.setCharCount((long) extractedText.length());
            contentSection.setPageStart(1);
            contentSection.setPageEnd(1);
            
            documentSectionRepository.save(contentSection);
            log.debug("Created single content section for document: {}", documentId);
            return 1;
        }
        
        return hierarchicalSections;
    }
    
    @Override
    public int detectAndCreateHierarchy(UUID documentId, String extractedText) {
        log.debug("Detecting hierarchy for document: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        String[] lines = extractedText.split("\n");
        List<DocumentSection> sections = new ArrayList<>();
        int sectionOrder = 1;
        
        DocumentSection currentChapter = null;
        DocumentSection currentSection = null;
        int chapterNumber = 0;
        int sectionNumber = 0;
        
        StringBuilder currentContent = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // Check for chapter pattern
            Matcher chapterMatcher = CHAPTER_PATTERN.matcher(line);
            if (chapterMatcher.matches()) {
                if (currentContent.length() > 0 && currentSection != null) {
                    currentSection.setContent(currentContent.toString());
                    currentSection.setWordCount(countWords(currentContent.toString()));
                    currentSection.setCharCount((long) currentContent.length());
                    sections.add(currentSection);
                    currentContent.setLength(0);
                }
                
                chapterNumber++;
                sectionNumber = 0;
                
                currentChapter = new DocumentSection();
                currentChapter.setDocument(document);
                currentChapter.setSectionType(SectionType.CHAPTER);
                currentChapter.setTitle("Chapter " + chapterMatcher.group(1) + ": " + chapterMatcher.group(2).trim());
                currentChapter.setHierarchyPath(String.valueOf(chapterNumber));
                currentChapter.setHierarchyLevel(0);
                currentChapter.setSectionOrder(sectionOrder++);
                currentChapter.setPageStart(1);
                currentChapter.setPageEnd(1);
                
                sections.add(currentChapter);
                currentSection = null;
                continue;
            }
            
            // Check for section pattern
            Matcher sectionMatcher = SECTION_PATTERN.matcher(line);
            if (sectionMatcher.matches()) {
                if (currentContent.length() > 0 && currentSection != null) {
                    currentSection.setContent(currentContent.toString());
                    currentSection.setWordCount(countWords(currentContent.toString()));
                    currentSection.setCharCount((long) currentContent.length());
                    sections.add(currentSection);
                    currentContent.setLength(0);
                }
                
                sectionNumber++;
                
                currentSection = new DocumentSection();
                currentSection.setDocument(document);
                currentSection.setSectionType(SectionType.SECTION);
                currentSection.setTitle(sectionMatcher.group(1) + ". " + sectionMatcher.group(2).trim());
                currentSection.setParentSection(currentChapter);
                currentSection.setHierarchyPath(currentChapter != null ?
                        currentChapter.getHierarchyPath() + "." + sectionNumber :
                        String.valueOf(sectionNumber));
                currentSection.setHierarchyLevel(currentChapter != null ? 1 : 0);
                currentSection.setSectionOrder(sectionOrder++);
                currentSection.setPageStart(1);
                currentSection.setPageEnd(1);
                
                sections.add(currentSection);
                continue;
            }
            
            // Add content to current section
            if (currentSection != null) {
                if (currentContent.length() > 0) {
                    currentContent.append("\n");
                }
                currentContent.append(line);
            } else {
                currentSection = new DocumentSection();
                currentSection.setDocument(document);
                currentSection.setSectionType(SectionType.CONTENT);
                currentSection.setTitle("Document Content");
                currentSection.setHierarchyPath("1");
                currentSection.setHierarchyLevel(0);
                currentSection.setSectionOrder(sectionOrder++);
                currentSection.setPageStart(1);
                currentSection.setPageEnd(1);
                
                currentContent.append(line);
            }
        }
        
        // Save final content
        if (currentContent.length() > 0 && currentSection != null) {
            currentSection.setContent(currentContent.toString());
            currentSection.setWordCount(countWords(currentContent.toString()));
            currentSection.setCharCount((long) currentContent.length());
            sections.add(currentSection);
        }
        
        // Save all sections to database
        if (!sections.isEmpty()) {
            documentSectionRepository.saveAll(sections);
            log.debug("Created {} hierarchical sections for document: {}", sections.size(), documentId);
        }
        
        return sections.size();
    }
    
    @Override
    public Document updateProcessingStatus(UUID documentId, ProcessingStatus status, String errorMessage) {
        log.debug("Updating processing status for document {} to {}", documentId, status);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        document.setProcessingStatus(status);
        
        if (status == ProcessingStatus.PROCESSING) {
            document.setProcessingStartedAt(LocalDateTime.now());
        } else if (status == ProcessingStatus.COMPLETED || status == ProcessingStatus.FAILED || status == ProcessingStatus.CANCELLED) {
            document.setProcessingCompletedAt(LocalDateTime.now());
        }
        
        if (status == ProcessingStatus.FAILED && errorMessage != null) {
            document.setErrorMessage(errorMessage);
        }
        
        return documentRepository.save(document);
    }
    
    @Override
    public Document retryProcessing(UUID documentId) {
        log.info("Retrying processing for document: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        if (document.getProcessingStatus() != ProcessingStatus.FAILED) {
            throw new DocumentProcessingException(documentId, "INVALID_STATUS_FOR_RETRY", 
                    "Document must be in FAILED status to retry processing");
        }
        
        document.setErrorMessage(null);
        document.setProcessingStartedAt(null);
        document.setProcessingCompletedAt(null);
        
        return processDocumentSync(documentId);
    }
    
    @Override
    public Document cancelProcessing(UUID documentId) {
        log.info("Cancelling processing for document: {}", documentId);
        return updateProcessingStatus(documentId, ProcessingStatus.CANCELLED, null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getProcessingStatistics() {
        log.debug("Fetching processing statistics");
        
        java.util.Map<String, Object> statistics = documentRepository.getProcessingStatisticsMap();
        statistics.put("totalSections", documentSectionRepository.count());
        
        return statistics;
    }
    
    @Override
    public void cleanupProcessingArtifacts(UUID documentId) {
        log.debug("Cleaning up processing artifacts for document: {}", documentId);
        
        Path tempDir = Paths.get(UPLOAD_DIR, "temp", documentId.toString());
        try {
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (Exception e) {
                                log.warn("Failed to delete temporary file: {}", path, e);
                            }
                        });
            }
        } catch (Exception e) {
            log.warn("Error during cleanup of processing artifacts for document: {}", documentId, e);
        }
    }
    
    /**
     * Count words in a text string.
     * 
     * @param text the text to count words in
     * @return the word count
     */
    private long countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}
