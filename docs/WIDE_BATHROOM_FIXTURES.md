# Sanitarios de dos bloques de ancho

`toilet`, `adult_men_urinal` y `men_urinal` conservan sus identificadores y pasan a ocupar dos celdas horizontales. El inodoro con cubículo y el urinario adulto mantienen dos bloques de altura; el urinario infantil mantiene uno. Una colocación genera todas las piezas tras validar espacio, colisión, permisos, soporte, altura y borde del mundo. Los urinarios siguen sujetos a la pared y el inodoro al suelo. Romper cualquier pieza desmonta el conjunto y entrega una unidad en supervivencia; creativo no duplica objetos.

El cubículo conserva el clic para abrir/cerrar. Dos hojas se recogen contra las paredes dentro de la profundidad existente; las cuatro celdas cambian juntas después de validar permisos y colisiones. El ancho local X pasa de 16 a 32 píxeles; la profundidad y la altura permanecen iguales.

`standalone_toilet` reutiliza únicamente taza, asiento, pedestal, cisterna y manija del inodoro, sin paneles ni puerta. Ocupa dos bloques de ancho y uno de alto, es decorativo y aparece en **LSM Mod School Functional Blocks**, al igual que los sanitarios existentes.

`light_school_tiled_floor` aparece en **LSM Mod School Blocks**. Gris base **#D7DADE**, juntas más oscuras y ruido discreto de 16×16. Reutiliza `BathroomFloorBlock`: nueve fases alineadas con coordenadas del mundo, baldosas de 24 píxeles (1,5×1,5 bloques), incluidas coordenadas negativas. `dark_school_tiled_floor` y sus texturas originales se conservan.

## Rendimiento

- Clase común `WideBathroomBlock`; `TallBathroomBlock` añade altura/puerta.
- Ocho estados para cada sanitario bajo y 32 para cada sanitario alto; nueve para el suelo claro.
- Formas físicas exactas por celda, construidas en una cuadrícula, con rotaciones directas compartidas. Los estados abierto/cerrado del urinario adulto comparten las mismas formas.
- Selección exterior sencilla mediante `SimpleBlockOutline.forState`; sin entidades de bloque, ticker permanente ni uniones de formas por consulta.
- Modelos nativos divididos en celdas, sin caras añadidas en las costuras. El optimizador existente sigue compartiendo modelos idénticos.
- Los generadores originales llaman al generador nuevo para no restaurar accidentalmente los sanitarios estrechos.

## Mundos existentes

Los bloques ya colocados con el formato anterior no tienen la segunda columna. Deben retirarse y colocarse de nuevo con espacio para dos bloques; no se rellena automáticamente espacio vecino ni se reemplazan otros bloques. El controlador desmonta los conjuntos incompletos al recibir una actualización y devuelve su objeto en supervivencia.

## Verificación

`python tools/check_wide_bathroom_assets.py`, `python tools/check_bathroom_statue_assets.py`, `python tools/check_dark_school_tiled_floor_assets.py` y `python tools/check_optimized_model_resources.py`: geometría en celda, caras sin solapamiento, apertura, selección de cuatro orientaciones, controlador/drop único, referencias y juntas de 24px. Regeneración idempotente mediante los puntos de entrada antiguos. Firmas de rotación e iteración comprobadas en la API 26.2 con javap.

No se compiló Java/Gradle ni se abrió Minecraft; colocación, interacción y renderizado requieren comprobación dentro del juego.
