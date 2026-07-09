-- HU dashboard #4: stock histórico "a una fecha".
-- El dosímetro necesita una fecha de ingreso al inventario. Para los datos ya
-- migrados (que no traían esta fecha) la aproximamos a la fecha de su PRIMERA
-- asignación; si nunca fue asignado, usamos la fecha actual.

ALTER TABLE dosimetro
    ADD COLUMN fecha_creacion DATE NULL;

UPDATE dosimetro d
SET d.fecha_creacion = COALESCE(
        (SELECT MIN(a.fecha_asignacion) FROM ASIGNACION a WHERE a.dosimetro_id = d.id),
        CURRENT_DATE
    )
WHERE d.fecha_creacion IS NULL;

ALTER TABLE dosimetro
    MODIFY COLUMN fecha_creacion DATE NOT NULL;

CREATE INDEX idx_dosimetro_fecha_creacion ON dosimetro (fecha_creacion);
