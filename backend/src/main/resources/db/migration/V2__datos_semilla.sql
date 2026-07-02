-- Datos semilla: catálogos base y usuario administrador inicial.

-- Roles del sistema
INSERT INTO rol (nombre) VALUES
    ('ADMIN'),
    ('OPERADOR'),
    ('EJECUTIVO');

-- Empresas administradas
INSERT INTO empresa (nombre, activa) VALUES
    ('Photomat', 1),
    ('Dosimet', 1);

-- Tipos de dosímetro
INSERT INTO tipo_dosimetro (nombre) VALUES
    ('OSL'),
    ('TLD'),
    ('Cristal');

-- Tipos de porta y su compatibilidad con cada tipo de dosímetro
-- (incluye el estado inicial "Sin armar" por tipo).
INSERT INTO tipo_porta (nombre, tipo_dosimetro_id)
SELECT 'OSL',             id FROM tipo_dosimetro WHERE nombre = 'OSL';
INSERT INTO tipo_porta (nombre, tipo_dosimetro_id)
SELECT 'Sin armar (OSL)', id FROM tipo_dosimetro WHERE nombre = 'OSL';

INSERT INTO tipo_porta (nombre, tipo_dosimetro_id)
SELECT 'Porta gringo',    id FROM tipo_dosimetro WHERE nombre = 'TLD';
INSERT INTO tipo_porta (nombre, tipo_dosimetro_id)
SELECT 'Porta viejo',     id FROM tipo_dosimetro WHERE nombre = 'TLD';
INSERT INTO tipo_porta (nombre, tipo_dosimetro_id)
SELECT 'Sin armar (TLD)', id FROM tipo_dosimetro WHERE nombre = 'TLD';

INSERT INTO tipo_porta (nombre, tipo_dosimetro_id)
SELECT 'Anillo',              id FROM tipo_dosimetro WHERE nombre = 'Cristal';
INSERT INTO tipo_porta (nombre, tipo_dosimetro_id)
SELECT 'Pulsera',             id FROM tipo_dosimetro WHERE nombre = 'Cristal';
INSERT INTO tipo_porta (nombre, tipo_dosimetro_id)
SELECT 'Cristalino',          id FROM tipo_dosimetro WHERE nombre = 'Cristal';
INSERT INTO tipo_porta (nombre, tipo_dosimetro_id)
SELECT 'Sin armar (Cristal)', id FROM tipo_dosimetro WHERE nombre = 'Cristal';

-- Usuario administrador inicial.
-- Credenciales: admin / admin123  (hash BCrypt; cambiar la contraseña tras el primer ingreso).
INSERT INTO usuario (username, password_hash, rol_id, ejecutivo_id, activo)
SELECT 'admin',
       '$2b$10$.441q9lcXExGKUvV7oQgZO0u0mwJmhZvogFZ4xtjNnJdw5slltf82',
       r.id,
       NULL,
       1
FROM rol r
WHERE r.nombre = 'ADMIN';
