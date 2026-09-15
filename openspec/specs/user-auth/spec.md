# user-auth Specification

## Purpose
Permite a una persona crear una cuenta en Red de Visionarios, identificarse con esa cuenta, recuperar el acceso si olvida su contraseña, y unirse como administrador mediante invitación, estableciendo la sesión JWT que el resto de la plataforma requiere.

## Requirements

### Requirement: Registro en wizard de 4 pasos
El sistema SHALL permitir crear una cuenta nueva (rol `USER`) mediante un wizard de 4 pasos: datos personales (nombre completo, celular, correo, contraseña), selección de perfil (Profesional/Emprendedor/Empresario), campos específicos de perfil, y confirmación.

#### Scenario: Registro exitoso
- **WHEN** el usuario completa los 3 primeros pasos y confirma la creación de cuenta con un correo no registrado
- **THEN** el sistema crea la cuenta con rol `USER`, emite un token de sesión, y avanza al paso 4 de confirmación

#### Scenario: Correo ya registrado
- **WHEN** el usuario confirma la creación de cuenta con un correo que ya existe
- **THEN** el sistema rechaza la creación con el error "Ya existe una cuenta con ese correo" y no avanza al paso 4

### Requirement: Campos dinámicos por perfil y checkbox de búsqueda de empleo
El sistema SHALL solicitar en el paso 3 los campos correspondientes al perfil elegido: Profesional (profesión, años de experiencia, empresa actual, checkbox "actualmente en búsqueda de empleo"), Emprendedor (producto o negocio, tiempo operando), Empresario (empresa, número de empleados, años con la empresa). Ninguno de estos campos es obligatorio a nivel de backend.

#### Scenario: Checkbox de búsqueda de empleo deshabilita empresa actual
- **WHEN** el usuario con perfil Profesional marca el checkbox "Actualmente en búsqueda de empleo" en el formulario
- **THEN** el campo "Empresa actual" se deshabilita en el formulario y su valor se limpia antes de enviarse

#### Scenario: Desmarcar el checkbox habilita el campo de nuevo
- **WHEN** el usuario desmarca el checkbox después de haberlo marcado
- **THEN** el campo "Empresa actual" vuelve a estar habilitado y editable, vacío

### Requirement: Inicio de sesión
El sistema SHALL permitir iniciar sesión con correo y contraseña, verificando la contraseña contra el hash almacenado y emitiendo un JWT con el id de usuario y su rol si son correctos.

#### Scenario: Credenciales correctas
- **WHEN** el usuario ingresa un correo y contraseña que coinciden con una cuenta existente
- **THEN** el sistema emite un JWT válido y los datos del usuario autenticado

#### Scenario: Credenciales incorrectas
- **WHEN** el correo no existe o la contraseña no coincide con el hash almacenado
- **THEN** el sistema rechaza el inicio de sesión con el error "Correo o contraseña invalidos", sin distinguir cuál de los dos falló

### Requirement: Recuperar y restablecer contraseña
El sistema SHALL permitir solicitar un enlace de restablecimiento de contraseña por correo, y usarlo una única vez dentro de su ventana de validez para establecer una nueva contraseña.

#### Scenario: Solicitud con correo existente
- **WHEN** el usuario solicita recuperar contraseña con un correo que existe en el sistema
- **THEN** el sistema genera un token de restablecimiento de un solo uso y envía por correo un enlace `/restablecer/{token}`

#### Scenario: Solicitud con correo inexistente
- **WHEN** el usuario solicita recuperar contraseña con un correo que no existe
- **THEN** el sistema responde con un error indicando que no existe una cuenta con ese correo (el backend no oculta la existencia del correo en esta operación)

#### Scenario: Restablecer con token válido
- **WHEN** el usuario envía una nueva contraseña junto con un token de restablecimiento no usado y no vencido
- **THEN** el sistema actualiza la contraseña del usuario asociado y marca el token como usado

#### Scenario: Restablecer con token usado o vencido
- **WHEN** el usuario intenta restablecer la contraseña con un token ya usado o vencido
- **THEN** el sistema rechaza la operación sin cambiar la contraseña

### Requirement: Aceptación de invitación de administrador
El sistema SHALL permitir que el destinatario de una invitación de administrador (ver capacidad `admin-team`) complete su registro con rol `ADMIN` a través del enlace recibido, mientras la invitación siga vigente y sin usar. Este endpoint es público (no requiere sesión previa), ya que quien hace clic en el correo todavía no tiene una sesión en ese navegador.

#### Scenario: Aceptar invitación válida
- **WHEN** el destinatario completa el formulario de aceptación (nombre, celular, contraseña, perfil) con un token no usado, no vencido, y un correo aún no registrado
- **THEN** el sistema crea la cuenta con rol `ADMIN`, marca la invitación como usada, y emite un JWT de sesión

#### Scenario: Invitación usada, vencida, o correo ya registrado
- **WHEN** se intenta aceptar una invitación ya usada, vencida, o cuyo correo ya tiene una cuenta
- **THEN** el sistema rechaza la operación y no crea ninguna cuenta

### Requirement: Protección de rutas por sesión y rol
El sistema SHALL exigir un JWT válido para acceder a cualquier endpoint fuera de `/api/auth/**` y `/api/mail/unsubscribe/**`, y SHALL restringir los endpoints de administración (`/api/admin/**`) a usuarios con rol `ADMIN`.

#### Scenario: Petición sin token a endpoint protegido
- **WHEN** una petición sin JWT válido llega a un endpoint distinto de `/api/auth/**` o `/api/mail/unsubscribe/**`
- **THEN** el sistema responde 401 sin ejecutar la operación

#### Scenario: Usuario sin rol admin en endpoint de administración
- **WHEN** un usuario autenticado con rol `USER` llama a un endpoint bajo `/api/admin/**`
- **THEN** el sistema responde 403 y no ejecuta la operación
