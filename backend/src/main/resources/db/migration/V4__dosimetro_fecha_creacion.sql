-- Fecha de creación del dosímetro: permite consultar el stock histórico
-- (cuántos dosímetros existían a una fecha pasada), calculado por creación
-- y no por asignación (requerimiento #4 del dashboard).

ALTER TABLE dosimetro ADD COLUMN fecha_creacion DATE NULL;

-- Los dosímetros que pertenecen a una tarea heredan la fecha de creación de
-- esa tarea (representa el lote/armado en que entraron al inventario).
UPDATE dosimetro d
JOIN tarea t ON d.tarea_id = t.id
SET d.fecha_creacion = t.fecha_creacion
WHERE d.fecha_creacion IS NULL;

-- Los que no tienen tarea toman la fecha actual como aproximación.
UPDATE dosimetro
SET fecha_creacion = CURRENT_DATE
WHERE fecha_creacion IS NULL;

ALTER TABLE dosimetro MODIFY COLUMN fecha_creacion DATE NOT NULL;
