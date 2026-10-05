CREATE TABLE iam.cuenta (
    id_cuenta       UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    correo          VARCHAR(254) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    rol             VARCHAR(20)  NOT NULL CHECK (rol IN ('PROPIETARIO', 'ADMINISTRADOR', 'SUPERADMIN')),
    plan            VARCHAR(10)  NOT NULL DEFAULT 'FREE' CHECK (plan IN ('FREE', 'PRO')),
    fecha_registro  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE iam.recuperacion_contrasena (
    id_recuperacion UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    id_cuenta       UUID         NOT NULL REFERENCES iam.cuenta (id_cuenta) ON DELETE CASCADE,
    token_hash      VARCHAR(255) NOT NULL,
    creado_en       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    expira_en       TIMESTAMPTZ  NOT NULL,
    utilizado_en    TIMESTAMPTZ
);

CREATE INDEX idx_recuperacion_token_hash ON iam.recuperacion_contrasena (token_hash);
CREATE INDEX idx_recuperacion_id_cuenta  ON iam.recuperacion_contrasena (id_cuenta);

CREATE TABLE iam.perfil (
    id_perfil   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    id_cuenta   UUID         NOT NULL UNIQUE REFERENCES iam.cuenta (id_cuenta) ON DELETE CASCADE,
    nombres     VARCHAR(100) NOT NULL,
    apellidos   VARCHAR(100) NOT NULL,
    telefono    VARCHAR(20),
    direccion   VARCHAR(255)
);
