package com.mystudypartner.rag.mapper;

import com.mystudypartner.rag.dto.DocumentDto;
import com.mystudypartner.rag.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for Document entity and DTO conversions.
 * Provides type-safe mapping between Document entities and DTOs.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Mapper(componentModel = "spring", uses = {DocumentSectionMapper.class})
public interface DocumentMapper {
    
    /**
     * Instance of the mapper for static access.
     */
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);
    
    /**
     * Map Document entity to DocumentDto.
     * 
     * @param document the document entity
     * @return the document DTO
     */
    @Mapping(target = "fileSizeFormatted", source = "document.fileSize", qualifiedByName = "formatFileSize")
    @Mapping(target = "processingStatusDescription", source = "document.processingStatus.description")
    @Mapping(target = "processingDurationFormatted", source = "document.processingDurationMs", qualifiedByName = "formatDuration")
    @Mapping(target = "isProcessingComplete", source = "document.processingStatus", qualifiedByName = "isProcessingComplete")
    @Mapping(target = "isProcessingSuccessful", source = "document.processingStatus", qualifiedByName = "isProcessingSuccessful")
    DocumentDto toDto(Document document);
    
    /**
     * Map DocumentDto to Document entity.
     * 
     * @param documentDto the document DTO
     * @return the document entity
     */
    @Mapping(target = "sections", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Document toEntity(DocumentDto documentDto);
    
    /**
     * Update Document entity from DocumentDto.
     * 
     * @param documentDto the document DTO
     * @param document the document entity to update
     * @return the updated document entity
     */
    @Mapping(target = "sections", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Document updateEntityFromDto(DocumentDto documentDto, @MappingTarget Document document);

    default DocumentDto toDtoWithSections(Document document, boolean includeSections) {
        if (document == null) {
            return null;
        }
        DocumentDto dto = toDto(document);
        if (includeSections) {
            dto.setSections(DocumentSectionMapper.INSTANCE.toDtoList(document.getSections()));
        }
        return dto;
    }
    
    /**
     * Map list of DocumentDto to list of Document entities.
     * 
     * @param documentDtos the list of document DTOs
     * @return the list of document entities
     */
    List<Document> toEntityList(List<DocumentDto> documentDtos);
    
    /**
     * Format file size in bytes to human-readable format.
     * 
     * @param bytes the file size in bytes
     * @return formatted file size string
     */
    @Named("formatFileSize")
    default String formatFileSize(Long bytes) {
        if (bytes == null) {
            return null;
        }
        
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }
    
    /**
     * Format duration in milliseconds to human-readable format.
     * 
     * @param durationMs the duration in milliseconds
     * @return formatted duration string
     */
    @Named("formatDuration")
    default String formatDuration(Long durationMs) {
        if (durationMs == null) {
            return null;
        }
        
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    /**
     * Check if document processing is complete.
     * 
     * @param processingStatus the processing status
     * @return true if processing is complete
     */
    @Named("isProcessingComplete")
    default Boolean isProcessingComplete(com.mystudypartner.rag.model.ProcessingStatus processingStatus) {
        return processingStatus != null && processingStatus.isFinal();
    }
    
    /**
     * Check if document processing was successful.
     * 
     * @param processingStatus the processing status
     * @return true if processing was successful
     */
    @Named("isProcessingSuccessful")
    default Boolean isProcessingSuccessful(com.mystudypartner.rag.model.ProcessingStatus processingStatus) {
        return processingStatus != null && processingStatus.isSuccessful();
    }
}
