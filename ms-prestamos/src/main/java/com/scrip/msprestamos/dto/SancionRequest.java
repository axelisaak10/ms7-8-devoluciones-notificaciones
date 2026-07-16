package com.scrip.msprestamos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SancionRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private UUID usuarioId;

    @NotNull(message = "El ID del préstamo es obligatorio")
    private UUID prestamoId;

    @NotNull(message = "El monto de la sanción es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto debe ser mayor o igual a 0")
    private BigDecimal monto;
}
