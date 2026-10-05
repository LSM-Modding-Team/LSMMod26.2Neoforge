# CHUNKS_FULL: filas completas de los chunks (archivos, decisiones, riesgos de compilación)

La línea corta de cada chunk vive en `docs/CHUNKS.md`; aquí está el detalle. Nace en la sesión 4 con `B0` y `B0.1`. Un chunk nuevo añade su fila completa aquí **solo cuando sale de la cima de `CHUNKS.md`** (para que `CHUNKS.md` siga siendo corto). Estados: `PLANNED` / `ACCEPTED` / `NOT COMPILED` / `COMPILES` / `WORKS` / `SUPERSEDED`; solo el reporte del usuario los cambia (R3).

---

## B0.1: limpieza de B0 (sesión 3) — `NOT COMPILED` (sin confirmar)

* **Qué hizo:** sin features y sin API nueva de Minecraft.
  * Clave `lang` del asiento corregida: `entity.lsmmod.seat` = "Seat" (antes `entity.lsmmod.chair_seat`, que no coincidía con la entidad registrada `seat`).
  * Receta nueva de `students_desk`: `PPP` / `SCS` / `S S` con `#minecraft:planks`, `minecraft:stick`, `minecraft:chest`.
  * Descripción real en `neoforge.mods.toml` (plantilla en `src/main/templates/META-INF/`).
  * Comentarios corregidos en `ModBlocks`, `ModItems`, `SeatEntity`, `ModEntities` (citaban "README section 9.1" y "Phase 3/4" de un README que no está en el zip).
* **Archivos tocados:** `lang/en_us.json`, `data/lsmmod/recipe/students_desk.json` (nuevo), `neoforge.mods.toml`, y los 4 `.java` solo en comentarios.
* **Decisiones sin preguntar:** la forma de la receta del pupitre de alumno *(propuesta)*; el texto de la descripción *(propuesta)*; **no** se añadió `noSave()` al asiento (API nueva).
* **Dejado fuera a propósito:** autores y créditos del `mods.toml` (no se inventan), `noSave()`, variantes de silla.
* **Verificación:** `ParseAll`: 15 archivos, 0 errores (solo sintaxis, no compilación).
* **Riesgo de compilación:** ninguno nuevo; el único riesgo es un JSON o TOML mal formado, que solo aparece al cargar el juego.
* **Qué probar:** `./gradlew build` y `./gradlew runClient`; pestaña "LSM Mod" con 6 bloques; las sillas se montan; los pupitres ocupan 2 bloques y abren 27 slots; el casillero abre 54; la receta del pupitre de alumno funciona.

---

## B0: baseline (sesión 1, leído en la sesión 2) — `NOT COMPILED` (sin confirmar)

* **Qué es:** lo que ya había al empezar el proyecto de docs. No lo escribió ningún chunk de este proceso.
* **Contenido:** 15 archivos Java (995 líneas); 6 bloques (`high_school_chair`, `elementary_chair`, `teachers_chair` con `ChairBlock` y `SeatEntity`; `students_desk`, `teachers_desk` con `DeskBlock`, `TeachersDeskBlock`, `DeskPart`, `DeskBlockEntity`, 27 slots; `locker` con `LockerBlock`, `LockerBlockEntity`, 54 slots); pestaña creativa `lsm_mod`; modelos, texturas, blockstates, loot tables de bloque, 5 recetas (la del pupitre de alumno falta; la añade B0.1) y `lang/en_us.json`.
* **Paquetes:** raíz (`LSMMod`), `block/`, `entity/`, `registry/`, `client/` (`docs/REFERENCE.md` §4).
* **Decisiones:** ninguna de este proceso; es el código heredado.
* **Discrepancias con el diseño:** lista en `docs/REFERENCE.md` §1 y tabla en `docs/BLOCK_SPECS.md` §3 (sillas, pupitres con inventario, `lang` solo en inglés).
* **Riesgos de compilación (sospechosos):** los nombres de `docs/REFERENCE.md` §1 que no aparecen en el índice de bunnidogs (`hurtServer`, `defineSynchedData`, `EntityType` con `passengerAttachments`, `NoopRenderer`, `RandomizableContainerBlockEntity`, `ChestMenu.threeRows` / `sixRows`, `pushReaction`, `updateShape` con `ScheduledTickAccess`, `CreativeModeTab.builder`...). No significa que estén mal: nadie los ha confirmado en 26.2.
* **Qué probar:** lo mismo que B0.1; los dos chunks se cierran con el mismo reporte.
