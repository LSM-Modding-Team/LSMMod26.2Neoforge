# Doce familias de escaleras de cristal

Se completan las seis parejas de materiales en ambos sentidos, conservando las cuatro familias anteriores y añadiendo ocho bloques en **LSM Mod School Blocks**. El primer material del ID/nombre va exclusivamente en el rectángulo frontal bajo el vidrio; el segundo cubre el resto de la media losa. `white` corresponde a `classroom_white_wall`.

| Frente | Resto | ID nuevo |
|---|---|---|
| Blanco de aula | Oscuro | white_dark_school_glass |
| Blanco de aula | Calle | white_street_school_glass |
| Oscuro | Blanco de aula | dark_white_school_glass |
| Oscuro | Calle | dark_street_school_glass |
| Claro | Blanco de aula | light_white_school_glass |
| Claro | Oscuro | light_dark_school_glass |
| Claro | Calle | light_street_school_glass |
| Calle | Claro | street_light_school_glass |

Combinaciones existentes: blanco/claro (`mixed_school_glass`), calle/blanco (`mixed_school_glass_reversed`), calle/oscuro (`supported_school_glass`), oscuro/claro (`supported_school_glass_reversed`). No se reemplazan registros ni cambia su aspecto.

## Geometría y juego

Todas reutilizan `SchoolGlassStairBlock`: orientación, esquinas internas/externas, mitad invertida, agua, cristal conectado y cortinas. La lógica del cristal y de las cortinas usa el tipo de bloque, así que reconoce las nuevas familias sin listas de IDs adicionales. El rectángulo frontal respeta las mitades y esquinas existentes. Registros, ítems, loot, traducciones y etiqueta de pico incluidos.

## Recursos y rendimiento

- Los modelos nativos nuevos son hijos de los modelos de `mixed_school_glass`, con dos referencias de textura sustituidas. No copian geometría, UV, cristal o marcos ni crean texturas PNG.
- Cada familia añade 97 archivos pequeños de herencia nativa y diez selectores Fusion de conexiones. Las cortinas y la geometría original se comparten.
- En cada selector Fusion se agrupan los masks que usan el mismo modelo. Las ocho familias requieren **768 referencias nativas únicas**, frente a 2 560 entradas si se repitiera la geometría por cada máscara. Selección equivalente para las 32 máscaras de cada forma/mitad.
- `SchoolGlazingPredicate` admite `masks` además del `mask` anterior. Mantiene la selección por hilo, refrescada en el primer grupo (contiene mask 0), y el aspecto de inventario para mask 3. La pertenencia es un bitset de 32 bits; incluye correctamente el bit 31.
- Las nuevas familias entran en `prepare_high_state_models`, conservando la optimización de cortinas: 464 selectores de origen → 64 preparados por familia. Conexiones visuales seleccionadas por el mundo, sin recuperar propiedades de conexión en Java. Los estados físicos y las 17 disposiciones válidas de cortina siguen siendo los de las familias existentes.
- Colisiones vanilla compartidas y contorno exterior sencillo; sin nueva clase de bloque, entidades, ticker permanente ni uniones de formas por consulta.
- Gradle sigue los cambios del catálogo de familias como entrada del optimizador.

Generación: `python tools/create_school_glass_material_families.py`. Ejecutar después de regenerar las familias base si cambian sus recursos de conexión.

## Verificación

`check_school_glass_material_families.py`: doce parejas exactas, 2 560 elecciones de máscara, herencia de geometría, rectángulo frontal, conexiones/cortinas, registros y regeneración idempotente. `check_school_glass.py`, `check_compact_school_glazing.py` y `check_optimized_model_resources.py`: familias anteriores y 26 224 selecciones de modelos preparados conservadas. No se compiló Java/Gradle ni se abrió Minecraft; el renderizado debe comprobarse dentro del juego.
