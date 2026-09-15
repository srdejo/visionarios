## Why

La pantalla "Mi perfil" (`/perfil`) solo muestra 3 datos (perfil, celular, miembro desde) y es de solo lectura, aunque el registro captura muchos más campos (fecha de nacimiento, y los campos específicos por perfil: profesión, empresa, producto de negocio, categoría de negocio, etc.). Un usuario que se equivocó al registrarse, o cuyos datos cambiaron (cambió de celular, cambió de empresa), no tiene forma de corregirlos ni de ver lo que respondió.

## What Changes

- La pantalla `/perfil` muestra ahora todos los campos que el usuario respondió en el registro: nombre completo, correo (solo lectura), celular, perfil (solo lectura), fecha de nacimiento, y los campos específicos de su perfil (Profesional: profesión/años de experiencia/empresa actual; Emprendedor: producto o negocio/tiempo operando/categoría de negocio; Empresario: empresa/número de empleados/años con la empresa/categoría de negocio).
- El usuario puede editar y guardar: nombre completo, celular, fecha de nacimiento, y los campos específicos de su perfil actual.
- **No editables en este cambio**: correo (identificador de login) y perfil (Profesional/Emprendedor/Empresario) — cambiar el perfil implicaría cambiar qué campos aplican, y cambiar el correo implica verificación, ambos fuera de alcance.
- **Fuera de alcance**: cambio de contraseña desde esta pantalla — ya existe el flujo de "olvidé mi contraseña" para eso.
- Nuevo endpoint `PUT /api/users/me` para persistir la actualización.

## Capabilities

### New Capabilities
(ninguna)

### Modified Capabilities
- `member-profile`: el requirement "Consulta de perfil propio" cambia de solo-lectura a lectura+edición, mostrando todos los campos capturados en el registro (no solo perfil/celular/miembro desde).

## Impact

- **Backend**: `UserController` (`modules/identity-access`) gana `PUT /api/users/me`; nuevo `UpdateProfileRequest` DTO; nuevo método en `AuthService` (o un nuevo `ProfileService` si `AuthService` no es el lugar natural — decisión en design.md) para aplicar la actualización sobre la entidad `User` ya cargada por `CurrentUser`/`UserRepository`.
- **Frontend**: `perfil.ts`/`perfil.html` pasan de solo-lectura a un formulario editable (mismos campos que el wizard de registro paso 1 + paso 3, sin el paso de contraseña); `AuthService` gana un método `updateProfile(...)` que llama al nuevo endpoint y actualiza `currentUser`/`localStorage` igual que `persistSession`.
- **Sin cambios** en login, registro, invitaciones, ni en el resto de `user-auth`.
