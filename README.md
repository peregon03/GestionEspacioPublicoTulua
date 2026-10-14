# Tulua Espacio Publico

Aplicacion movil Android para la gestion del espacio publico del Municipio de Tulua, Valle del Cauca, Colombia.

La aplicacion permite a los inspectores y funcionarios municipales gestionar el censo de vendedores ambulantes, reportar denuncias, consultar el mapa de ocupacion del espacio publico, hacer seguimiento a eventos y radicar tramites administrativos.

---

## Requisitos previos

- Android Studio Ladybug (2024.2.1) o superior
- JDK 17
- Android SDK 35
- Cuenta de Google Cloud con Maps SDK for Android habilitado

---

## Configuracion inicial

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-org/tulua-espacio-publico.git
   cd tulua-espacio-publico
   ```

2. **Copiar el template de propiedades locales**
   ```bash
   cp local.properties.template local.properties
   ```

3. **Agregar la clave de Google Maps**

   Edite `local.properties` y reemplace los valores:
   ```properties
   MAPS_API_KEY=AIzaSy...tu_clave_aqui
   BASE_URL=https://api.tulua.gov.co/api/v1/
   ```

   > **Nota:** `local.properties` esta excluido de git (`.gitignore`). Nunca lo suba al repositorio.

4. **Abrir en Android Studio**

   Abra el proyecto desde la raiz. Gradle sincronizara automaticamente todas las dependencias.

---

## Sabores (Product Flavors)

El proyecto tiene dos sabores de compilacion:

| Sabor  | `USE_MOCK` | Descripcion                                        |
|--------|------------|----------------------------------------------------|
| `mock` | `true`     | Usa repositorios de datos simulados (mock). Ideal para desarrollo y pruebas sin backend. |
| `prod` | `false`    | Conecta al backend real usando `BASE_URL`.         |

### Seleccionar sabor en Android Studio

1. Abra la ventana **Build Variants** (`View > Tool Windows > Build Variants`)
2. Seleccione el variant deseado, por ejemplo `mockDebug` o `prodRelease`

### Compilar desde linea de comandos

```bash
# Sabor mock (desarrollo)
./gradlew assembleMockDebug

# Sabor prod (produccion debug)
./gradlew assembleProdDebug

# Sabor prod release
./gradlew assembleProdRelease
```

---

## Arquitectura

El proyecto sigue **Clean Architecture con MVVM** organizado en capas:

```
app/
├── core/
│   ├── designsystem/          # Tokens de diseno: colores, tipografia, formas, tema
│   │   └── components/        # Componentes reutilizables de UI
│   ├── data/                  # Repositorios, fuentes de datos, modelos de datos
│   │   ├── local/             # Room DAOs y entidades
│   │   ├── remote/            # Retrofit API services y DTOs
│   │   └── repository/        # Implementaciones de repositorios
│   ├── domain/                # Casos de uso, modelos de dominio, interfaces de repositorio
│   └── common/                # Utilidades, extensiones, constantes
├── ui/
│   ├── navigation/            # Grafo de navegacion, destinos, bottom nav
│   ├── dashboard/             # Pantalla Inicio: ViewModel + Screen
│   ├── map/                   # Pantalla Mapa
│   ├── census/                # Pantalla Censo
│   ├── events/                # Pantalla Eventos
│   ├── requests/              # Pantalla Tramites
│   └── auth/                  # Login, recuperacion de contrasena
└── di/                        # Modulos de inyeccion de dependencias (Hilt)
```

### Flujo de datos

```
UI (Composable)
    ↕ StateFlow / collectAsState
ViewModel (Hilt ViewModel)
    ↕ suspend fun / Flow
Use Case (domain)
    ↕ Flow / Result
Repository Interface (domain)
    ↑ implementado por
Repository Impl (data)
    ↕ Room DAO  /  Retrofit API  /  DataStore
```

---

## Stack tecnologico

| Categoria       | Tecnologia                                    |
|-----------------|-----------------------------------------------|
| Lenguaje        | Kotlin 2.0.21                                 |
| UI              | Jetpack Compose + Material 3                  |
| DI              | Hilt 2.51.1                                   |
| Navegacion      | Navigation Compose 2.8.3 (rutas type-safe)    |
| Base de datos   | Room 2.6.1                                    |
| Red             | Retrofit 2.11.0 + OkHttp 4.12.0              |
| Serializacion   | kotlinx.serialization 1.7.3                  |
| Almacenamiento  | DataStore Preferences 1.1.1                   |
| Imagenes        | Coil 2.7.0                                    |
| Camara          | CameraX 1.3.4                                 |
| Mapas           | Google Maps Compose 6.1.0                     |
| Coroutines      | kotlinx.coroutines 1.9.0                      |

---

## Instrucciones de compilacion

```bash
# Verificar el proyecto
./gradlew build

# Solo tests unitarios
./gradlew testMockDebugUnitTest

# Lint
./gradlew lintMockDebug

# APK debug mock (listo para instalar)
./gradlew installMockDebug
```

---

## Progreso por fases

| Fase | Descripcion                                          | Estado     |
|------|------------------------------------------------------|------------|
| 1    | Configuracion del proyecto, Design System, Navegacion | Completada |
| 2    | Pantalla Dashboard con estadisticas reales           | Pendiente  |
| 3    | Mapa interactivo de vendedores                       | Pendiente  |
| 4    | Modulo de Censo: listado y detalle de vendedores     | Pendiente  |
| 5    | Autenticacion real con JWT                           | Pendiente  |
| 6    | Modulo de Eventos                                    | Pendiente  |
| 7    | Modulo de Tramites y radicacion                      | Pendiente  |
| 8    | Camara y carga de documentos                         | Pendiente  |
| 9    | Notificaciones push (FCM)                            | Pendiente  |
| 10   | Modo offline y sincronizacion en segundo plano       | Pendiente  |
| 11   | Pruebas end-to-end y publicacion en Play Store       | Pendiente  |

---

## Convenciones de codigo

- **Idioma de UI:** Espanol (Colombia) — todos los textos visibles al usuario en `strings.xml`
- **Idioma de codigo:** Ingles — nombres de clases, funciones, variables y comentarios en ingles
- **Estilo:** Kotlin official code style (enforced por `kotlin.code.style=official`)
- **Formato:** `ktlint` integrado via hooks de pre-commit (configurar localmente con `./gradlew addKtlintCheckGitPreCommitHook`)

---

## Integrantes del equipo

| Nombre                | Código  |
|-----------------------|---------|
| Michael Perez         | 2266121 |
| Santiago Sanchez      | 2359606 |
| Daniel Rojas          | 2569189 |
| Julian Cortes Navarro | 2159816 |
| Juan Vargas           | 2380678 |
| Nicolas Restrepo      | 2380471 |

---

## Licencia

Proyecto académico — Universidad del Valle
