# ITEM_SPECS: armas, pacificar/enojar, instrumentos, consumibles, legendarios, pelotas y discos

**Estado: implementación parcial.** Las seis reglas están implementadas sobre `c0fa4c3`, pendientes de compilación y prueba en Minecraft; detalles en `docs/RULERS.md`. Las demás secciones conservan su estado de diseño. Escrito en la sesión 3 (Tanda C) a partir de `docs/history/DESIGN_SOURCE_v1.md` §3.5, §5, §7.1.

**Implementan este spec (todos `PLANNED`, ver `docs/CHUNKS.md`):** `I1` (items simples), `I2` (efectos, ver `docs/MECHANICS_SPECS.md` §5), `S1` (discos y música).

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*. `V#` / `P#` = vacío o pendiente en `docs/DESIGN.md` §5. **Donde el diseño no da un dato, la celda dice "sin definir": no se rellena.** **Este doc manda sobre los números de los items;** la dirección de cada disparador (enoja/apacigua) y la matriz de hostilidad están en `MECHANICS_SPECS.md` §6. Qué hizo cada chunk distinto del plan: ninguno todavía.

**Regla R16 ("las tablas mandan"):** una tabla por escalera (tiers de la regla, instrumentos, pelotas). Items, texturas, `lang`, recetas y checks la siguen; añadir un peldaño = una fila más y sus archivos.

---

## 1. Armas

| Item | Descripción |
|---|---|
| **Regla** | Arma de los profesores. Mucho rango y ataque en área como la espada. **Hidalgo no la usa** (tiene su propio ataque: Expulsión). |
| **Lápiz / lapicero** | Armas leves. |
| **Escoba / trapeador** | Armas de limpieza; las usa el personal de limpieza. |
| **Silla arrojable** | Gran daño; **rompe el bloque donde cae**. Cómo se obtiene y su cooldown: sin definir (V17). |
| **Pelotas** (plástica, fútbol, básquet, vóley) | Armas a distancia; como item se lanzan. Ver §2. |

**Tiers de la regla** (implementados, pendientes de prueba). Daño total sin encantamientos, incluyendo el punto base del jugador:

| Tier / ID | Material vanilla | Daño | Durabilidad | Encantabilidad |
|---|---|---:|---:|---:|
| Madera / `wooden_ruler` | WOOD | 3.5 | 59 | 15 |
| Piedra / `stone_ruler` | STONE | 4.5 | 131 | 5 |
| Oro / `golden_ruler` | GOLD | 3.5 | 32 | 22 |
| Hierro / `iron_ruler` | IRON | 5.5 | 250 | 14 |
| Diamante / `diamond_ruler` | DIAMOND | 6.5 | 1561 | 10 |
| Netherite / `netherite_ruler` | NETHERITE | 7.5 | 2031 | 15 |

Todas: daño 0.5 menor que la espada equivalente, velocidad 1.6 ataques/s, alcance de entidades +1.5 bloques en la mano principal y barrido vanilla. Reparación y comportamiento de herramienta se heredan de `ToolMaterial`; netherite resiste fuego. Fuente de tiers para recursos: `tools/ruler_tiers.json`.

---

## 2. Pelotas (item y entidad proyectil)

| Pelota | Comportamiento |
|---|---|
| Pelota de plástico | Normal. |
| Pelota de fútbol | Mayor velocidad y daño. |
| Pelota de básquet | Mayor daño. |
| Pelota de vóley | Mayor velocidad. |

Cada una es un item que se usa como arma a distancia y una **entidad proyectil**. Números: sin definir (P4). Es la primera API de proyectil propio del mod: **una sola API nueva por chunk** (R6); el chunk que la use la señala como el error de compilación más probable.

---

## 3. Pacificar y enojar

**Arte implementado (2026-10-07):** folder oficio de tapa dura sin liga, inspirado en ARTESCO; cuaderno college de color sólido, lomo encolado y sin espiral, inspirado en Bakan/Stanford. Dos modelos padre (`folder`, `notebook`), 16 variantes por tipo (`<color>_folder`, `<color>_notebook`) y atlas de tapas compartidos entre ambos. Hojas y etiquetas usan un atlas independiente para conservar su color. Items decorativos registrados en la pestaña LSM Mod; recetas, consumo y Estudioso siguen pendientes. Regenerar con `python tools/create_stationery_assets.py`; preview con `blender -b --python tools/render_stationery_assets.py`.

Dirección de cada uno y quién queda afectado: `MECHANICS_SPECS.md` §6.1.1. Aquí los valores.

| Item | Efecto en profesores | Efecto en alumnos | Notas |
|---|---|---|---|
| **Bola de papel** | Los enoja | Aturde ~3 s | Los alumnos la lanzan en masa como ataque a distancia. |
| **Folder** | Los apacigua (**a todos**); te da **Estudioso 30 s** | — | Consumible, un solo uso. *(V12, resuelto)* |
| **Cuaderno** (de colores) | Apacigua **solo a profesores de primaria**; **Estudioso 20 s solo para ellos** | — | Consumible, un solo uso. *(V12, resuelto)* |
| **Celular** | "Enoja a todos" (V11) | Apacigua al alumno enojado | |
| **Parlante portátil** | Los enoja | Apacigua a todos | Reproduce cualquier canción (cuáles: V17). |
| **Sticker** | **Todos** los profesores atacan a quien lo lleve | — | Se pone en polos de alumnos o del jugador. |
| **Guitarra eléctrica** (legendario) | Apacigua | Apacigua | 10 s a todos. Dónde se obtiene: sin definir (P3). |

El Estudioso del cuaderno **no** es un efecto distinto: es el mismo efecto con alcance reducido (solo profesores de primaria). Cómo se marca ese alcance en el efecto (un nivel, dos efectos, un dato del item) es decisión del chunk `I2`. Qué profesor es "de primaria": V13.

---

## 4. Instrumentos

**Implementado: música y combate; pendiente de compilación y prueba.** Clic derecho conserva los sonidos existentes. Música y ataque comparten el cooldown vanilla del mismo instrumento, sincronizado al cliente y visible como la superposición gris del inventario. Tocar o acertar un golpe activa ese mismo temporizador y bloquea ambas acciones hasta que termine. La música no consume durabilidad. Clic izquierdo permite atacar con estos valores (daño total, contando el punto base del jugador):

| Instrumento | Daño | Cooldown entre golpes | Durabilidad en golpes |
|---|---:|---:|---:|
| Guitarra | 10 | 16 s / 320 ticks | 8 |
| Mandolina | 10 | 16 s / 320 ticks | 8 |
| Violín | 10 | 16 s / 320 ticks | 8 |
| Flauta | 2 | 8 s / 160 ticks | 16 |
| Pandereta | 2 | 8 s / 160 ticks | 16 |
| Melódica | 2 | 8 s / 160 ticks | 16 |

El impacto reproduce `minecraft:item.mace.smash_ground`. Cada golpe aceptado sobre una entidad viva consume un punto de durabilidad e inicia el cooldown de combate. El cooldown pertenece al tipo de instrumento: copias del mismo item comparten el indicador y el bloqueo de ataque/música. No añade barrido. El daño indicado corresponde a un ataque normal cargado, antes de armadura, críticos u otros modificadores vanilla.

**Diseño pendiente de efectos musicales:** los valores y buffs de la tabla siguiente siguen sin implementarse.

| Instrumento | Duración al usarlo el jugador | Notas |
|---|---|---|
| Guitarra | 6 s | |
| Flauta | 8 s | De primaria. |
| Mandolina | 10 s | |
| Violín | 14 s | **Más raro.** |
| Pandereta | 6 s | |

* **Uso del jugador:** pacifican a los NPC en un radio de **10 bloques**. Cooldown de 60 s *(propuesta)*.
* **Uso de NPC** (estudiantina y otros): buffean **+20 %** a alumnos y profesores en **15 bloques** durante **20 s** y llaman a 2-3 más *(propuesta)*.
* **Pendiente (P5):** la reacción exacta por tipo de profesor o de alumno.

---

## 5. Consumibles y utilidad

| Item | Efecto | Datos que faltan |
|---|---|---|
| **Lonchera** | Te da 5 muslos; se recarga con comida. Sale del kiosko. | Cómo se recarga (V17); qué se paga en el kiosko (V18) |
| **Medalla de festidanza** | Multiplica la velocidad actual. | Factor y duración (V17) |
| **Medalla de olimpiadas** | Multiplica el daño. | Factor y duración (V17) |
| **Diploma** | Otorga más corazones. | Cuántos (V17) |
| **Huevo podrido** | Veneno fuerte y náusea en un área cercana. Aparece en algunos casilleros: afecta a quien lo abre y a los cercanos. | Radio y duración (V17) |
| **Papel higiénico** | No hace nada. Hay **uno por salón**. | — |
| **Libros** | Solo existen de **2do a 5to**. Incluyen la oración sanmartiniana. | Uso (¿se leen?): sin definir |

---

## 6. Legendarios

| Item | Descripción | Dónde sale |
|---|---|---|
| **Ardilla PUCP** | Inmunidad ante los salones de 4to y 5to y ante Hidalgo; esos salones pasan a ser tus aliados. | Solo en casilleros de 4to y 5to, "exactamente una" (V22) |
| **Guitarra eléctrica** | Apacigua a todos 10 s. | Sin definir (P3) |
| **Mapa al ataúd** | Indica dónde está el ataúd de Manuel Tirado. | Drop del Director |
| **Alma de Manuel Tirado** | Parte del ritual. | Drop del fantasma de Manuel |
| **Traje de Manuel Tirado** | Pacifismo total. Ver `ARMOR_SPECS.md` §5. | Drop del boss |

Los otros dos legendarios del diseño son **bloques**: el ataúd de Manuel Tirado y San Martín de Porres (`BLOCK_SPECS.md`).

---

## 7. Discos y música (diseño §3.5)

* Las **laptops reproducen discos** (música custom, p. ej. "nene malo", "bby wow"); los discos se obtienen como **loot**.
* Laptop común: radio de 6 bloques *(propuesta)*. Laptop de Moisés: toda la estructura, click derecho alterna himno y marcha del colegio. Detalle del bloque: `BLOCK_SPECS.md`.
* Sin definir (V17): cómo se carga un disco en la laptop, de qué loot sale cada disco, la lista completa de canciones, y si el himno y la marcha son discos o sonidos fijos de la laptop de Moisés.
* El parlante portátil (§3) también "reproduce cualquier canción".

---

## 8. Qué existe hoy y riesgo de compilación

* **Existe hoy:** nada de lo de este doc. Los 6 `BlockItem` de `ModItems` son de los bloques.
* **Chunks:** `I1` = items simples (regla y tiers, bola de papel, folder, cuaderno, celular, consumibles); `I2` = efectos Estudioso, Trackeo, Bad Omen LSM; `S1` = discos y música.
* **Familias de API que ningún código compilado en 26.2 cubre** (cada una es un chunk de riesgo, **una por chunk**): items propios con componentes (lo primero nuevo en `I1`), efectos propios, proyectiles (pelotas, bola de papel, silla arrojable), discos y música, advancements. Índice: `docs/REFERENCE.md` §8 y `docs/history/bunnidogs_patterns/API_NOTES.md`.
* **Arte (D3, abierta):** pick propuesto: texturas de items con Pillow como placeholder para que el usuario las sustituya.
* **R11:** cada item que cambia un estado necesita señal visible o audible y su palabra explicada en el juego y en `FEATURES`.

---

## 9. Vacíos que tocan este doc

V11 (celular), V13 (qué es un profesor "de primaria"), V17 (datos de items sin definir), V18 (trueque en el kiosko), V22 (cuántas Ardillas), P3, P4, P5. Detalle en `docs/DESIGN.md` §5.
