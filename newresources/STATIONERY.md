# Folders y cuadernos

- Folder oficio de tapa dura inspirado en ARTESCO, sin liga, sin anillos y con pliegues interiores.
- Cuaderno college inspirado en Bakan/Stanford, color sólido, sin espiral, lomo encolado y bloque de hojas visible.
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
