# Reducción de estados de cristales y cortinas

Se mantienen los cinco IDs, sus ítems, texturas, geometría, colisiones y las cortinas rectangulares de hasta 4×8. Ninguna puerta pierde sus propiedades actuales.

| Bloque | Estados anteriores | Estados actuales |
|---|---:|---:|
| school_glass | 3200 | 68 |
| Cada una de las cuatro escaleras de cristal | 64000 | 1360 |
| Total de los cinco bloques | 259200 | 5508 |

La reducción es del 97,875 % en estos cinco bloques. `curtain` guarda únicamente `none` y las dieciséis combinaciones válidas horizontal/vertical. Se retiran `curtain_vertical`, `frame_layout`, `top_connected` y `bottom_connected` de su definición Java; las propiedades normales de escalera (orientación, mitad, esquina y agua) permanecen.

Fusion 1.3.16 usa modelos compuestos con el predicado cliente `lsmmod:glazing_mask`. La selección calcula las mismas conexiones verticales y la distribución par/impar de separadores sobre la fila cargada. Selecciona los modelos anteriores sin alterar sus caras, materiales, UV ni rotación. No agrega entidades de bloque ni ticks constantes por cristal. Una selección ordenada reutiliza el resultado por hilo durante esa selección; no conserva una caché de filas ni una referencia fuerte al mundo. No fuerza cargas de chunks; las filas incompletamente cargadas usan marcos individuales provisionalmente.

Los avisos de actualización de fila llegan al cliente aunque el estado Java sea idéntico. Los hooks cliente para paquetes individuales y agrupados invalidan la sección y sus vecinas, permitiendo actualizar los separadores distantes al cambiar la paridad. Las cortinas válidas ya no se revisan cada segundo: se validan tras colocar o modificar sus bloques; los intentos con chunks aún descargados siguen esperando.

## Mundos existentes

Antes de leer las paletas de chunks, `CurtainChunkMigrationMixin` convierte los marcadores antiguos a la propiedad compacta. `CurtainPaletteMigrationMixin` hace lo mismo para lectores directos de estados NBT, incluidas estructuras. La conversión copia únicamente entradas/chunks afectados, mantiene el orden y tamaño de la paleta y no cambia datos de bloques empaquetados. Los datos modernos pasan sin modificación; volver a leer una conversión es idempotente. Un marcador vertical antiguo `none` se normaliza a `single`. Al guardar, los estados quedan con el formato compacto. Comandos externos que especificaban propiedades retiradas deben usar los nuevos nombres.

## Recursos y verificación

`create_school_curtain_assets.py` termina ejecutando `compact_school_glazing_assets.py`; el generador horizontal también puede recuperar sus entradas anteriores desde modelos compuestos. La preparación de recursos del build comparte los compuestos idénticos entre orientaciones/agua, sin cambiar sus condiciones. Más archivos fuente no implican más estados registrados: el objetivo de este cambio es reducir el producto cartesiano Java.

Comprobaciones estáticas: `check_compact_school_glazing.py`, `check_school_curtain_assets.py`, `check_horizontal_glazing.py`, `check_glazing_face_culling.py`, `check_optimized_model_resources.py`. Verifican 10368 selecciones de geometría, los diecisiete marcadores, 2624 orientaciones de cortina, límites y continuidad, regeneración idempotente y equivalencia de recursos preparados. Los hooks y firmas se contrastaron con las clases de Minecraft 26.2 y la API de Fusion; su ejecución sigue pendiente.

No se ha compilado ni iniciado Minecraft por la instrucción vigente. Esta reducción no promete un tiempo concreto de arranque: comparar la duración de registro de bloques y la duración hasta el menú, con los mismos mods y configuración, después de actualizar. El tiempo hasta abrir un mundo incluye también la carga del mundo y no debe confundirse con el arranque.
