-- #18: despacho físico de las asignaciones. "Pendiente de envío" = asignación
-- cuyo dosímetro ya está asignado pero todavía no se despachó (enviado = 0).

ALTER TABLE ASIGNACION ADD COLUMN enviado TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE ASIGNACION ADD COLUMN fecha_envio DATE NULL;
