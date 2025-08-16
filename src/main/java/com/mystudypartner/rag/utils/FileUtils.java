package com.mystudypartner.rag.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

/**
 * Utility class for file operations.
 * Provides common file handling functionality for the RAG application.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
public final class FileUtils {
    
    private FileUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Generate a unique filename with original extension.
     * 
     * @param originalFilename the original filename
     * @return unique filename with extension
     */
    public static String generateUniqueFilename(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return UUID.randomUUID().toString();
        }
        
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String uniqueName = UUID.randomUUID().toString();
        
        return extension != null ? uniqueName + "." + extension : uniqueName;
    }
    
    /**
     * Create directory if it doesn't exist.
     * 
     * @param directoryPath the directory path
     * @throws IOException if directory creation fails
     */
    public static void createDirectoryIfNotExists(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.debug("Created directory: {}", directoryPath);
        }
    }
    
    /**
     * Delete file if it exists.
     * 
     * @param filePath the file path
     * @return true if file was deleted, false if it didn't exist
     * @throws IOException if deletion fails
     */
    public static boolean deleteFileIfExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        boolean deleted = Files.deleteIfExists(path);
        if (deleted) {
            log.debug("Deleted file: {}", filePath);
        }
        return deleted;
    }
    
    /**
     * Check if file exists.
     * 
     * @param filePath the file path
     * @return true if file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Get file size in bytes.
     * 
     * @param filePath the file path
     * @return file size in bytes, or -1 if file doesn't exist
     */
    public static long getFileSize(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.exists(path) ? Files.size(path) : -1;
        } catch (IOException e) {
            log.warn("Failed to get file size for: {}", filePath, e);
            return -1;
        }
    }
    
    /**
     * Format file size in bytes to human-readable format.
     * 
     * @param bytes the file size in bytes
     * @return formatted file size string
     */
    public static String formatFileSize(Long bytes) {
        if (bytes == null || bytes < 0) {
            return "Unknown";
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
     * Calculate MD5 hash of a file.
     * 
     * @param filePath the file path
     * @return MD5 hash as hex string, or null if calculation fails
     */
    public static String calculateFileHash(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return null;
            }
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(Files.readAllBytes(path));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException | IOException e) {
            log.warn("Failed to calculate file hash for: {}", filePath, e);
            return null;
        }
    }
    
    /**
     * Get file extension from filename.
     * 
     * @param filename the filename
     * @return file extension (without dot), or null if no extension
     */
    public static String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return null;
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT);
        }
        
        return null;
    }
    
    /**
     * Get filename without extension.
     * 
     * @param filename the filename
     * @return filename without extension
     */
    public static String getFilenameWithoutExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return filename;
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(0, lastDotIndex);
        }
        
        return filename;
    }
    
    /**
     * Validate file extension against allowed extensions.
     * 
     * @param filename the filename
     * @param allowedExtensions array of allowed extensions (without dots)
     * @return true if file extension is allowed
     */
    public static boolean isValidFileExtension(String filename, String... allowedExtensions) {
        if (!StringUtils.hasText(filename) || allowedExtensions == null || allowedExtensions.length == 0) {
            return false;
        }
        
        String extension = getFileExtension(filename);
        if (extension == null) {
            return false;
        }
        
        for (String allowedExtension : allowedExtensions) {
            if (extension.equalsIgnoreCase(allowedExtension)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Sanitize filename for safe storage.
     * 
     * @param filename the original filename
     * @return sanitized filename
     */
    public static String sanitizeFilename(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "unnamed";
        }
        
        // Remove or replace problematic characters
        String sanitized = filename
                .replaceAll("[<>:\"/\\\\|?*]", "_") // Replace invalid characters
                .replaceAll("\\s+", "_") // Replace spaces with underscores
                .replaceAll("_+", "_") // Replace multiple underscores with single
                .trim();
        
        // Ensure filename is not too long
        if (sanitized.length() > 255) {
            String extension = getFileExtension(sanitized);
            String nameWithoutExt = getFilenameWithoutExtension(sanitized);
            
            int maxNameLength = 255 - (extension != null ? extension.length() + 1 : 0);
            sanitized = nameWithoutExt.substring(0, Math.min(nameWithoutExt.length(), maxNameLength));
            
            if (extension != null) {
                sanitized += "." + extension;
            }
        }
        
        return sanitized.isEmpty() ? "unnamed" : sanitized;
    }
}
