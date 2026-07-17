package com.scrip.msprestamos.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignClientConfig {

    private final NotificationsServiceTokenProvider serviceTokenProvider;

    public FeignClientConfig(NotificationsServiceTokenProvider serviceTokenProvider) {
        this.serviceTokenProvider = serviceTokenProvider;
    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
                    template.header("Authorization", "Bearer " + jwtAuthToken.getToken().getTokenValue());
                } else {
                    template.header("Authorization", "Bearer " + serviceTokenProvider.getToken());
                }
            }
        };
    }
}
