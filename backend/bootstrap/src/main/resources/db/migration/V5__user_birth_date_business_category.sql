-- Fecha de nacimiento (para calcular edad, nunca se persiste como número) y categoría
-- de negocio (Emprendedor/Empresario), usadas por los filtros del directorio de admin.
-- Nullable y sin default: las cuentas ya registradas no tienen estos datos.

ALTER TABLE users
    ADD COLUMN birth_date DATE,
    ADD COLUMN business_category VARCHAR(32);
