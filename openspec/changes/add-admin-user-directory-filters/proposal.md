## Why

El admin puede ver hoy una lista plana de usuarios (nombre, correo, perfil) sin poder consultar el resto de su información ni filtrarla. Con la red creciendo, el equipo necesita poder encontrar rápidamente usuarios por perfil, categoría de negocio, tamaño de empresa o rango de edad, y ver el detalle completo de cada uno sin salir de la pantalla. Dos datos que hoy no se capturan (fecha de nacimiento, categoría de negocio) son necesarios para habilitar esos filtros.

## What Changes

- El registro (paso 1, todos los perfiles) pide **fecha de nacimiento**. Se almacena como fecha, nunca como edad numérica — la edad se calcula siempre a partir de ella.
- El registro (paso 3, solo Emprendedor y Empresario) pide **categoría de negocio**, de una lista fija de 8 opciones: Servicios, Comida/Restaurante, Retail/Comercio, Tecnología, Salud, Educación, Construcción, Otro.
- La pantalla `/admin/usuarios` gana:
  - Vista de **detalle de usuario** (bottom-sheet, mismo patrón que admin-eventos/admin-material) con todos sus datos, incluida la edad calculada y la categoría de negocio.
  - **Barra de búsqueda** por nombre o correo.
  - **Filtro por perfil** (Profesional/Emprendedor/Empresario/Todos).
  - **Filtro por categoría de negocio** (chips, solo aplica a Emprendedor/Empresario).
  - **Filtro por rango de edad** en buckets predefinidos (18-25, 26-35, 36-45, 46-60, 60+), calculados a partir de la fecha de nacimiento.
  - **Filtro por número de empleados** en buckets predefinidos (1-10, 11-50, 51-200, 200+), solo aplica a perfil Empresario.
  - Todos los filtros y la búsqueda son combinables y se resuelven en el cliente (el endpoint `GET /api/admin/users` sigue devolviendo la lista completa de una sola vez, mismo patrón ya usado en la biblioteca de material).

## Capabilities

### New Capabilities
(ninguna)

### Modified Capabilities
- `user-auth`: el registro (paso 1 y paso 3 del wizard) gana los campos `birthDate` (todos los perfiles) y `businessCategory` (solo Emprendedor/Empresario).
- `admin-directory`: la pantalla de usuarios gana detalle de usuario, búsqueda por texto, y filtros por perfil, categoría de negocio, rango de edad y número de empleados.

## Impact

- **Backend**: nueva migración Flyway (`V5__`) que agrega columnas `birth_date` (date, nullable — los usuarios existentes no la tienen) y `business_category` (varchar, nullable) a `users`; nuevo enum `BusinessCategory`; `RegisterRequest`/`UserResponse` ganan ambos campos; sin cambios en `AuthController`/`AuthService` más allá de pasar los nuevos campos al crear el usuario.
- **Frontend**: el wizard de registro (paso 1 y paso 3) gana los campos nuevos; `admin-usuarios` gana el bottom-sheet de detalle, la barra de búsqueda y los 4 filtros, todo resuelto client-side con los datos que ya trae `GET /api/admin/users`.
- **Datos existentes**: los usuarios ya registrados quedan con `birthDate`/`businessCategory` nulos — el detalle de usuario y los filtros deben tratar esos casos (mostrar "No indicado" / excluir de los filtros de edad y categoría en vez de fallar).
- **Fuera de alcance**: no se modifica la pantalla de perfil propio del usuario (`member-profile`) — estos campos nuevos solo se muestran en el detalle de admin, no al usuario sobre sí mismo, en este cambio.
