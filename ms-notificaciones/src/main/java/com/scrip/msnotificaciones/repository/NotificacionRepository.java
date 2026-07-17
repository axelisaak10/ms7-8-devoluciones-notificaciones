package com.scrip.msnotificaciones.repository;

import com.scrip.msnotificaciones.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {
    List<Notificacion> findByUsuarioIdOrderByFechaEnvioDesc(UUID usuarioId);
    Optional<Notificacion> findByClaveIdempotencia(String claveIdempotencia);
    List<Notificacion> findByEstadoEntregaInAndIntentosLessThan(List<com.scrip.msnotificaciones.entity.EstadoEntrega> estados, int intentos);
}
