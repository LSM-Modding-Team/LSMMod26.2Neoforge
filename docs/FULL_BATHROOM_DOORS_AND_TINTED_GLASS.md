# Puertas completas de baño y cristal opaco

- `tall_bathroom_door_full_3`: tres bloques de hoja móvil de madera, sin claraboya fija.
- `tall_bathroom_door_full_4`: cuatro bloques de hoja móvil de madera, sin claraboya fija.

Ambas usan las texturas existentes de `bathroom_door`: mantienen la parte inferior y la manija; la madera superior cubre todo el resto de la altura mediante bandas UV continuas. Un ítem coloca la puerta completa. Clic derecho en cualquier fila abre o cierra todas las filas; la señal de redstone en cualquier fila controla toda la puerta. Bisagras izquierda/derecha, cuatro orientaciones, comprobación de espacio/permisos y apoyo, eliminación conjunta con un único drop. La puerta alta anterior se conserva. La propiedad ROW compartida permite valores 0–3; la versión de tres bloques únicamente coloca 0–2.

`school_tinted_glass` usa exactamente la geometría del `school_glass` independiente, de ocho píxeles de profundidad, con marco marrón y cristal gris oscuro. Su textura tiene alfa 255 en todos los píxeles y usa renderizado sólido; todas las caras conservan su textura opaca. Solo tiene la propiedad FACING: sin conexión horizontal/vertical, sin eliminación de marcos ni separadores compartidos y sin integración con el sistema de cortinas.

Verificación: `python tools/check_full_bathroom_doors_and_tinted_glass.py`. Comprueba todos los estados, hoja completa móvil en cada fila, ambas bisagras y orientaciones, continuidad UV, opacidad, geometría de cristal y un solo drop de puerta. Contornos simples separados de la colisión. No se ejecuta compilación ni pruebas dentro de Minecraft por instrucción del usuario; `checkFurnitureOutlines` requiere ejecutar Java mediante Gradle y queda pendiente.
