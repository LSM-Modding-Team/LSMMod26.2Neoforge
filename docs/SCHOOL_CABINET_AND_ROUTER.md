# Armario cerrado y router

`school_cabinet`: mueble decorativo de **2 bloques de ancho × 2 de alto × 1 de fondo**, con dos puertas siempre cerradas, acabado marrón oscuro, junta central, tiradores y pequeña placa como en la referencia parcial. La textura frontal de 32×32 se reparte en cuatro piezas con UV continuas; laterales y tapa usan madera oscura de 16×16.

Una colocación genera el conjunto completo tras comprobar las cuatro celdas, ambas bases, permisos, colisiones y límites del mundo. Romper cualquier pieza desmonta todo y entrega una unidad en supervivencia. En creativo no se duplica. Tiene 16 estados (dirección, mitad, columna); sin inventario, interacción de puerta, entidad de bloque ni propiedad OPEN.

La colisión es cúbica en cada celda: las dos piezas superiores llegan a y=16 local y proporcionan una superficie firme para colocar otros bloques. No necesita un ítem especial ni interacción para habilitar la tapa.

`school_router`: bloque independiente y decorativo, con carcasa negra, rejillas, indicadores dibujados y dos antenas posteriores. Una celda y cuatro orientaciones. Se coloca sobre el armario o cualquier superficie firme; si desaparece su soporte, cae como ítem. No tiene función de red, luz dinámica, inventario ni entidad.

Ambos aparecen en **LSM Mod School Functional Blocks**.

## Rendimiento y compatibilidad

El armario reutiliza colocación/desmontaje de `WideBathroomBlock`; solo añade la mitad vertical. La inicialización de HALF y OPEN se independiza para admitir muebles altos sin puerta interactiva, conservando los estados de los sanitarios. Dentro de cada constructor, celdas con idénticas cajas comparten la cuadrícula y las cuatro rotaciones; no se recalculan formas por consulta. El router usa tres cajas en una cuadrícula estática y cuatro rotaciones compartidas. Selección sencilla mediante `SimpleBlockOutline.forState`.

Siete modelos nativos pequeños, seis texturas y ninguna combinación adicional de estados de soporte/conexión. El optimizador existente procesa los cubos opacos y conserva geometría, UV y referencias. Sin ticker permanente ni uniones sucesivas de formas.

## Validación

Generador `tools/create_school_cabinet_and_router.py`, idempotente. `check_school_cabinet_and_router.py`: 32×32×16 píxeles, cuatro celdas y orientaciones, cobertura exacta de la textura frontal, superficie sólida, colisión del router, referencias y loot. Regresiones `check_wide_bathroom_assets.py` y `check_optimized_model_resources.py` correctas.

No se compiló Java/Gradle ni se abrió Minecraft; la colocación y el renderizado deben comprobarse dentro del juego.
