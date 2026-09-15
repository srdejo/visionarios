## Why

En el login actual (`login.html`), los campos de correo y contraseña no declaran `autocomplete`, por lo que el navegador no siempre ofrece guardar las credenciales, obligando al usuario a volver a escribirlas en cada sesión. Se pide un control "Recordar contraseña" para que el usuario decida si el navegador debe recordar sus credenciales.

## What Changes

- Agregar un checkbox "Recordar contraseña" al formulario de login, debajo de los campos de correo/contraseña.
- Cuando está marcado (comportamiento por defecto), los inputs de correo y contraseña usan `autocomplete="username"` / `autocomplete="current-password"`.
- Cuando está desmarcado, los inputs usan `autocomplete="off"` para reducir el autocompletado del navegador en ese intento de login.
- **Ajuste de diseño tras implementación inicial**: el login es una SPA (Angular `(ngSubmit)` con `HttpClient`, sin recarga de página ni submit nativo del `<form>`), por lo que los navegadores modernos (Chrome, Firefox) no detectan el login exitoso vía su heurística habitual y no ofrecen el diálogo nativo de guardar contraseña solo con `autocomplete`. Para que el checkbox realmente tenga efecto, tras un login exitoso y si `rememberPassword` está marcado, se invoca la Credential Management API (`navigator.credentials.store(new PasswordCredential({ id: email, password }))`) para pedirle explícitamente al navegador que guarde la credencial. Si el navegador no soporta la API (Safari, Firefox sin flag), la llamada se omite sin error: el checkbox marcado simplemente no logra ese efecto en esos navegadores, pero no rompe el login.
- No se persiste la preferencia del checkbox entre visitas ni se envía al backend: es un control puramente de UI/cliente.
- Sin cambios en el backend, en el JWT emitido, ni en la duración de la sesión (`AuthService` ya persiste el token en `localStorage` de forma indefinida; este cambio no toca esa lógica).

## Capabilities

### New Capabilities
(ninguna)

### Modified Capabilities
- `user-auth`: el requisito "Inicio de sesión" gana un control de UI para que el usuario decida si el navegador debe recordar sus credenciales de acceso.

## Impact

- `frontend/src/app/pages/auth/login/login.html`: nuevo checkbox y atributos `autocomplete` dinámicos en los inputs de correo/contraseña.
- `frontend/src/app/pages/auth/login/login.ts`: nuevo estado `rememberPassword` (booleano, por defecto `true`) que controla el valor de `autocomplete` y, tras un login exitoso, si se llama a `navigator.credentials.store(...)`.
- Sin cambios en `AuthService`, en el backend, ni en otras páginas de auth (`forgot-password`, `reset-password`, `registro`), que quedan fuera de alcance.
