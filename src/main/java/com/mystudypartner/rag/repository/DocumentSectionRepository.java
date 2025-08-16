package com.mystudypartner.rag.repository;

import com.mystudypartner.rag.model.DocumentSection;
import com.mystudypartner.rag.model.SectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for DocumentSection entity operations.
 * Provides data access methods for hierarchical document section management in the RAG system.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Repository
public interface DocumentSectionRepository extends JpaRepository<DocumentSection, UUID> {
    
    /**
     * Find sections by document ID with proper ordering.
     * 
     * @param documentId the document ID
     * @return list of sections for the document ordered by section order
     */
    List<DocumentSection> findByDocumentIdOrderBySectionOrderAsc(UUID documentId);
    
    /**
     * Find sections by document ID with pagination.
     * 
     * @param documentId the document ID
     * @param pageable pagination information
     * @return page of sections for the document
     */
    Page<DocumentSection> findByDocumentId(UUID documentId, Pageable pageable);
    
    /**
     * Find sections by document ID and section type.
     * 
     * @param documentId the document ID
     * @param sectionType the section type
     * @return list of sections matching the criteria
     */
    List<DocumentSection> findByDocumentIdAndSectionType(UUID documentId, SectionType sectionType);
    
    /**
     * Find sections by document ID and section type with pagination.
     * 
     * @param documentId the document ID
     * @param sectionType the section type
     * @param pageable pagination information
     * @return page of sections matching the criteria
     */
    Page<DocumentSection> findByDocumentIdAndSectionType(UUID documentId, SectionType sectionType, Pageable pageable);
    
    /**
     * Find sections by section type across all documents.
     * 
     * @param sectionType the section type
     * @return list of sections with the specified type
     */
    List<DocumentSection> findBySectionType(SectionType sectionType);
    
    /**
     * Find sections by section type with pagination.
     * 
     * @param sectionType the section type
     * @param pageable pagination information
     * @return page of sections with the specified type
     */
    Page<DocumentSection> findBySectionType(SectionType sectionType, Pageable pageable);
    
    /**
     * Find sections by hierarchy path pattern.
     * 
     * @param hierarchyPathPattern the hierarchy path pattern (e.g., "1.%")
     * @return list of sections matching the hierarchy path pattern
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.hierarchyPath LIKE :pattern ORDER BY ds.hierarchyPath")
    List<DocumentSection> findByHierarchyPathPattern(@Param("pattern") String hierarchyPathPattern);
    
    /**
     * Find sections by hierarchy path pattern with pagination.
     * 
     * @param hierarchyPathPattern the hierarchy path pattern
     * @param pageable pagination information
     * @return page of sections matching the hierarchy path pattern
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.hierarchyPath LIKE :pattern ORDER BY ds.hierarchyPath")
    Page<DocumentSection> findByHierarchyPathPattern(@Param("pattern") String hierarchyPathPattern, Pageable pageable);
    
    /**
     * Find sections by exact hierarchy path.
     * 
     * @param hierarchyPath the exact hierarchy path
     * @return list of sections with the exact hierarchy path
     */
    List<DocumentSection> findByHierarchyPath(String hierarchyPath);
    
    /**
     * Find sections by parent section ID.
     * 
     * @param parentSectionId the parent section ID
     * @return list of child sections
     */
    List<DocumentSection> findByParentSectionIdOrderBySectionOrderAsc(UUID parentSectionId);
    
    /**
     * Find sections by parent section ID with pagination.
     * 
     * @param parentSectionId the parent section ID
     * @param pageable pagination information
     * @return page of child sections
     */
    Page<DocumentSection> findByParentSectionId(UUID parentSectionId, Pageable pageable);
    
    /**
     * Find root sections (sections without parent) for a document.
     * 
     * @param documentId the document ID
     * @return list of root sections
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.document.id = :documentId AND ds.parentSection IS NULL ORDER BY ds.sectionOrder")
    List<DocumentSection> findRootSectionsByDocumentId(@Param("documentId") UUID documentId);
    
    /**
     * Find sections by hierarchy level.
     * 
     * @param hierarchyLevel the hierarchy level
     * @return list of sections at the specified hierarchy level
     */
    List<DocumentSection> findByHierarchyLevel(Integer hierarchyLevel);
    
    /**
     * Find sections by hierarchy level with pagination.
     * 
     * @param hierarchyLevel the hierarchy level
     * @param pageable pagination information
     * @return page of sections at the specified hierarchy level
     */
    Page<DocumentSection> findByHierarchyLevel(Integer hierarchyLevel, Pageable pageable);
    
    /**
     * Find sections by hierarchy level for a specific document.
     * 
     * @param documentId the document ID
     * @param hierarchyLevel the hierarchy level
     * @return list of sections at the specified hierarchy level for the document
     */
    List<DocumentSection> findByDocumentIdAndHierarchyLevel(UUID documentId, Integer hierarchyLevel);
    
    /**
     * Find sections by hierarchy level range.
     * 
     * @param minLevel the minimum hierarchy level
     * @param maxLevel the maximum hierarchy level
     * @return list of sections within the hierarchy level range
     */
    List<DocumentSection> findByHierarchyLevelBetween(Integer minLevel, Integer maxLevel);
    
    /**
     * Find sections by hierarchy level range with pagination.
     * 
     * @param minLevel the minimum hierarchy level
     * @param maxLevel the maximum hierarchy level
     * @param pageable pagination information
     * @return page of sections within the hierarchy level range
     */
    Page<DocumentSection> findByHierarchyLevelBetween(Integer minLevel, Integer maxLevel, Pageable pageable);
    
    /**
     * Find sections by title containing the specified pattern.
     * 
     * @param titlePattern the title pattern to search for
     * @return list of sections with matching titles
     */
    List<DocumentSection> findByTitleContainingIgnoreCase(String titlePattern);
    
    /**
     * Find sections by title containing the specified pattern with pagination.
     * 
     * @param titlePattern the title pattern to search for
     * @param pageable pagination information
     * @return page of sections with matching titles
     */
    Page<DocumentSection> findByTitleContainingIgnoreCase(String titlePattern, Pageable pageable);
    
    /**
     * Find sections by content containing the specified text.
     * 
     * @param contentText the text to search for in content
     * @return list of sections with matching content
     */
    List<DocumentSection> findByContentContainingIgnoreCase(String contentText);
    
    /**
     * Find sections by content containing the specified text with pagination.
     * 
     * @param contentText the text to search for in content
     * @param pageable pagination information
     * @return page of sections with matching content
     */
    Page<DocumentSection> findByContentContainingIgnoreCase(String contentText, Pageable pageable);
    
    /**
     * Find sections by content containing the specified text for a specific document.
     * 
     * @param documentId the document ID
     * @param contentText the text to search for in content
     * @return list of sections with matching content for the document
     */
    List<DocumentSection> findByDocumentIdAndContentContainingIgnoreCase(UUID documentId, String contentText);
    
    /**
     * Find sections by word count greater than the specified value.
     * 
     * @param minWordCount the minimum word count
     * @return list of sections with word count greater than the specified value
     */
    List<DocumentSection> findByWordCountGreaterThan(Long minWordCount);
    
    /**
     * Find sections by word count between the specified values.
     * 
     * @param minWordCount the minimum word count
     * @param maxWordCount the maximum word count
     * @return list of sections with word count in the specified range
     */
    List<DocumentSection> findByWordCountBetween(Long minWordCount, Long maxWordCount);
    
    /**
     * Find sections by character count greater than the specified value.
     * 
     * @param minCharCount the minimum character count
     * @return list of sections with character count greater than the specified value
     */
    List<DocumentSection> findByCharCountGreaterThan(Long minCharCount);
    
    /**
     * Find sections by character count between the specified values.
     * 
     * @param minCharCount the minimum character count
     * @param maxCharCount the maximum character count
     * @return list of sections with character count in the specified range
     */
    List<DocumentSection> findByCharCountBetween(Long minCharCount, Long maxCharCount);
    
    /**
     * Find sections by page range.
     * 
     * @param pageStart the starting page number
     * @param pageEnd the ending page number
     * @return list of sections within the specified page range
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.pageStart >= :pageStart AND ds.pageEnd <= :pageEnd")
    List<DocumentSection> findByPageRange(@Param("pageStart") Integer pageStart, @Param("pageEnd") Integer pageEnd);
    
    /**
     * Find sections by page range for a specific document.
     * 
     * @param documentId the document ID
     * @param pageStart the starting page number
     * @param pageEnd the ending page number
     * @return list of sections within the specified page range for the document
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.document.id = :documentId AND ds.pageStart >= :pageStart AND ds.pageEnd <= :pageEnd")
    List<DocumentSection> findByDocumentIdAndPageRange(@Param("documentId") UUID documentId, @Param("pageStart") Integer pageStart, @Param("pageEnd") Integer pageEnd);
    
    /**
     * Find sections by Weaviate ID.
     * 
     * @param weaviateId the Weaviate vector database ID
     * @return optional section with the specified Weaviate ID
     */
    Optional<DocumentSection> findByWeaviateId(String weaviateId);
    
    /**
     * Find sections that don't have a Weaviate ID (not yet vectorized).
     * 
     * @return list of sections without Weaviate IDs
     */
    List<DocumentSection> findByWeaviateIdIsNull();
    
    /**
     * Find sections that don't have a Weaviate ID for a specific document.
     * 
     * @param documentId the document ID
     * @return list of sections without Weaviate IDs for the document
     */
    List<DocumentSection> findByDocumentIdAndWeaviateIdIsNull(UUID documentId);
    
    /**
     * Find sections with their parent and children using JOIN FETCH.
     * 
     * @param sectionId the section ID
     * @return optional section with parent and children loaded
     */
    @Query("SELECT DISTINCT ds FROM DocumentSection ds LEFT JOIN FETCH ds.parentSection LEFT JOIN FETCH ds.childSections WHERE ds.id = :sectionId")
    Optional<DocumentSection> findByIdWithHierarchy(@Param("sectionId") UUID sectionId);
    
    /**
     * Find all sections for a document with their hierarchy using JOIN FETCH.
     * 
     * @param documentId the document ID
     * @return list of sections with hierarchy loaded
     */
    @Query("SELECT DISTINCT ds FROM DocumentSection ds LEFT JOIN FETCH ds.parentSection LEFT JOIN FETCH ds.childSections WHERE ds.document.id = :documentId ORDER BY ds.hierarchyPath, ds.sectionOrder")
    List<DocumentSection> findByDocumentIdWithHierarchy(@Param("documentId") UUID documentId);
    
    /**
     * Find sections by content similarity using full-text search.
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of sections with content similar to the search term
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.content IS NOT NULL AND (LOWER(ds.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(ds.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ORDER BY ds.wordCount DESC")
    Page<DocumentSection> findByContentSimilarity(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find sections by content similarity for a specific document.
     * 
     * @param documentId the document ID
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of sections with content similar to the search term for the document
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.document.id = :documentId AND ds.content IS NOT NULL AND (LOWER(ds.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(ds.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ORDER BY ds.wordCount DESC")
    Page<DocumentSection> findByDocumentIdAndContentSimilarity(@Param("documentId") UUID documentId, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find sections by multiple search terms.
     * 
     * @param searchTerms the list of search terms
     * @param pageable pagination information
     * @return page of sections matching any of the search terms
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.content IS NOT NULL AND (LOWER(ds.content) LIKE LOWER(CONCAT('%', :searchTerm1, '%')) OR LOWER(ds.content) LIKE LOWER(CONCAT('%', :searchTerm2, '%')) OR LOWER(ds.title) LIKE LOWER(CONCAT('%', :searchTerm1, '%')) OR LOWER(ds.title) LIKE LOWER(CONCAT('%', :searchTerm2, '%'))) ORDER BY ds.wordCount DESC")
    Page<DocumentSection> findByMultipleSearchTerms(@Param("searchTerm1") String searchTerm1, @Param("searchTerm2") String searchTerm2, Pageable pageable);
    
    /**
     * Find sections with the highest word counts.
     * 
     * @param pageable pagination information
     * @return page of sections ordered by word count (descending)
     */
    @Query("SELECT ds FROM DocumentSection ds ORDER BY ds.wordCount DESC")
    Page<DocumentSection> findLargestSections(Pageable pageable);
    
    /**
     * Find sections with the highest word counts for a specific document.
     * 
     * @param documentId the document ID
     * @param pageable pagination information
     * @return page of sections ordered by word count (descending) for the document
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.document.id = :documentId ORDER BY ds.wordCount DESC")
    Page<DocumentSection> findLargestSectionsByDocumentId(@Param("documentId") UUID documentId, Pageable pageable);
    
    /**
     * Find sections by section type and content containing text.
     * 
     * @param sectionType the section type
     * @param contentText the text to search for in content
     * @param pageable pagination information
     * @return page of sections matching the criteria
     */
    Page<DocumentSection> findBySectionTypeAndContentContainingIgnoreCase(SectionType sectionType, String contentText, Pageable pageable);
    
    /**
     * Update Weaviate ID for a section.
     * 
     * @param sectionId the section ID
     * @param weaviateId the Weaviate ID
     * @return the number of updated records
     */
    @Modifying
    @Query("UPDATE DocumentSection ds SET ds.weaviateId = :weaviateId, ds.updatedAt = CURRENT_TIMESTAMP WHERE ds.id = :sectionId")
    int updateWeaviateId(@Param("sectionId") UUID sectionId, @Param("weaviateId") String weaviateId);
    
    /**
     * Update hierarchy path for a section.
     * 
     * @param sectionId the section ID
     * @param hierarchyPath the new hierarchy path
     * @return the number of updated records
     */
    @Modifying
    @Query("UPDATE DocumentSection ds SET ds.hierarchyPath = :hierarchyPath, ds.updatedAt = CURRENT_TIMESTAMP WHERE ds.id = :sectionId")
    int updateHierarchyPath(@Param("sectionId") UUID sectionId, @Param("hierarchyPath") String hierarchyPath);
    
    /**
     * Update section order for a section.
     * 
     * @param sectionId the section ID
     * @param sectionOrder the new section order
     * @return the number of updated records
     */
    @Modifying
    @Query("UPDATE DocumentSection ds SET ds.sectionOrder = :sectionOrder, ds.updatedAt = CURRENT_TIMESTAMP WHERE ds.id = :sectionId")
    int updateSectionOrder(@Param("sectionId") UUID sectionId, @Param("sectionOrder") Integer sectionOrder);
    
    /**
     * Get section statistics by type.
     * 
     * @return list of section type counts
     */
    @Query("SELECT ds.sectionType, COUNT(ds) FROM DocumentSection ds GROUP BY ds.sectionType")
    List<Object[]> getSectionTypeStatistics();
    
    /**
     * Get section statistics by hierarchy level.
     * 
     * @return list of hierarchy level counts
     */
    @Query("SELECT ds.hierarchyLevel, COUNT(ds) FROM DocumentSection ds GROUP BY ds.hierarchyLevel ORDER BY ds.hierarchyLevel")
    List<Object[]> getHierarchyLevelStatistics();
    
    /**
     * Get content statistics (word count, character count).
     * 
     * @return array with [min, max, avg] word counts and character counts
     */
    @Query("SELECT MIN(ds.wordCount), MAX(ds.wordCount), AVG(ds.wordCount), MIN(ds.charCount), MAX(ds.charCount), AVG(ds.charCount) FROM DocumentSection ds")
    Object[] getContentStatistics();
    
    /**
     * Get section statistics for a specific document.
     * 
     * @param documentId the document ID
     * @return array with [sectionCount, totalWordCount, totalCharCount, avgWordCount, avgCharCount]
     */
    @Query("SELECT COUNT(ds), SUM(ds.wordCount), SUM(ds.charCount), AVG(ds.wordCount), AVG(ds.charCount) FROM DocumentSection ds WHERE ds.document.id = :documentId")
    Object[] getDocumentSectionStatistics(@Param("documentId") UUID documentId);
    
    /**
     * Find sections that need vectorization (have content but no Weaviate ID).
     * 
     * @param pageable pagination information
     * @return page of sections that need vectorization
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.weaviateId IS NULL AND ds.content IS NOT NULL AND LENGTH(TRIM(ds.content)) > 0 ORDER BY ds.createdAt ASC")
    Page<DocumentSection> findSectionsNeedingVectorization(Pageable pageable);
    
    /**
     * Find sections that need vectorization for a specific document.
     * 
     * @param documentId the document ID
     * @return list of sections that need vectorization for the document
     */
    @Query("SELECT ds FROM DocumentSection ds WHERE ds.document.id = :documentId AND ds.weaviateId IS NULL AND ds.content IS NOT NULL AND LENGTH(TRIM(ds.content)) > 0 ORDER BY ds.sectionOrder ASC")
    List<DocumentSection> findSectionsNeedingVectorizationByDocumentId(@Param("documentId") UUID documentId);
}
