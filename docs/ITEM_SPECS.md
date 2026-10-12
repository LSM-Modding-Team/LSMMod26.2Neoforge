# Objetos escolares

Reglas, pelotas e instrumentos tienen lógica; folder/cuaderno son decorativos. Los efectos y legendarios de diseño siguen pendientes salvo indicación expresa.

Sin marca = decidido; *(propuesta)* = ajustable; *(a confirmar)* = supuesto. Datos sin definir e IDs P/V/D/A: [DESIGN](DESIGN.md).

## Armas

| Item | Descripción |
|---|---|
| **Regla** | Arma de los profesores. Mucho rango y ataque en área como la espada. **Hidalgo no la usa** (tiene su propio ataque: Expulsión). |
| **Lápiz / lapicero** | Armas leves. |
| **Escoba / trapeador** | Armas de limpieza; las usa el personal de limpieza. |
| **Silla arrojable** | Gran daño; **rompe el bloque donde cae**. Cómo se obtiene y su cooldown: sin definir (V17). |
| **Pelotas** (plástica, fútbol, básquet, vóley) | Armas a distancia; como item se lanzan. |

**Tiers de la regla** (implementados, pendientes de prueba). Daño total sin encantamientos, incluyendo el punto base del jugador:

| Tier / ID | Material vanilla | Daño | Durabilidad | Encantabilidad |
|---|---|---:|---:|---:|
| Madera / `wooden_ruler` | WOOD | 3.5 | 59 | 15 |
| Piedra / `stone_ruler` | STONE | 4.5 | 131 | 5 |
| Oro / `golden_ruler` | GOLD | 3.5 | 32 | 22 |
| Hierro / `iron_ruler` | IRON | 5.5 | 250 | 14 |
| Diamante / `diamond_ruler` | DIAMOND | 6.5 | 1561 | 10 |
| Netherite / `netherite_ruler` | NETHERITE | 7.5 | 2031 | 15 |

Todas: daño 0.5 menor que la espada equivalente, velocidad 1.6 ataques/s, alcance de entidades +1.5 bloques en la mano principal y barrido vanilla. Reparación y comportamiento de herramienta se heredan de `ToolMaterial`; netherite resiste fuego. Fuente de tiers para recursos: `tools/ruler_tiers.json`.

## Pelotas (item, juguete y proyectil)

Gravedad, aire, rodadura, rebotes y giro visible propios de cada tipo. Clic derecho sobre un bloque coloca el juguete; en el aire lanza; sobre la entidad recoge una unidad si cabe en el inventario. Se mantiene el mismo ítem y entidad al impactar. Apilado de 16.

| Pelota / ID | Daño cercano fácil / normal / difícil | Daño máximo fácil / normal / difícil | Velocidad inicial proyectil (bloques/s) | Impulso juguete (bloques/s) | Movimiento |
|---|---|---|---:|---:|---|
| Plástico / `plastic_ball` | 9 / 13.5 / 18 | 18 / 27 / 36 | 16 | 5 | Ligera; saltos pequeños, mayor frenado y rebotes cortos. |
| Fútbol / `football_ball` | 15 / 22.5 / 30 | 30 / 45 / 60 | 24 | 8 | Rodadura larga, salto inicial bajo y rebotes intermedios. |
| Básquet / `basketball_ball` | 20 / 30 / 40 | 40 / 60 / 80 | 14 | 4 | Mayor gravedad, saltos altos y rebotes que conservan el 88 % de la velocidad vertical. |
| Vóley / `volleyball_ball` | 13 / 19.5 / 26 | 26 / 39 / 52 | 28 | 10 | Arcos altos y largos; golpe en el aire orientado por la mirada, incluso en vertical. |

Daño = daño cercano × (1 + min(distancia recorrida / 32, 1)). La distancia sigue la trayectoria real del proyectil antes del primer impacto, no la separación actual respecto al lanzador; se guarda al cerrar el mundo. El máximo se alcanza a los 32 bloques recorridos. Son valores de daño bruto antes de armadura y otras reducciones. Pacífico: proyectiles 0. Los juguetes de vóley mantienen 2 de daño por choque en el aire en todas las dificultades.

Las velocidades de la tabla son iniciales: la gravedad curva la trayectoria y el aire y el suelo frenan la pelota. Los impactos transforman el proyectil en juguete conservando impulso y rebote; sin impacto, cambia a juguete tras 20 segundos. Física exagerada para que los tipos se distingan, basada en rodadura y rebotes con pérdida de energía. Modelos y texturas conservados.

## Pacificar y enojar

**Arte implementado:** folder oficio de tapa dura sin liga, inspirado en ARTESCO; cuaderno college de tapa flexible fina, color sólido, lomo encolado y sin espiral, inspirado en Bakan/Stanford. Dos modelos padre (`folder`, `notebook`), 16 variantes por tipo (`<color>_folder`, `<color>_notebook`) y atlas de tapas compartidos entre ambos. Hojas y etiquetas usan un atlas independiente para conservar su color. Items decorativos registrados en la pestaña LSM Mod; recetas, consumo y Estudioso siguen pendientes. Regenerar con `python tools/create_stationery_assets.py`; preview con `blender -b --python tools/render_stationery_assets.py`.

Dirección de cada uno y quién queda afectado: [MECHANICS_SPECS](MECHANICS_SPECS.md). Aquí los valores.

| Item | Efecto en profesores | Efecto en alumnos | Notas |
|---|---|---|---|
| **Bola de papel** | Los enoja | Aturde ~3 s | Los alumnos la lanzan en masa como ataque a distancia. |
| **Folder** | Los apacigua (**a todos**); te da **Estudioso 30 s** | — | Consumible, un solo uso. *(V12, resuelto)* |
| **Cuaderno** (de colores) | Apacigua **solo a profesores de primaria**; **Estudioso 20 s solo para ellos** | — | Consumible, un solo uso. *(V12, resuelto)* |
| **Celular** | "Enoja a todos" (V11) | Apacigua al alumno enojado | |
| **Parlante portátil** | Los enoja | Apacigua a todos | Reproduce cualquier canción (cuáles: V17). |
| **Sticker** | **Todos** los profesores atacan a quien lo lleve | — | Se pone en polos de alumnos o del jugador. |
| **Guitarra eléctrica** (legendario) | Apacigua | Apacigua | 10 s a todos. Dónde se obtiene: sin definir (P3). |

El Estudioso del cuaderno **no** es un efecto distinto: es el mismo efecto con alcance reducido (solo profesores de primaria). Cómo se marca ese alcance en el efecto (un nivel, dos efectos, un dato del item) se define al implementar el efecto. Qué profesor es "de primaria": V13.

## Instrumentos

**Implementado: música y combate; pendiente de compilación y prueba.** Clic derecho conserva los sonidos existentes. Música y ataque comparten el cooldown vanilla del mismo instrumento, sincronizado al cliente y visible como la superposición gris del inventario. Tocar o acertar un golpe activa ese mismo temporizador y bloquea ambas acciones hasta que termine. La música no consume durabilidad. Clic izquierdo permite atacar con estos valores (daño total, contando el punto base del jugador):

| Instrumento | Daño | Cooldown entre golpes | Durabilidad en golpes |
|---|---:|---:|---:|
| Guitarra | 10 | 16 s / 320 ticks | 8 |
| Mandolina | 10 | 16 s / 320 ticks | 8 |
| Violín | 10 | 16 s / 320 ticks | 8 |
| Flauta | 2 | 8 s / 160 ticks | 16 |
| Pandereta | 2 | 8 s / 160 ticks | 16 |
| Melódica | 2 | 8 s / 160 ticks | 16 |

El impacto reproduce `minecraft:item.mace.smash_ground`. Cada golpe aceptado sobre una entidad viva consume un punto de durabilidad e inicia el cooldown de combate. El cooldown pertenece al tipo de instrumento: copias del mismo item comparten el indicador y el bloqueo de ataque/música. No añade barrido. El daño indicado corresponde a un ataque normal cargado, antes de armadura, críticos u otros modificadores vanilla.

**Diseño pendiente de efectos musicales:** los valores y buffs de la tabla siguiente siguen sin implementarse.

| Instrumento | Duración al usarlo el jugador | Notas |
|---|---|---|
| Guitarra | 6 s | |
| Flauta | 8 s | De primaria. |
| Mandolina | 10 s | |
| Violín | 14 s | **Más raro.** |
| Pandereta | 6 s | |

* **Uso del jugador:** pacifican a los NPC en un radio de **10 bloques**. Cooldown de 60 s *(propuesta)*.
* **Uso de NPC** (estudiantina y otros): buffean **+20 %** a alumnos y profesores en **15 bloques** durante **20 s** y llaman a 2-3 más *(propuesta)*.
* **Pendiente (P5):** la reacción exacta por tipo de profesor o de alumno.

## Consumibles y utilidad

| Item | Efecto | Datos que faltan |
|---|---|---|
| **Lonchera** | Te da 5 muslos; se recarga con comida. Sale del kiosko. | Cómo se recarga (V17); qué se paga en el kiosko (V18) |
| **Medalla de festidanza** | Multiplica la velocidad actual. | Factor y duración (V17) |
| **Medalla de olimpiadas** | Multiplica el daño. | Factor y duración (V17) |
| **Diploma** | Otorga más corazones. | Cuántos (V17) |
| **Huevo podrido** | Veneno fuerte y náusea en un área cercana. Aparece en algunos casilleros: afecta a quien lo abre y a los cercanos. | Radio y duración (V17) |
| **Papel higiénico** | No hace nada. Hay **uno por salón**. | — |
| **Libros** | Solo existen de **2do a 5to**. Incluyen la oración sanmartiniana. | Uso (¿se leen?): sin definir |

## Legendarios

| Item | Descripción | Dónde sale |
|---|---|---|
| **Ardilla PUCP** | Inmunidad ante los salones de 4to y 5to y ante Hidalgo; esos salones pasan a ser tus aliados. | Solo en casilleros de 4to y 5to, "exactamente una" (V22) |
| **Guitarra eléctrica** | Apacigua a todos 10 s. | Sin definir (P3) |
| **Mapa al ataúd** | Indica dónde está el ataúd de Manuel Tirado. | Drop del Director |
| **Alma de Manuel Tirado** | Parte del ritual. | Drop del fantasma de Manuel |
| **Traje de Manuel Tirado** | Pacifismo total. Ver [ARMOR_SPECS](ARMOR_SPECS.md). | Drop del boss |

Los otros dos legendarios del diseño son **bloques**: el ataúd de Manuel Tirado y San Martín de Porres ([BLOCK_SPECS](BLOCK_SPECS.md)).

## Discos y música (diseño)

* Las **laptops reproducen discos** (música custom, p. ej. "nene malo", "bby wow"); los discos se obtienen como **loot**.
* Laptop común: radio de 6 bloques *(propuesta)*. Laptop de Moisés: toda la estructura, click derecho alterna himno y marcha del colegio. Detalle del bloque: [BLOCK_SPECS](BLOCK_SPECS.md).
* Sin definir (V17): cómo se carga un disco en la laptop, de qué loot sale cada disco, la lista completa de canciones, y si el himno y la marcha son discos o sonidos fijos de la laptop de Moisés.
* El parlante portátil también "reproduce cualquier canción".

Papelería: folder oficio cerrado 10×14 px, cuaderno de tapa fina al ras del papel/lomo; fichas gráficas sin palabras. 16 colores por tipo y tapas compartidas. Generador y render: [ASSETS](ASSETS.md).
