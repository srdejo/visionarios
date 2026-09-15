# visionarios

Esqueleto técnico de un proyecto nuevo. **El dominio funcional todavía no está definido** — este repo es solo la base (backend + frontend + infra) lista para recibir la primera propuesta de producto real vía OpenSpec.

## Stack

- **Backend**: `backend/` — Java 21, Spring Boot 3.3, Gradle multi-módulo (Groovy DSL), Spring Data JPA/Hibernate, Flyway, PostgreSQL. Sin autenticación todavía (no hay `platform:security`/`platform:tenancy` — ver `docs/DECISIONS.md`).
- **Frontend**: `frontend/` — Angular (standalone components) + Tailwind CSS.
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
