# Modelos y texturas

Guía para peticiones de arte a Codex. Lee solo la familia que cambias; registros y generadores son la fuente del inventario actual.

## Rutas y entrega completa

| Pieza | Ruta desde la raíz |
|---|---|
| Clases/registros | `src/main/java/net/nicomar2009/lsmmod/{block,item,entity,registry,client}/` |
| Estado de bloque | `src/main/resources/assets/lsmmod/blockstates/<id>.json` |
| Modelos | `src/main/resources/assets/lsmmod/models/{block,item}/` |
| Definición moderna de ítem | `src/main/resources/assets/lsmmod/items/<id>.json` |
| Texturas | `src/main/resources/assets/lsmmod/textures/{block,item,entity}/` |
| Idiomas | `src/main/resources/assets/lsmmod/lang/{en_us,es_es}.json` |
| Drops/recetas | `src/main/resources/data/lsmmod/{loot_table/blocks,recipe}/` |
| Herramienta de minado | `src/main/resources/data/minecraft/tags/block/mineable/` |
| Generadores/checks/renders | `tools/` |
| Referencias originales/previews | `newresources/`, `previews/` |

Un bloque nuevo necesita registro, ítem, estado, modelo, definición de ítem, materiales existentes o PNG nuevos, traducciones y drop; etiqueta de herramienta si corresponde. Añade recetas solo si se solicitan. ModCreativeTabs distribuye automáticamente los ítems; revisa sus reglas para elegir la pestaña correcta. Los modelos de entidades viven en Java, no en `models/block`.

## Flujo rápido

1. Busca un ID o patrón similar con `rg '<id>' src/main/java tools` y localiza sus recursos. Lee el generador antes de editar: algunos también escriben Java, registros, idiomas y tags.
2. Usa las referencias del usuario para fijar proporciones, materiales y silueta. Expresa medidas en píxeles: **16 px = 1 bloque**. Si solo cambia una textura, conserva geometría y colisión. No inventes funciones para decoración.
3. Reutiliza geometría padre, texturas y clases. Modifica el generador que produce el resultado y sus archivos finales; no ejecutes todos los generadores. Revisa `git diff` para detectar sobrescrituras ajenas al pedido.
4. Comprueba referencias, UV, materiales, alfa y modelo de inventario. Para conjuntos, verifica todas las celdas y orientaciones, bisagras/apertura, apoyo, retirada y drop único. Mantén UV continuas y elimina tapas artificiales en cortes entre celdas.
5. **Render obligatorio para cualquier textura nueva o modificada:** aplica los PNG finales al modelo real, revisa frente/dorso/laterales e ítem, y corrige estiramientos, costuras, caras negras/magenta y transparencia. Inspeccionar solo el PNG no basta. Guarda la preview en `previews/`; distingue render externo de captura en Minecraft.
6. Ejecuta el check existente de la familia. Si cambias selección, herencia, loaders o preparación Fusion, ejecuta también `python tools/check_optimized_model_resources.py`. No compiles según AGENTS. Documenta solo un contrato nuevo o un pendiente real.

## Patrones reutilizables

Nombres de generadores/checks bajo `tools/`; úsalo como índice, no como lista que haya que ejecutar completa.

| Familia | Referencia de implementación | Generador / comprobación |
|---|---|---|
| Textura de cubo/suelo | `classroom_floor`, `school_parquet` | `create_classroom_surfaces.py`, `create_school_parquet_assets.py` |
| Decoración pequeña orientable | `SchoolPassiveAccessoryBlock`, `SchoolRouterBlock` | `create_school_cabinet_and_router.py` / `check_school_cabinet_and_router.py` |
| Mueble de varias celdas | `SchoolRectangularBlock`, `WideBathroomBlock` | `create_facility_expansion_assets.py` / `check_facility_expansion_assets.py` |
| Puertas anchas y cristal fijo | `SchoolFramedDiningDoorBlock`, `SchoolGlazing` | `create_facility_expansion_assets.py`, `create_framed_door_glazing.py` / `check_framed_door_glazing.py` |
| Enfermería y frente claro | Mismos perfiles físicos y dos canales de cristal | `create_infirmary_door_assets.py` / `check_infirmary_door_assets.py` |
| Cristal, escaleras y cortinas | `SchoolGlassBlock`, `SchoolGlassStairBlock` | `create_school_glass_assets.py`, `create_school_curtain_assets.py` / `check_school_glass.py`, `check_compact_school_glazing.py` |
| Pintura/medios cubos | `SchoolPaint`, bloques pintables | `create_paintable_school_block.py`, `create_custom_paintable_blocks.py` / checks de los mismos nombres |
| Pendientes OBJ | `SchoolInvertedSlopeBlock`, cuadrículas de formas | `create_school_inverted_slopes.py` / `check_school_inverted_slopes.py` |
| Escudo de suelo 4×4 | `SchoolShieldBlock` | `create_school_shield_floor_assets.py` / `check_school_shield.py` |
| Papelería/modelos padre | `folder`, `notebook`, 16 colores | `create_stationery_assets.py`; render con `blender -b --python tools/render_stationery_assets.py` |
| Uniforme equipado/ítem | `SchoolUniformItem`, equipment vanilla | `create_uniform_assets.py`; contrato en [ARMOR_SPECS](ARMOR_SPECS.md) |
| Skins/retratos de Student | `PlaceholderSchoolNpcModel`, `StudentAppearanceTextures` | `create_placeholder_student_skins.py`, `create_student_appearance_assets.py`, `create_student_gender_skins.py` |

Para las puertas, madera, cristal tintado y cubierta existe `python tools/render_texture_review.py`; usa `--vanilla-dir` si necesita la carpeta de texturas vanilla. Otros renders: `tools/render_*.py`. Algunos requieren Blender/Pillow; cada script indica sus dependencias.

## Rendimiento y compatibilidad

- Texturas pixeladas a la densidad de los materiales vecinos; evita aumentar resolución para disimular UV incorrectas. Al ampliar un atlas pixel art usa vecino más cercano. Luces y sombras de la escena las produce Minecraft.
- Variantes de color heredan geometría. No dupliques modelos, texturas o clases por orientación si una rotación/tabla sirve. No copies cientos de combinaciones que solo cambian color.
- Separa contorno sencillo y colisión exacta. Reutiliza `ExactVoxelShapes`, las cuadrículas existentes, `SharedChairRotations` y `SimpleBlockOutline` según el patrón existente. Cachés físicas por geometría/ocupación, no por colores; no uses una caché por estado para geometría dependiente de vecinos.
- Conserva propiedades físicas y guardadas. Conexiones puramente visuales pueden calcularse con vecinos cargados mediante predicados Fusion; no fuerces cargas de chunks ni añadas entidades/tickers para decoración estática.
- En vidrio, separa capas solid/cutout/translucent; el vidrio transparente no debe ocultar caras opacas visibles a través de él. Cullface solo donde la cara toca el límite correcto y la conexión lo permite.
- `optimize_model_resources.py` prepara originales y `src/generated/resources` en `build/generated/optimizedResources`, antes de `processResources`. Edita **los fuentes**, nunca esa salida. Comparte duplicados, padres sólidos y selectores; `prepare_high_state_models.py` exige una hoja por modelo nativo en cada raíz.
- Fusion 1.3.16: conserva `neoforge:obj` para OBJ; declara materiales/particle en compuestos. El MTL de pavimento usa `map_Kd gray`/`yellow` sin `#`. Un `fusion:base` usado como componente debe tener geometría real válida; no uses bases vacías ni supongas soporte de matrices `transform` en compuestos.
- Reducir modelos o selectores no prueba una mejora de tiempo. Mide arranque/JFR si se pide rendimiento; no inventes segundos ahorrados.

## Peticiones del usuario

Basta una instrucción libre como «crea una mesa de 3×1 según esta foto» o «cambia la madera a este tono». Aprovecha medidas y referencias disponibles; pregunta solo por información que cambie el resultado. Codex debe entregar recursos integrados, generador coherente, preview revisada y comprobaciones relevantes, sin exigir una plantilla.
