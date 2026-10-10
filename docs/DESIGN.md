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
* **N0 (sesión 5):** orden de precedencia de `MECHANICS_SPECS.md` §6.5 como **pick sin aceptar** (Traje > apaciguado > sticker en profesores > provocado > inmunidad o Estudioso > estado). Estudioso se trata como el nivel del uniforme de profesor (lo dice el diseño). Una sola pieza de la prenda del grupo basta para la inmunidad (§6.2). El set completo no cambia el veredicto. Visión con 1 pieza = 0.5 *(propuesta del diseño)*; con 2 o más, sin valor (V6). V4, V6, V13 y V16 siguen abiertos y no se tocaron.

2026-10-05: school_bell decorativo con modelo y textura originales de newresources. Solo caras laterales firmes (isFaceSturdy); caída con drop al perder soporte mediante updateShape/tick. Sin sonido ni redstone. BUILD SUCCESSFUL; prueba Minecraft pendiente.

2026-10-05 — Decisiones de implementación del catálogo solicitado: IDs genéricos sin inventar grados; funciones inferidas del mobiliario marcadas como probables. Cajas interiores por sectores en coordenadas originales, precisión de bloques y medias alturas para losas. 30 s de partículas privadas, configurable de 5 a 120 s; una selección por jugador. Permisos equivalentes a TP vanilla, sin cambios físicos al mundo. Recintos bajos conservados aunque no quepa un jugador de pie. Catálogo estático del mundo, independiente de worldgen y NPC.

2026-10-05 — Renombrado solicitado: ID original estable más alias legible generado del nombre; los cambios son por mundo. Selección automática del recinto más pequeño que contiene al jugador; empate requiere renameid explícito, que también exige estar en ese espacio. No se alteran cajas. Nombres de hasta 80 caracteres y alias sin duplicados. Export completo como archivo y cambios compactos por botón de portapapeles; guardado inmediato antes de confirmar éxito.

2026-10-05 — Depuración solicitada: los 32 nombres del usuario pasan a IDs reales y se conservan seis lugares reconocibles; se retiran los otros 55 espacios y los IDs sustituidos. IDs anteriores disponibles solo para migrar, no como alias. define reemplaza toda la geometría, addbox amplía y delete guarda una baja. Rename ahora cambia ID y retira el anterior. Esquinas de bloque inclusivas; JSON con máximos exclusivos. Ediciones por mundo, no afectan bloques. Export/copia incluyen cajas y bajas. Límites de operación: 128 por eje, 16 sectores y 65536 de volumen por espacio para acotar búsqueda de TP y visualización.

Decisiones sin preguntar (2026-10-07, arte de papelería): paleta de 16 colores vanilla, una geometría padre por tipo y atlas de tapa compartido entre folder/cuaderno. Identificadores `<color>_folder` y `<color>_notebook`; registro decorativo para probar el arte. No se implementan efectos ni recetas en este cambio.

2026-10-08: decisiones sin preguntar: las tres puertas nuevas comparten comportamiento y propiedades de classroom_door; no se añaden recetas ni se sustituyen puertas de estructuras existentes.

2026-10-08 — Superficies escolares: se eligen cubos vanilla sin orientación, mismo material en las seis caras, dureza 1.8/resistencia 6 y pico requerido para drops. Paletas gris beige, gris crema y gris topo tomadas visualmente de las fotos; sin recetas añadidas.

2026-10-08 — Portón 5×3: colocación centrada en la celda inferior, apertura manual simultánea de 90° hacia el lado opuesto al jugador al colocarlo. Malla transparente y chapa marrón oscura según las fotos; grosor 2/16, dureza 5 y resistencia 6. Se comprueba todo el espacio antes de mover las hojas; no se sustituyen líquidos/bloques en el área de apertura. El controlador central mantiene un único drop. Piso actualizado a cuatro baldosas por bloque.

2026-10-08 — Portón: el usuario fija el inicio en el píxel 7 del bloque; grosor conservado de 2 px y bisagra central en 8 px. El escudo conserva geometría, UV, distribución y emblema: únicamente se cambia el material de fondo a las baldosas 2×2 nuevas. Se compensa la rotación superior del modelo en el fondo para alinear las juntas con el piso.

2026-10-08 — Decoraciones de pared: pantalla con ancla inferior en la primera columna local y extensión de cuatro columnas/dos filas; se exige pared sólida detrás de todas las piezas. Proyector y mochila también se cuelgan de pared por las referencias, permaneciendo decorativos. Horario de 14×7 px para conservar la proporción 2:1 de la tabla; atlas 128×64 para conservar sus columnas y bandas, únicamente con trazos sin glifos legibles. Pantalla sin marco, textura RGB blanca exacta y grosor de un píxel. La mochila no es equipable ni almacena objetos.

2026-10-08 — Tachos: se infieren cuerpos de plástico ligeramente cónicos porque la foto solo enseña las tapas. Verde con tapa elevada y negro con tapa baja; escala menor a un bloque. Diseño pixelado sin marcas, letras ni símbolos de reciclaje añadidos. Referencias de forma: https://www.fravega.com/p/tacho-de-basura-con-tapa-rebatible-x-25-lts-verde-colombraro-50013576 y https://www.boyaca.com/bano/tachos-de-basura/tacho-ovalado-vaiven-negro-plapasa-12l-0780. Estas referencias orientan la parte no visible y no identifican los modelos exactos de la foto.

2026-10-08 — San Martín: modelo estilizado conforme a la foto, con caras exteriores recortadas por oclusión y sin cambiar su lógica/colisión original. Baño: inodoro y urinario blancos, separadores grises integrados de un píxel a izquierda/derecha. Se elige un bloque de alto para el cubículo del inodoro y separadores menores para el urinario; ambos modelos caben en una celda. El urinario usa la colocación y la comprobación de pared de WallDecorationBlock, y cae al perderla. El inodoro se coloca desde una cara superior sin requisito de pared trasera. Todos los nuevos sanitarios son pasivos.

2026-10-08 — El usuario precisa cubículo y urinario adulto de dos bloques. Se amplían solo las paredes del inodoro y se integra una puerta gris de un píxel al frente, con apertura hacia el interior junto a la pared izquierda. La porcelana del inodoro conserva sus dimensiones. El urinario pequeño mantiene su id men_urinal y pasa a variante infantil; adult_men_urinal añade la variante alta. Se colocan completos en dos celdas con un único item/drop. Los inodoros ya colocados con la geometría antigua requieren volver a colocarse para crear la mitad superior.


2026-10-09 — Arco con canasta (`football_basketball_goal`), referencia d36f69a: conjunto corregido de 8 bloques de ancho × 6 de alto × 3 de fondo. La trasera es más estrecha arriba, con hombros inclinados en escalones de píxeles (profundidad z30 arriba, z46 abajo), postes blancos y pintura con variaciones discretas. Tablero centrado blanco con borde/recuadro negro, aro naranja y red propia de rombos, estrechada hacia la boca inferior abierta. Malla transparente en laterales, trasera y techo; entrada de goles libre. Un objeto coloca las 68 celdas con geometría, dejando aire en las demás; cuatro orientaciones, apoyo bajo los cuatro postes, desmontaje completo y un solo drop. Colisión detallada con contorno simple por celda. Generador y comprobación estática: `tools/create_sports_goal_assets.py` y `tools/check_sports_goal.py`; 576 variantes, inclinación, red y dimensiones verificadas. Los arcos de la versión anterior de 7 bloques deben retirarse antes de actualizar y colocarse de nuevo. Sin compilación, commits ni publicación; prueba en Minecraft pendiente.


2026-10-09 — Cristal y entrada del colegio, revisión de 4ab9792455e16a927d516486794946ae371911ec: `school_glass` es medio slab vertical de 8 píxeles, girable en cuatro direcciones, con vidrio tenue y reflejos irregulares. `supported_school_glass` conserva ese vidrio y completa los otros 8 píxeles con respaldo crema como en las capturas, sin caras internas coincidentes. `tall_classroom_entrance` es un conjunto decorativo de 2 × 2,5 bloques: dos hojas con las texturas originales de classroom_door y un sobreluz de medio bloque con marco oscuro y cristal; sin señal de salida ni apertura. Un objeto coloca sus seis celdas, requiere apoyo bajo ambas columnas, retira el conjunto al romper una pieza y entrega un solo objeto. Parte superior con modelo y colisión de 8 píxeles de alto. Colisiones separadas del contorno simple y render translúcido; caras contiguas del mismo vidrio/orientación se ocultan. Comprobación estática `python tools/check_school_glass.py` y vista previa externa `python tools/render_school_glass.py`. Sin compilación, commits ni publicación; validación en Minecraft pendiente.


2026-10-09 — Corrección del cristal y la entrada: el vidrio recibe marcos por bloque. TOP_CONNECTED/BOTTOM_CONNECTED se actualizan al colocar, quitar o cambiar vecinos compatibles de igual orientación, eliminando los travesaños y caras en las uniones verticales, conservando el marco exterior. `supported_school_glass` ocupa el slab inferior completo (16×8×16 px) y añade cristal en la mitad vertical superior (16×8×8 px), formando una escalera; no lleva respaldo en la mitad restante. El slab inferior rompe la continuidad del vidrio por abajo. La entrada mide 2×3 bloques: puerta de 2,5 y medio bloque superior con franja centrada de cristal de 16×4×4 px por columna, rodeada por marco oscuro. Texturas originales repartidas sobre la altura de 2,5 sin duplicar la ventana; sin señal ni apertura. Colisiones de la entrada precalculadas en cuatro orientaciones y selección simple. 56 variantes verificadas con Python; sin compilación, commits ni publicación. Validación visual en Minecraft pendiente.


2026-10-09 — Entrada por piezas y corrección de texturas: verificada la unión vertical entre `supported_school_glass` y `school_glass` encima, con el mismo plano de cristal y eliminación del travesaño en ambos modelos; funciona al colocar o quitar el vecino, en cuatro orientaciones. El slab opaco inferior interrumpe la conexión por abajo. Las manijas de las dos hojas apuntan al centro en ambas caras. La textura superior original se reparte de arriba abajo sobre sus 24 píxeles físicos sin repetir ni invertir fragmentos de la ventana. Claraboya con textura propia 16×4 y material uniforme en los cantos, evitando reflejos deformados. La entrada de 2×3 (puerta de 2,5) deja de colocarse como conjunto: seis bloques/objetos independientes para izquierda y derecha, niveles inferior, central y superior con claraboya. Cada objeto coloca una celda y entrega únicamente su propio drop; romper una parte conserva las otras cinco. `tall_classroom_entrance` es ahora la pieza inferior izquierda; se deben retirar las entradas automáticas antiguas antes de actualizar. Colisiones precalculadas por pieza/orientación, contorno simple. Verificación estática en `tools/check_school_glass.py`, preview en `tools/render_school_glass.py`. Sin compilación, commits ni publicación; prueba en Minecraft pendiente.


2026-10-09 — Puerta alta funcional: `tall_classroom_entrance` vuelve a ser un único bloque/objeto que coloca tres filas de una hoja (1×3). Conserva los archivos de textura y la distribución de puerta de 2,5 + claraboya en el medio bloque superior, franja 16×4×4 px por columna. Dos colocaciones contiguas con bisagras opuestas forman la puerta doble. Interacción desde cualquiera de las tres filas, sincronización de apertura de la hoja, redstone desde cualquier altura, sonidos de puerta de madera y eventos BLOCK_OPEN/BLOCK_CLOSE. Las dos puertas contiguas se operan individualmente, como las puertas normales. Modelos/colisiones abiertos y cerrados en cuatro direcciones, 96 variantes; selección simple y formas físicas precalculadas. Requiere apoyo y tres celdas libres; romper una fila retira las otras dos y entrega una sola puerta, no la vecina. Se retiran registros/items/recursos de las cinco piezas adicionales de la entrega anterior; retirar las entradas por piezas antes de actualizar. API comprobada mediante lectura del DoorBlock.java de Minecraft 26.2 en el archivo de fuentes previamente disponible, sin ejecutar compilación. Python estático y CRC/SHA del ZIP; prueba Minecraft pendiente. Sin commits ni publicación.


2026-10-09 — Perfil final de puerta y soportes de cristal: `tall_classroom_entrance` sigue siendo una puerta funcional de tres filas colocadas con un objeto. Hoja móvil inferior de 4 píxeles de fondo (z4..8), tramo central de 8 (z0..8), siguiendo el perfil escalonado de Blockbench. El tercer bloque es idéntico al modelo de `school_glass`, de 8 píxeles de fondo: queda fijo sobre la hoja y utiliza directamente sus cuatro variantes de conexión vertical. `SchoolGlazing` comparte las reglas entre vidrio normal, soportes y cristal superior de la puerta; la unión se actualiza por ambos lados y no cambia al abrir/cerrar ni al activar redstone. Conserva texturas de classroom_door, manijas al centro, bisagras, sonidos, integridad y un solo drop. 384 estados/variantes de puerta. `supported_school_glass` mantiene su cristal y sustituye la textura del slab inferior completo por `dark_school_wall`. Nuevo `mixed_school_glass` con la misma geometría del cristal: debajo del cristal, concreto blanco vanilla (16×8×8 px); al lado, en el resto del slab inferior, `light_school_wall` (16×8×8 px). Conexiones y cuatro orientaciones también en esta variante. Recursos/modelos/colisiones comprobados estáticamente con Python, previews actualizados; sin compilar, commits ni publicación. Prueba dentro de Minecraft pendiente.


2026-10-09 — Corrección final según las dos imágenes de puerta: las filas 0 y 1 tienen 16×16×4 px (z4..8); la fila 2 contiene media altura de puerta de 4 px de fondo y media altura de cristal enmarcado de 8 px de fondo. La hoja inferior abre como puerta normal y el remate superior permanece fijo; tres filas colocadas con un solo objeto, bisagras/manijas y redstone conservados. `supported_school_glass` pasa a tres materiales: cristal azul, pared oscura en la mitad celeste y pared clara en la mitad rosada bajo el cristal. `mixed_school_glass` conserva cristal/muro claro/concreto blanco. Ambos se registran como SchoolGlassStairBlock, subclase real de StairBlock: orientación por dirección del jugador, HALF superior/inferior por cara/altura del clic, cinco formas con esquinas automáticas y WATERLOGGED heredados. Modelos sincronizados con las formas vanilla, incluyendo inversión y las tres regiones de material en las esquinas. Cada escalera conserva su selección simple separada de la colisión exacta vanilla. Conexión vertical de cristal limitada a planos/huellas que coinciden y realmente tocan el límite de la celda, respetando soporte opaco, inversión y esquinas. 1040 estados de los cuatro bloques, geometría/perfil/materiales/drop comprobados mediante Python. StairBlock/Shapes de Minecraft 26.2 consultados en fuentes locales ya disponibles, sin compilación. ZIP de fuente sin caches ni builds; prueba Minecraft pendiente. Sin commits ni publicación.


2026-10-10 — Correcciones de las capturas en juego: marcos de las cuatro escaleras construidos como geometría real con la misma anchura y profundidad de los postes del cristal normal; se retira el borde pintado que producía líneas/caras discordantes en la unión. El cristal colocado arriba/abajo de otro elemento de vidrio hereda su orientación, evitando el desfase de la captura. Uniones normales/invertidas y esquinas siguen la huella y el límite físico de vidrio. Puerta trasladada al límite de la celda (z0..4 en orientación norte) en las dos filas inferiores y en el faldón de madera del tercer bloque. El faldón ahora gira con la hoja; el cristal superior permanece fijo. Colisiones y modelos cerrados/abiertos coinciden, sin el trozo de madera suspendido y desalineado de la captura. Nuevos `supported_school_glass_reversed` y `mixed_school_glass_reversed`: invierten únicamente materiales de las regiones inferiores, con vidrio/marcos/geometría idénticos a sus originales; heredan escalera vanilla (inversión, esquinas, agua). 1680 estados verificados estáticamente, comparación de geometría del cristal entre originales/invertidas, uniones y faldón abierto/cerrado. Previews de uniones/variantes y puerta actualizados. Sin compilar, commits ni publicación; nueva prueba en Minecraft pendiente.


2026-10-10 — Corrección del cristal de las cuatro escaleras: el generador deja de usar el vidrio transparente como oclusor de los marcos y soportes. Se conservan las caras opacas interiores visibles a través del cristal y se recorta el vidrio contra los sólidos para evitar caras coplanares. UV de vidrio/marco corregidas por orientación de cara; materiales inferiores, geometría, colocación y puerta conservados. Regresión estática de postes interiores y uniones de escalera normal/cristal/escalera invertida; preview externo school_glass_stack_corrected.png. Sin compilar, commits ni publicación; prueba dentro de Minecraft pendiente.
