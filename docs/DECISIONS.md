# DECISIONS.md

Decisiones técnicas tomadas y por qué.

## 2026-09-14 — Arrancar sin `platform:security` ni `platform:tenancy`

El usuario pidió la base técnica de "visionarios" sin haber definido todavía el dominio funcional del proyecto (qué hace, quién lo usa, si hay roles o multitenancy). Agregar autenticación, autorización o aislamiento por tenant ahora sería inventar requerimientos que no existen — YAGNI.

Consecuencia: `modules:core` expone `/api/ping` sin protección alguna. Superado por la decisión siguiente una vez se definió el dominio (Red de Visionarios).

## 2026-09-14 — Dominio funcional definido: Red de Visionarios, con `platform:security` (JWT) copiado de `consulting`, sin `platform:tenancy`

El diseño (`designdownloaded/DESIGN_SPEC.md`, destilado a mano de un prototipo de Claude Design) definió el dominio: usuarios con rol `USER`/`ADMIN` y perfil `PROFESIONAL`/`EMPRENDEDOR`/`EMPRESARIO`, eventos con RSVP, material filtrado por perfil, e invitaciones de administrador. No es multi-tenant (una sola organización, Mi Casa Church) — por eso se replicó el patrón de JWT de `consulting/backend/platform/security` (adaptado al paquete `co.com.srdejo.visionarios`, sin `TenantContext`) y NO se trajo `platform:tenancy`.

Módulos nuevos: `modules:identity-access` (User + auth), `modules:events` (Event/EventRsvp), `modules:materials` (Material), `modules:admin` (AdminInvite + listado de usuarios con conteo por perfil). `modules:core` y su `HealthController` se dejaron intactos como placeholder de verificación, sin integrarlos al dominio.

## 2026-09-14 — Logo como texto estilizado (pendiente reemplazar por asset real)

El archivo de diseño original (`Red de Visionarios.dc.html`) y los tres logos PNG (`logo-mi.png`, `logo-mi-white.png`, `logo-mi-crop.png`) no pudieron persistirse en este entorno local: algo (probablemente antivirus/EDR con heurística anti-phishing, dado que el prototipo tiene forma de formulario de login+contraseña con JS embebido) los borra en cuanto se escriben a disco, de forma silenciosa y repetible — incluso un PNG vacío de 1x1 desaparece, mientras que un `.md` con el mismo contenido persiste sin problema.

Por eso el frontend implementaba el logo como texto estilizado ("MI CASA" en Oswald bold) en vez de una imagen.

**Resuelto el 2026-09-14**: el usuario consiguió los PNG por otro medio (descargados manualmente, no por esta sesión) y los puso en `frontend/public/{logo-mi.png,logo-mi-white.png,logo-mi-crop.png}`. Se reemplazó el texto por `<img>` en login (hero oscuro, `logo-mi-white.png`), home (header oscuro, `logo-mi-white.png`) y la confirmación de registro paso 4 (fondo claro, `logo-mi-crop.png`, el emblema sin wordmark). También se generó `frontend/public/favicon.ico` desde `logo-mi-crop.png` (recorte cuadrado 16/32/48px con `sharp`, tomado prestado de `contact/node_modules` — no es dependencia del frontend) porque el que traía el esqueleto de Angular era el genérico.

## 2026-09-14 — "Olvidé mi contraseña" sin envío real de correo

El prototipo de diseño no implementa el envío del enlace de restablecimiento (solo pide el correo y no muestra pantalla de confirmación). Se implementó `POST /api/auth/forgot-password` validando que el correo exista, con un `TODO` explícito en `AuthService.forgotPassword()` para la integración real de envío — no se inventó un flujo de correo que el diseño no especificó (YAGNI).

## 2026-09-14 — Dominio único en vez de subdominio `api-` separado

Para el despliegue en producción se eligió el patrón de `nolost.micasachurch.co`: un solo dominio (`visionarios.micasachurch.co`) sirviendo el frontend estático en la raíz y proxeando `/api` al backend por nginx, en vez del patrón de dos hosts (`hotel.srdejo.com.co` + `api-hotel.srdejo.com.co`).

Razones:
- Un solo certificado SSL que gestionar (Certbot).
- El frontend usa rutas `/api` relativas, sin necesidad de configurar CORS ni exponer el backend en un host público aparte.
- Es el patrón más reciente y simple del workspace para proyectos sin necesidad de separar dominios (landing vs. API vs. admin), que no es el caso aquí.

## 2026-09-14 — Puerto de backend 8089

Siguiente puerto libre registrado en `../PORTS.md` en el momento de crear el proyecto. Reservado ahí como "esqueleto creado, backend aún no desplegado".

## 2026-09-14 — Imagen OG generada renderizando el `.dc.html` con Edge headless, no a mano

El diseño de `OG Red de Visionarios.dc.html` (1200x630, fuentes Oswald/Sacramento/Barlow, los dos logos como imágenes) se leyó completo con `DesignSync`. En vez de recrear el layout como código de producción (no hay ninguna vista de la app que necesite este layout — es una imagen estática de una sola vez), se reconstruyó el HTML del `.dc.html` con los PNG embebidos como `data:` URIs y se le tomó una captura con `msedge --headless --window-size=1200,630 --screenshot` a un archivo local (no al deploy real, que todavía no existe). El resultado quedó en `frontend/public/og.png` y las meta tags `og:*`/`twitter:card` en `frontend/src/index.html` apuntan a `https://visionarios.micasachurch.co/og.png` (el dominio de producción, aunque el sitio no esté desplegado ahí todavía — así queda correcto sin tocarlo de nuevo al desplegar).

Nota: el `file://` a través de Bash falló la primera vez porque la ruta del proyecto tiene espacios (`01-activos`) sin escapar en la URL; se resolvió renderizando desde el directorio de scratchpad (sin espacios) en vez de arreglar el escapado.

## 2026-09-14 — Correos transaccionales via contact-api, plantillas del diseño real

El proyecto de Claude Design sí pudo leerse completo en esta sesión (a diferencia de la vez anterior — ver la decisión de "Logo como texto estilizado"), incluyendo los tres correos: `Correo - Invitacion a evento.html`, `Correo - Invitacion administrador.html`, `Correo - Recuperar contrasena.html`. Se llevaron tal cual (mismo HTML tabular a prueba de clientes de correo) a `platform/web-common/src/main/resources/mail-templates/` con `{{placeholders}}` para las partes dinámicas, y se agregó `MailSender`/`ContactApiMailSender` en `platform:web-common` (mismo patrón que `ContactApiPasswordResetMailSender` de `micasachurch`): llama a `POST /api/send` del microservicio `contact` en vez de hablar SMTP directo.

Esto reemplaza el TODO de "Olvidé mi contraseña sin envío real de correo": se agregó `password_reset_tokens` (mismo patrón de un solo uso + vencimiento que `admin_invites`, 60 minutos) y el endpoint `POST /api/auth/reset-password`. También se conectó el envío en `AdminInviteService.create()` (ya tenía el token, solo faltaba mandarlo) y se agregó `POST /api/admin/events/{id}/notify` para que un admin dispare manualmente el correo de invitación a los usuarios a los que aplica el evento (por perfil o toda la red) — el diseño no especifica un envío automático al crear el evento, así que se dejó como acción explícita del admin en vez de inventar ese comportamiento (YAGNI).

Los logos PNG siguen sin persistirse (mismo problema de entorno local ya documentado); los correos no los necesitan porque el diseño real usa el wordmark en texto ("MI CASA CHURCH") en el header, no una imagen.

**Corregido el 2026-09-14**: el pie de los 3 correos traía datos de relleno del mockup original (dominio `redvisionarios.micasa.org`, dirección "Calle 00 #00-00, Bogotá, Colombia", `hola@micasa.org`) que nunca fueron reales. Se reemplazaron por los datos reales: dominio `visionarios.micasachurch.co`, dirección "Barrio La Primavera, diagonal a la Defensa Civil, Ocaña, Colombia", y el contacto de `invite-admin.html` ahora es WhatsApp `+57 304 533 2589` (mismo número que usa micasachurch) en vez de un correo que no existe. Esto es independiente del enlace dinámico de cada correo ({{link}}, botón principal), que ya se arma con `APP_PUBLIC_URL` — se documentó en `docs/DEPLOYMENT.md` que en producción debe ser `https://visionarios.micasachurch.co` (si no, cae al default de desarrollo `localhost:4200`).

**"Darme de baja" — implementado de verdad (no solo quitado del pie) el 2026-09-14**: el pie original tenía el link, pero apuntaba a una página que no existía. En vez de borrarlo, se construyó la funcionalidad: `users` ganó `email_opt_out` (bool) y `unsubscribe_token` (string opaco, generado una vez en el constructor de `User`, igual que el resto de tokens de la app — migración `V4__email_preferences.sql`). Nuevo endpoint público `POST /api/mail/unsubscribe/{token}` (`MailPreferencesController`/`MailPreferencesService` en `identity-access`, permitido en `SecurityConfig` sin login porque quien hace clic en el correo no tiene sesión en ese navegador) y una página frontend `darme-de-baja/:token` con botón de confirmación explícito (no un GET de un solo clic — los escáneres de seguridad de los clientes de correo a veces "previsitan" los links de los correos y darían de baja a todo el mundo por accidente). `EventService.notify()` ahora filtra a los usuarios con `emailOptOut=true` antes de mandar.

Solo se agregó el link de baja en `event-invitation.html` — es el único de los 3 correos que es tipo boletín/broadcast. `reset-password.html` e `invite-admin.html` son transaccionales (el propio usuario disparó la acción al pedir el enlace o al recibir una invitación puntual); no tiene sentido "darse de baja" de un correo que uno mismo pidió, así que ahí se dejó solo el link al dominio, sin opción de baja — mismo criterio que usan los proveedores de correo transaccional (Resend, SES, etc.) para no exigir el header de unsubscribe en ese tipo de envío.

## 2026-09-14 — Bootstrap del primer admin vía `ADMIN_BOOTSTRAP_EMAIL`

Al revisar cómo se crea el primer administrador se encontró un huevo-y-gallina real en el código: `POST /api/admin/invites` (la única forma de crear un admin) requiere `ROLE_ADMIN`, y `POST /api/auth/register` siempre crea `Role.USER` — sin un admin ya existente no había forma de invitar al primero desde la API.

En vez de un endpoint especial (superficie de ataque innecesaria — cualquiera podría intentar auto-promoverse) o una migración con credenciales hardcodeadas (mala práctica, y quedaría en el historial de Flyway para siempre), se agregó `AdminBootstrapRunner`: un `ApplicationRunner` que al arrancar, si `ADMIN_BOOTSTRAP_EMAIL` apunta a un usuario que ya se registró normal, lo promueve a `ADMIN` (idempotente — no hace nada si ya es admin o si el usuario no existe todavía). El flujo real es: registrarse en `/registro`, setear la variable, reiniciar una vez, quitarla de nuevo. De ahí en adelante los siguientes admins se invitan desde `/admin/equipo` sin volver a tocar el `.env` ni la base de datos.
