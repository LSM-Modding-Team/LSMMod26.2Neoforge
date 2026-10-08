# Tamaños de los NPCs — base 0dcc785

`Width` y `Height` son enteros universales de Student y Teacher, independientes, de **1 a 16**. Ausencia de campo = **14**, tamaño del jugador. Valores fuera del rango se limitan a 1/16; se guardan y sincronizan al cliente. No forman parte de las cinco estadísticas de combate ni modifican gender o StudentData. No se sortean ni se asignan variantes nuevas de textura.

## Modelos y placeholders

Las 256 combinaciones (16 anchos × 16 altos) se producen escalando el mesh ya existente durante render, en vez de duplicar 256 capas y PNG. Se conservan modelo masculino, modelo girl.java, busto, caminar, polos, casacas, M/F y numerales. Las texturas placeholder de 0dcc785 sirven para todas las opciones y conservan exactamente sus UV/píxeles. El transform engloba también las capas de items y armadura. Width afecta X/Z; Height afecta Y. Los pies conservan el plano del suelo y la sombra sigue el ancho.

Los límites visuales se miden con el modelo en reposo y sus caras base: de extremo de brazo a extremo de brazo, y de pies a cabeza. Cada modelo actual tiene 16 unidades de ancho y 32 de alto; 16 unidades = un bloque. El nivel 1 usa escala 0.5 (referencia de tamaño pequeño tipo bebé, sin copiar sus proporciones de cabeza). El nivel 16 usa escala 1: brazos hasta un bloque y cabeza a dos bloques. El nivel 14 usa 0.9375, igual que AvatarRenderer vanilla para el jugador: ancho visible 0.9375 y altura visible 1.875 bloques. Overlays de una skin futura, movimientos de extremidades o modificadores externos de SCALE pueden sobrepasar la silueta de reposo.

La colisión no mide el ancho de brazos: como en vanilla, se centra en el cuerpo. En el mínimo reproduce 0.3×0.975, referencia de la caja de bebé zombie/aldeano; en 14 usa 0.6×1.8, igual al jugador; en 16 usa 0.64×2.0. También se ajustan altura de ojos y attachments al tamaño. `LivingEntity.getDimensions` es final en 26.2: se modifica `getDefaultDimensions`, conservando la aplicación de SCALE vanilla. Al cambiar datos se recalcula la caja y se detiene el camino anterior para recalcular navegación. Aumentar tamaño contra obstáculos puede hacer que el motor ajuste la posición.

## Opciones por eje

Interpolación por tramos: 1→14 entre mínimo y jugador, 14→16 entre jugador y máximo. Esto garantiza una opción exactamente normal. Para cada columna se aplica el nivel del NBT correspondiente; no es necesario que coincidan.

| Nivel | Escala Width X/Z | Escala Height Y | Ancho visual (bloques) | Alto visual (bloques) | Colisión Width (bloques) | Colisión Height (bloques) |
|---:|---:|---:|---:|---:|---:|---:|
| 1 | 0.500000 | 0.500000 | 0.500000 | 1.000000 | 0.300000 | 0.975000 |
| 2 | 0.533654 | 0.533654 | 0.533654 | 1.067308 | 0.323077 | 1.038462 |
| 3 | 0.567308 | 0.567308 | 0.567308 | 1.134615 | 0.346154 | 1.101923 |
| 4 | 0.600962 | 0.600962 | 0.600962 | 1.201923 | 0.369231 | 1.165385 |
| 5 | 0.634615 | 0.634615 | 0.634615 | 1.269231 | 0.392308 | 1.228846 |
| 6 | 0.668269 | 0.668269 | 0.668269 | 1.336538 | 0.415385 | 1.292308 |
| 7 | 0.701923 | 0.701923 | 0.701923 | 1.403846 | 0.438462 | 1.355769 |
| 8 | 0.735577 | 0.735577 | 0.735577 | 1.471154 | 0.461538 | 1.419231 |
| 9 | 0.769231 | 0.769231 | 0.769231 | 1.538462 | 0.484615 | 1.482692 |
| 10 | 0.802885 | 0.802885 | 0.802885 | 1.605769 | 0.507692 | 1.546154 |
| 11 | 0.836538 | 0.836538 | 0.836538 | 1.673077 | 0.530769 | 1.609615 |
| 12 | 0.870192 | 0.870192 | 0.870192 | 1.740385 | 0.553846 | 1.673077 |
| 13 | 0.903846 | 0.903846 | 0.903846 | 1.807692 | 0.576923 | 1.736538 |
| 14 | 0.937500 | 0.937500 | 0.937500 | 1.875000 | 0.600000 | 1.800000 |
| 15 | 0.968750 | 0.968750 | 0.968750 | 1.937500 | 0.620000 | 1.900000 |
| 16 | 1.000000 | 1.000000 | 1.000000 | 2.000000 | 0.640000 | 2.000000 |

## Uso

```mcfunction
/summon lsmmod:student ~ ~ ~ {StudentData:{Level:"primary",Grade:1},gender:"female",Width:1,Height:1}
/summon lsmmod:student ~ ~ ~ {StudentData:{Level:"secondary",Grade:5},Width:16,Height:16}
/summon lsmmod:teacher ~ ~ ~ {Width:14,Height:14}
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {Width:16,Height:1}
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {Width:14,Height:14}
```

Huevos normales y /summon sin estos campos usan **14/14**, incluidos los once salones. Un huevo personalizado o un /summon con NBT explícito sí puede elegir tamaño. Las asignaciones aleatorias existentes de género/stats continúan como antes; Width/Height no participan en ellas. Las partidas antiguas sin campos migran a 14/14, sin elegir tamaños aleatorios.

## Validación

Revisión estática de API 26.2, 16 niveles únicos/monótonos, extremos y opción 14, las 256 combinaciones, límites válidos de ojos, persistencia/sincronización y conservación de registros de huevos y assets. No se compila ni se inicia Minecraft.

Pendiente en juego: Student male/female y Teacher en 1/1, 14/14, 16/16 y mezclas 1/16–16/1; comparar en reposo con bloques y jugador; colisiones, ojos, equipo y pasos bajo techos; editar tamaño y guardar/cargar; verificar que huevos e invocaciones normales quedan en 14/14. No se añaden variantes artísticas en esta entrega.
