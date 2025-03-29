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
import org.springframework.transaction.annotation.Transactional;

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
                    photo.setFilePath(fileName);
                    photo.setOriginalFileName(file.getOriginalFilename());
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

    public void savePhoto(MultipartFile file, Album album) {
        try {
            // Create uploads directory if it doesn't exist
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate unique filename
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // Save file
            Path path = Paths.get(uploadPath + File.separator + uniqueFileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Save to database with correct path
            Photo photo = new Photo();
            photo.setFileName(uniqueFileName);  // Just the filename
            photo.setFilePath(uniqueFileName);  // Just the filename
            photo.setFileType(file.getContentType());
            photo.setSize(file.getSize());
            photo.setOriginalFileName(file.getOriginalFilename());
            photo.setAlbum(album);
            photoRepository.save(photo);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
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

    @Transactional
    public Album createAlbumWithPhotos(Album album, MultipartFile[] files) throws IOException {
        Album savedAlbum = albumRepository.save(album);

        Path uploadPath = Paths.get(this.uploadPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filepath = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

                Photo photo = new Photo();
                photo.setAlbum(savedAlbum);
                photo.setFilePath(filename);
                photo.setOriginalFileName(file.getOriginalFilename());
                photo.setFileType(file.getContentType());
                photo.setSize(file.getSize());
                photo.setUploadDate(LocalDateTime.now());

                photoRepository.save(photo);
            }
        }

        return savedAlbum;
    }

    public Album findById(Long id) {
        return albumRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Album not found with id: " + id));
    }

    public Album save(Album album) {
        return albumRepository.save(album);
    }
} 