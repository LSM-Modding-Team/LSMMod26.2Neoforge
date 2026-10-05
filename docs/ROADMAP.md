# ROADMAP: qué está hecho, qué sigue y qué es solo una idea

**El usuario decide el orden de los hitos.** Los hitos de abajo son los de `docs/history/DOCS_PLAN.md` §6 (propuestos). Los tamaños y riesgos son estimaciones mías *(propuesta)*. Lo que ya se hizo y su estado exacto: `docs/CHUNKS.md`.

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*.

---

## Hecho

* **B0, baseline** (6 bloques: 3 sillas, 2 pupitres, 1 casillero; ver `docs/REFERENCE.md` §1). *(a confirmar)*: el usuario no ha reportado compile ni prueba.
* **B0.1, limpieza de B0:** clave `lang` del asiento, receta del pupitre de alumno, descripción del mod, comentarios. *(a confirmar)*: sin compile ni prueba.
* **N0, hostilidad como lógica pura** (primer trozo de M3: `rules/` y `RulesCheck`). *(a confirmar)*: sin compile; la precedencia es un pick sin aceptar.
* **Documentación:** Tanda A (`START_HERE`, `README`, `CHUNKS`, `ROADMAP`, `REFERENCE`, `tools/ParseAll.java`) Tanda B (`DESIGN_SOURCE_v1`, `DESIGN`, `MECHANICS_SPECS`, `NPC_SPECS`), Tanda C (`ITEM_SPECS`, `ARMOR_SPECS`, `BLOCK_SPECS`, `WORLD_SPECS`, `BOSS_SPECS`) y Tanda D (`INTEGRATION_BUNNIDOGS`, `API_NOTES`, `FEATURES`, `history/SESSION_LOG`, `history/CHUNKS_FULL`; `DOCS_PLAN` pasó a `history/`). **Plan de docs completo.** `RAID_PLAN.md` también escrito. Falta solo el plan de `N1` (antes de M4).

---

## Planeado

Riesgo = la familia de API de Minecraft/NeoForge que ningún código compilado en 26.2 cubre todavía (lista completa de familias en `docs/API_NOTES.md` cuando exista; cada una es un chunk de riesgo, UNA por chunk).

| Hito | Qué | Requiere | Tamaño y riesgo *(propuesta)* | Spec |
|---|---|---|---|---|
| **M1** | Bloques que faltan del diseño §4 sobre lo que ya existe: variantes de silla, kiosko, campana, laptops, PC, casilleros con loot, objetos perdidos | B0 confirmado; `BLOCK_SPECS.md`; abiertos V14, V17, V18, V22 | Mediano; poca API nueva | `BLOCK_SPECS.md` |
| **M2** | Items simples y efectos: regla y tiers, bola de papel, folder, cuaderno, celular, consumibles; efectos Estudioso, Trackeo, Bad Omen LSM | `ITEM_SPECS.md`, `MECHANICS_SPECS.md`; la decisión de arte D3; abiertos V7, V11, V13, V17, P3, P4, P5 | Mediano; riesgo bajo-medio (items propios y efectos son lo primero nuevo; proyectiles si la bola de papel se lanza) | `ITEM_SPECS.md`, `MECHANICS_SPECS.md` |
| **M3** | Armadura y primera lógica pura: `rules/` con la matriz de hostilidad (primer `RulesCheck`) | `ARMOR_SPECS.md`, `MECHANICS_SPECS.md`; investigar equipment assets (R14); abiertos V4, V6, V9, V13, V15, V16, P2, P4 | Grande; **riesgo alto** en armadura (API de equipment de 26.x) | `ARMOR_SPECS.md`, `MECHANICS_SPECS.md` |
| **M4** | NPC base: entidad de alumno con stats, rasgos, estados y zona propia; el llamado; luego los demás NPC de uno en uno | `NPC_SPECS.md` y `MECHANICS_SPECS.md` (existen); M3 ayuda (la matriz); abiertos V2, V3, V18; patrones de entidad de bunnidogs (su código compilado no está en el zip: `DESIGN.md` §6, D5); stats numéricos pendientes (P4, P6) | Grande; riesgo medio-alto (entidad con atributos, renderer, modelo, objetivos de ataque, huevos de aparición) | `NPC_SPECS.md` |
| **M5** | Estructura del colegio por jigsaw, aulas, colocación de NPC, uno garantizado cerca del spawn, mapa crafteable | `WORLD_SPECS.md`; M4; abiertos V19, P1 | Grande; **riesgo alto** (estructuras jigsaw, mapa de exploración) | `WORLD_SPECS.md` |
| **M6** | Patrullas, raids propias e integración con bunnidogs | `RAID_PLAN.md` (escrito en la sesión 4; falta que el usuario lo acepte); decisión **D1** (uno o dos mods) y **D6** (regla de raid por nidos); M4; abiertos V23-V26; hace falta el zip de bunnidogs (D5) | Grande; **riesgo alto** (raids propias, `EntityJoinLevelEvent`, ejecutar dos mods en `runClient`) | `MECHANICS_SPECS.md`, `INTEGRATION_BUNNIDOGS.md`, `RAID_PLAN.md` |
| **M7** | Hidalgo, Yahu, fantasma, condición del Director, legendarios, ataúd, ritual, boss final, traje | `BOSS_SPECS.md`; M4-M6; decisiones D4 y D7; abiertos V8, V20, V21, P4, P7 | Grande; riesgo medio-alto (barra de jefe, fases) | `BOSS_SPECS.md` |
| **M8** | Música (discos, laptops), advancements que explican las palabras nuevas, pulido | `ITEM_SPECS.md` (discos) | Mediano; riesgo medio (discos y música, advancements) | `ITEM_SPECS.md` |

Antes de cualquier hito: el usuario dice si B0 compiló (`START_HERE.md` §3), porque si no compila lo primero es arreglar eso, como chunk propio.

---

## Ideas sin spec

Cosas que aparecen en el plan pero todavía no tienen spec ni decisión; no se codifican hasta que se promuevan a un hito.

* Un archivo de idioma en español además de `en_us` con los nombres del diseño (decisión D2; el código del idioma está por verificar).
* Texturas de items generadas con Pillow como placeholder (decisión D3), con scripts en `tools/art/`.
* Datagen: `build.gradle` ya define la run `data` pero el mod no genera nada con ella.
* Comandos `/lsm ...` de depuración (`docs/REFERENCE.md` §10).
* Autores y créditos en `neoforge.mods.toml` (la descripción ya es real desde B0.1; `authors=` y `credits=` siguen comentados).
