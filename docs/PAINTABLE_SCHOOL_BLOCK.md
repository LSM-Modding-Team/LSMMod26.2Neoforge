# Bloque pintable por caras

`school_paintable_block` pertenece a **LSM Mod: School Blocks**. Es un cubo completo, inicialmente de hormigón gris claro sin pintar, sin conexiones ni ticker. Las pinturas reutilizables `light_school`, `dark_school`, `street_school`, `classroom_white` y `classroom_floor_paint`, y el limpiador `paint_cleaner`, están en **LSM Mod: School Items**.

- Clic derecho con una pintura: cambia únicamente la cara apuntada.
- Ctrl izquierdo o derecho + clic derecho sobre un bloque pintado: copia sus seis caras a la pintura de la mano principal (o secundaria si la principal no contiene pintura). No modifica el bloque origen.
- El siguiente clic derecho con esa pintura sobre un bloque pintable aplica las seis caras, incluidas las caras grises. La copia se consume; la pintura vuelve a pintar una cara normalmente.
- Clic derecho con `paint_cleaner`: devuelve las seis caras al gris original.
- Jade sustituye el nombre del bloque por el nombre completo de la pared de la cara apuntada, incluido **Classroom White Wall**. Requiere tener Jade instalado; el mod sigue funcionando sin él.

## Datos, recursos y compatibilidad

El bloque tiene **un solo estado**. Una entidad de bloque guarda seis identificadores enteros estables (`paint_down`, `paint_up`, etc.), los sincroniza con los clientes y solicita reconstruir la geometría después de recibirlos. No revisa vecinos ni calcula formas físicas nuevas. Fusion selecciona seis caras independientes a partir de treinta y seis modelos pequeños; no genera los 46 656 estados posibles. Las texturas son las paredes existentes y `minecraft:block/light_gray_concrete` para el gris inicial.

La copia se guarda en `CUSTOM_DATA` del ítem. El cliente intercepta Ctrl antes del paquete de interacción normal y solo solicita copiar una posición; el servidor comprueba alcance, línea de visión, mano, permisos y datos reales del bloque. No acepta colores proporcionados por el cliente.

El ID visible del antiguo bloque blanco pasa a `classroom_white_wall`. Se conserva `classroom_painted_block` como registro y recursos de compatibilidad, oculto en creativo; la migración de paletas de chunks y estructuras reemplaza ese ID por el nuevo. Los ítems antiguos siguen siendo válidos. No se elimina ningún bloque anterior. El bloque blanco y su pintura usan ahora `classroom_white_wall.png`; se conserva también la textura antigua `classroom_painted_block.png` para las referencias anteriores. La pintura `classroom_floor_paint` reutiliza `classroom_floor.png` y también puede recuperarse con la lija. Su ID de color se añade después de los colores existentes, sin cambiar los datos guardados.

Generadores: `tools/create_classroom_surfaces.py` y `tools/create_paintable_school_block.py`. Verificaciones estáticas: `tools/check_paintable_school_block.py`, `tools/check_optimized_model_resources.py`, y regresiones de puertas/paredes combinadas. No se compiló ni se ejecutó Minecraft por instrucción vigente. Falta comprobar en juego la sincronización, Ctrl, guardado/carga y Jade.
