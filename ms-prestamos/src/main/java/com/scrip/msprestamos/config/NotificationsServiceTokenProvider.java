package com.scrip.msprestamos.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Component
public class NotificationsServiceTokenProvider {
    private final RestClient restClient = RestClient.create();
    private final String tokenUri, clientId, clientSecret;
    private String token;
    private Instant expiresAt = Instant.EPOCH;

    public NotificationsServiceTokenProvider(@Value("${notifications.oauth.token-uri}") String tokenUri,
                                             @Value("${notifications.oauth.client-id}") String clientId,
                                             @Value("${notifications.oauth.client-secret}") String clientSecret) {
        this.tokenUri = tokenUri; this.clientId = clientId; this.clientSecret = clientSecret;
    }

    public synchronized String getToken() {
        if (token != null && Instant.now().isBefore(expiresAt)) return token;
        TokenResponse response = restClient.post().uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .body("grant_type=client_credentials")
                .retrieve().body(TokenResponse.class);
        if (response == null || response.accessToken() == null) throw new IllegalStateException("OAuth no devolvió token para ms-prestamos");
        token = response.accessToken();
        expiresAt = Instant.now().plusSeconds(Math.max(30, response.expiresIn() - 30));
        return token;
    }
    private record TokenResponse(@JsonProperty("access_token") String accessToken, @JsonProperty("expires_in") long expiresIn) { }
}
