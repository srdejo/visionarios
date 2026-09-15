-- El dominio funcional de "visionarios" todavia no esta definido.
-- Esta migracion solo deja Flyway operativo con una tabla trivial de ejemplo.
-- Las migraciones reales llegan cuando se defina el dominio de negocio (ver docs/DECISIONS.md).
CREATE TABLE ping_log (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
