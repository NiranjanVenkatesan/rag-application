package com.mystudypartner.rag.model;

/**
 * Enum representing the different types of sections that can be extracted from a document.
 * These types form a hierarchical structure for organizing document content.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
public enum SectionType {
    
    /**
     * Main chapter of a document (e.g., "Chapter 1: Introduction").
     * This is the highest level of document organization.
     */
    CHAPTER("CHAPTER", "Main chapter", 1),
    
    /**
     * Sub-chapter within a main chapter (e.g., "1.1 Background").
     * Second level of document hierarchy.
     */
    SUB_CHAPTER("SUB_CHAPTER", "Sub-chapter", 2),
    
    /**
     * Section within a chapter or sub-chapter (e.g., "1.1.1 Historical Context").
     * Third level of document hierarchy.
     */
    SECTION("SECTION", "Section", 3),
    
    /**
     * Sub-section within a section (e.g., "1.1.1.1 Early Developments").
     * Fourth level of document hierarchy.
     */
    SUB_SECTION("SUB_SECTION", "Sub-section", 4),
    
    /**
     * Heading that introduces content but is not a major organizational unit.
     * Can appear at any level.
     */
    HEADING("HEADING", "Heading", 5),
    
    /**
     * Paragraph of content text.
     * Contains the actual document content.
     */
    PARAGRAPH("PARAGRAPH", "Paragraph", 6),
    
    /**
     * Generic content block that doesn't fit other categories.
     * Fallback type for miscellaneous content.
     */
    CONTENT("CONTENT", "Content", 7);
    
    private final String code;
    private final String description;
    private final int hierarchyLevel;
    
    SectionType(String code, String description, int hierarchyLevel) {
        this.code = code;
        this.description = description;
        this.hierarchyLevel = hierarchyLevel;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }
    
    /**
     * Check if this section type is a structural element (not content).
     * 
     * @return true if the type is a structural element
     */
    public boolean isStructural() {
        return this == CHAPTER || this == SUB_CHAPTER || this == SECTION || this == SUB_SECTION || this == HEADING;
    }
    
    /**
     * Check if this section type contains actual content.
     * 
     * @return true if the type contains content
     */
    public boolean isContent() {
        return this == PARAGRAPH || this == CONTENT;
    }
    
    /**
     * Check if this section type can have child sections.
     * 
     * @return true if the type can have children
     */
    public boolean canHaveChildren() {
        return this == CHAPTER || this == SUB_CHAPTER || this == SECTION || this == SUB_SECTION;
    }
    
    /**
     * Get the maximum allowed hierarchy level for this section type.
     * 
     * @return the maximum hierarchy level
     */
    public int getMaxHierarchyLevel() {
        return this.hierarchyLevel;
    }
    
    /**
     * Check if this section type can be a child of the given parent type.
     * 
     * @param parentType the potential parent section type
     * @return true if this type can be a child of the parent type
     */
    public boolean canBeChildOf(SectionType parentType) {
        if (parentType == null) {
            return this == CHAPTER; // Only chapters can be root elements
        }
        
        switch (parentType) {
            case CHAPTER:
                return this == SUB_CHAPTER || this == SECTION || this == HEADING || this == PARAGRAPH || this == CONTENT;
            case SUB_CHAPTER:
                return this == SECTION || this == SUB_SECTION || this == HEADING || this == PARAGRAPH || this == CONTENT;
            case SECTION:
                return this == SUB_SECTION || this == HEADING || this == PARAGRAPH || this == CONTENT;
            case SUB_SECTION:
                return this == HEADING || this == PARAGRAPH || this == CONTENT;
            case HEADING:
                return this == PARAGRAPH || this == CONTENT;
            default:
                return false; // PARAGRAPH and CONTENT cannot have children
        }
    }
}
