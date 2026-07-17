package com.scrip.msnotificaciones.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Component
public class ServiceTokenProvider {
    private final RestClient restClient = RestClient.create();
    private final String tokenUri, clientId, clientSecret;
    private String accessToken;
    private Instant expiresAt = Instant.EPOCH;

    public ServiceTokenProvider(@Value("${identity.token-uri}") String tokenUri,
                                @Value("${identity.client-id}") String clientId,
                                @Value("${identity.client-secret}") String clientSecret) {
        this.tokenUri = tokenUri; this.clientId = clientId; this.clientSecret = clientSecret;
    }

    public synchronized String getAccessToken() {
        if (accessToken != null && Instant.now().isBefore(expiresAt)) return accessToken;
        TokenResponse response = restClient.post().uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .body("grant_type=client_credentials")
                .retrieve().body(TokenResponse.class);
        if (response == null || response.accessToken() == null) throw new IllegalStateException("OAuth no devolvió un access token");
        accessToken = response.accessToken();
        expiresAt = Instant.now().plusSeconds(Math.max(30, response.expiresIn() - 30));
        return accessToken;
    }

    private record TokenResponse(@JsonProperty("access_token") String accessToken,
                                 @JsonProperty("expires_in") long expiresIn) { }
}
