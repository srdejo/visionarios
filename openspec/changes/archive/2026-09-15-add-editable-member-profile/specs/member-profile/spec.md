## MODIFIED Requirements

### Requirement: Consulta de perfil propio
El sistema SHALL exponer (`GET /api/users/me`) y mostrar en la pantalla de perfil todos los datos del usuario autenticado: nombre completo, correo, celular, perfil, fecha de nacimiento, fecha de creación de la cuenta, y los campos específicos de su perfil (Profesional: profesión, años de experiencia, empresa actual; Emprendedor: producto o negocio, tiempo operando, categoría de negocio; Empresario: empresa, número de empleados, años con la empresa, categoría de negocio). El sistema SHALL permitir actualizar (`PUT /api/users/me`) nombre completo, celular, fecha de nacimiento, y los campos específicos del perfil actual del usuario. El correo y el perfil (Profesional/Emprendedor/Empresario) no son editables en este requirement.

#### Scenario: Ver mi perfil
- **WHEN** un usuario autenticado abre la pantalla de perfil
- **THEN** el sistema muestra nombre, correo, celular, perfil, fecha de nacimiento, fecha de creación, y los campos específicos de su perfil, correspondientes a su propia cuenta

#### Scenario: Actualizar datos editables
- **WHEN** el usuario modifica nombre completo, celular, fecha de nacimiento, o algún campo específico de su perfil, y guarda
- **THEN** el sistema persiste los cambios y la pantalla de perfil (y cualquier otra superficie que muestre esos datos, como el saludo de home) refleja los valores actualizados

#### Scenario: Intento de editar correo o perfil
- **WHEN** el usuario intenta cambiar su correo o su perfil (Profesional/Emprendedor/Empresario) desde la pantalla de edición
- **THEN** el sistema no lo permite — esos campos se muestran de solo lectura y no forman parte del payload de actualización

#### Scenario: Actualizar sin completar campos opcionales
- **WHEN** el usuario guarda dejando en blanco campos no obligatorios (p. ej. fecha de nacimiento, años de experiencia)
- **THEN** el sistema persiste el cambio igualmente, sin exigir esos campos
