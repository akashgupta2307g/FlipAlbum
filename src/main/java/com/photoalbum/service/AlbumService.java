package com.photoalbum.service;

import com.photoalbum.model.Album;
import com.photoalbum.model.Photo;
import com.photoalbum.model.User;
import com.photoalbum.repository.AlbumRepository;
import com.photoalbum.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.photoalbum.service.EncryptionService;

@Service
public class AlbumService {
    private static final Logger log = LoggerFactory.getLogger(AlbumService.class);
    
    @Autowired
    private AlbumRepository albumRepository;
    
    @Autowired
    private PhotoRepository photoRepository;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @PostConstruct
    public void init() {
        try {
            Path uploadDir = Paths.get(uploadPath);
            Files.createDirectories(uploadDir);
            log.info("Upload directory created at: {}", uploadDir.toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not create upload directory: {}", e.getMessage());
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public Album createAlbum(String title, String description, List<MultipartFile> photos, User user) {
        try {
            Album album = new Album();
            album.setTitle(title);
            album.setDescription(description);
            album.setUser(user);
            album.setCreatedAt(LocalDateTime.now());
            
            log.debug("Creating album: {}", title);
            
            for (MultipartFile file : photos) {
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path path = Paths.get(uploadPath, fileName);
                    
                    log.debug("Saving file: {} to path: {}", fileName, path);
                    
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    
                    Photo photo = new Photo();
                    photo.setFileName(fileName);
                    photo.setFilePath(path.toString());
                    album.addPhoto(photo);
                    
                    log.debug("Added photo to album: {}", fileName);
                }
            }
            
            Album savedAlbum = albumRepository.save(album);
            log.info("Album saved successfully with ID: {}", savedAlbum.getId());
            return savedAlbum;
            
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage());
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public Album getAlbum(Long id) {
        return albumRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Album not found"));
    }

    public List<Album> getAlbumsByUser(User user) {
        return albumRepository.findByUser(user);
    }

    public List<Album> getRecentAlbumsByUser(User user, int limit) {
        return albumRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(0, limit));
    }
    
    public long countAlbumsByUser(User user) {
        return albumRepository.countByUser(user);
    }
    
    public long countPhotosByUser(User user) {
        return albumRepository.findByUser(user).stream()
                .mapToLong(album -> album.getPhotos().size())
                .sum();
    }

    public void savePhoto(MultipartFile file, Album album) throws Exception {
        // Encrypt file data before saving
        byte[] encryptedData = encryptionService.encryptFile(file.getBytes());
        
        // Generate secure filename
        String secureFileName = generateSecureFileName(file.getOriginalFilename());
        
        // Save encrypted data
        Path path = Paths.get(uploadPath + secureFileName);
        Files.write(path, encryptedData);
        
        // Save photo metadata
        Photo photo = new Photo();
        photo.setFileName(secureFileName);
        photo.setOriginalName(file.getOriginalFilename());
        photo.setAlbum(album);
        photoRepository.save(photo);
    }

    private String generateSecureFileName(String originalFilename) {
        return UUID.randomUUID().toString() + getFileExtension(originalFilename);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return filename.substring(dotIndex);
    }
} 