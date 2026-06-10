# Spec — Set de tests para masterclass de captación (Testing Android)

**Fecha:** 2026-06-10
**Autor:** AristiDevs (con Claude)
**Estado:** Aprobado, pendiente de implementación

---

## 1. Contexto y objetivo

Proyecto `Masterclass`: app de **Notas** (Jetpack Compose + Room, Clean Architecture ligera, sin DI).

Se usará como base de una **masterclass de testing en Android de ~2 horas**. El objetivo NO es formar exhaustivamente, sino **captar**: que el público vea lo potente y accesible que es testear y **quiera comprar el curso completo**.

**Principios de diseño:**
- **Impacto y variedad > rigor.** Cada pieza es una demo corta de "mira qué potente", no una clase a fondo.
- **No profundizar.** Se muestra el WOW de cada herramienta sin explicar cada API en detalle.
- **Audiencia amplia:** saben Kotlin/Compose, poco o nada de testing. De cero a lo jugoso.
- **YAGNI:** lo que no entre en 2h o no sume al gancho, se menciona como "esto y más, en el curso".

## 2. Narrativa (opción A — pirámide con gancho de dolor)

1. **Dolor:** se arranca mostrando un bug real en producción.
2. **Ascenso:** se sube por la pirámide de testing — unitarios → integración → UI — y cada escalón tiene su momento WOW.
3. **Cierre:** se recuerda el bug del principio (ya cazado por un test) y se lanza el pitch del curso.

El clímax visual es el **emulador manejándose solo** (test de UI). No se usa screenshot testing (fuera de alcance).

## 3. El bug sembrado (hilo conductor)

En `GetNotesSummaryUseCase`, el porcentaje de notas importantes se calcula con **división entera sin redondeo**:

```kotlin
// BUG sembrado (rama inicio):
val percentage = if (total == 0) 0 else (important * 100) / total

// Correcto (rama solucion):
val percentage = if (total == 0) 0 else ((important * 100.0) / total).roundToInt()
```

**Síntoma visible:** con 2 de 3 notas importantes, el resumen muestra **66%** en vez de 67%. Bug sutil, realista ("el que nadie ve hasta que un cliente se queja"), y que **falla en varios casos de redondeo** — ideal para que el test **parametrizado** lo cace con varios rojos de golpe.

## 4. Guion por bloques (~2h)

| Bloque | Tiempo | Contenido | WOW |
|--------|--------|-----------|-----|
| **0. Apertura** | ~12 min | Demo de la app. Se enseña el 66% incorrecto. "Esto está en producción." Pirámide de testing en 1 slide. | El dolor |
| **1. Unitarios** | ~45 min | (a) Primer test con `ValidateTitleUseCase` (qué es un assert). (b) **Parametrizados**: un test valida ~15 títulos. (c) **Cazar el bug** del % con un parametrizado (rojo) → arreglar `GetNotesSummaryUseCase` → verde. (d) `NotesViewModel` con **MockK** (mockear el repo en 1 línea). (e) **Turbine** para verificar las emisiones del `StateFlow`. | 15 casos de golpe · mockear en 1 línea · ver el flujo de estados · el bug cae |
| **2. Integración** | ~22 min | `NoteDao` con **Room in-memory** (`inMemoryDatabaseBuilder`): insert, query, orden DESC, `@Before/@After`. | BD real, desechable, sin tocar producción |
| **3. UI** | ~30 min | `NotesScreen` con `createAndroidComposeRule`: `testTag`, `performTextInput`, `performClick`, filtro "solo importantes" (`assertDoesNotExist`). | El emulador se maneja **solo** (clímax visual) |
| **4. Cierre + pitch** | ~11 min | Recap de la pirámide con los archivos vistos. "¿El bug del principio? Un test de 3 líneas." Gancho: en el curso → TDD, CI, coverage, navegación, etc. | El cierre que vende |

Material pensado con margen: si el directo va justo, lo primero que se recorta es Turbine (d/e del bloque 1) y se acorta el bloque 2.

## 5. Set de tests detallado

Todos sobre la app de Notas. Reemplazan a los 7 tests de ejemplo actuales.

### Unitarios (`src/test`)
| Archivo | Qué prueba | Herramienta / técnica |
|---------|------------|------------------------|
| `ValidateTitleUseCaseTest` | Validación de título: 1 test simple + **parametrizado** con ~15 casos (válidos/ inválidos) | JUnit4 `@Parameterized` |
| `GetNotesSummaryUseCaseTest` | Resumen y **porcentaje** (casos de redondeo). Es el que **caza el bug** sembrado | JUnit4 `@Parameterized` |
| `NotesViewModelTest` | ViewModel: `canSave`, guardar nota, filtro, resumen. Repo como **doble** y verificación de interacción. Emisiones de estado con **Turbine** | MockK + kotlinx-coroutines-test + Turbine |
| `MainDispatcherRule` | Helper: fija `Dispatchers.Main` (`UnconfinedTestDispatcher`) | kotlinx-coroutines-test |

### Integración (`src/androidTest`)
| Archivo | Qué prueba | Herramienta |
|---------|------------|-------------|
| `NoteDaoTest` | Room real en memoria: insert, query (`getNotesOnce` y `Flow.first()`), orden DESC | Room in-memory + AndroidJUnit4 |

### UI (`src/androidTest`)
| Archivo | Qué prueba | Herramienta |
|---------|------------|-------------|
| `NotesScreenTest` | Guardar nota → aparece en lista; filtro "solo importantes" oculta/muestra | Compose UI Test (`createAndroidComposeRule`) |

## 6. Arsenal y dependencias nuevas

Añadir a `gradle/libs.versions.toml` y `app/build.gradle.kts`:
- **MockK** (`io.mockk:mockk`, ~1.13.x) → `testImplementation`
- **Turbine** (`app.cash.turbine:turbine`, ~1.x) → `testImplementation`
- Parametrizados: JUnit4 nativo (`org.junit.runners.Parameterized`), sin dependencia extra.

Se mantienen: JUnit4, kotlinx-coroutines-test, Room testing, Compose UI test, Espresso (transitivo).

## 7. Estructura de ramas

| Rama | Contenido |
|------|-----------|
| `inicio` | App **con el bug sembrado** (% por división entera), `testTag`s y deps de testing (MockK/Turbine) ya en Gradle, **sin archivos de test**. Punto de partida del directo. |
| `solucion` | App con el bug **arreglado** + el **set nuevo completo**, todo en verde. Referencia/chuleta. |
| `ejemplos-originales` | Backup de los 7 tests de ejemplo actuales, por si se quieren consultar. |
| `main` | Documentación de diseño (este spec). |

Flujo en directo: programar en `inicio`, con `solucion` abierta en otra pantalla como referencia.

## 8. Criterios de éxito

- En `solucion`: `./gradlew testDebugUnitTest` en **verde**; tests de UI en verde en emulador **API ≤ 34**.
- En `inicio`: la app compila y arranca, y **muestra el bug** (66%); no hay archivos de test.
- Cada bloque del guion tiene una demo WOW reproducible.
- El recorrido completo cabe en ~2h con margen de recorte.

## 9. Notas técnicas

- **Espresso 3.6.1 es incompatible con Android 15/16** (`InputManager.getInstance` eliminado): los tests de UI fallan en el Pixel 8a (Android 16). **Correr la parte de UI en emulador API ≤ 34** (`Pixel_6_API_34`). Room/integración funciona en cualquier API.
- Warning de deprecación de Gradle (Gradle 10) presente; inofensivo para el directo.

## 10. Fuera de alcance (se mencionan como "en el curso")

Screenshot testing (Paparazzi/Roborazzi), CI, code coverage, tests de navegación, TDD en profundidad, inyección de dependencias para tests.
