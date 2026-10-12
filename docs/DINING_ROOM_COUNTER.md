# Mostrador del comedor y materiales escolares

`dining_room_counter` (Cubierta del mostrador del comedor / Dining Room Countertop)
conserva únicamente la cubierta Y14..16 px, de 1×1 bloques y 2 píxeles de espesor.
Base y cuerpo eliminados del modelo, selección y colisión. Se coloca individualmente
y puede alinearse con otras cubiertas.

Textura nueva y uniforme RGB22/31/37, tomada de la cara superior de teachers_desk.
`DiningRoomCounterBlock` hereda las nueve fases X/Z anteriores de `BathroomFloorBlock`
para conservar los estados guardados. Todos seleccionan un solo modelo/imagen;
colisión y contorno comparten una caja constante Y14..16, con espacio libre debajo.
La cara inferior es visible a Y14 y no se marca con cullface=down, porque no está
en el límite inferior de la celda. Un ítem/drop, resistencia y sonidos de piedra,
etiqueta de pico y School Functional Blocks. Sin entidad, inventario, interacción
ni ticker nuevos.

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
Render revisado: previews/tinted_glass_and_counter_review.png, generado con
tools/render_texture_review.py usando el modelo y la textura finales.
