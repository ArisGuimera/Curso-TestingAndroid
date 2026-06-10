# Masterclass Testing Android – Proyecto Notas

Proyecto pensado como **guion** para una masterclass de ~1h sobre testing en Android: **tests unitarios**, **de integración** y **de UI** con Kotlin, Jetpack Compose y Room.

La app tiene **una pantalla** de notas (persistencia en Room) y varios tipos de tests que ejercitan código real de la app.

---

## 1. Cómo ejecutar los tests

| Tipo | Comando |
|------|--------|
| **Unitarios** (JVM, sin dispositivo) | `./gradlew testDebugUnitTest` |
| **Instrumentados** (integración + UI, con emulador/dispositivo) | `./gradlew connectedDebugAndroidTest` |

---

## 2. Tipos de test y qué hace cada uno

### Tests unitarios (`src/test`)

Se ejecutan en la JVM, sin Android. Sirven para probar **lógica de dominio** y **ViewModel** con dependencias falsas.

| Archivo | Qué prueba | Qué demuestra |
|---------|------------|----------------|
| **`ValidateTitleUseCaseTest`** | `ValidateTitleUseCase`: validación de título (longitud 3–40, al menos una letra, caracteres permitidos, sin espacios dobles) a través de `invoke`. | Tests de **lógica pura** de dominio modelada como caso de uso. Casos borde y nomenclatura clara. |
| **`GetNotesSummaryUseCaseTest`** | `GetNotesSummaryUseCase`: dado una lista de `Note`, devuelve `NotesSummary` (total, importantes, no importantes, porcentaje). Casos: lista vacía, solo no importantes, solo importantes, mezcla, redondeos. | Tests de un **caso de uso** con `invoke`, varias aserciones por test y estructura given/when/then en el cuerpo. |
| **`NotesViewModelTest`** | `NotesViewModel` usando `FakeNotesRepository`: uso de `ValidateTitleUseCase` para `canSave`; guardar nota válida (limpia inputs, aparece en state); filtro “solo importantes”; actualización de `notesSummary` al guardar. | Tests de **ViewModel** con `kotlinx-coroutines-test`, `MainDispatcherRule` y un fake del repositorio (sin mocks pesados). |

**Apoyo en tests:**  
- `MainDispatcherRule`: fija `Dispatchers.Main` con `StandardTestDispatcher` en tests de corutinas.  
- `FakeNotesRepository`: implementa `NotesRepository` en memoria con `StateFlow`; se usa en los tests del ViewModel.

---

### Tests de integración (`src/androidTest`)

Se ejecutan en dispositivo o emulador. Prueban **componentes reales** (p. ej. Room) en entorno Android.

| Archivo | Qué prueba | Qué demuestra |
|---------|------------|----------------|
| **`NoteDaoTest`** | **Room DAO**: base de datos in-memory con `Room.inMemoryDatabaseBuilder`, insert de dos notas, consulta con `getNotesOnce()`. Comprueba cantidad y orden (DESC por id). | Cómo testear **persistencia** con Room sin tocar la BD real, usando una BD en memoria. |

---

### Tests de UI (`src/androidTest`)

Se ejecutan en dispositivo o emulador. Prueban la **pantalla Compose** como la vería el usuario.

| Archivo | Qué prueba | Qué demuestra |
|---------|------------|----------------|
| **`NotesScreenTest`** | **Pantalla de notas**: escribir título → pulsar Guardar → comprobar que la nota aparece en la lista; crear nota normal y nota importante → activar “Solo importantes” → solo se muestra la importante. | Tests de **UI con Compose**: `createAndroidComposeRule<MainActivity>()`, `testTag` para localizar nodos, `performTextInput`, `performClick`, `assertIsDisplayed`, `assertDoesNotExist`. |

---

## 3. Estructura de la app (resumen)

- **Entrada:** `MainActivity` crea directamente la BD Room, `RoomNotesRepository` y `NotesViewModel` (sin DI ni `Application`).
- **Datos:** `Note`, `NoteDao`, `NotesDatabase`, `RoomNotesRepository`; interfaz `NotesRepository` en dominio.
- **Dominio:** `ValidateTitleUseCase`, `GetNotesSummaryUseCase` (clases con `invoke`), `NotesRepository`.
- **UI:** `NotesViewModel` (state con `notes`, `notesSummary`, `canSave`, etc.), `NotesScreen` con `testTag` en campos, botón, filtro, resumen y lista.

El **resumen de notas** (“X notas (Y importantes, Z%)”) viene de `GetNotesSummaryUseCase`; el ViewModel lo calcula y lo expone en `NotesUiState.notesSummary`.

---

## 4. TestTags de la pantalla

En `NotesScreen.kt` se usan estos tags para los tests de UI:

| Constante | Valor | Dónde |
|-----------|--------|--------|
| `TITLE_INPUT_TAG` | `"titleInput"` | Campo título |
| `CONTENT_INPUT_TAG` | `"contentInput"` | Campo contenido |
| `IMPORTANT_SWITCH_TAG` | `"importantSwitch"` | Checkbox “Importante” |
| `SAVE_BUTTON_TAG` | `"saveButton"` | Botón Guardar |
| `IMPORTANT_ONLY_TOGGLE_TAG` | `"importantOnlyToggle"` | Filtro “Solo importantes” |
| `NOTES_SUMMARY_TAG` | `"notesSummary"` | Texto del resumen (total, importantes, %) |
| `NOTES_LIST_TAG` | `"notesList"` | `LazyColumn` de notas |

---

## 5. Guion sugerido para la masterclass

- **Unitarios (5–10 min):** Explicar qué es un test unitario. Mostrar `ValidateTitleUseCaseTest` y `GetNotesSummaryUseCaseTest` (given/when/then, varias aserciones). Mencionar `NotesViewModelTest` y el uso de `FakeNotesRepository` y `MainDispatcherRule`.
- **Integración (10–15 min):** Qué es un test de integración. Abrir `NoteDaoTest`, explicar `inMemoryDatabaseBuilder` y qué se está comprobando.
- **UI (15–20 min):** Qué es un test de UI. Recorrer `NotesScreenTest`: `testTag`, interacción con la pantalla y aserciones sobre lo que ve el usuario.
- **Cierre (5 min):** Resumir los tres tipos y cuándo usar cada uno.
