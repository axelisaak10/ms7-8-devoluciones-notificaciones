package com.scrip.msdevoluciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoDto {
    private UUID id;
    private UUID usuarioId;
    private UUID libroId;
    private UUID reservaId;
    private OffsetDateTime fechaPrestamo;
    private OffsetDateTime fechaLimite;
    private String estado; // ACTIVO, DEVUELTO
}
