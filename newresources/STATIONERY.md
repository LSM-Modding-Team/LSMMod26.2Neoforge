# Folders y cuadernos

- Folder oficio de tapa dura inspirado en ARTESCO, sin liga, sin anillos y con pliegues interiores.
- Cuaderno college inspirado en Bakan/Stanford, tapa flexible fina, color sólido, sin espiral, diseño gráfico sin palabras, lomo encolado y bloque de hojas visible.
- 16 colores vanilla por tipo, 32 items registrados en la pestaña LSM Mod.
- Geometría compartida: `assets/lsmmod/models/item/folder.json` y `notebook.json`.
- Cada hijo `<color>_folder.json` / `<color>_notebook.json` cambia únicamente `textures.cover`.
- Ambos tipos usan el mismo `textures/item/stationery/cover_<color>.png` (128×128).
- `details.png` mantiene neutras las hojas y etiquetas; no se recolorea.

Regeneración desde la raíz:

```sh
python tools/create_stationery_assets.py
blender -b --python tools/render_stationery_assets.py
```

Prueba en Minecraft:

```mcfunction
/give @s lsmmod:blue_folder
/give @s lsmmod:red_notebook
```

Comprobar en inventario, ambas manos, suelo y marco. La preview se renderiza a partir de los cuboides y atlas; no sustituye la prueba en el juego. Recetas, consumo y Estudioso quedan pendientes.

## Diseño final pendiente de publicación

Folder oficio cerrado con borde prensado y ficha pequeña abajo a la derecha, dentro del borde. Cuaderno de tapas finas satinadas al ras del conjunto hojas/lomo; gráficos superiores y ficha inferior sin palabras literales. Lomo y hojas contiguos, sin solapamiento ni remate blanco junto a la tapa.

Vista frontal: `stationery_front.png`. Perspectiva: `stationery_preview.png`.
JAR local compilado; prueba en Minecraft pendiente. Commit preparado, sin push hasta autorización explícita del usuario.
