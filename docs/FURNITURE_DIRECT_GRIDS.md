# Segunda fase de formas físicas: construcción y transformaciones directas

Tras optimizar la rotación de sillas, la JFR mostró construcción inicial de NewChairShapes (364/2351 muestras), camillas (197) y pendientes con baranda (99). Son muestras de CPU, no segundos exactos.

## Cambios

ExactVoxelShapes construye una cuadrícula con todas las coordenadas originales de cajas AABB y marca directamente las celdas de su unión. No realiza uniones u optimizaciones sucesivas, no sustituye el volumen por una envolvente y no redondea coordenadas. Sus listas de coordenadas son inmutables. Admite solapamientos, huecos, cajas fuera del bloque y entrada vacía.

- ChairBlock, NewChairShapes y StoolBlock: formas originales construidas directamente, antes de usar la caché de rotaciones existente.
- InfirmaryCotShapes: dos segmentos originales con cuadrícula directa y cuatro orientaciones mediante la caché compartida. Se conservan sus perfiles distintos y las celdas de la camilla.
- SchoolRailingSlopeBlock: sus 32 segmentos de muro, 32 segmentos de pasamanos y poste opcional se incorporan directamente a la cuadrícula. Las cuatro orientaciones usan Shapes.rotate a través de la caché; se conserva la altura por mitades, banda inferior, agua y conexiones con descansos.
- DeskBlock: forma original directa, rotaciones compartidas y reflejo de la mitad derecha con Shapes.rotate/INVERT_Z. Las subclases que utilizan este constructor reciben la mejora. No se modificaron las familias independientes de escritorios de computadora.

No se cambiaron estados, recursos gráficos, selección, colisiones declaradas, colocación ni lógica de uso. El generador de la silla de comedor y los lectores de verificaciones ahora reconocen ExactVoxelShapes, evitando que una regeneración reintroduzca las uniones anteriores.

## Verificación

`check_furniture_direct_grids.py` compara 295 cajas con una instantánea de coordenadas previas versionada y prueba 15000 puntos de ocupación independientes. La comparación adicional con las fuentes anteriores confirmó que el código de altura/conexiones de pendientes no cambió. `check_chair_grid_rotations.py` conserva siete perfiles y 28 giros; `check_school_railing_slopes.py` conserva las comprobaciones de geometría y continuidad de sus diez piezas.

Se ampliaron las pruebas Java de checkFurnitureOutlines para verificar huecos, solapamientos, volumen fuera de bloque y unión vacía; sus lectores incluyen la nueva notación. No se ejecutó ese task ni se compiló, conforme a la instrucción vigente. Las pruebas Python son estáticas y no sustituyen una prueba Java/Minecraft. Falta una nueva JFR y comprobar dentro del juego sillas, camillas, pendientes y ambas mitades de escritorios.
