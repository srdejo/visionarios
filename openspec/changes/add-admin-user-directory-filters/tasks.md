## 1. Backend — esquema y dominio

- [x] 1.1 Crear `BusinessCategory.java` (enum: `SERVICIOS, COMIDA_RESTAURANTE, RETAIL_COMERCIO, TECNOLOGIA, SALUD, EDUCACION, CONSTRUCCION, OTRO`) en `modules/identity-access`, junto a `Profile.java`
- [x] 1.2 Crear migración `V5__user_birth_date_business_category.sql` agregando `birth_date DATE` y `business_category VARCHAR(32)` (ambas nullable, sin default) a `users`
- [x] 1.3 Agregar `birthDate` (`LocalDate`) y `businessCategory` (`BusinessCategory`) a `User.java` con getters/setters, y pasarlos en el constructor o setters igual que los campos por perfil existentes
- [x] 1.4 Agregar `birthDate`/`businessCategory` a `RegisterRequest` y `UserResponse`, y a `AuthService.applyProfileFields`/`register` para persistirlos; verificar con `./gradlew :modules:identity-access:test` (o el módulo que corresponda) que compila y los tests existentes siguen pasando

## 2. Frontend — modelos y registro

- [x] 2.1 Agregar `BusinessCategory` (union type) y `BUSINESS_CATEGORY_LABELS`, y los campos `birthDate`/`businessCategory` a `RegisterRequest`/`UserResponse` en `core/models.ts`
- [x] 2.2 Agregar el campo fecha de nacimiento al paso 1 del wizard (`register.html`/`.ts`), para los 3 perfiles
- [x] 2.3 Agregar el selector de categoría de negocio al paso 3 del wizard, solo para Emprendedor/Empresario
- [x] 2.4 Verificar con `npx ng build --configuration production` que el wizard compila y los nuevos campos viajan en el payload de registro

## 3. Frontend — directorio de admin

- [x] 3.1 Agregar helper `ageFrom(birthDate)` (o similar) en `core/models.ts` o en el propio componente, para calcular edad a partir de fecha de nacimiento
- [x] 3.2 En `admin-usuarios.ts`: agregar signals de búsqueda (texto) y filtros (perfil, categoría de negocio, rango de edad, rango de empleados), y un `computed` que combina todos sobre `result().users`, igual que el patrón de `biblioteca.ts`
- [x] 3.3 En `admin-usuarios.html`: agregar barra de búsqueda y chips de filtro (perfil, categoría, edad, empleados) sobre el listado, con estado vacío cuando no hay coincidencias
- [x] 3.4 Agregar bottom-sheet de detalle de usuario (reutilizar `BottomSheet`) que se abre al hacer clic en una fila, mostrando todos los campos del usuario seleccionado (incluida edad calculada y categoría de negocio), con manejo explícito de campos nulos ("No indicado")
- [x] 3.5 Verificar con `npx ng build --configuration production` que compila; revisar manualmente que los filtros combinados y la búsqueda funcionan sobre datos con `birthDate`/`businessCategory` nulos sin romper el listado
