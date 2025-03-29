package com.photoalbum.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "home";  // home.html पर redirect करेगा
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";  // login.html पर redirect करेगा
    }
    
    @GetMapping("/home")
    public String dashboard() {
        // logged in users के लिए main page
        return "redirect:/album/list";
    }
} 