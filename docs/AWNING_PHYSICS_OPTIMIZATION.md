# Primera optimización de formas físicas: toldos

La grabación JFR del usuario contiene 71 segundos, 4378 muestras Java y 3333 con métodos de formas físicas (76 %). Fusion aparece en 120 (2,7 %). Son proporciones de muestras, no tiempos exactos. Las operaciones de formas se concentran durante unos 53 segundos. Las pilas suelen mostrar solo cinco niveles: aparecen consultas de AwningBlock, pero no permiten atribuirle toda la demora.

## Cambio

AwningBlock construía hasta ocho cajas de tela, unidas con Shapes.or en cada consulta. Ese método también proporcionaba la colisión heredada. Ahora AwningShapes construye directamente una cuadrícula discreta con las mismas coordenadas y celdas ocupadas, sin Shapes.or, join ni optimize. El mayor perfil utiliza solo 120 celdas de cuadrícula. No se aproximan alturas ni se cambian los ocho segmentos originales.

El resultado se guarda por start/end/upper/facing y se comparte entre ambos tipos de toldo, sin referencias al mundo. Hay como máximo 2312 claves físicas y sus contornos. La orientación y la celda superior siguen distinguidas. La colisión y la forma de oclusión conservan el volumen original. El contorno de selección utiliza una caja envolvente, como el resto del mobiliario: el resaltado ya no recorre cada segmento.

El soporte ya disponía de dos formas estáticas compartidas por eje, por lo que no se modificó. No se cambiaron estados, modelos, texturas, colocación, entidades de bloque ni lógica de desmontaje. Los generadores existentes solo escriben recursos y siguen siendo compatibles.

## Validación y límites

`python tools/check_awning_physics.py` compara ocupación y volumen de la cuadrícula con la unión de las cajas originales para los 2312 perfiles físicos, incluyendo cuatro orientaciones, extremos 0–16 y ambas celdas. `check_awning_assets.py` conserva sus verificaciones de los 4624 estados de los dos toldos y los soportes. Las firmas de VoxelShape, BitSetDiscreteVoxelShape y getOcclusionShape se inspeccionaron en el JAR de Minecraft 26.2.

No se compiló ni ejecutó Minecraft conforme a la instrucción vigente. Por ese motivo tampoco se ejecutó checkFurnitureOutlines, que depende de la compilación; el contorno se verificó estáticamente. La prueba Python comprueba el algoritmo matemático y no sustituye la ejecución Java. La siguiente JFR debe confirmar cuánto disminuye la carga de formas y si queda otro bloque dominante. También comprobar dentro del juego la colisión, la selección y las dos celdas de los toldos.
