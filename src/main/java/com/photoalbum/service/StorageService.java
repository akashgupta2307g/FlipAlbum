package com.photoalbum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    public String storeFile(MultipartFile file, String fileName) throws Exception {
        Path uploadLocation = Paths.get(uploadPath);
        
        if (!Files.exists(uploadLocation)) {
            Files.createDirectories(uploadLocation);
        }
        
        Path targetLocation = uploadLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }
} 