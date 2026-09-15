## Context

`users` (tabla creada en `V2__identity_events_materials_admin.sql`) no tiene columnas de fecha de nacimiento ni categoría de negocio. `User.java`/`RegisterRequest`/`UserResponse` (`backend/modules/identity-access`) tienen el patrón ya establecido de "campos opcionales por perfil" (profesión/años/empresa para Profesional, etc.) que este cambio extiende. `admin-usuarios.html`/`.ts` (`frontend/src/app/pages/admin/usuarios`) hoy solo renderiza `AdminUsersResponse` (lista + contadores) sin filtrar ni mostrar detalle; `biblioteca.ts` ya tiene el patrón de filtrado 100% client-side sobre una señal con la lista completa, que este cambio replica.

## Goals / Non-Goals

- Goal: capturar `birthDate`/`businessCategory` en el registro y exponerlos en `GET /api/admin/users` sin tocar el resto del flujo de auth (login, JWT, invitaciones no cambian).
- Goal: filtros y búsqueda 100% client-side, sin nuevos parámetros de query ni endpoints.
- Non-goal: paginación del listado de usuarios — sigue devolviendo todos los registros de una vez, como hoy.
- Non-goal: editar birthDate/businessCategory desde el admin — el detalle de usuario en este cambio es de solo lectura (igual que hoy).
- Non-goal: mostrar estos campos nuevos en la pantalla de perfil propio del usuario (`member-profile`) — fuera de alcance, ver proposal.md.

## Decisions

- **`birthDate` como `DATE` nullable, no `age` como entero**: decisión ya tomada con el usuario (ver conversación) — evita que el dato quede desactualizado. La edad se calcula en el frontend a partir de `birthDate` (no se persiste ni se envía calculada desde el backend), igual que hoy no existe ningún cálculo derivado server-side en `UserResponse`.
- **`businessCategory` como enum de 8 valores fijos** (`SERVICIOS, COMIDA_RESTAURANTE, RETAIL_COMERCIO, TECNOLOGIA, SALUD, EDUCACION, CONSTRUCCION, OTRO`), siguiendo el mismo patrón que `Profile`/`Role` (enum Java + `@Enumerated(EnumType.STRING)`), no una tabla separada — no hay necesidad de que sea configurable dinámicamente.
- **Ambos campos nullable en la entidad y en la migración**: los ~ usuarios ya registrados no tienen estos datos; no se hace backfill. Alternativa descartada: hacerlos `NOT NULL` con un valor por defecto — se rechazó porque un valor por defecto falso (p. ej. "Otro" para todos los registros viejos) ensuciaría los filtros y contadores.
- **`businessCategory` se guarda igual para Emprendedor y Empresario** (misma columna, mismo enum) en vez de dos columnas separadas — ambos representan "tipo de negocio/empresa" conceptualmente, y ya comparten el patrón de reutilizar el mismo campo entre perfiles no existe hoy pero introducir dos columnas idénticas sería duplicación innecesaria (YAGNI).
- **Filtrado client-side en Angular, no query params en `GET /api/admin/users`**: el endpoint ya trae la lista completa (sin paginar) porque el volumen esperado es bajo (cientos, no miles, de usuarios en una red de una iglesia local); replica exactamente el patrón ya usado en `biblioteca.ts` (`computed` sobre signals). Alternativa descartada: filtros server-side con query params — más trabajo (nuevo contrato de API, tests de backend) sin beneficio real a este volumen.
- **Buckets de edad y de empleados como listas de rangos hardcodeadas en el componente** (no configurables), igual que los filtros de perfil ya existentes en `biblioteca.ts` — consistente con el resto de la app, que no tiene ningún mecanismo de configuración dinámica de UI.
- **Detalle de usuario como bottom-sheet** (reutilizando el componente `BottomSheet` ya usado en `admin-eventos`/`admin-material`), no una ruta nueva — consistente con el patrón existente y evita añadir un guard/ruta nueva para un panel de solo lectura.

## Risks / Trade-offs

- [Usuarios con `birthDate`/`businessCategory` nulos quedan invisibles bajo un filtro de edad o categoría activo, lo que puede confundir al admin si no se comunica] → El estado vacío y el propio filtro deben ser explícitos (chip de filtro activo visible, mensaje de "sin resultados" claro); no se agrega una opción "sin dato" a los buckets en este cambio por simplicidad, documentado como comportamiento esperado en el spec.
- [Migración Flyway sobre una tabla `users` que ya tiene filas en producción] → Columnas `nullable`, sin `DEFAULT`, sin backfill — migración aditiva de bajo riesgo, no bloquea ni reescribe filas existentes.

## Migration Plan

1. Nueva migración `V5__user_birth_date_business_category.sql`: `ALTER TABLE users ADD COLUMN birth_date DATE; ALTER TABLE users ADD COLUMN business_category VARCHAR(32);` (ambas nullable, sin default).
2. Desplegar backend con la migración — Flyway la aplica automáticamente al arrancar (mismo mecanismo que las migraciones V1-V4 existentes).
3. Desplegar frontend con el wizard y la pantalla de admin actualizados.
4. Rollback: si hace falta revertir, basta con desplegar el frontend/backend anteriores — las columnas nuevas quedan nullable e inertes, no requieren rollback de esquema.
