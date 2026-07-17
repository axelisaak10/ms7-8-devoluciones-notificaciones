package com.scrip.msnotificaciones.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificaciones", schema = "operaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoNotificacion tipo;

    @Column(name = "referencia_id")
    private UUID referenciaId; // Opcional, puede guardar el ID de un préstamo, sanción, etc.

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "correo_destino", length = 180)
    private String correoDestino;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_entrega", nullable = false, length = 20)
    private EstadoEntrega estadoEntrega = EstadoEntrega.PENDIENTE;

    @Builder.Default
    @Column(nullable = false)
    private int intentos = 0;

    @Column(name = "proveedor_id", length = 100)
    private String proveedorId;

    @Column(name = "ultimo_error", columnDefinition = "TEXT")
    private String ultimoError;

    @Column(name = "clave_idempotencia", length = 180, unique = true)
    private String claveIdempotencia;

    @Column(name = "fecha_entrega")
    private OffsetDateTime fechaEntrega;

    @Builder.Default
    @Column(name = "fecha_envio", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT now()")
    private OffsetDateTime fechaEnvio = OffsetDateTime.now();
}
