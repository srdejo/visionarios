## 1. Verificación de exactitud

- [x] 1.1 Confirmar que cada requirement de `specs/user-auth/spec.md` corresponde a un endpoint/comportamiento real en `modules:identity-access` y `modules:admin` (verificado por lectura de código: `AuthController`, `AuthService`, `AcceptInviteController`, `AdminInviteService`)
- [x] 1.2 Confirmar que `specs/member-profile/spec.md` corresponde a `UserController`, `pages/perfil`, `pages/home` (verificado por lectura de código)
- [x] 1.3 Confirmar que `specs/events/spec.md` corresponde a `EventController`, `AdminEventController`, `EventService` (verificado por lectura de código)
- [x] 1.4 Confirmar que `specs/materials/spec.md` corresponde a `MaterialController`, `AdminMaterialController`, `MaterialService` (verificado por lectura de código)
- [x] 1.5 Confirmar que `specs/admin-team/spec.md` corresponde a `AdminInviteController`, `AdminInviteService`, `AdminInvite` (verificado por lectura de código, expiración de 7 días confirmada en `AdminInvite`)
- [x] 1.6 Confirmar que `specs/admin-directory/spec.md` corresponde a `AdminUsersController`, `AdminUsersService` (verificado por lectura de código)

## 2. Archivo del cambio

- [ ] 2.1 Archivar este cambio con `openspec archive document-existing-capabilities`, publicando las 6 specs como línea base en `openspec/specs/`
- [ ] 2.2 Verificar con `openspec list --specs` que las 6 capacidades quedan registradas como specs principales
