# Pendientes individuales con baranda

Diez ítems independientes: cuatro para el tramo interrumpido y seis para el tramo continuo. Todos llevan pasamanos inclinado. El muro usa `light_school_wall` y el metal `school_gate_edge`, exactamente las mismas texturas que el muro con baranda horizontal. Los modelos OBJ tienen superficies planas, sin escalones visuales. No existe estado HALF ni inversión: siempre suben hacia la dirección horizontal del jugador, incluso al hacer clic en una cara inferior.

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

## Seis piezas entre los extremos de la captura

La captura se interpreta como cinco niveles contando los dos extremos: baranda inferior a altura 0 y superior a altura 4, es decir, cuatro bloques de desnivel. El diseño previo tomaba cinco de desnivel y por eso producía una línea más profunda. Se mantienen seis bloques de longitud total.

Las seis piezas se reconstruyen con dos segmentos de perfil cada una. La primera tiene medio bloque horizontal antes de comenzar la diagonal; la última tiene medio bloque horizontal después de terminarla. Los cinco bloques centrales de recorrido diagonal suben cuatro de altura (pendiente 4/5). Tanto el muro como el pasamanos siguen el perfil horizontal–diagonal–horizontal de la referencia.

| Pieza | Avance desde abajo | Altura de la celda |
| --- | --- | --- |
| `school_railing_slope_flight_1` | 0 | 0 |
| `school_railing_slope_flight_2` | 1 | 1 |
| `school_railing_slope_flight_3` | 2 | 1 |
| `school_railing_slope_flight_4` | 3 | 2 |
| `school_railing_slope_flight_5` | 4 | 3 |
| `school_railing_slope_flight_6` | 5 | 4 |

La altura de la celda no indica la altura del borde: cada pieza tiene un perfil propio que compensa esa diferencia. Retira las seis piezas antiguas y recolócalas según esta tabla, orientadas hacia la subida. Una colocación en seis alturas sucesivas no corresponde a este montaje.

El muro es una franja de 12 píxeles constantes con borde inferior paralelo y espacio libre debajo. El pasamanos conserva las dimensiones del bloque horizontal y tiene soporte en las piezas 1, 3 y 5. Se reconstruyen las UV sobre el perfil de cada cara usando los materiales de pared clara y portón, sin estirar cada mitad sobre toda la textura ni invadir otras texturas del atlas. Las caras internas entre los medios perfiles se omiten para evitar superficies duplicadas.

Coloca `light_school_wall_railing` en avance -1, altura 0 y avance 6, altura 4. Los bordes del muro y del tubo coinciden exactamente con ambos descansos. Las piezas detectan esos extremos por su altura real y actualizan la conexión al colocar o retirar los bloques.

## Descansos y comportamiento

En el tramo interrumpido, coloca `light_school_wall_railing` antes del avance 0 a altura 0 y después del último avance a altura 5; en el continuo, usa las alturas 0 y 4 de la tabla anterior. El bloque horizontal detecta la altura real de los extremos inclinados, incluso si la pendiente ocupa una celda vecina inferior, y añade su brazo en la dirección correcta. Colocación y retirada actualizan también los descansos diagonales. Las piezas del tramo interrumpido se numeran desde abajo; para reproducir una bajada, gira el conjunto y conserva el orden desde su extremo inferior.

Cada ítem coloca únicamente su pieza y entrega un solo drop. Cuatro orientaciones, giro, espejo y agua. El modelo y la colisión incluyen el tubo por encima de la celda: deja libre ese espacio, igual que con la baranda horizontal. La colisión aproxima la superficie en bandas de medio píxel como máximo, y el contorno es una caja simple separada de esa colisión.

Herramientas: `tools/create_school_railing_slopes.py`, `tools/check_school_railing_slopes.py` y `tools/render_school_railing_slopes.py`. Layout de montaje: `tools/school_railing_slope_layout.json`. Vista previa externa: `previews/school_railing_slopes.png`.

Verificación estática de 80 estados, caras/UV OBJ, uniones, alturas y ausencia de inversión. Sin compilación ni ejecución de Minecraft; nueva prueba en juego pendiente.
