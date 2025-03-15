package com.photoalbum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        // अगर user logged in नहीं है तो login page पर भेजें
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";  // login.html template को render करेगा
    }
    
    @GetMapping("/home")
    public String dashboard() {
        // logged in users के लिए main page
        return "redirect:/album/list";
    }
} 