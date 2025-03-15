package com.photoalbum.util;

public class SecurityUtil {
    
    public static String sanitizeInput(String input) {
        return input.replaceAll("[<>\"']", "");
    }
    
    public static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
} 