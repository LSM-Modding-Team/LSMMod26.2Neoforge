# Mostrador del comedor y materiales escolares

`dining_room_counter` (Mostrador del comedor / Dining Room Counter) es el bloque
de mampostería de la segunda referencia, de 1×1×1. Se coloca individualmente
y puede alinearse con otros mostradores sin juntas geométricas adicionales.

- Base Y0..1 px: `dark_school_tiled_floor`, material elegido por el usuario
  para la zona que en la referencia se llamaba dining_room_floor.
- Cuerpo Y1..14 px: `light_school_wall`.
- Cubierta Y14..16 px: `light_school_tiled_floor`, con sus lados claros y
  superficie de baldosas de paso 24 px.

Reutiliza `BathroomFloorBlock`: nueve fases X/Z módulo tres, incluso en
coordenadas negativas, para alinear las baldosas de la cubierta con el suelo.
Colisión y contorno de cubo completo vanilla; un ítem/drop, resistencia y
sonidos de piedra, etiqueta de pico y School Functional Blocks. Sin entidad,
inventario, interacción ni ticker nuevos.

Los pisos pasan a llamarse `dark_school_tiled_floor` y `light_school_tiled_floor`.
Texturas, geometría y estados originales conservados. Los recursos, registros,
ítems, drops, etiquetas y generadores utilizan esos IDs. `DeferredRegister.addAlias`
en bloques e ítems resuelve bathroom_floor/light_bathroom_floor a los nombres
nuevos para leer paletas e inventarios guardados, sin duplicar entradas creativas.
Firma confirmada en el código de NeoForge 26.2.x; carga de mundos pendiente de
verificación dentro de Minecraft.

`hallchair`, `plasticchair_red` y `plasticchair_red_arms` comparten `chair_red`.
Esa textura usa exactamente RGB112/21/36, el rojo de school_bell. El generador
lee el color del timbre; geometrías, estructuras negras, asientos y otras sillas
conservados.

Generador: `tools/create_dining_counter_assets.py`. Comprobación estática:
`tools/check_dining_counter_assets.py` (materiales/proporciones/caras/fases,
IDs/alias, recursos, rojo compartido e idempotencia). Las regresiones de pisos,
sanitarios, lavadero, rejillas/rotaciones de muebles y recursos se ejecutan con
Python. Sin compilación ni ejecución de Minecraft.
