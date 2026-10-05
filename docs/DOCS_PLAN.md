# DOCS_PLAN: qué documentos va a tener el proyecto LSM y qué lleva cada uno

**Estado: EJECUTADO; archivo histórico (movido a `docs/history/` en la sesión 4, al terminar la Tanda D).** Tandas A (sesión 2), B (sesión 3), C (sesión 3, según los propios specs) y D (sesión 4) escritas. Ya no se edita: las decisiones D1-D7 viven en `docs/DESIGN.md` §6 (copia viva), los datos de bunnidogs de §3 en `docs/INTEGRATION_BUNNIDOGS.md` §2, y el texto de las reglas R1-R16 en `docs/REFERENCE.md` §3. Lo de abajo es el plan original, **literal**, y puede mencionar docs como "pendientes" que ya existen.

**Objetivo:** que el zip de LSM se continúe solo, como el de bunnidogs: un `START_HERE.md` que orienta a Claude, un doc por trabajo, un estado que siempre es verdadero, trabajo en trozos pequeños (*chunks*), y *planificar antes de codificar*. Se copia la **filosofía y las indicaciones** de bunnidogs, **no su contenido**. Con este archivo dentro del zip, `bunnidogs-mod.zip` deja de hacer falta (salvo el punto D5 de la sección 5).

---

## 1. Qué hay hoy en el zip LSM (leído del código)

* Mod `lsmmod` ("LSM Mod"), paquete `net.nicomar2009.lsmmod`, **Minecraft 26.2, NeoForge 26.2.0.88, Java 25** (resuelve el pendiente P8 del diseño; es la misma versión que bunnidogs).
* ~1000 líneas de Java. 6 bloques: 3 sillas (`high_school_chair`, `elementary_chair`, `teachers_chair`: `ChairBlock` + `SeatEntity`), 2 pupitres de dos mitades con inventario de 27 (`students_desk`, `teachers_desk`), `locker` (54 slots). Pestaña creativa, modelos, texturas, blockstates, recetas y loot tables de esos 6, `lang/en_us.json`.
* Carpetas vacías con `.gitkeep`: `data/lsmmod/structure`, `worldgen`, `tags`, `assets/.../sounds`. `lsmmod.mixins.json` sin mixins.
* **No hay:** docs, `tools/`, items propios (solo BlockItems), entidades (salvo el asiento), efectos, estructuras, comandos, red, CI.
* Compilación: nadie ha reportado. Se registra como baseline **B0, SIN CONFIRMAR**, hasta que el usuario diga "compiló/funcionó".
* Los comentarios del código citan "README section 9.1" y "Phase 3/4" de un README anterior que no está en el zip: no fiarse de ellos.
* **Diferencias diseño ↔ código** (se resuelven en `BLOCK_SPECS.md`): las sillas del diseño son Hall negro/rojo, Aula gris y Auditorio rojo o blanco, las del código son elementary/high_school/teachers; el diseño dice que las carpetas son decorativas, el código les da 27 slots; la clave de lang `entity.lsmmod.chair_seat` no coincide con la entidad registrada `seat`; el lang solo está en inglés y el diseño usa nombres en español.

---

## 2. Los documentos

Convención de marcas (heredada del diseño original, se conserva literal en todos los docs): sin marca = **decidido**; *(propuesta)* = valor de balance elegido, ajustable; *(a confirmar)* = supuesto sin validar. Estados de chunk: `PLANNED` / `ACCEPTED` / `NOT COMPILED` / `COMPILES` / `WORKS` / `SUPERSEDED`.

### 2.1 Raíz

**`START_HERE.md`**: lo único que Claude lee al empezar. *Equivale a:* START_HERE de bunnidogs. Siete secciones, en este orden:
1. Qué es (30 s) y **vocabulario fijo**: las palabras del glosario del diseño (neutral, enojado, apaciguado, trackeo, llamar, importantes/no importantes, colegio, patrulla, raid) con un solo significado cada una.
2. **ESTADO ACTUAL** (se edita al final de CADA sesión y debe ser siempre verdadero): último chunk entregado y su estado, qué verificar en el juego, próximos candidatos, decisiones abiertas.
3. Tabla "cómo leer al usuario" (reglas R4 de la sección 4).
4. Quién hace qué (R3, R5, R9).
5. Comandos para copiar y pegar: descomprimir, `ParseAll`, autocomprobación de `rules/`, arte, entrega.
6. El bucle de un chunk y la lista corta de docs a tocar (R7, R8).
7. **Mapa de docs**: trabajo → archivo (una fila por doc de esta sección).

**`README.md`**: reemplaza el de la plantilla. Qué es el mod, cómo ejecutar (`./gradlew runClient`, `build`), "Continuar con Claude" (sube el zip y di "funcionó, sigue" / "compiló" / pega los errores), docs de un vistazo. La nota de licencia de los nombres Mojang de la plantilla se conserva, pero en `REFERENCE.md`.

### 2.2 `docs/` (vigentes)

**`CHUNKS.md`**: una línea por chunk, el más nuevo primero: id, qué hizo, estado. Ids por área: `B` bloques, `I` items, `A` armadura, `N` NPC, `W` estructura/mundo, `R` raids, `K` minibosses/boss/ritual, `X` integración bunnidogs, `S` sonido/música. Nace con `B0` (baseline) y los chunks planeados `PLANNED`. *Equivale a:* CHUNKS.

**`ROADMAP.md`**: Hecho / Planeado (tabla: ítem, qué, requiere, tamaño y riesgo, spec) / Ideas sin spec. Nace con los hitos de la sección 6. *Equivale a:* ROADMAP.

**`REFERENCE.md`**: hechos estables. Qué es y versiones; cómo compila y ejecuta el usuario; **proceso completo** (texto largo de R1-R13); **mapa de paquetes** (los que existen: `block/`, `entity/`, `registry/`, `client/`; los planeados, marcados `PLANNED`: `rules/` lógica pura sin imports de Minecraft y con autocomprobación, `npc/`, `effect/`, `worldgen/`, `raid/`, `integration/bunnidogs/`); convenciones (R16); autocomprobación de `rules/`; arte (D3); notas del entorno de desarrollo (R15, nota de licencia Mojang); preferencias del usuario aprendidas; chuleta de depuración (comandos `/lsm ...` planeados: crear un alumno con stats, forzar enojado/apaciguado, generar el colegio, empezar una raid). *Equivale a:* REFERENCE.

**`API_NOTES.md`**: *QUICK INDEX* antes de investigar nada. Empieza con el *playbook* de investigación (R14), la tabla de búsqueda **vacía** (columnas: necesidad, qué funciona, estado COMPILED/SOURCE/ASSUMED/AVOIDED, dónde se verificó) y un **mapa de riesgo**: las familias de API que este mod necesita y que ningún código compilado en 26.2 cubre todavía (armadura/equipment assets, discos y música, barra de jefe, raids propias, estructuras jigsaw, proyectiles, advancements, huevos de aparición, mapa de exploración, `EntityJoinLevelEvent` y objetivos de ataque). Cada una es un chunk de riesgo: UNA API nueva por chunk. *Equivale a:* API_NOTES.

**`FEATURES.md`**: guía del jugador, cómo se usa cada cosa en el juego. Empieza con lo que existe (sillas, pupitres, casillero) y crece un párrafo por chunk. *Equivale a:* FEATURES.

**`DESIGN.md`**: el diseño acordado y el porqué. Concepto y principios (§1 del diseño original: NPC muy OP, se quedan en su zona, stats y rasgos sin crear mobs); glosario y estados (§2); elementos descartados (§11); **Pendientes y supuestos** con ids `P1-P8` y `A1-A3` (§12, con P8 ya resuelto); y una sección "decisiones tomadas sin preguntar" por chunk. *Equivale a:* DESIGN.

**Specs por sistema.** Cada uno lleva tablas con números marcados (decidido/propuesta/a confirmar), qué chunk lo implementa, y una sección "qué hizo el chunk X distinto del plan". **El spec gana sobre cualquier otro doc.** *Equivalen a:* SHEET/TABLE/DROPS/HOME_SPECS.

| Doc | Trabajo | Fuente en el diseño original |
|---|---|---|
| `MECHANICS_SPECS.md` | Reglas transversales: territorio y llamados (quién llama a quién), patrullas, raids y olas, **matriz de hostilidad** (grupo de NPC × estado × disparador), efectos propios (Bad Omen LSM, Trackeo, Estudioso, Regeneración San Martín, Expulsión) | §2 efectos, §3.1-3.3 |
| `NPC_SPECS.md` | Un NPC por fila (grupo, zona, comportamiento, a quién llama, importante o no); sistema de stats 0-5 y rasgos; stats numéricos (hoy pendientes) | §7.2, §7.3 |
| `ITEM_SPECS.md` | Armas (regla y sus tiers, lápiz, escoba, silla arrojable, pelotas y su entidad proyectil), pacificar/enojar, instrumentos, consumibles, legendarios, discos | §3.5, §5, §7.1 |
| `ARMOR_SPECS.md` | 4 piezas, tabla de prendas e inmunidades, reglas (rango de visión, set completo, trampa del uniforme del profesor), dónde se consigue, Traje de Manuel Tirado | §6, §8 |
| `BLOCK_SPECS.md` | Bloques (efecto e interacción), casilleros y loot, objetos perdidos, laptops, PC, campana, escudo, San Martín; tabla "existe hoy / falta" | §4 |
| `WORLD_SPECS.md` | Generación del colegio (planicies, rareza, uno garantizado cerca del spawn), zonas por jigsaw, aulas y dónde va cada NPC, mapa crafteable | §3.4 |
| `BOSS_SPECS.md` | Cadena de eventos, condición del Director, Hidalgo, Yahu, fantasma, ritual, boss final y sus fases | §7.4, §7.5, §9 |
| `INTEGRATION_BUNNIDOGS.md` | Todo lo que cruza entre los dos mods: hostilidad de los bunnidogs, excepción de Bianca, nidos y raids, ingredientes del mapa. Lleva los datos de la sección 3 de este plan | §10 |

**Planes puntuales** (`<TEMA>_PLAN.md`): se escriben solo cuando un tema grande lo pide, antes de codificarlo, con la forma de los de bunnidogs (problema, idea en un párrafo, partes, chunks, decisiones con mi pick en negrita, "dejado fuera a propósito"). Ya previsto: `RAID_PLAN.md` antes del hito M6 (la raid de vanilla está pensada para aldeas y raiders; probablemente haga falta un gestor de olas propio, por verificar).

### 2.3 `docs/history/` (nunca hace falta para continuar)

* `DESIGN_SOURCE_v1.md`: el `mod_colegio_dungeon.md` original, **literal**. Garantiza que repartirlo en specs no pierde nada.
* `SESSION_LOG.md`: 2-4 líneas por sesión. Nace con la sesión 1 (diseño escrito, plantilla y 6 bloques existentes, este plan).
* `CHUNKS_FULL.md`: filas completas de chunks viejos (archivos, decisiones, riesgos de compilación). Nace con `B0`.
* `API_HISTORY.md`: solo cuando `API_NOTES.md` tenga secciones viejas que mover. No se crea antes.

### 2.4 `tools/` (herramientas, no docs)

* `ParseAll.java`: revisa la sintaxis de todos los `.java` (parse-only, ~35 líneas, genérica; se reescribe, no hace falta bunnidogs). Se escribe junto a `START_HERE.md`.
* `RulesCheck.java`: autocomprobación de `rules/` (matriz de hostilidad, inmunidad por prendas, condición del Director con el 90 % de primaria, composición de olas, stats). **Nace con el primer chunk de lógica pura**, no antes.

---

## 3. Datos de bunnidogs que usa el diseño (arrastrados aquí para poder soltar su zip)

* Entidad `bunnidogs:bunnidog` (clase `entity/Bunnidog`). Su `registerGoals()` corre dentro del constructor de la superclase, antes de que exista el genoma: los goals se registran para todos y se filtran en `canUse()`. Hoy solo ataca con `HurtByTargetGoal`; ser hostil a alumnos y profesores exige un goal nuevo con el mismo filtrado.
* Nido: bloque `bunnidogs:nest` y tipo de punto de interés (POI) `bunnidogs:nest`. Los perros lo reclaman con el `PoiManager`; el worldgen coloca nidos en cada casa de los asentamientos. Para "raids donde hay muchos nidos" basta **contar POIs `bunnidogs:nest`** en un radio, sin datos nuevos.
* Asentamientos: estructura `bunnidogs:warren_village`, features `bunnidogs:warren` y `bunnidogs:surface_warren`, niveles 1-5. Depuración: `/bunnidogs warren [1-5]`.
* **Legendarios** (rareza EPIC, caen de perros muy criados, se exhiben en el Trophy Pedestal): `bunnidogs:titan_haunch`, `gilded_foot`, `moon_sinew`, `listening_shell`, `everbloom`. Son los candidatos naturales para el mapa al colegio (pendiente P1; decide el usuario).
* Bianca: bunnidogs no tiene noción de "alumno con nombre". La excepción se resuelve con una marca (etiqueta o dato) en la entidad de LSM, no en bunnidogs.
* Mismas versiones y misma cadena de herramientas que LSM.

---

## 4. Reglas heredadas (se copian a `START_HERE.md` y `REFERENCE.md`; esto es lo que hace que sea "la misma filosofía")

**Proceso**
* **R1** Claude lee SOLO `START_HERE.md` al empezar. Los demás docs, solo para el trabajo que nombra el mapa de docs. Nunca leer `DESIGN`, `API_NOTES` o `history/` "para orientarse".
* **R2** El bloque ESTADO ACTUAL se edita al final de cada sesión y debe ser siempre verdadero.
* **R3** El usuario compila y prueba. El sandbox no tiene Minecraft ni Gradle: solo chequeo de sintaxis y autocomprobación de lógica pura. **Nunca decir que compila o que funciona.** `NOT COMPILED` (entregado sin reporte) → `COMPILES` ("compiló") → `WORKS` ("funcionó"); solo el reporte del usuario cambia el estado.
* **R4** Cómo leer al usuario (habla suelto, en español o inglés; se responde en su idioma): "funcionó / anduvo / todo bien" → el último chunk pasa a WORKS y se hace el siguiente. "compiló" → COMPILES. Errores o log pegado → arreglarlos PRIMERO, como chunk propio, sin features nuevas. "continúa" a secas → decir en una línea que el último chunk está sin confirmar y hacer un chunk pequeño. "continúa con X" → hacer X; si es grande o no tiene spec, plan en `docs/` primero y sin codificar hasta que lo diga. "elige tú / tus picks" → elegir y justificar en una línea. "solo docs / limpia" → tocar solo docs y tooling.
* **R5** Un chunk por turno: algo que el usuario pueda compilar y probar en pocos minutos (2-6 archivos). Decir qué se dejó fuera a propósito. Ante la duda se recorta el chunk, nunca los docs ni el zip.
* **R6** A lo sumo UNA API de Minecraft nueva por chunk, señalada como el error de compilación más probable. Copiar patrones que ya COMPILARON. La lógica que pueda ser Java puro va a `rules/`; el pegamento con Minecraft es delgado.
* **R7** Orden de construcción: código y recursos → `ParseAll` → (solo si cambió lógica pura) un grupo pequeño en `RulesCheck` y UNA corrida → docs → zip. Así quedarse sin espacio cuesta pulido, nunca un zip roto.
* **R8** Docs a tocar en cada chunk: ESTADO ACTUAL de `START_HERE`; una línea en `CHUNKS`; el spec del área (qué hizo, diferencias con el plan, riesgos de compilación); fila nueva en `API_NOTES` si se consultó algo; "decisiones sin preguntar" en `DESIGN` y un párrafo en `FEATURES`; 2-4 líneas en `SESSION_LOG`.
* **R9** Verificación: el usuario pidió "menos checks". Autocomprobación solo si cambió lógica pura, una vez por turno, sin pruebas de sabotaje ni mutación, tests chicos (cientos de muestras, no miles), nunca tras cambios de recursos, arte, docs o pegamento. No presumir cantidad de checks.
* **R10** Plan primero, luego detalles, luego código para lo grande. No codificar un plan hasta que el usuario lo diga. En decisiones menores Claude propone un pick y el usuario lo acepta en una línea.
* **R11** Una función debe poder leerse **en el mundo**, no solo en los docs: los estados de un NPC (neutral, enojado, apaciguado) y las mecánicas nuevas necesitan una señal visible o audible, y la palabra nueva se explica en el juego (lang o advancement) y en `FEATURES`. (Lección del "warren" de bunnidogs: el usuario creyó que era un bug.)
* **R12** Nunca pedir plantillas ni formularios; el feedback suelto basta.
* **R13** Entrega: el zip con el mismo nombre de carpeta y de archivo que se subió, sin `run/` ni `build/`, con `present_files`, y respuesta corta: qué hace / qué NO está compilado / qué probar / qué sigue.

**Técnica**
* **R14** Investigación de API: primero el QUICK INDEX; si falta, clon parcial de NeoForge 26.2.x (~30 s): `mkdir nf && cd nf && git init -q && git remote add origin https://github.com/neoforged/NeoForge && git config core.sparseCheckout true && printf 'tests\npatches\nsrc\n' > .git/info/sparse-checkout && git fetch -q --depth 1 origin 26.2.x && git checkout -q FETCH_HEAD`. `tests/` tiene código de mods reales (cómo se usa y qué imports), `tests/src/generated/resources/data/` tiene JSON reales de 26.x, `patches/` son solo diffs: **los cuerpos de vanilla NO están**, así que un nombre solo-vanilla queda ASSUMED hasta que el usuario compile. Buscar declaraciones de clase, no nombres de javadoc (NeoForge 26 renombró eventos). Sin red: `web_search`/`web_fetch` en docs.neoforged.net y en los *primers* de `neoforged/.github`. Si se investigó algo, añadir la fila al índice antes de terminar.
* **R15** Sandbox: JRE 21 con el módulo del compilador pero **sin binario `javac`** → `java -m jdk.compiler/com.sun.tools.javac.Main`. Python con Pillow. `github.com` y `raw.githubusercontent.com` casi siempre alcanzables; Maven y Gradle no.
* **R16** Convenciones: todo por `DeferredRegister` en `registry/`; código de cliente en `client/` con `@EventBusSubscriber(value = Dist.CLIENT)`; **las tablas mandan** (una enum o tabla por escalera: tiers de la regla, instrumentos, prendas, olas → items, texturas, lang, recetas y checks la siguen; añadir un peldaño = una entrada más y sus archivos); preferir datos a clases; decir con claridad lo no verificado.

---

## 5. Decisiones abiertas (mi pick entre paréntesis; "sigue con tus picks" las acepta todas)

* **D1 ¿Uno o dos mods?** El diseño dice "mismo proyecto que bunnidogs"; los zips son dos proyectos con ids distintos. *(Pick: dos jars; `lsmmod` depende de `bunnidogs`, nunca al revés; todo en `integration/bunnidogs/`; los goals se añaden a los bunnidogs con `EntityJoinLevelEvent`. Riesgo: ejecutar ambos en `runClient` requiere configurar Gradle.)* Se responde antes del hito M6, no antes.
* **D2 Idioma.** *(Pick: docs en español, porque el diseño y los nombres del juego están en español; código, identificadores y comentarios en inglés, como ya está; Claude responde en el idioma del usuario; textos del juego: `en_us` más un archivo en español con los nombres del diseño, código de idioma por verificar.)*
* **D3 Arte.** Hoy no hay scripts ni modelos de entidad. *(Pick: texturas de items simples generadas con Pillow como placeholder para que el usuario las sustituya; modelos de bloque y de entidad los hace el usuario hasta que diga otra cosa.)*
* **D4 Progreso del Director y del ritual: ¿por colegio o global?** El diseño no lo dice y hay varios colegios. *(Pick: por colegio, guardado en la instancia de la estructura.)*
* **D5 Soltar `bunnidogs-mod.zip`.** Es lo único que ese zip aporta y que este plan no puede reemplazar: **código que ya compiló en 26.2** (entidad con atributos, renderer + modelo + render-state, registro de estructura y pieza, comando, payload de red, tooltips, pantalla) y las filas COMPILED de su `API_NOTES`. LSM va a necesitar todo eso y no tiene nada. *(Pick: copiarlo ahora, literal y sin tocar, a `docs/history/bunnidogs_patterns/`: ~10 archivos + su `API_NOTES.md`. Hay que hacerlo antes de soltar el zip.)*
* **D6 Regla de raid "donde hay muchos nidos"** (cuántos, en qué radio, cómo se elige el lugar): va a `RAID_PLAN.md`, no se decide ahora.
* **D7 El Alma de Manuel Tirado** figura como "parte del ritual" (§5.5) pero el ritual (§9.1, paso 7) solo nombra los dos San Martín y el ataúd. *(Pick: el alma se entrega en el ataúd; se confirma al escribir `BOSS_SPECS.md`.)*

---

## 6. Hitos propuestos (para `ROADMAP.md`; el usuario decide el orden)

* **M1** Bloques que faltan del diseño (§4) sobre lo que ya existe: variantes de silla, kiosko, campana, laptops, PC, casilleros con loot, objetos perdidos. Poca API nueva.
* **M2** Items simples y efectos: regla y tiers, bola de papel, folder, cuaderno, celular, consumibles; efectos Estudioso, Trackeo, Bad Omen LSM.
* **M3** Armadura (API de equipment de 26.x: riesgo) y `rules/` con la matriz de hostilidad (lógica pura, primera autocomprobación).
* **M4** NPC base: una entidad de alumno con stats, rasgos, estados y zona propia; el llamado; luego los demás NPC de uno en uno.
* **M5** Estructura del colegio por jigsaw, aulas, colocación de NPC, uno garantizado cerca del spawn, mapa crafteable.
* **M6** Patrullas, raids propias (`RAID_PLAN.md` antes) e integración con bunnidogs (D1).
* **M7** Hidalgo, Yahu, fantasma, condición del Director, legendarios, ataúd, ritual, boss final, traje.
* **M8** Música (discos, laptops), advancements que explican las palabras nuevas, pulido.

---

## 7. Orden en que se escriben los docs (una tanda por sesión; el usuario puede cambiarlo)

1. **Tanda A** (la que deja el zip operativo): `START_HERE.md`, `README.md`, `CHUNKS.md`, `ROADMAP.md`, `REFERENCE.md`, `tools/ParseAll.java`.
2. **Tanda B** (el diseño repartido): `history/DESIGN_SOURCE_v1.md`, `DESIGN.md`, `MECHANICS_SPECS.md`, `NPC_SPECS.md`.
3. **Tanda C**: `ITEM_SPECS.md`, `ARMOR_SPECS.md`, `BLOCK_SPECS.md`, `WORLD_SPECS.md`, `BOSS_SPECS.md`.
4. **Tanda D**: `INTEGRATION_BUNNIDOGS.md`, `API_NOTES.md`, `FEATURES.md`, `history/SESSION_LOG.md`, `history/CHUNKS_FULL.md`; este archivo pasa a `history/`.

Después de la tanda A ya se puede empezar a trabajar con chunks aunque falten las demás.

---

## 8. Dejado fuera a propósito

* Un `PROJECT_CONTEXT.md` único y gigante: es justo lo que bunnidogs tuvo que partir (350 KB, tardaba en leerse en cada conversación).
* Docs por fase (`PHASES.md`): aquí la unidad es el chunk.
* Un doc de drops separado: el loot de casilleros va en `BLOCK_SPECS.md` y el de los jefes en `BOSS_SPECS.md`.
* CI (`.github/workflows`), `dev/options.txt` y una carpeta `art/` completa: no hacen falta hasta que haya algo que automatizar.
* Ninguna decisión de balance nueva: todo número sigue marcado como en el diseño original.
