package com.photoalbum.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.photoalbum.model.Album;
import com.photoalbum.model.Photo;
import com.photoalbum.model.User;
import com.photoalbum.security.CustomUserDetails;
import com.photoalbum.service.AlbumService;

@Controller
@RequestMapping("/album")
public class AlbumController {
    private static final Logger log = LoggerFactory.getLogger(AlbumController.class);
    
    @Autowired
    private AlbumService albumService;
    
    @GetMapping("/create")
    public String showCreateForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return "create-album";
    }
    
    @PostMapping("/create")
    public String createAlbum(@RequestParam String title,
                            @RequestParam String description,
                            @RequestParam("photos") List<MultipartFile> photos,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        try {
            if (userDetails == null) {
                return "redirect:/login";
            }
            
            log.info("Creating album for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            Album album = albumService.createAlbum(title, description, photos, user);
            return "redirect:/album/view/" + album.getId();
        } catch (Exception e) {
            log.error("Error creating album", e);
            model.addAttribute("error", e.getMessage());
            return "create-album";
        }
    }
    
    @GetMapping("/view/{id}")
    public String viewAlbum(@PathVariable Long id, Model model) {
        Album album = albumService.getAlbum(id);
        log.debug("Album found: {}", album.getTitle());
        log.debug("Photos count: {}", album.getPhotos().size());
        for (Photo photo : album.getPhotos()) {
            log.debug("Photo path: {}", photo.getFileName());
        }
        model.addAttribute("album", album);
        return "view-album";
    }

    @GetMapping("/list")
    public String listAlbums(Model model, @AuthenticationPrincipal User user) {
        List<Album> albums = albumService.getAlbumsByUser(user);
        model.addAttribute("albums", albums);
        return "album-list";
    }
} 