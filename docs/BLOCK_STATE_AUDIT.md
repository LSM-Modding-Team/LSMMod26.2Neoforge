# Auditoría de combinaciones de bloques

El usuario informa que, con el último cambio, el arranque tarda 130 segundos y los modelos/texturas se ven iguales dentro del juego. Es una medición del usuario; no se ha ejecutado Minecraft en este entorno. La optimización anterior cambió la representación interna y la selección visual, manteniendo el aspecto.

Inventario estático actual: **116 bloques registrados y 17149 estados Java**. Antes de optimizar las cinco puertas eran 37981. La optimización ya aplicada se detalla en [COMPACT_GLASS_DOORS.md](COMPACT_GLASS_DOORS.md). Se obtiene multiplicando los dominios de las propiedades añadidas por `createBlockStateDefinition`, incluyendo las heredadas. Estos números incluyen combinaciones que la colocación normal nunca utiliza: Minecraft también las registra. No equivalen al número de modelos JSON, estados utilizados en un mundo ni tiempo empleado por bloque.

Reproducir desde la raíz con `python tools/audit_block_states.py`; `--json` incluye el inventario completo y los factores. El lector reconoce las declaraciones y herencia actuales; falla ante tipos no contemplados en lugar de adivinar. Los dominios de clases vanilla están declarados explícitamente para Minecraft 26.2. Inventario guardado: [BLOCK_STATE_AUDIT.json](BLOCK_STATE_AUDIT.json).

| Bloque/familia | Estados por ID | IDs | Total |
|---|---:|---:|---:|
| double_school_entrance | 288 | 1 | 288 |
| tall_classroom_entrance, tall_bathroom_door, tall_teachers_office_door, tall_dining_door | 96 | 4 | 384 |
| playground_awning, elementary_playground_awning | 2312 | 2 | 4624 |
| Cuatro escaleras de cristal, ya optimizadas | 1360 | 4 | 5440 |
| awning_support | 1024 | 1 | 1024 |
| school_gate | 768 | 1 | 768 |
| football_basketball_goal | 576 | 1 | 576 |
| school_entrance_pavement_slab | 384 | 1 | 384 |
| street_school_bars | 384 | 1 | 384 |
| Seis school_entrance_pavement_slope | 256 | 6 | 1536 |
| light_school_wall_railing | 256 | 1 | 256 |
| school_wooden_handrail | 144 | 1 | 144 |
| tall_bathroom_door_full_3 y full_4 | 128 | 2 | 256 |

## Optimización de las puertas ya aplicada (recuento anterior)

La puerta doble registra `4 facing × 2 open × 2 powered × 3 column × 3 row × 2 depth × 2 top_connected × 2 bottom_connected × 8 frame_layout = 9216`.

Cada puerta alta registra `4 facing × 2 hinge × 2 open × 2 powered × 3 row × 2 top_connected × 2 bottom_connected × 8 frame_layout = 3072`.

Las tres propiedades de conexiones multiplican por 32 **todas** las filas de las puertas, aunque solo el cristal necesita seleccionar estos modelos. Estas cinco puertas suman 21504 estados, aproximadamente el 56,6 % del total del mod. Reutilizar la selección visual dinámica de Fusion permitiría retirar esas tres propiedades de las puertas:

- Puerta doble: 9216 → 288.
- Cada puerta alta: 3072 → 96.
- Las cinco: 21504 → 672, eliminando 20832 combinaciones.
- Total previsto del mod: 37981 → 17149, aproximadamente un 54,8 % menos.

Estos objetivos **ya están implementados**; las fórmulas anteriores documentan el coste previo. Conservar orientación, apertura, redstone, bisagras, posiciones multicelda, colisiones, drops y todos los modelos actuales. Adaptar el predicado al cristal superior de la puerta doble (incluidas columnas, profundidad y apertura) y las puertas altas; ampliar la migración NBT y actualizar correctamente filas mixtas de cristal/puerta. Las puertas de madera completas de baño no tienen las tres propiedades y no requieren esta conversión.

## Próximo cambio recomendado: toldos y soporte

Cada toldo multiplica `4 facing × 17 start × 17 end × 2 upper = 2312`. Las alturas sí cambian la geometría y la colisión; no se pueden borrar como si fueran conexiones visuales. Evaluar separar variantes constantes de playground de las alturas configurables, o aprovechar los datos ya existentes en AwningBlockEntity para seleccionar modelos y preservar la física. Comprobar todas las alturas válidas, la segunda celda superior y la recuperación de estructuras antiguas. No limitar arbitrariamente la altura para reducir el recuento.

El soporte multiplica `4 facing × 16 column × 16 width = 1024`. La colocación solo usa `column < width`, pero los estados incluyen las 120 parejas inválidas de columna/anchura: 480 estados redundantes. Compactar las 136 parejas válidas reduciría 1024 → 544 conservando anchuras 1–16. Otra opción requiere mover metadatos a la entidad de bloque existente: tiene mayor alcance porque las dos propiedades también sirven para encontrar el origen y desmontar el conjunto.

## Candidatos secundarios

- Pavimento, slab y sus seis pendientes: conexiones de borde visuales combinadas con propiedades físicas. Evaluar selección Fusion preservando bordes amarillos y continuidad vertical/horizontal; conjunto de 1984 estados, que podría pasar a 31 si todas las conexiones se calculan visualmente. Se conserva la orientación de slopes y tipo/agua del slab.
- street_school_bars: `column` y `row` añaden doce combinaciones visuales al estado de rejas vanilla, 384 → 32 si se calculan visualmente; mantener conexiones y colisión vanilla.
- light_school_wall_railing: separar el poste alternado visual de las conexiones físicas. No borrar las propiedades de conexión que necesita la valla para la colisión.
- school_gate y football_basketball_goal: gran parte del recuento representa posiciones reales de una estructura multicelda. Prioridad menor; no eliminar column/row/depth/cell sin sustituir la lógica de colocación, colisiones y desmontaje.

## Límites de esta auditoría

Es un recuento de estados, no un perfil temporal de Minecraft. La reducción prevista de estados no es una promesa proporcional de segundos. La auditoría es estática; la fase de puertas no se ha compilado ni probado en Minecraft. Tras cada optimización, comparar el arranque con la misma configuración y comprobar puertas, redstone, conexiones, colocación y mundos existentes.
