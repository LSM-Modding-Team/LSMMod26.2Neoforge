# Suelos de patio y slopes elevados blancos

Todos los trece modelos `school_inverted_slope_*_raised_*`, incluidos `raised_extra`, usan `classroom_white_wall` en los slots `wall` y `particle`. Solo cambian esas referencias de material; OBJ, colisiones, altura y estados permanecen intactos. El generador de raised actualiza las referencias y sus extras heredan la textura corregida.

Nuevos cubos completos de un estado en **LSM Mod School Blocks**:

- `white_playground_floor`: tono claro cálido aproximado de la referencia, base #E7E4DA.
- `gray_playground_floor`: gris aproximado de la referencia, base #777672.

Texturas de 16×16 con grano muy discreto de hormigón, modelos nativos cube_all y caída del propio bloque. No son conectables ni tienen entidades.

Pinturas `white_playground_floor_paint` y `gray_playground_floor_paint` en **School Items**. Reutilizan las texturas de los suelos y funcionan en ambos bloques pintables, con copia, limpiador, lija y nombre/icono Jade. Los colores se añaden con IDs 7 y 8, sin alterar los IDs anteriores.

## Guardado y optimización

El color 8 necesita cuatro bits. El bloque personalizable sigue guardando dos longs (16 cuadrados en el primero, ocho en el segundo), ahora con `paint_bits=4`. Al leer guardados antiguos sin ese campo usa tres bits: conserva los colores existentes. La extracción usa desplazamiento sin signo y una máscara acorde al formato, incluido el bit 63 del primer long. Paletas del bloque básico y del portapapeles siguen usando los enteros existentes.

Los pintables mantienen un estado cada uno: 54/432 modelos pequeños de caras, sin combinaciones de estados ni ticker. Verificación estática de 531 441 paletas básicas, 256 máscaras, 10 009 paletas compactas nuevas y paletas antiguas de tres bits, texturas presentes, referencias raised y optimizador correcta. Sin compilar Java/Gradle ni iniciar Minecraft.

Generación de suelos/pinturas: `python tools/create_playground_floors.py`. Los generadores de pintables conservan las dos pinturas nuevas.
