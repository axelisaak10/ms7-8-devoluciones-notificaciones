package com.scrip.msnotificaciones.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ResendEmailClient {
    private final RestClient restClient = RestClient.create();
    private final String apiKey, from;

    public ResendEmailClient(@Value("${resend.api-key}") String apiKey, @Value("${resend.from}") String from) {
        this.apiKey = apiKey; this.from = from;
    }

    public String enviar(String destino, String asunto, String html, String idempotencyKey) {
        ResendResponse response = restClient.post().uri("https://api.resend.com/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> { headers.setBearerAuth(apiKey); headers.set("User-Agent", "ms-notificaciones/1.0"); headers.set("Idempotency-Key", idempotencyKey); })
                .body(new ResendRequest(from, List.of(destino), asunto, html))
                .retrieve().body(ResendResponse.class);
        if (response == null || response.id() == null) throw new IllegalStateException("Resend no devolvió el identificador del correo");
        return response.id();
    }

    private record ResendRequest(String from, List<String> to, String subject, String html) { }
    private record ResendResponse(@JsonProperty("id") String id) { }
}
