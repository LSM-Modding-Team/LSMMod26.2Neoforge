# Mesa del kiosco y accesorios de entrada

Todos estos bloques están en **LSM Mod School Functional Blocks**.

| ID | Dimensiones y comportamiento |
|---|---|
| `kiosk_table` | Nuevas colocaciones de 3×1, con mantel y soportes originales en los extremos. Un ítem y tres celdas. |
| `school_wood_panel` | Panel de madera marrón, slab vertical de 8 px de grosor; cuatro orientaciones. |
| `school_wood_door` | Puerta de madera de tres bloques, todas las filas se abren juntas. Reutiliza bisagras y redstone de la puerta alta existente. |
| `school_sliding_entrance` | Conjunto de 6×4. Laterales decorativos fijos de 1×4; centro de 4×4 que se abre con clic derecho en el conjunto. |
| `drinking_fountain_raised` | Bebedero original elevado 8 px; reserva dos celdas verticales y necesita pared detrás de ambas. |
| `lost_and_found_box` | Caja hueca de 1×1, sin inventario, con etiqueta blanca «OBJETOS PERDIDOS». |

## Entrada deslizable

Las columnas negras internas delimitan el paso central. Cuatro hojas de un bloque
(dos por lado) se recogen en dos carriles detrás de los laterales decorativos.
La puerta abierta deja libres las cuatro columnas centrales en sus cuatro filas;
no invade posiciones fuera de los seis bloques de ancho.

El estado abierto/cerrado cambia inmediatamente, como en las puertas del juego;
no hay animación gradual ni entidad de bloque. El controlador es la celda inferior
izquierda. La colocación comprueba las 24 celdas y el suelo antes de colocar;
la apertura/cierre comprueba permisos, integridad y entidades antes de cambiar
ninguna celda. Romper cualquier parte retira el conjunto y suelta un solo ítem.

## Compatibilidad y rendimiento

La mesa conserva los valores serializados `part=left/right` y su orientación
original. Los pares antiguos siguen funcionando como mesas de dos bloques;
recolócalos para obtener el tramo nuevo de tres. El nuevo valor `center` no añade
estados a los escritorios del mod.

Colisiones construidas en cuadrículas directas, rotaciones compartidas entre
celdas iguales y contornos de selección exteriores simples. Sin inventarios ni
tickers nuevos. Modelos de entrada idénticos entre filas comparten geometría.
La puerta de madera ignora `powered` en los selectores de modelos.

Estados: mesa 12, panel 4, puerta de madera 128, entrada 192, bebedero 8, caja 4.
La puerta de madera comparte el dominio de cuatro filas con la clase existente,
pero solo coloca tres filas.

## Validación

`python tools/check_kiosk_entrance_assets.py`: cobertura de 348 estados, 4096
muestras del paso central, bolsillos laterales, cuatro orientaciones, desplazamiento
del bebedero, extremos originales de la mesa, controladores de loot e idempotencia.
`check_optimized_model_resources.py`: referencias y preservación de geometría/UV.
`check_furniture_direct_grids.py`: regresión de las formas originales compartidas.
Sin compilación Java ni ejecución de Minecraft; comportamiento y aspecto en el
juego pendientes de prueba.
