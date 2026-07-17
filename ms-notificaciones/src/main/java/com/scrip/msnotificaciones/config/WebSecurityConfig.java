package com.scrip.msnotificaciones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/notificaciones/**")
                        .hasAnyAuthority("SCOPE_write", "SCOPE_notifications.write")
                        .requestMatchers("/api/v1/notificaciones/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                ));

        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("roles");
        rolesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
            authorities.addAll(scopesConverter.convert(jwt));
            authorities.addAll(rolesConverter.convert(jwt));
            return authorities;
        });

        return jwtAuthenticationConverter;
    }
}
