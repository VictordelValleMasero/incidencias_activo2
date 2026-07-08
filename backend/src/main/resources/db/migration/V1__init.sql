-- Activo2Incidencias - esquema inicial del modulo "Comunicar incidencia"

CREATE TABLE administrador (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    email           VARCHAR(180) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    rol             VARCHAR(30) NOT NULL DEFAULT 'ADMIN',
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE edificio (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    codigo          VARCHAR(50) NOT NULL UNIQUE,
    descripcion     TEXT NULL,
    estado          VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    orden           INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE zona (
    id              BIGSERIAL PRIMARY KEY,
    edificio_id     BIGINT NOT NULL REFERENCES edificio(id) ON DELETE CASCADE,
    nombre          VARCHAR(150) NOT NULL,
    codigo          VARCHAR(50) NOT NULL,
    descripcion     TEXT NULL,
    imagen          VARCHAR(500) NULL,
    estado          VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    orden           INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_zona_edificio ON zona(edificio_id);

-- departamento y responsable se referencian mutuamente (responsable principal
-- de un departamento, departamento de un responsable). Se crea primero
-- departamento sin esa columna, luego responsable, y se anade la FK despues.
CREATE TABLE departamento (
    id                      BIGSERIAL PRIMARY KEY,
    nombre                  VARCHAR(150) NOT NULL,
    descripcion             TEXT NULL,
    estado                  VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    responsable_principal_id BIGINT NULL,
    plantilla_whatsapp      TEXT NULL,
    created_at              TIMESTAMP NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE responsable (
    id                  BIGSERIAL PRIMARY KEY,
    departamento_id     BIGINT NOT NULL REFERENCES departamento(id) ON DELETE CASCADE,
    nombre              VARCHAR(100) NOT NULL,
    apellidos           VARCHAR(150) NULL,
    cargo               VARCHAR(150) NULL,
    telefono_whatsapp   VARCHAR(30) NOT NULL,
    email               VARCHAR(180) NULL,
    horario             VARCHAR(255) NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_responsable_departamento ON responsable(departamento_id);

ALTER TABLE departamento
    ADD CONSTRAINT fk_departamento_responsable_principal
    FOREIGN KEY (responsable_principal_id) REFERENCES responsable(id) ON DELETE SET NULL;

CREATE TABLE zona_departamento_responsable (
    id              BIGSERIAL PRIMARY KEY,
    edificio_id     BIGINT NOT NULL REFERENCES edificio(id) ON DELETE CASCADE,
    zona_id         BIGINT NOT NULL REFERENCES zona(id) ON DELETE CASCADE,
    departamento_id BIGINT NOT NULL REFERENCES departamento(id) ON DELETE CASCADE,
    responsable_id  BIGINT NOT NULL REFERENCES responsable(id) ON DELETE CASCADE,
    estado          VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    observaciones   TEXT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_zdr_zona ON zona_departamento_responsable(zona_id);
CREATE INDEX idx_zdr_departamento ON zona_departamento_responsable(departamento_id);
CREATE INDEX idx_zdr_zona_departamento ON zona_departamento_responsable(zona_id, departamento_id);

CREATE TABLE incidencia (
    id                      BIGSERIAL PRIMARY KEY,
    codigo                  VARCHAR(20) NOT NULL UNIQUE,
    edificio_id             BIGINT NOT NULL REFERENCES edificio(id),
    zona_id                 BIGINT NOT NULL REFERENCES zona(id),
    departamento_id         BIGINT NOT NULL REFERENCES departamento(id),
    responsable_id          BIGINT NULL REFERENCES responsable(id),
    descripcion             TEXT NOT NULL,
    estado                  VARCHAR(30) NOT NULL DEFAULT 'NUEVA',
    whatsapp_estado         VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    whatsapp_error          TEXT NULL,
    mensaje_whatsapp        TEXT NULL,
    observaciones_internas  TEXT NULL,
    created_at              TIMESTAMP NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP NOT NULL DEFAULT now(),
    closed_at               TIMESTAMP NULL
);

CREATE INDEX idx_incidencia_edificio ON incidencia(edificio_id);
CREATE INDEX idx_incidencia_zona ON incidencia(zona_id);
CREATE INDEX idx_incidencia_departamento ON incidencia(departamento_id);
CREATE INDEX idx_incidencia_estado ON incidencia(estado);
CREATE INDEX idx_incidencia_created ON incidencia(created_at);

CREATE TABLE imagen_incidencia (
    id                  BIGSERIAL PRIMARY KEY,
    incidencia_id       BIGINT NOT NULL REFERENCES incidencia(id) ON DELETE CASCADE,
    nombre_original     VARCHAR(255) NOT NULL,
    nombre_archivo      VARCHAR(255) NOT NULL,
    mime_type           VARCHAR(100) NOT NULL,
    size                BIGINT NOT NULL,
    url                 VARCHAR(500) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_imagen_incidencia ON imagen_incidencia(incidencia_id);

CREATE TABLE incidencia_historial (
    id              BIGSERIAL PRIMARY KEY,
    incidencia_id   BIGINT NOT NULL REFERENCES incidencia(id) ON DELETE CASCADE,
    estado_anterior VARCHAR(30) NULL,
    estado_nuevo    VARCHAR(30) NOT NULL,
    comentario      TEXT NULL,
    actor           VARCHAR(180) NULL,
    fecha           TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_historial_incidencia ON incidencia_historial(incidencia_id);

CREATE TABLE auditoria (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      VARCHAR(180) NULL,
    accion          VARCHAR(100) NOT NULL,
    entidad         VARCHAR(100) NOT NULL,
    entidad_id      VARCHAR(50) NULL,
    fecha           TIMESTAMP NOT NULL DEFAULT now(),
    ip              VARCHAR(64) NULL,
    detalles        TEXT NULL
);

CREATE INDEX idx_auditoria_entidad ON auditoria(entidad, entidad_id);
CREATE INDEX idx_auditoria_fecha ON auditoria(fecha);
