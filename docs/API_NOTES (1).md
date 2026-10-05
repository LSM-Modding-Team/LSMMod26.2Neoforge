# API_NOTES: índice rápido de la API de Minecraft 26.2 / NeoForge 26.2.0.88 para LSM

**Léelo ANTES de investigar cualquier nombre de la API.** Nace en la sesión 4 (Tanda D) con la **tabla de búsqueda vacía a propósito**: ningún nombre de la API ha sido confirmado por un compile del usuario **en este proyecto** (B0 y B0.1 siguen `NOT COMPILED`). Lo que sí compiló, en otro mod, está enlazado en §4 sin copiarlo.

**Regla:** si tuviste que buscar un nombre de la API o un formato de archivo, añade una fila a la tabla de §3 (qué, estado, lugar exacto) **antes de terminar la sesión** (R14). Los docs viejos de esta carpeta pasarán a `docs/history/API_HISTORY.md` solo cuando haga falta (no existe todavía).

**Estados de una fila:** **COMPILED** (el compile del usuario lo aceptó: nombres y tipos bien; el comportamiento puede seguir abierto) · **SOURCE** (visto en fuentes reales de NeoForge, sin compilar) · **ASSUMED** (de memoria de 1.21.x, nada lo confirma) · **AVOIDED** (nunca visto; el código no lo usa).

---

## 1. Playbook de investigación (en este orden; R14, R15)

1. Busca en este archivo (`grep -n "nombre" docs/API_NOTES.md`) y en el de bunnidogs (§4).
2. Si no está, clon parcial de NeoForge 26.2.x (~30 s; antes comprueba que `github.com` responde):
   ```
   mkdir nf && cd nf && git init -q && git remote add origin https://github.com/neoforged/NeoForge && git config core.sparseCheckout true && printf 'tests\npatches\nsrc\n' > .git/info/sparse-checkout && git fetch -q --depth 1 origin 26.2.x && git checkout -q FETCH_HEAD
   ```
3. Qué tiene el clon y para qué sirve:
   * `tests/src/main/java/...`: **código real de mods** que llama a la API. Lo mejor para saber "cómo se usa" y las líneas `import` exactas (`grep -rl "import net.minecraft.world.InteractionHand;" tests src patches`).
   * `tests/src/generated/resources/data/...`: **JSON real de 26.x** (recetas, tags, loot tables...). Lo mejor para formatos de datos.
   * `src/main/java/net/neoforged/neoforge/...`: las clases propias de NeoForge (p. ej. todas las sobrecargas de `DeferredRegister` en `registries/DeferredRegister.java`).
   * `patches/net/minecraft/...`: **diffs** de clases de vanilla. Muestran la firma de un método solo si NeoForge lo cambió o está a 3 líneas de un cambio. **Los cuerpos de vanilla NO están.**
   * **No contiene las fuentes de vanilla**: un nombre solo-vanilla (`Shapes.box`, `Attributes.JUMP_STRENGTH`...) no se puede confirmar ahí; el grep simplemente no encuentra nada.
4. Busca **declaraciones de clase**, no nombres: el javadoc de NeoForge usa nombres viejos (`BlockEvent.BreakEvent` es ahora `BreakBlockEvent`). `grep -rn "class .*Break.*Event" src`.
5. Sin red: `web_search` / `web_fetch` en docs.neoforged.net (páginas de 26.1) y en los *primers* de `neoforged/.github` (`https://raw.githubusercontent.com/neoforged/.github/main/primers/26.1/index.md`; también 26.2 y 26.3).
6. Un nombre solo-vanilla que nadie puede confirmar: escríbelo a la manera 1.21.x, márcalo **ASSUMED**, díselo al usuario y deja que su compile lo resuelva; mejor aún, **diseña alrededor** (ejemplo de bunnidogs: `isInLove()` nunca se vio, así que llevó su propio estado de amor).
7. R6: **una API nueva por chunk**, señalada como el error de compilación más probable.

---

## 2. Mapa de riesgo: familias de API que LSM necesita y ningún código compilado en 26.2 cubre aquí

Cada fila es **un chunk de riesgo** (una por chunk, R6). "Cubierto por bunnidogs" apunta a lo que ya compiló en el **otro** mod (§4); no cuenta como COMPILED para LSM hasta que LSM compile.

| Familia de API | La necesita | Cubierto por bunnidogs (puntero, §4) | Notas |
|---|---|---|---|
| **Items propios con componentes** | `I1` (primero nuevo; `ITEM_SPECS.md` §8) | Sí: `registerItem` / `registerSimpleItem`, `rarity`, tooltips, componente de datos (COMPILED) | Poco riesgo si se copia el patrón. |
| **Efectos propios** (Bad Omen LSM, Trackeo, Estudioso, Regeneración San Martín, Expulsión) | `I2` (`MECHANICS_SPECS.md` §5) | Solo **dar** efectos de vanilla (parcial, SOURCE/ASSUMED). **Registrar** un efecto propio: no | Un efecto por chunk si es posible. |
| **Proyectiles** (pelotas, bola de papel, silla arrojable) | `I1`/`I2` | No | Primera entidad proyectil del mod; "como si tuvieras glowing" de Trackeo es otra familia (efecto). |
| **Armadura / equipment assets de 26.x** | `A1` (`ARMOR_SPECS.md` §4) | No | **Riesgo alto.** Primer chunk: una sola prenda. |
| **Entidad con atributos, renderer, modelo y render-state** | `N1` | Sí (código compilado en bunnidogs, **no está en este zip**, D5) | Pedir el zip antes de M4. |
| **Huevos de aparición** | `N1` | No | |
| **Objetivos de ataque** (goals de objetivo) y **`EntityJoinLevelEvent`** | `N1`, `X1` (`INTEGRATION_BUNNIDOGS.md`) | Solo `HurtByTargetGoal` y el patrón de `registerGoals` (código, no en el zip) | Dos APIs: partirlas. |
| **Mantener un mob en su zona** | `N1` | Ver trampa de §5: `restrictTo` **no compila** | Bunnidogs usa su propio goal de "volver a casa". |
| **Estructuras jigsaw** (template pools, piezas, estructura, conjunto, tags de bioma) | `W1` (`WORLD_SPECS.md` §6) | Registro de estructura y pieza compilado en bunnidogs (código no en el zip) | **Riesgo alto.** |
| **Mapa de exploración / localizar la estructura más cercana** | `W2` | No | **Riesgo alto.** |
| **"¿Esta posición está dentro de un colegio?"** y dato guardado **por instancia de estructura** (D4) | `W1`, `K1` | No | Cómo se resuelve lo decide `W1`. |
| **Raids propias / gestor de olas** | `R1` (`RAID_PLAN.md`, partido en `R1a`-`R1g`) | No (usa POI de nidos de bunnidogs) | **Riesgo alto.** La raid de vanilla es de aldeas. |
| **Discos y música** (laptops, himno, marcha) | `S1` | Solo piezas sueltas: reproducir un sonido de UI (parcial) | En el índice de bunnidogs aparece `MusicDiscTest` de NeoForge como fuente de un detalle de rareza: un punto de partida para investigar. |
| **Sonido en bloques** (laptops, campana) | `B2` | No | |
| **Contenedores con loot table** (casilleros) | `B2` | No | B0 ya usa `RandomizableContainerBlockEntity`, también `NOT COMPILED`. |
| **Barra de jefe** y fases | `K1` | No | |
| **Entidades invocadas** (vexes de Manuel, fantasmas) | `K1` | No | Faltan sus datos (V8). |
| **Teletransporte** (Hidalgo) | `K1` | No | |
| **Advancements** que explican palabras nuevas (R11) | `S1` | Fila ASSUMED de bunnidogs: disparador `minecraft:location` | Formato 26.x por copiar de un JSON real. |
| **Comandos `/lsm ...`** de depuración | cuando haga falta | Comandos de bunnidogs: COMPILED (su código no está) | `docs/REFERENCE.md` §10. |
| **Red** (avisos al cliente) | si R11 lo pide | Payload servidor→cliente: SOURCE | |

**Sospechosos del primer compile de B0** (nombres que B0 ya usa y nadie ha confirmado): lista en `docs/REFERENCE.md` §1. No se repite aquí; cuando el usuario compile, esas filas se mueven a la tabla de §3.

---

## 3. Tabla de búsqueda (necesidad → qué funciona → estado → dónde se verificó)

*(Vacía. Se llena según se investigue, una fila por consulta, antes de terminar la sesión.)*

| Necesidad | Qué funciona | Estado | Dónde se verificó |
|---|---|---|---|
| — | — | — | — |

---

## 4. Lo que ya compiló en bunnidogs (puntero, no copia)

Fuente: `docs/history/bunnidogs_patterns/API_NOTES.md` (mismas versiones: Minecraft 26.2, NeoForge 26.2.0.88). Sus estados valen **para bunnidogs**; para LSM son una pista, no una confirmación. Filas de ese archivo que LSM probablemente reutilice:

| Necesidad de LSM | Fila del índice de bunnidogs | Estado allí |
|---|---|---|
| Items propios | "Register an item with its own class", "Register a plain item", "Rarity", "Tooltip lines" | COMPILED |
| Bloques e items de bloque | "Register a block / block item" | COMPILED |
| Pestaña creativa | "Creative tab" | COMPILED |
| Guardar datos de una entidad | "Save data of an entity" (`ValueOutput` / `ValueInput`) | COMPILED |
| Drops al morir | "Death drops of a LivingEntity", "Drops glue" (`LivingDropsEvent`) | COMPILED |
| Sobrescribir `die` | "A mob dies: give things back" | COMPILED |
| POI: reclamar y soltar | "Claim a free point of interest..." (`getPoiManager`, `take`, `release`) | COMPILED (comportamiento sin probar) |
| POI: registrar uno propio | "Register a modded point of interest" | ASSUMED |
| Click derecho de un item sobre un bloque | "Right-click an item on a custom block..." | ASSUMED, sin nombre nuevo |
| Block entity con item y renderer | "Block entity that holds an item...", "Block entity RENDERER..." | SOURCE |
| Red servidor → cliente | "Server -> client message that opens a window" | SOURCE |
| Recetas, modelos de item y datos JSON | "Recipe JSON (26.x)", "Client item / model JSON", "Cooking recipe JSON" | SOURCE / ACCEPTED |
| Efecto por id | "Give an effect, looked up by id" | parcial SOURCE / ASSUMED |
| Sonido de UI | "Play a UI sound from a client screen" | parcial SOURCE / ASSUMED |
| Reloj del mundo (¿es de noche?) | "Is it night?" | SOURCE |
| Eventos renombrados en 26.x | "Events that changed in 26.x" | COMPILED |
| Advancement al entrar en una estructura | "Advancement fired by entering a structure" | ASSUMED |

---

## 5. Trampas heredadas (costaron tiempo en bunnidogs)

* **`Mob#restrictTo(BlockPos,int)` y `Mob#clearRestriction()` NO compilan en esta versión** (lo rechazó el compile de bunnidogs). Importa a LSM: "los NPC se quedan en su zona" no puede apoyarse en ellas. Bunnidogs guarda la posición él mismo y usa un goal propio para volver. `MoveTowardsRestrictionGoal` sí compiló pero ya no se usa allí. Los nombres alternativos que se sospecharon (`setHomeTo`, `clearHome`) **nunca se verificaron: no usarlos**.
* **`registerGoals()` corre dentro del constructor de la superclase**, antes de que existan los campos de la subclase: los goals se registran para todos y se filtran en `canUse()`. Un campo con inicializador solo es seguro si la superclase no lo toca.
* **El javadoc de NeoForge atrasa respecto al código**: confiar en declaraciones de clase e `import`, no en nombres de javadoc.
* **Un compile prueba nombres y tipos, no comportamiento ni datos**: el JSON (recetas, modelos, tags, blockstates, loot tables) solo se valida al cargar el juego. Todo archivo de datos que añade el mod cuenta como "abierto" hasta que el usuario lo haya ejecutado.
* **`isInLove()`, `canFallInLove()`, `setInLove(Player)`** existen en 1.21.x pero nunca se vieron en 26.x: no confiar en ellos (ejemplo de "diseñar alrededor").
* **Mundo con reloj:** en 26.x el tiempo es por *world clocks*; `isDarkOutside()` / `getDayTime()` no se vieron en ninguna fuente.
