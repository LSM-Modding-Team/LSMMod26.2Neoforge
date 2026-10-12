# Ampliaciones de instalaciones y soporte de la baranda

## Entrada nueva

`school_sliding_entrance` conserva sus dimensiones de 6×4 y el paso central de
4×4. Cada celda tiene una cuadrícula de **3×3: nueve cuadrados**, con barras de
un píxel. Entre ellas aparece `school_tinted_glass`, completamente opaco. Vidrio
incrustado en el espesor de las barras, visible desde ambos lados. Los laterales
siguen fijos y las hojas telescópicas conservan sus bolsillos y controladores.

## Soporte

`light_school_wall_railing` devuelve una forma de soporte completa y declara
`forceSolidOn`. Son las dos comprobaciones distintas que utiliza Minecraft 26.2:
cuadros/marcos consultan `BlockState.isSolid()`; muchos bloques montados del mod
consultan `isFaceSturdy`. Se inspeccionó la API con javap. La colisión sigue siendo
el muro de 12 px más la baranda; la oclusión conserva el muro de 12 px. La forma
virtual de soporte permite las colocaciones solicitadas sin rellenar físicamente
el hueco de la baranda.

## Camilla y estatua

Las camillas nuevas ocupan 3×1. Se conserva el respaldo inclinado, reutilizando
sus cuadrículas/rotaciones originales, y se alarga la sección plana. `extended`
permite mantener intactos los pares antiguos de dos celdas (24 estados totales).

Las estatuas nuevas `san_martin_de_porres` ocupan 2×3×1. Se amplía el modelo
esculpido existente, con seis celdas y un único ítem. La clase conserva los estados
antiguos `half`, la geometría y los modelos de dos mitades; `expanded` distingue
las colocaciones nuevas. 96 estados totales, con 32 selectores compartidos.

Recoloca las camillas/estatuas antiguas para obtener las dimensiones nuevas.

## Bloques nuevos en School Functional Blocks

- `dining_door_wide`: conjunto de 2×3. La hoja de madera mide 24×40 px
  (1.5×2.5) y utiliza las texturas originales de bathroom_door. Su abertura
  de 12×26 px (X8..20/Y6..32) lleva vidrio vanilla con su borde, insertado
  a Z1..3 dentro de los 4 px de grosor de la hoja. La franja superior completa
  mide 32×8×16 px y usa school_glass. No se generan tapas interiores al dividir
  el cristal entre celdas; las UV conservan un único borde en la ventana.
  La franja lateral de medio bloque usa classroom_white_wall, salvo la cara
  exterior que limita con el bloque vecino: esa cara usa light_school_wall.
  Dos bisagras, cuatro orientaciones, 192 estados. Se coloca sobre el suelo
  mediante BlockItem, sin exigir clic en un marco o pared; el lado del clic y
  los bloques contiguos eligen la bisagra. Clic derecho abre/cierra.
  Al abrir se reservan tres celdas adicionales para los 8 px del giro que exceden
  la profundidad original; apertura y cierre comprueban todo antes de modificar
  el mundo. El vidrio y la franja lateral permanecen fijos.
- `school_microwave`: microondas decorativo de una celda, cuerpo claro,
  puerta oscura, ventana opaca, tirador y controles. Cuatro estados.
- `school_trophy_gallery`: vitrina de 6×4×0,75 con seis columnas, cuatro niveles y
  24 premios: dos perfiles de copa, escudo y premio vertical escalonado; alturas
  de 8, 10, 12 y 13 px. Dieciocho son dorados y seis plateados o bronceados.
  Fondo en Z10,5..12, estantes/laterales en Z0..10,5, cristal frontal Z0..0,125.
  Cristal frontal en una capa translúcida separada de madera y
  trofeos. 96 estados. Un ítem coloca las 24 celdas; sin inventario ni ticker.
  Los modelos de ítem de puerta y vitrina usan componentes Fusion base con
  geometría real y capas de renderizado independientes.

## Verificación

`check_facility_expansion_assets.py`: 412 estados, cobertura de variantes/capas,
dimensiones, vidrio opaco entre las nueve casillas, rutas antiguas, giro y material
exterior de la puerta, ventana/dintel/materiales/UV/bisagras, dimensión exacta
de vitrina, perfiles/alturas/metales de los premios, soporte virtual, cachés e idempotencia.
`check_kiosk_entrance_assets.py`, `check_bathroom_statue_assets.py`,
`check_furniture_direct_grids.py` y `check_optimized_model_resources.py`: regresiones
de los conjuntos originales, colisiones originales compartidas y referencias/UV.
Sin compilación Java ni ejecución de Minecraft; prueba visual/interactiva pendiente.
No se ejecuta `checkFurnitureOutlines` porque requiere compilar
`outlineCheckClasses`; se verifican las rejillas/rotaciones y contornos estáticamente.
