# Set de tests para masterclass — Plan de implementación

> **For agentic workers:** Use `mobiai-mobile-executing-plans-with-subagents` o `mobiai-mobile-executing-plans` para implementar task-by-task. Los pasos usan checkboxes.

**Goal:** Construir un set de tests nuevo (unit/integración/UI) para la app de Notas, pensado para una masterclass de captación, y dejar el repo listo para GitHub con ramas `inicio` (con bug, sin tests) y `solucion` (arreglado + tests).

**Architecture:** App existente (Compose + Room, Clean Arch ligera, sin DI). Los tests reemplazan a los 7 de ejemplo. Se usan parametrizados (JUnit4), MockK (dobles), Turbine (Flows), Room in-memory y Compose UI test.

**Tech Stack:** Kotlin, JUnit4, MockK, Turbine, kotlinx-coroutines-test, Room testing, Compose UI test.

**Platform:** Android

---

## Estrategia de ramas

Se reconstruye todo desde `main` (que ya tiene app correcta + spec). No hay push aún, así que se reescriben `inicio`/`solucion` localmente.

1. Trabajar en `main`: borrar tests viejos, añadir deps, crear set nuevo, README. → `main` = solución completa + docs.
2. `solucion` ← `main` (`git branch -f solucion main`).
3. `inicio` ← `main` sin tests + bug sembrado + README de arranque.

`main` será la rama por defecto en GitHub (solución completa). `inicio` para seguir el directo.

---

## Task 1: Dependencias (MockK + Turbine)

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`

- [ ] **Step 1: Añadir versiones y librerías en `libs.versions.toml`**

```toml
# [versions]
mockk = "1.13.13"
turbine = "1.2.0"

# [libraries]
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
```

- [ ] **Step 2: Añadir al bloque `dependencies` de `app/build.gradle.kts` (sección unit tests)**

```kotlin
testImplementation(libs.mockk)
testImplementation(libs.turbine)
```

- [ ] **Step 3: Sync** — `./gradlew help` (resuelve dependencias). Expected: BUILD SUCCESSFUL.

---

## Task 2: Tests unitarios

**Files:**
- Create: `app/src/test/java/com/aristidevs/masterclass/notes/MainDispatcherRule.kt` (se mantiene, `UnconfinedTestDispatcher`)
- Create: `app/src/test/java/com/aristidevs/masterclass/notes/domain/ValidateTitleUseCaseTest.kt` (parametrizado)
- Create: `app/src/test/java/com/aristidevs/masterclass/notes/domain/GetNotesSummaryUseCaseTest.kt` (parametrizado, caza el bug)
- Create: `app/src/test/java/com/aristidevs/masterclass/notes/ui/NotesViewModelTest.kt` (MockK + Turbine)
- Delete: `app/src/test/java/com/aristidevs/masterclass/notes/data/FakeNotesRepository.kt` (se sustituye por MockK)

- [ ] **Step 1: `ValidateTitleUseCaseTest` parametrizado** (~15 casos)

```kotlin
@RunWith(Parameterized::class)
class ValidateTitleUseCaseTest(
    private val title: String,
    private val expected: Boolean,
) {
    private val validateTitle = ValidateTitleUseCase()

    @Test
    fun validates_title() {
        assertEquals(expected, validateTitle(title))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "\"{0}\" -> {1}")
        fun data() = listOf(
            arrayOf("", false),
            arrayOf("   ", false),
            arrayOf("ab", false),
            arrayOf("a".repeat(41), false),
            arrayOf("123456", false),
            arrayOf("Nota@rara", false),
            arrayOf("Mi  nota", false),
            arrayOf("!?.,", false),
            arrayOf("Mi nota", true),
            arrayOf("  Mi nota válida  ", true),
            arrayOf("Título válido 123!?.,", true),
            arrayOf("abc", true),
            arrayOf("Compra: pan, leche!", false), // ':' no permitido
            arrayOf("Reunión 10am", true),
            arrayOf("a".repeat(40), true),
        )
    }
}
```

- [ ] **Step 2: `GetNotesSummaryUseCaseTest` parametrizado** (caza el bug del %)

```kotlin
@RunWith(Parameterized::class)
class GetNotesSummaryUseCaseTest(
    private val important: Int,
    private val nonImportant: Int,
    private val expectedPercentage: Int,
) {
    private val getNotesSummary = GetNotesSummaryUseCase()

    @Test
    fun calculates_important_percentage() {
        val notes = buildList {
            repeat(important) { add(Note(title = "imp$it", content = "", important = true)) }
            repeat(nonImportant) { add(Note(title = "no$it", content = "", important = false)) }
        }
        assertEquals(expectedPercentage, getNotesSummary(notes).importantPercentage)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0} imp / {1} no -> {2}%")
        fun data() = listOf(
            arrayOf(0, 0, 0),
            arrayOf(2, 0, 100),
            arrayOf(0, 3, 0),
            arrayOf(2, 1, 67),  // bug (división entera) da 66
            arrayOf(1, 2, 33),
            arrayOf(1, 5, 17),  // bug da 16
            arrayOf(1, 0, 100),
            arrayOf(5, 1, 83),  // bug da 83 también -> ok
        )
    }
}
```

- [ ] **Step 3: `NotesViewModelTest` con MockK + Turbine**

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: NotesRepository = mockk(relaxed = true)

    @Test
    fun canSave_is_false_when_title_invalid() = runTest {
        every { repository.getNotes() } returns flowOf(emptyList())
        val viewModel = NotesViewModel(repository)

        viewModel.onTitleChanged("ab")

        assertFalse(viewModel.uiState.value.canSave)
    }

    @Test
    fun saving_valid_note_calls_repository_and_clears_inputs() = runTest {
        every { repository.getNotes() } returns flowOf(emptyList())
        coEvery { repository.addNote(any(), any(), any()) } just Runs
        val viewModel = NotesViewModel(repository)

        viewModel.onTitleChanged("  Mi nota  ")
        viewModel.onContentChanged("Contenido")
        viewModel.onImportantChanged(true)
        viewModel.saveNote()

        coVerify { repository.addNote("Mi nota", "Contenido", true) }
        assertEquals("", viewModel.uiState.value.title)
    }

    @Test
    fun state_emits_notes_when_repository_emits() = runTest {
        val notesFlow = MutableStateFlow(emptyList<Note>())
        every { repository.getNotes() } returns notesFlow
        val viewModel = NotesViewModel(repository)

        viewModel.uiState.test {
            assertEquals(0, awaitItem().notes.size)
            notesFlow.value = listOf(Note(id = 1, title = "A", content = "", important = true))
            assertEquals(1, awaitItem().notes.size)
            cancelAndConsumeRemainingEvents()
        }
    }
}
```

- [ ] **Step 4: Verificar unit verdes**

Run: `./gradlew testDebugUnitTest`
Expected: PASS (todos verdes; en `solucion` el % usa `roundToInt`).

- [ ] **Step 5: Commit**

---

## Task 3: Integración (Room)

**Files:**
- Create: `app/src/androidTest/java/com/aristidevs/masterclass/notes/data/NoteDaoTest.kt`

- [ ] **Step 1: Test Room in-memory** (insert + query + orden DESC; equivalente al original limpio)

```kotlin
@RunWith(AndroidJUnit4::class)
class NoteDaoTest {
    private lateinit var db: NotesDatabase
    private lateinit var dao: NoteDao

    @Before fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java).build()
        dao = db.noteDao()
    }

    @After fun tearDown() = db.close()

    @Test fun insert_and_query_returns_notes_in_desc_order() = runTest {
        dao.insert(Note(title = "Primera", content = "", important = false))
        dao.insert(Note(title = "Segunda", content = "", important = true))

        val notes = dao.getNotesOnce()

        assertEquals(2, notes.size)
        assertEquals("Segunda", notes[0].title)
        assertEquals("Primera", notes[1].title)
    }
}
```

- [ ] **Step 2: Verificar en emulador API ≤ 34** — `./gradlew connectedDebugAndroidTest` (con `Pixel_6_API_34`). Expected: PASS.

---

## Task 4: UI (Compose)

**Files:**
- Create: `app/src/androidTest/java/com/aristidevs/masterclass/notes/ui/NotesScreenTest.kt`

- [ ] **Step 1: Test de UI** (guardar nota + filtro)

```kotlin
@RunWith(AndroidJUnit4::class)
class NotesScreenTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test fun save_note_shows_item_in_list() {
        composeRule.onNodeWithTag(TITLE_INPUT_TAG).performTextInput("Nota UI")
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
        composeRule.onNodeWithText("Nota UI").assertIsDisplayed()
    }

    @Test fun important_only_filter_hides_non_important() {
        composeRule.onNodeWithTag(TITLE_INPUT_TAG).performTextInput("Normal UI")
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
        composeRule.onNodeWithTag(TITLE_INPUT_TAG).performTextInput("Importante UI")
        composeRule.onNodeWithTag(IMPORTANT_SWITCH_TAG).performClick()
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()

        composeRule.onNodeWithTag(IMPORTANT_ONLY_TOGGLE_TAG).performClick()

        composeRule.onNodeWithText("Importante UI").assertIsDisplayed()
        composeRule.onNodeWithText("Normal UI").assertDoesNotExist()
    }
}
```

- [ ] **Step 2: Verificar en emulador API ≤ 34** — `./gradlew connectedDebugAndroidTest`. Expected: PASS.

---

## Task 5: README para GitHub

**Files:**
- Modify: `README.md`

- [ ] **Step 1: Reescribir README** explicando: propósito (masterclass de testing), la app, las ramas `inicio`/`solucion`, cómo correr cada tipo de test, el set de tests, y la nota de Espresso/API≤34. (Contenido en Task de ejecución.)

- [ ] **Step 2: Commit**

---

## Task 6: Ramas finales

- [ ] **Step 1:** Con `main` ya completo (tasks 1-5), actualizar `solucion`:

```bash
git branch -f solucion main
```

- [ ] **Step 2:** Recrear `inicio` desde `main`, sin tests y con el bug sembrado:

```bash
git checkout -B inicio main
git rm -r app/src/test app/src/androidTest
```

- [ ] **Step 3:** Sembrar el bug en `inicio` — `GetNotesSummaryUseCase.kt`:

```kotlin
// inicio (con bug): división entera, sin redondeo
val percentage = if (total == 0) 0 else (important * 100) / total
```

(quitar el import `kotlin.math.roundToInt` si queda sin uso)

- [ ] **Step 4:** README de arranque en `inicio` (versión "rama de inicio") + commit.

- [ ] **Step 5:** Volver a `main`: `git checkout main`.

---

## Self-review (cobertura del spec)

- Bug sembrado → Task 6 Step 3 ✓
- Parametrizados → Task 2 Steps 1-2 ✓
- MockK + Turbine → Task 2 Step 3 ✓
- Room in-memory → Task 3 ✓
- Compose UI → Task 4 ✓
- Borrar tests antiguos → reemplazo en Tasks 2-4 + borrar `FakeNotesRepository` ✓
- README GitHub → Task 5 ✓
- Ramas inicio/solucion → Task 6 ✓
- Espresso/API≤34 → Tasks 3-4 Step 2 ✓

## Criterios de éxito
- `solucion`/`main`: `testDebugUnitTest` verde; UI verde en API ≤ 34.
- `inicio`: compila, muestra 66% (bug), sin archivos de test.
