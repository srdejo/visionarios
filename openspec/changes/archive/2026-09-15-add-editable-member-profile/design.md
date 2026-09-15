## Context

`UserController` (`backend/modules/identity-access/.../UserController.java`) hoy solo tiene `GET /api/users/me`, resuelto vía `CurrentUser` (id del JWT) + `UserRepository`. `User.java` ya tiene setters para todos los campos relevantes (`setBirthDate`, `setBusinessCategory`, `setProfession`, etc. — agregados en `add-admin-user-directory-filters`), así que no hace falta tocar la entidad. `AuthService.applyProfileFields(User, RegisterRequest)` ya tiene el patrón exacto de "aplicar campos según perfil" que este cambio reutiliza para la edición. En frontend, `perfil.ts`/`perfil.html` son de solo lectura; `AuthService.persistSession` ya tiene el patrón de "actualizar `currentUserSignal` + `localStorage`" que `updateProfile` reutiliza.

## Goals / Non-Goals

- Goal: exponer todos los campos ya capturados en el registro en la pantalla de perfil, y permitir editar los que tiene sentido editar (todo excepto correo y perfil).
- Non-goal: cambiar de perfil (Profesional↔Emprendedor↔Empresario) — cambiaría qué campos aplican; no se pidió.
- Non-goal: cambiar correo — implica verificación, no está en alcance.
- Non-goal: cambiar contraseña desde esta pantalla — ya existe "olvidé mi contraseña".

## Decisions

- **`PUT /api/users/me` vive en `UserController`** (no en `AuthController`), junto al `GET` existente — ambos operan sobre "mi propia cuenta" vía `CurrentUser`, mientras que `AuthController` cubre operaciones sin sesión previa (login/registro/recuperar contraseña) o de alta de cuenta (invitaciones). Mantiene la separación ya existente en el código real.
- **Nuevo método en `AuthService`** (`updateProfile(UpdateProfileRequest)`), no un servicio nuevo — reutiliza `applyProfileFields` (se hace `protected`/paquete-visible o se extrae a un método estático reutilizable) y evita duplicar la lógica de "qué campos aplican según perfil" que ya existe para el registro. Alternativa descartada: `ProfileService` nuevo — innecesario, `AuthService` ya es donde vive toda la lógica de mutación de `User`.
- **`UpdateProfileRequest` como DTO separado de `RegisterRequest`**, sin `password`/`email`/`profile` — evita que el endpoint de actualización pueda aceptar (aunque se ignore) esos campos, y deja explícito en la firma qué es editable.
- **El perfil (Profesional/Emprendedor/Empresario) del usuario determina qué campos del payload se aplican**, igual que en `applyProfileFields` — si el usuario envía campos de un perfil que no es el suyo, se ignoran (no se usa el perfil del payload, se usa `user.getProfile()` ya persistido).
- **Frontend: un solo formulario editable, no vista+edición separada** — la pantalla de perfil siempre muestra inputs editables para los campos permitidos (nombre, celular, fecha de nacimiento, campos por perfil) y de solo lectura para correo/perfil, con un botón "Guardar cambios"; no hay un modo "solo lectura" previo con botón "Editar" — simplifica el componente y es consistente con que la mayoría de estos campos son opcionales y de bajo riesgo. Alternativa descartada: toggle ver/editar — añade estado sin beneficio claro, ningún otro formulario de la app lo usa.

## Risks / Trade-offs

- [Reutilizar `applyProfileFields` para registro Y actualización podría acoplar de más los dos flujos si en el futuro divergen] → Es un método pequeño y ambos casos ya comparten exactamente la misma regla ("qué campos aplican según perfil"); si divergen, se separan entonces (YAGNI).
- [Un `PUT` con body parcial podría interpretarse como "borrar" campos no enviados] → El contrato es reemplazo completo de los campos editables (como ya hace `AdminEventController`/`AdminMaterialController` con sus `PUT`), no parcial — el frontend siempre envía el estado completo del formulario, precargado con los valores actuales.

## Migration Plan

No aplica — no hay cambios de esquema de base de datos en este cambio (reutiliza columnas ya existentes). Es aditivo a nivel de API (`PUT /api/users/me` nuevo) y de UI; desplegar backend y frontend juntos como con cualquier otro cambio de esta app.
