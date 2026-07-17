ALTER TABLE operaciones.notificaciones ADD COLUMN IF NOT EXISTS correo_destino VARCHAR(180);
ALTER TABLE operaciones.notificaciones ADD COLUMN IF NOT EXISTS estado_entrega VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE';
ALTER TABLE operaciones.notificaciones ADD COLUMN IF NOT EXISTS intentos INTEGER NOT NULL DEFAULT 0;
ALTER TABLE operaciones.notificaciones ADD COLUMN IF NOT EXISTS proveedor_id VARCHAR(100);
ALTER TABLE operaciones.notificaciones ADD COLUMN IF NOT EXISTS ultimo_error TEXT;
ALTER TABLE operaciones.notificaciones ADD COLUMN IF NOT EXISTS clave_idempotencia VARCHAR(180);
ALTER TABLE operaciones.notificaciones ADD COLUMN IF NOT EXISTS fecha_entrega TIMESTAMP WITH TIME ZONE;
CREATE UNIQUE INDEX IF NOT EXISTS ux_notificaciones_clave_idempotencia
    ON operaciones.notificaciones (clave_idempotencia);
