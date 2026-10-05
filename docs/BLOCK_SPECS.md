# BLOCK_SPECS: bloques, casilleros y loot, y la tabla "existe hoy / falta"

**Estado: SPEC.** Hoy existen 6 bloques (B0, **SIN CONFIRMAR / NOT COMPILED**, más B0.1); el resto del diseño está `PLANNED`. Escrito en la sesión 3 (Tanda C) a partir de `docs/history/DESIGN_SOURCE_v1.md` §4 y del código de B0 (`docs/REFERENCE.md` §1).

**Implementan este spec (ver `docs/CHUNKS.md`):** `B1` (alinear los bloques que ya existen), `B2` (bloques sin API nueva: kiosko, campana, laptops, PC, casilleros con loot, objetos perdidos). Hitos M1 y M8 de `docs/ROADMAP.md`.

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*. `V#` / `P#` = vacío o pendiente en `docs/DESIGN.md` §5. **Donde el diseño no da un dato, dice "sin definir".** Qué hizo cada chunk distinto del plan: ninguno todavía (B0.1 solo tocó `lang`, una receta y comentarios).

---

## 1. Bloques del diseño

| Bloque | Efecto e interacciones (diseño §4) | Existe hoy |
|---|---|---|
| **Sillas** (Hall negro con rojo; Aula interactiva todo gris; Auditorio todo rojo o blanco) | Decorativas. | Parcial: 3 sillas que **no coinciden** con estas variantes (§3) |
| **Carpetas** (pupitres) del Aula interactiva/inglés: gris, con CPU y espacio adicional para teclado | Decorativas. | Parcial: 2 pupitres de dos mitades con 27 slots (§3) |
| **Mesa de kiosko** | Decorativa. Ahí está la mamá de cuarto. | No |
| **San Martín de Porres** | Click derecho: regeneración ~30 s. Pasivo: regenera automáticamente a profesores y alumnos cercanos, **más a profesores**. Legendario: tarda muchísimo en minarse. Dos se usan en el ritual (`BOSS_SPECS.md`). | No |
| **Escudo** (3x3) | Al pisarlo: Bad Omen LSM; todos los mobs te trackean (5 min, +25 % de daño); llama al azar a 6-10 profesores y 10-20 alumnos durante 60 s *(propuesta)*. Los profesores salen de su aula de forma temporal. | Visual: sí; efectos pendientes |
| **Laptop (común)** | Reproduce el disco en un radio de 6 bloques *(propuesta)*. | No |
| **Laptop de Moisés** (mayor rareza) | Reproduce el disco en toda la estructura. Click derecho alterna entre himno y marcha del colegio. | No |
| **PC** (Aula interactiva y escritorio negro) | Click derecho: **Estudioso 3 min**. 20 % de que llame a un profesor agresivo. Cooldown de 5 min por PC *(propuesta)*. Atrae al **alumno vicioso**, que se sienta a usarla. Si se la rompes, el vicioso te persigue hasta matarte (de un golpe). | No |
| **Campana de recreo** | Los NPC salen al patio (salvo algunos alumnos) durante **90 s**, tiempo para lootear ~2 salones. Cooldown de 15 min *(propuesta)*. | No |
| **Casilleros** | Contenedores con loot table baja (§4). | **Sí:** `locker`, 54 slots |
| **Objetos perdidos** | Contienen ropa de alumno **segura**. | No |
| **Ataúd de Manuel Tirado** | Funciona como cofre del tesoro (`BOSS_SPECS.md`). | No |

Los datos de cada efecto (Estudioso, Trackeo, Bad Omen LSM, Regeneración San Martín): `docs/MECHANICS_SPECS.md` §5. Los discos: `docs/ITEM_SPECS.md` §7.

---

## 2. Notas por bloque

* **Escudo.** 3x3; el disparador "pisarlo" y todo lo que provoca está en `MECHANICS_SPECS.md` §6.1. Dónde va dentro del colegio: sin definir (`WORLD_SPECS.md`).
* **PC.** El 20 % "llama a un profesor agresivo": qué es un "profesor agresivo" y a cuál llama: sin definir. El vicioso es un NPC (`NPC_SPECS.md` §2).
* **Laptops.** Cómo se carga el disco, y si el himno y la marcha son discos o sonidos fijos de la laptop de Moisés: sin definir (V17).
* **San Martín de Porres.** Radio y cantidad del efecto pasivo y tiempo de minado: sin definir. Es un bloque legendario (`ITEM_SPECS.md` §6).
* **Campana.** Un bloque con cooldown; "salvo algunos alumnos": cuáles, sin definir. Necesita una zona de patio (`WORLD_SPECS.md`).
* **Mesa de kiosko.** Decorativa. El rol de trueque de la mamá de cuarto está en `NPC_SPECS.md`; qué vende y a cambio de qué: V18.

---

## 3. Existe hoy / falta, y discrepancias con el código

Lo que hay hoy (leído del código): 3 sillas (`high_school_chair`, `elementary_chair`, `teachers_chair`), 2 pupitres de dos mitades con 27 slots (`students_desk`, `teachers_desk`), 1 casillero con 54 slots (`locker`). Detalle: `docs/REFERENCE.md` §1.

| # | Código de hoy | Diseño | Estado y pick *(propuesta)* |
|---|---|---|---|
| 1 | Sillas `elementary`, `high_school`, `teachers` | Variantes por zona: Hall negro con rojo, Aula interactiva gris, Auditorio rojo, Auditorio blanco. **Ninguna de las tres de hoy aparece en el diseño.** | Abierto (V14). **Pick: no borrar ni renombrar nada; las variantes del diseño se añaden como bloques nuevos en `B2`.** Razón: renombrar rompería modelos y mundos de prueba, y las 3 actuales pueden servir como sillas de otras zonas. |
| 2 | Sillas **se montan** con clic derecho (`SeatEntity`) | "Decorativos" | Abierto (V14). **Pick: dejar que se monten:** es una extensión inocua del diseño y ya está hecha. Se cambia si el usuario las quiere puramente decorativas. |
| 3 | Pupitres con **27 slots** (`students_desk`, `teachers_desk`) | Carpetas **decorativas**, grises, con CPU y espacio de teclado. No hay "pupitre del profesor" en el diseño como tal; la PC va en el "escritorio negro". | Abierto (V14). **Pick: dejar los 27 slots hasta que el usuario decida:** quitar el inventario es un cambio visible. El pupitre del profesor puede ser el "escritorio negro" de la PC, a confirmar. |
| 4 | Casillero con **54 slots** y soporte de loot table | "Contenedores con loot table baja" | **Coincide.** (`REFERENCE.md` decía antes que el diseño los daba por decorativos; era un error de la sesión 2 y se corrigió en la sesión 3: eran las **carpetas** las decorativas.) |
| 5 | `lang` solo en `en_us` | Nombres en español | Pendiente (D2): archivo en español, código de idioma por verificar. |
| 6 | `students_desk` sin receta hasta B0.1 | Sin receta en el diseño (ropa y casilleros son loot) | Hecho en B0.1 como *(propuesta)*. Las recetas de B0 (sillas, `teachers_desk`, `locker`) tampoco están en el diseño: se dejan. |

**Las 6 loot tables de `data/lsmmod/loot_table/blocks/`** son los drops del propio bloque; **no existe** ninguna loot table de contenido para casilleros (falta `chests/` o equivalente): es parte de `B2`.

---

## 4. Casilleros y loot

* **Loot table baja** con: huevos podridos (**trampa**: veneno fuerte y náusea en área, afecta a quien lo abre y a los cercanos), polos, stickers y otros (lista sin definir).
* En los casilleros de **4to y 5to** hay **exactamente una Ardilla PUCP** garantizada. Una por colegio, por salón o por casillero: sin definir (V22).
* **Ropa de alumno** también sale de los casilleros (`ARMOR_SPECS.md` §3).
* **Objetos perdidos:** contienen **ropa de alumno segura** (siempre hay).
* **Discos** como loot: `ITEM_SPECS.md` §7. De qué contenedor sale cada uno: sin definir (V17).
* Los números de la loot table (pesos, cantidades): sin definir. `RandomizableContainerBlockEntity` ya admite loot tables; falta escribirlas y que el worldgen las asigne a cada casillero.

---

## 5. Riesgos de compilación

* Casilleros con loot y objetos perdidos: **ninguna API nueva** (el contenedor ya soporta loot tables); riesgo bajo en `B2`.
* Laptops (sonido), escudo (efectos al pisar), PC (efecto con cooldown): dependen de familias que ningún código compilado en 26.2 cubre (música, efectos propios): una por chunk (R6). Índice: `docs/REFERENCE.md` §8.
* Si un cuarto bloque necesita la rotación de formas de colisión, extraerla (hoy está copiada en `ChairBlock`, `DeskBlock` y `LockerBlock`): `docs/REFERENCE.md` §5.

---

## 6. Vacíos que tocan este doc

V14 (mapeo de sillas y pupitres), V17 (laptops y discos), V18 (trueque del kiosko), V22 (Ardilla). Detalle en `docs/DESIGN.md` §5.

## Escudo 3×3: implementación visual (corrección 2026-10-05)

Petición vigente del usuario: bloque simple, completo al colocarlo, horizontal en el suelo, escudo hacia arriba y textura pixelada estilo Minecraft. `school_shield` coloca nueve bloques completos en el plano X/Z, centrados en la posición de colocación. Estados: `facing`, `column`, `row`; se eliminó `assembled` y la interacción de ampliación. La orientación sigue al jugador. Los nueve modelos muestran regiones de la misma textura de 96×96 (32×32 por pieza), con cuatro colores planos y borde blanco. El item muestra el escudo completo. Se comprueba espacio reemplazable, altura, chunks, borde y permiso de interacción antes de colocar. La retirada conjunta y el único drop del centro se mantienen. Los efectos al pisarlo del diseño siguen pendientes.

La textura se creó con la herramienta de imágenes a partir de la referencia y se exportó a resolución de juego con muestreo por puntos y paleta fija; no hay suavizado ni degradados. El harness persistente vive en `tools/build/`.

Verificación de esta corrección: BUILD SUCCESSFUL con `bash tools/build/compile.sh build --offline`. Se comprobó la textura de 96×96, las nueve posiciones horizontales y los UV superiores en las cuatro orientaciones. Prueba en Minecraft pendiente.

### Restauración del bloque completo

A petición del usuario se revierte B-SHIELD.2: altura de bloque completo, dureza 2.5, sonido de metal, textura opaca de B-SHIELD.1 y modelos cúbicos con caras laterales y base blancas. Se elimina la dependencia de soporte inferior y el generador de modelos recortados. Se conserva el harness reutilizable.
