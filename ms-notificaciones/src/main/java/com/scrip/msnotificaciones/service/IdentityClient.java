package com.scrip.msnotificaciones.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdentityClient {
    private final ServiceTokenProvider serviceTokenProvider;
    private final RestClient restClient = RestClient.create();
    @Value("${identity.base-url}") private String identityBaseUrl;

    public UserStatus obtenerUsuario(UUID usuarioId) {
        return restClient.get().uri(identityBaseUrl + "/api/v1/internal/users/{id}/status", usuarioId)
                .headers(headers -> headers.setBearerAuth(serviceTokenProvider.getAccessToken()))
                .retrieve().body(UserStatus.class);
    }

    public record UserStatus(String email, @JsonProperty("rol") String rol, boolean activo) { }
}
