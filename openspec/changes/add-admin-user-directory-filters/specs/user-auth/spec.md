## MODIFIED Requirements

### Requirement: Registro en wizard de 4 pasos
El sistema SHALL permitir crear una cuenta nueva (rol `USER`) mediante un wizard de 4 pasos: datos personales (nombre completo, celular, correo, contraseña, fecha de nacimiento), selección de perfil (Profesional/Emprendedor/Empresario), campos específicos de perfil, y confirmación.

#### Scenario: Registro exitoso
- **WHEN** el usuario completa los 3 primeros pasos y confirma la creación de cuenta con un correo no registrado
- **THEN** el sistema crea la cuenta con rol `USER`, emite un token de sesión, y avanza al paso 4 de confirmación

#### Scenario: Correo ya registrado
- **WHEN** el usuario confirma la creación de cuenta con un correo que ya existe
- **THEN** el sistema rechaza la creación con el error "Ya existe una cuenta con ese correo" y no avanza al paso 4

#### Scenario: Fecha de nacimiento no es obligatoria a nivel de backend
- **WHEN** el usuario completa el registro sin indicar fecha de nacimiento
- **THEN** el sistema crea la cuenta igualmente, con `birthDate` nulo

### Requirement: Campos dinámicos por perfil y checkbox de búsqueda de empleo
El sistema SHALL solicitar en el paso 3 los campos correspondientes al perfil elegido: Profesional (profesión, años de experiencia, empresa actual, checkbox "actualmente en búsqueda de empleo"), Emprendedor (producto o negocio, tiempo operando, categoría de negocio), Empresario (empresa, número de empleados, años con la empresa, categoría de negocio). Ninguno de estos campos es obligatorio a nivel de backend.

#### Scenario: Checkbox de búsqueda de empleo deshabilita empresa actual
- **WHEN** el usuario con perfil Profesional marca el checkbox "Actualmente en búsqueda de empleo" en el formulario
- **THEN** el campo "Empresa actual" se deshabilita en el formulario y su valor se limpia antes de enviarse

#### Scenario: Desmarcar el checkbox habilita el campo de nuevo
- **WHEN** el usuario desmarca el checkbox después de haberlo marcado
- **THEN** el campo "Empresa actual" vuelve a estar habilitado y editable, vacío

#### Scenario: Categoría de negocio solo aplica a Emprendedor y Empresario
- **WHEN** el usuario tiene perfil Emprendedor o Empresario
- **THEN** el paso 3 incluye un selector de categoría de negocio con las opciones Servicios, Comida/Restaurante, Retail/Comercio, Tecnología, Salud, Educación, Construcción, Otro

#### Scenario: Profesional no tiene categoría de negocio
- **WHEN** el usuario tiene perfil Profesional
- **THEN** el paso 3 no incluye el selector de categoría de negocio
