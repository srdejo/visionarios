## MODIFIED Requirements

### Requirement: Registro en wizard de 4 pasos
El sistema SHALL permitir crear una cuenta nueva (rol `USER`) mediante un wizard de 4 pasos: datos personales (nombre completo, celular, correo, contraseña, confirmación de contraseña), selección de perfil (Profesional/Emprendedor/Empresario), campos específicos de perfil, y confirmación. La cuenta se crea en estado no verificado y el sistema envía un correo de verificación (ver Requirement: Verificación de correo tras el registro); el sistema NO emite un JWT de sesión en este paso.

#### Scenario: Registro exitoso
- **WHEN** el usuario completa los 3 primeros pasos con una contraseña y confirmación que coinciden, y confirma la creación de cuenta con un correo no registrado
- **THEN** el sistema crea la cuenta con rol `USER` en estado no verificado, envía un correo de verificación, no emite JWT, y avanza al paso 4 de confirmación indicando que debe verificar su correo para iniciar sesión

#### Scenario: Correo ya registrado
- **WHEN** el usuario confirma la creación de cuenta con un correo que ya existe
- **THEN** el sistema rechaza la creación con el error "Ya existe una cuenta con ese correo" y no avanza al paso 4

#### Scenario: Contraseña y confirmación no coinciden
- **WHEN** el usuario confirma la creación de cuenta con `password` y `confirmPassword` que no son iguales
- **THEN** el sistema rechaza la creación con un error indicando que las contraseñas no coinciden y no crea ninguna cuenta

### Requirement: Inicio de sesión
El sistema SHALL permitir iniciar sesión con correo y contraseña, verificando la contraseña contra el hash almacenado y emitiendo un JWT con el id de usuario y su rol si son correctos y la cuenta está verificada.

#### Scenario: Credenciales correctas
- **WHEN** el usuario ingresa un correo y contraseña que coinciden con una cuenta existente y verificada
- **THEN** el sistema emite un JWT válido y los datos del usuario autenticado

#### Scenario: Credenciales incorrectas
- **WHEN** el correo no existe o la contraseña no coincide con el hash almacenado
- **THEN** el sistema rechaza el inicio de sesión con el error "Correo o contraseña invalidos", sin distinguir cuál de los dos falló

#### Scenario: Credenciales correctas pero correo no verificado
- **WHEN** el usuario ingresa un correo y contraseña correctos, pero la cuenta aún no ha sido verificada
- **THEN** el sistema rechaza el inicio de sesión con un error indicando que debe verificar su correo antes de iniciar sesión, y no emite JWT

### Requirement: Recuperar y restablecer contraseña
El sistema SHALL permitir solicitar un enlace de restablecimiento de contraseña por correo, y usarlo una única vez dentro de su ventana de validez para establecer una nueva contraseña, confirmada dos veces.

#### Scenario: Solicitud con correo existente
- **WHEN** el usuario solicita recuperar contraseña con un correo que existe en el sistema
- **THEN** el sistema genera un token de restablecimiento de un solo uso y envía por correo un enlace `/restablecer/{token}`

#### Scenario: Solicitud con correo inexistente
- **WHEN** el usuario solicita recuperar contraseña con un correo que no existe
- **THEN** el sistema responde con un error indicando que no existe una cuenta con ese correo (el backend no oculta la existencia del correo en esta operación)

#### Scenario: Restablecer con token válido
- **WHEN** el usuario envía una nueva contraseña y su confirmación, iguales entre sí, junto con un token de restablecimiento no usado y no vencido
- **THEN** el sistema actualiza la contraseña del usuario asociado y marca el token como usado

#### Scenario: Restablecer con token usado o vencido
- **WHEN** el usuario intenta restablecer la contraseña con un token ya usado o vencido
- **THEN** el sistema rechaza la operación sin cambiar la contraseña

#### Scenario: Restablecer con contraseña y confirmación distintas
- **WHEN** el usuario envía una nueva contraseña y una confirmación que no coinciden entre sí, con un token válido
- **THEN** el sistema rechaza la operación con un error indicando que las contraseñas no coinciden y no cambia la contraseña

### Requirement: Protección de rutas por sesión y rol
El sistema SHALL exigir un JWT válido para acceder a cualquier endpoint fuera de `/api/auth/**` y `/api/mail/unsubscribe/**`, y SHALL restringir los endpoints de administración (`/api/admin/**`) a usuarios con rol `ADMIN`.

#### Scenario: Petición sin token a endpoint protegido
- **WHEN** una petición sin JWT válido llega a un endpoint distinto de `/api/auth/**` o `/api/mail/unsubscribe/**`
- **THEN** el sistema responde 401 sin ejecutar la operación

#### Scenario: Usuario sin rol admin en endpoint de administración
- **WHEN** un usuario autenticado con rol `USER` llama a un endpoint bajo `/api/admin/**`
- **THEN** el sistema responde 403 y no ejecuta la operación

## ADDED Requirements

### Requirement: Verificación de correo tras el registro
El sistema SHALL, al crear una cuenta mediante el registro normal (no aplica a la aceptación de invitación de administrador, que ya asume el correo válido), generar un token de verificación de un solo uso con vigencia de 24 horas y enviar un correo con un enlace `/verificar-correo/{token}` que además advierte que la cuenta será eliminada si no se verifica dentro de ese plazo.

#### Scenario: Correo de verificación enviado al registrarse
- **WHEN** se crea una cuenta nueva mediante el wizard de registro
- **THEN** el sistema genera un token de verificación de un solo uso, válido por 24 horas, y envía un correo al correo registrado con el enlace de verificación y la advertencia de eliminación

#### Scenario: Verificar con token válido
- **WHEN** el destinatario visita `/verificar-correo/{token}` con un token no usado y no vencido
- **THEN** el sistema marca la cuenta como verificada, marca el token como usado, y permite iniciar sesión a partir de ese momento

#### Scenario: Verificar con token usado o vencido
- **WHEN** el destinatario visita `/verificar-correo/{token}` con un token ya usado, vencido, o inexistente
- **THEN** el sistema rechaza la verificación y no marca la cuenta como verificada

#### Scenario: Invitación de administrador no requiere verificación de correo
- **WHEN** un destinatario acepta una invitación de administrador válida
- **THEN** la cuenta creada con rol `ADMIN` queda verificada de inmediato, sin token de verificación de correo adicional

### Requirement: Eliminación automática de cuentas no verificadas vencidas
El sistema SHALL ejecutar periódicamente un proceso que elimina toda cuenta cuyo estado siga sin verificar y cuyo token de verificación de correo haya vencido sin usarse.

#### Scenario: Cuenta no verificada tras 24 horas
- **WHEN** transcurren 24 horas desde la creación de una cuenta sin que su correo haya sido verificado
- **THEN** el proceso de limpieza elimina la cuenta y su token de verificación asociado

#### Scenario: Cuenta verificada a tiempo no se elimina
- **WHEN** una cuenta verifica su correo antes de que venza su token de verificación
- **THEN** el proceso de limpieza no elimina la cuenta, sin importar cuánto tiempo pase después
