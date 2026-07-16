package com.scrip.msdevoluciones.repsitory;

import com.scrip.msdevoluciones.entity.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, UUID> {
    boolean existsByPrestamo(UUID prestamoId);
}
