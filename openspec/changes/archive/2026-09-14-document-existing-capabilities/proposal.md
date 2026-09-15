## Why

El dominio funcional de `visionarios` (auth, wizard de registro, eventos, material, administración) ya está completamente implementado en código, pero nunca pasó por OpenSpec — `openspec/specs/` está vacío. Sin una línea base documentada, cualquier propuesta futura carece de contexto contra el cual comparar y corre el riesgo de reinventar o malinterpretar el alcance de lo ya construido.

## What Changes

- No se modifica ningún comportamiento del sistema. Este cambio documenta, en formato de spec, el comportamiento ya implementado y desplegado en el código actual (backend `modules:identity-access`, `modules:events`, `modules:materials`, `modules:admin`, y frontend `pages/auth`, `pages/home`, `pages/biblioteca`, `pages/evento-detalle`, `pages/perfil`, `pages/admin/*`).
- Se establecen 6 capacidades como línea base en `openspec/specs/`, reflejando el comportamiento observado en el código, no un comportamiento deseado nuevo.

## Capabilities

### New Capabilities
- `user-auth`: registro (wizard 4 pasos, incluido el checkbox "búsqueda de empleo"), login, logout, recuperar/restablecer contraseña, aceptación de invitación de admin, sesión JWT.
- `member-profile`: consulta del perfil propio y recorte visual del nombre en pantallas angostas.
- `events`: agenda de eventos, detalle de evento, RSVP, notificación por correo, y CRUD de eventos desde administración.
- `materials`: biblioteca de material filtrable, y CRUD de material desde administración.
- `admin-team`: invitación de administradores (enlace único, expira en 7 días, un solo uso) y listado de equipo.
- `admin-directory`: listado de usuarios registrados con contadores por perfil.

### Modified Capabilities
(ninguna — son capacidades nuevas para OpenSpec, aunque el código ya exista)

## Impact

- Solo se agregan archivos bajo `openspec/specs/`. No se toca código de `backend/` ni `frontend/`.
- Habilita que futuras propuestas (`opsx:propose`) generen deltas correctos contra una línea base real en vez de asumir un dominio inexistente.
