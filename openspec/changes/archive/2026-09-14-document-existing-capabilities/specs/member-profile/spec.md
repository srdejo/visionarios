## Purpose

Permite a un usuario autenticado ver sus propios datos de cuenta, cerrar sesión, y asegura que su nombre se muestre de forma legible en pantallas de ancho reducido.

## ADDED Requirements

### Requirement: Consulta de perfil propio
El sistema SHALL exponer (`GET /api/users/me`) y mostrar en la pantalla de perfil los datos del usuario autenticado: nombre completo, correo, celular, perfil, y fecha de creación de la cuenta. No es editable en el estado actual.

#### Scenario: Ver mi perfil
- **WHEN** un usuario autenticado abre la pantalla de perfil
- **THEN** el sistema muestra nombre, correo, celular, perfil y fecha de creación correspondientes a su propia cuenta

### Requirement: Cerrar sesión
El sistema SHALL permitir cerrar la sesión activa desde la pantalla de perfil, descartando el token JWT localmente y redirigiendo a login.

#### Scenario: Cerrar sesión
- **WHEN** el usuario confirma "Cerrar sesión" en su perfil
- **THEN** el cliente descarta el token guardado y redirige a `/login`; las pantallas protegidas dejan de ser accesibles hasta volver a iniciar sesión

### Requirement: Recorte visual del nombre en pantallas angostas
El sistema SHALL truncar visualmente con elipsis el nombre completo del usuario en el saludo de home y en el encabezado de perfil cuando no cabe en el ancho disponible, sin alterar el nombre almacenado ni el que se muestra en pantallas donde sí cabe completo.

#### Scenario: Nombre largo en pantalla angosta
- **WHEN** el nombre completo del usuario no cabe en el ancho disponible del contenedor del saludo (home) o del encabezado (perfil)
- **THEN** el sistema lo muestra recortado con elipsis, sin desbordar ni romper el layout

#### Scenario: Nombre corto no se recorta
- **WHEN** el nombre completo del usuario cabe en el ancho disponible
- **THEN** el sistema lo muestra completo, sin aplicar recorte
