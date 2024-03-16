package com.karthick.youtubeclone.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final String SHORT_VIDEO_ENDPOINT = "/api/youtube/video/short-video";

    private final String SUGGEST_VIDEO_ENDPOINT = "/api/youtube/video/suggestion-videos";

    private final String GET_VIDEO_ENDPOINT = "/api/youtube/video/watch/{videoId}";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                auth -> auth.requestMatchers(SHORT_VIDEO_ENDPOINT,
                                SUGGEST_VIDEO_ENDPOINT,
                                GET_VIDEO_ENDPOINT).permitAll()

                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
                return http.build();
    }



}
