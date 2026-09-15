## MODIFIED Requirements

### Requirement: Listado de usuarios con contadores por perfil
El sistema SHALL exponer (`GET /api/admin/users`) el listado completo de cuentas registradas (incluyendo tanto rol `USER` como `ADMIN`), con todos sus datos (incluida fecha de nacimiento y categoría de negocio cuando existan) junto con el total de cuentas y un contador por cada uno de los tres perfiles (Profesional, Emprendedor, Empresario).

#### Scenario: Ver directorio de usuarios
- **WHEN** un administrador consulta el listado de usuarios
- **THEN** el sistema devuelve todas las cuentas registradas con su perfil, y los contadores por perfil coinciden con la cantidad real de cuentas en cada uno

#### Scenario: Usuario sin fecha de nacimiento o categoría de negocio
- **WHEN** una cuenta registrada antes de este cambio no tiene `birthDate` ni `businessCategory`
- **THEN** el listado la incluye igualmente, con esos campos nulos

## ADDED Requirements

### Requirement: Detalle de usuario
El sistema SHALL permitir al administrador ver el detalle completo de un usuario del listado: nombre, correo, celular, perfil, fecha de nacimiento (y edad calculada a partir de ella), fecha de registro, y los campos específicos de su perfil (incluida la categoría de negocio para Emprendedor/Empresario).

#### Scenario: Ver detalle de un usuario
- **WHEN** un administrador selecciona un usuario del listado
- **THEN** el sistema muestra su detalle completo, incluyendo la edad calculada si tiene fecha de nacimiento registrada

#### Scenario: Usuario sin fecha de nacimiento en el detalle
- **WHEN** el usuario seleccionado no tiene `birthDate` registrada
- **THEN** el detalle indica que ese dato no fue provisto, sin mostrar una edad calculada

### Requirement: Búsqueda y filtros del directorio de usuarios
El sistema SHALL permitir al administrador buscar por texto (nombre o correo) y filtrar el listado por perfil, categoría de negocio, rango de edad, y rango de número de empleados, de forma combinable. La búsqueda y los filtros se resuelven sobre el listado ya cargado (`GET /api/admin/users`), sin round-trips adicionales al backend.

#### Scenario: Búsqueda por nombre o correo
- **WHEN** el administrador escribe un texto en la barra de búsqueda
- **THEN** el listado se reduce a los usuarios cuyo nombre o correo contiene ese texto (sin distinguir mayúsculas/minúsculas)

#### Scenario: Filtro por perfil
- **WHEN** el administrador selecciona un perfil (Profesional/Emprendedor/Empresario)
- **THEN** el listado se reduce a los usuarios con ese perfil; "Todos" quita el filtro

#### Scenario: Filtro por categoría de negocio
- **WHEN** el administrador selecciona una categoría de negocio
- **THEN** el listado se reduce a los usuarios (Emprendedor o Empresario) cuya `businessCategory` coincide; los usuarios sin esa categoría (incluido cualquier Profesional) quedan excluidos

#### Scenario: Filtro por rango de edad
- **WHEN** el administrador selecciona un rango de edad predefinido (18-25, 26-35, 36-45, 46-60, 60+)
- **THEN** el listado se reduce a los usuarios cuya edad calculada a partir de `birthDate` cae en ese rango; los usuarios sin `birthDate` quedan excluidos de cualquier filtro de edad activo

#### Scenario: Filtro por número de empleados
- **WHEN** el administrador selecciona un rango de número de empleados predefinido (1-10, 11-50, 51-200, 200+)
- **THEN** el listado se reduce a los usuarios con perfil Empresario cuyo `employeeCount` cae en ese rango; usuarios de otro perfil o sin `employeeCount` quedan excluidos

#### Scenario: Filtros combinados sin resultados
- **WHEN** la combinación de búsqueda y filtros activos no coincide con ningún usuario
- **THEN** el sistema muestra un estado vacío indicando que no hay usuarios para esos criterios, sin tratarlo como error
