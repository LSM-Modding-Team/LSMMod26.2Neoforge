# Horneado de modelos: hojas únicas en Fusion

11 de octubre de 2026 (America/Lima). Grabación del usuario: startup.zip,
file_00000000ab08820eb1f0785072cad617. Análisis con JFR y profundidad 128.

## Evidencia de la grabación

1458 muestras ExecutionSample. Conteo inclusivo por método: cada método se
cuenta como máximo una vez por muestra. Los valores se solapan y no son segundos.

| Método / etapa | Muestras | Porcentaje del total |
|---|---:|---:|
| CompositeModelType.bakeBlockStateModel | 163 | 11,2 % |
| SimpleModelType.bakeBlockStateModel | 157 | 10,8 % |
| CuboidModelType.bakeGeometry | 100 | 6,9 % |
| CuboidModelGeometryImpl.bakeElement | 87 | 6,0 % |
| CuboidModelGeometryImpl.bakeFace | 75 | 5,1 % |
| FaceBakery.bakeQuad | 53 | 3,6 % |
| UnknownModelType.bakeGeometry (geometría no cuboide) | 25 | 1,7 % |

JFR no proporciona el identificador del modelo en estos eventos. No se puede
atribuir ese tiempo a bloques específicos solamente a partir de las pilas.
La tabla siguiente proviene de la inspección estática de los compuestos, no
representa una clasificación de CPU por bloque.

## Cambio aplicado

El árbol anterior partía la tabla de estados en mitades contiguas. Un mismo
modelo podía aparecer en varias hojas, aunque sus condiciones fueran excluyentes.
Fusion hornea cada hoja: sus SimpleModelType y ModelEntryModelInstance no
reutilizan automáticamente ese horneado condicionado.

El generador ahora agrupa los índices por identificador nativo y divide el
conjunto de modelos distintos. Cada raíz tiene una sola hoja por modelo.
ModelSelectionPredicate admite intervalos disjuntos ordenados y los consulta
con búsqueda binaria. Se mantiene la selección por índice mixto, los selectores
anteriores min/max, el índice 0 para ítems y el refresco visual del nodo raíz.
No se añaden cachés de sprites ni se interviene en las recargas de recursos.

| Familia | Hojas antes | Hojas después |
|---|---:|---:|
| elementary_playground_awning | 578 | 578 |
| playground_awning | 577 | 347 |
| Cada una de las seis pendientes de pavimento | 256 | 256 |
| school_entrance_pavement_slab | 192 | 192 |
| school_gate | 87 | 57 |
| Cada uno de los cuatro cristales con cortinas | 84 | 54 |
| football_basketball_goal | 81 | 38 |
| double_school_entrance | 29 | 23 |
| Total de todas las raíces preparadas | 3552 | 3123 |

Los conteos suman la estructura de cada archivo compuesto una vez; no cuentan
las orientaciones adicionales que el cargador puede hornear. Son 429 hojas
repetidas eliminadas (12,1 %); la mejora temporal requiere otra grabación.
Los modelos originales, OBJ, cubos, UV, texturas y estados físicos se conservan.
Las raíces de pavimento mantienen las declaraciones gray/yellow/particle.
La cantidad de archivos preparados sigue siendo 3416: reducir archivos y
reducir geometría horneada son operaciones distintas.

## Verificación y pendientes

check_optimized_model_resources.py pasa: todos los modelos originales resuelven
igual, 15344 combinaciones de las familias intervenidas, 1477 selectores de otras
familias y referencias de ítems intactos. El verificador rechaza hojas repetidas
en cualquier raíz preparada. No se compiló ni ejecutó Minecraft, por instrucción
vigente del usuario; resta verificar Java y medir otra JFR.

Los modelos que siguen siendo distintos necesitan otra estrategia: compartir
componentes geométricos reales (toldos) y separar superficies/bordes (pavimento).
Eso exige pruebas de UV, materiales y conexiones; esta fase no sustituye sus
geometrías por aproximaciones ni elimina variantes visuales.
