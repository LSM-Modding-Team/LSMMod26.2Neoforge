# Bloque y pintura amarillos

`yellow_school_block`: cubo completo en **LSM Mod School Blocks**, textura 16×16 RGB uniforme **#FFD300 (255, 211, 0)**, exactamente el amarillo de los bordes de `school_entrance_pavement`. Modelo nativo `cube_all`, un estado, colisión cúbica estándar y caída del propio bloque. No necesita conexiones, entidades ni modelo Fusion propio.

`yellow_school_paint`: pintura reutilizable en **LSM Mod School Items**. Aplica ese material a la cara o cuadrado apuntado, siguiendo `SchoolPaintItem`. Compatible con los dos bloques pintables, Ctrl+copia de seis caras/24 cuadrados, limpiador y lija. La lija recupera esta pintura; Jade muestra el nombre y el icono del bloque amarillo.

El color `YELLOW` se añade al final de `SchoolPaint` como ID 6: IDs 0–5 conservados. Cabe en el empaquetado existente de tres bits. Los bloques pintables siguen teniendo un estado cada uno y 42/336 modelos pequeños de cara; no se generan combinaciones de paletas. El optimizador existente comparte recursos idénticos.

Generación reproducible: `python tools/create_yellow_school_block.py`, que actualiza los dos conjuntos de caras pintables. Regenerar los pintables por sus puntos de entrada originales también conserva el color amarillo.

Validación estática: amarillo idéntico al borde, registros/modelos/texturas/loot/traducciones, orden estable de IDs, lija/Jade/tabs, 117 649 paletas de seis caras, 256 máscaras y 10 007 paletas empaquetadas de 24 cuadrados; regresión del optimizador e idempotencia. No se compiló Java/Gradle ni se inició Minecraft.
