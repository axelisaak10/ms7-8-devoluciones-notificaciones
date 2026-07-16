package com.scrip.msdevoluciones.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionRequest {
    @NotNull(message = "El ID del préstamo es obligatorio")
    private UUID prestamoId;

    private OffsetDateTime fechaDevolucion;
}
