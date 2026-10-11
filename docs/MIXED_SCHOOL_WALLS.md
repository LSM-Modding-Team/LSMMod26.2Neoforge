# Paredes de dos texturas orientables

11 de octubre de 2026 (America/Lima). Quince bloques completos, sin conexiones:
cinco proporciones por cada pareja light/dark, light/street y dark/street.
El usuario confirmó caras agrupadas, sin añadir patrones de caras opuestas.

IDs: light_dark_school_wall_1..5, light_street_school_wall_1..5 y
dark_street_school_wall_1..5. El número indica las caras con la primera textura;
las restantes usan la segunda. Proporciones: 1/5, 2/4, 3/3, 4/2 y 5/1.
Dos caras comparten arista; tres se encuentran en un vértice. Las proporciones
4/2 y 5/1 son complementos de esos patrones. Se reutilizan las tres texturas
existentes sin crear PNG adicionales. Todos aparecen en School Blocks.

## Colocación y transformaciones

Una cara del grupo minoritario apunta hacia la cara donde se hace clic. Para
3/3 se orienta el grupo de la primera textura. El punto de clic, relativo al
centro de la celda, determina las otras caras de la esquina/vértice: con dos
caras se elige el borde perpendicular más cercano; con tres se usa el cuadrante.
En un clic exactamente centrado hay un desempate determinista por eje.
Se aceptan las seis caras, incluido suelo y techo; no necesitan apoyo adicional.
Rotación y espejo de estructuras transforman la distribución de las texturas.
El bloque conserva volumen, contorno y colisión completos de Block vanilla.

## Recursos y rendimiento

Una propiedad orientation 0..15 codifica cuatro giros X por cuatro giros Y.
Cada bloque tiene un solo modelo con padre minecraft:block/cube, reutilizado
por sus variantes mediante las rotaciones nativas. Cubre las seis posiciones
de cara, doce de arista y ocho de vértice; algunas orientaciones comparten la
misma distribución de colores, conservando la rotación de UV del modelo.
Son 15 modelos de bloque, 15 referencias de ítem y 240 estados adicionales:
131 bloques registrados y 15084 estados totales después de esta incorporación.

No requieren compuestos dinámicos de Fusion, conexiones de vecinos, entidades
de bloque ni cachés/cálculos de formas físicas. La preparación global sigue
aplicándose y conserva los recursos nuevos. Se evitan modelos independientes
por orientación y propiedades booleanas para cada cara. Dureza 1,8, resistencia
6, sonido de piedra, herramienta apropiada y un drop del propio bloque.

## Generación y verificación

create_mixed_school_walls.py genera registros, modelos, estados, ítems, drops,
traducciones y etiqueta de picota; puede volver a ejecutarse sin duplicar IDs.
MixedSchoolWallBlock implementa la colocación y las transformaciones.
Los controles de familias antiguas mantienen el subtotal previo de 14844
estados en lugar de impedir la incorporación de estos bloques nuevos.

check_mixed_school_walls.py pasa: 132 distribuciones distintas, 720 casos de
colocación en seis caras, rotaciones/espejos, proporciones, recursos, drops,
traducciones y clasificación creativa. Se verificó la composición X/Y contra
el bytecode de Variant.SimpleModelState/Quadrant de Minecraft 26.2.
check_optimized_model_resources.py, check_compact_school_doors.py y
check_door_glazing_faces.py pasan. No se compiló ni ejecutó Minecraft por la
instrucción vigente; falta comprobar visualmente la colocación dentro del juego.
