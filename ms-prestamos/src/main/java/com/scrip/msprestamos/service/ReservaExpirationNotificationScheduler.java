package com.scrip.msprestamos.service;

import com.scrip.msprestamos.client.NotificacionClient;
import com.scrip.msprestamos.entity.EstadoReserva;
import com.scrip.msprestamos.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaExpirationNotificationScheduler {
    private final ReservaRepository reservaRepository;
    private final NotificacionClient notificacionClient;

    @Scheduled(cron = "${notifications.reservation-expiration.cron:0 0 9 * * *}")
    public void avisarReservasProximasAExpirar() {
        OffsetDateTime ahora = OffsetDateTime.now();
        reservaRepository.findByEstadoAndFechaExpiracionGreaterThanEqualAndFechaExpiracionLessThan(
                        EstadoReserva.ACTIVA, ahora, ahora.plusHours(24))
                .forEach(reserva -> {
                    try {
                        notificacionClient.enviarNotificacion(Map.of(
                                "usuarioId", reserva.getUsuarioId(),
                                "tipo", "RESERVA_POR_EXPIRAR",
                                "referenciaId", reserva.getId(),
                                "mensaje", "Tu reserva está próxima a expirar. Confirma tu préstamo antes de la fecha límite."
                        ));
                    } catch (Exception exception) {
                        log.warn("No se pudo registrar aviso de expiración para reserva {}: {}", reserva.getId(), exception.getMessage());
                    }
                });
    }
}
