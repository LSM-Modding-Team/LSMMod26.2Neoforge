# Pintura de aula y césped sintético

11 de octubre de 2026 (America/Lima). classroom_white_wall es un cubo opaco
con textura gris muy clara #EDEDED y variación de un nivel por canal.
school_synthetic_grass usa verde oscuro #537E3E y fibras cortas discretas; la referencia
está detrás de vidrio, por lo que no se reproduce su filtro ni transparencia.
No tiene tierra, variación por bioma ni conexiones. Ambas texturas son RGB 16×16
y cubren las seis caras. Ambos bloques aparecen en School Blocks.

Se usan Block y cube_all nativos, un estado por bloque, geometría/colisión
completa reutilizada. La preparación de recursos existente conserva sus modelos;
no hace falta Fusion dinámico ni cachés adicionales. La pintura tiene dureza 1,8,
resistencia 6, sonido de piedra y requiere herramienta adecuada (picota). El
césped tiene dureza 0,8 y sonido de musgo (`SoundType.MOSS`). Ambos dejan un drop de sí mismos.

El modelo blanco usa la textura explícita `classroom_white_wall.png`; el ID y la textura
`classroom_painted_block` se conservan por compatibilidad. La pintura blanca usa la
misma textura canónica.

create_classroom_surfaces.py genera las texturas y recursos/registro de forma
reproducible. Comprobación estática correcta de colores/opacidad, seis caras,
estados, ítems, drops y tab; regresiones de modelos y puertas correctas.
Los controles previos ya no fijan el total de estados de todo el mod, permitiendo
nuevos bloques, y mantienen los checks de las familias que verifican.
Total actual: 137 bloques / 15090 estados. Sin compilar ni ejecutar Minecraft.
