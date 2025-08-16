package com.mystudypartner.rag.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing a section within a document.
 * This entity supports hierarchical document structure with self-referencing relationships.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "document_sections", indexes = {
    @Index(name = "idx_document_sections_document_id", columnList = "document_id"),
    @Index(name = "idx_document_sections_parent_id", columnList = "parent_section_id"),
    @Index(name = "idx_document_sections_section_type", columnList = "section_type"),
    @Index(name = "idx_document_sections_hierarchy_path", columnList = "hierarchy_path"),
    @Index(name = "idx_document_sections_section_order", columnList = "section_order"),
    @Index(name = "idx_document_sections_weaviate_id", columnList = "weaviate_id"),
    @Index(name = "idx_document_sections_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = {"document", "parentSection", "childSections", "content"})
@EqualsAndHashCode(of = {"id"})
public class DocumentSection {

    /**
     * All-args constructor with defensive copies for mutable fields.
     */
    public DocumentSection(UUID id, Document document, SectionType sectionType, String title, String content, String hierarchyPath, Integer hierarchyLevel, Integer pageStart, Integer pageEnd, Long wordCount, Long charCount, Integer sectionOrder, DocumentSection parentSection, List<DocumentSection> childSections, String weaviateId, LocalDateTime createdAt, LocalDateTime updatedAt, Long version) {
        this.id = id;
        this.document = document;
        this.sectionType = sectionType;
        this.title = title;
        this.content = content;
        this.hierarchyPath = hierarchyPath;
        this.hierarchyLevel = hierarchyLevel;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.wordCount = wordCount;
        this.charCount = charCount;
        this.sectionOrder = sectionOrder;
        this.parentSection = parentSection;
        this.childSections = childSections == null ? null : new java.util.ArrayList<>(childSections);
        this.weaviateId = weaviateId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
    
    /**
     * Unique identifier for the section.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    
    /**
     * Reference to the parent document.
     */
    @NotNull(message = "Document reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false, foreignKey = @ForeignKey(name = "fk_document_sections_document"))
    @JsonIgnore
    private Document document;
    
    /**
     * Type of section (chapter, sub-chapter, section, etc.).
     */
    @NotNull(message = "Section type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "section_type", nullable = false, length = 20)
    private SectionType sectionType;
    
    /**
     * Title or heading of the section.
     */
    @Column(name = "title", length = 500)
    private String title;
    
    /**
     * Content of the section (TEXT type for large content).
     */
    @NotBlank(message = "Content is required")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    /**
     * Hierarchical path representing the section's position (e.g., "1.2.3").
     */
    @Column(name = "hierarchy_path", length = 100)
    private String hierarchyPath;
    
    /**
     * Numeric level in the hierarchy (1 for chapters, 2 for sub-chapters, etc.).
     */
    @PositiveOrZero(message = "Hierarchy level must be non-negative")
    @Column(name = "hierarchy_level", nullable = false)
    private Integer hierarchyLevel;
    
    /**
     * Starting page number of the section.
     */
    @PositiveOrZero(message = "Page start must be non-negative")
    @Column(name = "page_start")
    private Integer pageStart;
    
    /**
     * Ending page number of the section.
     */
    @PositiveOrZero(message = "Page end must be non-negative")
    @Column(name = "page_end")
    private Integer pageEnd;
    
    /**
     * Word count of the section content.
     */
    @PositiveOrZero(message = "Word count must be non-negative")
    @Column(name = "word_count", nullable = false)
    private Long wordCount;
    
    /**
     * Character count of the section content.
     */
    @PositiveOrZero(message = "Character count must be non-negative")
    @Column(name = "char_count", nullable = false)
    private Long charCount;
    
    /**
     * Order of the section within its parent.
     */
    @PositiveOrZero(message = "Section order must be non-negative")
    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder;
    
    /**
     * Reference to the parent section (for tree structure).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_section_id", foreignKey = @ForeignKey(name = "fk_document_sections_parent"))
    @JsonIgnore
    private DocumentSection parentSection;
    
    /**
     * Child sections of this section.
     */
    @OneToMany(mappedBy = "parentSection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("sectionOrder ASC")
    @JsonIgnore
    @Builder.Default
    private List<DocumentSection> childSections = new ArrayList<>();

    // Lombok builder customization for defensive copy
    public static class DocumentSectionBuilder {
        private List<DocumentSection> childSections;

        public DocumentSectionBuilder childSections(List<DocumentSection> childSections) {
            this.childSections = childSections == null ? null : new java.util.ArrayList<>(childSections);
            return this;
        }

        public DocumentSection build() {
            return new DocumentSection(
                id,
                document,
                sectionType,
                title,
                content,
                hierarchyPath,
                hierarchyLevel,
                pageStart,
                pageEnd,
                wordCount,
                charCount,
                sectionOrder,
                parentSection,
                childSections == null ? null : new java.util.ArrayList<>(childSections),
                weaviateId,
                createdAt,
                updatedAt,
                version
            );
        }
    }
    public List<DocumentSection> getChildSections() {
        return childSections == null ? null : new java.util.ArrayList<>(childSections);
    }

    public void setChildSections(List<DocumentSection> childSections) {
        this.childSections = childSections == null ? null : new java.util.ArrayList<>(childSections);
    }
    
    /**
     * Weaviate vector database identifier for this section.
     */
    @Column(name = "weaviate_id", length = 255)
    private String weaviateId;
    
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
     * Pre-persist callback to set creation and update timestamps.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        
        // Calculate word and character counts if not set
        if (this.wordCount == null) {
            this.wordCount = calculateWordCount();
        }
        if (this.charCount == null) {
            this.charCount = calculateCharCount();
        }
        
        // Set hierarchy level based on section type if not set
        if (this.hierarchyLevel == null) {
            this.hierarchyLevel = this.sectionType.getHierarchyLevel();
        }
    }
    
    /**
     * Pre-update callback to set update timestamp and recalculate counts.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        
        // Recalculate word and character counts
        this.wordCount = calculateWordCount();
        this.charCount = calculateCharCount();
    }
    
    /**
     * Add a child section to this section.
     * 
     * @param childSection the child section to add
     */
    public void addChildSection(DocumentSection childSection) {
        childSections.add(childSection);
        childSection.setParentSection(this);
    }
    
    /**
     * Remove a child section from this section.
     * 
     * @param childSection the child section to remove
     */
    public void removeChildSection(DocumentSection childSection) {
        childSections.remove(childSection);
        childSection.setParentSection(null);
    }
    
    /**
     * Get the number of child sections.
     * 
     * @return the number of child sections
     */
    @JsonProperty("childCount")
    public int getChildCount() {
        return childSections.size();
    }
    
    /**
     * Check if this section is a root section (no parent).
     * 
     * @return true if this section has no parent
     */
    @JsonProperty("isRoot")
    public boolean isRoot() {
        return parentSection == null;
    }
    
    /**
     * Check if this section is a leaf section (no children).
     * 
     * @return true if this section has no children
     */
    @JsonProperty("isLeaf")
    public boolean isLeaf() {
        return childSections.isEmpty();
    }
    
    /**
     * Get the depth of this section in the hierarchy.
     * 
     * @return the depth (0 for root sections)
     */
    @JsonProperty("depth")
    public int getDepth() {
        if (isRoot()) {
            return 0;
        }
        return parentSection.getDepth() + 1;
    }
    
    /**
     * Get the full path from root to this section.
     * 
     * @return the full hierarchy path
     */
    @JsonProperty("fullPath")
    public String getFullPath() {
        if (isRoot()) {
            return hierarchyPath != null ? hierarchyPath : "";
        }
        String parentPath = parentSection.getFullPath();
        if (parentPath.isEmpty()) {
            return hierarchyPath != null ? hierarchyPath : "";
        }
        return parentPath + "." + (hierarchyPath != null ? hierarchyPath : "");
    }
    
    /**
     * Get all ancestor sections (parent, grandparent, etc.).
     * 
     * @return list of ancestor sections from root to parent
     */
    @JsonProperty("ancestors")
    public List<DocumentSection> getAncestors() {
        List<DocumentSection> ancestors = new ArrayList<>();
        DocumentSection current = parentSection;
        while (current != null) {
            ancestors.add(0, current); // Add to beginning to maintain order
            current = current.getParentSection();
        }
        return ancestors;
    }
    
    /**
     * Get all descendant sections (children, grandchildren, etc.).
     * 
     * @return list of all descendant sections
     */
    @JsonProperty("descendants")
    public List<DocumentSection> getDescendants() {
        List<DocumentSection> descendants = new ArrayList<>();
        for (DocumentSection child : childSections) {
            descendants.add(child);
            descendants.addAll(child.getDescendants());
        }
        return descendants;
    }
    
    /**
     * Get all sibling sections (sections with the same parent).
     * 
     * @return list of sibling sections
     */
    @JsonProperty("siblings")
    public List<DocumentSection> getSiblings() {
        if (isRoot()) {
            return new ArrayList<>();
        }
        return parentSection.getChildSections().stream()
                .filter(sibling -> !sibling.equals(this))
                .toList();
    }
    
    /**
     * Check if this section can have children based on its type.
     * 
     * @return true if this section type can have children
     */
    @JsonProperty("canHaveChildren")
    public boolean canHaveChildren() {
        return sectionType.canHaveChildren();
    }
    
    /**
     * Check if this section contains actual content.
     * 
     * @return true if this section type contains content
     */
    @JsonProperty("isContent")
    public boolean isContent() {
        return sectionType.isContent();
    }
    
    /**
     * Calculate the word count of the content.
     * 
     * @return the word count
     */
    private Long calculateWordCount() {
        if (content == null || content.trim().isEmpty()) {
            return 0L;
        }
        return (long) content.trim().split("\\s+").length;
    }
    
    /**
     * Calculate the character count of the content.
     * 
     * @return the character count
     */
    private Long calculateCharCount() {
        return content != null ? (long) content.length() : 0L;
    }
    
    /**
     * Get the page range as a formatted string.
     * 
     * @return formatted page range or null if not available
     */
    @JsonProperty("pageRange")
    public String getPageRange() {
        if (pageStart == null && pageEnd == null) {
            return null;
        }
        if (pageStart != null && pageEnd != null) {
            return pageStart.equals(pageEnd) ? pageStart.toString() : pageStart + "-" + pageEnd;
        }
        return pageStart != null ? pageStart.toString() : pageEnd.toString();
    }
    
    /**
     * Get the document ID for JSON serialization.
     * 
     * @return the document ID
     */
    @JsonProperty("documentId")
    public UUID getDocumentId() {
        return document != null ? document.getId() : null;
    }
    
    /**
     * Get the parent section ID for JSON serialization.
     * 
     * @return the parent section ID
     */
    @JsonProperty("parentSectionId")
    public UUID getParentSectionId() {
        return parentSection != null ? parentSection.getId() : null;
    }
}
