# SESSION_LOG: 2-4 líneas por sesión (nunca hace falta para continuar)

Historia, no estado. El estado vive en `START_HERE.md` §2. Una sesión = una conversación con Claude.

* **Sesión 1** (fecha no registrada). Se escribió el diseño (`docs/history/DESIGN_SOURCE_v1.md`, entonces `mod_colegio_dungeon.md`). Ya existían la plantilla de NeoForge y los 6 bloques (3 sillas, 2 pupitres, 1 casillero), que pasan a ser el baseline `B0`. Se escribió el plan de documentación (`DOCS_PLAN.md`).
* **Sesión 2** (2026-10-04). **Tanda A** de docs: `START_HERE.md`, `README.md`, `CHUNKS.md`, `ROADMAP.md`, `REFERENCE.md` y `tools/ParseAll.java`. De bunnidogs solo se copió su `API_NOTES.md` a `docs/history/bunnidogs_patterns/` (D5 quedó parcial). Se leyó el código de B0 y se anotaron las discrepancias diseño ↔ código.
* **Sesión 3** (2026-10-04). Chunk `B0.1` (limpieza de B0: clave `lang` del asiento, receta del pupitre de alumno, descripción del mod, comentarios). **Tanda B** de docs: `DESIGN_SOURCE_v1.md` (literal), `DESIGN.md`, `MECHANICS_SPECS.md`, `NPC_SPECS.md`; 12 vacíos V1-V12. **Tanda C** de docs (la sesión que dicen los propios specs; no quedó en el zip): `ITEM_SPECS`, `ARMOR_SPECS`, `BLOCK_SPECS`, `WORLD_SPECS`, `BOSS_SPECS` (vacíos V13-V22). El usuario resolvió V1, V5 y V12.
* **Sesión 4** (2026-10-04). El usuario subió los 5 specs de la Tanda C porque no estaban en el zip; se copiaron a `docs/`. Se sincronizaron `DESIGN.md`, `NPC_SPECS.md` y `MECHANICS_SPECS.md` con lo resuelto (V1, V5, V12) y con V13-V22. **Tanda D** de docs: `INTEGRATION_BUNNIDOGS.md`, `API_NOTES.md`, `FEATURES.md`, `history/SESSION_LOG.md`, `history/CHUNKS_FULL.md`; `DOCS_PLAN.md` pasó a `history/`. Vacíos nuevos V23-V24. Solo docs: no se tocó el mod ni se compiló nada. Después, a pedido ("lo más chico solo docs"): `docs/RAID_PLAN.md` (plan, sin código; vacíos V25-V26).
* **Sesión 5** (2026-10-04). El usuario dijo "continúa" a secas; B0 y B0.1 seguían sin confirmar. Chunk `N0`: `rules/` con `NpcGroup`, `NpcState`, `PlayerGear`, `Hostility` y `tools/RulesCheck.java` (19/19). Sin API de Minecraft. La precedencia quedó como pick sin aceptar y V4, V6, V13, V16 sin rellenar.
  Después, el usuario recordó `./gradlew runClient` y mandó su `options.txt`: tarea `prepareClientRun` en `build.gradle` y `tools/run-defaults/options.txt` (`T0`).
  Después, a pedido del usuario: nueva regla R17 (la respuesta empieza por la lista exacta de archivos añadidos/modificados/borrados, sacada con `diff -rq` contra una copia base). Solo docs.
  El usuario reportó que `T0` rompía Gradle (`prepareClientRun` ya existía en el plugin). `T0.1`: tarea renombrada a `applyClientOptions`; nueva carpeta `newresources/` con README (recursos sin usar).
  El usuario reportó que `T0.1` fallaba: `file()` dentro de `doLast` rompe la configuration cache de Gradle 9.2.1. `T0.2`: los `File` se resuelven al configurar. Nota para el futuro en `REFERENCE.md` §10.

### 2026-10-05 — Escudo solicitado por el usuario

Se añadió el bloque school_shield y su item, estados y modelos para un único escudo de 3×3. El centro crea ocho piezas al clic derecho; no sobrescribe bloques y el conjunto devuelve un solo item. Textura recreada de la referencia con fondo y borde blanco. Prueba dentro del juego pendiente.

Verificación del escudo: Gradle BUILD SUCCESSFUL (Java 25, NeoForge 26.2.0.88). Sintaxis Java y JSON válidos; comprobado el mapeo UV de las nueve piezas en cuatro orientaciones. Prueba en juego pendiente.

### 2026-10-05 — Corrección del escudo y harness persistente

El usuario corrigió la petición: suelo horizontal completo desde su colocación, sin ampliación. Se actualizan colocación, modelos y textura pixelada de 96×96, cuatro colores, 32×32 por pieza. Se guardan tools/build/compile.sh y environment.sh, con selección/instalación de Java 25, proxy/TLS y cachés. AGENTS.md, START_HERE y REFERENCE documentan la compilación para próximas conversaciones.

El harness persistente se ejecutó correctamente: BUILD SUCCESSFUL. La versión anterior queda sustituida por esta corrección. Se verifican layout horizontal, textura y recursos; sin prueba dentro de Minecraft.

### 2026-10-05 — Alfombra del escudo

A petición del usuario, se reduce la altura a 1/16 y se exige soporte como en CarpetBlock. Se elimina el fondo blanco exterior mediante transparencia, conservando el borde fino; modelos recortados al contorno. BUILD SUCCESSFUL usando el harness persistente. Prueba en Minecraft pendiente.

### 2026-10-05 — Restaurar y publicar

El usuario pide volver al bloque completo anterior a la alfombra y hacer push. Se restauran geometría, colisión, textura, dureza y sonido de B-SHIELD.1. Se conservan la colocación inmediata 3×3 y el harness reutilizable, incluyendo una excepción de .gitignore para versionar tools/build.

Compilación de la restauración: BUILD SUCCESSFUL. Rama de publicación: work.

### 2026-10-05 — Preferencia Git y corrección del destino

El usuario confirma: trabajar y pushear siempre en master, sin crear ramas. Se guarda en AGENTS.md, START_HERE.md y REFERENCE.md §9. Se aplica el escudo sobre el master remoto actual, conservando las sillas añadidas en otra conversación y combinando registros y traducciones.

### 2026-10-05 — Integrar escudo.zip

El usuario aporta nueve PNG, modelos exportados y bbmodels. Se usan sus PNG intactos y la geometría original, corrigiendo referencias a lsm2 y namespaces. Distribución 9 5 8 / 2 1 3 / 7 4 6, con lsm1 en el centro y mosaico de inventario 48×48. BUILD SUCCESSFUL; publicación directa a master.

### 2026-10-05 — Uniones del escudo y ladrillos del End

Corrección de las uniones marcadas en azul moviendo los píxeles de lsm2, lsm7 y la franja de lsm6. Fondo de end_stone_bricks real de Minecraft 26.2, mosaico de inventario y bbmodels sincronizados. BUILD SUCCESSFUL; publicación a master y entrega del JAR.

Al integrar los cambios remotos de San Martín de Porres se detectó la eliminación del registro del escudo. Se restaura su bloque, item y nombre inglés conservando el nuevo bloque de la estatua; compilación del conjunto verificada antes de entregar el JAR.

### 2026-10-05 — Orientación según la referencia del escudo

Se ajusta la composición completa (orden y UV superiores) para mostrar borde recto arriba y punta abajo, con la marca negra sobre el centro. Se conserva end_stone_bricks y se actualizan inventario y bbmodels. BUILD SUCCESSFUL; validación de mosaico y modelos píxel a píxel.

### 2026-10-05 — Registro de las seis sillas nuevas

2026-10-05: se completan los registros de bloque e item de englishroomchair, hallchair y las cuatro plasticchair, utilizando NewChairShapes. La pestaña creativa las incluye automáticamente. Se conservan las tres sillas originales y school_shield. Compilación verificada en Windows con Java 25 y gradlew.bat build --offline: BUILD SUCCESSFUL. Prueba en Minecraft pendiente.

2026-10-05: Se crean las dos laptops solicitadas, únicamente visuales y registradas, sin funciones.
Modelos abiertos y texturas propias: gris/plateado para laptop; oro/diamante para moises_laptop.
BUILD SUCCESSFUL con Java 25 y wrapper Windows offline; Bash encontró CRLF. Prueba en Minecraft pendiente.

2026-10-05: Se añade pc como monitor escolar de gama media-baja, con grosor moderado y soporte.
Modelo/textura propios, registro de bloque/item y recursos; sin torre, periféricos ni funciones.
BUILD SUCCESSFUL en Java 25 con wrapper Windows offline. Prueba en Minecraft pendiente.

2026-10-05: Mesa de computación basada en la fotografía, una columna de dos bloques y un alojamiento exclusivo para PC.
Estado persistente has_pc; inserción con clic derecho, retirada con Shift y mano vacía, representación conjunta de monitor/teclado/CPU pequeña.
BUILD SUCCESSFUL con Java 25 y wrapper Windows offline. Prueba en Minecraft pendiente.

2026-10-05: Escritorio gris de 2×1×2 con la textura exacta de la silla del aula interactiva.
Bandeja inferior de teclado y soporte lateral de CPU; un alojamiento de PC persistente compartido por cuatro partes, usando la interacción anterior.
BUILD SUCCESSFUL con Java 25 offline Windows; colocación, drops y guardado dentro de Minecraft pendientes.

2026-10-05: Correcciones de computer_desk siguiendo dos fotografías: sin techo, paredes laterales de madera, bordes negros y bandeja deslizante ligada a has_pc.
CPU compacta sobre la cubierta junto al monitor; se eliminan caras internas y de corte superpuestas, con colisiones sincronizadas.
BUILD SUCCESSFUL con Java 25 offline Windows. Confirmación del flickering en Minecraft pendiente.

2026-10-05: Se afinan los costados curvos de computer_desk, se añade relieve trasero de un píxel de alto y mouse pequeño ligado a has_pc.
Modelos y colisiones sincronizados. BUILD SUCCESSFUL; comprobación visual en Minecraft pendiente.

2026-10-05: Se retira la curva excesivamente subdividida de computer_desk: tres cajas por costado y UV de madera uniformes.
Compilación correcta; revisión visual Minecraft pendiente.

2026-10-05: Correcciones gray_computer_desk según captura: UV recortados sin repetición, caras de corte eliminadas y soporte CPU hasta cubierta.
Bandeja sale un píxel con PC; modelo y colisiones sincronizados. BUILD SUCCESSFUL; revisión Minecraft pendiente.

2026-10-05: Mouse pequeño sobre gray_computer_desk con PC instalada; modelos y colisiones regenerados. BUILD SUCCESSFUL; revisión visual pendiente.

2026-10-05: school_bell (Timbre escolar) usa newresources/school_bell.json y PNG originales; solo se coloca sobre caras laterales con soporte firme, cuatro orientaciones, forma ajustada al modelo y drop al perder la pared. Bloque/item en pestaña LSM Mod; sin sonido, interacción ni redstone. BUILD SUCCESSFUL con Java 25 y harness offline usando caché existente. Prueba dentro de Minecraft pendiente.


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
