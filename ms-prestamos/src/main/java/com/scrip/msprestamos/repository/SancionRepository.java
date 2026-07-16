package com.scrip.msprestamos.repository;

import com.scrip.msprestamos.entity.Sancion;
import com.scrip.msprestamos.entity.EstadoSancion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SancionRepository extends JpaRepository<Sancion, UUID> {
    List<Sancion> findByUsuarioIdAndEstado(UUID usuarioId, EstadoSancion estado);
}
