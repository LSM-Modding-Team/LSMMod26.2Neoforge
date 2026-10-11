# Cimientos y bloque pintable personalizable

En **LSM Mod: School Blocks**:

- `school_foundation`: cubo de hormigón gris oscuro con textura discreta 16×16, resistencia 3/8 y extracción con picota.
- `school_paintable_custom_block`: cubo dividido en ocho partes de 8×8×8 píxeles. Sus seis caras tienen cuatro cuadrados pintables cada una, para un total de 24 colores independientes.

En **LSM Mod: School Items**:

- `school_sandpaper`: clic derecho recupera un ítem del color apuntado y deja esa cara o cuadrado gris. Funciona con ambos bloques pintables. No entrega pintura si el área ya es gris; si el inventario está lleno, deja el ítem a los pies del jugador. Es reutilizable.
- `school_hammer`: clic derecho sobre el nuevo bloque retira únicamente el medio cubo señalado. Los huecos afectan a la colisión y permiten apuntar a las superficies interiores. Al retirar la última parte se elimina el bloque; el martillo no devuelve material. Es reutilizable.

Las cinco pinturas existentes (incluida `classroom_floor_paint`) pintan un cuadrado del bloque nuevo por clic. El limpiador restaura los 24 colores sin restaurar los cubos retirados. Las caras interiores expuestas reutilizan el cuadrado de esa dirección y columna: también se pintan y lijan. Nunca hay dos superficies expuestas de la misma dirección/columna, así que conservamos exactamente 24 colores.

Ctrl + clic derecho copia los 24 colores; el siguiente clic los aplica juntos. No copia los recortes. La copia anterior de seis caras continúa funcionando: aplicada al nuevo bloque, llena los cuatro cuadrados de cada cara. Si una copia de 24 cuadrados se aplica al bloque original, usa el cuadrado 0 de cada cara (las coordenadas menores de sus dos ejes). Jade identifica el color del cuadrado apuntado, incluidas las caras interiores.

## Rendimiento y compatibilidad

Ambos bloques tienen **un solo estado**. La entidad nueva no tiene ticker: guarda la ocupación en ocho bits y los 24 colores en dos `long` de tres bits por color. La sincronización y la actualización de geometría ocurren al cambiar datos. La enumeración de pinturas conserva sus identificadores anteriores y reutiliza una tabla estática, evitando crear arrays al consultar colores durante el renderizado.

Fusion usa 288 modelos pequeños (ocho cubos × seis direcciones × seis colores, contando el gris sin pintar), todos de una sola cara y con UV que conservan los cuadrantes de las texturas existentes. Solo dibuja las superficies expuestas de las partes presentes; no se hornean combinaciones de colores/recortes. Un modelo vacío adicional aporta la partícula gris de respaldo sin dibujar caras ocultas. No hay conexiones con vecinos ni colores en el estado. Las formas físicas se construyen directamente en la cuadrícula y se comparten por máscara, de forma diferida; el cubo completo reutiliza `Shapes.block()`. Se necesitan contornos de las ocho celdas para que los clics atraviesen los huecos y alcancen superficies reales, en lugar de seleccionar espacio vacío. Son contornos simples de medios cubos, sin detalles adicionales.

El bloque original conserva sus seis colores guardados y los portapapeles antiguos siguen siendo válidos. Los permisos de interacción y el procesamiento en servidor se conservan para pintura, copia, lija y martillo. No se elimina ningún bloque existente.

Generador: `tools/create_custom_paintable_blocks.py`. Regresión: `tools/check_custom_paintable_blocks.py`, además de las comprobaciones de pintura original, recursos, puertas y paredes combinadas. Comprobación estática de las 256 máscaras, 288 modelos, superficies y planos de clic, y 10 006 paletas empaquetadas. No se compiló ni se ejecutó Minecraft o `checkFurnitureOutlines`, siguiendo la instrucción vigente. Falta probar edición, guardado/carga, sincronización entre jugadores y Jade en el juego.
