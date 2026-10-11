# Armario de pared y pendiente amarilla

`school_cabinet` se coloca únicamente al hacer clic en una cara horizontal de pared. Reutiliza el modo de soporte de pared de `WideBathroomBlock`: las cuatro celdas deben tener pared firme detrás. Conserva sus 2×2×1 bloques, puertas cerradas, tapa sólida, estados y desmontaje conjunto. Los armarios existentes deben tener ese respaldo; al recibir actualizaciones se desmontan si falta soporte.

Cuatro bloques nuevos en **LSM Mod School Blocks**:

| Bloque | Inicio alto | Final bajo |
|---|---:|---:|
| yellow_school_slope_1 | 8 px | 6 px |
| yellow_school_slope_2 | 6 px | 4 px |
| yellow_school_slope_3 | 4 px | 2 px |
| yellow_school_slope_4 | 2 px | 0 px |

Colocar 1→2→3→4 en el mismo nivel, avanzando en la dirección `facing`, forma un descenso de ocho píxeles en cuatro bloques de largo. Se orientan según la mirada del jugador al colocarlos; disponen de cuatro orientaciones y no admiten inversión. Toda la superficie usa exclusivamente `yellow_school_block` (#FFD300), sin bordes ni nuevas texturas.

## Movimiento y recursos

Cuatro modelos OBJ simples y una MTL con el slot `yellow` explícito, compatibles con NeoForge. Cuatro estados por bloque; sin conexiones, entidades ni ticker. Colisión construida una sola vez en cuadrícula con 32 franjas por bloque y rotaciones compartidas, como la entrada. Contorno de selección sencillo mediante `SimpleBlockOutline.forState`.

El perfil de colisión conservador se eleva sobre el plano visible como máximo **0,00390625 bloques (0,0625 px)**. La entrada mantiene sus 32 franjas y como máximo 0,0078125 bloques (0,125 px) de aproximación. Estos incrementos quedan muy por debajo de la subida normal de paso de 0,6 bloques. Las franjas permiten subir y bajar sin exigir saltos entre cada subdivisión.

`check_walkable_school_slopes.py` comprueba el recorrido con una huella de jugador de 0,6 bloques, superficies planas en ambos extremos y pasos de 1/64 de bloque, en ambos sentidos. Delta máximo entre contactos: 0,00390625 en amarillos y 0,0078125 en entrada. Se comprueban las uniones de altura de las seis piezas de entrada, incluidas las piezas colocadas un nivel más arriba, y las cuatro amarillas. Las rotaciones preservan el perfil.

También pasaron `check_school_cabinet_and_router.py`, `check_entrance_pavement_shapes.py` y `check_optimized_model_resources.py`, y el generador `create_yellow_school_slopes.py` es idempotente. No se modificaron las formas de las pendientes de entrada porque ya cumplen el criterio geométrico de paso. Otros slopes del mod conservan su función decorativa.

**Límite de verificación:** se verificaron perfiles, continuidad, incrementos y recursos de manera estática. No se compiló Java/Gradle ni se abrió Minecraft; caminar sobre los bloques dentro del juego sigue pendiente de comprobar.
