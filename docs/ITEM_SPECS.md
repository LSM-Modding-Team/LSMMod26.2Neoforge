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
| Fútbol / `football_ball` | 15 / 22.5 / 30 | 30 / 45 / 60 | 24 | Regate; X: 4–30 | Conducción con toques; tiros cargados, efecto y rebotes intermedios. |
| Básquet / `basketball_ball` | 20 / 30 / 40 | 40 / 60 / 80 | 14 | 4 | Mayor gravedad, saltos altos y rebotes que conservan el 88 % de la velocidad vertical. |
| Vóley / `volleyball_ball` | 13 / 19.5 / 26 | 26 / 39 / 52 | 28 | 10 | Arcos altos y largos; golpe en el aire orientado por la mirada, incluso en vertical. |

Daño = daño cercano × (1 + min(distancia recorrida / 32, 1)). La distancia sigue la trayectoria real del proyectil antes del primer impacto, no la separación actual respecto al lanzador; se guarda al cerrar el mundo. El máximo se alcanza a los 32 bloques recorridos. Son valores de daño bruto antes de armadura y otras reducciones. Pacífico: proyectiles 0. Los juguetes de vóley mantienen 2 de daño por choque en el aire en todas las dificultades.

Las velocidades de la tabla son iniciales: la gravedad curva la trayectoria y el aire y el suelo frenan la pelota. Los impactos transforman el proyectil en juguete conservando impulso y rebote; sin impacto, cambia a juguete tras 20 segundos. Física exagerada para que los tipos se distingan, basada en rodadura y rebotes con pérdida de energía. Modelos y texturas conservados.

### Fútbol: conducción y tiros

En el suelo, acercarse permite controlar una pelota lenta con toques suaves en la dirección del movimiento; el contacto y el ataque normal ya no aplican un puntapié fijo. Caminar mantiene el balón cerca, esprintar lo adelanta y agacharse da control fino. Detenerse amortigua la pelota; giros bruscos, obstáculos y otro jugador mejor colocado pueden hacer perder el control. La pelota sigue siendo una entidad física libre, sin teletransportarse ni atravesar paredes. Un balón rápido no se captura automáticamente.

**X** (reasignable en Controles → Fútbol LSM) carga el disparo **aunque no haya balón**. La carga pertenece al jugador; al soltar se busca un balón alcanzable en ese instante. Si no hay ninguno, no hay golpe, recuperación del tiro ni acción pendiente que se dispare al llegar otro balón. La potencia aumenta hasta **24 ticks / 1,2 s** y queda limitada al máximo sin disparar automáticamente: un toque breve sirve de pase. Al soltar, la mira selecciona un objetivo hasta 64 bloques, limitado por los bloques que encuentre; la dirección inicial se calcula **desde el centro del balón hasta ese objetivo**, corrigiendo tanto altura como desplazamiento lateral. En un golpe desde el suelo, un objetivo por debajo del balón produce un pase raso; las voleas y los cabezazos pueden dirigirse hacia abajo. La gravedad afecta la trayectoria. Puede conducirse y saltar mientras se carga; perder el balón no cancela la preparación. Abrir una pantalla, perder el foco, desconectar, cambiar de dimensión o dejar de ser un jugador válido cancela la carga. Una barra en el mensaje sobre la barra rápida indica potencia y efecto. Los tiros con X no hacen daño; lanzar el ítem desde el inventario conserva el daño de la tabla.

Una pelota frontal que llega a la altura del torso se amortigua automáticamente con el **pecho**, incluso mientras cargas X si aún no has disparado: pierde casi toda su velocidad horizontal y cae delante del jugador, sin teletransportarse. Al preparar y soltar X se intenta un **tiro en suelo**, una **volea** por debajo de la cabeza o un **cabezazo** a la altura de la cabeza, según la altura del centro del balón respecto a la postura del jugador. Los contactos aéreos requieren estar a menos de un bloque horizontal y dentro de la altura alcanzable; las paredes bloquean el contacto. Los cabezazos tienen 70 % de la velocidad de una volea de la misma carga y pueden dirigirse hacia abajo. Saltar permite elevar la zona de contacto. Los impactos se procesan en los pasos cortos de movimiento para amortiguar también pases rápidos; un control de pecho válido convierte una pelota lanzada en pelota deportiva antes del daño. Un balón que llega por detrás o fuera de la zona del pecho no se amortigua automáticamente.

Los tiros desde el suelo añaden hasta **6°** de elevación sobre la dirección calculada desde el balón. La ayuda disminuye suavemente al apuntar casi al ras y desaparece al seleccionar un objetivo a la altura del centro del balón o más abajo: siguen siendo posibles los pases y tiros rasos. Las voleas y los cabezazos conservan la dirección aérea apuntada. La dirección se mantiene normalizada para que elevar el tiro no añada velocidad.

Solo al soltar X con un balón alcanzable, se suma a la barra preparada **0,5 × velocidad relativa / 0,28**, y la barra total se limita al **100 %**. Una carrera de 0,28 bloques/tick hacia un balón quieto añade **50 puntos porcentuales**, independientemente de cuánto X se haya cargado: por ejemplo, 25 % pasa a 75 %, y 75 % alcanza el límite de 100 %. Si el balón viene hacia el jugador, la velocidad relativa puede añadir más de 50 puntos; no hay un límite separado de 50 puntos para ese caso. La velocidad inicial se calcula después de sumar: **0,20 + 1,30 × barra total**, en bloques/tick. Se usa la velocidad del balón de **7 ticks / 350 ms** antes del golpeo, tomada antes de los toques de regate; si acaba de aparecer, se usa su muestra disponible más antigua. La velocidad relativa es la mayor norma de los tres componentes de la velocidad del jugador respecto a esa muestra del balón, medida durante los últimos 7 ticks de preparación y al soltar. Así, un toque automático justo antes del tiro no borra el impulso, y la desaceleración de cargar no elimina de inmediato una carrera reciente. Conducir a una velocidad parecida durante más de esa ventana aporta poco. Las muestras caducan y no se guardan al descargar el mundo.

El resultado conserva el límite de **1,5 bloques/tick / 30 bloques/s**; los cabezazos conservan su factor de 70 %. A carga máxima ya no queda margen para aumentar la velocidad. No se aplica este impulso al mero contacto ni al regate. Durante la preparación, la barra muestra la carga de X; al disparar, el mensaje muestra el porcentaje final de barra tras sumar el contacto y aplicar el factor del tipo de golpeo. La fuerza física que amplifica la curva sigue calculándose a partir de la velocidad inicial resultante. Para comparar en juego, usa una carga breve similar al estar quieto y al llegar corriendo; una carga máxima oculta la bonificación porque ya alcanza el límite.

El umbral de renderizado por distancia del balón de fútbol se **duplica**, y su seguimiento pasa de **8 a 16 chunks**; la distancia visible efectiva depende también de los ajustes de entidades/chunks del cliente. Las demás pelotas conservan sus distancias. X es el valor predeterminado nuevo: una asignación guardada previamente como K u otra tecla se conserva; puede restablecerse en Controles → Fútbol LSM.

Cargar reduce progresivamente la velocidad de movimiento: **65 %** de la velocidad habitual al empezar, **12 %** con carga máxima, siguiendo una curva cuadrática. Tras disparar, la velocidad se recupera gradualmente durante **2–7 ticks / 100–350 ms**, según la carga alcanzada. La reducción se aplica como modificador temporal independiente, conservando los demás modificadores y restaurándose al cancelar, soltar sin balón, desconectar o cambiar de dimensión. La carga sigue activa aunque no haya balones cargados en el mundo.

Las teclas de movimiento asignadas aportan efecto **durante la carga**, al soltar X y durante los **3 ticks / 150 ms** posteriores: A curva a la izquierda, D a la derecha, W añade sustentación y S caída. Mantenerlas más tiempo acumula más giro; los ticks cerca de carga máxima acumulan más de cuatro veces lo que acumula un tick a media carga, y la curvatura se amplifica de forma cúbica con la potencia. La preparación aporta hasta **70 %** del giro, el instante de soltar **10 %**, y los tres ticks posteriores **8 %, 7 %, 5 %**. Las referencias son las del disparo, aunque después se gire la cámara. Entradas opuestas se cancelan y las diagonales normalizan el giro total. Terminado el golpeo, las teclas dejan de afectar la pelota. El giro y la potencia pierden efecto con el aire, la rodadura y los impactos; giro y carga del tiro se guardan junto con el balón. La posesión, la carga activa y la ventana de entrada son transitorias.

Además de la carga y la duración de las teclas, la curvatura lateral y la sustentación/caída se multiplican por **0,60 + 1,60 × fuerza inicial relativa al máximo**. A fuerza máxima, la curva lateral alcanza **2,2 veces** el valor que tenía con el multiplicador anterior; los golpes débiles curvan bastante menos que los fuertes. La sustentación/caída queda limitada a **0,035 bloques/tick por tick**, por debajo de la gravedad de fútbol (0,05), para que el backspin no haga flotar el balón indefinidamente. Se conserva el papel de la carga alta y no se genera efecto si no se aportó giro. La fuerza inicial se guarda como `ShotStrength`, con un valor compatible derivado de `ShotCharge` para balones guardados antes de este cambio.

La **red del arco** amortigua las pelotas con su geometría real (laterales, espalda inclinada y techo): conserva **2 %** de la velocidad normal y **10 %** de la tangencial, eliminando gran parte de la energía del impacto. El material se comprueba en la zona central de la cara que golpea, con las mismas rotaciones físicas que el arco; que el borde inferior del balón roce la barra del suelo ya no convierte un tiro raso contra la red en un rebote rígido. Los impactos directos contra postes, travesaño y soporte de básquet conservan el rebote rígido. La amortiguación se aplica a todos los tipos de pelota y se sincroniza al cliente al impactar.

El balón de fútbol reproduce cuatro sonidos posicionales distintos, recortados de grabaciones reales de fútbol: patadas a un balón de Joseph Sardin / BigSoundBank, balón contra pared de 1skyland, balón contra poste de 14GPanskaValaChristoffer y red grabada con micrófono piezoeléctrico de Luisa_Sanchez. El sonido de pared también se utiliza para el suelo rígido. El volumen depende de la fuerza del golpe o de la velocidad normal del impacto; los roces por debajo de 0,12 bloques/tick no suenan. Cada material tiene una pausa de 3 ticks para evitar repetir el mismo impacto en los subpasos, permitiendo escuchar poste y red en una misma jugada. Los sonidos se emiten desde el servidor para los jugadores cercanos y tienen subtítulos en español e inglés. Los cuatro Ogg Vorbis mono están en `assets/lsmmod/sounds/football/`; las fuentes, licencias, URLs públicas y recortes exactos se incluyen en su `CREDITS.txt`.

Revisión estática y comprobaciones de reglas/geometría; compilación del mod y prueba en Minecraft pendientes.

## Pacificar y enojar

`portable_speaker`: item 3D cilíndrico rojo, no apilable. Clic derecho reproduce
uno de once fragmentos finales Ogg Vorbis mono, con volumen de discos. Cada
parlante conserva en `CUSTOM_DATA` su identidad y un orden aleatorio completo;
al terminar el ciclo se sortea otro, sin repetición inmediata entre ciclos.
Cortes/duraciones: `tools/portable_speaker_tracks.json`. Generador de modelo/texturas:
`tools/create_portable_speaker_assets.py`.

El audio sigue al stack real en inventario/cursor, suelo, inventarios de entidades,
contenedores de bloques cargados y contenedores portátiles anidados (componentes
container/bundle o capacidades NeoForge). Trasladarlo actualiza la posición sin
reiniciar el stream. Cooldown por duración y bloqueo por identidad evitan reproducir
dos canciones desde el mismo parlante, incluso al transferirlo. No hay autoplay.
No carga chunks por música; desaparecido o descargado el stack, detiene el sonido
tras cinco ticks de tolerancia. Profesores investigan la fuente: [SCHOOL_NPCS](SCHOOL_NPCS.md).
Apaciguar alumnos, canciones externas y receta siguen pendientes. La versión de
esta conversación compiló antes de integrar master; integración actual revisada
estáticamente, pendiente de compilación y prueba en Minecraft.

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

## Discos y música

Implementado: los discos escolares se reproducen en la gramola y aparecen en la pestaña de objetos, después del himno y la marcha, en orden de 5to a 1ro de Secundaria. Los siete discos de esta colección tienen etiquetas de vinilo únicas de 16×16 px, pila máxima de uno y rareza rara; el himno y la marcha del colegio conservan sus texturas. Generador: `tools/create_music_disc_assets.py`; revisión externa del ítem generado: `tools/render_music_discs.py`. Los cinco Ogg originales se conservan completos; la duración de la canción sigue el archivo, sin recortes, silencios ni cambios de tempo.

| Salón | Disco (ID) | Duración (s) |
|---|---|---|
| 5to | `qapackachampa` | 584.771338 |
| 4to | `cofradia_san_miguel` | 503.571156 |
| 3ro | `carnaval_huaccana` | 573.393560 |
| 2do | `carnaval_huanupampa` | 594.012880 |
| 1ro | `carnaval_macari` | 540.467664 |

También están implementados `formacion` (129.985306 s) e `himnoperu` (159.497868 s), después de los cinco discos de Secundaria. Se convirtieron íntegros desde los MP3 suministrados a Ogg Vorbis mono, sin recortes ni cambios de tempo.

Diseño pendiente para laptops y loot:

* Las **laptops reproducen discos** (música custom, p. ej. "nene malo", "bby wow"); los discos se obtienen como **loot**.
* Laptop común: radio de 6 bloques *(propuesta)*. Laptop de Moisés: toda la estructura, click derecho alterna himno y marcha del colegio. Detalle del bloque: [BLOCK_SPECS](BLOCK_SPECS.md).
* Sin definir (V17): cómo se carga un disco en la laptop, de qué loot sale cada disco, la lista completa de canciones, y si el himno y la marcha son discos o sonidos fijos de la laptop de Moisés.
* El parlante portátil también "reproduce cualquier canción".

Papelería: folder oficio cerrado 10×14 px, cuaderno de tapa fina al ras del papel/lomo; fichas gráficas sin palabras. 16 colores por tipo y tapas compartidas. Generador y render: [ASSETS](ASSETS.md).
