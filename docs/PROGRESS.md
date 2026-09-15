# PROGRESS.md

**Estado actual: dominio funcional "Red de Visionarios" implementado (backend completo, frontend implementado por un agente en la misma sesión) contra `designdownloaded/DESIGN_SPEC.md`.**

## Verificado en esta sesión (2026-09-14)

- Backend: agregados `platform:security`, `modules:identity-access`, `modules:events`, `modules:materials`, `modules:admin`. `cd backend && ./gradlew build -x test` — compiló y generó el jar sin errores (`21 actionable tasks`, `BUILD SUCCESSFUL`).
- Migración `V2__identity_events_materials_admin.sql` agregada después de `V1__init.sql` (que se deja intacta).
- Endpoints implementados: `/api/auth/register|login|forgot-password|accept-invite/{token}`, `/api/users/me`, `/api/events`, `/api/events/{id}`, `/api/events/{id}/rsvp`, `/api/admin/events`, `/api/materials`, `/api/admin/materials`, `/api/admin/invites`, `/api/admin/users`.
- Frontend: implementado por un agente en la misma sesión (Angular standalone, rutas, AuthService, guards, interceptor JWT, pantallas del DESIGN_SPEC.md) — ver el reporte de esa tarea para el detalle de archivos y si `npm run build` pasó.
- No se levantó el backend contra Postgres real en runtime (falta Postgres local con la DB `visionarios`) ni se probó el flujo end-to-end en el navegador.

## Verificado en esta sesión (2026-09-14, correos)

- Se pudo leer el proyecto de Claude Design completo (`list_files`/`get_file` de `DesignSync` contra el projectId `0d3a7fc3-...`) y se trajeron los 3 correos reales a `platform/web-common/.../resources/mail-templates/`.
- Agregado `MailSender`/`ContactApiMailSender`/`MailConfig` en `platform:web-common` (llama a `POST /api/send` de `contact-api`, mismo patrón que `micasachurch`).
- Migración `V3__password_reset_tokens.sql`; nuevo endpoint `POST /api/auth/reset-password`; `forgotPassword` ahora genera el token y manda el correo (ya no es un TODO).
- `AdminInviteService.create()` ahora manda el correo de invitación con el nombre de quien invita.
- Nuevo endpoint admin `POST /api/admin/events/{id}/notify` + botón "Enviar invitación por correo" en `admin-eventos` — envía el correo de evento a los usuarios a los que aplica.
- Nueva página/ruta frontend `restablecer/:token` para completar el flujo del enlace del correo.
- `./gradlew compileJava` y `npx tsc --noEmit` (frontend) pasan sin errores. No se probó el envío real end-to-end (requiere `contact-api` corriendo en `127.0.0.1:3000`).

## Tarea actual

Ninguna pendiente de esta sesión de implementación.

## Próximo paso recomendado

1. Levantar Postgres local y correr el backend + frontend juntos para una verificación end-to-end manual del flujo de login/registro/RSVP/admin.
2. Conseguir los logos PNG reales (ver `docs/DECISIONS.md`) y reemplazar el logo de texto.
3. Levantar `contact-api` local (`cd ../../contact && npm start`) y probar de punta a punta el envío de los 3 correos.

## Bloqueos

- Ninguno técnico bloqueante. Pendiente de asset (logo) documentado arriba.
