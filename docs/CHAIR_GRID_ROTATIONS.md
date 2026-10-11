# Rotación y reutilización de las colisiones de sillas

La última JFR del usuario permite localizar el cuello de botella: 4225 de 5621 muestras Java (75,2 %) contienen ChairBlock.rotateClockwise/buildRotatedShapes durante el registro. El método anterior descomponía una forma en cajas y llamaba a Shapes.or por cada caja, repitiendo las uniones y optimizaciones para las tres orientaciones adicionales de cada bloque.

ChairBlock ahora utiliza SharedChairRotations. Esta clase llama a Shapes.rotate con BLOCK_ROT_Y_90, BLOCK_ROT_Y_180 y BLOCK_ROT_Y_270 desde la forma nativa. La implementación vanilla de Minecraft 26.2 rota DiscreteVoxelShape y sus coordenadas, sin Shapes.or ni optimize. El centro de giro es (0.5,0.5,0.5); la transformación horizontal es (x,z)→(1-z,x). El orden sigue siendo SOUTH/WEST/NORTH/EAST, correspondiente a get2DDataValue.

La caché tiene un acceso rápido por identidad de la forma original y otro por lista inmutable de cajas ordenadas. Esto comparte las rotaciones entre sillas de diferentes texturas que usan la misma forma, y entre instancias independientes con la misma descomposición exacta en cajas. No busca equivalencia mediante uniones de formas, no redondea coordenadas ni llena huecos. Cada bloque recibe una copia de la pequeña matriz de cuatro referencias; los objetos de forma se comparten. La caché no contiene mundos ni estados y solo se utiliza al construir bloques.

Las cajas de colisión originales, el contorno simple, estados, modelos, texturas, altura del asiento, orientación y lógica de sentarse quedan conservados. StoolBlock hereda la misma mejora. Los generadores existentes no reescriben estos métodos Java.

## Verificación

- `python tools/check_chair_grid_rotations.py`: siete perfiles declarados, 28 comprobaciones de giro, correspondencia exacta de celdas ocupadas y regreso a la forma original tras cuatro giros, incluidos huecos y coordenadas no uniformes.
- API y bytecode inspeccionados en Minecraft 26.2: Shapes.rotate llama directamente a DiscreteVoxelShape.rotate; BLOCK_ROT_Y_90 corresponde a ROT_90_Y_NEG.
- Se amplió checkFurnitureOutlines con comprobaciones Java de equivalencia de volumen frente a las rotaciones antiguas, forma vacía, cubo, huecos, volumen fuera del bloque y reutilización por identidad/descomposición. Este check depende de compilación y no se ejecutó, respetando la prohibición vigente de compilar. La comprobación Python no sustituye su ejecución Java.

No se ejecutó Minecraft. La nueva JFR deberá mostrar cuánto disminuye este trabajo y qué domina después; los porcentajes de muestras no prometen una reducción proporcional de segundos.
