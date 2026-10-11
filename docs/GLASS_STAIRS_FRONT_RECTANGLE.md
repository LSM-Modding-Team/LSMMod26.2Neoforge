# Material inferior limitado al rectángulo frontal

Las cuatro familias `supported_school_glass`, `mixed_school_glass` y sus versiones `_reversed` ahora muestran el material que estaba bajo el vidrio exclusivamente en el rectángulo frontal de la media losa. El usuario confirmó que laterales, base y resto del escalón deben usar el segundo material de cada variante.

El frente es norte en el modelo local y rota con el estado `facing`. En una escalera normal el rectángulo ocupa y=0..8; invertida y=8..16. En esquinas exteriores el ancho depende del cuarto ocupado; en las interiores se conserva el ancho de la franja frontal bajo el vidrio. La regla también se aplica a todas las variantes de conexión horizontal y vertical.

Asignaciones frontal/resto:

| Familia | Rectángulo frontal | Resto de media losa |
|---|---|---|
| supported_school_glass | street_school_wall | dark_school_wall |
| supported_school_glass_reversed | dark_school_wall | light_school_wall |
| mixed_school_glass | classroom_white_wall | light_school_wall |
| mixed_school_glass_reversed | street_school_wall | classroom_white_wall |

Se corrigieron 2 016 caras en 392 modelos existentes. Los otros cuatro modelos de legado ya cumplían la regla. No se cambiaron geometría, UV, cullfaces, vidrios/marcos, colisiones, cantidades de estados/modelos, cortinas ni predicados Fusion. El generador base incluye `restrict_lower_material`; los derivados horizontales conservan esas superficies corregidas.

`python tools/restrict_glass_stair_materials.py` repara recursos existentes de manera idempotente sin reconstruir sus blockstates compactos. `python tools/check_glass_stair_front_materials.py` valida los 396 modelos y todos los anchos/mitades/conexiones. También pasaron `check_school_glass.py` y `check_optimized_model_resources.py`. Comparación exacta con los modelos previos: solo cambiaron las asignaciones de material autorizadas. El generador reproduce los 164 modelos base corregidos. Sin compilación Java/Gradle ni Minecraft.
