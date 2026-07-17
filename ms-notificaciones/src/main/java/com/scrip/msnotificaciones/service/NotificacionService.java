package com.scrip.msnotificaciones.service;

import com.scrip.msnotificaciones.dto.NotificacionRequest;
import com.scrip.msnotificaciones.entity.Notificacion;
import com.scrip.msnotificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final EntregaCorreoService entregaCorreoService;

    @Transactional
    public Notificacion enviarNotificacion(NotificacionRequest request) {
        String idempotencyKey = generarClaveIdempotencia(request);
        var existente = notificacionRepository.findByClaveIdempotencia(idempotencyKey);
        if (existente.isPresent()) {
            return existente.get();
        }

        Notificacion notificacion = notificacionRepository.save(Notificacion.builder()
                .usuarioId(request.getUsuarioId())
                .tipo(request.getTipo())
                .referenciaId(request.getReferenciaId())
                .mensaje(request.getMensaje())
                .claveIdempotencia(idempotencyKey)
                .fechaEnvio(OffsetDateTime.now())
                .build());

        UUID id = notificacion.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { entregaCorreoService.entregar(id); }
        });
        return notificacion;
    }

    public java.util.List<Notificacion> obtenerNotificacionesUsuario(UUID usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaEnvioDesc(usuarioId);
    }

    private String generarClaveIdempotencia(NotificacionRequest request) {
        String fuente = request.getTipo() + ":" + request.getUsuarioId() + ":" +
                (request.getReferenciaId() == null ? request.getMensaje() : request.getReferenciaId());
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(fuente.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder("notificacion-");
            for (byte b : hash) resultado.append(String.format("%02x", b));
            return resultado.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("No fue posible generar la clave de idempotencia", exception);
        }
    }
}
