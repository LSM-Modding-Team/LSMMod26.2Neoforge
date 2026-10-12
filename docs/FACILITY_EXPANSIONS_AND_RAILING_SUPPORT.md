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
  (1.5×2.5) y utiliza las texturas originales de dining_door. Su ventana
  de 9×27,5 px (X7,5..16,5/Y7,5..35) coincide con la región transparente
  de las texturas pequeñas escaladas. Lleva vidrio vanilla con su borde, insertado
  a Z1..3 dentro de los 4 px de grosor de la hoja. La franja superior completa
  mide 32×8×8 px y usa el cristal/marco de school_glass, con el mismo espesor
  y posición que las demás piezas escolares. El modelo Fusion del dintel
  selecciona las máscaras de SchoolGlazing sin añadir propiedades al bloque:
  suprime el borde superior al conectar con cristal encima y comparte los
  separadores horizontales, incluso junto a school_glass completo. Se comprueban
  orientación, huella y contacto; horizontalmente no conecta con el tramo inferior de vidrio
  de una escalera invertida. El cristal escolar colocado encima hereda su
  orientación; las filas se refrescan al colocar, abrir, cerrar o retirar.
  Las tapas del cristal conectadas desaparecen sin ocultar las caras del muro.
  La ventana vanilla se dibuja una sola vez desde la celda de fila 1 con un
  elemento completo de 9×27,5 px y UV0..16; sus límites quedan dentro del rango
  nativo de modelos -16..32. Evita las uniones y diferencias de contracción UV
  de varias piezas. Las colisiones y la selección permanecen en cada celda.
  La franja lateral de medio bloque usa classroom_white_wall, salvo la cara
  exterior que limita con el bloque vecino: esa cara usa light_school_wall.
  El frente marcado en verde se ha restaurado a classroom_white_wall; también
  son blancos el reverso, interior, superior e inferior.
  Dos bisagras, cuatro orientaciones, 192 estados. Se coloca sobre el suelo
  mediante BlockItem, sin exigir clic en un marco o pared; el lado del clic y
  los bloques contiguos eligen la bisagra. Clic derecho abre/cierra.
  Al abrir se reservan tres celdas adicionales para los 8 px del giro que exceden
  la profundidad original; apertura y cierre comprueban todo antes de modificar
  el mundo. El vidrio y la franja lateral permanecen fijos.
- `bathroom_door_wide`: variante independiente de la misma puerta con marco,
  textura de madera bathroom_door, ítem, drop, traducciones y etiqueta de hacha
  propios. 192 estados; misma clase, colisiones y conexiones. Los modelos
  estáticos heredan los de dining_door_wide cambiando únicamente las texturas
  de madera. La hoja del baño es completamente de madera, sin modelo ni capa
  de ventana central; comparte el dintel de cristal dinámico.
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

Las dos puertas con marco usan las texturas originales de sus versiones pequeñas:
mitad inferior Y0..20 y superior Y20..40, escala vertical uniforme de 1,25 y
horizontal de 1,5. Las caras comparten coordenadas UV globales al dividirse
entre celdas; los cantos usan las tres columnas de textura de la puerta vanilla.
El inventario utiliza ese mismo mapeo. La madera usa cutout, como las puertas
pequeñas, para conservar los píxeles transparentes de dining_door. Generador
auxiliar `tools/framed_door_texture_uv.py`; sin modificar los PNG originales.
La hoja completa conserva las colisiones habituales de una puerta; la ventana
se define visualmente por el recorte de la textura original. Al partir el modelo
entre celdas se eliminan las tapas internas de madera, que antes aparecían como
barras atravesando el cristal, y se refleja la textura al cerrar la bisagra derecha.

## Verificación

`check_facility_expansion_assets.py`: 604 estados, cobertura de variantes/capas,
dimensiones, vidrio opaco entre las nueve casillas, rutas antiguas, giro y material
exterior de la puerta, ventana/dintel/materiales/UV/bisagras, dimensión exacta
de vitrina, perfiles/alturas/metales de los premios, soporte virtual, cachés e idempotencia.
`check_framed_door_glazing.py`: las 32 máscaras de conexión, profundidad de
8px, tapas/bordes retirados, una sola ventana en comedor y ninguna en baño en
las 32 combinaciones de variante/orientación/apertura/bisagra, UV y materiales/ítems/drops.
`check_texture_review_changes.py`: 7 680 muestras de las UV frente a los PNG
originales, ambas caras/bisagras y ausencia de tapas interiores de madera.
`render_texture_review.py --vanilla-dir /tmp/lsm-render-assets`: renders finales
en previews/wide_doors_texture_review.png, wide_doors_right_hinge_review.png,
wide_doors_back_review.png, wide_doors_inventory_review.png y
wide_doors_original_comparison.png. El directorio externo debe contener glass.png,
door_bottom_left.json y door_top_left.json de los assets de Minecraft 26.2.
Rasterización con profundidad, recortes, transparencia y UV/materiales reales;
vistas externas revisadas, no capturas de Minecraft.
`check_horizontal_glazing.py`, `check_glazing_face_culling.py` y
`check_school_glass.py`: regresión de conexiones escolares existentes,
espaciado par/impar y caras expuestas junto a puertas/escaleras.
`check_kiosk_entrance_assets.py`, `check_bathroom_statue_assets.py`,
`check_furniture_direct_grids.py` y `check_optimized_model_resources.py`: regresiones
de los conjuntos originales, colisiones originales compartidas y referencias/UV.
Sin compilación Java ni ejecución de Minecraft; prueba visual/interactiva pendiente.
No se ejecuta `checkFurnitureOutlines` porque requiere compilar
`outlineCheckClasses`; se verifican las rejillas/rotaciones y contornos estáticamente.
