# DEPLOYMENT.md

**Estado: subdominio + SSL en producción, app aún no desplegada.** Puerto reservado en `../PORTS.md` (8089). Modelado en el patrón de dominio único de `nolost.micasachurch.co` (ver `docs/DECISIONS.md`). El proyecto ya está wireado en `infra/deploy.ps1` (`-Projects visionarios`) para los despliegues normales de aquí en adelante — los pasos manuales de abajo son el runbook de aprovisionamiento inicial (ya ejecutados los pasos 2 y 3).

- [x] Paso 2 — vhost de nginx subido y habilitado (2026-09-14).
- [x] Paso 3 — certificado SSL emitido por Certbot, `https://visionarios.micasachurch.co` responde (2026-09-14, hoy da 500 porque falta desplegar la app — esperado).
- [ ] Paso 1 — base de datos Postgres (requiere password de sudo interactivo, no se pudo automatizar desde esta sesión).
- [ ] Paso 4 — backend (jar + `.env` + systemd) — requiere password de sudo interactivo para instalar el `.service`.
- [ ] Paso 5 — frontend (primer build + copia). A partir de esto, usar `infra/deploy.ps1 -Projects visionarios` en vez de los comandos manuales.

## Dominio

`https://visionarios.micasachurch.co/` — frontend estático en la raíz, `/api` proxeado al backend. Un solo vhost, un solo certificado SSL. Sin subdominio `api-` separado.

## 1. Base de datos Postgres en el VPS

```sql
CREATE DATABASE visionarios;
CREATE USER visionarios WITH PASSWORD '<password real>';
GRANT ALL PRIVILEGES ON DATABASE visionarios TO visionarios;
ALTER DATABASE visionarios OWNER TO visionarios;
```

Agregar también a `infra/postgres/init-databases.ps1` en la raíz del workspace (ya hecho al crear este scaffold, ver ese archivo).

## 2. Vhost de nginx

`/etc/nginx/sites-available/visionarios.micasachurch.co` — copiar el contenido de `infra/nginx/visionarios.micasachurch.co.conf` de este repo:

```bash
cat infra/nginx/visionarios.micasachurch.co.conf | ssh srdejo@nolost-vps "sudo tee /etc/nginx/sites-available/visionarios.micasachurch.co > /dev/null"
ssh srdejo@nolost-vps "sudo ln -sf /etc/nginx/sites-available/visionarios.micasachurch.co /etc/nginx/sites-enabled/visionarios.micasachurch.co"
ssh srdejo@nolost-vps "sudo nginx -t && sudo systemctl reload nginx"
```

## 3. Certificado SSL (Certbot)

```bash
ssh srdejo@nolost-vps "sudo certbot --nginx -d visionarios.micasachurch.co --non-interactive --agree-tos -m <email real>"
```

Certbot reescribe el vhost agregando el bloque `443 ssl` — mismo patrón que el resto del workspace.

## 4. Backend: jar + systemd

1. Build local: `cd backend && ./gradlew bootJar` → genera `bootstrap/build/libs/bootstrap-0.0.1-SNAPSHOT.jar`.
2. Copiar al VPS como `/home/srdejo/apps/visionarios/app.jar`.
3. Variables de entorno en `/home/srdejo/apps/visionarios/.env` (ver `.env.example` en la raíz de este repo):
   ```
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=visionarios
   DB_USER=visionarios
   DB_PASSWORD=<password real>
   SERVER_PORT=8089
   APP_PUBLIC_URL=https://visionarios.micasachurch.co
   CONTACT_API_URL=http://127.0.0.1:3000
   MAIL_FROM_NAME=Red de Visionarios
   ```
   `APP_PUBLIC_URL` es la base con la que se arman los enlaces de los correos (recuperar contraseña, invitación de administrador, invitación de evento) — sin esto en prod, los enlaces apuntarían a `localhost:4200`. `CONTACT_API_URL` asume que `contact` (ver `../contact`) corre en el mismo host.
4. Unit systemd: copiar `infra/visionarios.service` a `/etc/systemd/system/visionarios.service`:
   ```bash
   cat infra/visionarios.service | ssh srdejo@nolost-vps "sudo tee /etc/systemd/system/visionarios.service > /dev/null"
   ssh srdejo@nolost-vps "sudo systemctl daemon-reload && sudo systemctl enable --now visionarios"
   ```
5. Primer administrador: registrarse normal en `/registro`, luego agregar `ADMIN_BOOTSTRAP_EMAIL=ese-correo` al `.env`, reiniciar el servicio una vez (`sudo systemctl restart visionarios`) y quitarlo del `.env` de nuevo (ver `AdminBootstrapRunner`). De ahí en adelante los siguientes admins se invitan desde `/admin/equipo` sin volver a tocar el `.env`.

## 5. Frontend: build + copia

```bash
cd frontend && npm run build
scp -r dist/frontend/browser/* srdejo@nolost-vps:/home/srdejo/apps/visionarios/frontend/
```

(Ajustar la ruta de salida exacta si Angular cambia el layout de `dist/` — verificar con `ls dist/frontend` tras el build.)

## 6. Verificación

```bash
curl -I https://visionarios.micasachurch.co
curl https://visionarios.micasachurch.co/api/ping
ssh srdejo@nolost-vps "sudo systemctl status visionarios"
```

Confirmar que `/api/ping` responde `{"success":true,...}` y que el `.service` está `active (running)` antes de dar el despliegue por completo.
