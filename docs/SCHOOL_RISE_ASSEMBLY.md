# Escalera uniforme y pendientes reutilizables

Este diseño sustituye completamente el kit numerado de 7×5. La medida exacta deja de ser obligatoria: se prioriza repetir la misma escalera. Cada pieza se coloca y rompe de forma independiente.

| Bloque | Diseño |
| --- | --- |
| `classroom_floor_stairs` | Una escalera vanilla, dos escalones de 8 px, textura original `classroom_floor`. |
| `school_slope` | Cuña lisa a 45°, textura original `light_school_wall`. |
| `school_slope_raised_base` | Parte inferior del borde elevado, limitada a una celda. |
| `school_slope_raised_tip` | Remate de ese borde, colocado una celda encima de la base. |

La escalera continúa en toda la anchura que decidas, incluida la columna central de tres bloques. No se crean huecos ni conjuntos automáticos. Repite el mismo ítem en cada posición; para un tramo ascendente, cada columna sucesiva queda un bloque más alta y un bloque más atrás. La escalera conserva esquinas vanilla, posición superior/inferior, giro y agua.

Las tres pendientes siguen la dirección del jugador. Haz clic en la cara inferior o en la mitad superior de una cara lateral para colocarlas invertidas; en la cara superior o mitad inferior del lateral para colocarlas normales. Admiten agua, giro y espejo; sus pendientes son rectas, sin esquinas automáticas. Los modelos OBJ tienen superficies planas sin escalones visuales; la colisión aproxima el plano en bandas de medio píxel.

Para un borde elevado continuo, coloca `school_slope_raised_base` y encima `school_slope_raised_tip`, con igual orientación. Repite el par avanzando un bloque y subiendo otro. El plano queda 10 px por encima de la pendiente normal y entre 2 y 10 px por encima de las huellas de la escalera. Puedes repetirlo a lo ancho, incluida una franja de tres bloques, y usar las posiciones invertidas debajo del tramo. Cada pieza y su colisión permanecen dentro de su propia celda.

## Aplicar la entrega

Extrae el ZIP sobre la raíz del proyecto y ejecuta `python tools/remove_previous_school_rise.py`. El script elimina exclusivamente los archivos antiguos enumerados en `ARCHIVOS_ELIMINADOS.txt`; puede ejecutarse varias veces. Los registros, traducciones y tags ya contienen los cuatro bloques nuevos. Retira las piezas numeradas antiguas en los mundos antes de actualizar, porque esos identificadores se eliminan.

Generación: `python tools/create_school_rise_assets.py`. Verificación estática: `python tools/check_school_rise_assets.py`. Vista previa externa: `python tools/render_school_rise.py`.

No se ha compilado, hecho commits ni publicado. La prueba visual y de colocación en Minecraft queda pendiente.
