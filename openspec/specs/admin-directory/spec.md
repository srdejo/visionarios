# admin-directory Specification

## Purpose
Da a los administradores visibilidad de todas las cuentas registradas en la red y su distribución por perfil.

## Requirements

### Requirement: Listado de usuarios con contadores por perfil
El sistema SHALL exponer (`GET /api/admin/users`) el listado completo de cuentas registradas (incluyendo tanto rol `USER` como `ADMIN`) junto con el total de cuentas y un contador por cada uno de los tres perfiles (Profesional, Emprendedor, Empresario).

#### Scenario: Ver directorio de usuarios
- **WHEN** un administrador consulta el listado de usuarios
- **THEN** el sistema devuelve todas las cuentas registradas con su perfil, y los contadores por perfil coinciden con la cantidad real de cuentas en cada uno

### Requirement: Acceso restringido a administradores
El sistema SHALL restringir la consulta del directorio de usuarios a usuarios con rol `ADMIN`.

#### Scenario: Usuario sin rol admin intenta ver el directorio
- **WHEN** un usuario con rol `USER` llama a `GET /api/admin/users`
- **THEN** el sistema responde 403 y no ejecuta la operación
