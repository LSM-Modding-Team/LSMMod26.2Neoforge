# CHUNKS: una línea por chunk, el más nuevo primero

Un **chunk** es lo que se entrega en un turno: algo que el usuario compila y prueba en pocos minutos (2-6 archivos; `docs/REFERENCE.md` §3, R5). Las filas completas de chunks viejos (archivos, decisiones, riesgos de compilación) están en `docs/history/CHUNKS_FULL.md` (nació en la sesión 4 con `B0` y `B0.1`).

**Estados:** `PLANNED` / `ACCEPTED` / `NOT COMPILED` (entregado sin reporte) / `COMPILES` ("compiló") / `WORKS` ("funcionó") / `SUPERSEDED`. **Solo el reporte del usuario cambia el estado** (R3).

**Ids por área:** `B` bloques · `I` items · `A` armadura · `N` NPC · `W` estructura y mundo · `R` raids · `K` minibosses, boss y ritual · `X` integración bunnidogs · `S` sonido y música.

---

## Entregados (el más nuevo primero)

| Id | Qué hizo | Estado |
|---|---|---|
| `T0.1` | **Arreglo de T0** (sin features): la tarea se llama ahora `applyClientOptions`, porque `prepareClientRun` ya existe en el plugin de NeoForge. Misma lógica. Además nace la carpeta `newresources/` con su README (recursos sin usar, fuera de `src/`, no se empaqueta). Riesgo: otro choque de nombres o error de Gradle; si pasa, pega el error | **NOT COMPILED** (sin confirmar) |
| `T0` | **Tooling, sin código del mod:** tarea `prepareClientRun` que copiaba `tools/run-defaults/options.txt` a `run/options.txt` antes de `runClient`. **Falló en el arranque de Gradle** (reporte del usuario): el nombre ya existía en el plugin | **SUPERSEDED** por `T0.1` |
| `N0` | **Hostilidad como lógica pura** (sin API de Minecraft, nada visible en el juego): `rules/{NpcGroup, NpcState, PlayerGear, Hostility}` y `tools/RulesCheck.java`. Decidido: el Traje gana a todo; apaciguado no ataca; inmunidad por prenda del grupo; Estudioso = nivel del uniforme de profesor. Pick sin aceptar: precedencia del sticker y de "provocado" (`MECHANICS_SPECS.md` §6.5). Sin rellenar: V4, V6 (2+ piezas), V13, V16. `RulesCheck` 19/19, `ParseAll` 19 archivos, 0 errores. Riesgo de compilación: bajo (Java puro, `record` y `switch` de expresión; el mod usa Java 25) | **NOT COMPILED** (sin confirmar) |
| `B0.1` | **Limpieza de B0** (sin features, sin API nueva de Minecraft): clave `lang` del asiento (`entity.lsmmod.seat`), receta nueva de `students_desk`, descripción real en `neoforge.mods.toml`, comentarios corregidos en 4 clases. Solo JSON, TOML y comentarios; `ParseAll` 15 archivos, 0 errores (solo sintaxis). Riesgo de compilación: ninguno nuevo; el único riesgo es un JSON/TOML mal formado, que solo aparece al cargar el juego. Detalle: `docs/REFERENCE.md` §1 | **NOT COMPILED** (sin confirmar) |
| `B0` | **Baseline:** lo que ya había al empezar el proyecto de docs. 6 bloques (3 sillas con `SeatEntity`, 2 pupitres de dos mitades con 27 slots, 1 casillero con 54 slots), pestaña creativa `lsm_mod`, modelos, texturas, blockstates, loot tables, 5 recetas (falta la del pupitre de alumno) y `lang/en_us.json`. Nadie ha reportado compile ni prueba. Inventario completo y sospechosos de compilación: `docs/REFERENCE.md` §1 | **NOT COMPILED** (sin confirmar) |

---

## Planeados (ids provisionales; se parten y se renumeran al planear cada uno)

Un chunk nuevo necesita antes su spec (Tandas B y C de docs). Cada fila corresponde a un hito de `docs/ROADMAP.md`. Una sola API nueva por chunk (R6).

| Id | Qué | Hito | Estado |
|---|---|---|---|
| `B1` | Alinear los bloques existentes con `BLOCK_SPECS.md`: variantes de silla, 27 slots frente a "decorativo" (la clave de `lang` del asiento y la receta del pupitre de alumno ya se hicieron en B0.1) | M1 | `PLANNED` |
| `B2` | Bloques que faltan del diseño §4 sin API nueva (kiosko, campana, laptops, PC, casilleros con loot, objetos perdidos) | M1 | `PLANNED` |
| `I1` | Items simples: regla y sus tiers, bola de papel, folder, cuaderno, celular, consumibles | M2 | `PLANNED` |
| `I2` | Efectos propios: Estudioso, Trackeo, Bad Omen LSM | M2 | `PLANNED` |
| `A1` | Armadura (API de equipment de 26.x: **riesgo**) | M3 | `PLANNED` |
| `N1` | NPC base: una entidad de alumno con stats, rasgos, estados y zona propia; luego el llamado | M4 | `PLANNED` |
| `W1` | Estructura del colegio por jigsaw, aulas y colocación de NPC | M5 | `PLANNED` |
| `W2` | Uno garantizado cerca del spawn y mapa crafteable | M5 | `PLANNED` |
| `R1` | Patrullas y raids propias (partido en `R1a`-`R1g` en `RAID_PLAN.md` §6; el usuario debe aceptar el plan antes de codificar) | M6 | `PLANNED` |
| `X1` | Integración con bunnidogs (decisión D1 antes) | M6 | `PLANNED` |
| `K1` | Hidalgo, Yahu, fantasma, condición del Director, ataúd, ritual, boss final, traje | M7 | `PLANNED` |
| `S1` | Música (discos, laptops) y advancements que explican las palabras nuevas | M8 | `PLANNED` |
