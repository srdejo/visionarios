## Purpose

Permite a los usuarios descubrir y acceder al material (documentos, hojas de cálculo, audio) visible para su perfil, y a los administradores mantener el catálogo mediante enlaces a Google Drive.

## ADDED Requirements

### Requirement: Listado de material filtrado por perfil (servidor)
El sistema SHALL exponer (`GET /api/materials`) solo el material visible para "toda la red" (`visibleProfile = null`) o para el perfil del usuario autenticado, ordenado por fecha de creación descendente. El filtrado ocurre en el backend según el perfil del usuario que hace la petición, no en el cliente.

#### Scenario: Material visible para toda la red
- **WHEN** un material tiene `visibleProfile = null`
- **THEN** aparece en el listado de cualquier usuario autenticado, sin importar su perfil

#### Scenario: Material visible solo para un perfil
- **WHEN** un material tiene `visibleProfile` igual al perfil del usuario autenticado
- **THEN** aparece en su listado; si el perfil del material no coincide con el del usuario, no aparece

### Requirement: Acceso a un material
El sistema SHALL permitir abrir un material mediante su `driveUrl` registrado, en una pestaña nueva.

#### Scenario: Abrir material visible
- **WHEN** un usuario con acceso a un material selecciona "Abrir"/"Descargar"
- **THEN** el cliente abre `driveUrl` en una pestaña nueva

### Requirement: Administración de material
El sistema SHALL permitir a un administrador crear (`POST /api/admin/materials`) y editar (`PUT /api/admin/materials/{id}`) material: título, tipo (PDF/DOC/XLS/MP3), meta (descripción corta), URL de Drive, y perfil visible (uno o `null` para toda la red).

#### Scenario: Crear material
- **WHEN** un administrador envía título, tipo, meta, URL de Drive y perfil visible (o ninguno, para toda la red)
- **THEN** el sistema crea el material y queda visible según esa regla desde la siguiente consulta

#### Scenario: Editar material existente
- **WHEN** un administrador modifica los datos de un material existente
- **THEN** el sistema actualiza esos campos, afectando su visibilidad si el perfil visible cambió

#### Scenario: Material inexistente
- **WHEN** se intenta editar un id de material que no existe
- **THEN** el sistema responde con error "Material no encontrado" (404)
