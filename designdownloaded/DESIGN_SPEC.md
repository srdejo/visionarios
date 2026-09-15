# Red de Visionarios — especificación de diseño

Origen: proyecto de Claude Design "Plataforma de registro y login" (`Red de Visionarios.dc.html`), importado el 2026-09-14.

**Nota:** el archivo `.dc.html` original y los logos PNG no pudieron persistirse en este repo — algo en el entorno local (antivirus/EDR, aparentemente heurística de plantillas de login/phishing) los borra en cuanto se escriben a disco, de forma silenciosa. Este documento es la fuente de verdad destilada a mano a partir de ese archivo para no bloquear la implementación. Si en algún momento se puede recuperar el `.dc.html`/los logos, vive en el proyecto de Claude Design: `https://claude.ai/design/p/0d3a7fc3-f2c4-4467-907d-a6adfa51e7cb`.

## Identidad visual

- Fondo oscuro de referencia (pantallas de auth): `#111111` (negro, no puro `#000` en body pero header/hero usa `#000000`).
- Acento primario: `#FAA61A` (naranja/dorado — barras de progreso, tags, hover de botón secundario, subrayados de "Abrir/Descargar/Editar").
- Hover de enlace: `#B87200`.
- Texto secundario: `#6B6B6B` / `#5C5C5C` / `#7A7A7A` / `#9A9A9A` según jerarquía.
- Bordes sutiles: `#E1E1E1` / `#E9E9E9` / `#EFEFEF` / `#F0F0F0`.
- Fondo de inputs: `#FAFAFA`, focus: blanco + borde `#111111`.
- Tipografías (Google Fonts):
  - **Oswald** (400/500/600/700) — títulos en mayúsculas, labels de formulario, nav, badges. Letter-spacing amplio en labels pequeños (`.14em`) y nav (`.1em`).
  - **Barlow** (400/500/600/700, italic 400) — cuerpo de texto, párrafos, inputs.
  - **Sacramento** (script) — saludo emocional puntual: "Bienvenido" (login), "Hola, {{nombre}}" (home).
- Logo: `logo-mi.png` (negro, sobre fondo claro), `logo-mi-white.png` (blanco, sobre fondo oscuro/hero), `logo-mi-crop.png` (recorte, uso puntual).
- El branding es de **Mi Casa Church**; el producto es **Red de Visionarios**, un programa/comunidad para profesionales, emprendedores y empresarios.

## Modelo de dominio (deducido del prototipo)

- **Usuario**: nombre completo, celular, correo, contraseña, rol (`USER` | `ADMIN`), perfil (`PROFESIONAL` | `EMPRENDEDOR` | `EMPRESARIO`), campos específicos según perfil.
- **Campos por perfil** (paso 3 del registro):
  - Profesional: Profesión, Años de experiencia, Empresa actual.
  - Emprendedor: Producto o negocio, Tiempo operando.
  - Empresario: Empresa, Número de empleados, Años con la empresa.
- **Evento**: título, fecha/hora, lugar, descripción, a qué perfiles aplica (o toda la red), contador de confirmados. Relación de asistencia (RSVP) por usuario.
- **Material**: título, tipo (PDF/DOC/XLS/MP3), meta (descripción corta), URL de Google Drive, perfiles a los que es visible (uno, varios o "toda la red").
- **Invitación de administrador**: enlace único, expira en 7 días, un solo uso.

## Pantallas (flujo de usuario)

### 1. Login (`isLogin`)
Hero oscuro con logo blanco, clip-path diagonal, franja naranja decorativa arriba a la derecha. Texto: script "Bienvenido" + título "A CASA" en naranja + subtítulo "Red de Visionarios · Mi Casa Church". Formulario: Correo, Contraseña, botón "Entrar" (negro), enlace "Olvidé mi contraseña", separador "O", botón secundario outline "Crear mi cuenta" (hover: fondo naranja).

### 2. Olvidé mi contraseña (`isForgot`)
Botón volver, título "Recuperar contraseña", barra naranja corta, texto explicando que se enviará un enlace, input de correo, botón "Enviar enlace". (No hay pantalla de "revisa tu correo" en el prototipo — el submit no está implementado más allá de volver a login).

### 3. Registro — wizard de 4 pasos (`isRegister`)
Header con botón "Atrás", label "PASO N DE 4", barra de progreso de 4 segmentos que se rellenan en naranja según avance.

- **Paso 1 — Tus datos**: Nombre completo, Celular, Correo, Contraseña (mín. 8 caracteres). Botón "Continuar".
- **Paso 2 — ¿Cómo te identificas hoy?**: 3 tarjetas seleccionables (Profesional / Emprendedor / Empresario), cada una con descripción corta ("Trabajas para una empresa o buscas empleo." / "Ya tienes un producto o negocio en marcha." / "Tienes una o más empresas y personas a cargo."). Tarjeta seleccionada: borde negro + barra lateral naranja (inset shadow). Botón "Continuar".
- **Paso 3 — Cuéntanos un poco más**: badge con el nombre del perfil elegido, campos dinámicos según perfil (ver tabla arriba). Botón "Crear mi cuenta".
- **Paso 4 — Confirmación**: logo, script "Bienvenido" + "A LA RED", mensaje "Tu cuenta quedó creada como **{perfil}**. Ya puedes descargar el material de tu perfil y confirmar tu asistencia a los próximos entrenamientos." Botón "Entrar a la red" → login/home.

### 4. Home (`isHome`, requiere sesión)
Header oscuro con logo, badge de perfil, saludo script "Hola, {nombre}" + título "Red de VISIONARIOS" (visionarios en naranja) + subtítulo "Mi Casa Church · Entrenamiento 2026".
- Tarjeta "Próximo evento" (clicable → detalle de evento): franja naranja superior con "Próximo evento" / "Faltan N días", fecha grande (día/mes), título, meta (hora y lugar).
- Sección "Material para ti" (2 destacados, filtrados por perfil del usuario) con enlace "Ver todo" → biblioteca. Cada item: icono con tipo de archivo, título, meta, "Abrir".
- Sección "Agenda" (lista de próximos eventos) con día/mes, título, meta, badge de estado ("Esta semana" en naranja si aplica, si no "Abierto").

### 5. Biblioteca (`isBiblioteca`)
Título "Material", filtros tipo chip ("Todo" / "Toda la red" / perfil del usuario), lista de tarjetas de material con tipo, título, meta, tag de visibilidad, enlace "Descargar".

### 6. Detalle de evento (`isEvento`)
Header oscuro con botón volver "← Agenda", badge de fecha/hora, título grande, lugar/transmisión. Cuerpo: descripción, grid 2 columnas ("Dirigido a" / "Confirmados: N personas"), botón RSVP toggle (negro "Confirmar asistencia" → naranja "Asistencia confirmada ✓" al activarse, contador +1), botón secundario "Agregar al calendario", bloque "Material del evento" con un documento y enlace "Abrir".

### 7. Perfil (`isPerfil`)
Avatar circular con iniciales, nombre, correo. Filas: Perfil, Celular, Miembro desde. Botón "Cerrar sesión" (rojo `#B00020`, borde claro).

### 8. Admin — Eventos (`isAdminEventos`)
Header "ADMINISTRADOR" (naranja) + título "Eventos", botón "+" para crear. Lista de tarjetas: título, badge de estado, fecha larga + meta, confirmados, enlace "Editar".

### 9. Admin — Material (`isAdminMaterial`)
Mismo patrón: título "Material", botón "+", nota explicando que cada documento es un enlace de Drive etiquetado por perfil visible, lista de tarjetas con título, url (truncada), tag de visibilidad, "Editar".

### 10. Admin — Usuarios (`isAdminUsuarios`)
Título "Usuarios", 3 contadores (Total / Emprend. / Empresarios), lista de usuarios con iniciales, nombre, correo, badge de perfil.

### 11. Admin — Equipo/Admins (`isAdminAdmins`)
Título "Equipo". Bloque oscuro "Invitar administrador": explica que el enlace vence en 7 días y es de un solo uso, muestra el enlace generado, botón "Copiar enlace" (confirmación visual "Enlace copiado ✓"). Lista de administradores existentes: iniciales, nombre, correo, estado ("Tú" / "Activa" / "Pendiente").

### Modal genérico (crear/editar evento o material)
Hoja inferior (bottom sheet): handle de arrastre, título, campos dinámicos según si es evento o material, selector multi-chip de "Visible para" (perfiles), botones "Cancelar" / "Guardar".

### Navegación inferior
- Usuario: Inicio (⌂) / Material (▤) / Agenda (◈) / Perfil (◉).
- Admin: Eventos (◈) / Material (▤) / Usuarios (◉) / Equipo (✦).
No se muestra en pantallas de auth (login/registro/olvidé contraseña).

## Notas de implementación

- El prototipo es solo de estado local (sin backend real) — todo lo anterior es la referencia de UX/copy/flujo a implementar contra la API real definida en `docs/ARCHITECTURE.md`.
- "Olvidé mi contraseña" no tiene flujo de envío de correo real en el diseño — implementar como TODO explícito (ver `docs/DECISIONS.md`).
- Los tres perfiles (Profesional/Emprendedor/Empresario) son el eje central de la personalización: filtran material, aplican a eventos, y se muestran como badge en casi toda la app.
