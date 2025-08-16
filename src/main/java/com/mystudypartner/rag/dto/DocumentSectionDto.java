package com.mystudypartner.rag.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mystudypartner.rag.model.DocumentSection;
import com.mystudypartner.rag.model.SectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for DocumentSection entity.
 * Used for REST API responses with proper JSON serialization and content preview.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentSectionDto {
    
    /**
     * Unique identifier for the section.
     */
    @JsonProperty("id")
    private UUID id;
    
    /**
     * Reference to the parent document ID.
     */
    @JsonProperty("documentId")
    private UUID documentId;
    
    /**
     * Type of section (chapter, sub-chapter, section, etc.).
     */
    @JsonProperty("sectionType")
    private SectionType sectionType;
    
    /**
     * Human-readable section type description.
     */
    @JsonProperty("sectionTypeDescription")
    private String sectionTypeDescription;
    
    /**
     * Title or heading of the section.
     */
    @JsonProperty("title")
    private String title;
    
    /**
     * Content of the section (full content).
     */
    @JsonProperty("content")
    private String content;
    
    /**
     * Content preview (first 200 characters).
     */
    @JsonProperty("contentPreview")
    private String contentPreview;
    
    /**
     * Hierarchical path representing the section's position (e.g., "1.2.3").
     */
    @JsonProperty("hierarchyPath")
    private String hierarchyPath;
    
    /**
     * Numeric level in the hierarchy (1 for chapters, 2 for sub-chapters, etc.).
     */
    @JsonProperty("hierarchyLevel")
    private Integer hierarchyLevel;
    
    /**
     * Starting page number of the section.
     */
    @JsonProperty("pageStart")
    private Integer pageStart;
    
    /**
     * Ending page number of the section.
     */
    @JsonProperty("pageEnd")
    private Integer pageEnd;
    
    /**
     * Formatted page range for display (e.g., "5-10" or "5").
     */
    @JsonProperty("pageRange")
    private String pageRange;
    
    /**
     * Word count of the section content.
     */
    @JsonProperty("wordCount")
    private Long wordCount;
    
    /**
     * Character count of the section content.
     */
    @JsonProperty("charCount")
    private Long charCount;
    
    /**
     * Order of the section within its parent.
     */
    @JsonProperty("sectionOrder")
    private Integer sectionOrder;
    
    /**
     * Reference to the parent section ID (for tree structure).
     */
    @JsonProperty("parentSectionId")
    private UUID parentSectionId;
    
    /**
     * Weaviate vector database identifier for this section.
     */
    @JsonProperty("weaviateId")
    private String weaviateId;
    
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
     * Number of child sections.
     */
    @JsonProperty("childCount")
    private Integer childCount;
    
    /**
     * Check if this section is a root section (no parent).
     */
    @JsonProperty("isRoot")
    private Boolean isRoot;
    
    /**
     * Check if this section is a leaf section (no children).
     */
    @JsonProperty("isLeaf")
    private Boolean isLeaf;
    
    /**
     * Depth of this section in the hierarchy.
     */
    @JsonProperty("depth")
    private Integer depth;
    
    /**
     * Full path from root to this section.
     */
    @JsonProperty("fullPath")
    private String fullPath;
    
    /**
     * Check if this section can have children based on its type.
     */
    @JsonProperty("canHaveChildren")
    private Boolean canHaveChildren;
    
    /**
     * Check if this section contains actual content.
     */
    @JsonProperty("isContent")
    private Boolean isContent;
    
    /**
     * List of child sections (optional, for detailed responses).
     */
    @JsonProperty("childSections")
    private List<DocumentSectionDto> childSections;

    /**
     * List of ancestor sections (parent, grandparent, etc.).
     */
    @JsonProperty("ancestors")
    private List<DocumentSectionDto> ancestors;

    /**
     * List of sibling sections (sections with the same parent).
     */
    @JsonProperty("siblings")
    private List<DocumentSectionDto> siblings;

    // Lombok builder customization for defensive copy
    public static class DocumentSectionDtoBuilder {
        public DocumentSectionDtoBuilder childSections(List<DocumentSectionDto> childSections) {
            this.childSections = childSections == null ? null : new java.util.ArrayList<>(childSections);
            return this;
        }
        public DocumentSectionDtoBuilder ancestors(List<DocumentSectionDto> ancestors) {
            this.ancestors = ancestors == null ? null : new java.util.ArrayList<>(ancestors);
            return this;
        }
        public DocumentSectionDtoBuilder siblings(List<DocumentSectionDto> siblings) {
            this.siblings = siblings == null ? null : new java.util.ArrayList<>(siblings);
            return this;
        }
    }
    public List<DocumentSectionDto> getChildSections() {
        return childSections == null ? null : new java.util.ArrayList<>(childSections);
    }

    public void setChildSections(List<DocumentSectionDto> childSections) {
        this.childSections = childSections == null ? null : new java.util.ArrayList<>(childSections);
    }

    public List<DocumentSectionDto> getAncestors() {
        return ancestors == null ? null : new java.util.ArrayList<>(ancestors);
    }

    public void setAncestors(List<DocumentSectionDto> ancestors) {
        this.ancestors = ancestors == null ? null : new java.util.ArrayList<>(ancestors);
    }

    public List<DocumentSectionDto> getSiblings() {
        return siblings == null ? null : new java.util.ArrayList<>(siblings);
    }

    public void setSiblings(List<DocumentSectionDto> siblings) {
        this.siblings = siblings == null ? null : new java.util.ArrayList<>(siblings);
    }
    
    /**
     * Constructor for mapping from DocumentSection entity.
     * 
     * @param section the document section entity to map from
     */
    public DocumentSectionDto(DocumentSection section) {
        this.id = section.getId();
        this.documentId = section.getDocumentId();
        this.sectionType = section.getSectionType();
        this.sectionTypeDescription = section.getSectionType() != null ? 
            section.getSectionType().getDescription() : null;
        this.title = section.getTitle();
        this.content = section.getContent();
        this.contentPreview = generateContentPreview(section.getContent());
        this.hierarchyPath = section.getHierarchyPath();
        this.hierarchyLevel = section.getHierarchyLevel();
        this.pageStart = section.getPageStart();
        this.pageEnd = section.getPageEnd();
        this.pageRange = section.getPageRange();
        this.wordCount = section.getWordCount();
        this.charCount = section.getCharCount();
        this.sectionOrder = section.getSectionOrder();
        this.parentSectionId = section.getParentSectionId();
        this.weaviateId = section.getWeaviateId();
        this.createdAt = section.getCreatedAt();
        this.updatedAt = section.getUpdatedAt();
        this.version = section.getVersion();
        this.childCount = section.getChildCount();
        this.isRoot = section.isRoot();
        this.isLeaf = section.isLeaf();
        this.depth = section.getDepth();
        this.fullPath = section.getFullPath();
        this.canHaveChildren = section.canHaveChildren();
        this.isContent = section.isContent();
    }
    
    /**
     * Constructor for mapping from DocumentSection entity with hierarchy.
     * 
     * @param section the document section entity to map from
     * @param includeHierarchy whether to include hierarchy information
     */
    public DocumentSectionDto(DocumentSection section, boolean includeHierarchy) {
        this(section);
        if (includeHierarchy) {
            if (section.getChildSections() != null) {
                this.childSections = section.getChildSections().stream()
                        .map(child -> new DocumentSectionDto(child, false))
                        .toList();
            }
            if (section.getAncestors() != null) {
                this.ancestors = section.getAncestors().stream()
                        .map(ancestor -> new DocumentSectionDto(ancestor, false))
                        .toList();
            }
            if (section.getSiblings() != null) {
                this.siblings = section.getSiblings().stream()
                        .map(sibling -> new DocumentSectionDto(sibling, false))
                        .toList();
            }
        }
    }
    
    /**
     * Constructor for mapping from DocumentSection entity with full hierarchy.
     * 
     * @param section the document section entity to map from
     * @param includeHierarchy whether to include hierarchy information
     * @param includeContent whether to include full content
     */
    public DocumentSectionDto(DocumentSection section, boolean includeHierarchy, boolean includeContent) {
        this(section, includeHierarchy);
        if (!includeContent) {
            this.content = null; // Exclude full content for list responses
        }
    }
    
    /**
     * Generate content preview from full content.
     * 
     * @param content the full content
     * @return content preview (first 200 characters)
     */
    private String generateContentPreview(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        
        String trimmedContent = content.trim();
        if (trimmedContent.length() <= 200) {
            return trimmedContent;
        }
        
        // Try to break at a word boundary
        String preview = trimmedContent.substring(0, 200);
        int lastSpace = preview.lastIndexOf(' ');
        
        if (lastSpace > 150) { // Only break at word if it's not too far back
            preview = preview.substring(0, lastSpace);
        }
        
        return preview + "...";
    }
    
    /**
     * Create a DocumentSectionDto from DocumentSection entity.
     * 
     * @param section the document section entity
     * @return the DocumentSectionDto
     */
    public static DocumentSectionDto fromEntity(DocumentSection section) {
        return new DocumentSectionDto(section);
    }
    
    /**
     * Create a DocumentSectionDto from DocumentSection entity with hierarchy.
     * 
     * @param section the document section entity
     * @param includeHierarchy whether to include hierarchy information
     * @return the DocumentSectionDto
     */
    public static DocumentSectionDto fromEntity(DocumentSection section, boolean includeHierarchy) {
        return new DocumentSectionDto(section, includeHierarchy);
    }
    
    /**
     * Create a DocumentSectionDto from DocumentSection entity with options.
     * 
     * @param section the document section entity
     * @param includeHierarchy whether to include hierarchy information
     * @param includeContent whether to include full content
     * @return the DocumentSectionDto
     */
    public static DocumentSectionDto fromEntity(DocumentSection section, boolean includeHierarchy, boolean includeContent) {
        return new DocumentSectionDto(section, includeHierarchy, includeContent);
    }
    
    /**
     * Create a list of DocumentSectionDto from a list of DocumentSection entities.
     * 
     * @param sections the list of document section entities
     * @return the list of DocumentSectionDto
     */
    public static List<DocumentSectionDto> fromEntities(List<DocumentSection> sections) {
        return sections.stream()
                .map(DocumentSectionDto::new)
                .toList();
    }
    
    /**
     * Create a list of DocumentSectionDto from a list of DocumentSection entities with options.
     * 
     * @param sections the list of document section entities
     * @param includeHierarchy whether to include hierarchy information
     * @param includeContent whether to include full content
     * @return the list of DocumentSectionDto
     */
    public static List<DocumentSectionDto> fromEntities(List<DocumentSection> sections, boolean includeHierarchy, boolean includeContent) {
        return sections.stream()
                .map(section -> new DocumentSectionDto(section, includeHierarchy, includeContent))
                .toList();
    }
    
    /**
     * Create a DocumentSectionDto for list responses (without full content).
     * 
     * @param section the document section entity
     * @return the DocumentSectionDto without full content
     */
    public static DocumentSectionDto forList(DocumentSection section) {
        return new DocumentSectionDto(section, false, false);
    }
    
    /**
     * Create a list of DocumentSectionDto for list responses (without full content).
     * 
     * @param sections the list of document section entities
     * @return the list of DocumentSectionDto without full content
     */
    public static List<DocumentSectionDto> forList(List<DocumentSection> sections) {
        return sections.stream()
                .map(DocumentSectionDto::forList)
                .toList();
    }
}
