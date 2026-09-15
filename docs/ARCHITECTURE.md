# ARCHITECTURE.md

Arquitectura real del scaffold — no aspiracional. Se actualiza cuando la estructura cambie de verdad.

## Backend (`backend/`)

Gradle multi-módulo, Groovy DSL, Java 21, Spring Boot 3.3.

```
backend/
├── bootstrap/            # único módulo con @SpringBootApplication, arranca el proceso
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/
│           ├── V1__init.sql                                  # placeholder original (ping_log)
│           └── V2__identity_events_materials_admin.sql       # dominio real
├── platform/
│   ├── web-common/       # ApiResponse, GlobalExceptionHandler, MessageResolver, logging — genérico
│   └── security/         # JWT (issue/parse), SecurityConfig, JwtAuthenticationFilter — copiado y adaptado de consulting/backend/platform/security, sin platform:tenancy (no es multi-tenant)
└── modules/
    ├── core/             # placeholder original: HealthController en /api/ping (se mantiene, sin protección)
    ├── identity-access/  # User (rol USER/ADMIN, perfil PROFESIONAL/EMPRENDEDOR/EMPRESARIO + campos por perfil), /api/auth/register|login|forgot-password, /api/users/me
    ├── events/           # Event + EventRsvp, /api/events, /api/events/{id}, /api/events/{id}/rsvp, /api/admin/events
    ├── materials/        # Material (filtrado por perfil visible), /api/materials, /api/admin/materials
    └── admin/            # AdminInvite (token, expira 7 días, un solo uso), /api/admin/invites, /api/auth/accept-invite/{token}, /api/admin/users (con conteo por perfil)
```

- `bootstrap` agrega todos los módulos anteriores y expone Actuator (`/actuator/health`).
- `platform:security`: JWT HMAC (`JwtService`), filtro `JwtAuthenticationFilter` (orden 10, sin `TenantContext` — a diferencia del original de `consulting`, aquí no hay noción de tenant), `SecurityConfig` con `/api/auth/**` público, `/api/admin/**` sólo `ROLE_ADMIN`, resto autenticado.
- `CurrentUser` (en `modules:identity-access`) resuelve el `userId` autenticado leyendo el `JwtClaims` del `SecurityContext` — lo usan `events` y `materials` sin acoplarse a Spring Security directamente.
- Perfiles nullable en `Event.targetProfile` y `Material.visibleProfile` significan "toda la red" (visible/aplica a todos).
- Persistencia: Postgres + Spring Data JPA/Hibernate + Flyway. `V1` se deja intacta (placeholder histórico); `V2` crea el esquema real (`users`, `events`, `event_rsvps`, `materials`, `admin_invites`).

## Frontend (`frontend/`)

Angular 22 (standalone components, generado con `ng new`) + Tailwind CSS 4.

```
frontend/
├── src/app/
│   ├── app.config.ts       # provideHttpClient (con interceptor JWT), provideRouter
│   ├── app.routes.ts       # /login, /registro, /olvide-password, /home, /biblioteca,
│   │                       # /eventos/:id, /perfil, /admin/eventos|material|usuarios|equipo
│   ├── core/                # AuthService, interceptor, authGuard, adminGuard
│   └── features/            # componentes standalone por pantalla (auth, home, biblioteca,
│                             # evento, perfil, admin/*)
├── proxy.conf.json         # /api -> http://localhost:8089 (dev)
├── tailwind.config.js       # paleta/tipografías de designdownloaded/DESIGN_SPEC.md
└── .postcssrc.json
```

Sin SSR (`--ssr=false`) — no hay landing pública que requiera SEO todavía. Ver el reporte del agente de frontend en esta misma sesión para la lista exacta de archivos creados.

## Infraestructura (`infra/`)

- `infra/nginx/visionarios.micasachurch.co.conf` — dominio único, frontend estático en la raíz, `/api` proxeado al backend en `localhost:8089`.
- `infra/visionarios.service` — unit systemd para correr el jar del backend.

## Puertos

- Backend: `8089` (dev y prod — ver `../PORTS.md` en la raíz del workspace).
- Frontend dev: `4200` (Angular CLI default).
