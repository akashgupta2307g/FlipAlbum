package com.photoalbum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.photoalbum.model.Album;
import com.photoalbum.model.User;
import com.photoalbum.service.AlbumService;
import com.photoalbum.service.UserService;

@Controller
public class DashboardController {

    @Autowired
    private AlbumService albumService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Get user by email instead of username
        User user = userService.findByEmail(authentication.getName());
        List<Album> recentAlbums = albumService.getRecentAlbumsByUser(user, 5);
        long totalAlbums = albumService.countAlbumsByUser(user);
        long totalPhotos = albumService.countPhotosByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("recentAlbums", recentAlbums);
        model.addAttribute("totalAlbums", totalAlbums);
        model.addAttribute("totalPhotos", totalPhotos);
        
        return "dashboard";
    }
} 