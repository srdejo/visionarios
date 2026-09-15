# ROADMAP.md

## Etapa 0 — Base técnica

- [x] Backend: Gradle multi-módulo (`bootstrap`, `platform:web-common`, `modules:core`) — compila con `./gradlew build`.
- [x] Backend: `/api/ping` responde vía `ApiResponse`, Flyway con `V1__init.sql`, Actuator health expuesto.
- [x] Frontend: Angular standalone + Tailwind, `npm run build` compila.
- [x] Frontend: página mínima que hace fetch a `/api/ping`.
- [x] Infra: vhost nginx + unit systemd para `visionarios.micasachurch.co` (puerto 8089).
- [x] Documentación base (`README.md`, `CLAUDE.md`, `docs/*`).
- [ ] Backend levantado localmente contra Postgres real y `/api/ping` verificado en runtime (pendiente: crear la DB local).
- [ ] Despliegue real a `nolost-vps` (pendiente: dominio funcional definido primero).

## Etapa 1 — Red de Visionarios (dominio real)

- [x] Backend: `platform:security` (JWT) copiado y adaptado de `consulting`, sin `platform:tenancy`.
- [x] Backend: `modules:identity-access` — User, registro/login/forgot-password, `/api/users/me`.
- [x] Backend: `modules:events` — Event/EventRsvp, listado/detalle/RSVP toggle, CRUD admin.
- [x] Backend: `modules:materials` — Material filtrado por perfil, CRUD admin.
- [x] Backend: `modules:admin` — invitaciones de administrador (7 días, un solo uso), listado de usuarios con conteo por perfil.
- [x] Backend: `./gradlew build` compila con todos los módulos nuevos.
- [ ] Frontend: pantallas y rutas de `DESIGN_SPEC.md` — implementado por agente en esta sesión, verificar `npm run build` y marcar aquí cuando se confirme.
- [ ] Verificación end-to-end manual (Postgres local + backend + frontend corriendo juntos).
- [x] Reemplazar el logo de texto por el PNG real (ver `docs/DECISIONS.md`).
- [x] Envío real de correo (recuperar contraseña, invitación de administrador, invitación de evento) vía `contact-api`, con las plantillas reales del diseño (ver `docs/DECISIONS.md`).
- [ ] Despliegue a `nolost-vps` (pendiente: nada técnico lo bloquea, falta decisión de cuándo salir a producción).
