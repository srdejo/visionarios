## 1. Login component

- [x] 1.1 En `login.ts`, agregar `rememberPassword = signal(true)` (o propiedad simple con `[(ngModel)]`) y un getter/computed que devuelva los valores de `autocomplete` a usar (`'username'`/`'current-password'` si está marcado, `'off'` si no) — verificar con `ng build` que compila sin errores.
- [x] 1.2 En `login.html`, enlazar `[attr.autocomplete]` en los inputs de correo y contraseña al valor calculado en 1.1 — verificar inspeccionando el DOM renderizado que el atributo cambia al marcar/desmarcar el checkbox.
- [x] 1.3 Agregar el checkbox "Recordar contraseña" (marcado por defecto) en el formulario, con estilo Tailwind consistente con el resto del login (`text-muted-1`, `font-oswald` donde aplique) — verificar visualmente en el navegador en mobile y desktop.
- [x] 1.4 En `login.ts`, tras un login exitoso (`res.success`) y si `rememberPassword()` está marcado, invocar `navigator.credentials.store(new (window as any).PasswordCredential({ id: this.email, password: this.password }))` (guardado tras `if ('credentials' in navigator && 'PasswordCredential' in window)`), sin bloquear la navegación a `/home` si falla — verificar con `ng build` que compila y que el `catch`/chequeo de soporte no rompe el flujo cuando el navegador no soporta la API.

## 2. Verificación manual

- [ ] 2.1 Levantar el frontend en Chrome/Edge, hacer login con el checkbox marcado y confirmar que el navegador ofrece guardar la contraseña — verificar observando el prompt nativo del navegador.
- [ ] 2.2 Repetir con el checkbox desmarcado y confirmar que el navegador no ofrece guardar la contraseña para ese envío — verificar observando que no aparece el prompt nativo.
- [x] 2.3 Confirmar que el flujo de "Olvidé mi contraseña" (`/olvide-password`) sigue funcionando sin cambios — verificado por inspección: `app.routes.ts` y el componente `forgot-password` no fueron modificados por este cambio.
