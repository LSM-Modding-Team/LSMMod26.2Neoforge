# FEATURES: cómo se usa cada cosa en el juego

Guía del jugador. Crece **un párrafo por chunk** (R8): cuando un chunk añade algo que se ve o se usa en el juego, se explica aquí, con las palabras que el juego usa (R11).

**Estado:** el proyecto compila con Java 25 / NeoForge 26.2.0.88. La compilación se comprueba con el harness en `tools/build/`. La prueba del escudo corregido dentro de Minecraft está pendiente; si difiere de esta guía, hay que corregirla.

Dónde está todo: pestaña creativa **"LSM Mod"** (icono: el libro de vanilla). Muestra todo lo registrado, así que un item nuevo aparece sin tocar la pestaña.

---

## Lo que existe hoy (B0 y B0.1, `NOT COMPILED`)

Seis bloques. Nombres del juego en inglés por ahora (`lang/en_us.json`); el archivo en español es la decisión D2, todavía sin hacer.

### Sillas (Elementary Chair, High School Chair, Teacher's Chair)

* **Se colocan** mirando hacia donde miras al ponerlas.
* **Clic derecho = sentarte.** Te sientas en una entidad invisible (`seat`) que desaparece sola cuando te levantas.
* **Craftear** (en la mesa de crafteo, forma de silla `G__` / `GGG` / `I_I`):
  * Elementary Chair: lingotes de oro (G) y de hierro (I).
  * High School Chair: losas de piedra lisa (S, arriba y en medio) y lingotes de hierro (I, abajo).
  * Teacher's Chair: cuero (L) y lingotes de hierro (I).
* Se rompen mejor con **pico**. Dureza 2.0.
* Ojo: estas tres sillas **no son** las variantes del diseño (Hall negro con rojo, aula gris, auditorio rojo o blanco); esas se añadirán después (`docs/BLOCK_SPECS.md` §3).

### Pupitres (Student's Desk, Teacher's Desk)

* **Ocupan 2 bloques.** Hay que **colocarlos con el item**: la segunda mitad la crea el juego al colocar, así que un `/setblock` deja una sola mitad.
* **Clic derecho** abre un inventario de **27 slots** (como un cofre). Los dos pupitres usan el mismo tipo de inventario.
* Se rompen mejor con **hacha**. Dureza 2.5. Al romper una mitad desaparece la otra y suelta el item una sola vez.
* **Craftear:**
  * Student's Desk: tablones (P, arriba), palos (S) y un cofre (C) en medio: `PPP` / `SCS` / `S_S`. *(propuesta de B0.1)*
  * Teacher's Desk: tablones (P, arriba), lingotes de hierro (I) y un cofre (C): `PPP` / `ICI` / `I_I`.
* Ojo: el diseño dice que las carpetas son **decorativas**; el código les da inventario. Está abierto (V14, `docs/BLOCK_SPECS.md` §3).

### Casillero (Locker)

* **Clic derecho** abre un inventario de **54 slots** (como un cofre doble).
* Se rompe mejor con **pico**. Dureza 3.0, suena a metal.
* **Craftear:** lingotes de hierro (I) y de cobre (P) arriba y abajo, bloque de hierro (B) y bloque de cobre (C) a los lados en medio: `IPI` / `B_C` / `IPI`.
* **Todavía no tiene botín:** los casilleros de hoy salen vacíos. Las loot tables de contenido (huevos podridos, polos, la Ardilla PUCP...) son del chunk `B2`.

---

## Lógica de hostilidad (N0, `NOT COMPILED`): todavía no se nota en el juego

`N0` añade solo las reglas de "quién te ataca" como código puro; **no hay ningún NPC que las use**, así que no hay nada que probar dentro del juego. Cuando existan los NPC (`N1`), las reglas serán: el **Traje de Manuel Tirado** hace que nadie te ataque; un NPC **apaciguado** no te ataca; **una prenda** del grupo (alumno o profesor) hace que ese grupo no te ataque; **Estudioso** hace que los profesores te vean neutral; el **sticker** hace que los profesores te ataquen igual; y un NPC al que atacas **sí** te ataca. Las prendas reducen además lo lejos que te ven los NPC (una pieza: la mitad). El orden entre el sticker y atacar al NPC es una propuesta mía que puedes cambiar (`docs/MECHANICS_SPECS.md` §6.5).

---

## Lo que todavía NO existe

Todo el resto del diseño está planeado y **no se puede usar ni encontrar**: items (regla, folder, cuaderno, instrumentos...), prendas y uniformes, NPC (alumnos, profesores, jefes), el colegio como estructura del mundo, el mapa, las raids, los efectos (Bad Omen LSM, Trackeo, Estudioso...), los discos y la música. Dónde está planeado cada uno: `docs/ROADMAP.md` y los specs de `START_HERE.md` §7.

Cuando un chunk añada una de esas cosas, aquí aparecerá un párrafo con cómo se obtiene, cómo se usa, qué se ve o se oye en el juego al activarla y qué palabra nueva introduce. Ejemplos de palabras que van a necesitar explicación dentro del juego (R11): *neutral*, *enojado*, *apaciguado*, *Trackeo*, *Estudioso*, *Bad Omen LSM*, *Expulsión*.

## Escudo del colegio (school_shield)

Selecciona el Escudo del colegio en la pestaña LSM Mod y colócalo en el suelo. El item pone el conjunto completo de 3×3 de una sola vez, con el centro en el lugar donde lo colocas. El escudo queda mirando hacia arriba y el borde recto apunta hacia donde miras al colocarlo y la punta queda hacia ti. Usa las nueve texturas de escudo.zip con las uniones corregidas, con el símbolo blanco, fondo rojo oscuro, contorno dorado y fondo de ladrillos de piedra del End. La pieza lsm1 es el centro; los bloques muestran sus texturas en las seis caras. El inventario muestra el escudo completo. Necesita nueve posiciones libres o reemplazables, a la misma altura. No requiere clic adicional y no tiene ampliación. Romper una pieza retira el conjunto y devuelve un solo item en supervivencia, ninguno en creativo. Sin receta ni efectos de NPC por ahora. Prueba en Minecraft pendiente.

## Sillas de aula interactiva, hall y auditorio

Las seis sillas nuevas se incluyen en la pestaña LSM Mod como ítems independientes. Se colocan orientadas hacia el jugador y permiten sentarse con clic derecho sin objeto en la mano. Cada variante usa la colisión de su modelo. Sus recetas y drops existentes quedan conectados a los registros. Compilación verificada; prueba en Minecraft pendiente.

### Laptops decorativas (2026-10-05)
En la pestaña LSM Mod aparecen Laptop y Laptop de Moisés. Se colocan abiertas, orientadas hacia el jugador, y se recuperan al romperlas. La común es gris/plateada; la de Moisés es dorada con detalles celestes de diamante. Todavía no reproducen música ni abren una interfaz. Compilación verificada; prueba en Minecraft pendiente.

### PC escolar (2026-10-05)
El bloque PC escolar está en la pestaña LSM Mod. Representa únicamente un monitor con marco oscuro, carcasa de grosor moderado, soporte y base. Se orienta hacia el jugador al colocarlo y deja su propio item al romperse. Todavía no tiene efectos ni interfaz. Compilación verificada; prueba en Minecraft pendiente.

### Mesa de computación (2026-10-05)
En la pestaña LSM Mod aparece Mesa de computación. Necesita dos espacios verticales libres y ocupa una sola columna. Clic derecho con PC escolar instala una unidad y muestra monitor, teclado y CPU pequeña. No acepta otros objetos ni una segunda PC. Shift + clic derecho con mano vacía retira la PC; romper la mesa en supervivencia permite recuperar la mesa y la PC. El estado se conserva en el mundo. Todavía no tiene funciones electrónicas. Compilación correcta; prueba en Minecraft pendiente.

### Escritorio gris de computación (2026-10-05)
En la pestaña LSM Mod aparece el escritorio gris de 2 bloques de ancho, 1 de fondo y 2 de alto. Usa el mismo gris que la silla del aula interactiva. Requiere los cuatro espacios libres. Clic derecho con PC escolar en cualquier parte instala una sola PC y muestra monitor, teclado en la bandeja bajo la cubierta y CPU pequeña junto al lateral derecho. Rechaza otros objetos y una segunda PC. Shift + clic derecho con mano vacía devuelve la PC. Se retira completo al romperlo y en supervivencia entrega el escritorio y la PC instalada. Compilación verificada; prueba en Minecraft pendiente.

### Corrección visual de la mesa de computación (2026-10-05)
La mesa de madera tiene paredes laterales y cantos negros pintados, sin techo ni patas metálicas. La bandeja queda retraída mientras está vacía; al instalar la PC se despliega con el teclado. El monitor y la CPU pequeña permanecen juntos sobre la cubierta. Al retirar la PC, se retrae la bandeja. Se corrigen las caras superpuestas del modelo; compilación correcta y confirmación visual en Minecraft pendiente.

La mesa de computación incorpora costados redondeados por arriba y un pequeño relieve trasero. Al instalar la PC también aparece un mouse sobre la cubierta, junto al monitor. Compilación verificada; aspecto en Minecraft pendiente.

La mesa de computación usa ahora esquinas de dos escalones grandes, sin la curva subdividida anterior; mantiene mouse, relieve y bandeja retráctil.

La bandeja del escritorio gris mantiene su posición original vacío y sale un píxel al instalar la PC, junto con el teclado; vuelve a retraerse al retirarla. Se corrigen la textura dividida del monitor y el soporte lateral de CPU.

El escritorio gris muestra también un mouse pequeño sobre la cubierta al instalar la PC.

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

2026-10-05: infirmary_cot (Camilla de enfermería) añade camilla decorativa de 2 bloques de largo y 1 de ancho, dentro de una altura de un bloque. Acolchado azul oscuro en dos piezas, cabecera inclinada 22.5°, estructura metálica azul grisácea clara, cuatro patas conectadas, travesaños y tacos negros. Texturas 16×16; colocación/retirada conjunta, cuatro orientaciones, un drop del origen y pistones bloqueados. Colisión aproximada por tramos del respaldo inclinado. Registros, modelos de bloque/item, traducciones, loot y render incluidos. Sin inventario, dormir ni efectos de curación. BUILD SUCCESSFUL con Java 25 y harness offline; prueba Minecraft pendiente.

## Espacios del colegio suministrado

El catálogo tiene tus 32 nombres confirmados y seis lugares reconocibles conservados: 38 espacios iniciales. Se retiraron los recintos provisionales y los IDs reemplazados. Usa `/lsmmod rooms`, `/lsmmod where`, `/lsmmod tp 5tosec` o `/lsmmod boundingbox 5tosec`. Tab completa únicamente los IDs actuales. Comandos para operadores nivel 2; el teletransporte busca apoyo y espacio de pie, sin mover al jugador a un punto inseguro.

Para crear una delimitación: `/lsmmod define sala_nueva -80 136 8 -76 138 12`. Las dos esquinas son bloques inclusivos y puedes invertir su orden. Si el ID ya existe, define reemplaza todas sus cajas. Para añadir un sector: `/lsmmod addbox sala_nueva -75 136 8 -73 138 10`. Para eliminar: `/lsmmod delete sala_nueva`. No modifica los bloques físicos. Los cambios quedan guardados en este mundo y los espacios eliminados no reaparecen al reiniciar.

Ponte dentro del lugar y usa `/lsmmod rename sala de música` para cambiar su ID a sala_de_musica. El anterior queda retirado; no hay alias antiguos. Si hay zonas superpuestas, se elige la más pequeña; `/lsmmod renameid <id> <nombre>` permite seleccionar otro lugar donde estés. Después de renombrar o delimitar se muestran sus límites durante 30 segundos. `/lsmmod boundingbox off` detiene la visualización.

Para pasarme todo: `/lsmmod export`, clic en **[Copiar espacios y límites para pegar en el chat]** y pega el texto en Codex. Alternativa: adjunta school_spaces_export.json de la carpeta lsmmod de tu mundo; el comando muestra la ruta completa. Ambos incluyen los IDs, nombres, coordenadas y eliminaciones actuales. Exporta después de los últimos cambios.

Al actualizar el mod se migra automáticamente el archivo de nombres anterior; no tienes que importar otra vez los nombres que ya confirmaste. Compilación y comprobación de persistencia correctas; prueba dentro de Minecraft pendiente.

Folders y cuadernos (2026-10-07): 32 items decorativos en la pestaña LSM Mod, 16 colores por tipo. Folder oficio rígido sin liga y cuaderno college de tapas lisas, sin espiral. Ejemplos: `/give @s lsmmod:blue_folder` y `/give @s lsmmod:red_notebook`. Aún no aplican Estudioso ni se consumen al usarlos.

2026-10-08: puertas de comedor, sala de profesores y baños disponibles en LSM Mod y mediante /give lsmmod:dining_door, lsmmod:teachers_office_door y lsmmod:bathroom_door; funcionamiento vanilla de puerta de madera.

2026-10-08: sobre 7a5054d, añadidos classroom_floor (baldosas gris beige 4×4 por cara), light_school_wall (gris crema) y dark_school_wall (gris topo de columnas). Tres Block cúbicos completos con cube_all y texturas opacas 16×16, items creativos, traducciones, drop propio y minado con pico. Generador reproducible tools/create_school_surface_assets.py. Solo revisión estática; sin compilación ni prueba Minecraft, sin commits ni publicación por petición del usuario.

2026-10-08: piso corregido a 2×2 baldosas (cuatro por bloque), conservando las dos paredes. Añadido school_gate: portón metálico marrón oscuro de 5×3, dos hojas de 2.5 bloques, marcos, paneles y malla superior transparente. Una colocación crea 15 celdas; clic en cualquier hoja abre/cierra ambas a 90°. Al abrir añade doce celdas para las hojas, ocupando 2.5 bloques de fondo detrás del plano del portón; exige espacio libre y evita colisionar con entidades. Requiere apoyo bajo ambos extremos. Rotura o pérdida de una pieza/apoyo desmonta el conjunto; drop único del controlador inferior central. Pistones bloqueados, sin recetas ni redstone. Texturas por celda 16×16 y modelos cutout; generador y revisión estática reproducibles. Sin compilación, commits, publicación ni prueba Minecraft.

2026-10-08: school_gate desplazado a profundidad 7–9 px, con pivote en 8 px; modelos, colisión y texturas abiertas sincronizados. Las hojas abiertas recorren 40 px desde el pivote (tramos de 8/16/16 px). lsm1–lsm9 conservan exactamente el emblema y sustituyen solo el fondo de end_stone_bricks por classroom_floor, pre-rotado 180° para mantener alineadas las juntas en las caras superiores. Item del escudo y nueve proyectos Blockbench sincronizados; generador tools/create_school_shield_floor_assets.py. Verificados los 360 estados del portón y los píxeles del escudo, sin compilación, commits, publicación ni prueba Minecraft.

2026-10-08: sobre 38ea63e, cuatro decoraciones pasivas: projector_screen (pantalla blanca #ffffff de 4×2, grosor 1/16), wall_projector (carcasa blanca/gris con brazo de pared, ventilación y abertura óptica negra), classroom_timetable (tabla roja/blanca/gris de 14×7×1 px, nueve filas lectivas y cuatro bandas grises, sin título ni contenido legible), emergency_backpack (roja con asa, bolsillo, franja clara y cruz). Colocación exclusiva en paredes para las cuatro. Pantalla validada en ocho celdas y colocada con un solo item; perder soporte o una pieza retira el conjunto, drop único. Los tres bloques pequeños caben en una celda y caen al perder apoyo. Contornos por envolvente simple separados de la colisión detallada. Items creativos, modelos, texturas, traducciones y drops; sin inventarios, proyección, interacción ni recetas. Generador tools/create_classroom_wall_assets.py y comprobación estática tools/check_classroom_wall_assets.py. Sin compilación, commits, publicación ni prueba Minecraft.

2026-10-08: añadidos green_trash_bin y black_trash_bin, dos tachos decorativos de plástico con tapa vaivén cerrada e inmóvil. Verde con tapa alta escalonada (15.5 px de alto), negro con tapa más baja redondeada (13.5 px); cuerpos ligeramente más estrechos en la base reconstruidos a partir de la foto parcial y referencias comerciales. Una celda por tacho, texturas 16×16, colocación sobre caras superiores y cuatro orientaciones. Sin inventario, interacción, procesamiento de basura ni recetas. Items, creativo, traducciones y drop propio; contorno simple por estado separado de colisión detallada. Generador tools/create_school_bin_assets.py. Verificación estática y vista previa externa; sin compilación, commits, publicación ni prueba Minecraft.

2026-10-08: San Martín de Porres reconstruido con geometría sólida y texturas opacas 16×16, hábito oscuro, túnica crema, piel morena, rosario/cruz, escoba, gato, perro y pedestal. Se retiran planos de grosor cero, caras interiores y solapamientos coplanares; rectángulos exteriores fusionados y unión de las mitades sin tapas internas. Item actualizado a figura completa. Registro, estados, colocación en dos bloques y colisión de TwoTallBlock conservados. Añadidos toilet (inodoro apoyado en el suelo sin pared trasera, separadores grises laterales de 1 px, altura 16 px) y men_urinal (urinario de pared con base a nivel del piso y separadores laterales de 1 px, altura 11 px/fondo 7 px). Sanitarios decorativos sin interacción, inventario ni recetas. Items, creativo, drops, traducciones y tags de pico; contornos simples separados de colisión. Generador tools/create_bathroom_statue_assets.py y regresión estática tools/check_bathroom_statue_assets.py. Sin compilación, commits, publicación ni prueba Minecraft.

2026-10-08: corrección del baño. toilet conserva la porcelana original y ahora coloca un cubículo de dos bloques de altura con separadores laterales grises de un píxel y puerta gris integrada, apertura manual de 90° sincronizada entre mitades y colisión abierta/cerrada. Colocación completa con un item, suelo requerido, sin pared trasera, pistones bloqueados y drop único de mitad inferior. men_urinal se conserva como urinario infantil con la misma geometría/recursos y nuevo nombre traducido. Añadido adult_men_urinal de 32 px, con separadores más pequeños que los del inodoro, pared sólida detrás de ambas mitades, colocación de dos celdas y drop único. Sanitarios pasivos; solo la puerta interactúa. Generador tools/create_tall_bathroom_assets.py integrado al generador anterior, comprobaciones estáticas actualizadas y vistas previas. Sin compilación, commits, publicación ni prueba Minecraft.


2026-10-09 — Arco con canasta (`football_basketball_goal`), referencia d36f69a: conjunto corregido de 8 bloques de ancho × 6 de alto × 3 de fondo. La trasera es más estrecha arriba, con hombros inclinados en escalones de píxeles (profundidad z30 arriba, z46 abajo), postes blancos y pintura con variaciones discretas. Tablero centrado blanco con borde/recuadro negro, aro naranja y red propia de rombos, estrechada hacia la boca inferior abierta. Malla transparente en laterales, trasera y techo; entrada de goles libre. Un objeto coloca las 68 celdas con geometría, dejando aire en las demás; cuatro orientaciones, apoyo bajo los cuatro postes, desmontaje completo y un solo drop. Colisión detallada con contorno simple por celda. Generador y comprobación estática: `tools/create_sports_goal_assets.py` y `tools/check_sports_goal.py`; 576 variantes, inclinación, red y dimensiones verificadas. Los arcos de la versión anterior de 7 bloques deben retirarse antes de actualizar y colocarse de nuevo. Sin compilación, commits ni publicación; prueba en Minecraft pendiente.
