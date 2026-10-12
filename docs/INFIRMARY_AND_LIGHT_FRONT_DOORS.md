# Puerta de enfermería y variante de frente claro

`infirmary_door` parte de `bathroom_door_wide`: hoja maciza de madera de 24×40 px, dos columnas y tres filas, ambas bisagras y cuatro orientaciones. Conserva la colocación con un ítem, apertura desde cualquier celda, comprobaciones de espacio/apoyo y un solo drop.

En el lateral fijo de 8 px, el primer bloque de altura sigue siendo pared. Desde y16 hasta y48, su mitad posterior (z8..16) usa el cristal y marco de `school_connected_tinted_glass`. El frente de la pared utiliza `light_school_wall`. El sobreluz de y40..48 también es tintado. Abrir la hoja mantiene ambos cristales fijos.

El sobreluz y el panel perpendicular tienen canales Fusion separados (`main` y `side`). Comparten las reglas de SchoolGlazing, pero cada uno se conecta según su orientación y contacto real: la media pieza lateral conecta hacia atrás, sin inventar una unión a través del muro delantero. Las dos filas laterales eliminan el travesaño intermedio; los cristales normales y tintados vecinos pueden prolongarlas horizontalmente. Cada panel conserva los marcos expuestos; las conexiones verticales requieren la misma huella. La selección del modelo distingue ambos canales para evitar reutilizar la máscara frontal en el lateral. Los perfiles son constantes compartidas y la actualización del tramo recorre sus celdas una vez.

`dining_door_wide_light` conserva la geometría, cristal normal, hoja y funcionamiento de `dining_door_wide`. Solo el frente de la franja lateral usa `light_school_wall`; las otras caras de esa pared utilizan `classroom_white_wall`.

En ambas puertas anchas del comedor, el cristal central conserva los reflejos vanilla y usa UV interiores 1..15. Así no se superpone su borde exterior al marco gris original de la textura de madera. El cambio también se aplica al icono de inventario.

Los nuevos bloques reutilizan SchoolFramedDiningDoorBlock. La caché física incluye el perfil de enfermería para no confundir sus celdas con las de la puerta original. Ambos mantienen `SimpleBlockOutline.forState`, 192 estados por bloque y ningún bloque entidad o ticker adicional.

## Recursos y comprobación

Corrección del crash al renderizar con Xaero (2026-10-12): el estado que recibe el render puede quedar obsoleto respecto al mundo consultado. SchoolGlazing valida el perfil de la celda actual y los extremos leídos después del recorrido; ante una fila desaparecida o incompleta conserva la máscara segura de bordes. El refresco horizontal del servidor también valida extremos y celdas antes de calcular el marco. No cambia los perfiles, UV, geometría ni conexiones de filas estables. La comprobación es estática; falta confirmar el caso dentro de Minecraft con Xaero.

Generar: `python tools/create_facility_expansion_assets.py`; el módulo `tools/create_infirmary_door_assets.py` produce las variantes reproduciblemente.

Renders externos: `python tools/render_texture_review.py --vanilla-dir /tmp/lsm-render-assets`. Se revisaron las vistas frontales/posteriores, apertura, ambas bisagras, conexiones e inventario, conforme al capítulo 8 de START_HERE. Son renders de los modelos y materiales finales, no capturas de Minecraft:

- `previews/infirmary_door_texture_review.png`
- `previews/door_variants_and_connections_review.png`
- `previews/new_door_inventory_review.png`
- Vistas actualizadas de las puertas anchas originales en `previews/wide_doors_*`.

Regresión específica: `python tools/check_infirmary_door_assets.py`: 384 estados, 64 selecciones independientes de máscaras frontal/lateral, materiales, drops, huella física abierta/cerrada y bisagras. También se comprueban las puertas existentes, UV nativas, espaciado horizontal, escaleras de cristal, culling y recursos preparados. Generador idempotente.

Sin compilación ni prueba dentro de Minecraft. `checkFurnitureOutlines` depende de `outlineCheckClasses`, por lo que no se ejecutó al requerir compilación; la separación de contorno/colisión y la ocupación física se verificaron estáticamente.
