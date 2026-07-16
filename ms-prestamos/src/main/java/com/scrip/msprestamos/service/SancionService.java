package com.scrip.msprestamos.service;

import com.scrip.msprestamos.client.NotificacionClient;
import com.scrip.msprestamos.dto.SancionRequest;
import com.scrip.msprestamos.entity.EstadoSancion;
import com.scrip.msprestamos.entity.Sancion;
import com.scrip.msprestamos.repository.SancionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SancionService {

    private final SancionRepository sancionRepository;
    private final NotificacionClient notificacionClient;

    @Transactional
    public Sancion registrarSancion(SancionRequest request) {
        // 1. Crear la Sanción (SA01)
        Sancion sancion = Sancion.builder()
                .usuarioId(request.getUsuarioId())
                .prestamoId(request.getPrestamoId())
                .monto(request.getMonto())
                .estado(EstadoSancion.PENDIENTE)
                .fechaGeneracion(OffsetDateTime.now())
                .build();

        sancion = sancionRepository.save(sancion);

        // 2. Notificar al usuario (NO03)
        String mensajeNotificacion = String.format(
                "Se ha aplicado una sanción a tu cuenta por un monto de $%s debido a la entrega tardía de un préstamo.",
                request.getMonto().toString()
        );

        Map<String, Object> notificationRequest = Map.of(
                "usuarioId", sancion.getUsuarioId().toString(),
                "tipo", "SANCION_GENERADA",
                "referenciaId", sancion.getId().toString(),
                "mensaje", mensajeNotificacion
        );

        try {
            notificacionClient.enviarNotificacion(notificationRequest);
        } catch (Exception e) {
            System.err.println("No se pudo enviar la notificación de sanción generada: " + e.getMessage());
        }

        return sancion;
    }

    public List<Sancion> obtenerSancionesActivas(UUID usuarioId) {
        // SA02
        return sancionRepository.findByUsuarioIdAndEstado(usuarioId, EstadoSancion.PENDIENTE);
    }

    @Transactional
    public Sancion pagarSancion(UUID id) {
        // SA03
        Sancion sancion = sancionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La sanción especificada no existe."));

        if (sancion.getEstado() == EstadoSancion.PAGADA) {
            throw new IllegalArgumentException("La sanción especificada ya se encuentra pagada.");
        }

        sancion.setEstado(EstadoSancion.PAGADA);
        sancion.setFechaPago(OffsetDateTime.now());

        return sancionRepository.save(sancion);
    }

    public List<Sancion> obtenerTodasSanciones() {
        return sancionRepository.findAll();
    }
}
