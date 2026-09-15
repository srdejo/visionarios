## MODIFIED Requirements

### Requirement: Inicio de sesión
El sistema SHALL permitir iniciar sesión con correo y contraseña, verificando la contraseña contra el hash almacenado y emitiendo un JWT con el id de usuario y su rol si son correctos. El formulario de login SHALL mostrar un checkbox "Recordar contraseña", marcado por defecto, que controla si el navegador debe ofrecer recordar las credenciales ingresadas.

#### Scenario: Credenciales correctas
- **WHEN** el usuario ingresa un correo y contraseña que coinciden con una cuenta existente
- **THEN** el sistema emite un JWT válido y los datos del usuario autenticado

#### Scenario: Credenciales incorrectas
- **WHEN** el correo no existe o la contraseña no coincide con el hash almacenado
- **THEN** el sistema rechaza el inicio de sesión con el error "Correo o contraseña invalidos", sin distinguir cuál de los dos falló

#### Scenario: Checkbox "Recordar contraseña" marcado (por defecto)
- **WHEN** el usuario envía el formulario de login con el checkbox "Recordar contraseña" marcado
- **THEN** los campos de correo y contraseña usan los atributos `autocomplete` que permiten al navegador reconocer credenciales de login

#### Scenario: Checkbox "Recordar contraseña" desmarcado
- **WHEN** el usuario desmarca "Recordar contraseña" antes de enviar el formulario de login
- **THEN** los campos de correo y contraseña usan `autocomplete="off"`

#### Scenario: Login exitoso con "Recordar contraseña" marcado en un navegador compatible
- **WHEN** el login se completa exitosamente con el checkbox "Recordar contraseña" marcado y el navegador soporta la Credential Management API
- **THEN** el sistema invoca `navigator.credentials.store(...)` con el correo y la contraseña ingresados, para que el navegador ofrezca guardarlos

#### Scenario: Login exitoso con "Recordar contraseña" desmarcado, o navegador sin soporte
- **WHEN** el login se completa exitosamente y el checkbox "Recordar contraseña" está desmarcado, o el navegador no soporta la Credential Management API
- **THEN** el sistema no invoca `navigator.credentials.store(...)`, y el login continúa normalmente (la ausencia de soporte no genera error visible al usuario)
