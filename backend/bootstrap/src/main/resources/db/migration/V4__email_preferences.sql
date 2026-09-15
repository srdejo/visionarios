-- Soporte real de "darme de baja" para los correos de tipo boletín/invitación
-- (no aplica a correos transaccionales como recuperar contraseña o invitación de
-- administrador, que el usuario mismo disparó). Ver docs/DECISIONS.md.

ALTER TABLE users
    ADD COLUMN email_opt_out BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN unsubscribe_token VARCHAR(255) UNIQUE
        DEFAULT md5(random()::text || clock_timestamp()::text);

UPDATE users SET unsubscribe_token = md5(random()::text || clock_timestamp()::text) WHERE unsubscribe_token IS NULL;

ALTER TABLE users ALTER COLUMN unsubscribe_token SET NOT NULL;
