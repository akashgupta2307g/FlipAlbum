package com.photoalbum.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccessLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(AccessLoggingFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response,
                                  FilterChain filterChain) 
            throws ServletException, IOException {
        
        String username = request.getUserPrincipal() != null ? 
                         request.getUserPrincipal().getName() : "anonymous";
        
        String timestamp = LocalDateTime.now().format(formatter);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        // Log request
        log.info("[ACCESS] {} | {} | {} {} | IP: {} | User-Agent: {} | User: {}", 
                timestamp, 
                response.getStatus(),
                method, 
                uri, 
                ip,
                userAgent,
                username);
        
        // Continue with the request
        filterChain.doFilter(request, response);
    }
} 