# visionarios

Red de Visionarios: plataforma de una comunidad con registro y acceso por perfil, agenda de eventos con confirmación de asistencia, biblioteca de materiales filtrada por perfil y un panel de administración (eventos, materiales, equipo, usuarios e invitaciones). Ver `docs/ROADMAP.md` para el estado por etapa.

> El README describía este repo como un esqueleto sin dominio ni autenticación. Eso dejó de ser cierto con la Etapa 1 (ver `docs/ROADMAP.md`); corregido el 2026-09-22 contra `backend/settings.gradle` y `frontend/package.json`.

## Stack

- **Backend**: `backend/` — Java 21, Spring Boot 3.3.13, Gradle multi-módulo (Groovy DSL), Spring Data JPA/Hibernate, Flyway, PostgreSQL. Ocho subproyectos: `bootstrap`, `platform:web-common`, `platform:security` (JWT) y los módulos `core`, `identity-access`, `events`, `materials` y `admin`. **Sí hay autenticación** (JWT, adaptada de `consulting`); lo que no hay es `platform:tenancy`, porque esto es de un solo tenant — ver `docs/DECISIONS.md`.
- **Frontend**: `frontend/` — Angular 22 (standalone components) + Tailwind CSS 4. `npm run build` en verde (2026-09-22).
- **Infraestructura**: sin Docker — Postgres nativo compartido (`infra/` en la raíz del workspace), despliegue con systemd + nginx, dominio único `https://visionarios.micasachurch.co/` con `/api` proxeado (mismo patrón que `nolost`).

## Cómo correr en local

**Backend**
```bash
cd backend
./gradlew bootRun
```
Requiere una base Postgres `visionarios` (ver `infra/postgres` en la raíz del workspace) y las variables de `.env.example` en el entorno. Levanta en `http://localhost:8089`, healthcheck en `/actuator/health`, endpoint de prueba en `/api/ping`.

**Frontend**
```bash
cd frontend
npm install
npm start
```
Levanta en `http://localhost:4200` con proxy de `/api` hacia `http://localhost:8089` (ver `frontend/proxy.conf.json`).

## Documentación

- `CLAUDE.md` — reglas de trabajo (léelo antes de tocar código).
- `docs/ARCHITECTURE.md` — estructura real del scaffold.
- `docs/DECISIONS.md` — por qué se arrancó sin security/tenancy y con dominio único.
- `docs/DEPLOYMENT.md` — runbook de despliegue a `nolost-vps`.
- `docs/PROGRESS.md` / `docs/ROADMAP.md` — estado actual (esqueleto) y siguiente paso (definir dominio).
