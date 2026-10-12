# Suelos verde y naranja del patio

`green_playground_floor` y `orange_playground_floor` son cubos completos en **LSM Mod School Blocks**, con sus pinturas `green_playground_floor_paint` y `orange_playground_floor_paint` en **School Items**.

Las referencias muestran iluminación directa y sombra sobre el mismo material. Cada textura usa un único tono base, sin dibujar mitades claras/oscuras ni emitir luz. La iluminación normal y el sombreado de Minecraft producen la diferencia de brillo.

Tonos base aproximados de referencia: verde **#618D35** y naranja cálido **#D3A34A**. Texturas RGB 16×16 con grano discreto de ±3 unidades por canal, el mismo acabado de los suelos claros/grises existentes. Los colores y texturas anteriores se conservan.

## Pintura y optimización

IDs estables 9 (`PLAYGROUND_GREEN`) y 10 (`PLAYGROUND_ORANGE`), añadidos después de los colores existentes. Caben en el formato vigente de cuatro bits: no cambia el guardado ni requiere otra migración. Compatibles con ambos bloques pintables, pintura/lija rectangular, Ctrl+copia, limpiador, recuperación de pinturas y nombre/icono Jade.

Cada suelo tiene un estado y un modelo nativo cube_all, colisión estándar y loot. Sin entidades, conexiones o ticker. Los pintables siguen teniendo un estado cada uno, con 66/528 caras pequeñas; no se generan variantes por combinación de paleta. El optimizador existente comparte sus geometrías.

Generación mediante `tools/create_playground_floors.py`. Regeneración idempotente y referencias/colores comprobados, sin diferencias artificiales de tono entre mitades. Verificaciones estáticas correctas: 1 771 561 paletas básicas, 256 máscaras y 10 011 paletas compactas, selección rectangular y optimizador. No se compiló Java/Gradle ni se inició Minecraft.
