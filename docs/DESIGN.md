# DESIGN: el diseño acordado y el porqué

**Estado:** escrito en la sesión 3 (Tanda B de `docs/history/DOCS_PLAN.md` §7) a partir de `docs/history/DESIGN_SOURCE_v1.md` (el diseño original, literal; versión 1.0). Actualizado en la sesión 4 con lo que dejó la Tanda C (V13-V22, V1/V5/V12 resueltos por el usuario) y la Tanda D. Aquí no se inventa nada: cada número conserva la marca que tenía.

Marcas (las del diseño original): sin marca = **decidido**; *(propuesta)* = valor de balance elegido, ajustable; *(a confirmar)* = supuesto sin validar.

**El spec de un sistema gana sobre este doc.** Este archivo guarda el concepto, el glosario, lo que falta decidir y las decisiones tomadas sin preguntar. Los números y reglas viven en los specs (§3).

---

## 1. Concepto y principios

Mod de Minecraft tipo dungeon ambientado en el colegio **Libertador San Martín**. La estructura del colegio es un **híbrido entre una mansión de pillagers y un bastión de piglins**: patrullas, raids y capitanes por un lado; zonas de loot cerradas y con mucha hostilidad por otro.

Principios (la razón de casi todas las reglas):

1. **Todos los NPC son muy OP por defecto**, para que haga falta usar las mecánicas (apaciguar, enojar, evadir, uniformes) en lugar de pelear de frente.
2. **Los NPC se quedan en su zona** (profesores en sus oficinas, tutores en sus aulas), controlado con jigsaw. Solo salen por eventos concretos (escudo, llamados, raids) y de forma temporal: así es una dungeon y no un caos.
3. **Los alumnos usan stats y rasgos** (parecido al de los bunnidogs, pero más sencillo y **sin reproducción**) para dar variedad **sin crear mobs nuevos**.
4. **Integración con bunnidogs:** los bunnidogs son hostiles a todos los alumnos y profesores, excepto a Bianca (alumna de 4to). El diseño dice que es "el mismo proyecto" que bunnidogs; hoy son dos zips con ids distintos (decisión abierta D1, §6).

---

## 2. Glosario y estados

Un solo significado por palabra (es el vocabulario fijo de `START_HERE.md` §1).

| Término | Significado |
|---|---|
| **Neutral** | No ataca salvo que lo ataques o veas que atacas (según el tipo de NPC). |
| **Enojado** | Hostil activo hacia el jugador. |
| **Apaciguado** | Hostilidad suspendida temporalmente. |
| **Trackeo** | Los NPC te localizan automáticamente (como si tuvieras glowing), se mueven más rápido y hacen más daño. |
| **Llamar** | Excepción a la regla de quedarse en su zona: el NPC llamado acude rápido desde donde esté y luego vuelve a su posición. |
| **Importantes** | NPC que hay que matar para volver vulnerable al Director. Son todos salvo los no importantes. |
| **No importantes** | NPC que no hace falta matar: Bianca, Lucio, Deivis y un décimo de los alumnos de primaria. |

Los efectos propios del mod (Bad Omen LSM, Trackeo, Estudioso, Regeneración San Martín, Expulsión) están en `docs/MECHANICS_SPECS.md` §5.

---

## 3. Dónde vive cada parte del diseño original

Para comprobar que repartirlo no perdió nada. Desde la sesión 4 todos los docs de esta tabla existen.

| Sección del original | Va a |
|---|---|
| §1 Concepto | este doc (§1) |
| §2 Glosario | este doc (§2); efectos: `MECHANICS_SPECS.md` §5 |
| §3.1 Territorio y llamados, §3.2 Patrullas, §3.3 Raids | `MECHANICS_SPECS.md` §2, §3, §4 |
| §3.4 Generación de colegios | `WORLD_SPECS.md` |
| §3.5 Música | `ITEM_SPECS.md` §7 (discos) y `BLOCK_SPECS.md` (laptops) |
| §4 Bloques | `BLOCK_SPECS.md` |
| §5 Items (armas, pacificar/enojar, instrumentos, consumibles, legendarios) | `ITEM_SPECS.md`; la dirección de cada disparador (enoja/apacigua) también en `MECHANICS_SPECS.md` §6 |
| §6 Armaduras | `ARMOR_SPECS.md`; su efecto en la hostilidad en `MECHANICS_SPECS.md` §6 |
| §7.1 Pelotas | `ITEM_SPECS.md` |
| §7.2 Stats de alumnos | `NPC_SPECS.md` §1 |
| §7.3 Personas | `NPC_SPECS.md` §2 |
| §7.4 Minibosses, §7.5 Boss final | fila en `NPC_SPECS.md` §2; detalle en `BOSS_SPECS.md` |
| §8 Traje de Manuel Tirado | `ARMOR_SPECS.md`; precedencia en `MECHANICS_SPECS.md` §6 |
| §9 Progresión y ritual, condición del Director | `BOSS_SPECS.md`; lista de importantes en `NPC_SPECS.md` §2 |
| §10 Integración con bunnidogs | `INTEGRATION_BUNNIDOGS.md` |
| §11 Descartados | este doc (§4) |
| §12 Pendientes y supuestos | este doc (§5) |

---

## 4. Elementos descartados

* Pistola de juguete (invocaba a Hidalgo).
* Mazo como item del mod.

---

## 5. Pendientes y supuestos

### 5.1 Pendientes del diseño original (faltan datos)

| Id | Qué falta | Afecta a | Estado |
|---|---|---|---|
| P1 | Ingredientes del crafteo del mapa al colegio (lista de legendarios de bunnidogs: candidatos en `INTEGRATION_BUNNIDOGS.md` §2: `titan_haunch`, `gilded_foot`, `moon_sinew`, `listening_shell`, `everbloom`; decide el usuario) | `WORLD_SPECS`, `INTEGRATION_BUNNIDOGS` | abierto |
| P2 | Casco y botas del uniforme de profesores, y dónde se consigue el peinado escolar | `ARMOR_SPECS` | abierto |
| P3 | Dónde se obtiene la guitarra eléctrica (legendario) | `ITEM_SPECS` | abierto |
| P4 | Estadísticas numéricas de cada NPC (vida, daño, velocidad) y de los tiers de la regla | `NPC_SPECS`, `ITEM_SPECS` | abierto |
| P5 | Reacción exacta de cada instrumento según tipo de profesor o alumno | `ITEM_SPECS`, `MECHANICS_SPECS` | abierto |
| P6 | Stats de alumnos de secundaria, 4to y 5to; detalles de las psicólogas | `NPC_SPECS` | abierto |
| P7 | Disposición exacta del ritual (cómo se colocan los dos santos y el ataúd) | `BOSS_SPECS` | abierto |
| P8 | Versión de Minecraft y loader | todo | **RESUELTO:** Minecraft 26.2, NeoForge 26.2.0.88, Java 25 (leído de `gradle.properties`, sesión 2) |

### 5.2 Supuestos a confirmar (del diseño original)

| Id | Supuesto |
|---|---|
| A1 | La casaca de promoción de 5to Sec. tiene el mismo efecto que la de 6to. |
| A2 | Cualquier combinación de piezas de alumno (p. ej. zapatos con buzo) cuenta para el set completo. |
| A3 | Los números de las olas de raid y los valores de duración, cooldown y rango son propuestas de balance. |

### 5.3 Vacíos y contradicciones detectados al repartir el diseño (sesión 3)

No estaban en el original. Se anotan para que el usuario decida y para que ningún chunk los resuelva por su cuenta. Id `V` = vacío. **Resueltos por el usuario: V1, V5, V12.** Abiertos: V2-V4, V6-V11 y V13-V26.

| Id | Qué no queda claro | Fuente | Lo bloquea |
|---|---|---|---|
| V1 | **RESUELTO por el usuario:** los alumnos de 6to **cuentan como primaria** para el 90 % de la condición del Director (`BOSS_SPECS.md` §3). Antes: ¿cuentan? Son "de 6to de primaria" (brigadier) pero "se comportan como secundaria con tamaño de primaria". | §7.3, §9.2 | — |
| V2 | "Los alumnos de primaria llaman a **su profesor**": ¿es el tutor de su aula o un profesor de la sala de profes? | §3.1 | `N1`, llamados |
| V3 | **Cuándo** llaman Rosa Sanmartiniana, Pollo, la enfermera y Miss Alessandra: el diseño dice a quién, no con qué disparador (solo primaria lo dice: "al ser enojados"). | §3.1 | `N1`, llamados |
| V4 | Quién reacciona a "verte atacar a otros": solo el personal de limpieza y Deivis lo dicen; el glosario dice "según el tipo de NPC" sin tabla. | §2, §7.3 | `N0`, matriz |
| V5 | **RESUELTO por el usuario.** Grupo **Profesores**: profesores, tutores, Miss Alessandra, enfermera, psicólogas, Director, Hidalgo, Junior, Moisés, Pollo y Lucio. Grupo **Alumnos**: todos los alumnos, incluidos Yahu, Rosa Sanmartiniana, brigadieres, Bianca, la estudiantina y el vicioso. **Sin grupo:** personal de limpieza, Deivis, mamá de cuarto, fantasma y boss (`ARMOR_SPECS.md` §2). Quedan dos huecos que salen de esto: V13 y V16. | §6 | — |
| V6 | Rango de visión con 2 y 3 piezas (solo se da 1 pieza = mitad *(propuesta)*) y relación con la stat Percepción. | §6, §7.2 | `N0`, `ARMOR_SPECS` |
| V7 | Trackeo: la velocidad extra no tiene número; el "+25 % de daño" figura en el escudo y se lee como daño que los NPC te hacen; la mamá de cuarto da Trackeo de 10 min *(propuesta)* sin más datos. | §2, §4, §7.3 | `I2` (efecto) |
| V8 | Los **vexes de Manuel Tirado** (Expulsión) no tienen fila en las entidades del §7, ni los **fantasmas** con los que el boss revive a los muertos (¿uno por NPC muerto?, ¿con qué stats?). | §7.4, §7.5 | `K1` |
| V9 | Mamá de cuarto agredida: "todo el colegio te ataca **excepto 5to**". La Ardilla PUCP da inmunidad ante 4to **y** 5to. ¿Por qué solo 5to aquí? | §5.5, §7.3 | `N0`, matriz |
| V10 | Deivis es personal de limpieza según §9.2 ("personal de limpieza (salvo Deivis)"), pero §7.3 lo lista aparte. Se lee como un miembro especial del personal. | §7.3, §9.2 | `NPC_SPECS` |
| V11 | Celular: en la columna de profesores dice "Enoja a todos". ¿A todos los profesores o a todos los NPC? | §5.2 | `ITEM_SPECS`, `N0` |
| V12 | **RESUELTO por el usuario:** el **folder** apacigua a **todos** los profesores y da Estudioso 30 s al jugador; el **cuaderno** apacigua **solo a profesores de primaria** y su Estudioso (20 s) vale **solo para ellos** (`ITEM_SPECS.md` §3). Qué profesor es "de primaria": V13. | §2, §5.2 | — |

Detectados al escribir la Tanda C (sesión 3). Tampoco están resueltos; el spec que los toca dice "sin definir" en vez de rellenarlos.

| Id | Qué no queda claro | Spec donde se anota | Lo bloquea |
|---|---|---|---|
| V13 | Alcance de "profesores": qué profesor es **"de primaria"** (cuaderno), y si una prenda o item "para profesores" alcanza también al Director, a Moisés y a los demás del grupo. | `ITEM_SPECS` §3, `ARMOR_SPECS` §2 | `I1`, `N0`, `A1` |
| V14 | Mapeo de las **sillas y pupitres** del código con los del diseño (variantes por zona; sillas que se montan; carpetas con 27 slots frente a "decorativas"). Los picks de `BLOCK_SPECS.md` §3 siguen abiertos. | `BLOCK_SPECS` §3 | `B1` |
| V15 | **Traje de Manuel Tirado:** ¿una prenda o un set de 4?, ¿en qué ranura? El diseño lo lista como una fila de la tabla de prendas. | `ARMOR_SPECS` §2, §5 | `A1`, `K1` |
| V16 | Qué pasa al **mezclar** piezas de alumno con piezas de profesor (grupo del set completo). | `ARMOR_SPECS` §3 | `N0`, `A1` |
| V17 | Datos de items y bloques sin definir: silla arrojable (cómo se obtiene, cooldown), lonchera (cómo se recarga), medallas (factor y duración), diploma (cuántos corazones), huevo podrido (radio y duración), laptops y discos (cómo se carga, de qué loot sale cada uno, lista de canciones, si himno y marcha son discos), canciones del parlante. | `ITEM_SPECS` §1, §5, §7; `BLOCK_SPECS` §2 | `I1`, `B2`, `S1` |
| V18 | **Trueque del kiosko:** qué vende la mamá de cuarto y a cambio de qué; qué se paga por la lonchera. | `BLOCK_SPECS` §2, `ITEM_SPECS` §5 | `N1`, `B2` |
| V19 | Tamaño de las zonas y del colegio, distribución, **lista de grados** y aulas, cantidad de NPC por zona y de alumnos por salón. | `WORLD_SPECS` §2, §3 | `W1` |
| V20 | **Yahu y el Director sin datos:** comportamiento, stats, zona, ataque, drops (salvo el mapa al ataúd) y por qué Yahu es "Alcalde". | `BOSS_SPECS` §5, §9 | `K1` |
| V21 | **Fases del boss:** cuántas y a qué vida cambia cada una. | `BOSS_SPECS` §8 | `K1` |
| V22 | **Ardilla PUCP:** "exactamente una" en los casilleros de 4to y 5to; ¿una por colegio, por salón o por casillero? | `BLOCK_SPECS` §4, `ITEM_SPECS` §6 | `B2`, `W1` |

Detectados en la sesión 4 (Tanda D y `RAID_PLAN.md`):

| Id | Qué no queda claro | Spec donde se anota | Lo bloquea |
|---|---|---|---|
| V23 | **Alcance de la hostilidad de los bunnidogs:** el diseño dice "hostiles a todos los alumnos y profesores". ¿Incluye a Director, Hidalgo, fantasmas, boss, mamá de cuarto, Deivis y personal de limpieza (que hoy no tienen grupo, V5)? | `INTEGRATION_BUNNIDOGS` §3 | `X1` |
| V24 | ¿Los NPC de LSM **devuelven** el ataque a los bunnidogs o solo los bunnidogs los atacan? El diseño solo da la dirección bunnidog → NPC. | `INTEGRATION_BUNNIDOGS` §3 | `X1`, `N1` |
| V25 | **Fin de una raid:** victoria, derrota, recompensa; y efecto del nivel (I-V) del Bad Omen LSM sobre las olas. | `RAID_PLAN` §7 | `R1` |
| V26 | **Estandarte del escudo** que lleva el capitán de la ola 3: qué es y qué hace. | `RAID_PLAN` §8 | `R1`, `I1` |

---

## 6. Decisiones abiertas

Vienen de `docs/history/DOCS_PLAN.md` §5 (movido a `history/` al terminar la Tanda D): esta es la copia viva. El usuario no ha respondido ninguna; "sigue con tus picks" acepta todas.

| Id | Decisión | Pick *(propuesta)* | Se responde |
|---|---|---|---|
| D1 | ¿Uno o dos mods? | Dos jars; `lsmmod` depende de `bunnidogs`, nunca al revés; todo en `integration/bunnidogs/`; los goals se añaden a los bunnidogs con `EntityJoinLevelEvent`. Riesgo: ejecutar ambos en `runClient` requiere configurar Gradle. Detalle: `INTEGRATION_BUNNIDOGS.md` §7. | antes del hito M6 |
| D2 | Idioma | Docs en español; código, identificadores y comentarios en inglés; Claude responde en el idioma del usuario; textos del juego `en_us` más un archivo en español (código de idioma por verificar). **Aplicado como pick.** | cuando el usuario diga otra cosa |
| D3 | Arte | Texturas de items simples generadas con Pillow como placeholder; modelos de bloque y de entidad los hace el usuario. | antes del hito M2 |
| D4 | Progreso del Director y del ritual: ¿por colegio o global? El diseño no lo dice y hay varios colegios. | Por colegio, guardado en la instancia de la estructura. | antes del hito M7 |
| D5 | Soltar `bunnidogs-mod.zip` | **Parcial.** En la sesión 2 solo se copió su `API_NOTES.md` a `docs/history/bunnidogs_patterns/` (la Tanda D lo enlaza desde `docs/API_NOTES.md`). El plan original también pedía copiar ~10 archivos de código que ya compiló en 26.2 (entidad con atributos, renderer, estructura, comando, red, pantalla): **no están en este zip** y LSM los va a necesitar desde M4. Requiere que el usuario suba `bunnidogs-mod.zip`. | antes del hito M4 |
| D6 | Regla de raid "donde hay muchos nidos" (cuántos, radio, cómo se elige el lugar) | `RAID_PLAN.md` §5: dos parámetros de datos (cantidad y radio, valores sin definir) y el lugar es el jugador con el omen. | antes del hito M6 |
| D7 | El Alma de Manuel Tirado figura como "parte del ritual" (§5.5) pero el ritual (§9.1, paso 7) solo nombra los dos San Martín y el ataúd. | El alma se entrega en el ataúd. `BOSS_SPECS.md` §7 lo deja como pick abierto. | antes del hito M7 |

---

## 7. Decisiones tomadas sin preguntar (por sesión o chunk)

* **Sesión 2 (docs, Tanda A):** docs en español (D2, pick). Vocabulario fijo provisional. Hitos M1-M8 como propuesta.
* **B0.1 (sesión 3):** receta del pupitre de alumno `PPP`/`SCS`/`S S` (tablones, palos, cofre) *(propuesta)*; texto de la descripción de `neoforge.mods.toml`; no se añadió `noSave()` al asiento (API nueva).
* **Tanda B (sesión 3, solo docs):**
  * El vocabulario fijo de `START_HERE.md` pasa de "provisional, a confirmar" a las definiciones del §2 del diseño. Las marcas *(a confirmar)* que tenían eran de antes de tener el diseño.
  * Se interpretó "Trackeo" como un **efecto del jugador** (lo da el escudo o la mamá de cuarto) cuyas consecuencias recaen en los NPC; el diseño lo usa a la vez como efecto y como verbo ("te trackean"). Ver V7.
  * Se añadió el concepto de **inmunidad** (las prendas "dan inmunidad ante un grupo: no te atacan") como distinto de **neutral** (set completo: solo atacan si los atacas). Es la lectura literal de §6; no es una palabra nueva del vocabulario fijo.
  * En `NPC_SPECS.md` las columnas de grupo de inmunidad y de zona llevan `—` o *(a confirmar)* donde el diseño no dice nada (V5); no se rellenaron por inferencia.
  * **Ninguna decisión de balance nueva** (`docs/history/DOCS_PLAN.md` §8). Los vacíos V1-V12 se listan sin resolver.
* **Tanda C (sesión 3, solo docs; los 5 specs no estaban en el zip y el usuario los subió en la sesión 4):** `ITEM_SPECS`, `ARMOR_SPECS`, `BLOCK_SPECS`, `WORLD_SPECS`, `BOSS_SPECS`. El usuario resolvió V1, V5 y V12. Los picks de `BLOCK_SPECS.md` §3 (no borrar ni renombrar bloques, dejar que las sillas se monten, dejar los 27 slots) y D4/D7 quedan **abiertos** hasta que el usuario responda.
* **Sesión 4 (solo docs, Tanda D):**
  * Se trasladó a `NPC_SPECS.md` y `MECHANICS_SPECS.md` lo que los specs de la Tanda C ya dan por resuelto (V1, V5, V12): son las mismas decisiones del usuario, no una decisión nueva. Se tocaron solo las celdas que dependían de eso.
  * `INTEGRATION_BUNNIDOGS.md` lleva los datos de bunnidogs que estaban en `docs/history/DOCS_PLAN.md` §3, tal como se leyeron del zip de bunnidogs en la sesión 2; **no se re-verificaron** (el zip de bunnidogs no está).
  * Se añadieron V23 y V24 (hostilidad de los bunnidogs); ningún chunk debe resolverlos por su cuenta.
  * `API_NOTES.md` nace con la tabla de búsqueda **vacía** y el mapa de riesgo; los patrones que ya compilaron en bunnidogs se enlazan, no se copian.
  * **Ninguna decisión de balance nueva.**
  * `RAID_PLAN.md` (solo docs): gestor de raids propio en vez de reutilizar la de vanilla *(pick, por verificar)*; una raid por jugador; sin nidos cerca el omen sigue corriendo. Ningún número nuevo. Vacíos V25 y V26.
