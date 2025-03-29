package com.photoalbum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.photoalbum.model.User;
import com.photoalbum.repository.UserRepository;
import com.photoalbum.security.CustomUserDetails;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Attempting to load user by email: {}", email);
        
        try {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
            
            log.info("User found: {}", user.getEmail());
            log.info("Stored password hash: {}", user.getPassword());
            
            return new CustomUserDetails(user);
        } catch (Exception e) {
            log.error("Error loading user: ", e);
            throw new UsernameNotFoundException("Error loading user", e);
        }
    }
} 