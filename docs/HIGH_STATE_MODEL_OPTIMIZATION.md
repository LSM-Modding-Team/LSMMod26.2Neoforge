# Optimización de los bloques con 256 estados o más

Fusion no es necesario para cada bloque. Aquí se aplica a los 19 IDs que tenían al menos 256 estados después de optimizar las cinco puertas, y al pavimento base que comparte la lógica de sus variantes.

El total pasa de **17149 a 14844 estados Java**, conservando los 116 bloques. Las propiedades visuales de bordes del pavimento se calculan desde los vecinos cargados; las fases de las rejas se calculan desde su posición en el grupo 4×3. No se añaden entidades de bloque ni cargas de chunks.

| Familia | Estados antes por ID | Estados después por ID |
|---|---:|---:|
| Dos toldos | 2312 | 2312 |
| Cuatro escaleras de cristal | 1360 | 1360 |
| awning_support | 1024 | 1024 |
| school_gate | 768 | 768 |
| football_basketball_goal | 576 | 576 |
| school_entrance_pavement_slab | 384 | 6 |
| street_school_bars | 384 | 32 |
| double_school_entrance | 288 | 288 |
| light_school_wall_railing | 256 | 256 |
| Seis pendientes de pavimento | 256 | 4 |
| Pavimento base adicional | 64 | 1 |

Los estados conservados representan orientación, apertura, agua, celdas de estructuras, alturas, colisiones o disposiciones de cortinas. Borrarlos requeriría otra representación física y de persistencia. Todos los 19 objetivos reciben una selección de modelos más compacta, aunque algunos sigan superando el umbral.

## Recursos compartidos

`prepare_high_state_models.py` convierte tablas de variantes en árboles de decisiones equilibrados de Fusion. Sus hojas siguen apuntando a los modelos nativos, incluidos los OBJ de NeoForge. La geometría, UV, texturas y rotaciones no se redibujan. Las condiciones de componentes simultáneos de barandas conservan sus conexiones y poste físico.

Los 19 objetivos pasan de **10791 a 315 entradas de selección**. Incluyendo el pavimento base: **10855 → 316**. Estos son selectores de recursos, no estados Java ni medidas de tiempo. Se generan 67 modelos auxiliares compartidos en los recursos preparados. Los JSON fuente y generadores se conservan; `processResources` utiliza exclusivamente el directorio preparado y no empaqueta las tablas originales.

Los predicados calculan un índice mixto por combinación. Para superficies visuales se reutiliza el cálculo dentro del árbol y se renueva al iniciar la evaluación; el caché por hilo conserva una referencia débil al mundo. Las rejas actualizan el grupo conectado cuando cambia su fase, compartiendo el recorrido por mundo y tick para evitar repetirlo por cada bloque.

## Mundos existentes y verificación

La migración NBT elimina únicamente las seis propiedades visuales retiradas del pavimento y `column`/`row` de las rejas. Se conservan propiedades físicas, índices y datos de las paletas. Las notificaciones al cliente invalidan las secciones vecinas para actualizar las conexiones calculadas.

Comprobaciones estáticas realizadas:

- Equivalencia exhaustiva de 15344 combinaciones de estado/perfil visual para los objetivos y el pavimento base.
- Equivalencia de geometría, UV, texturas y transformaciones de los 3723 modelos fuente; referencias de ítems y otros 1477 selectores conservados.
- Regresiones de puertas, cortinas, cristales, toldos, soporte, portón, arco deportivo, barandas, pavimento y rejas.

No se compiló ni ejecutó Minecraft, conforme a la instrucción vigente. El arranque de 130 segundos corresponde a una fase anterior. Es necesario medir este cambio con la misma configuración; reducir selectores o estados no garantiza una reducción proporcional de segundos.

## Corrección de las texturas del pavimento

El usuario informó texturas ausentes en el slab y las pendientes después de esta conversión. Los modelos compuestos de pavimento ahora declaran explícitamente `gray`, `yellow` y `particle`, usados por las referencias MTL de los OBJ nativos. Esto hace explícita la disponibilidad de sus materiales en la raíz Fusion. La regresión comprueba las declaraciones y la existencia de los PNG, además de conservar la equivalencia de selección de geometría. No se ejecutó el juego: la resolución del fallo necesita confirmación dentro de Minecraft; el log solicitado puede revelar otra causa.
