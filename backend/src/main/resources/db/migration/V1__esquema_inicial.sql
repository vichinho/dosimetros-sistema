-- Esquema inicial del Sistema de Gestión de Dosímetros (modelo v5)
-- Tipos elegidos para coincidir con los mapeos JPA de las entidades:
--   Integer -> INT, Boolean -> TINYINT(1) (Connector/J lo reporta como BIT),
--   LocalDate -> DATE, String -> VARCHAR(n).

CREATE TABLE empresa (
    id     INT          NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    activa TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT uk_empresa_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE rol (
    id     INT         NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE tipo_dosimetro (
    id     INT         NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_tipo_dosimetro_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE tipo_porta (
    id                INT          NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(100) NOT NULL,
    tipo_dosimetro_id INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_tipo_porta_tipo_dosimetro
        FOREIGN KEY (tipo_dosimetro_id) REFERENCES tipo_dosimetro (id)
) ENGINE = InnoDB;

CREATE TABLE ejecutivo (
    id     INT          NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(200) NOT NULL,
    email  VARCHAR(200),
    activo TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE usuario (
    id            INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol_id        INT          NOT NULL,
    ejecutivo_id  INT,
    activo        TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_username UNIQUE (username),
    CONSTRAINT fk_usuario_rol       FOREIGN KEY (rol_id)       REFERENCES rol (id),
    CONSTRAINT fk_usuario_ejecutivo FOREIGN KEY (ejecutivo_id) REFERENCES ejecutivo (id)
) ENGINE = InnoDB;

CREATE TABLE cliente (
    id           INT          NOT NULL AUTO_INCREMENT,
    razon_social VARCHAR(300) NOT NULL,
    nombre_corto VARCHAR(200),
    activo       TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE tarea (
    id             INT          NOT NULL AUTO_INCREMENT,
    numero_tarea   VARCHAR(100) NOT NULL,
    fecha_creacion DATE         NOT NULL,
    observacion    VARCHAR(500),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE dosimetro (
    id                INT         NOT NULL AUTO_INCREMENT,
    numero            INT         NOT NULL,
    tipo_dosimetro_id INT         NOT NULL,
    tipo_porta_id     INT,
    tarea_id          INT,
    numero_bandeja    INT,
    slot_bandeja      INT,
    estado            VARCHAR(50) NOT NULL DEFAULT 'disponible',
    observacion       VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT fk_dosimetro_tipo_dosimetro
        FOREIGN KEY (tipo_dosimetro_id) REFERENCES tipo_dosimetro (id),
    CONSTRAINT fk_dosimetro_tipo_porta
        FOREIGN KEY (tipo_porta_id) REFERENCES tipo_porta (id),
    CONSTRAINT fk_dosimetro_tarea
        FOREIGN KEY (tarea_id) REFERENCES tarea (id)
) ENGINE = InnoDB;

CREATE INDEX idx_dosimetro_numero ON dosimetro (numero);
CREATE INDEX idx_dosimetro_estado ON dosimetro (estado);
CREATE INDEX idx_dosimetro_tarea  ON dosimetro (tarea_id);

-- Nombre en mayúscula para coincidir con @Table(name = "ASIGNACION").
CREATE TABLE ASIGNACION (
    id               INT         NOT NULL AUTO_INCREMENT,
    dosimetro_id     INT         NOT NULL,
    cliente_id       INT         NOT NULL,
    ejecutivo_id     INT         NOT NULL,
    empresa_id       INT         NOT NULL,
    tipo_porta_id    INT         NOT NULL,
    tarea_id         INT,
    numero_bandeja   INT,
    slot_bandeja     INT,
    trimestre        VARCHAR(20) NOT NULL,
    fecha_asignacion DATE        NOT NULL,
    link_trello      VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT fk_asignacion_dosimetro  FOREIGN KEY (dosimetro_id)  REFERENCES dosimetro (id),
    CONSTRAINT fk_asignacion_cliente    FOREIGN KEY (cliente_id)    REFERENCES cliente (id),
    CONSTRAINT fk_asignacion_ejecutivo  FOREIGN KEY (ejecutivo_id)  REFERENCES ejecutivo (id),
    CONSTRAINT fk_asignacion_empresa    FOREIGN KEY (empresa_id)    REFERENCES empresa (id),
    CONSTRAINT fk_asignacion_tipo_porta FOREIGN KEY (tipo_porta_id) REFERENCES tipo_porta (id),
    CONSTRAINT fk_asignacion_tarea      FOREIGN KEY (tarea_id)      REFERENCES tarea (id)
) ENGINE = InnoDB;

CREATE INDEX idx_asignacion_dosimetro ON ASIGNACION (dosimetro_id);
CREATE INDEX idx_asignacion_cliente   ON ASIGNACION (cliente_id);
CREATE INDEX idx_asignacion_ejecutivo ON ASIGNACION (ejecutivo_id);
