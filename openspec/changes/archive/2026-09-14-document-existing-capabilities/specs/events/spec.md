## Purpose

Permite a los usuarios ver los eventos programados de la red, confirmar su asistencia, y a los administradores crear, editar, y notificar por correo los eventos.

## ADDED Requirements

### Requirement: Listado de eventos ordenado por fecha
El sistema SHALL exponer (`GET /api/events`) todos los eventos ordenados por fecha de inicio ascendente, cada uno con su cantidad de confirmados y si el usuario autenticado ya confirmó asistencia. No filtra por perfil en el listado — todos los eventos son visibles para cualquier usuario autenticado; el perfil objetivo (`targetProfile`, `null` = toda la red) es solo un dato informativo mostrado en el detalle.

#### Scenario: Listar eventos
- **WHEN** un usuario autenticado solicita la agenda de eventos
- **THEN** el sistema devuelve todos los eventos ordenados por `startsAt` ascendente, con `confirmedCount` y `confirmedByMe` calculados para ese usuario

### Requirement: Detalle de evento y RSVP
El sistema SHALL permitir consultar el detalle de un evento por id (`GET /api/events/{id}`) y alternar (toggle) la confirmación de asistencia propia (`POST /api/events/{id}/rsvp`).

#### Scenario: Confirmar asistencia
- **WHEN** un usuario que no ha confirmado asistencia a un evento llama al endpoint de RSVP
- **THEN** el sistema registra su confirmación, incrementa `confirmedCount` en 1, y `confirmedByMe` pasa a verdadero

#### Scenario: Retirar asistencia (toggle)
- **WHEN** un usuario que ya había confirmado asistencia vuelve a llamar al endpoint de RSVP para ese mismo evento
- **THEN** el sistema elimina su confirmación, decrementa `confirmedCount` en 1, y `confirmedByMe` pasa a falso

#### Scenario: Evento inexistente
- **WHEN** se consulta o se hace RSVP sobre un id de evento que no existe
- **THEN** el sistema responde con error "Evento no encontrado" (404)

### Requirement: Administración de eventos
El sistema SHALL permitir a un administrador crear (`POST /api/admin/events`) y editar (`PUT /api/admin/events/{id}`) eventos: título, fecha/hora de inicio, lugar, descripción, y perfil objetivo (o `null` para toda la red).

#### Scenario: Crear evento
- **WHEN** un administrador envía título, fecha/hora, lugar, descripción y perfil objetivo válidos
- **THEN** el sistema crea el evento y queda disponible de inmediato en el listado general

#### Scenario: Editar evento existente
- **WHEN** un administrador envía cambios sobre un evento existente
- **THEN** el sistema actualiza esos campos y los cambios se reflejan en las siguientes consultas del evento

### Requirement: Notificación de evento por correo
El sistema SHALL permitir a un administrador disparar el envío de un correo de invitación (`POST /api/admin/events/{id}/notify`) a los usuarios a los que aplica el evento (todos si el perfil objetivo es `null`, o solo los de ese perfil), excluyendo a quienes se hayan dado de baja de correos (`isEmailOptOut`).

#### Scenario: Notificar evento abierto a toda la red
- **WHEN** un administrador dispara la notificación de un evento sin perfil objetivo
- **THEN** el sistema envía el correo de invitación a todos los usuarios que no se hayan dado de baja, y devuelve la cantidad de destinatarios

#### Scenario: Notificar evento dirigido a un perfil
- **WHEN** un administrador dispara la notificación de un evento con perfil objetivo definido
- **THEN** el sistema envía el correo solo a los usuarios de ese perfil que no se hayan dado de baja
