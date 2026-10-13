# Modelos y texturas

Entrada única para crear bloques/modelos/texturas: sigue el flujo y abre solo las herramientas de tu familia. Registros y generadores son el inventario real.

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

1. Elige el patrón en la tabla inferior y abre sus enlaces; para un ID existente usa `rg '<id>' src/main/java tools`. Lee el generador: puede escribir Java/registros/idiomas/tags. Consulta [BLOCK_SPECS](BLOCK_SPECS.md) solo al cambiar comportamiento o compatibilidad.
2. Usa las referencias del usuario para fijar proporciones, materiales y silueta. Expresa medidas en píxeles: **16 px = 1 bloque**. Si solo cambia una textura, conserva geometría y colisión. No inventes funciones para decoración.
3. Reutiliza geometría padre, texturas y clases. Modifica el generador que produce el resultado y sus archivos finales; no ejecutes todos los generadores. Revisa `git diff` para detectar sobrescrituras ajenas al pedido.
4. Comprueba referencias, UV, materiales, alfa y modelo de inventario. Para conjuntos, verifica todas las celdas y orientaciones, bisagras/apertura, apoyo, retirada y drop único. Mantén UV continuas y elimina tapas artificiales en cortes entre celdas.
5. **Render obligatorio:** `python tools/render_block_review.py <id>`; para rejilla column/row/depth añade `--grid ancho alto fondo`, por ejemplo `school_refrigerator --grid 2 3 1`. Usa `--state facing=north clave=valor` para otros estados y `--vanilla-dir` para materiales vanilla. Requiere Pillow/numpy; guarda frente/laterales/dorso/ítem en `previews/<id>_review.png`. Abre la imagen y corrige UV/costuras/materiales/alfa. Para OBJ, piezas giradas, estados dinámicos u otras rejillas usa el render de la familia; el comando exige geometría nativa sin giro. Es render externo, no prueba en Minecraft.
6. Ejecuta el check existente de la familia. Si cambias selección, herencia, loaders o preparación Fusion, ejecuta también `python tools/check_optimized_model_resources.py`. No compiles según AGENTS. Documenta solo un contrato nuevo o un pendiente real.

## Patrones reutilizables

Enlaces relativos a `tools/`: ejecuta solo el generador/check/render de tu familia. El render nativo común es [render_block_review.py](../tools/render_block_review.py).

| Familia | Referencia de implementación | Generador / comprobación |
|---|---|---|
| Textura de cubo/suelo | `classroom_floor`, `school_parquet` | [create_classroom_surfaces.py](../tools/create_classroom_surfaces.py), [create_school_parquet_assets.py](../tools/create_school_parquet_assets.py); render nativo |
| Decoración pequeña orientable | [SchoolPassiveAccessoryBlock](../src/main/java/net/nicomar2009/lsmmod/block/SchoolPassiveAccessoryBlock.java), SchoolRouterBlock | [create_school_cabinet_and_router.py](../tools/create_school_cabinet_and_router.py) / [check_school_cabinet_and_router.py](../tools/check_school_cabinet_and_router.py); render nativo |
| Mueble de varias celdas | [SchoolRectangularBlock](../src/main/java/net/nicomar2009/lsmmod/block/SchoolRectangularBlock.java), [WideBathroomBlock](../src/main/java/net/nicomar2009/lsmmod/block/WideBathroomBlock.java) | [create_facility_expansion_assets.py](../tools/create_facility_expansion_assets.py) / [check_facility_expansion_assets.py](../tools/check_facility_expansion_assets.py); referencia cerrada con vidrio: [create_school_refrigerator_assets.py](../tools/create_school_refrigerator_assets.py), render nativo con `--grid` |
| San Martín en vitrina | Caja blanca 2×2,5×1; estatua centrada 1×2, cristal frontal; ítem EPIC | [create_san_martin_display.py](../tools/create_san_martin_display.py) / [check_bathroom_statue_assets.py](../tools/check_bathroom_statue_assets.py); render [render_san_martin_display.py](../tools/render_san_martin_display.py) |
| Puertas anchas y cristal fijo | `SchoolFramedDiningDoorBlock`, `SchoolGlazing` | [create_facility_expansion_assets.py](../tools/create_facility_expansion_assets.py), [create_framed_door_glazing.py](../tools/create_framed_door_glazing.py) / [check_framed_door_glazing.py](../tools/check_framed_door_glazing.py) |
| Enfermería y frente claro | Mismos perfiles físicos y dos canales de cristal | [create_infirmary_door_assets.py](../tools/create_infirmary_door_assets.py) / [check_infirmary_door_assets.py](../tools/check_infirmary_door_assets.py) |
| Ventana proyectante y mitad pintable | Retícula 24×24 px anclada al primer bloque; plano de 2 px en el borde lejano, apertura a 45° alejándose del jugador. Mitad acristalada integrada en la misma retícula y apertura de la ventana normal; mitad sólida fija con las seis pinturas estándar; sin cortinas | [create_school_projecting_window_assets.py](../tools/create_school_projecting_window_assets.py) / [check_school_glass.py](../tools/check_school_glass.py) con `--projecting-only` y [check_paintable_school_block.py](../tools/check_paintable_school_block.py) con `--projecting-half-only`; render [render_school_projecting_window.py](../tools/render_school_projecting_window.py), `--vanilla-dir` para el hormigón sin pintar |
| Cristal, escaleras y cortinas | `SchoolGlassBlock`, `SchoolGlassStairBlock` | [create_school_glass_assets.py](../tools/create_school_glass_assets.py), [create_school_curtain_assets.py](../tools/create_school_curtain_assets.py) / [check_school_glass.py](../tools/check_school_glass.py), [check_compact_school_glazing.py](../tools/check_compact_school_glazing.py) |
| Pintura/medios cubos | `SchoolPaint`, bloques pintables | [create_paintable_school_block.py](../tools/create_paintable_school_block.py), [create_custom_paintable_blocks.py](../tools/create_custom_paintable_blocks.py) / checks de los mismos nombres |
| Pendientes OBJ | `SchoolInvertedSlopeBlock`, cuadrículas de formas | [create_school_inverted_slopes.py](../tools/create_school_inverted_slopes.py) / [check_school_inverted_slopes.py](../tools/check_school_inverted_slopes.py) |
| Escudo de suelo 4×4 | `SchoolShieldBlock` | [create_school_shield_floor_assets.py](../tools/create_school_shield_floor_assets.py) / [check_school_shield.py](../tools/check_school_shield.py) |
| Papelería/modelos padre | `folder`, `notebook`, 16 colores | [create_stationery_assets.py](../tools/create_stationery_assets.py); render con `blender -b --python tools/render_stationery_assets.py` |
| Uniforme equipado/ítem | `SchoolUniformItem`, equipment vanilla | [create_uniform_assets.py](../tools/create_uniform_assets.py); contrato en [ARMOR_SPECS](ARMOR_SPECS.md) |
| Skins/retratos de Student | `PlaceholderSchoolNpcModel`, `StudentAppearanceTextures` | [create_placeholder_student_skins.py](../tools/create_placeholder_student_skins.py), [create_student_appearance_assets.py](../tools/create_student_appearance_assets.py), [create_student_gender_skins.py](../tools/create_student_gender_skins.py) |

Puertas/cristal dinámico: [render_texture_review.py](../tools/render_texture_review.py) (`--vanilla-dir` para vanilla). OBJ: [render_school_inverted_slopes.py](../tools/render_school_inverted_slopes.py), [render_school_railing_slopes.py](../tools/render_school_railing_slopes.py). Papelería girada: [render_stationery_assets.py](../tools/render_stationery_assets.py), con Blender. Conserva el render específico cuando verifica apertura/conexiones; no escribas otro para un bloque nativo simple.

San Martín: edita la figura completa en `sculpture()` de su generador. Este fija las UV antes de ocultar caras y dividir las seis celdas; exporta los modelos completos de cada capa a `newresources/san_martin_de_porres_complete_{solid,translucent}.json`. El hábito y las partes crema usan texturas 16×32 con proyección continua. El recorte respeta el sentido de las UV de cada cara (`crop_face_uv`); no reasignes texturas completas a los fragmentos. Su check compara renders del conjunto ensamblado con el modelo entero; su render también muestra la figura sin vitrina para revisar el dorso.

## Rendimiento y compatibilidad

- Texturas pixeladas a la densidad de los materiales vecinos; evita aumentar resolución para disimular UV incorrectas. Al ampliar un atlas pixel art usa vecino más cercano. Luces y sombras de la escena las produce Minecraft.
- Variantes de color heredan geometría. No dupliques modelos, texturas o clases por orientación si una rotación/tabla sirve. No copies cientos de combinaciones que solo cambian color.
- Separa contorno sencillo y colisión exacta. Reutiliza `ExactVoxelShapes`, las cuadrículas existentes, `SharedChairRotations` y `SimpleBlockOutline` según el patrón existente. Cachés físicas por geometría/ocupación, no por colores; no uses una caché por estado para geometría dependiente de vecinos.
- Conserva propiedades físicas y guardadas. Conexiones puramente visuales pueden calcularse con vecinos cargados mediante predicados Fusion; no fuerces cargas de chunks ni añadas entidades/tickers para decoración estática.
- En vidrio, separa capas solid/cutout/translucent; el vidrio transparente no debe ocultar caras opacas visibles a través de él. Cullface solo donde la cara toca el límite correcto y la conexión lo permite.
- SchoolGlazing valida el perfil del bloque actual y los extremos/celdas después de recorrer la fila. Conserva el fallback de máscara segura ante estados de render obsoletos o cristales desaparecidos, incluido el minimapa de Xaero; no accedas a `pane()` sin perfil válido. Confirmación de ese caso en Minecraft/Xaero pendiente.
- `optimize_model_resources.py` prepara originales y `src/generated/resources` en `build/generated/optimizedResources`, antes de `processResources`. Edita **los fuentes**, nunca esa salida. Comparte duplicados, padres sólidos y selectores; `prepare_high_state_models.py` exige una hoja por modelo nativo en cada raíz.
- Fusion 1.3.16: conserva `neoforge:obj` para OBJ; declara materiales/particle en compuestos. El MTL de pavimento usa `map_Kd gray`/`yellow` sin `#`. Un `fusion:base` usado como componente debe tener geometría real válida; no uses bases vacías ni supongas soporte de matrices `transform` en compuestos.
- Reducir modelos o selectores no prueba una mejora de tiempo. Mide arranque/JFR si se pide rendimiento; no inventes segundos ahorrados.
