package com.photoalbum.controller;

import java.util.List;
import java.time.LocalDateTime;
import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.photoalbum.model.Album;
import com.photoalbum.model.Photo;
import com.photoalbum.model.User;
import com.photoalbum.security.CustomUserDetails;
import com.photoalbum.service.AlbumService;
import com.photoalbum.service.UserService;

@Controller
@RequestMapping("/album")
public class AlbumController {
    private static final Logger log = LoggerFactory.getLogger(AlbumController.class);
    
    @Autowired
    private AlbumService albumService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("album", new Album());
        return "create-album";
    }
    
    @PostMapping("/create")
    public String createAlbum(@RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("photos") MultipartFile[] files,
                              Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName());
            albumService.createAlbum(title, description, Arrays.asList(files), user);
            return "redirect:/dashboard";
        } catch (Exception e) {
            // Log the error with more details
            System.err.println("Error creating album: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/album/create?error=true";
        }
    }
    
    @GetMapping("/{id}")
    public String viewAlbum(@PathVariable Long id, Model model) {
        Album album = albumService.findById(id);
        model.addAttribute("album", album);
        return "view-album";
    }

    @GetMapping("/view/{id}")
    public String viewAlbumAlternate(@PathVariable Long id, Model model) {
        return viewAlbum(id, model);
    }

    @GetMapping("/list")
    public String listAlbums(Model model, @AuthenticationPrincipal User user) {
        List<Album> albums = albumService.getAlbumsByUser(user);
        model.addAttribute("albums", albums);
        return "album-list";
    }

    @GetMapping("/edit/{id}")
    public String editAlbum(@PathVariable Long id, Model model) {
        Album album = albumService.findById(id);
        model.addAttribute("album", album);
        return "edit-album";  // This will render edit-album.html
    }

    @PostMapping("/edit/{id}")
    public String updateAlbum(@PathVariable Long id, 
                            @ModelAttribute Album album,
                            @RequestParam(required = false) List<MultipartFile> photos) {
        Album existingAlbum = albumService.findById(id);
        existingAlbum.setTitle(album.getTitle());
        existingAlbum.setDescription(album.getDescription());
        
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    albumService.savePhoto(photo, existingAlbum);
                }
            }
        }
        
        albumService.save(existingAlbum);
        return "redirect:/dashboard";
    }
} 