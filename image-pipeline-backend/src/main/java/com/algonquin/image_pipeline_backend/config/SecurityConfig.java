package com.algonquin.image_pipeline_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${app.auth-enabled:false}")
    private boolean authEnabled;

    @Value("${azure.storage.jwt-issuer-uri:}")
    private String issuerUri;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())      // ✅ IMPORTANT for browser calls
                .csrf(csrf -> csrf.disable());

        // ✅ Always allow CORS preflight (OPTIONS)
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        );

        // No auth mode (what you’re using now)
        if (!authEnabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        // Auth mode (Entra later)
        if (issuerUri == null || issuerUri.isBlank()) {
            throw new IllegalStateException("JWT_ISSUER_URI is required when AUTH_ENABLED=true.");
        }

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
        );

        http.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        return http.build();
    }
}