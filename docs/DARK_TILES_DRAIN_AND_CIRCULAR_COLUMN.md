# Baldosas oscuras, desagüe y columna circular

`dark_tiled_floor` es un cubo completo en School Blocks. Reutiliza el dibujo de classroom_floor, recoloreado alrededor de #656667, un gris intermedio entre stone y deepslate. No cambia las baldosas existentes ni añade pinturas.

`school_drain`, en School Functional Blocks, reproduce la referencia: mitad elevada con superficie de baldosas oscuras, base de 8px y canal abierto sobre la otra mitad; seis barras de metal oscuro de 1px atraviesan el canal a la altura superior. El hormigón usa bathroom_floor_side (gris original del pavimento), las barras una textura #2B2E31 con grano discreto. Cuatro orientaciones, colisión exacta de cajas y contorno cúbico sencillo, sin entidad.

`school_circular_column`, en School Blocks, tiene un fuste de 12px de diámetro con dark_school_wall y base cuadrada de 2px con classroom_floor. La sección circular sigue una cuadrícula de medio píxel para adaptar la silueta al estilo del juego; modelo y física comparten las mismas franjas. Al apilar, solo el bloque inferior muestra la base y se ocultan las tapas entre fustes.

Se une horizontalmente con light_school_wall, light_school_wall_railing y ambos bloques pintables. La prolongación usa el material de la pared, como pidió el usuario: light_school_wall para las paredes normales y el color de cada cara/cuadrado vecino para las pintables. Respeta los medios cubos retirados. La base permanece abajo. La columna no se une horizontalmente a otra columna.

Fusion selecciona dieciséis series independientes (cuatro direcciones por cuatro cuadrados), con modelos de geometría heredados y rotaciones compartidas. No hay propiedades de conexión, entidad, ticker ni paletas nuevas: solo un estado. Las colisiones y los contornos se calculan una vez por geometría encontrada y se reutilizan; el color no forma parte de la clave física. La física no se precalcula para combinaciones que no aparecen en el mundo. dynamicShape evita reutilizar una forma de vecinos incorrectos.

Generador: tools/create_dark_tiles_drain_column.py. Comprobaciones: tools/check_dark_tiles_drain_column.py (4 288 casos de orientaciones/ocupación, canal abierto, herencia, transformaciones e idempotencia) y check_optimized_model_resources.py (resolución de recursos y regresiones). Sintaxis de transformaciones comprobada contra el código Fusion neoforge-26.2. Sin compilación ni ejecución de Minecraft: queda pendiente la comprobación visual y de interacción dentro del juego.
