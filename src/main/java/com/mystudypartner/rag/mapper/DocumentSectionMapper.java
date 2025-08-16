package com.mystudypartner.rag.mapper;

import com.mystudypartner.rag.dto.DocumentSectionDto;
import com.mystudypartner.rag.model.DocumentSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for DocumentSection entity and DTO conversions.
 * Provides type-safe mapping between DocumentSection entities and DTOs.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DocumentSectionMapper {
    
    /**
     * Instance of the mapper for static access.
     */
    DocumentSectionMapper INSTANCE = Mappers.getMapper(DocumentSectionMapper.class);
    
    /**
     * Map DocumentSection entity to DocumentSectionDto.
     * 
     * @param section the document section entity
     * @return the document section DTO
     */
    @Mapping(target = "documentId", source = "section.document.id")
    @Mapping(target = "sectionTypeDescription", source = "section.sectionType.description")
    @Mapping(target = "contentPreview", source = "section.content", qualifiedByName = "generateContentPreview")
    @Mapping(target = "pageRange", expression = "java(generatePageRange(section.getPageStart(), section.getPageEnd()))")
    @Mapping(target = "childCount", expression = "java(section.getChildSections() != null ? section.getChildSections().size() : 0)")
    @Mapping(target = "isRoot", expression = "java(section.getParentSection() == null)")
    @Mapping(target = "isLeaf", expression = "java(section.getChildSections() == null || section.getChildSections().isEmpty())")
    @Mapping(target = "depth", expression = "java(calculateDepth(section))")
    @Mapping(target = "fullPath", expression = "java(generateFullPath(section))")
    @Mapping(target = "canHaveChildren", source = "section.sectionType", qualifiedByName = "canHaveChildren")
    @Mapping(target = "isContent", source = "section.sectionType", qualifiedByName = "isContent")
    DocumentSectionDto toDto(DocumentSection section);
    
    /**
     * Map DocumentSection entity to DocumentSectionDto with hierarchy.
     * 
     * @param section the document section entity
     * @param includeHierarchy whether to include hierarchy information
     * @return the document section DTO with hierarchy
     */
    @Mapping(target = "documentId", source = "section.document.id")
    @Mapping(target = "sectionTypeDescription", source = "section.sectionType.description")
    @Mapping(target = "contentPreview", source = "section.content", qualifiedByName = "generateContentPreview")
    @Mapping(target = "pageRange", expression = "java(generatePageRange(section.getPageStart(), section.getPageEnd()))")
    @Mapping(target = "childCount", expression = "java(section.getChildSections() != null ? section.getChildSections().size() : 0)")
    @Mapping(target = "isRoot", expression = "java(section.getParentSection() == null)")
    @Mapping(target = "isLeaf", expression = "java(section.getChildSections() == null || section.getChildSections().isEmpty())")
    @Mapping(target = "depth", expression = "java(calculateDepth(section))")
    @Mapping(target = "fullPath", expression = "java(generateFullPath(section))")
    @Mapping(target = "canHaveChildren", source = "section.sectionType", qualifiedByName = "canHaveChildren")
    @Mapping(target = "isContent", source = "section.sectionType", qualifiedByName = "isContent")
    @Mapping(target = "childSections", expression = "java(includeHierarchy && section.getChildSections() != null ? toDtoList(section.getChildSections()) : null)")
    @Mapping(target = "ancestors", expression = "java(includeHierarchy && section.getAncestors() != null ? toDtoList(section.getAncestors()) : null)")
    @Mapping(target = "siblings", expression = "java(includeHierarchy && section.getSiblings() != null ? toDtoList(section.getSiblings()) : null)")
    DocumentSectionDto toDtoWithHierarchy(DocumentSection section, boolean includeHierarchy);
    
    /**
     * Map DocumentSection entity to DocumentSectionDto with options.
     * 
     * @param section the document section entity
     * @param includeHierarchy whether to include hierarchy information
     * @param includeContent whether to include full content
     * @return the document section DTO with options
     */
    @Mapping(target = "documentId", source = "section.document.id")
    @Mapping(target = "sectionTypeDescription", source = "section.sectionType.description")
    @Mapping(target = "contentPreview", source = "section.content", qualifiedByName = "generateContentPreview")
    @Mapping(target = "content", expression = "java(includeContent ? section.getContent() : null)")
    @Mapping(target = "pageRange", expression = "java(generatePageRange(section.getPageStart(), section.getPageEnd()))")
    @Mapping(target = "childCount", expression = "java(section.getChildSections() != null ? section.getChildSections().size() : 0)")
    @Mapping(target = "isRoot", expression = "java(section.getParentSection() == null)")
    @Mapping(target = "isLeaf", expression = "java(section.getChildSections() == null || section.getChildSections().isEmpty())")
    @Mapping(target = "depth", expression = "java(calculateDepth(section))")
    @Mapping(target = "fullPath", expression = "java(generateFullPath(section))")
    @Mapping(target = "canHaveChildren", source = "section.sectionType", qualifiedByName = "canHaveChildren")
    @Mapping(target = "isContent", source = "section.sectionType", qualifiedByName = "isContent")
    @Mapping(target = "childSections", expression = "java(includeHierarchy && section.getChildSections() != null ? toDtoList(section.getChildSections()) : null)")
    @Mapping(target = "ancestors", expression = "java(includeHierarchy && section.getAncestors() != null ? toDtoList(section.getAncestors()) : null)")
    @Mapping(target = "siblings", expression = "java(includeHierarchy && section.getSiblings() != null ? toDtoList(section.getSiblings()) : null)")
    DocumentSectionDto toDtoWithOptions(DocumentSection section, boolean includeHierarchy, boolean includeContent);
    
    /**
     * Map DocumentSectionDto to DocumentSection entity.
     * 
     * @param sectionDto the document section DTO
     * @return the document section entity
     */
    @Mapping(target = "document", ignore = true)
    @Mapping(target = "parentSection", ignore = true)
    @Mapping(target = "childSections", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    DocumentSection toEntity(DocumentSectionDto sectionDto);
    
    /**
     * Update DocumentSection entity from DocumentSectionDto.
     * 
     * @param sectionDto the document section DTO
     * @param section the document section entity to update
     * @return the updated document section entity
     */
    @Mapping(target = "document", ignore = true)
    @Mapping(target = "parentSection", ignore = true)
    @Mapping(target = "childSections", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    DocumentSection updateEntityFromDto(DocumentSectionDto sectionDto, @MappingTarget DocumentSection section);
    
    /**
     * Map list of DocumentSection entities to list of DocumentSectionDto.
     * 
     * @param sections the list of document section entities
     * @return the list of document section DTOs
     */
    default List<DocumentSectionDto> toDtoList(List<DocumentSection> sections) {
        if (sections == null) return new java.util.ArrayList<>();
        List<DocumentSectionDto> result = new java.util.ArrayList<>();
        for (DocumentSection section : sections) {
            result.add(toDto(section));
        }
        return result;
    }
    
    /**
     * Map list of DocumentSection entities to list of DocumentSectionDto with options.
     * 
     * @param sections the list of document section entities
     * @param includeHierarchy whether to include hierarchy information
     * @param includeContent whether to include full content
     * @return the list of document section DTOs with options
     */
    default List<DocumentSectionDto> toDtoListWithOptions(List<DocumentSection> sections, boolean includeHierarchy, boolean includeContent) {
        if (sections == null) return new java.util.ArrayList<>();
        List<DocumentSectionDto> result = new java.util.ArrayList<>();
        for (DocumentSection section : sections) {
            result.add(toDtoWithOptions(section, includeHierarchy, includeContent));
        }
        return result;
    }
    
    /**
     * Map list of DocumentSectionDto to list of DocumentSection entities.
     * 
     * @param sectionDtos the list of document section DTOs
     * @return the list of document section entities
     */
    List<DocumentSection> toEntityList(List<DocumentSectionDto> sectionDtos);
    
    /**
     * Generate content preview from full content.
     * 
     * @param content the full content
     * @return content preview (first 200 characters)
     */
    @Named("generateContentPreview")
    default String generateContentPreview(String content) {
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
     * Generate page range string.
     * 
     * @param pageStart the starting page
     * @param pageEnd the ending page
     * @return formatted page range string
     */
    default String generatePageRange(Integer pageStart, Integer pageEnd) {
        if (pageStart == null) {
            return null;
        }
        
        if (pageEnd == null || pageStart.equals(pageEnd)) {
            return pageStart.toString();
        }
        
        return pageStart + "-" + pageEnd;
    }
    
    /**
     * Calculate depth of section in hierarchy.
     * 
     * @param section the document section
     * @return the depth level
     */
    default Integer calculateDepth(DocumentSection section) {
        if (section == null) {
            return 0;
        }
        
        int depth = 0;
        DocumentSection current = section.getParentSection();
        while (current != null) {
            depth++;
            current = current.getParentSection();
        }
        
        return depth;
    }
    
    /**
     * Generate full path from root to section.
     * 
     * @param section the document section
     * @return the full path string
     */
    default String generateFullPath(DocumentSection section) {
        if (section == null) {
            return null;
        }
        
        StringBuilder path = new StringBuilder();
        DocumentSection current = section;
        
        while (current != null) {
            if (path.length() > 0) {
                path.insert(0, " > ");
            }
            path.insert(0, current.getTitle() != null ? current.getTitle() : current.getSectionType().getCode());
            current = current.getParentSection();
        }
        
        return path.toString();
    }
    
    /**
     * Check if section type can have children.
     * 
     * @param sectionType the section type
     * @return true if it can have children
     */
    @Named("canHaveChildren")
    default Boolean canHaveChildren(com.mystudypartner.rag.model.SectionType sectionType) {
        return sectionType != null && sectionType.canHaveChildren();
    }
    
    /**
     * Check if section type contains content.
     * 
     * @param sectionType the section type
     * @return true if it contains content
     */
    @Named("isContent")
    default Boolean isContent(com.mystudypartner.rag.model.SectionType sectionType) {
        return sectionType != null && sectionType.isContent();
    }
}
