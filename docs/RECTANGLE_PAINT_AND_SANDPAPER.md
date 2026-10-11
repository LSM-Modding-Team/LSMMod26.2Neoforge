# Pintura y lija por rectángulos

Con cualquier pintura o `school_sandpaper`:

1. Shift + clic derecho en una cara pintable marca la primera esquina.
2. Otro clic derecho en la esquina opuesta completa el rectángulo, con o sin Shift.
3. Shift + clic derecho al aire cancela una selección pendiente.

Ambos extremos deben estar en la misma dimensión, cara y plano. Funciona sobre cualquiera de las seis caras, incluidos suelos/techos, y sobre superficies interiores expuestas al retirar medios cubos. En el bloque básico se pinta su cara completa; en el personalizable se incluyen únicamente los cuadrados de la selección. Se pueden combinar ambos tipos en una misma superficie. Los extremos son inclusivos y un rectángulo de una cara/cuadrado también es válido.

La lija limpia cada cara o cuadrado pintado y devuelve su pintura correspondiente, igual que el uso individual. Las pinturas van al inventario; si no caben, caen como objetos. Los cuadrados sin pintura no producen objetos. Las piezas retiradas o cubiertas no se pintan ni se lijan. Los bloques que no sean pintables se omiten, sin reemplazarlos.

## Comportamiento y límites

- Hasta **256 bloques** por rectángulo (pueden contener hasta cuatro cuadrados por cara).
- Se valida la zona cargada y los permisos de los bloques pintables antes de modificar nada. Si falla la validación, la selección permanece para elegir otro extremo o cancelarla.
- Selección guardada en el CUSTOM_DATA de la herramienta, asociada al jugador/dimensión/cara/plano. El servidor valida los clics y limita el alcance del trabajo a la región seleccionada. Sin nuevos paquetes, entidades ni estados de bloque.
- Una paleta/sincronización por bloque cambiado, sin repintar cuatro veces un bloque completo ni enviar actualizaciones separadas por cada cuadrado.
- Las demás caras se conservan. La selección utiliza coordenadas de medio bloque con división y módulo correctos para posiciones negativas.
- El clic ordinario sin selección mantiene la pintura/lija individual. Ctrl+copia cancela la selección de esa pintura y conserva el comportamiento de paleta copiada; pintar por rectángulo utiliza el color de la herramienta, sin aplicar la paleta copiada a todas las caras.

`RectanglePaintTool` centraliza ambos usos y la lija comparte su recuperación de colores entre operaciones individuales y rectangulares. Los mensajes están traducidos a español e inglés.

## Verificación

`check_rectangle_paint_tools.py`: 12 288 casos de superficies/ocupación/ejes/coordenadas negativas, selección inclusiva, huecos, permisos y aplicación por paleta. `check_paintable_school_block.py` y `check_custom_paintable_blocks.py`: paletas completas y compactas, máscaras de ocupación y compatibilidad de guardados. Firmas de API de mensajes/NBT comprobadas con javap. No se compiló Java/Gradle ni se abrió Minecraft: la interacción real de dos clics debe comprobarse dentro del juego.
