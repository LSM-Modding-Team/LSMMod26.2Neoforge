# REFERENCE: hechos estables del proyecto LSM

Se lee solo para el trabajo que lo pide (mapa de docs en `START_HERE.md` §7). Las marcas son las de siempre: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*.

Índice: §1 qué es e inventario · §2 cómo compila y ejecuta el usuario · §3 proceso completo (R1-R13, R17) · §4 mapa de paquetes · §5 convenciones (R16) · §6 autocomprobación de `rules/` · §7 arte · §8 entorno de desarrollo e investigación de API (R14, R15) · §9 preferencias del usuario · §10 chuleta de depuración.

---

## 1. Qué es, versiones e inventario del código (baseline B0)

**Qué es.** Mod `lsmmod` ("LSM Mod") para NeoForge, ambientado en un colegio (dungeon). El diseño completo (NPC muy fuertes que se quedan en su zona, stats y rasgos, estructura del colegio, raids, jefes) está en la lista de docs de `START_HERE.md` §7; casi todo es `PLANNED`.

**Versiones** (leídas de `gradle.properties` y `build.gradle`):

| Dato | Valor |
|---|---|
| Minecraft | 26.2 (rango `[26.2]`) |
| NeoForge | 26.2.0.88 |
| Java | 25 (toolchain de `build.gradle`) |
| Plugin de Gradle | `net.neoforged.moddev` 2.0.148 |
| `mod_id` / `mod_name` | `lsmmod` / `LSM Mod` |
| `mod_version` / licencia | `1.0.0` / All Rights Reserved |
| Paquete base | `net.nicomar2009.lsmmod` |
| Mismas versiones que | el mod bunnidogs (mismo MC, NeoForge y Java) |

**Qué hay (leído del código el 2026-10-04): 15 archivos Java, 995 líneas.** Nada de esto ha sido confirmado por el usuario: **B0 = SIN CONFIRMAR / NOT COMPILED**.

* **6 bloques**, todos con item (solo `BlockItem`), modelo, blockstate, textura, loot table y entrada en `lang`:

| Id | Clase | Qué hace |
|---|---|---|
| `high_school_chair`, `elementary_chair`, `teachers_chair` | `ChairBlock` | Silla con `FACING` (mira hacia donde mira quien la coloca). Clic derecho = sentarse (crea un `SeatEntity` a 7/16 de altura). Dureza 2.0 |
| `students_desk`, `teachers_desk` | `DeskBlock`, `TeachersDeskBlock` | Pupitre de **2 mitades** (LEFT = bloque clicado; RIGHT = `pos.relative(FACING)`). Un solo inventario de **27 slots** en la mitad LEFT (`DeskBlockEntity`, compartido por los dos pupitres). Solo LEFT suelta el item. `pushReaction(BLOCK)`. Dureza 2.5 |
| `locker` | `LockerBlock` | Bloque con inventario de **54 slots** (`LockerBlockEntity`, `ChestMenu.sixRows`). Dureza 3.0, sonido metal |

* **1 entidad:** `seat` (`SeatEntity`, `MobCategory.MISC`, hitbox 0.001, `noSummon()`, renderer `NoopRenderer`). Se descarta sola cuando nadie la monta.
* **Pestaña creativa** `lsm_mod` (icono: el libro de vanilla). Muestra todo lo registrado en `ModItems`, así que un item nuevo aparece sin tocar la pestaña.
* **Recursos:** 6 blockstates, 6 `items/*.json`, modelos de bloque (incluidos `classroom_chair_base`, `locker_item`, `students_desk_item`, `teachers_desk_item`, y mitades de pupitre `*left` / `*right`), 8 texturas PNG de bloque, 6 loot tables, **6 recetas** (la del pupitre de alumno se añadió en B0.1), `lang/en_us.json` (11 claves), tags `minecraft:mineable/axe` (pupitres) y `minecraft:mineable/pickaxe` (sillas y casillero).
* **Vacío a propósito** (solo `.gitkeep`): `data/lsmmod/structure`, `worldgen`, `tags`, `assets/lsmmod/sounds`. `lsmmod.mixins.json` existe sin ningún mixin (el paquete `net.nicomar2009.lsmmod.mixin` que nombra no existe).
* **No hay:** items propios, entidades aparte del asiento, efectos, estructuras, comandos, red, CI, datagen (la run `data` de `build.gradle` existe pero no se usa).

**Discrepancias diseño ↔ código** (se resuelven en `BLOCK_SPECS.md` §3, que deja los picks abiertos; hasta que el usuario responda no se tocan):

1. Sillas: el diseño lleva Hall negro/rojo, Aula gris y Auditorio rojo o blanco; el código tiene `elementary`, `high_school` y `teachers`.
2. Casilleros: el diseño los dice decorativos; el código les da 54 slots (y a los pupitres 27).
3. ~~`lang`: la clave `entity.lsmmod.chair_seat` no coincidía con la entidad `seat`~~ **Resuelto en B0.1** (ahora `entity.lsmmod.seat` = "Seat"). *(NOT COMPILED; los JSON solo se validan al cargar el juego)*
4. `lang`: solo hay inglés; el diseño usa nombres en español.
5. ~~`students_desk` no tenía receta~~ **Resuelto en B0.1** con un pick *(propuesta)*: `PPP` / `SCS` / `S S` con `#minecraft:planks`, `minecraft:stick` y `minecraft:chest` (el equivalente de madera del pupitre del profesor, que lleva hierro). Ajustable.
6. `neoforge.mods.toml`: la descripción de plantilla se reemplazó en B0.1 *(propuesta de texto)*. **Siguen sin autores ni créditos** (no se inventan; los pone el usuario: `authors=` y `credits=` están comentados en la plantilla).
7. ~~Comentarios del código desactualizados~~ **Corregidos en B0.1** (`ModBlocks`, `ModItems`, `SeatEntity`, `ModEntities`). Lo que sigue abierto: `ModEntities` **no llama a `noSave()`**, así que no se sabe si el asiento se guarda con el mundo (el `tick()` lo descarta sin pasajero, por lo que no debería importar); añadir `noSave()` sería una API nueva y no se hizo. Texto original del hallazgo: `ModBlocks` cita "README section 9.1", `ModItems` habla de `lsmmod:chair` y "Phase 3/4"; `SeatEntity` dice "see noSave() in ModEntities" pero `ModEntities` no llama a `noSave()` (solo `noSummon()`), así que **no se sabe si el asiento se guarda** con el mundo. *(hallazgo de la sesión 2)*

**Si el primer compile falla, sospechosos** (nombres que usa B0 y que NO aparecen en el `API_NOTES` de bunnidogs, es decir, nadie los ha confirmado en 26.2; no significa que estén mal): `Entity#hurtServer` y `defineSynchedData` (en `SeatEntity`); el builder de `EntityType` con `passengerAttachments` y `noSummon`, y `DeferredRegister.createEntities` / `registerEntityType`; `NoopRenderer`; `RandomizableContainerBlockEntity` con `trySaveLootTable`, `ContainerHelper` y `ChestMenu.threeRows` / `sixRows`; `player.openMenu`; el constructor `new BlockEntityType<>(factory, bloques...)`; `pushReaction(PushReaction.BLOCK)`; `playerWillDestroy` con `LevelEvent`; `updateShape` con `ScheduledTickAccess` (en bunnidogs solo figura como SOURCE/ASSUMED); `startRiding` y `getEntitiesOfClass`; `CreativeModeTab.builder`. Los dos patrones que sí compilaron en bunnidogs y que B0 también usa: `registerBlock(nombre, Function, UnaryOperator)` y el guardado con `ValueOutput` / `ValueInput` en entidades.

---

## 2. Cómo compila y ejecuta el usuario

El usuario puede compilar en su PC con el wrapper Gradle. En el entorno cloud, el harness reutilizable es `bash tools/build/compile.sh` (ver `tools/build/README.md`).

* **Requisitos:** JDK 25. `settings.gradle` incluye el plugin *foojay*, que puede descargar el JDK por su cuenta. El wrapper de Gradle viene en el zip (`gradlew`, `gradlew.bat`).
* **Compilar:** `./gradlew build` (jar en `build/libs/lsmmod-1.0.0.jar`, porque `archivesName = mod_id` y `version = mod_version`).
* **Jugar:** `./gradlew runClient` abre el juego **directo desde el código fuente**: no hay que copiar ningún jar a una carpeta `mods`. Antes de arrancar, la tarea `applyClientOptions` (ojo: `prepareClientRun` ya la define el plugin de NeoForge y chocaba) copia `tools/run-defaults/options.txt` (las opciones del usuario: pantalla completa, 15 chunks, 260 fps...) a `run/options.txt` **solo si `run/options.txt` no existe**; para aplicar una versión nueva, borrar `run/options.txt` una vez. Otras *runs* definidas en `build.gradle`: `server` (con `--nogui`), `gameTestServer`, `data`.
* **Carpeta de ejecución:** `run/` (está en `.gitignore`). El log es `run/logs/latest.log`; al cargar el mod escribe la línea `LSM Mod loaded`.
* **Si algo raro pasa con dependencias:** `./gradlew --refresh-dependencies`; para limpiar, `./gradlew clean` (del README de la plantilla).
* **Cómo reporta el usuario:** "compiló", "funcionó", o pega el error o el log. Nada más; ver `START_HERE.md` §3.

---

## 3. Proceso completo (R1-R13 y R17, con su porqué)

**R1. Qué se lee.** Claude lee SOLO `START_HERE.md` al empezar. Los demás docs, solo para el trabajo que nombra el mapa de docs. Nunca `DESIGN`, `API_NOTES` o `history/` "para orientarse": en bunnidogs el contexto único creció hasta 350 KB y tardaba en leerse en cada conversación; por eso aquí hay un doc por trabajo.

**R2. Estado siempre verdadero.** El bloque ESTADO ACTUAL de `START_HERE` se edita al final de cada sesión. Si un doc dice algo que ya no es cierto, es peor que si no existiera: se corrige o se borra en el mismo turno.

**R3. Compilación y prueba real se registran por separado.** A petición del usuario, se guarda el harness en `tools/build/`. El agente compila con `bash tools/build/compile.sh`, respeta los permisos del entorno y reporta el resultado real. COMPILES requiere Gradle exitoso; WORKS requiere una prueba en Minecraft o un reporte del usuario. Las limitaciones del sandbox histórico no aplican al entorno cloud actual.

**R4. Cómo leer al usuario.** Tabla en `START_HERE.md` §3. Habla suelto, en español o inglés; se responde en su idioma. Errores o log pegado: se arreglan PRIMERO, como chunk propio y sin features nuevas.

**R5. Un chunk por turno.** Algo que el usuario pueda compilar y probar en pocos minutos (2-6 archivos). Se dice qué se dejó fuera a propósito. Ante la duda se recorta el chunk, nunca los docs ni el zip.

**R6. Una API nueva por chunk.** A lo sumo UNA API de Minecraft nueva por chunk, señalada como el error de compilación más probable. Se copian patrones que ya COMPILARON. La lógica que pueda ser Java puro va a `rules/` y el pegamento con Minecraft se mantiene delgado.

**R7. Orden de construcción.** Código y recursos → `ParseAll` → (solo si cambió lógica pura) un grupo pequeño en `RulesCheck` y UNA corrida → docs → zip. Así quedarse sin espacio cuesta pulido, nunca un zip roto.

**R8. Docs a tocar en cada chunk.** ESTADO ACTUAL de `START_HERE`; una línea en `CHUNKS`; el spec del área (qué hizo, diferencias con el plan, riesgos de compilación); una fila en `API_NOTES` si se consultó algo; "decisiones sin preguntar" en `DESIGN` y un párrafo en `FEATURES`; 2-4 líneas en `SESSION_LOG`.

**R9. Verificación mínima.** El usuario pidió "menos checks". Autocomprobación solo si cambió lógica pura, una vez por turno, sin pruebas de sabotaje ni mutación, con tests chicos (cientos de muestras, no miles), y nunca tras cambios de recursos, arte, docs o pegamento. No presumir cantidad de checks.

**R10. Plan primero.** Para lo grande: plan, luego detalles, luego código; no se codifica un plan hasta que el usuario lo diga. En decisiones menores Claude propone un pick (en negrita, con su razón) y el usuario lo acepta en una línea.

**R11. Visible en el mundo.** Una función debe poder leerse en el juego, no solo en los docs: los estados de un NPC (neutral, enojado, apaciguado) y las mecánicas nuevas necesitan una señal visible o audible, y la palabra nueva se explica en el juego (`lang` o advancement) y en `FEATURES`. Lección del "warren" de bunnidogs: el usuario creyó que era un bug.

**R12. Sin plantillas.** Nunca pedir plantillas ni formularios; el feedback suelto basta.

**R13. Entrega.** El zip con el mismo nombre de carpeta y de archivo que se subió, sin `run/` ni `build/`, entregado con `present_files`, y respuesta corta: qué hace / qué NO está compilado / qué probar / qué sigue. El comando está en `START_HERE.md` §5.

**R17. La respuesta empieza por la lista exacta de archivos tocados.** Cada respuesta que cambie algo en el proyecto abre con tres grupos: **añadidos**, **modificados** y **borrados** (los vacíos se omiten), cada archivo en su propia línea con la ruta completa desde la raíz del proyecto (p. ej. `docs/CHUNKS.md`, no "CHUNKS"). La lista sale de comparar la carpeta contra una copia base tomada al empezar el turno (`diff -rq`, comandos en `START_HERE.md` §5), nunca de memoria, y debe coincidir con lo que lleva el zip. Si el turno no tocó archivos, la respuesta dice "ningún archivo modificado". Por qué: el usuario sustituye archivos a mano o revisa cambios; una lista de memoria que omita un archivo le cuesta un error difícil de rastrear.

---

## 4. Mapa de paquetes

Raíz: `net.nicomar2009.lsmmod` (`src/main/java/net/nicomar2009/lsmmod/`).

**Existen:**

| Paquete | Contenido |
|---|---|
| (raíz) | `LSMMod`: punto de entrada; registra todos los `DeferredRegister` en el bus del mod |
| `block/` | `ChairBlock`, `DeskBlock`, `TeachersDeskBlock`, `DeskPart`, `DeskBlockEntity`, `LockerBlock`, `LockerBlockEntity` |
| `entity/` | `SeatEntity` |
| `registry/` | `ModBlocks`, `ModItems`, `ModBlockEntities`, `ModEntities`, `ModCreativeTabs` |
| `client/` | `ClientSetup` (renderer de la entidad asiento) |
| `rules/` | `NpcGroup`, `NpcState`, `PlayerGear`, `Hostility` (lógica pura de hostilidad, desde `N0`; sin imports de Minecraft; aún no la usa ningún código del juego) |

**Planeados (`PLANNED`, no existen):**

| Paquete | Para qué |
|---|---|
| `npc/` | Entidades y comportamiento de los NPC |
| `effect/` | Efectos propios |
| `worldgen/` | Generación del colegio |
| `raid/` | Raids propias y olas |
| `integration/bunnidogs/` | Todo lo que cruza con el mod bunnidogs |

Si hacen falta `item/` (clases de items) u otros, se añaden entonces *(propuesta, no decidido)*. El paquete de mixins que nombra `lsmmod.mixins.json` solo se crea si hace falta un mixin.

**Recursos** (formato 26.x; las carpetas van en singular): `assets/lsmmod/{blockstates, items, models/block, textures/block, lang, sounds}`; `data/lsmmod/{loot_table/blocks, recipe, structure, worldgen, tags}`; `data/minecraft/tags/block/mineable/`.

---

## 5. Convenciones (R16) y patrones del código actual

**Reglas (R16):**

* Todo por `DeferredRegister` en `registry/`; `LSMMod` los registra todos.
* Código de cliente en `client/`, con `@EventBusSubscriber(value = Dist.CLIENT)` (así lo hace `ClientSetup`, además con `modid`).
* **Las tablas mandan:** una enum o tabla por escalera (tiers de la regla, instrumentos, prendas, olas); items, texturas, `lang`, recetas y checks la siguen. Añadir un peldaño = una entrada más y sus archivos.
* Preferir datos a clases. Decir con claridad lo no verificado.
* Nombres en código, identificadores y comentarios: inglés. Docs: español.

**Patrones que ya están en B0** (todos `NOT COMPILED`; si se copian, la copia hereda ese estado):

* Bloques con `DeferredRegister.Blocks` / `registerBlock(nombre, Function, UnaryOperator)`; items con `registerSimpleBlockItem(DeferredBlock)`; entidades con `DeferredRegister.Entities` / `registerEntityType`.
* **Un blockstate que selecciona por `facing` exige que el bloque declare esa propiedad**; si no, el juego dibuja el modelo de "falta" (lo dice el comentario de `ChairBlock`).
* Formas de colisión en píxeles (`Block.box`) para la orientación nativa del modelo, y las otras tres por rotación. **La misma ayuda de rotación está copiada en `ChairBlock`, `DeskBlock` y `LockerBlock`**: si un cuarto bloque la necesita, mejor extraerla.
* Pupitres de dos mitades: la mitad LEFT tiene el block entity; `updateShape` hace desaparecer una mitad si falta la otra; `playerWillDestroy` evita el drop doble en creativo.
* Inventarios: `RandomizableContainerBlockEntity` + `ChestMenu` (27 o 54 slots).
* Nombres de archivo de arte poco uniformes: `studentsdeskleft.png` frente a `high_school_chair.png`. No se renombra sin necesidad (rompería modelos).

---

## 6. Autocomprobación de `rules/`

`tools/RulesCheck.java` **existe desde `N0`** (hostilidad: 19 comprobaciones). Revisa lo que viva en `rules/`; irá creciendo con: la condición del Director con el 90 % de primaria, la composición de olas y los stats. Reglas de uso: R9 (una corrida, tests chicos, sin sabotaje). Comando en `START_HERE.md` §5.

`tools/ParseAll.java` **sí existe** (revisa solo la sintaxis de todo el Java; nunca resuelve tipos, así que no sustituye al compilador).

---

## 7. Arte (D3, decisión abierta)

**Recursos sin usar:** van en `newresources/` (raíz del proyecto, fuera de `src/`; ver su README). Cuando un chunk los use, los mueve a `src/main/resources/`.

Hoy no hay scripts de arte ni modelos de entidad. **Pick propuesto:** texturas de items simples generadas con Pillow como placeholder para que el usuario las sustituya; los modelos de bloque y de entidad los hace el usuario hasta que diga otra cosa. Si se acepta, los scripts irían en `tools/art/` *(propuesta)*.

Lo que existe: 8 texturas de bloque en `assets/lsmmod/textures/block/` y modelos de bloque hechos a mano (con formas por elemento, de ahí las cajas de colisión del código).

---

## 8. Entorno de desarrollo e investigación de API (R14, R15)

**Entorno cloud (R15 actualizado).** El harness selecciona Java 25 e instala el JDK cuando falta, configura proxy/TLS sin guardar credenciales y reutiliza las cachés de Gradle. Hay que respetar la política de red y los permisos de ejecución del entorno. Ver `tools/build/README.md`; si el sandbox impide los sockets locales de Gradle, usar el mecanismo de permisos disponible. La primera preparación de Minecraft puede tardar varios minutos.

**Investigación de API (R14).** Primero el QUICK INDEX de `docs/API_NOTES.md` (existe desde la sesión 4, con la tabla vacía y el mapa de riesgo; el de bunnidogs, con las mismas versiones, está en `docs/history/bunnidogs_patterns/API_NOTES.md` y se enlaza desde ahí). Si falta, clon parcial de NeoForge 26.2.x (~30 s):

```
mkdir nf && cd nf && git init -q && git remote add origin https://github.com/neoforged/NeoForge && git config core.sparseCheckout true && printf 'tests\npatches\nsrc\n' > .git/info/sparse-checkout && git fetch -q --depth 1 origin 26.2.x && git checkout -q FETCH_HEAD
```

* `tests/` tiene código de mods reales (cómo se usa y qué imports); `tests/src/generated/resources/data/` tiene JSON reales de 26.x; `patches/` son solo diffs.
* **Los cuerpos de vanilla NO están**: un nombre solo-vanilla queda ASSUMED hasta que el usuario compile.
* Buscar declaraciones de clase, no nombres de javadoc (NeoForge 26 renombró eventos).
* Sin red: `web_search` / `web_fetch` en docs.neoforged.net y en los *primers* de `neoforged/.github`.
* Si se investigó algo, añadir la fila al índice antes de terminar.
* Estados de una fila: **COMPILED** (el compile del usuario lo aceptó), **SOURCE** (visto en fuentes reales, sin compilar), **ASSUMED** (de memoria de 1.21.x), **AVOIDED** (nunca visto; el código no lo usa).

**Nota de licencia de los nombres Mojang** (conservada de la plantilla): por defecto el MDK usa los nombres oficiales de Mojang para métodos y campos de Minecraft. Esos nombres están cubiertos por una licencia específica que todo modder debe conocer. El texto vigente está en el propio archivo de mapeos y una copia de referencia en https://github.com/NeoForged/NeoForm/blob/main/Mojang.md. Recursos de la plantilla: documentación https://docs.neoforged.net/ y Discord https://discord.neoforged.net/. `TEMPLATE_LICENSE.txt` es la licencia MIT del proyecto NeoForged para los archivos de la plantilla (MDK); no es la licencia del mod (esa es "All Rights Reserved" en `gradle.properties`).

---

## 9. Preferencias del usuario (aprendidas)

* **Git:** trabajar y hacer push siempre a la rama existente `master`. No crear ramas nuevas. Antes de publicar, traer los cambios de `origin/master` e integrarlos conservando el trabajo de otras conversaciones. No usar force-push.

* Pidió **menos checks** (R9) y feedback suelto, sin plantillas (R12).
* Habla en español o inglés, sin formalidad; "sigue con tus picks" acepta todos los picks pendientes.
* Quiere **plan antes de código** para lo grande (R10) y que el zip se continúe solo, sin depender de otros zips.
* Espera ver en el juego lo que hace cada mecánica (R11).

---

## 10. Chuleta de depuración

**Gradle (reportado por el usuario, 9.2.1):** la *configuration cache* está activa. En `build.gradle`, dentro de `doFirst`/`doLast` **no se llama a métodos del proyecto** (`file(...)`, `project...`, `layout`...): se resuelven fuera, al configurar, y el bloque solo usa esas variables. Los nombres `prepareClientRun` y similares ya los define el plugin de NeoForge: comprobar antes de registrar una tarea con un nombre "obvio".

**Hoy (B0), con comandos de vanilla:**

* Dar un bloque: `/give @s lsmmod:locker`, y lo mismo con `high_school_chair`, `elementary_chair`, `teachers_chair`, `students_desk`, `teachers_desk`. **Los pupitres hay que colocarlos con el item**: la segunda mitad la crea `setPlacedBy`, así que un `/setblock` dejaría una sola mitad.
* `/summon lsmmod:seat` no funciona a propósito (`noSummon()`).
* Si el juego dibuja un cubo negro y rosa en vez de un modelo: revisar el blockstate (propiedades que usa) y la ruta del modelo.
* Primero mirar `run/logs/latest.log`; los errores de JSON (recetas, modelos, tags, blockstates) solo aparecen al cargar el juego.

**Planeados (`/lsm ...`, `PLANNED`, nombres provisionales):** crear un alumno con stats dados, forzar el estado enojado o apaciguado de un NPC, generar el colegio en un punto, empezar una raid. Se definen en el chunk que cree cada sistema y se anotan aquí.
