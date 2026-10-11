# Suelo de baldosas con franja de parquet

`tiled_parquet_floor`: cubo completo en **LSM Mod School Blocks**, con 12 píxeles de `classroom_floor` y una franja de 4 píxeles de `school_parquet` en la cara superior, siguiendo la distribución de la referencia. La base y las cuatro caras laterales conservan las baldosas. `tiled_floor` en la petición se confirmó como `classroom_floor`.

Reutiliza ambas texturas sin recolorearlas ni estirarlas: las UV superiores corresponden a [0,0,12,16] y [12,0,16,16]. La franja queda inicialmente a la derecha de la dirección de mirada del jugador al colocar el bloque. Tiene cuatro orientaciones horizontales, admite rotación/espejo de estructuras y no es conectable.

Modelo nativo de tres elementos y siete regiones de superficie: las dos partes superiores comparten un borde, sin caras superpuestas ni planos internos. Colisión y selección son las de un cubo estándar. No introduce entidades, ticker ni formas físicas detalladas; cuatro estados y un modelo compartido, compatible con el optimizador existente. No necesita un modelo Fusion propio.

Generador: `python tools/create_tiled_parquet_floor.py`. Verificación estática de todas las regiones de superficie, UV, orientaciones, referencias, ítem y loot mediante `python tools/check_tiled_parquet_floor.py`; regresión de recursos optimizados y regeneración idempotente. No se compiló Java/Gradle ni se inició Minecraft.
