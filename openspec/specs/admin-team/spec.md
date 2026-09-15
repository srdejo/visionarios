# admin-team Specification

## Purpose
Permite a un administrador existente invitar a nuevos administradores mediante enlaces de un solo uso con expiración de 7 días, y mantener visibilidad de las invitaciones generadas.

## Requirements

### Requirement: Generación de invitación de administrador
El sistema SHALL permitir a un administrador generar (`POST /api/admin/invites`) una invitación para un correo, creando un token único que expira exactamente 7 días después de su creación, y enviando el enlace `/aceptar-invitacion/{token}` por correo al destinatario.

#### Scenario: Generar invitación
- **WHEN** un administrador solicita una invitación indicando el correo del destinatario
- **THEN** el sistema crea un token único con `expiresAt = ahora + 7 días`, lo marca como no usado, y envía el correo con el enlace de aceptación

### Requirement: Listado de invitaciones
El sistema SHALL exponer (`GET /api/admin/invites`) todas las invitaciones generadas, ordenadas por fecha de creación descendente, indicando para cada una si está usada y si está vencida.

#### Scenario: Ver invitaciones generadas
- **WHEN** un administrador consulta el listado de invitaciones
- **THEN** el sistema devuelve todas las invitaciones con su estado de uso y vencimiento, más recientes primero

### Requirement: Acceso restringido a administradores
El sistema SHALL restringir la generación y consulta de invitaciones a usuarios con rol `ADMIN`.

#### Scenario: Usuario sin rol admin intenta generar o ver invitaciones
- **WHEN** un usuario con rol `USER` llama a `POST /api/admin/invites` o `GET /api/admin/invites`
- **THEN** el sistema responde 403 y no ejecuta la operación

Nota: la aceptación de la invitación (creación de la cuenta `ADMIN` a partir del token) está documentada en la capacidad `user-auth`, ya que ese endpoint es público y forma parte del flujo de autenticación.
