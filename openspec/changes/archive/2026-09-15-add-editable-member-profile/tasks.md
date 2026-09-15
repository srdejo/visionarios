## 1. Backend

- [x] 1.1 Crear `UpdateProfileRequest.java` (DTO en `modules/identity-access/dto`: `fullName`, `phone`, `birthDate`, y los campos por perfil — profession/yearsExperience/currentCompany, businessProduct/operatingTime, companyName/employeeCount/yearsWithCompany, businessCategory — sin `email`/`password`/`profile`)
- [x] 1.2 Refactorizar `AuthService.applyProfileFields` para poder reutilizarlo desde un nuevo método `updateProfile(UUID userId, UpdateProfileRequest request)` que aplique nombre/celular/fecha de nacimiento y los campos del perfil actual del usuario (usando `user.getProfile()`, no un perfil del payload), y devuelva `UserResponse.from(user)`
- [x] 1.3 Agregar `PUT /api/users/me` a `UserController`, usando `CurrentUser` para resolver el id y llamando a `AuthService.updateProfile`
- [x] 1.4 Verificar con `./gradlew :modules:identity-access:compileJava` (y `./gradlew compileJava` para toda la app) que compila sin errores

## 2. Frontend

- [x] 2.1 Agregar `UpdateProfileRequest` a `core/models.ts` (mismos campos que el DTO backend) y un método `updateProfile(payload)` en `AuthService` que llame a `PUT /api/users/me` y actualice `currentUser`/`localStorage` igual que `persistSession`
- [x] 2.2 Reescribir `perfil.ts`/`perfil.html`: formulario editable con nombre, celular, fecha de nacimiento, correo (solo lectura), perfil (solo lectura, como badge), y los campos específicos según `user().profile` (mismos labels que el wizard de registro paso 3); precargar los valores actuales del usuario; botón "Guardar cambios" con estado de guardado/error (mismo patrón que `admin-eventos`/`admin-material`)
- [x] 2.3 Verificar con `npx ng build --configuration production` que compila; confirmar que los campos que no aplican al perfil del usuario (p. ej. "Empresa actual" para un Emprendedor) no se muestran
