package com.scrip.msdevoluciones.service;

import com.scrip.msdevoluciones.client.NotificacionClient;
import com.scrip.msdevoluciones.client.PrestamoClient;
import com.scrip.msdevoluciones.dto.DevolucionRequest;
import com.scrip.msdevoluciones.dto.PrestamoDto;
import com.scrip.msdevoluciones.entity.Devolucion;
import com.scrip.msdevoluciones.repsitory.DevolucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DevolucionService {

    private final DevolucionRepository devolucionRepository;
    private final PrestamoClient prestamoClient;
    private final NotificacionClient notificacionClient;

    @Transactional
    public Devolucion registrarDevolucion(DevolucionRequest request) {
        // 1. Validar si la devolución ya fue registrada
        if (devolucionRepository.existsByPrestamo(request.getPrestamoId())) {
            throw new IllegalArgumentException("La devolución para este préstamo ya ha sido registrada anteriormente.");
        }

        // 2. Consultar préstamo mediante Feign Client
        PrestamoDto prestamo;
        try {
            prestamo = prestamoClient.obtenerPrestamoPorId(request.getPrestamoId());
        } catch (Exception e) {
            throw new IllegalArgumentException("El préstamo especificado no existe o no se pudo consultar.");
        }

        if (prestamo == null) {
            throw new IllegalArgumentException("El préstamo especificado no existe.");
        }

        if ("DEVUELTO".equalsIgnoreCase(prestamo.getEstado())) {
            throw new IllegalArgumentException("El préstamo especificado ya figura como DEVUELTO.");
        }

        // 3. Evaluar si la entrega es tardía
        OffsetDateTime fechaDevolucion = request.getFechaDevolucion() != null 
                ? request.getFechaDevolucion() 
                : OffsetDateTime.now();

        boolean tardia = false;
        int diasRetraso = 0;

        long dias = ChronoUnit.DAYS.between(
                prestamo.getFechaLimite().toLocalDate(), 
                fechaDevolucion.toLocalDate()
        );

        if (dias > 0) {
            tardia = true;
            diasRetraso = (int) dias;
        }

        // 4. Crear y persistir la Devolución
        Devolucion devolucion = Devolucion.builder()
                .prestamo(request.getPrestamoId())
                .fechaDevolucion(fechaDevolucion)
                .tardia(tardia)
                .diasRetraso(diasRetraso)
                .build();

        devolucion = devolucionRepository.save(devolucion);

        // 5. Marcar préstamo como devuelto
        prestamoClient.marcarPrestamoComoDevuelto(prestamo.getId());

        // 6. Si es tardía, generar sanción
        if (tardia) {
            double montoMulta = diasRetraso * 15.00;
            Map<String, Object> sanctionRequest = Map.of(
                    "usuarioId", prestamo.getUsuarioId().toString(),
                    "prestamoId", prestamo.getId().toString(),
                    "monto", montoMulta
            );
            prestamoClient.crearSancion(sanctionRequest);
        }

        // 7. Notificar devolución registrada
        String mensajeNotificacion = String.format(
                "Se ha registrado la devolución de tu préstamo. %s",
                tardia ? "Se aplicó una multa de $" + (diasRetraso * 15.00) + " por entrega tardía de " + diasRetraso + " días." 
                       : "Entrega realizada a tiempo."
        );

        Map<String, Object> notificationRequest = Map.of(
                "usuarioId", prestamo.getUsuarioId().toString(),
                "tipo", "DEVOLUCION_REGISTRADA",
                "referenciaId", prestamo.getId().toString(),
                "mensaje", mensajeNotificacion
        );
        notificacionClient.enviarNotificacion(notificationRequest);

        return devolucion;
    }
}
