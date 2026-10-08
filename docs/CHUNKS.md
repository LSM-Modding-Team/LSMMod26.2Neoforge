# CHUNKS: una línea por chunk, el más nuevo primero

Un **chunk** es lo que se entrega en un turno: algo que el usuario compila y prueba en pocos minutos (2-6 archivos; `docs/REFERENCE.md` §3, R5). Las filas completas de chunks viejos (archivos, decisiones, riesgos de compilación) están en `docs/history/CHUNKS_FULL.md` (nació en la sesión 4 con `B0` y `B0.1`).

**Estados:** `PLANNED` / `ACCEPTED` / `NOT COMPILED` (entregado sin reporte) / `COMPILES` ("compiló") / `WORKS` ("funcionó") / `SUPERSEDED`. **Solo el reporte del usuario cambia el estado** (R3).

**Ids por área:** `B` bloques · `I` items · `A` armadura · `N` NPC · `W` estructura y mundo · `R` raids · `K` minibosses, boss y ritual · `X` integración bunnidogs · `S` sonido y música.

---

## Entregados (el más nuevo primero)

| Id | Qué hizo | Estado |
|---|---|---|
| `T0.2` | **Arreglo de T0.1** (sin features): `applyClientOptions` llamaba a `file()` dentro de `doLast`, y la *configuration cache* de Gradle 9.2.1 lo prohíbe. Ahora los dos `File` se resuelven al configurar y `doLast` solo los usa. Sin API nueva. Riesgo: otro incumplimiento de la configuration cache; si pasa, pega el error | **NOT COMPILED** (sin confirmar) |
| `T0.1` | **Arreglo de T0** (sin features): la tarea se llama ahora `applyClientOptions`, porque `prepareClientRun` ya existe en el plugin de NeoForge. Misma lógica. Además nace la carpeta `newresources/` con su README (recursos sin usar, fuera de `src/`, no se empaqueta). **Falló al ejecutar** (reporte del usuario): la tarea ya no chocaba, pero rompía la configuration cache (ver `T0.2`). Lo de `newresources/` no falló | **SUPERSEDED** por `T0.2` (solo la tarea de Gradle) |
| `T0` | **Tooling, sin código del mod:** tarea `prepareClientRun` que copiaba `tools/run-defaults/options.txt` a `run/options.txt` antes de `runClient`. **Falló en el arranque de Gradle** (reporte del usuario): el nombre ya existía en el plugin | **SUPERSEDED** por `T0.1` |
| `N0` | **Hostilidad como lógica pura** (sin API de Minecraft, nada visible en el juego): `rules/{NpcGroup, NpcState, PlayerGear, Hostility}` y `tools/RulesCheck.java`. Decidido: el Traje gana a todo; apaciguado no ataca; inmunidad por prenda del grupo; Estudioso = nivel del uniforme de profesor. Pick sin aceptar: precedencia del sticker y de "provocado" (`MECHANICS_SPECS.md` §6.5). Sin rellenar: V4, V6 (2+ piezas), V13, V16. `RulesCheck` 19/19, `ParseAll` 19 archivos, 0 errores. Riesgo de compilación: bajo (Java puro, `record` y `switch` de expresión; el mod usa Java 25) | **NOT COMPILED** (sin confirmar) |
| `B0.1` | **Limpieza de B0** (sin features, sin API nueva de Minecraft): clave `lang` del asiento (`entity.lsmmod.seat`), receta nueva de `students_desk`, descripción real en `neoforge.mods.toml`, comentarios corregidos en 4 clases. Solo JSON, TOML y comentarios; `ParseAll` 15 archivos, 0 errores (solo sintaxis). Riesgo de compilación: ninguno nuevo; el único riesgo es un JSON/TOML mal formado, que solo aparece al cargar el juego. Detalle: `docs/REFERENCE.md` §1 | **NOT COMPILED** (sin confirmar) |
| `B0` | **Baseline:** lo que ya había al empezar el proyecto de docs. 6 bloques (3 sillas con `SeatEntity`, 2 pupitres de dos mitades con 27 slots, 1 casillero con 54 slots), pestaña creativa `lsm_mod`, modelos, texturas, blockstates, loot tables, 5 recetas (falta la del pupitre de alumno) y `lang/en_us.json`. Nadie ha reportado compile ni prueba. Inventario completo y sospechosos de compilación: `docs/REFERENCE.md` §1 | **NOT COMPILED** (sin confirmar) |

---

## Planeados (ids provisionales; se parten y se renumeran al planear cada uno)

Un chunk nuevo necesita antes su spec (Tandas B y C de docs). Cada fila corresponde a un hito de `docs/ROADMAP.md`. Una sola API nueva por chunk (R6).

| Id | Qué | Hito | Estado |
|---|---|---|---|
| `B1` | Alinear los bloques existentes con `BLOCK_SPECS.md`: variantes de silla, 27 slots frente a "decorativo" (la clave de `lang` del asiento y la receta del pupitre de alumno ya se hicieron en B0.1) | M1 | `PLANNED` |
| `B2` | Bloques que faltan del diseño §4 sin API nueva (kiosko, campana, laptops, PC, casilleros con loot, objetos perdidos) | M1 | `PLANNED` |
| `I1` | Items simples: regla y sus tiers, bola de papel, folder, cuaderno, celular, consumibles | M2 | `PLANNED` |
| `I2` | Efectos propios: Estudioso, Trackeo, Bad Omen LSM | M2 | `PLANNED` |
| `A1` | Armadura (API de equipment de 26.x: **riesgo**) | M3 | `PLANNED` |
| `N1` | NPC base: una entidad de alumno con stats, rasgos, estados y zona propia; luego el llamado | M4 | `PLANNED` |
| `W1` | Estructura del colegio por jigsaw, aulas y colocación de NPC | M5 | `PLANNED` |
| `W2` | Uno garantizado cerca del spawn y mapa crafteable | M5 | `PLANNED` |
| `R1` | Patrullas y raids propias (partido en `R1a`-`R1g` en `RAID_PLAN.md` §6; el usuario debe aceptar el plan antes de codificar) | M6 | `PLANNED` |
| `X1` | Integración con bunnidogs (decisión D1 antes) | M6 | `PLANNED` |
| `K1` | Hidalgo, Yahu, fantasma, condición del Director, ataúd, ritual, boss final, traje | M7 | `PLANNED` |
| `S1` | Música (discos, laptops) y advancements que explican las palabras nuevas | M8 | `PLANNED` |

* **B-SHIELD (2026-10-05):** escudo visual 3×3 con centro ampliable al clic derecho, validación de espacio y retirada conjunta; efectos de NPC pendientes. Sin prueba en juego.

Verificación del escudo: Gradle BUILD SUCCESSFUL (Java 25, NeoForge 26.2.0.88). Sintaxis Java y JSON válidos; comprobado el mapeo UV de las nueve piezas en cuatro orientaciones. Prueba en juego pendiente.

* **B-SHIELD.1 (2026-10-05):** reemplaza la versión vertical ampliable por un suelo 3×3 completo al colocarlo; textura pixelada 96×96. Harness reutilizable y documentación para próximas conversaciones. Prueba en juego pendiente.

B-SHIELD queda SUPERSEDED por B-SHIELD.1. B-SHIELD.1: COMPILES (harness, BUILD SUCCESSFUL); WORKS pendiente de prueba real.

* **B-SHIELD.2 (2026-10-05): COMPILES.** Alfombra 3×3 de 1/16 de alto, soporte vanilla, sonido de lana, textura exterior transparente con borde fino y modelos recortados al contorno. Sustituye el grosor completo de B-SHIELD.1. Prueba en juego pendiente.

* **Restauración B-SHIELD.1 (2026-10-05):** B-SHIELD.2 queda REVERTED a petición del usuario. Vuelven los bloques completos 3×3 y la textura opaca anterior; se conserva el harness.

Restauración verificada: BUILD SUCCESSFUL con el harness; modelos de cubo completo y textura opaca 96×96 comprobados. Prueba en juego pendiente.

* **B-SHIELD.ZIP (2026-10-05): COMPILES.** Integra las nueve texturas y modelos aportados, con lsm1 como centro; corrige rutas y orden de piezas, añade mosaico de inventario y conserva bbmodels. Sustituye el arte generado. BUILD SUCCESSFUL; prueba en juego pendiente.

* **B-SHIELD.FIX (2026-10-05): COMPILES.** Alinea las partes blancas y la franja dorada marcadas por el usuario; cambia el fondo a end_stone_bricks vanilla. Inventario y bbmodels sincronizados. BUILD SUCCESSFUL; prueba en juego pendiente.

* **B-SHIELD.ORIENT (2026-10-05): COMPILES.** Composición según la referencia: borde recto arriba, punta abajo, UV superiores 180° y distribución invertida, con inventario y bbmodels sincronizados.

* **B-CHAIRS.REGISTRY (2026-10-05): COMPILES.** 2026-10-05: se completan los registros de bloque e item de englishroomchair, hallchair y las cuatro plasticchair, utilizando NewChairShapes. La pestaña creativa las incluye automáticamente. Se conservan las tres sillas originales y school_shield. Compilación verificada en Windows con Java 25 y gradlew.bat build --offline: BUILD SUCCESSFUL. Prueba en Minecraft pendiente.

* 2026-10-05 — Laptops visuales: laptop y moises_laptop, modelos/texturas, registro de bloque/item y drops. COMPILES (Java 25, build offline en Windows); prueba en Minecraft pendiente. Sin funciones.

* 2026-10-05 — PC visual: bloque pc con monitor escolar de grosor moderado, textura/modelo, registro e item. COMPILES (Java 25, build offline Windows). Sin funciones; prueba en Minecraft pendiente.

* 2026-10-05 — Mesa de computación computer_desk (1×1×2), alojamiento exclusivo de una PC con clic derecho, modelos vacío/ocupado y devolución de PC. COMPILES (Java 25, offline Windows); prueba en Minecraft pendiente.

* 2026-10-05 — Escritorio gris gray_computer_desk, 2×1×2, textura de englishroomchair, bandeja de teclado y soporte lateral de CPU, un alojamiento de PC compartido. COMPILES (Java 25 offline Windows); Minecraft pendiente.

* 2026-10-05 — Corrección visual computer_desk: sin techo, paredes de madera con cantos negros, bandeja retraída/desplegada según PC, eliminación de caras superpuestas. COMPILES; confirmación visual Minecraft pendiente.

* 2026-10-05 — Acabado computer_desk: costados con perfil superior curvo, relieve trasero de 1×2 píxeles y mouse al instalar PC. COMPILES; comprobación visual Minecraft pendiente.

* 2026-10-05 — Simplificación computer_desk: tres cajas por costado, escalones enteros, densidad de textura uniforme. COMPILES; revisión Minecraft pendiente.

* 2026-10-05 — Corrección gray_computer_desk: UV y uniones del monitor, soporte CPU y bandeja extensible un píxel. COMPILES; revisión Minecraft pendiente.

* 2026-10-05 — Mouse en gray_computer_desk con PC instalada. COMPILES; Minecraft pendiente.

2026-10-05: school_bell (Timbre escolar) usa newresources/school_bell.json y PNG originales; solo se coloca sobre caras laterales con soporte firme, cuatro orientaciones, forma ajustada al modelo y drop al perder la pared. Bloque/item en pestaña LSM Mod; sin sonido, interacción ni redstone. BUILD SUCCESSFUL con Java 25 y harness offline usando caché existente. Prueba dentro de Minecraft pendiente.

* 2026-10-05: se corrige el fondo de school_shield compensando el giro de 180° de los UV superiores: los píxeles de end_stone_bricks se giran en cada PNG sin mover el símbolo ni sus uniones. Mosaico de inventario y texturas embebidas de los nueve bbmodels sincronizados. BUILD SUCCESSFUL con Java 25 y harness offline; prueba visual dentro de Minecraft pendiente.


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

2026-10-05: infirmary_cot (Camilla de enfermería) añade camilla decorativa de 2 bloques de largo y 1 de ancho, dentro de una altura de un bloque. Acolchado azul oscuro en dos piezas, cabecera inclinada 22.5°, estructura metálica azul grisácea clara, cuatro patas conectadas, travesaños y tacos negros. Texturas 16×16; colocación/retirada conjunta, cuatro orientaciones, un drop del origen y pistones bloqueados. Colisión aproximada por tramos del respaldo inclinado. Registros, modelos de bloque/item, traducciones, loot y render incluidos. Sin inventario, dormir ni efectos de curación. BUILD SUCCESSFUL con Java 25 y harness offline; prueba Minecraft pendiente.

2026-10-05 — W-CATALOG: 93 espacios del colegio de world (3).zip, catálogo fijo, comandos rooms/tp/boundingbox/where; contornos privados temporales y TP con validación del mundo activo. BUILD SUCCESSFUL Java 25 offline; Minecraft pendiente. No implementa worldgen W1/W2.

2026-10-05 — W-LABELS: rename/renameid del espacio actual, alias en tp/boundingbox/Tab, persistencia inmediata por mundo y exportación JSON con botón para copiar resultados. BUILD SUCCESSFUL Java 25 offline; comprobación de persistencia/exportación correcta; Minecraft pendiente.

2026-10-05 — W-EDIT: 32 nombres confirmados como IDs definitivos, seis lugares conservados y 55 recintos provisionales retirados. define/addbox/delete con coordenadas, rename sin alias antiguos, ediciones y bajas persistentes por mundo, migración de esquema 1 y exportación completa. BUILD SUCCESSFUL Java 25 offline; comprobación de migración/geometría/bajas correcta; Minecraft pendiente.

2026-10-07: I1 arte parcial — folders oficio y cuadernos college, dos geometrías reutilizables y 16 colores por tipo; registro decorativo, sin efectos ni recetas.

2026-10-07: I1 arte revisado localmente — etiqueta pequeña de folder, cuaderno de tapa flexible sin etiqueta. JAR compilado; no publicar antes de confirmación visual.

2026-10-08: incorporación de las tres puertas y auditoría de recursos no utilizados; referencias activas revisadas.

Validación de puertas 2026-10-08: BUILD SUCCESSFUL; 188 formas/orientaciones verificadas y recursos de las tres puertas presentes en el JAR. Sin prueba dentro de Minecraft.

2026-10-08: sobre 7a5054d, añadidos classroom_floor (baldosas gris beige 4×4 por cara), light_school_wall (gris crema) y dark_school_wall (gris topo de columnas). Tres Block cúbicos completos con cube_all y texturas opacas 16×16, items creativos, traducciones, drop propio y minado con pico. Generador reproducible tools/create_school_surface_assets.py. Solo revisión estática; sin compilación ni prueba Minecraft, sin commits ni publicación por petición del usuario.

2026-10-08: piso corregido a 2×2 baldosas (cuatro por bloque), conservando las dos paredes. Añadido school_gate: portón metálico marrón oscuro de 5×3, dos hojas de 2.5 bloques, marcos, paneles y malla superior transparente. Una colocación crea 15 celdas; clic en cualquier hoja abre/cierra ambas a 90°. Al abrir añade doce celdas para las hojas, ocupando 2.5 bloques de fondo detrás del plano del portón; exige espacio libre y evita colisionar con entidades. Requiere apoyo bajo ambos extremos. Rotura o pérdida de una pieza/apoyo desmonta el conjunto; drop único del controlador inferior central. Pistones bloqueados, sin recetas ni redstone. Texturas por celda 16×16 y modelos cutout; generador y revisión estática reproducibles. Sin compilación, commits, publicación ni prueba Minecraft.

2026-10-08: school_gate desplazado a profundidad 7–9 px, con pivote en 8 px; modelos, colisión y texturas abiertas sincronizados. Las hojas abiertas recorren 40 px desde el pivote (tramos de 8/16/16 px). lsm1–lsm9 conservan exactamente el emblema y sustituyen solo el fondo de end_stone_bricks por classroom_floor, pre-rotado 180° para mantener alineadas las juntas en las caras superiores. Item del escudo y nueve proyectos Blockbench sincronizados; generador tools/create_school_shield_floor_assets.py. Verificados los 360 estados del portón y los píxeles del escudo, sin compilación, commits, publicación ni prueba Minecraft.
