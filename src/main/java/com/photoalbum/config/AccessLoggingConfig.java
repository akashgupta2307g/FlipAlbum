package com.photoalbum.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.photoalbum.filter.AccessLoggingFilter;

@Configuration
public class AccessLoggingConfig {
    
    @Bean
    public FilterRegistrationBean<AccessLoggingFilter> loggingFilter() {
        FilterRegistrationBean<AccessLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AccessLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
} 