-- Verificación de correo obligatoria tras el registro (mismo patrón de
-- password_reset_tokens: token de un solo uso con vencimiento, esta vez 24h).
-- Las cuentas ya existentes se marcan verificadas para no bloquear a nadie;
-- las nuevas cuentas de registro normal se crean con verified = false.

ALTER TABLE users
    ADD COLUMN verified BOOLEAN NOT NULL DEFAULT true;

CREATE TABLE email_verification_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
