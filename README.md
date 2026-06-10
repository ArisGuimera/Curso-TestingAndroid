# Masterclass Testing Android — App de Notas (rama `inicio`) 📝

Punto de partida de la **masterclass de testing en Android**: la app de notas funcionando, **sin ningún test todavía**. Aquí es donde escribimos los tests en vivo.

> 👉 ¿Quieres ver el resultado final con todos los tests? Cámbiate a la rama **`solucion`**:
> ```bash
> git checkout solucion
> ```

## La app

App de notas con **Jetpack Compose + Room** (Clean Architecture ligera, sin inyección de dependencias). Permite crear notas, marcarlas como importantes, filtrar por "solo importantes" y ver un resumen.

Las **dependencias de testing ya están configuradas** en Gradle (JUnit4, MockK, Turbine, Room testing, Compose UI Test) y la UI ya expone `testTag`s, así que puedes empezar a escribir tests sin pelearte con la configuración.

## Ejecutar la app

Ábrela en Android Studio y pulsa **Run**, o:

```bash
./gradlew installDebug
```

## Stack

Kotlin · Jetpack Compose · Room · Coroutines/Flow
