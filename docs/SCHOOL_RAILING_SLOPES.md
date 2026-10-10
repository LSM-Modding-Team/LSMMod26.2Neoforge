# Pendientes individuales con baranda

Nueve ítems independientes: cuatro para el tramo interrumpido y cinco para el tramo continuo. Todos llevan pasamanos inclinado. El muro usa `light_school_wall` y el metal `school_gate_edge`, exactamente las mismas texturas que el muro con baranda horizontal. Los modelos OBJ tienen superficies planas, sin escalones visuales. No existe estado HALF ni inversión: siempre suben hacia la dirección horizontal del jugador, incluso al hacer clic en una cara inferior.

## Tramo interrumpido

Se adopta una subida de 5 bloques en 7 de largo, de acuerdo con la proporción de la referencia. Las columnas 2, 3 y 4 pertenecen al soporte oscuro de tres bloques de ancho; allí no se coloca ninguna pendiente ni baranda. Solo hay dos piezas visibles antes y dos después del soporte.

Coordenadas desde el extremo inferior, avanzando hacia la subida:

| Pieza | Avance | Altura de la celda |
| --- | --- | --- |
| `school_railing_slope_split_1` | 0 | 0 |
| `school_railing_slope_split_2` | 1 | 1 |
| Soporte oscuro | 2–4 | según el edificio |
| `school_railing_slope_split_3` | 5 | 4 |
| `school_railing_slope_split_4` | 6 | 5 |

La pendiente sube 5/7 de bloque por columna. Las cuatro piezas tienen perfiles propios para conservar esa misma línea, aunque cambie la altura de sus celdas. El soporte interrumpe el pasamanos; no hay geometría oculta que atraviese la columna. Los soportes metálicos conservan la alternancia de la línea teórica.

## Tramo continuo de 5×5

Coloca `school_railing_slope_flight_1` a `school_railing_slope_flight_5` en las posiciones (avance, altura) (0,0), (1,1), (2,2), (3,3) y (4,4). La superficie sube exactamente cinco bloques en cinco de largo. Solo esas cinco piezas superiores son pendientes con baranda; utiliza bloques normales para el relleno inferior. No se coloca un conjunto automático de 25 bloques.

El pasamanos es continuo y lleva soporte en las piezas 1, 3 y 5. El muro mantiene el rebaje de cuatro píxeles respecto de una superficie completa y el tubo conserva la misma separación que en `light_school_wall_railing`.

## Descansos y comportamiento

En ambos tramos, coloca `light_school_wall_railing` antes del avance 0 a altura 0 y después del último avance a altura 5. El bloque horizontal detecta la altura real de los extremos inclinados, incluso si la pendiente ocupa una celda vecina inferior, y añade su brazo en la dirección correcta. Colocación y retirada actualizan también los descansos diagonales. Las piezas del tramo interrumpido se numeran desde abajo; para reproducir una bajada, gira el conjunto y conserva el orden desde su extremo inferior.

Cada ítem coloca únicamente su pieza y entrega un solo drop. Cuatro orientaciones, giro, espejo y agua. El modelo y la colisión incluyen el tubo por encima de la celda: deja libre ese espacio, igual que con la baranda horizontal. La colisión aproxima la superficie en bandas de medio píxel como máximo, y el contorno es una caja simple separada de esa colisión.

Herramientas: `tools/create_school_railing_slopes.py`, `tools/check_school_railing_slopes.py` y `tools/render_school_railing_slopes.py`. Layout de montaje: `tools/school_railing_slope_layout.json`. Vista previa externa: `previews/school_railing_slopes.png`.

Verificación estática de 72 estados, caras/UV OBJ, uniones, alturas y ausencia de inversión. Sin compilación ni ejecución de Minecraft; nueva prueba en juego pendiente.
