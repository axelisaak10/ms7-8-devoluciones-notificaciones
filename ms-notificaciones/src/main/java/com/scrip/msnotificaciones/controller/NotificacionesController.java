package com.scrip.msnotificaciones.controller;

import com.scrip.msnotificaciones.dto.NotificacionRequest;
import com.scrip.msnotificaciones.entity.Notificacion;
import com.scrip.msnotificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionesController {

    private final NotificacionService notificacionService;

    @PostMapping
    public ResponseEntity<?> enviarNotificacion(@Valid @RequestBody NotificacionRequest request) {
        try {
            Notificacion notificacion = notificacionService.enviarNotificacion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(notificacion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al emitir la notificación: " + e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<java.util.List<com.scrip.msnotificaciones.entity.Notificacion>> obtenerNotificacionesUsuario(@PathVariable("usuarioId") java.util.UUID usuarioId) {
        return ResponseEntity.ok(notificacionService.obtenerNotificacionesUsuario(usuarioId));
    }
}
