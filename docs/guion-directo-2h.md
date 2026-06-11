# Guion del directo — Masterclass Testing Android (~2h)

> **Objetivo: CAPTACIÓN.** Que el público vea lo potente y accesible que es testear y **quiera el curso**. Impacto y variedad > rigor. No profundizar; encadenar momentos "ajá".

## 🎒 Setup previo (antes de empezar)

- [ ] Emulador o tu dispositivo arrancado (los tests de UI/Room son deterministas; con Espresso 3.7.0 funcionan en cualquier API, incluida Android 16).
- [ ] **Dos pantallas**: una en `inicio` (donde programas) y otra con `solucion` abierta como referencia.
- [ ] App de `inicio` ya instalada en el dispositivo.
- [ ] Fuente del IDE grande, panel de tests visible.
- [ ] Comandos a mano: `git checkout inicio` / `git checkout solucion`, `./gradlew testDebugUnitTest`, `./gradlew connectedDebugAndroidTest`.

## ⏱️ Resumen de bloques

| Bloque | Tiempo | WOW |
|--------|--------|-----|
| 0. Apertura + el dolor | 0:00–0:12 | El bug del 66 % en producción |
| 1. Unitarios | 0:12–0:54 | 15 casos de golpe · el bug cae · mock en 1 línea |
| 2. Integración | 0:54–1:16 | BD real, en memoria, desechable |
| 3. UI | 1:16–1:50 | Refactor → testable · emulador autómata |
| 4. Cierre + pitch | 1:50–2:00 | "test = mejor arquitectura" + CTA |

---

## 0 · Apertura + el dolor (0:00–0:12)

**Haces:** demo de la app en `inicio` — crear nota, marcar importante, filtrar "solo importantes", borrar con la papelera.

**El gancho 🪝:** crea **3 notas y marca 2 como importantes**. El resumen dice **"66 %"**.
> *"2 de 3 es 67 %… y la app dice 66. Un bug que nadie ve hasta que un cliente se queja. ¿Cuántos tenéis ahora mismo en producción sin saberlo?"*

**Cierras la intro:** pirámide de testing en 1 slide (muchos unitarios → algunos de integración → pocos de UI). *"Hoy cazamos ese bug y blindamos la app. Y de paso veréis por qué testear te hace mejor desarrollador."*

---

## 1 · Tests unitarios (0:12–0:54)

**0:12 — Primer test.** Abre `ValidateTitleUseCase`. *"Un test es una función que comprueba otra función."* Escribe uno:
```kotlin
@Test fun titulo_valido() { assertTrue(validateTitle("Mi nota")) }
```
Córrelo → **verde en milisegundos, sin emulador**. 🔥 *velocidad*.

**0:20 — Parametrizados.** *"¿Y si quiero probar 15 títulos? ¿15 tests? No."* Conviértelo a `@RunWith(Parameterized::class)` con la tabla de casos. Córrelo → **15 verdes de golpe** en el árbol. 🔥
> Truco para contarlo: *"`data()` es la tabla de casos; el constructor recibe cada fila; el test se ejecuta una vez por fila."*

**0:28 — Cazar el bug 🎯.** *"¿Os acordáis del 66 %?"* Abre `GetNotesSummaryUseCase`. Escribe el test parametrizado del porcentaje con los casos `2 imp / 1 no → 67` y `1 imp / 5 no → 17`. Córrelo → **🔴 ROJO** (da 66 y 16). *"El ojo no lo vio; el test sí."* Cambia la división entera por redondeo:
```kotlin
((important * 100.0) / total).roundToInt()
```
→ **🟢**. 🔥🔥 *clímax técnico*.

**0:40 — ViewModel + MockK.** *"El ViewModel depende del repositorio… no quiero base de datos en un test unitario. Lo finjo."*
```kotlin
val repository: NotesRepository = mockk(relaxed = true)
every { repository.getNotes() } returns flowOf(emptyList())
// ... guardar ...
coVerify { repository.addNote("Mi nota", "Contenido", true) }
```
🔥 *"mockeo la base de datos en una línea."*

**0:49 — Turbine** *(recortable si vas justo).* Testear el `StateFlow` de estados:
```kotlin
viewModel.uiState.test { assertEquals(0, awaitItem().notes.size); /* … */ }
```
🔥 *"veo cada emisión del estado."*

→ Corre toda la suite unit: **27 casos verdes en ~1 segundo**.

---

## 2 · Tests de integración (0:54–1:16)

*"Mockear está bien… ¿pero y si mi consulta SQL está mal? El mock no lo pilla. Necesito la BD de verdad."*

Abre `NoteDaoTest`. **Room en memoria**:
```kotlin
db = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java).build()
```
`@Before`/`@After`, insertas dos notas, compruebas cantidad y **orden DESC**. Córrelo en el dispositivo → verde. 🔥 *"SQLite de verdad, pero en memoria: se borra al acabar. Aquí sí pruebo la query."* Añade el test de **borrar**.

**Contraste:** *"esto tarda segundos (necesita dispositivo); los unitarios, milisegundos. Por eso pocos de estos."*

---

## 3 · Tests de UI (1:16–1:50) — el arco que engancha

**1:16 — El problema.** Muestra la pantalla de `inicio`: `NotesScreen` está **pegada al ViewModel**. *"Quiero testear esta pantalla… pero para crearla necesito el ViewModel entero, Room, el teclado… un lío y además frágil."*

**1:22 — Refactor en vivo 🛠️.** Extrae el contenido a un composable **sin estado**:
```kotlin
@Composable fun NotesContent(state: NotesUiState, onTitleChanged: (String) -> Unit, /* … callbacks */) { … }
```
La pantalla queda en dos: una que **conecta** el ViewModel y otra **tonta** que solo pinta. *"Esto se llama **state hoisting**."*

**1:34 — Ahora SÍ se testea.** Con `createComposeRule()` (sin Activity, sin Room, sin teclado): le das un **estado** y compruebas qué pinta; **interactúas** y compruebas que avisa.
```kotlin
composeRule.setContent { NotesContent(state = NotesUiState(notes = …), onSaveClicked = { saved = true }, …) }
composeRule.onNodeWithText("Comprar pan").assertIsDisplayed()
composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick(); assertTrue(saved)
```
→ **6 verdes**, deterministas, y se ve interactuar en el emulador. 🔥🔥

**El cierre que vende 💰:** *"Fijaos: la testabilidad me ha llevado a **mejor arquitectura**. No es teoría — es que el test no me dejaba avanzar de otra forma. Eso es lo que separa a un junior de un senior."*

---

## 4 · Cierre + pitch (1:50–2:00)

- Recap de la pirámide con **tus** archivos: 27 unitarios (ms), 2 de integración, 6 de UI.
- *"¿El bug del 66 %? Lo cazó un test de 3 líneas. Imaginad esto corriendo en cada commit, antes de llegar a producción."*
- **Pitch:** *"Hoy hemos tocado la superficie. En el curso completo: TDD de verdad, inyección de dependencias, CI que ejecuta esto solo, cobertura, tests de navegación…"*
- **CTA:** enlace al curso.

---

## 🔥 Los 8 momentos WOW (checklist en cámara)

1. El bug del **66 %** en la app (dolor).
2. **15 casos** verdes de un solo test parametrizado.
3. El bug **cae en directo** (rojo → fix → verde).
4. **MockK**: mockear la BD en una línea.
5. **Turbine**: ver el flujo de estados.
6. **Room en memoria**: BD real y desechable.
7. **Refactor → testable**: state hoisting motivado por el test.
8. El **emulador autómata** ejecutando la UI.

## 🗣️ Frases de captación (repítelas)

- *"Y esto es solo el principio…"* (en cada bloque).
- *"¿Quién ha tenido un bug así?"* (participación).
- *"El test no me dejó hacerlo mal."* (testing como guía de arquitectura).
- *"Verde no siempre significa probado."* (criterio).
