# Tulua Espacio Publico — App Android

> Proyecto academico en desarrollo — Curso de Desarrollo Movil

---

## Para el equipo: que estamos construyendo?

Estamos desarrollando una **aplicacion movil Android** para la Secretaria de Gobierno del Municipio de Tulua, Valle del Cauca. El objetivo es digitalizar la gestion del espacio publico: censar vendedores ambulantes y modulos de comercio, administrar ferias y eventos, y gestionar tramites administrativos desde el celular.

**Cliente de la app:** funcionarios y administradores de la Secretaria de Gobierno.



### Que hace la app?

| Modulo | Descripcion |
|--------|-------------|
| Inicio (Dashboard) | Resumen de estadisticas: modulos censados, ocupacion, solicitudes pendientes |
| Mapa | Mapa interactivo del centro de Tulua con pines por tipo de modulo |
| Censo | Listado, busqueda y registro de modulos de comercio formal e informal |
| Eventos | Calendario de ferias, asignacion de espacios temporales |
| Tramites | Bandeja de solicitudes administrativas: aprobar o rechazar |

---

## Para el equipo: como esta organizado el codigo?

El proyecto usa **Clean Architecture con MVVM**. Esto significa que el codigo esta dividido en capas con responsabilidades claras. Antes de tocar codigo, lean esto:

```
app/
├── core/
│   ├── designsystem/      → Colores, tipografia, formas y componentes visuales reutilizables
│   │   └── components/    → Botones, tarjetas, badges, barras de busqueda, etc.
│   ├── di/                → Configuracion de inyeccion de dependencias (Hilt)
├── data/                  → Todo lo relacionado con datos
│   ├── local/             → Base de datos local (Room)
│   ├── remote/            → Llamadas a la API (Retrofit)
│   ├── mock/              → Datos simulados para desarrollo sin backend
│   └── repository/        → Implementaciones concretas de repositorios
├── domain/                → Logica de negocio pura (sin Android, sin base de datos)
│   ├── model/             → Modelos de datos del negocio
│   ├── repository/        → Interfaces (contratos) de repositorios
│   └── usecase/           → Casos de uso
└── ui/                    → Pantallas y ViewModels
    ├── navigation/        → Rutas y navegacion entre pantallas
    ├── dashboard/         → Pantalla Inicio
    ├── map/               → Pantalla Mapa
    ├── census/            → Pantalla Censo
    ├── events/            → Pantalla Eventos
    ├── requests/          → Pantalla Tramites
    └── auth/              → Login y recuperacion de contrasena
```

### Flujo de datos (como se mueven los datos en la app)

```
Pantalla (Composable)
    ↕  observa StateFlow
ViewModel  ←  unico punto de contacto con la logica
    ↕  llama casos de uso
Use Case  ←  logica de negocio, sin saber de Android
    ↕  usa interfaces
Repository  ←  decide si usa datos locales, remotos o mock
    ↕
Room / Retrofit / MockData
```

**Regla de oro:** la UI solo habla con el ViewModel, el ViewModel solo habla con Use Cases, los Use Cases solo hablan con interfaces de Repository. Nunca saltar capas.

---

## Para el equipo: reglas de codigo

- **Textos visibles al usuario** → siempre en `res/values/strings.xml`, nunca como texto directo en Kotlin
- **Colores y medidas** → siempre desde el tema (`MaterialTheme.colorScheme`, `MaterialTheme.typography`), nunca hardcodeados
- **Idioma del codigo** → ingles (nombres de clases, funciones, variables)
- **Idioma de la UI** → espanol colombiano
- **Cada pantalla nueva debe tener:** `@Preview`, estados Loading / Empty / Error, y al menos un test del ViewModel

---

## Configuracion inicial (primer uso)

### Requisitos
- Android Studio Ladybug (2024.2.1) o superior
- JDK 17
- Android SDK 35

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repo>
   cd tulua-espacio-publico
   ```

2. **Crear el archivo de propiedades locales**

   Copiar el template:
   ```bash
   cp local.properties.template local.properties
   ```
   Editar `local.properties` y agregar la clave de Google Maps:
   ```properties
   MAPS_API_KEY=xxxxxx (No toquen esta parte, solo tengan claro que existe)
   BASE_URL=xxxxx
   ```
   > `local.properties` esta en `.gitignore`. **Nunca lo suban al repositorio.**

3. **Abrir en Android Studio**

   Ir a `File > Open` y seleccionar la carpeta raiz del proyecto. Gradle descargara las dependencias automaticamente.

4. **Seleccionar el flavor `mock`**

   En Android Studio: `View > Tool Windows > Build Variants` → seleccionar **`mockDebug`**.
   Esto permite correr la app con datos simulados sin necesitar el backend.

5. **Ejecutar la app** con Run (▶)

---

## Flavors: mock vs prod

| Flavor | Uso | `USE_MOCK` |
|--------|-----|------------|
| `mock` | Desarrollo diario — datos simulados, sin backend | `true` |
| `prod` | Cuando el backend este listo | `false` |

```bash
# Compilar en modo mock (desarrollo)
./gradlew assembleMockDebug

# Compilar en modo prod
./gradlew assembleProdDebug
```

---

## Stack tecnologico

| Categoria | Tecnologia |
|-----------|------------|
| Lenguaje | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Inyeccion de dependencias | Hilt 2.51.1 |
| Navegacion | Navigation Compose 2.8.3 (rutas type-safe) |
| Base de datos local | Room 2.6.1 |
| Red | Retrofit 2.11.0 + OkHttp 4.12.0 |
| Serializacion | kotlinx.serialization 1.7.3 |
| Almacenamiento local | DataStore Preferences 1.1.1 |
| Imagenes | Coil 2.7.0 |
| Camara | CameraX 1.3.4 |
| Mapas | Google Maps Compose 6.1.0 |
| Coroutines | kotlinx.coroutines 1.9.0 |

---

## Progreso del proyecto

| Fase | Descripcion | Estado |
|------|-------------|--------|
| 1 | Configuracion del proyecto, Design System, Navegacion | Completada |
| 2 | Capa de datos y repositorios mock | Pendiente |
| 3 | Pantalla de Login y sesion | Pendiente |
| 4 | Dashboard con estadisticas | Pendiente |
| 5 | Mapa interactivo del espacio publico | Pendiente |
| 6 | Censo: listado, detalle y nuevo registro | Pendiente |
| 7 | Ferias y eventos con calendario | Pendiente |
| 8 | Asignacion temporal de espacios | Pendiente |
| 9 | Bandeja de tramites y solicitudes | Pendiente |
| 10 | Reportes y exportacion | Pendiente |
| 11 | Pulido, accesibilidad y entrega final | Pendiente |

---

## Integrantes del equipo

| Nombre | Codigo |
|--------|--------|
| Michael Perez | 2266121 |
| Santiago Sanchez | 2359606 |
| Juan Manuel Perez | 2266033 |
| Julian Cortes Navarro | 2159816 |
| Juan Vargas | 2380678 |
| Nicolas Restrepo | 2380471 |
| _(por definir)_ | |

---

## Licencia

Proyecto academico — Universidad del Valle
