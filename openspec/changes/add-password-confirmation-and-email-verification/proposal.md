## Why

Cuentas falsas o con correos erróneos se están registrando en Red de Visionarios sin ninguna verificación, y errores de tipeo en la contraseña durante el registro o el restablecimiento no se detectan hasta que el usuario intenta iniciar sesión y falla. Ambos problemas se resuelven en la misma capacidad (`user-auth`) y comparten el mismo punto de entrada (el wizard de registro y el flujo de reset).

## What Changes

- **BREAKING**: El registro (`POST /api/auth/register`) ya no emite un JWT de sesión de inmediato. La cuenta se crea en estado no verificado, y el sistema responde indicando que se debe verificar el correo antes de iniciar sesión.
- Se agrega confirmación de contraseña (campo `confirmPassword`) al paso 1 del wizard de registro y al formulario de restablecer contraseña, validada en frontend (igualdad de campos antes de enviar) y en backend (rechazo si no coinciden).
- Al registrarse (no aplica a la aceptación de invitación de administrador), el sistema envía un correo con un enlace de verificación de un solo uso (`/verificar-correo/{token}`), válido por 24 horas, y el correo advierte que la cuenta será eliminada si no se verifica a tiempo.
- El login se bloquea para cuentas no verificadas, con un error explícito indicando que falta verificar el correo.
- Un job programado (nuevo `@Scheduled`, primero en el backend) elimina cada hora las cuentas no verificadas cuyo token de verificación venció sin usarse.
- Se agrega la ruta `verificar-correo/:token` en el frontend, que llama al nuevo endpoint público de verificación y confirma al usuario que ya puede iniciar sesión.

## Capabilities

### New Capabilities
(ninguna)

### Modified Capabilities
- `user-auth`: agrega confirmación de contraseña en registro y restablecimiento; agrega verificación de correo obligatoria post-registro (estado no verificado, envío de enlace, bloqueo de login, expiración de 24h, eliminación automática de cuentas no verificadas vencidas); modifica el escenario "Registro exitoso" para que ya no emita JWT de inmediato.

## Impact

- Backend `identity-access`: nuevo campo `verified`/`emailVerifiedAt` en `User`, nueva entidad `EmailVerificationToken` (mismo patrón que `PasswordResetToken`/`AdminInvite`), cambios en `AuthController`/`AuthService` (register, login, nuevo endpoint `POST /api/auth/verify-email/{token}`), nuevo `@Scheduled` cleanup job (requiere `@EnableScheduling`), nuevas DTOs con `confirmPassword`.
- Mail: nuevo método `sendEmailVerification` en `MailSender`/`ContactApiMailSender`, nueva plantilla `mail-templates/verify-email.html`.
- Frontend: `register.ts`/`register.html` y `reset-password.ts` agregan campo de confirmación reutilizando `app-password-input`; nueva página/ruta `verificar-correo/:token`; manejo del nuevo error de login "correo no verificado".
- Spec `openspec/specs/user-auth/spec.md` se actualiza vía delta. Existe un cambio en curso (`add-admin-user-directory-filters`) que también toca este spec; revisar solapamiento antes de archivar.
