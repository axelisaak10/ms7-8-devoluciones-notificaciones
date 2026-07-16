package com.scrip.msnotificaciones.service;

import com.scrip.msnotificaciones.dto.NotificacionRequest;
import com.scrip.msnotificaciones.entity.Notificacion;
import com.scrip.msnotificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Transactional
    public Notificacion enviarNotificacion(NotificacionRequest request) {
        // 1. Persistir la notificación en base de datos
        Notificacion notificacion = Notificacion.builder()
                .usuarioId(request.getUsuarioId())
                .tipo(request.getTipo())
                .referenciaId(request.getReferenciaId())
                .mensaje(request.getMensaje())
                .fechaEnvio(OffsetDateTime.now())
                .build();

        notificacion = notificacionRepository.save(notificacion);

        // 2. Imprimir en consola de forma formateada según el tipo (NO01 - NO04)
        String prefix = switch (request.getTipo()) {
            case PRESTAMO_AUTORIZADO -> "PRÉSTAMO AUTORIZADO";
            case DEVOLUCION_REGISTRADA -> "DEVOLUCIÓN REGISTRADA";
            case SANCION_GENERADA -> "SANCIÓN GENERADA";
            case RESERVA_POR_EXPIRAR -> "RESERVA POR EXPIRAR";
        };

        System.out.println(String.format(
                "\n================================================================================\n" +
                "[NOTIFICACIÓN - %s]\n" +
                "Destinatario (Usuario ID): %s\n" +
                "Referencia ID            : %s\n" +
                "Mensaje                  : %s\n" +
                "Fecha de Envío           : %s\n" +
                "================================================================================",
                prefix,
                request.getUsuarioId(),
                request.getReferenciaId() != null ? request.getReferenciaId() : "N/A",
                request.getMensaje(),
                notificacion.getFechaEnvio().toString()
        ));

        return notificacion;
    }

    public java.util.List<com.scrip.msnotificaciones.entity.Notificacion> obtenerNotificacionesUsuario(java.util.UUID usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaEnvioDesc(usuarioId);
    }
}
