package com.scrip.msnotificaciones.dto;

import com.scrip.msnotificaciones.entity.TipoNotificacion;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private UUID usuarioId;

    @NotNull(message = "El tipo de notificación es obligatorio")
    private TipoNotificacion tipo;

    private UUID referenciaId;

    @NotNull(message = "El mensaje es obligatorio")
    private String mensaje;
}
