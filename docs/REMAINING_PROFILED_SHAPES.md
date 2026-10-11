# Formas restantes identificadas en la JFR

La grabación previa mostró 1661 muestras Java: 266 en operaciones de formas físicas. Se revisaron todas las clases de bloques presentes, sin atribuir los métodos de Fusion/carga de clases a una colisión concreta.

## Cambios

- SchoolWoodenHandrailBlock: construcción directa de sus 16 tramos; caché por columna, fila, orientación y lado (144 perfiles, sin mundo/contexto). Se conserva la altura desde Y=0 y el límite izquierdo/derecho.
- SchoolWallRailingBlock: cuadrícula y caché por conexiones, poste y eje aislado. Los 256 estados comparten 34 claves físicas; la base y la oclusión conservan 12 píxeles.
- SchoolInvertedSlopeBlock y EntrancePavementSlopeBlock: 32 tramos originales en cuadrícula directa; rotaciones vanilla sin uniones ni optimize. Se conservan las alturas, offsets, tapas y orientación nativa.
- SportsGoalBlock: cada una de las 144 celdas conserva sus cajas y obtiene las cuatro orientaciones mediante la caché compartida. Las celdas vacías siguen vacías.
- ComputerDeskShapes y GrayComputerDeskShapes: perfiles nativos directos, rotaciones de cuadrícula. Las tablas mantienen mitad/ocupación/columna/orientación; sus bloques consultan esas mismas tablas.
- KioskTableBlock y DiningTableBlock: las cajas originales se incorporan directamente.
- TallBathroomBlock, TrashBinBlock y WallDecorationBlock: entrada de cajas con construcción directa y rotación de cuadrícula. Se conserva el recorte por mitad/estado abierto del baño.
- DrinkingFountainBlock y ManuelTiradoBustShapes: geometría nativa directa y rotaciones vanilla, conservando capas y orientaciones.
- DoubleSchoolEntranceBlock: dos volúmenes como máximo por celda en cuadrícula directa; rotaciones sin reconstrucción de cajas. Apertura, cristal, bisagras, celdas y controladores no cambian.

Los generadores de escritorios de computadora y comedor se actualizaron para conservar este formato. ExactVoxelShapes incluye una entrada para arrays de cajas en píxeles. Las rotaciones parten de las formas originales y conservan sus huecos; no se reemplaza la colisión por una envolvente.

## Casos revisados que ya eran sencillos

AwningBlock/AwningShapes, ChairBlock/SharedChairRotations y SchoolRailingSlopeBlock ya usaban cuadrículas y/o tablas compartidas. FullBathroomDoorBlock, ToiletBlock, VaultingBoxBlock y SchoolGlassStairBlock no necesitaban repetir esta conversión. ComputerDeskBlock/GrayComputerDeskBlock/ManuelTiradoBustBlock solo consultan las tablas actualizadas. SimpleBlockOutline ya guarda el contorno simple. No se modificaron los predicados visuales ni el registro de Fusion.

## Validación

`check_remaining_shape_grids.py` compara 228 cajas con coordenadas anteriores versionadas y 6000 muestras de ocupación. Revisa 144 perfiles de pasamanos, las 34 claves físicas de los 256 estados de baranda y compatibilidad de generadores. Pasaron las regresiones de pasamanos, baranda, pendientes invertidas/elevadas/extras, pavimento, portería y puertas. Se comprobó estáticamente que las definiciones de estado y métodos de colocación de los archivos editados siguen iguales.

No se compiló ni ejecutó Minecraft, conforme a la instrucción vigente. checkFurnitureOutlines depende de compilación y quedó pendiente. Las verificaciones Python no sustituyen pruebas Java ni una comprobación de colisión dentro del juego. Falta una nueva JFR para medir esta fase; no se promete una mejora proporcional en segundos.
