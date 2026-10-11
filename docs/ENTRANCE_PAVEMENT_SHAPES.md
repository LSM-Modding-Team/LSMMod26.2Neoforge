# Pendiente y slab del pavimento de entrada

Cuatro bloques independientes school_entrance_pavement_slope_1..4 forman una pendiente suave de 4 bloques de largo, repetible en seis columnas paralelas de ancho. No se coloca automáticamente el conjunto. Cada pieza sube 6 px (3/8 de bloque), desde 8 px hasta 32 px: subida de 24 px (1 bloque +8), altura final de dos bloques incluyendo la base. Dirección de colocación hacia la subida. Sin inversión ni baranda.

| Pieza | Avance | Fila de colocación | Altura local inicial/final |
| --- | --- | --- | --- |
| 1 | 0 | 0 | 8→14 px |
| 2 | 1 | 0 | 14→20 px |
| 3 | 2 | 1 | 4→10 px |
| 4 | 3 | 1 | 10→16 px |

Repite esta tabla en seis columnas de ancho. La pieza 2 sobresale 4 px de su celda hacia arriba; deja libre ese volumen. Rellena con pavimento normal debajo de las piezas 3/4 si el terreno no sirve ya de apoyo. La base del primer extremo tiene altura de slab.

school_entrance_pavement_slab es un SlabBlock real: mitad inferior, superior, doble, agua y dos drops al romper el doble. Gris #878d96 y amarillo intenso como el pavimento original. Las tres familias comparten conexiones por altura: se comparan ambos extremos de las aristas, incluidos vecinos en otra fila, para conservar solo el marco exterior. No se borra un borde contra una superficie a otra altura. Al colocar o quitar cualquier pieza se recalculan las uniones cercanas, sin cargar chunks ni recorrer el montaje entero.

El borde de 3 px se construye como regiones de caras sin solapamiento, usando las texturas del pavimento: evita que se estire al cambiar la altura del slab o la inclinación. Mallas OBJ planas, colisiones precalculadas a pasos de 1/32, selección simple separada de la colisión.

Generador tools/create_entrance_pavement_shapes.py; layout tools/entrance_pavement_slope_layout.json; comprobación tools/check_entrance_pavement_shapes.py. Verifica subida, continuidad, 1024 estados de pendiente, 384 estados de slab, mallas sin degeneraciones, volúmenes, colisión y drop doble. Regresión de pavimento y baño conservada. Sin compilar ni probar Minecraft.
