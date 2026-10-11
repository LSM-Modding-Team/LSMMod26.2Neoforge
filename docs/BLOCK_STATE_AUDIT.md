# Auditoría de combinaciones de bloques

El usuario informa que, con el último cambio, el arranque tarda 130 segundos y los modelos/texturas se ven iguales dentro del juego. Es una medición del usuario; no se ha ejecutado Minecraft en este entorno. La optimización anterior cambió la representación interna y la selección visual, manteniendo el aspecto.

Inventario estático actual: **116 bloques registrados y 14844 estados Java**. Antes de esta fase eran 17149; antes de optimizar las cinco puertas eran 37981. La optimización ya aplicada se detalla en [COMPACT_GLASS_DOORS.md](COMPACT_GLASS_DOORS.md). Se obtiene multiplicando los dominios de las propiedades añadidas por `createBlockStateDefinition`, incluyendo las heredadas. Estos números incluyen combinaciones que la colocación normal nunca utiliza: Minecraft también las registra. No equivalen al número de modelos JSON, estados utilizados en un mundo ni tiempo empleado por bloque.

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
| school_entrance_pavement_slab | 6 | 1 | 6 |
| street_school_bars | 32 | 1 | 32 |
| Seis school_entrance_pavement_slope | 4 | 6 | 24 |
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

## Optimización de todos los objetivos ≥256 ya aplicada

Los 19 IDs que alcanzaban el umbral recibieron selectores compartidos de Fusion. Pavimento y rejas también perdieron propiedades puramente visuales: el conjunto de pavimento pasa de 1984 a 31 estados y las rejas de 384 a 32. Detalles y verificación: [HIGH_STATE_MODEL_OPTIMIZATION.md](HIGH_STATE_MODEL_OPTIMIZATION.md).

Los dos toldos, su soporte, las escaleras de cristal, el portón, el arco, la puerta doble y la baranda conservan los estados necesarios para geometría, funcionamiento o colisiones. Sus tablas de modelos se compactaron, pero no se trasladaron propiedades físicas a entidades de bloque. Una fase posterior debería medir el coste restante de registro y horneado antes de cambiar esa representación; trasladarla exige comprobar colocación, persistencia, redstone, colisiones y desmontaje.

## Límites de esta auditoría

Es un recuento de estados, no un perfil temporal de Minecraft. Se verificó estáticamente; no se compiló ni ejecutó el juego. Comparar el arranque con la misma configuración y comprobar conexiones, colocación y mundos existentes. Los 130 segundos informados corresponden a la fase anterior a las puertas y a esta optimización.
