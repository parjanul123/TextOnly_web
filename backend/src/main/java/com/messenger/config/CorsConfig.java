package com.messenger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite toate originile (web + Android)
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        
        // Permite toate metodele HTTP
        config.addAllowedMethod("*");
        
        // Permite toate header-ele
        config.addAllowedHeader("*");
        
        // Headers expuse pentru client
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
