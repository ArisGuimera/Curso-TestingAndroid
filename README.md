# Masterclass Testing Android — App de Notas 📝✅

Proyecto base para una **masterclass de testing en Android**: una app de notas pequeña pero realista sobre la que se construyen los **tres tipos de test** —unitarios, de integración y de UI— con Kotlin, Jetpack Compose y Room.

La idea: ver lo **rápido, potente y accesible** que es testear una app real, de lo más simple a lo más jugoso.

---

## 🌿 Ramas

El repo está pensado para seguirse en directo. Tienes dos puntos de partida:

| Rama | Qué contiene | Para qué |
|------|--------------|----------|
| **`inicio`** | La app **con un bug sembrado** y **sin ningún test** (las dependencias de testing ya están listas). | Punto de partida: aquí escribes los tests en vivo. |
| **`solucion`** (= `main`) | La app **con el bug arreglado** y **todos los tests** en verde. | El resultado final / referencia. |

> 🐛 **El bug del directo:** en `inicio`, `GetNotesSummaryUseCase` calcula el porcentaje de notas importantes con **división entera** (`important * 100 / total`). Con 2 de 3 notas importantes muestra **66 %** en vez de 67 %. Un test parametrizado lo caza al instante.

```bash
git checkout inicio     # empezar desde cero
git checkout solucion   # ver el resultado
```

---

## 🧪 Los tres tipos de test

| Tipo | Dónde | Qué prueba | Herramientas |
|------|-------|------------|--------------|
| **Unitario** | `app/src/test` | `ValidateTitleUseCase` y `GetNotesSummaryUseCase` (**parametrizados**) y `NotesViewModel` | JUnit4, **MockK**, **Turbine**, coroutines-test |
| **Integración** | `app/src/androidTest` | `NoteDao` sobre una base de datos **Room en memoria** | Room testing, AndroidJUnit4 |
| **UI** | `app/src/androidTest` | La pantalla de notas: escribir, guardar y filtrar | **Compose UI Test** (`createAndroidComposeRule`) |

---

## ▶️ Cómo ejecutar los tests

| Tipo | Comando |
|------|---------|
| **Unitarios** (JVM, sin dispositivo) | `./gradlew testDebugUnitTest` |
| **Integración + UI** (emulador/dispositivo) | `./gradlew connectedDebugAndroidTest` |

> ⚠️ **Importante para la parte de UI:** Espresso 3.6.1 **no es compatible con Android 15/16** (`InputManager.getInstance` fue eliminado). Ejecuta los tests instrumentados en un **emulador con API ≤ 34** (p. ej. Pixel 6 API 34). Los unitarios y los de Room funcionan en cualquier configuración.

---

## 🏗️ Estructura

```
app/src/main/java/com/aristidevs/masterclass/
├─ MainActivity.kt                 # Wiring manual (sin DI): BD, repositorio y ViewModel
├─ notes/
│  ├─ data/                        # Note, NoteDao, NotesDatabase, RoomNotesRepository
│  ├─ domain/                      # ValidateTitleUseCase, GetNotesSummaryUseCase, NotesRepository
│  └─ ui/                          # NotesScreen (Compose), NotesViewModel
└─ ui/theme/                       # Tema Material 3

docs/                              # Spec y plan de implementación de la masterclass
```

Arquitectura **Clean Architecture ligera** (data / domain / ui), una sola pantalla, sin inyección de dependencias para que todo el cableado sea visible.

---

## 🧰 Stack

Kotlin · Jetpack Compose · Room · Coroutines/Flow · JUnit4 · MockK · Turbine · Compose UI Test

---

## 🎓 Sobre la masterclass

Este proyecto es el material de una masterclass de ~2h sobre testing en Android impartida por [AristiDevs](https://github.com/AristiDevs). El recorrido sube por la *pirámide de testing* (unitarios → integración → UI) cazando un bug real por el camino.
