# Lavadero continuo

`school_connected_sink`, en **LSM Mod School Functional Blocks**, es un lavadero decorativo basado en la referencia: respaldo alto, dos paredes laterales, taza abierta y frente bajo sobre una base posterior. Tiene 1,5 bloques de altura como el lavadero anterior, y ocupa dos celdas verticales. Usa directamente `bathroom_floor_side`, el gris original de `bathroom_floor`, sin texturas nuevas. El lavadero de pedestal `school_sink` se conserva.

Colocar piezas una junto a otra con la misma orientación forma un tramo de ancho extensible. Los laterales interiores desaparecen tanto en el modelo como en la colisión, quedando únicamente los extremos externos y una taza continua. Piezas giradas o mitades diferentes no conectan. Al retirar una pieza, los laterales de sus vecinos vuelven a cerrarse.

## Optimización

Ocho estados: cuatro orientaciones y dos mitades; ninguna propiedad de conexión. El código consulta los dos vecinos laterales para obtener una máscara de dos bits. Modelo Fusion con cuatro posibilidades por mitad y un respaldo cerrado para ítems/contextos sin mundo. Las superficies de contacto entre piezas alineadas se ocultan en los límites de celda.

Ocho geometrías base (mitad × máscara), con cuatro rotaciones físicas precalculadas, y sus contornos exteriores simples. No se construyen ni unen formas por consulta. El contorno depende de los vecinos, por lo que se guarda por mitad/máscara/rotación y no en la caché global por BlockState. `dynamicShape()` evita que Minecraft congele una forma cerrada cuando faltan datos del mundo.

Sin entidad, inventario, función de agua ni ticker permanente. Colocación/desmontaje de las dos mitades reutilizados; un ítem por pareja y cero en creativo. No hay límite de anchura del tramo.

## Validación

`create_school_connected_sink.py` genera geometría, formas físicas, modelos, registros y recursos de manera idempotente. `check_school_connected_sink.py` verifica cuatro máscaras/dos mitades, taza hueca, desaparición de tabiques para tramos de una a cinco piezas, ocho estados, altura de 24 píxeles y caída única. Regresión del optimizador correcta. No se compiló Java/Gradle ni se inició Minecraft; las conexiones deben comprobarse visualmente dentro del juego.
