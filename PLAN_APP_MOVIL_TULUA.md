# Plan de desarrollo: App móvil Android – Gestión del Espacio Público (Tuluá)

> Documento para Claude Code. Léelo completo antes de escribir código. Trabaja por fases, en orden, y al terminar cada fase verifica los criterios de aceptación antes de pasar a la siguiente.

---

## 0. Contexto

App móvil **Android** para la Secretaría de Gobierno de Tuluá. Es el cliente móvil de una plataforma que censa y categoriza módulos de comercio formal e informal (ventas ambulantes, casetas), y gestiona la asignación temporal de espacios para ferias, eventos y emprendimientos en el centro y sectores clave de Tuluá.

- Comparte **backend** con la versión web (otro curso). El backend aún se está implementando, así que la app debe poder correr con **datos simulados (mock)** y cambiar a la API real sin tocar UI ni lógica.
- Diseño de referencia: Figma Make `TULUA-APP` (https://www.figma.com/make/twAxV6QhYeBvdRhyJiIf2v/TULUA-APP) y las 8 capturas del diseño móvil (Dashboard, Mapa Centro, Censo ×2, Eventos ×2, Solicitudes).
- Idioma de la UI: **español (Colombia)**. Moneda/fechas con locale `es-CO`. Zona horaria `America/Bogota`.

### Rol principal
Administrador (secretaría). El login permite seleccionar rol, pero el alcance de esta entrega es el flujo de Administrador. Dejar la arquitectura lista para otros roles (p. ej. Inspector, Comerciante) sin implementarlos.

---

## 1. Correcciones del profesor (reglas de UX obligatorias)

Aplican a **todas** las pantallas, no solo a una:

1. **Menos texto y menos información por pantalla; todo más compacto y versátil.**
   - Cada tarjeta muestra máximo 3 datos clave. El resto va en un detalle (bottom sheet o pantalla aparte).
   - Divulgación progresiva: resumen → toque → detalle.
   - Reducir tamaños: tipografía base 13–14 sp, títulos de sección 16 sp, paddings de tarjeta 12 dp, separación entre elementos 8 dp.
   - Eliminar subtítulos descriptivos largos, párrafos de ayuda y filas repetidas de metadatos.
   - Texto máximo a 1 línea con ellipsis en títulos de tarjetas; descripciones a 1–2 líneas.
2. **El calendario debe ocupar más espacio.**
   - En Eventos, el calendario mensual es el elemento protagonista: celdas altas (mín. 52–56 dp), ocupa el ancho completo (sin padding extra) y alrededor de 45–50 % del alto visible.
   - Los eventos se pintan dentro de la celda (barra de color o punto con inicial), no solo un punto diminuto.
   - La lista de eventos va debajo y es colapsable (bottom sheet arrastrable) para que el calendario se pueda ver grande.
   - Al tocar un día, se filtra la lista; el calendario no se achica.

Observaciones adicionales detectadas en las capturas (corregir al implementar):
- La barra inferior corta los textos ("Dashboard", "Trámite"). Usar etiquetas cortas (`Inicio`, `Mapa`, `Censo`, `Eventos`, `Trámites`) con 11 sp y `NavigationBar` de Material 3.
- Los chips de acción superiores del Dashboard se cortan ("Exp…"). Usarlos como fila con scroll horizontal o reducirlos a 2 acciones principales + menú "más".
- El chip con el nombre de la pantalla junto al avatar (arriba a la derecha) parece un artefacto de Figma; no implementarlo.
- Las tarjetas de Censo con foto grande ocupan demasiado alto: usar miniatura de 56–64 dp a la izquierda en lugar de imagen a todo el ancho.

---

## 2. Stack técnico

| Capa | Decisión |
|---|---|
| Lenguaje / UI | **Kotlin + Jetpack Compose + Material 3** |
| Arquitectura | MVVM + Clean (data / domain / ui), unidirectional data flow (`StateFlow`, `UiState` sellado) |
| DI | Hilt |
| Navegación | Navigation Compose (type-safe routes) |
| Red | Retrofit + OkHttp + kotlinx.serialization, interceptor de auth (JWT), refresh de token |
| Local | Room (caché offline-first de módulos, eventos, solicitudes) + DataStore (sesión, preferencias) |
| Mapas | Maps Compose (Google Maps SDK) con clustering. La API key va en `local.properties` / secrets, nunca en el repo |
| Imágenes | Coil |
| Gráficos | Vico (o implementación propia con Canvas para barras/dona simples) |
| Cámara/GPS | CameraX + FusedLocationProvider (captura de foto y coordenadas en el censo) |
| Exportar | PDF con `PdfDocument` nativo; Excel/CSV desde el backend (descarga con `DownloadManager`) o generación local CSV como respaldo |
| Tests | JUnit5 + MockK + Turbine (unitarios), Compose UI tests (flujos clave) |
| Mín. SDK | 26 (Android 8.0), target SDK el más reciente estable |
| Paquete | `co.gov.tulua.espaciopublico` (ajustable) |

Si el equipo prefiere Flutter o React Native, avisar **antes** de empezar; el plan de fases y el contrato de API se mantienen igual.

---

## 3. Estructura del proyecto

```
app/
 └─ src/main/java/.../
     ├─ core/            (di, network, database, util, designsystem)
     ├─ data/            (remote/dto, local/entity+dao, repository impl, mappers, mock/)
     ├─ domain/          (model, repository interfaces, usecases)
     └─ ui/
         ├─ auth/        (login, recuperar)
         ├─ dashboard/
         ├─ map/
         ├─ census/      (lista, detalle módulo, nuevo censo)
         ├─ events/      (calendario, lista, asignación)
         ├─ requests/    (bandeja, detalle, aprobar/rechazar)
         ├─ reports/
         └─ navigation/
```

**Regla clave:** la UI solo conoce interfaces de `domain/repository`. Hay dos implementaciones: `Mock*Repository` (datos locales de ejemplo) y `Remote*Repository` (API real). Se elige con un flag `BuildConfig.USE_MOCK` / flavor `mock` y `prod`.

---

## 4. Sistema de diseño (extraído de las capturas)

Verificar valores exactos en Figma (inspect) y centralizarlos en `core/designsystem`.

**Colores (aproximados)**
- Primario: verde bosque `#0F5F3A`; contenedor primario / fondos suaves: verde menta `#E8F3EA`; fondo de app: `#F1FAF2`
- Acento / advertencia (ocupación alta, "En revisión", "Programada"): ámbar `#F5B14C` con fondo `#FCEBC8`
- Peligro / crítico (saturación, rechazado, vence pronto): rojo `#C62828` con fondo `#FDE3E3`
- Texto principal `#1B1B1B`, secundario `#5F6B63`
- Tarjetas blancas, radio 16 dp; chips radio completo; bordes sutiles `#E1EBE3`

**Semántica de estado (única en toda la app)**
- Verde: Aprobado / Formal / En curso / Equilibrado / Media
- Ámbar: En revisión / Informal / Programada / Alta
- Rojo: Rechazado / Saturación crítica / Urgente
- Gris: Finalizada / Cerrado

**Tipografía:** Inter (Google Fonts). Escala compacta: Display 28, Title 18, Section 16, Body 14, Label 12, Caption 11.

**Componentes reutilizables a construir primero**
`TuluaTopBar`, `StatCard`, `StatusBadge`, `SectionHeader`, `ProgressBarSegmented`, `FilterChipRow` (scroll horizontal), `SearchField`, `ModuleCard` (compacto), `EventCard`, `RequestCard`, `InfoRow`, `EmptyState`, `ErrorState`, `SkeletonLoader`, `BottomSheetScaffold`, `ConfirmDialog` (con campo de motivo para rechazo).

**Navegación inferior (5):** Inicio · Mapa · Censo · Eventos · Trámites. Reportes y perfil/ajustes se acceden desde Inicio (acción) y el avatar.

---

## 5. Modelo de datos (dominio)

```kotlin
enum class ModuleType { FORMAL, INFORMAL, FERIA }
enum class ModuleStatus { APROBADO, EN_REVISION, RECHAZADO, SUSPENDIDO }
enum class EventStatus { PROGRAMADA, EN_CURSO, FINALIZADA }
enum class RequestStatus { PENDIENTE, APROBADA, RECHAZADA }

Module(id, code /*MOD-2025-0142*/, name, type, category, status,
       vendor: Vendor, location: GeoLocation(address, lat, lng, sectorId, zone),
       areaM2, widthM, lengthM, photoUrl, validUntil, lastVerifiedAt,
       observations /*salvedad operativa*/, permits: List<Permit>,
       documents: List<Document>, sanctions: List<Sanction>)

Vendor(id, fullName, idNumber /*C.C.*/, phone?, association?)
Sector(id, name, description, moduleCount, occupancyPercent, saturationLevel)
Event(id, code /*RAD-EV-2024-041*/, name, status, startDate, endDate,
      location, organizer, comuna, totalSpots, assignedSpots, zoneId)
SpotAssignment(id, eventId, spotLabel, vendorId?, moduleId?)
Request(id, code /*RAD-2024-0512*/, type, title, applicant, location, createdAt,
        dueAt, status, rejectionReason?, attachments)
DashboardMetrics(totalModules, weeklyDelta, formal, informal, occupiedToday,
                 occupancyPercent, pendingRequests, sectors, upcomingEvents)
Report(period, sectorId?, series...)
User(id, name, role, token)
```

---

## 6. Contrato de API (propuesta para acordar con el backend web)

Compartir este contrato con el equipo del backend. Idealmente se publica como **OpenAPI** y ambos clientes (web y móvil) lo consumen. Base: `/api/v1`. Auth: `Authorization: Bearer <jwt>`. Paginación: `?page=&size=`. Errores: `{ "code": "...", "message": "..." }`.

| Área | Endpoints |
|---|---|
| Auth | `POST /auth/login {email|cedula, password, role}` · `POST /auth/refresh` · `POST /auth/forgot-password` · `POST /auth/reset-password` · `GET /me` |
| Dashboard | `GET /dashboard/metrics` · `GET /dashboard/sectors` |
| Sectores | `GET /sectors` |
| Módulos | `GET /modules?search=&type=&status=&sectorId=&page=` · `GET /modules/{id}` · `POST /modules` · `PUT /modules/{id}` · `POST /modules/{id}/photo` (multipart) · `GET /modules/map?bbox=&type=&status=&sectorId=` · `GET /modules/export?format=xlsx` |
| Comerciantes | `GET /vendors/{id}` · `GET /modules/{id}/permits` · `GET /modules/{id}/documents` · `GET /modules/{id}/sanctions` |
| Eventos | `GET /events?month=YYYY-MM&status=` · `GET /events/{id}` · `POST /events` · `PUT /events/{id}` |
| Asignación | `GET /events/{id}/spots` · `POST /events/{id}/assignments` · `DELETE /events/{id}/assignments/{aid}` · `POST /events/validate-overlap {zoneId,start,end}` · `GET /zones/availability?from=&to=` |
| Solicitudes | `GET /requests?status=&search=&page=` · `GET /requests/{id}` · `POST /requests/{id}/approve` · `POST /requests/{id}/reject {reason}` |
| Reportes | `GET /reports?from=&to=&sectorId=` · `GET /reports/export?format=pdf|xlsx` |

Mientras el backend no exista, el módulo `data/mock` implementa todo esto en memoria con ~40 módulos, 8 eventos, 14 solicitudes y 5 sectores (usar los nombres reales de las capturas: Carrera 25, Galería y Plaza de Mercado, Parque Boyacá, Parque Céspedes, etc.).

---

## 7. Fases de desarrollo

### Fase 1 – Fundaciones
- Crear proyecto Android (Kotlin, Compose, M3, Hilt, Navigation, Room, Retrofit, DataStore, Coil).
- Flavors `mock` / `prod`; `local.properties` para API key de mapas y `BASE_URL`.
- Tema (colores, tipografía Inter, formas) y componentes base del sistema de diseño.
- Scaffold con bottom navigation de 5 destinos y top bar.
- CI básico (GitHub Actions: build + tests + lint) y `README` con instrucciones.

**Aceptación:** la app compila, abre con 5 pestañas vacías con el tema correcto, un `@Preview` por componente base.

### Fase 2 – Capa de datos y mock
- Modelos de dominio, DTOs, mappers, interfaces de repositorio.
- `Mock*Repository` con datos realistas; `Remote*Repository` con Retrofit (stub si no hay backend).
- Room como caché (módulos, eventos, solicitudes), estrategia offline-first (mostrar caché, refrescar en segundo plano).
- Manejo uniforme de `Result`/errores y estados `Loading | Success | Error | Empty`.

**Aceptación:** tests unitarios de repositorios y mappers; cambiar de flavor no modifica la UI.

### Fase 3 – Acceso (Funcionalidad 1)
- Login con selección de rol (selector compacto), validaciones, mostrar/ocultar contraseña.
- Recuperar contraseña (solicitar → confirmación).
- Sesión persistente (DataStore cifrado), refresh de token, cierre de sesión, redirección a login si 401.
- Splash con escudo de Tuluá.

**Aceptación:** flujo login → home → logout → login; token expirado redirige al login.

### Fase 4 – Dashboard (Funcionalidad 2)
- Indicadores compactos en cuadrícula 2×2: total censados (+ delta semanal), formales vs informales (barra segmentada), ocupación hoy, solicitudes pendientes (toque → Trámites).
- Gráfico por sector (barras horizontales con color por nivel de saturación, toque → detalle del sector).
- Próximos eventos (máx. 2–3 tarjetas, "Ver todos").
- Alerta de pico de ocupación como banner descartable (una sola línea).
- Pull-to-refresh y skeletons.
- Aplicar regla del profesor: sin subtítulos largos, sin bloque de brigadas salvo que quepa como una sola fila.

**Aceptación:** pantalla cabe en ~1.5 pantallas de scroll; todos los datos vienen del repositorio.

### Fase 5 – Mapa del espacio público (Funcionalidad 3)
- Mapa centrado en el centro histórico de Tuluá, polígonos/zonas por sector.
- Pines por tipo (formal verde, informal ámbar, feria con ícono distinto) con clustering al alejar el zoom.
- Barra de búsqueda y chips de filtro (tipo, estado, sector) en la parte superior; hoja de filtros avanzada (sector, categoría, estado).
- Bottom sheet de detalle al tocar un pin: foto en miniatura, titular, ubicación, espacio ocupado, vigencia, acciones `Inspeccionar` y `Reportar`. Estado colapsado (resumen) y expandido (detalle).
- Botones de zoom, capas y "mi ubicación"; permisos de ubicación con rationale.

**Aceptación:** filtros actualizan los pines sin recargar el mapa; abrir detalle desde un pin y navegar al detalle completo del módulo.

### Fase 6 – Censo / listado de módulos (Funcionalidades 4 y 5)
- Lista con búsqueda (cédula, nombre, radicado), chips Todos/Formales/Informales, hoja de filtros con contador de filtros activos, paginación infinita (en vez de "Anterior/Siguiente").
- `ModuleCard` compacta: código, nombre, badge de estado, comerciante y dirección en una línea, miniatura; sin chips redundantes.
- **Exportar** a Excel/CSV (acción en la top bar o menú ⋮, no un botón de ancho completo).
- **Detalle del módulo/comerciante** con pestañas o secciones colapsables: Datos, Ubicación (mini mapa), Permisos (historial), Documentos, Sanciones. Foto con visor ampliable.
- **Nuevo censo** (FAB): formulario en pasos (1. Titular, 2. Módulo y categoría, 3. Ubicación con GPS + mapa, 4. Foto con CameraX, 5. Resumen). Guardado como borrador local si no hay red y sincronización posterior.

**Aceptación:** buscar, filtrar, abrir detalle, exportar y crear un censo completo (en mock) sin errores.

### Fase 7 – Ferias y eventos (Funcionalidad 6) – corrección del profesor
- **Calendario mensual grande** (ver sección 1): celdas altas, ancho completo, eventos dibujados dentro de la celda, navegación por mes con deslizamiento, hoy resaltado. Leyenda en una sola línea.
- Lista de eventos como bottom sheet arrastrable debajo del calendario, con filtro Todos / En curso / Programadas / Finalizadas.
- `EventCard` compacta: nombre, fechas, ubicación, barra de ocupación (`45/45`) y un solo botón principal (Planilla / Asignar / Ver acta según estado); el resto en menú ⋮.
- Crear/editar evento (botón "Programar" como FAB o icono en top bar).

**Aceptación:** el calendario ocupa visiblemente más espacio que el diseño anterior; cambiar de mes y tocar un día filtra la lista.

### Fase 8 – Asignación temporal de espacios (Funcionalidad 7)
- Desde un evento → `Asignar`: seleccionar **zona en el mapa**, rango de fechas (date range picker), validación de cruces (`validate-overlap`) con mensaje claro y ubicación del conflicto.
- Vista de disponibilidad por zona/fechas (grilla o lista de puestos libres/ocupados).
- Asignar comerciantes a puestos (buscar comerciante → elegir puesto), deshacer asignación.
- Resumen final antes de confirmar.

**Aceptación:** no se puede confirmar con cruces; asignar y quitar actualiza el contador de ocupación del evento.

### Fase 9 – Bandeja de solicitudes (Funcionalidad 8)
- Lista con pestañas Pendientes / Aprobadas / Rechazadas (con contadores), búsqueda por radicado/cédula/nombre.
- Alerta de términos legales como banner de una línea; badge de tiempo restante por tarjeta (ámbar < 3 días, rojo < 24–48 h).
- Detalle de solicitud (pantalla o bottom sheet): resumen, adjuntos, ubicación, concepto.
- **Aprobar** (con confirmación) y **Rechazar** (motivo obligatorio, mínimo 10 caracteres); estados optimistas con reversión si falla la red.

**Aceptación:** aprobar/rechazar mueve la solicitud de pestaña y actualiza el contador del Dashboard.

### Fase 10 – Reportes (Funcionalidad 9)
- Filtros por periodo (rango de fechas con atajos: mes, trimestre, año) y por sector.
- Gráficos: módulos por sector, formales vs informales en el tiempo, ocupación de eventos, solicitudes por estado.
- Descarga **PDF** y **Excel** (descarga + compartir con el share sheet de Android).

**Aceptación:** los filtros cambian los gráficos; ambos archivos se generan/descargan y se pueden abrir.

### Fase 11 – Pulido, calidad y entrega
- Revisión transversal de las reglas del profesor pantalla por pantalla (checklist de la sección 8).
- Accesibilidad: `contentDescription`, objetivos táctiles ≥ 48 dp, contraste AA, escalado de fuente.
- Estados vacíos, errores de red, modo sin conexión (banner), reintentos.
- Rendimiento: listas con `key`, imágenes con tamaño fijo, evitar recomposiciones innecesarias; baseline profile si da tiempo.
- Tests UI de flujos críticos (login, filtrar censo, aprobar solicitud, asignar espacio).
- Generar APK/AAB firmado (release), ícono adaptativo con escudo, versión 1.0.
- Documentación: README, guía de configuración (API key, BASE_URL), cómo cambiar de mock a prod.

---

## 8. Checklist de las correcciones del profesor (revisar al final de cada fase)

- [ ] Ninguna tarjeta muestra más de 3 datos principales.
- [ ] No hay textos descriptivos largos ni subtítulos redundantes.
- [ ] Tipografía y paddings usan la escala compacta del sistema de diseño.
- [ ] Imágenes grandes reemplazadas por miniaturas en listas.
- [ ] Acciones secundarias van en menú ⋮ o bottom sheet, no como botones grandes.
- [ ] Calendario de Eventos ocupa ~45–50 % del alto visible y eventos visibles dentro de las celdas.
- [ ] Barra inferior sin textos cortados; chips con scroll horizontal sin cortes.

---

## 9. Pantallas que NO están en las capturas (diseñar con el mismo sistema)

Login y recuperación de contraseña · Detalle de comerciante/módulo · Nuevo censo (pasos) · Asignación temporal (mapa + fechas + puestos) · Detalle de solicitud y diálogo de rechazo · Reportes · Crear/editar evento · Perfil/ajustes. Si el Figma ya tiene alguna, seguir el Figma; si no, construirlas respetando los componentes y reglas de este documento.

---

## 10. Reglas de trabajo para Claude Code

1. Trabaja una fase a la vez, con commits pequeños (`feat:`, `fix:`, `refactor:`) y un PR/branch por fase.
2. No hardcodear textos: todo en `strings.xml` (es-CO), colores y medidas en el tema.
3. No inventar endpoints: si algo falta en el contrato, agrégalo a `docs/api-contract.md` y créalo primero en el mock.
4. Nunca subir secretos (API keys, keystores) al repositorio.
5. Cada pantalla: `@Preview` (claro), estados Loading/Empty/Error y al menos un test del ViewModel.
6. Al terminar cada fase, resumir qué se hizo, qué falta y qué decisiones se tomaron.
7. Ante ambigüedad de diseño, preguntar o seguir las reglas de la sección 1 antes de improvisar.

---

## 11. Prompt inicial sugerido para Claude Code

> Lee `PLAN_APP_MOVIL_TULUA.md` completo. Empieza por la **Fase 1**: crea el proyecto Android con Kotlin + Jetpack Compose + Material 3, configura Hilt, Navigation, Room, Retrofit, DataStore y Coil, los flavors `mock`/`prod`, el tema con los colores y la tipografía Inter de la sección 4, y la navegación inferior de 5 pestañas. Muéstrame el resultado y los criterios de aceptación cumplidos antes de pasar a la Fase 2.
