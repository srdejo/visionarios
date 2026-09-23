# ROADMAP.md

## Etapa 0 — Base técnica

- [x] Backend: Gradle multi-módulo (`bootstrap`, `platform:web-common`, `modules:core`) — compila con `./gradlew build`.
- [x] Backend: `/api/ping` responde vía `ApiResponse`, Flyway con `V1__init.sql`, Actuator health expuesto.
- [x] Frontend: Angular standalone + Tailwind, `npm run build` compila.
- [x] Frontend: página mínima que hace fetch a `/api/ping`.
- [x] Infra: vhost nginx + unit systemd para `visionarios.micasachurch.co` (puerto 8089).
- [x] Documentación base (`README.md`, `CLAUDE.md`, `docs/*`).
- [x] Backend levantado localmente contra Postgres real y `/api/ping` verificado en runtime — **2026-09-23**: sobre una base `visionarios` limpia en PostgreSQL 18, Flyway aplicó las **6 migraciones** (`v6`), `ddl-auto: validate` pasó, Tomcat escuchó en 8089 y `GET /api/ping` devolvió **200** con el sobre `ApiResponse` esperado (`{"success":true,"data":{"status":"ok",...},"error":null}`). Nota: `GET /actuator/health` responde **401** — el ítem de arriba dice "Actuator health expuesto" y hoy está detrás de la seguridad; revisar si es a propósito.
- [ ] Despliegue real a `nolost-vps` (pendiente: dominio funcional definido primero).

## Etapa 1 — Red de Visionarios (dominio real)

- [x] Backend: `platform:security` (JWT) copiado y adaptado de `consulting`, sin `platform:tenancy`.
- [x] Backend: `modules:identity-access` — User, registro/login/forgot-password, `/api/users/me`.
- [x] Backend: `modules:events` — Event/EventRsvp, listado/detalle/RSVP toggle, CRUD admin.
- [x] Backend: `modules:materials` — Material filtrado por perfil, CRUD admin.
- [x] Backend: `modules:admin` — invitaciones de administrador (7 días, un solo uso), listado de usuarios con conteo por perfil.
- [x] Backend: `./gradlew build` compila con todos los módulos nuevos.
- [x] Frontend: pantallas y rutas de `DESIGN_SPEC.md` — **`npm run build` verificado 2026-09-22**: bundle de producción generado en 57 s, *initial* 299.69 kB (80.33 kB transferido), por debajo del presupuesto de aviso de 500 kB, con 23 chunks lazy separados por ruta (`home`, `login`, `register`, `agenda`, `evento-detalle`, `biblioteca`, `perfil`, `forgot-password`, `reset-password`, `accept-invite`, `admin-eventos`, `admin-material`, `admin-usuarios`, `admin-equipo`, …). Cero errores de compilación. Nota de entorno: el paso final de *inlining* de Google Fonts falla en el entorno del agente porque su proxy devuelve 403 a `fonts.googleapis.com`; se verificó con ese paso desactivado y `angular.json` quedó intacto. No afecta al build de Daniel ni al del VPS, que sí tienen salida a internet.
- [ ] Verificación end-to-end manual (Postgres local + backend + frontend corriendo juntos).
- [x] Reemplazar el logo de texto por el PNG real (ver `docs/DECISIONS.md`).
- [x] Envío real de correo (recuperar contraseña, invitación de administrador, invitación de evento) vía `contact-api`, con las plantillas reales del diseño (ver `docs/DECISIONS.md`).
- [ ] Despliegue a `nolost-vps` (pendiente: nada técnico lo bloquea, falta decisión de cuándo salir a producción).
