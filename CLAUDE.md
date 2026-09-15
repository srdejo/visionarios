# CLAUDE.md

Instrucciones de trabajo para Claude Code en este repositorio. Léelo antes de tocar cualquier archivo.

## Qué es este proyecto

`visionarios` es, por ahora, **solo un esqueleto técnico**: backend Spring Boot + frontend Angular + infra de despliegue, sin ninguna funcionalidad de negocio todavía. El dominio funcional (qué hace la app, para quién, qué entidades tiene) **no está definido** — no lo inventes ni lo asumas.

Monorepo con dos proyectos independientes:
- `backend/` — API en Java + Spring Boot, Gradle multi-módulo.
- `frontend/` — SPA en Angular.

## Primer paso real de este proyecto

Antes de escribir cualquier controlador, entidad o pantalla de negocio, el primer cambio debe salir de una propuesta OpenSpec (`opsx:propose`) que capture qué es "visionarios" en términos de producto. No implementes funcionalidad de negocio sin ese paso.

## Stack tecnológico

**Backend**
- Java 21 (toolchain), Spring Boot 3.3, Gradle multi-módulo con **Groovy DSL**.
- PostgreSQL + Spring Data JPA/Hibernate + Flyway.
- Módulos: `bootstrap`, `platform:web-common`, `modules:core` (placeholder, solo `/api/ping`).
- **No hay `platform:security` ni `platform:tenancy`** — decisión explícita YAGNI: no hay requerimiento de autenticación/multitenancy todavía porque no hay dominio definido. Ver `docs/DECISIONS.md`. Cuando el dominio lo requiera, replicar el patrón de `consulting`/`hotel` para esos módulos, no inventar uno nuevo.
- Testing: JUnit 5, Mockito, AssertJ.

**Frontend**
- Angular (standalone components, sin NgModules) + Tailwind CSS.

**Infraestructura**
- Sin Docker. Postgres nativo compartido (proyecto `infra/` en la raíz del workspace). Despliegue con systemd + nginx.
- **Dominio único** `https://visionarios.micasachurch.co/`: frontend estático en la raíz, `/api` proxeado al backend — sin subdominio `api-` separado (mismo patrón que `nolost`, ver `docs/DECISIONS.md`).
- Backend en el puerto 8089 (loopback), aún sin desplegar — ver `docs/DEPLOYMENT.md`.

## Convenciones de código

- **Todo el código en inglés** (clases, métodos, variables, nombres de tabla/columna en migraciones Flyway). **La documentación (`docs/`, `README.md`, este archivo) va en español**, igual que el resto del workspace.
- Paquetes en inglés técnico: `co.com.srdejo.visionarios.<modulo>`.
- Sin comentarios explicando QUÉ hace el código — comentarios solo para un PORQUÉ no obvio.
- No añadas abstracciones, entidades o endpoints para un dominio que no existe todavía. YAGNI estricto mientras no haya una propuesta OpenSpec aprobada.
- Spring Data JPA/Hibernate, no JdbcTemplate manual.

## Reglas que debes respetar

1. **No inventes dominio de negocio.** Ni entidades, ni controladores, ni pantallas más allá del placeholder `/api/ping`. El primer cambio de producto real pasa por `opsx:propose`.
2. **No agregues `platform:security`/`platform:tenancy` sin que el usuario lo pida explícitamente** — no hay requerimiento todavía.
3. **No marques nada como completado en `docs/PROGRESS.md` sin verificarlo** (build, test, o levantar el servicio real).
4. **No dupliques documentación** — referencia `docs/ARCHITECTURE.md`, `docs/DECISIONS.md` o `docs/DEPLOYMENT.md` en vez de repetir contenido.
5. **Cambios de alto impacto** (versión de Gradle/Spring Boot/Angular, tocar `infra/` compartida del workspace, `git push`) — confírmalos con el usuario antes de ejecutarlos.
6. No hagas commit de los cambios salvo que el usuario lo pida explícitamente.
