package com.scrip.msprestamos.service;

import com.scrip.msprestamos.client.NotificacionClient;
import com.scrip.msprestamos.dto.PrestamoRequest;
import com.scrip.msprestamos.entity.EstadoPrestamo;
import com.scrip.msprestamos.entity.EstadoSancion;
import com.scrip.msprestamos.entity.Libro;
import com.scrip.msprestamos.entity.Prestamo;
import com.scrip.msprestamos.repository.LibroRepository;
import com.scrip.msprestamos.repository.PrestamoRepository;
import com.scrip.msprestamos.repository.SancionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final SancionRepository sancionRepository;
    private final LibroRepository libroRepository;
    private final NotificacionClient notificacionClient;

    @Transactional
    public Prestamo registrarPrestamo(PrestamoRequest request) {
        // 1. Verificar si el usuario tiene sanciones pendientes (SA02)
        boolean tieneSanciones = !sancionRepository.findByUsuarioIdAndEstado(request.getUsuarioId(), EstadoSancion.PENDIENTE).isEmpty();
        if (tieneSanciones) {
            throw new IllegalArgumentException("El usuario tiene sanciones activas pendientes de pago y no puede realizar nuevos préstamos.");
        }

        // 2. Verificar existencia del libro y stock
        Libro libro = libroRepository.findById(request.getLibroId())
                .orElseThrow(() -> new IllegalArgumentException("El libro especificado no existe en el catálogo."));

        if (libro.getActivo() != null && !libro.getActivo()) {
            throw new IllegalArgumentException("El libro especificado no está activo para préstamos.");
        }

        int stockDisponible = (libro.getStock() != null ? libro.getStock() : 0) - (libro.getStockReservado() != null ? libro.getStockReservado() : 0);
        if (stockDisponible <= 0) {
            throw new IllegalArgumentException("No hay ejemplares disponibles del libro '" + libro.getTitulo() + "' para préstamo.");
        }

        // 3. Reducir stock del libro
        libro.setStock(libro.getStock() - 1);
        libroRepository.save(libro);

        // 4. Crear el Préstamo
        OffsetDateTime fechaPrestamo = OffsetDateTime.now();
        OffsetDateTime fechaLimite = request.getFechaLimite() != null 
                ? request.getFechaLimite() 
                : fechaPrestamo.plusDays(7); // Plazo por defecto: 7 días

        Prestamo prestamo = Prestamo.builder()
                .usuarioId(request.getUsuarioId())
                .libroId(request.getLibroId())
                .reservaId(request.getReservaId())
                .fechaPrestamo(fechaPrestamo)
                .fechaLimite(fechaLimite)
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        prestamo = prestamoRepository.save(prestamo);

        // 5. Notificar al usuario (NO01)
        String mensajeNotificacion = String.format(
                "Tu préstamo del libro '%s' ha sido autorizado con éxito. Fecha límite de devolución: %s",
                libro.getTitulo(),
                fechaLimite.toLocalDate().toString()
        );

        Map<String, Object> notificationRequest = Map.of(
                "usuarioId", prestamo.getUsuarioId().toString(),
                "tipo", "PRESTAMO_AUTORIZADO",
                "referenciaId", prestamo.getId().toString(),
                "mensaje", mensajeNotificacion
        );
        
        try {
            notificacionClient.enviarNotificacion(notificationRequest);
        } catch (Exception e) {
            // Loguear error de notificación pero permitir que el préstamo continúe
            System.err.println("No se pudo enviar la notificación de préstamo autorizado: " + e.getMessage());
        }

        return prestamo;
    }

    public Prestamo obtenerPrestamoPorId(UUID id) {
        return prestamoRepository.findById(id).orElse(null);
    }

    @Transactional
    public void marcarComoDevuelto(UUID id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El préstamo especificado no existe."));
        
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamoRepository.save(prestamo);

        // Devolver stock del libro
        Libro libro = libroRepository.findById(prestamo.getLibroId()).orElse(null);
        if (libro != null) {
            libro.setStock(libro.getStock() + 1);
            libroRepository.save(libro);
        }
    }
}
