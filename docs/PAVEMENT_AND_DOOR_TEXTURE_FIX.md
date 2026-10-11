# Pavimento y caras internas de las puertas con cristal

11 de octubre de 2026 (America/Lima). El usuario confirmó que el slab y las seis
pendientes seguían sin textura, y señaló las franjas/caras internas del vidrio.

## Materiales del pavimento

NeoForge ObjGeometry resuelve map_Kd mediante MaterialBaker.resolveSlot, que
llama TextureSlots.getMaterial con el token completo. Minecraft 26.2 retira un
# inicial; el adaptador ModelGeometryImpl.createTextureSlots de Fusion 1.3.16
lo reenvía al resolutor de materiales sin retirarlo. Las claves declaradas son
gray/yellow, por lo que #gray/#yellow no se encuentran dentro del compuesto.
Declararlas solamente en la raíz, como en la corrección anterior, no bastaba.

El MTL compartido ahora utiliza map_Kd gray y map_Kd yellow. Ambos resolutores
aceptan esos nombres; posiciones, caras, UV, colores y selección de bordes se
conservan. El generador de las pendientes escribe el mismo MTL corregido.
La ruta se comprobó leyendo el código de NeoForge/Fusion y el bytecode de
TextureSlots/MaterialBaker de Minecraft 26.2, sin compilar ni ejecutar el juego.

## Caras del vidrio

Al preparar recursos, repair_door_glazing_faces.py corrige los modelos nativos
de tall_classroom y double_school_entrance antes de compartir modelos idénticos.
Las otras tres puertas altas heredan la geometría corregida del aula.

- Retira caras de vidrio completamente cubiertas por una caja de marco/madera.
- Conserva las caras parcialmente cubiertas por el faldón de madera de cuatro
  píxeles: la claraboya tiene ocho píxeles de profundidad.
- Retira tapas laterales del vidrio en límites sin poste. La selección visual
  solo permite esos límites dentro de una fila conectada; ambos extremos
  expuestos de una fila siempre tienen poste.
- Restaura cullface en caras de vidrio situadas en límites de bloque. El recorte
  por celdas de la puerta doble había descartado esas marcas, dejando caras en
  uniones verticales aunque SchoolGlazing.connects indicase contacto.

396 caras retiradas; 312 archivos preparados cambian. No se desplazan elementos
ni se cambian texturas/UV. Madera, marcos, colisiones, apertura, propiedades y
recuento de estados quedan intactos. Los JSON originales siguen editables.
El helper se registra como entrada de optimizeModelResources, por lo que Gradle
invalida los recursos preparados al actualizarlo; no hace falta editar los
modelos manualmente. No se añaden estados ni se revierte la optimización Fusion.

## Verificaciones

- check_entrance_pavement_shapes.py: geometría/volumen de las seis pendientes y
  slab, continuidad, MTL y texturas.
- check_door_glazing_faces.py: 366 modelos originales inspeccionados, ausencia de
  tapas cubiertas/internas, madera/marcos/UV intactos, reparación idempotente,
  faldones parciales conservados, variantes heredadas y siete familias OBJ.
- check_optimized_model_resources.py: equivalencia de recursos con la reparación
  explícita de caras/cullface; 15344 combinaciones y 1477 selectores adicionales,
  referencias de ítems y hojas únicas de los compuestos conservadas.
- check_compact_school_doors.py: 21504 selecciones heredadas; 672 estados de puertas.
- check_glazing_face_culling.py: soportes de escaleras junto a puertas conservados.

Todas pasan de forma estática. No se compiló ni ejecutó Minecraft por instrucción
vigente. Falta confirmar que desaparecen las franjas de la captura y el patrón
magenta/negro del pavimento con los nuevos recursos dentro del juego.
