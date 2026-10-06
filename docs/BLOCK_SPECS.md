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
| **Mesa de kiosko** | Decorativa. Ahí está la mamá de cuarto. | Sí: kiosk_table, mesa 2×1×1 con mantel, sin inventario |
| **San Martín de Porres** | Click derecho: regeneración ~30 s. Pasivo: regenera automáticamente a profesores y alumnos cercanos, **más a profesores**. Legendario: tarda muchísimo en minarse. Dos se usan en el ritual (`BOSS_SPECS.md`). | No |
| **Escudo** (3x3) | Al pisarlo: Bad Omen LSM; todos los mobs te trackean (5 min, +25 % de daño); llama al azar a 6-10 profesores y 10-20 alumnos durante 60 s *(propuesta)*. Los profesores salen de su aula de forma temporal. | Visual: sí; efectos pendientes |
| **Laptop (común)** | Reproduce el disco en un radio de 6 bloques *(propuesta)*. | Visual y registro: sí (`laptop`); funciones pendientes |
| **Laptop de Moisés** (mayor rareza) | Reproduce el disco en toda la estructura. Click derecho alterna entre himno y marcha del colegio. | Visual y registro: sí (`moises_laptop`); funciones pendientes |
| **PC** (Aula interactiva y escritorio negro) | Click derecho: **Estudioso 3 min**. 20 % de que llame a un profesor agresivo. Cooldown de 5 min por PC *(propuesta)*. Atrae al **alumno vicioso**, que se sienta a usarla. Si se la rompes, el vicioso te persigue hasta matarte (de un golpe). | Visual y registro: sí (`pc`); funciones pendientes |
| **Campana de recreo** | Los NPC salen al patio (salvo algunos alumnos) durante **90 s**, tiempo para lootear ~2 salones. Cooldown de 15 min *(propuesta)*. | No |
| **Casilleros** | Contenedores con loot table baja (§4). | **Sí:** `locker`, 54 slots |
| **Objetos perdidos** | Contienen ropa de alumno **segura**. | No |
| **Ataúd de Manuel Tirado** | Funciona como cofre del tesoro (`BOSS_SPECS.md`). | No |

Los datos de cada efecto (Estudioso, Trackeo, Bad Omen LSM, Regeneración San Martín): `docs/MECHANICS_SPECS.md` §5. Los discos: `docs/ITEM_SPECS.md` §7.

---

## 2. Notas por bloque

* **Acabado de `computer_desk` (2026-10-05).** Se redondea el frente superior de los dos paneles laterales mediante un perfil de cuarto de círculo de radio 4 píxeles, aproximado con tramos de 0,25 píxeles. El borde negro sigue el perfil. Se añade un relieve horizontal trasero de 1 píxel de alto y 2 de fondo, sin cubrir el monitor. Un mouse pequeño con cuerpo elevado y rueda aparece sobre la cubierta al instalar la PC y desaparece al retirarla. Se sincronizan modelos vacío/ocupado, item y colisiones. BUILD SUCCESSFUL; apariencia dentro de Minecraft pendiente.

* **Corrección visual de `computer_desk` (2026-10-05).** Según las nuevas referencias y correcciones del usuario: se retira el techo, se reemplazan patas y barras por paredes laterales completas de madera, con los cantos delanteros pintados negros. Las pequeñas guías de la bandeja van fijadas a los costados; no hay armazón debajo apoyándola. Bandeja retraída vacía y extendida solo con `has_pc=true`; retirar la PC vuelve a retraerla. Se mantienen monitor y CPU compacta juntos sobre la cubierta (el usuario aclaró que no va debajo). Geometría estructural sin cajas solapadas; se omiten caras totalmente enterradas en otras piezas y las caras artificiales de corte entre las dos mitades. Se regeneran los cuatro modelos y colisiones, y el item muestra la bandeja retraída. BUILD SUCCESSFUL; verificación visual en Minecraft pendiente. Esta corrección no modifica el escritorio gris.

* **Escritorio gris (`gray_computer_desk`, 2026-10-05).** Dimensiones pedidas 2×1×2: dos columnas laterales y dos mitades verticales, orientadas hacia el jugador. Usa exactamente `chair_light_gray`, la textura de `englishroomchair`, para cubierta, laterales, bandeja y soporte lateral de CPU. La bandeja queda bajo la cubierta y el soporte de CPU junto al lateral derecho; ambos vacíos por defecto. Una sola PC instala simultáneamente monitor sobre la cubierta, teclado sobre la bandeja y CPU pequeña en su soporte. Interacción reutilizada de la mesa anterior; estado `has_pc` compartido por las cuatro celdas, sin menú. Colocación comprueba espacio y borde del mundo; destrucción conjunta con un solo drop de escritorio y una PC si estaba ocupada. Pistones bloqueados. Modelos y formas se generan con `tools/create_gray_computer_desk_assets.py`; se reutilizan las texturas existentes sin alterar la silla ni las mesas anteriores. BUILD SUCCESSFUL; prueba dentro de Minecraft pendiente, especialmente cuatro orientaciones, retirada desde cada parte y persistencia.

* **Mesa de computación (`computer_desk`, 2026-10-05).** Petición actual del usuario: un bloque de huella y dos de alto, con un único alojamiento que solo acepta una PC. El clic derecho con `lsmmod:pc` consume una unidad en supervivencia y activa la representación de monitor, teclado y CPU pequeña. Otros items y una segunda PC se rechazan. Shift + clic derecho con mano vacía devuelve la PC (al inventario o al suelo si está lleno). El alojamiento fijo se guarda como `has_pc`, sin block entity ni menú; ambas mitades comparten el estado. La mitad inferior tiene los drops de la mesa y la PC ocupante; la retirada de la otra mitad reutiliza `TwoTallBlock`. Los pistones no la desplazan. Modelo con madera, estructura oscura, bandeja de teclado, repisa y panel trasero, inspirado en la fotografía aportada. Formas de colisión generadas desde los mismos elementos del modelo, para ambos estados y las cuatro orientaciones. No añade funciones electrónicas de la PC. Nueva llamada utilizada: `useItemOn`, verificada por compilación. BUILD SUCCESSFUL; prueba real de colocación, guardado y drops pendiente.

* **Escudo.** 3x3; el disparador "pisarlo" y todo lo que provoca está en `MECHANICS_SPECS.md` §6.1. Dónde va dentro del colegio: sin definir (`WORLD_SPECS.md`).
* **PC.** El 20 % "llama a un profesor agresivo": qué es un "profesor agresivo" y a cuál llama: sin definir. El vicioso es un NPC (`NPC_SPECS.md` §2).

  Visual implementado 2026-10-05 por petición del usuario: únicamente monitor escolar de gama media-baja, con grosor intermedio entre una pantalla delgada y un CRT de caja. Marco y carcasa gris oscuro, trasera escalonada con ventilación, pantalla azul, soporte y base. Sin torre ni periféricos. Modelo JSON y atlas pixel art de 64×64 generados con `tools/create_pc_assets.py`. Registro `pc`, item, cuatro orientaciones, colisión del monitor y soporte, drop propio y traducciones. Sin funciones, recetas ni API nueva; reutiliza el patrón de orientación existente. BUILD SUCCESSFUL con Java 25; prueba en Minecraft pendiente.
* **Laptops.** Cómo se carga el disco, y si el himno y la marcha son discos o sonidos fijos de la laptop de Moisés: sin definir (V17).

  Implementación visual 2026-10-05: dos portátiles abiertos de un bloque, orientación horizontal y forma de selección/colisión adaptada a la base y pantalla. La común usa gris oscuro y plateado, inspirada en un portátil HP habitual de gama media-baja; Moisés usa oro, teclado y panel táctil celestes y un diamante en la tapa. Texturas pixel art propias de 64×64 y modelos JSON generados con `tools/create_laptop_assets.py`. Incluyen items, pestaña creativa, traducciones y drop propio. Sin recetas ni funciones electrónicas por petición del usuario. Ninguna familia nueva de API: se reutiliza el patrón de orientación de los muebles existentes. BUILD SUCCESSFUL en Windows con Java 25; prueba en Minecraft pendiente.
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

## Escudo 3×3: recursos definitivos del ZIP (2026-10-05)

`school_shield` coloca nueve bloques completos de una sola vez en el plano X/Z, centrados donde se coloca el item. Estados: facing, column, row. Sin ampliación y sin comportamiento de alfombra. Orientación hacia donde mira el jugador; retirada conjunta con un solo drop. Efectos de NPC pendientes.

Recursos aportados por el usuario: escudo.zip, nueve PNG originales de 16×16 y sus modelos cúbicos con seis caras, más los bbmodels editables. Se corrigen las referencias erróneas a lsm2 y las rutas sin namespace. Texturas y partículas de cada modelo: lsmmod:block/lsmN. La geometría se conserva intacta. Las texturas corrigen las uniones señaladas por el usuario y usan fondo de end_stone_bricks. Posiciones desde arriba: `6 4 7 / 3 1 2 / 8 5 9`; lsm1 es el centro. Los modelos school_shield_row_col apuntan al lsmN correspondiente. El item usa un mosaico de 48×48 formado con los nueve PNG originales, sin alterar sus píxeles. Los bbmodels se conservan en newresources/escudo/bbmodels.

Se retiran la textura generada y el antiguo modelo del item. BUILD SUCCESSFUL con el harness; comprobados los bytes de las texturas, las caras originales, las 36 variantes y los píxeles del mosaico. Prueba en Minecraft pendiente.

### Correcciones de textura (2026-10-05)

Se desplaza la parte blanca de lsm2 tres píxeles hacia abajo y la continuación blanca de lsm7 un píxel hacia la derecha, repartiendo el resultado entre las piezas correspondientes. Se iguala la franja dorada de lsm6 con la franja inferior de lsm4. El fondo se sustituye con los píxeles reales de assets/minecraft/textures/block/end_stone_bricks.png de Minecraft 26.2; cada pieza repite el patrón vanilla 16×16. Mosaico de inventario y texturas embebidas de Blockbench sincronizados. BUILD SUCCESSFUL; uniones y franja uniforme verificadas, y todos los píxeles exteriores coinciden con el patrón vanilla. Sin prueba en juego de esta corrección.

### Orientación según la nueva referencia

Se invierten conjuntamente la distribución de piezas y sus UV superiores (rotation=180), conservando el centro lsm1. Resultado: borde recto arriba, punta abajo y símbolo negro sobre el centro. Posiciones 6 4 7 / 3 1 2 / 8 5 9. Inventario y bbmodels actualizados. Se verifica que cada píxel del mosaico coincide con los UV renderizados; BUILD SUCCESSFUL. Prueba en Minecraft pendiente.

Corrección 2026-10-05: se sustituye la curva fina de computer_desk por tres cajas por costado, dos escalones enteros y UV de madera con densidad uniforme. Se eliminan las subdivisiones de 0,25 píxeles. Colisiones sincronizadas; mouse y relieve conservados. Compilación correcta; revisión visual Minecraft pendiente.

Corrección del escritorio gris (2026-10-05): split recorta UV proporcionalmente, respetando orientación de caras, y retira caras artificiales de corte entre celdas. Corrige repetición de la pantalla en la columna derecha. Soportes laterales de CPU alcanzan la cubierta. Bandeja mantiene posición vacía y sale 1 píxel al instalar PC; teclado acompaña el movimiento. Modelos y colisiones regenerados. BUILD SUCCESSFUL; revisión visual Minecraft pendiente.

2026-10-05: gray_computer_desk añade mouse pequeño con rueda sobre la cubierta, visible solo con PC. Modelos y colisiones sincronizados. BUILD SUCCESSFUL; revisión visual pendiente.

2026-10-05: school_bell (Timbre escolar) usa newresources/school_bell.json y PNG originales; solo se coloca sobre caras laterales con soporte firme, cuatro orientaciones, forma ajustada al modelo y drop al perder la pared. Bloque/item en pestaña LSM Mod; sin sonido, interacción ni redstone. BUILD SUCCESSFUL con Java 25 y harness offline usando caché existente. Prueba dentro de Minecraft pendiente.

### Orientación del fondo del escudo

2026-10-05: se corrige el fondo de school_shield compensando el giro de 180° de los UV superiores: los píxeles de end_stone_bricks se giran en cada PNG sin mover el símbolo ni sus uniones. Mosaico de inventario y texturas embebidas de los nueve bbmodels sincronizados. BUILD SUCCESSFUL con Java 25 y harness offline; prueba visual dentro de Minecraft pendiente.


2026-10-05: dining_table (Mesa del comedor), bloque decorativo 1×1×1 con tablero blanco de 3 píxeles de grosor, pedestal central y base en cruz de color sólido #D1D1D1. Modelo, textura, colisión ajustada, bloque/item, traducciones y drop propios; pestaña LSM Mod. BUILD SUCCESSFUL con Java 25 y harness offline usando la caché existente. Prueba dentro de Minecraft pendiente.

2026-10-05: dining_table actualiza los cuatro cantos del tablero al mismo gris sólido #D1D1D1 del pedestal; superficie superior blanca y geometría conservadas. Render actualizado. Cambio solo de UV, sin nueva compilación ni prueba Minecraft.

2026-10-05: dining_table afina proporciones: tablero de 2 píxeles, pedestal de 1.5 píxeles y base en cruz de 1 píxel de alto. Conserva tamaño 1×1×1, cubierta blanca y cantos/estructura #D1D1D1. Colisión sincronizada; render alternativo más frontal. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: dining_table engrosa ligeramente las patas inferiores a 2 píxeles de ancho y 1.5 de alto, conservando extremos y largo anteriores. Modelo, colisión y render alternativo sincronizados. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: dining_table prueba patas inferiores de 1 píxel de ancho (uno menos), conservando altura de 1.5 y largo. dining_chair reutiliza la silueta de englishroomchair con respaldo sólido sin agujeros, asiento/respaldo #F4F4F4 y patas #D1D1D1 usando la textura de la mesa. Bloque/item, cuatro orientaciones, asiento interactivo y colisión existentes, traducciones y drop. Renders actualizados. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: dining_chair añade marco continuo gris bajo el asiento y dos uniones traseras hasta el respaldo; las cuatro patas sostienen el marco sin quedar al aire. Colisión propia DINING_CHAIR sincronizada con todos los elementos. Render con profundidad por píxel para evitar superposiciones incorrectas. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: dining_chair elimina los dos soportes traseros que sobresalían sobre el asiento; patas traseras rectas alineadas directamente bajo los extremos del respaldo, marco inferior ajustado y colisión sincronizada. Render corregido. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: speaker y tall_speaker añaden parlantes decorativos de 1 y 2 bloques de altura, respectivamente. Ambos son prismas rectangulares de 12 píxeles de ancho y 10 de fondo, carcasa negra mate y rejilla frontal con bocina/woofers texturizados, sin salientes. Modelos, texturas, registros de bloque/item, traducciones, drops y cuatro orientaciones. El alto reutiliza colocación/retirada vertical de TwoTallBlock, drop solo inferior y bloqueo de pistones; item muestra el prisma completo. Sin reproducción de audio. Render comparativo incluido. BUILD SUCCESSFUL con Java 25 y harness offline; prueba dentro de Minecraft pendiente.

2026-10-05: speaker y tall_speaker corrigen proporciones a 16 píxeles de ancho y 12 de fondo, conservando alturas de 16/32. Texturas frontales redibujadas en 16×16 y 16×32; carcasa 16×16. Woofers con proporción circular, sin estiramiento vertical, y detalle pixelado. Colisiones y render sincronizados. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: drinking_fountain (Bebedero) añade bloque decorativo de pared inspirado en la foto: carcasa gris metálica escalonada, rejillas laterales, bandeja hundida con borde, desagüe y boquilla protegida. Sin botones frontales. Modelo dentro de una celda, atlas pixelado 32×32 con regiones 16×16, colisión propia, bloque/item, traducciones, drop y cuatro orientaciones. Reutiliza el patrón del timbre: solo paredes firmes y drop al perder el soporte. Sin función de beber ni efectos. Render incluido. BUILD SUCCESSFUL con Java 25 y harness offline; prueba Minecraft pendiente.

2026-10-05: drinking_fountain corrige paleta según la foto: carcasa gris medio oscuro #79797B, borde de acero #B2B5B8, bandeja #898D91 y rejillas oscuras. Boquilla usa región cromada más clara del atlas. Geometría conservada; textura, UV y render actualizados. Cambio de recursos sin nueva compilación; prueba Minecraft pendiente.

2026-10-05: kiosk_table (Mesa del kiosko) añade mesa normal decorativa 2×1×1 con cuatro patas de madera, marco bajo la cubierta y mantel blanco roto con caída por los cuatro lados. Texturas 16×16 para madera, cubierta y pliegues. Dos mitades horizontales con colocación y retirada conjunta, cuatro orientaciones, colisiones ajustadas y drop solo en origen. Reutiliza DeskBlock con hasStorage=false: sin inventario ni menú. Modelos de bloque/item, registros, traducciones y render incluidos. BUILD SUCCESSFUL con Java 25 y harness offline; prueba Minecraft pendiente.

2026-10-05: art_table (Mesa del salón de arte) añade mesa de madera 2×4×1, tablero de 2 píxeles y seis patas con marco inferior. Cubierta continua 32×64 dividida en ocho texturas 16×16, con manchas irregulares apagadas de rojo, azul, verde, ocre y pintura clara; sin acabado brillante. Ocho celdas se colocan juntas al validar el espacio; retirada conjunta, un solo drop del origen, cuatro orientaciones y pistones bloqueados. Colisiones derivadas de los modelos; item reducido dentro del límite de coordenadas del formato. Registros, traducciones y render incluidos. BUILD SUCCESSFUL con Java 25 y harness offline; prueba dentro de Minecraft pendiente.

2026-10-05: drinking_fountain ensancha el borde superior de 1 a 1.5 píxeles hacia dentro, conservando contorno exterior y paleta de acero de la foto; bandeja, escalón interior y colisión sincronizados. Se apoya la boquilla hasta el fondo de la bandeja. stool (Taburete) añade asiento de madera sin respaldo, cuatro patas conectadas y travesaños, texturas 16×16 a juego con la mesa de arte, colisión propia y asiento interactivo a altura 7/16 mediante ChairBlock. Registros, traducciones, drop y renders incluidos. BUILD SUCCESSFUL con Java 25 y harness offline; prueba dentro de Minecraft pendiente.

2026-10-05: stool se rehace según la foto: asiento circular pixelado de 2 píxeles de grosor a altura 12/16, cuatro patas más largas y abiertas mediante tres tramos, y dos niveles de travesaños en los cuatro costados. Paleta propia de madera cálida y desgastada, texturas 16×16. Colisión sincronizada; ChairBlock permite altura de asiento configurable sin cambiar la altura de las sillas anteriores. Render actualizado. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: stool aumenta su altura de 12 a 16 píxeles, conservando diámetro del asiento, grosor de 2 píxeles y dos niveles de travesaños. Patas alargadas, colisión y altura para sentarse sincronizadas. Render actualizado. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: vaulting_box (Plinto de gimnasia) añade aparato decorativo de 2 bloques de largo, 1 de ancho y 2 de alto, basado en la referencia: cinco secciones rojas que se estrechan hacia arriba, juntas oscuras, asas texturizadas y tornillos en los extremos, cubierta acolchada negra con borde escalonado. Cuatro celdas con colocación y retirada conjunta, un solo drop, cuatro orientaciones y pistones bloqueados; colisiones derivadas de los modelos. Texturas pixeladas 16×8/16×16, registros, traducciones e item completo. Render incluido. BUILD SUCCESSFUL con Java 25 y harness offline; prueba Minecraft pendiente.

2026-10-05: vaulting_box corrige el estrechamiento: mantiene longitud constante de 32 píxeles en todas las secciones y extremos delantero/trasero verticales; solo el ancho se reduce hacia arriba. Modelos de celdas, item y colisiones sincronizados. Render corregido. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: vaulting_box acentúa el perfil trapezoidal de los costados: ancho pasa de 16 a 10 píxeles con inclinación aproximada en tramos de 1 píxel de alto, en lugar de escalones grandes. Longitud constante de 32 píxeles y extremos verticales; altura conserva los 2 bloques solicitados. Cubierta ajustada a la parte estrecha, UV vertical continuo y colisiones sincronizadas. Render nuevo desde ángulo más alto para mostrar proporciones. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente.

2026-10-05: classroom_door (Puerta de salón) añade puerta de madera rojiza de 1×2 con ventana circular pequeña de marco claro, transparencia y tirador metálico, inspirada en las dos puertas de la foto. Usa DoorBlock con BlockSetType.OAK: apertura manual, redstone, mitades, bisagras y colocación doble vanilla. Modelos y 32 variantes basados en las plantillas oficiales de oak_door; render cutout, texturas 16×16 por mitad e item 16×32. Registros, traducciones, drop solo inferior y tags doors/wooden_doors/axe. Sin decoraciones ni carteles de la foto. BUILD SUCCESSFUL con Java 25 offline; prueba Minecraft pendiente. Render comparativo de dos hojas incluido.

2026-10-05: manuel_tirado_bust (Busto de Manuel Tirado) añade monumento decorativo de tres bloques: pedestal de piedra clara de 2 y busto de bronce mate de 1, inspirado en la foto. Traje con solapas y corbata, cuello, cabeza, orejas y nariz; placa frontal dorada con escudo y líneas pixeladas. Texturas propias 16×16, rostro 8×8 y placa 16×32. Tres celdas verticales con colocación completa, cuatro orientaciones, retirada conjunta, un único drop y pistones bloqueados. Colisiones derivadas de los modelos; item reducido dentro del límite de coordenadas. Registros, traducciones y render incluidos. BUILD SUCCESSFUL con Java 25 y harness offline; prueba dentro de Minecraft pendiente.

2026-10-05: classroom_door amplía la ventana circular de 6 a 8 píxeles de diámetro y representa vidrio azul grisáceo suave con reflejos claros y borde sombreado, en vez del hueco transparente anterior. Texturas de ambas mitades e item sincronizadas, render actualizado. Solo recursos; sin nueva compilación ni prueba Minecraft.

2026-10-05: classroom_door conserva ventanas de 8 píxeles y marco, pero hace transparente el centro mediante alfa 0 en render cutout, con solo unos píxeles de reflejo azul claro. Textura superior e item sincronizados. Render actualizado. Solo recursos; sin nueva compilación ni prueba Minecraft.

2026-10-05: revisión final de los 11 bloques creados en esta conversación: dining_table, dining_chair, speaker, tall_speaker, drinking_fountain, kiosk_table, art_table, stool, vaulting_box, classroom_door y manuel_tirado_bust. Todos tienen bloque/item registrados, inclusión automática en LSM Mod, blockstates, modelos, texturas, traducciones y drops resueltos sin referencias faltantes. Se comprueban las revisiones finales activas: mesa y silla corregidas, parlantes 16×16/16×32, borde ancho del bebedero, taburete alto con dos travesaños, plinto con estrechamiento solo en el ancho, vidrio transparente y busto de tres celdas. Compilación final y contenido del JAR verificados; prueba dentro de Minecraft pendiente.
