# Plan de Equipo y Sprints — TuluApp

> Complemento del plan técnico (`PLAN_APP_MOVIL_TULUA.md`). Este documento organiza el trabajo en **5 sprints** con asignaciones individuales para los 7 integrantes.

---

## 1. Equipo

| # | Integrante | GitHub (asignar) |
|---|---|---|
| 1 | Juan Manuel Pérez | _@username_ |
| 2 | Michael Pérez | _@username_ |
| 3 | Nicolás Restrepo | _@username_ |
| 4 | Juan Vargas | _@username_ |
| 5 | Santiago Sánchez | _@username_ |
| 6 | Julián | _@username_ |
| 7 | Daniel | _@username_ |

---

## 2. Herramientas gratuitas (costo $0)

| Necesidad | Herramienta | Notas |
|---|---|---|
| **Mapas** | **osmdroid + OpenStreetMap** | 100% gratis, sin API key, sin cuenta. Se envuelve en `AndroidView` dentro de Compose. Alternativa: Google Maps tiene $200/mes de crédito gratis pero requiere cuenta GCP con tarjeta. |
| **Gráficos/Charts** | **Vico** (open source) | Barras, líneas, dona. Soporte nativo para Compose. |
| **Backend** | **Mock local** (ya en el plan) | Si el equipo web despliega algo, pueden usar **Supabase** (free: 500MB DB, 50K users) o **Railway** ($5 crédito gratis). |
| **Auth** | **Mock local** | Si necesitan auth real: Firebase Auth plan Spark (gratis, 50K auth/mes). |
| **CI/CD** | **GitHub Actions** | 2000 min/mes gratis en repos privados. Ya configurado en `.github/workflows/ci.yml`. |
| **Diseño** | **Figma** (free tier) | 3 archivos gratis. El diseño ya existe en Figma. |
| **Imágenes** | **Coil** (open source) | Ya configurado en el proyecto. |
| **PDF** | **PdfDocument** (nativo Android) | Sin dependencias externas. |
| **Fuentes** | **Inter** (Google Fonts) | Gratis, ya en el sistema de diseño. |
| **Comunicación** | **Discord / WhatsApp** | Para dailies y coordinación del equipo. |
| **Gestión tareas** | **GitHub Projects** (free) | Tablero Kanban integrado con issues y PRs. |

### Cambio técnico: osmdroid en vez de Google Maps

El proyecto actual tiene Google Maps configurado. Para evitar costos, se reemplaza por **osmdroid**:

```kotlin
// Dependencia en libs.versions.toml
osmdroid = "6.1.18"
osmdroid-android = { group = "org.osmdroid", name = "osmdroid-android", version.ref = "osmdroid" }

// Se elimina: maps-compose, play-services-maps
// Se mantiene: play-services-location (gratis, para GPS)
```

osmdroid se usa dentro de Compose con un wrapper `AndroidView`. Soporta:
- Marcadores (pins) con íconos personalizados
- Polígonos y overlays por sector
- Clustering con `RadiusMarkerClusterer`
- Tiles offline (caché automático)
- Sin límite de uso, sin API key

---

## 3. Estrategia Git para 7 personas

### Ramas
```
main                    ← Solo merges de develop (releases/entregables)
  └─ develop            ← Integración continua del equipo
       ├─ feat/login              (Sprint 2 - Michael)
       ├─ feat/dashboard-charts   (Sprint 2 - Juan Manuel)
       ├─ feat/map-osmdroid       (Sprint 3 - Juan Manuel)
       ├─ feat/census-list        (Sprint 3 - Michael)
       └─ ...
```

### Reglas
1. **Nunca push directo a `main` o `develop`** — siempre via Pull Request.
2. Cada PR requiere **al menos 1 review** de otro integrante.
3. Commits con prefijo: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`.
4. Resolver conflictos **en la rama feature**, no en develop.
5. Hacer `git pull origin develop` al inicio de cada sesión de trabajo.
6. PRs pequeños y frecuentes — máximo 1 PR por tarea asignada.

### Revisores sugeridos (rotar cada sprint)
| Sprint | Revisores principales |
|---|---|
| 1 | Juan Manuel ↔ Juan Vargas, Michael ↔ Nicolás, Santiago ↔ Julián, Daniel (revisa a Santiago) |
| 2 | Rotar: cada uno revisa al compañero de la derecha en la tabla |
| 3-5 | Seguir rotando para que todos revisen código de diferentes módulos |

---

## 4. Sprints

### Convenciones
- Cada sprint dura **2 semanas**
- Al inicio: reunión de planificación (30 min)
- A mitad: revisión rápida de avance (15 min)
- Al final: demo interna + merge a develop
- Cada integrante tiene **1 tarea principal por sprint** (equitativo)

---

### Sprint 1 — Fundaciones + Capa de Datos (Semanas 1–2)

> **Objetivo:** Completar la base técnica. Al terminar, cualquier integrante puede construir pantallas con datos mock.

**Estado actual:** La Fase 1 (tema, componentes base, navegación, CI) ya está parcialmente implementada. Este sprint completa lo que falta y construye toda la capa de datos.

| Integrante | Tarea | Branch | Entregable |
|---|---|---|---|
| Juan Manuel Pérez | Configurar osmdroid + componente `OsmMapView` reutilizable en Compose (wrapper AndroidView con soporte para marcadores, polígonos y eventos de toque) | `feat/osmdroid-setup` | Componente `OsmMapView` con `@Preview`, mapa renderizando tiles de OSM |
| Michael Pérez | Modelos de dominio completos: `Module`, `Vendor`, `Sector`, `Event`, `SpotAssignment`, `Request`, `DashboardMetrics`, `Report`, `User` + enums de estado | `feat/domain-models` | Clases en `domain/model/`, todas con `@Serializable` |
| Nicolás Restrepo | DTOs para la API (`remote/dto/`), entities para Room (`local/entity/`), y mappers bidireccionales (DTO ↔ Domain ↔ Entity) | `feat/dto-mappers` | Clases en `data/remote/dto/`, `data/local/entity/`, `data/mappers/` con tests unitarios de mappers |
| Juan Vargas | Interfaces de repositorio en `domain/repository/` + Room database (`AppDatabase`, DAOs para módulos, eventos, solicitudes, sectores) + DataStore para sesión | `feat/room-repositories` | Interfaces, DAOs compilando, database con migraciones, DataStore de sesión |
| Santiago Sánchez | `MockModuleRepository` + `MockSectorRepository` + `MockVendorRepository` con datos realistas de Tuluá (40 módulos, 5 sectores: Carrera 25, Galería, Parque Boyacá, Parque Céspedes, Plaza de Mercado) | `feat/mock-modules` | Repositorios mock con datos completos, tests unitarios |
| Julián | `MockEventRepository` + `MockRequestRepository` + `MockDashboardRepository` con datos realistas (8 eventos, 14 solicitudes, métricas coherentes) | `feat/mock-events-requests` | Repositorios mock con datos completos, tests unitarios |
| Daniel | Componentes de UI faltantes: `ProgressBarSegmented`, `ModuleCard`, `EventCard`, `RequestCard`, `InfoRow`, `ConfirmDialog`, `BottomSheetScaffold` (todos con `@Preview`) | `feat/ui-components` | Componentes en `core/designsystem/components/`, cada uno con preview |

**Criterios de aceptación del Sprint 1:**
- [ ] La app compila en flavor `mock` con datos de ejemplo disponibles
- [ ] Los mappers convierten correctamente entre las 3 capas (DTO → Domain → Entity)
- [ ] Room DB se crea sin errores con todas las tablas
- [ ] `OsmMapView` muestra un mapa de Tuluá con un marcador de prueba
- [ ] Todos los componentes de UI tienen `@Preview`
- [ ] CI pasa (build + lint + tests)

---

### Sprint 2 — Autenticación + Dashboard (Semanas 3–4)

> **Objetivo:** El usuario puede iniciar sesión y ver el Dashboard con datos reales del mock.

| Integrante | Tarea | Branch | Entregable |
|---|---|---|---|
| Juan Manuel Pérez | Dashboard — Gráfico de módulos por sector (barras horizontales con Vico, color por nivel de saturación) + lógica pull-to-refresh | `feat/dashboard-charts` | Gráfico interactivo con datos del `DashboardRepository`, toque en barra → detalle sector |
| Michael Pérez | Pantalla de Login: campos email/cédula + contraseña, selector de rol (compacto), validaciones, mostrar/ocultar contraseña, ViewModel con estados Loading/Error/Success | `feat/login` | Login funcional con mock auth, manejo de errores |
| Nicolás Restrepo | Dashboard — Indicadores en cuadrícula 2×2 (`StatCard`): total censados (+delta), formales vs informales (`ProgressBarSegmented`), ocupación hoy, solicitudes pendientes (toque → Trámites) + tarjetas de próximos eventos (máx. 3) | `feat/dashboard-indicators` | Dashboard con datos del mock, interacciones de navegación |
| Juan Vargas | Sesión persistente con DataStore cifrado (EncryptedSharedPreferences como backing), interceptor OkHttp para JWT, lógica de refresh token, redirección automática a Login cuando token expira (401) | `feat/auth-session` | `SessionManager`, `AuthInterceptor`, `AuthRepository` con tests |
| Santiago Sánchez | Pantalla de recuperar contraseña (solicitar email → pantalla de confirmación) + Splash screen con escudo de Tuluá y animación sutil | `feat/recover-splash` | 2 pantallas funcionales con navegación |
| Julián | Dashboard — Banner de alerta de pico de ocupación (descartable, 1 línea) + implementar estados Loading (skeletons), Error y Empty en toda la pantalla Dashboard | `feat/dashboard-states` | Estados visibles al cambiar datos del mock, skeleton animado |
| Daniel | Integración del flujo completo de navegación: Splash → Login → Home (con bottom nav) → Logout → Login. Protección de rutas (si no hay sesión → Login). Transiciones animadas entre pantallas | `feat/auth-navigation` | Flujo completo navegable, rutas protegidas |

**Criterios de aceptación del Sprint 2:**
- [ ] Flujo Login → Dashboard → Logout → Login funciona sin errores
- [ ] Token expirado redirige automáticamente al Login
- [ ] Dashboard muestra los 4 indicadores + gráfico de sectores + próximos eventos
- [ ] Dashboard cabe en ~1.5 pantallas de scroll (regla del profesor)
- [ ] No hay subtítulos largos ni párrafos de ayuda (regla del profesor)
- [ ] Pull-to-refresh actualiza datos, skeletons visibles durante carga

---

### Sprint 3 — Mapa + Censo (Semanas 5–6)

> **Objetivo:** El mapa muestra módulos con filtros y el censo permite listar, buscar, ver detalle y crear nuevos registros.

| Integrante | Tarea | Branch | Entregable |
|---|---|---|---|
| Juan Manuel Pérez | Mapa — Pines por tipo (formal=verde, informal=ámbar, feria=ícono distinto) con clustering al alejar zoom + polígonos/zonas por sector con colores semitransparentes, centrado en centro histórico de Tuluá | `feat/map-pins-clusters` | Mapa con ~40 pines del mock, clustering funcional, polígonos por sector |
| Michael Pérez | Censo — Lista con búsqueda (cédula, nombre, radicado), chips Todos/Formales/Informales, hoja de filtros avanzados con contador, paginación infinita (LazyColumn + cargar más) | `feat/census-list` | Lista filtrable con datos mock, paginación, hoja de filtros |
| Nicolás Restrepo | Censo — Pantalla de detalle de módulo/comerciante: secciones colapsables (Datos generales, Ubicación con mini mapa osmdroid, Permisos, Documentos, Sanciones), foto con visor ampliable | `feat/module-detail` | Detalle completo navegable desde la lista, foto ampliable |
| Juan Vargas | Censo — Formulario "Nuevo Censo" en 5 pasos con `HorizontalPager`: (1) Titular/comerciante, (2) Módulo y categoría, (3) Ubicación GPS + selección en mapa, (4) Foto con CameraX, (5) Resumen y confirmar. Guardado como borrador en Room si no hay red | `feat/new-census-wizard` | Wizard completo funcional, foto capturada, coordenadas GPS, borrador offline |
| Santiago Sánchez | Mapa — Bottom sheet de detalle al tocar un pin: estado colapsado (foto miniatura, titular, ubicación) y expandido (espacio ocupado, vigencia, botones Inspeccionar/Reportar). Navegación al detalle completo del módulo | `feat/map-detail-sheet` | Bottom sheet funcional con 2 estados, navega al detalle |
| Julián | Mapa — Barra de búsqueda en la parte superior + chips de filtro rápido (tipo, estado) + hoja de filtros avanzada (sector, categoría, estado). Filtros actualizan pines sin recargar mapa. Botones de zoom y "mi ubicación" con permisos | `feat/map-filters` | Filtros funcionales, permiso de ubicación con rationale |
| Daniel | Censo — Botón exportar CSV en top bar (menú ⋮), genera archivo y abre share sheet de Android. Lógica de sincronización de borradores: al recuperar conexión, sincronizar censos guardados localmente | `feat/census-export-sync` | Exportar CSV funcional, borradores se sincronizan al tener red |

**Criterios de aceptación del Sprint 3:**
- [ ] Filtros del mapa actualizan pines sin recargar (regla del profesor: compacto)
- [ ] Desde un pin se navega al detalle completo del módulo
- [ ] Búsqueda del censo encuentra por cédula, nombre y radicado
- [ ] Wizard de nuevo censo captura foto (CameraX) y coordenadas (GPS)
- [ ] `ModuleCard` compacta: máx. 3 datos + miniatura 56dp (regla del profesor)
- [ ] Exportar CSV genera archivo y abre share sheet

---

### Sprint 4 — Eventos + Solicitudes + Asignación (Semanas 7–8)

> **Objetivo:** Calendario de eventos funcional con asignación de espacios, y bandeja de solicitudes con aprobación/rechazo.

| Integrante | Tarea | Branch | Entregable |
|---|---|---|---|
| Juan Manuel Pérez | Asignación temporal — Flujo completo desde evento: seleccionar zona en mapa osmdroid, rango de fechas (DateRangePicker de M3), validación de cruces temporales (`validate-overlap`) con mensaje claro, resumen final antes de confirmar | `feat/space-assignment` | Flujo asignación funcional, bloquea si hay cruce, resumen final |
| Michael Pérez | Solicitudes — Lista con tabs (Pendientes/Aprobadas/Rechazadas) con contadores en cada tab, búsqueda por radicado/cédula/nombre, badge de tiempo restante por tarjeta (ámbar < 3 días, rojo < 48h) | `feat/requests-list` | Lista con tabs funcionales, contadores, badges de urgencia |
| Nicolás Restrepo | Eventos — `EventCard` compacta (nombre, fechas, ubicación, barra ocupación `45/45`, 1 botón principal + menú ⋮) + lista como bottom sheet arrastrable debajo del calendario + filtro Todos/En curso/Programadas/Finalizadas | `feat/events-list-sheet` | Bottom sheet arrastrable con lista filtrable |
| Juan Vargas | Eventos — Calendario mensual grande como componente custom: celdas altas (mín. 52-56dp), ancho completo, eventos dentro de celda (barra de color o punto con inicial), navegación por mes con deslizamiento, día actual resaltado, leyenda 1 línea. Tocar día → filtra lista | `feat/events-calendar` | Calendario que ocupa 45-50% del alto visible (regla del profesor) |
| Santiago Sánchez | Solicitudes — Pantalla de detalle (resumen, adjuntos, ubicación, concepto) + acciones Aprobar (con confirmación) y Rechazar (motivo obligatorio, mín. 10 caracteres) + estados optimistas con reversión si falla | `feat/request-actions` | Aprobar/rechazar funcional, mueve entre tabs, actualiza contadores |
| Julián | Eventos — Formulario crear/editar evento: nombre, fechas (DateRangePicker), ubicación (selector en mapa), organizador, comuna, total de puestos. Validaciones y guardado | `feat/create-event` | Formulario completo, evento aparece en calendario tras guardar |
| Daniel | Asignación — Vista de disponibilidad de puestos por zona/fechas (grilla o lista libres/ocupados), asignar comerciante a puesto (buscar → elegir), desasignar. Actualiza contador de ocupación del evento | `feat/assignment-spots` | Vista disponibilidad funcional, asignar/desasignar actualiza contadores |

**Criterios de aceptación del Sprint 4:**
- [ ] Calendario ocupa ~45-50% del alto visible con eventos dentro de las celdas (regla del profesor)
- [ ] Cambiar de mes y tocar un día filtra la lista de eventos
- [ ] No se puede confirmar asignación con cruces temporales
- [ ] Aprobar/rechazar solicitud mueve la tarjeta de tab y actualiza contador del Dashboard
- [ ] Rechazo requiere motivo de mínimo 10 caracteres
- [ ] `EventCard` compacta: 1 botón principal, resto en menú ⋮ (regla del profesor)

---

### Sprint 5 — Reportes + Pulido + Entrega (Semanas 9–10)

> **Objetivo:** Reportes funcionales, revisión transversal de calidad, y APK listo para entregar.

| Integrante | Tarea | Branch | Entregable |
|---|---|---|---|
| Juan Manuel Pérez | Reportes — Gráficos con Vico: módulos por sector (barras), formales vs informales en el tiempo (líneas), ocupación de eventos (dona/barras), solicitudes por estado (dona). Interactivos con toque | `feat/report-charts` | 4 gráficos funcionales que responden a filtros |
| Michael Pérez | Generación de PDF nativo con `PdfDocument`: reporte con encabezado (escudo + título + periodo), gráficos como bitmap, tablas de datos. Integración con share sheet de Android | `feat/pdf-export` | PDF generado y compartible con datos reales del mock |
| Nicolás Restrepo | Revisión de accesibilidad en TODAS las pantallas: `contentDescription` en imágenes/íconos, touch targets ≥ 48dp, contraste AA (verificar con herramienta), soporte de escalado de fuente | `feat/accessibility` | Checklist de accesibilidad completado, todas las pantallas cumplen |
| Juan Vargas | Tests UI de flujos críticos con Compose Testing: (1) Login → Dashboard, (2) Filtrar censo → abrir detalle, (3) Aprobar solicitud, (4) Navegar calendario → ver evento. Mínimo 8 tests instrumentados | `feat/ui-tests` | 8+ tests UI pasando en CI |
| Santiago Sánchez | Reportes — Pantalla con filtros (rango de fechas con atajos: mes/trimestre/año + selector de sector) + exportar CSV/Excel (generación local CSV como respaldo). Los filtros actualizan los gráficos en tiempo real | `feat/reports-screen` | Pantalla de reportes completa con filtros funcionales |
| Julián | Revisión transversal: estados vacíos, errores de red y modo offline (banner "Sin conexión") en TODAS las pantallas. Reintentos con botón. Verificar que ninguna pantalla quede en blanco sin feedback | `feat/offline-states` | Todas las pantallas manejan Empty/Error/Offline correctamente |
| Daniel | Documentación final: README completo (setup, arquitectura, screenshots), guía de configuración (cómo cambiar mock→prod, configurar BASE_URL), ícono adaptativo con escudo de Tuluá, generar APK firmado release v1.0 | `feat/docs-release` | README, APK firmado, ícono adaptativo |

**Criterios de aceptación del Sprint 5 (entrega final):**
- [ ] Los filtros de reportes cambian los gráficos
- [ ] PDF y CSV se generan correctamente y se pueden abrir/compartir
- [ ] Todas las pantallas tienen `contentDescription` y touch targets ≥ 48dp
- [ ] 8+ tests UI pasan en CI
- [ ] Ninguna pantalla queda en blanco (siempre hay feedback: loading, empty, error, offline)
- [ ] APK firmado release compila y funciona en dispositivo real
- [ ] README tiene instrucciones claras para clonar y ejecutar

---

## 5. Checklist del profesor (revisar al final de CADA sprint)

Cada integrante debe verificar que su trabajo cumple:

- [ ] Ninguna tarjeta muestra más de 3 datos principales
- [ ] No hay textos descriptivos largos ni subtítulos redundantes
- [ ] Tipografía y paddings usan la escala compacta (Body 14sp, paddings 12dp, separación 8dp)
- [ ] Imágenes grandes reemplazadas por miniaturas en listas (56-64dp)
- [ ] Acciones secundarias en menú ⋮ o bottom sheet, no como botones grandes
- [ ] Calendario de Eventos ocupa ~45-50% del alto visible
- [ ] Barra inferior sin textos cortados (Inicio, Mapa, Censo, Eventos, Trámites con 11sp)
- [ ] Chips con scroll horizontal sin cortes

---

## 6. Resumen de carga por integrante (5 sprints)

| Integrante | Sprint 1 | Sprint 2 | Sprint 3 | Sprint 4 | Sprint 5 |
|---|---|---|---|---|---|
| **Juan Manuel Pérez** | osmdroid setup | Dashboard charts | Mapa pines+clusters | Asignación temporal | Reportes gráficos |
| **Michael Pérez** | Modelos dominio | Login | Censo lista | Solicitudes lista | PDF export |
| **Nicolás Restrepo** | DTOs + mappers | Dashboard indicadores | Detalle módulo | Eventos cards+lista | Accesibilidad |
| **Juan Vargas** | Room + repositorios | Auth sesión | Nuevo censo wizard | Calendario eventos | Tests UI |
| **Santiago Sánchez** | Mock módulos | Recuperar + splash | Mapa bottom sheet | Solicitudes acciones | Reportes pantalla |
| **Julián** | Mock eventos | Dashboard estados | Mapa filtros | Crear evento | Offline states |
| **Daniel** | UI components | Auth navegación | CSV export + sync | Asignación puestos | Docs + release |

> **Cada integrante tiene exactamente 5 tareas** (1 por sprint). La carga está balanceada entre tareas de UI, lógica, datos e infraestructura.

---

## 7. Definición de "Hecho" (Definition of Done)

Una tarea se considera terminada cuando:

1. El código compila sin errores ni warnings en CI
2. Tiene al menos 1 test unitario (ViewModels, repositorios, mappers)
3. Las pantallas tienen `@Preview` en modo claro
4. El PR fue revisado y aprobado por al menos 1 compañero
5. Se mergeó a `develop` sin conflictos
6. Cumple las reglas UX del profesor (checklist sección 5)

---

## 8. Riesgos y mitigaciones

| Riesgo | Probabilidad | Mitigación |
|---|---|---|
| Un integrante no entrega a tiempo | Alta | Cada sprint tiene buffer de 2-3 días. Si alguien se atrasa, otro puede ayudar en la misma rama. |
| osmdroid no cubre algo que Google Maps sí | Media | osmdroid cubre todo lo necesario (pins, clusters, polygons). Si falta algo, se implementa con Canvas overlay. |
| El backend web nunca se despliega | Alta | El mock es autosuficiente. La app funciona 100% sin backend real. El switch mock→prod es solo cambiar flavor. |
| Conflictos de merge frecuentes | Media | PRs pequeños y frecuentes. Cada quien trabaja en módulos diferentes. Merge a develop al menos 2 veces por sprint. |
| CameraX o GPS falla en emulador | Baja | CameraX funciona en emulador con cámara virtual. GPS se simula con ubicación ficticia en emulador settings. |

---

## 9. Cronograma visual

```
Semana:  1    2    3    4    5    6    7    8    9    10
         ├────┤    ├────┤    ├────┤    ├────┤    ├────┤
Sprint:  [  Sprint 1  ][  Sprint 2  ][  Sprint 3  ][  Sprint 4  ][  Sprint 5  ]
         Fundaciones   Auth+Dashboard Mapa+Censo   Eventos+Sol.  Reportes+
         +Datos                                    +Asignación   Pulido+Entrega

Hitos:   ▲ Setup      ▲ Login       ▲ Mapa+Censo ▲ Features    ▲ APK v1.0
         completo     funcional     funcional     completas     entregable
```

---

## 10. Cómo empezar

1. **Todos**: Clonar el repo, configurar Android Studio, compilar flavor `mock`
2. **Todos**: Crear branch `develop` a partir de `main`
3. **Cada integrante**: Crear su branch del Sprint 1 desde `develop`
4. **Juan Manuel**: Empezar con osmdroid (es prerequisito para Sprint 3)
5. **Michael + Nicolás + Juan Vargas**: Empezar con dominio/datos (es prerequisito para todo)
6. **Santiago + Julián**: Empezar con mocks (necesitan los modelos de Michael)
7. **Daniel**: Empezar con componentes UI (independiente)

**Dependencias del Sprint 1:**
```
Michael (modelos) ──→ Nicolás (DTOs/mappers) ──→ Juan Vargas (Room/repos)
                  └─→ Santiago (mock módulos)
                  └─→ Julián (mock eventos)
Juan Manuel (osmdroid) ── independiente
Daniel (UI components) ── independiente
```

> **Consejo:** Michael debe mergear sus modelos en los primeros 3-4 días para desbloquear a Nicolás, Santiago y Julián.
