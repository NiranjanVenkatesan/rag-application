package com.mystudypartner.rag.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * JPA Converter for converting between Map<String, Object> and JSONB string.
 * This converter handles the conversion between Java Map objects and PostgreSQL JSONB format.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Converter
@Slf4j
public class JsonMetadataConverter implements AttributeConverter<Map<String, Object>, String> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "{}";
        }
        
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            log.error("Error converting metadata to JSON string: {}", e.getMessage(), e);
            return "{}";
        }
    }
    
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData) || "{}".equals(dbData.trim())) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON string to metadata: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
