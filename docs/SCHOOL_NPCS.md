# Student y Teacher

Reemplazan la entidad de laboratorio `test_npc`. Tipos fijos: `lsmmod:student` y `lsmmod:teacher`; no se guardan ni usan los flags `isStudent`/`isTeacher`. Implementación pendiente de compilación y prueba en Minecraft.

## Invocación y NBT

```mcfunction
/summon lsmmod:student ~ ~ ~ {StudentData:{Level:"primary",Grade:1}}
/summon lsmmod:student ~ ~ ~ {StudentData:{Level:"secondary",Grade:5}}
/summon lsmmod:teacher ~ ~ ~
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {StudentData:{Level:"primary",Grade:6}}
```

Student conserva `StudentData`: `Level` = `primary` (Grade 1–6) o `secondary` (Grade 1–5). Solo nivel y grado; no hay secciones. Sin datos válidos queda sin salón: responde a ataques directos pero no reacciona como testigo. Cambiar su grado limpia el objetivo previo. Los datos se guardan y sincronizan al cliente. Teacher ignora y no guarda `StudentData`.

Teacher conserva `WitnessedAttackers` (UUIDs en texto) y `ObservedUniforms` (UUID → último estado visible del uniforme). Student no guarda estas memorias. Los NBT vanilla de equipo, salud, nombres y demás datos normales de entidad se conservan a través de la clase común. `CustomName` sigue teniendo prioridad sobre los nombres Student/Teacher.

## Combate y comportamiento actual

Valores base con los cinco NBT en nivel 3, sin armadura ni resistencias del objetivo:

| Entidad | HP máximos | Daño fácil | Daño normal | Daño difícil | Cadencia | Movimiento |
|---|---:|---:|---:|---:|---|---|
| Student | 30 | 1.5 | 2 | 2.5 | 20 ticks / 1 s | Normal 0.25; provocado sqrt(0.117) ≈ 0.3421 |
| Teacher | 50 | 3.5 | 7 | 10.5 | 20 ticks / 1 s | Normal 0.25; persiguiendo sqrt(0.13) ≈ 0.3606 |

Ambos siguen en `MobCategory.CREATURE`, sin heredar Monster. `EntityType.Builder.notInPeaceful()` impide las apariciones normales en pacífico (incluidos huevos y creación estándar); el despawn vanilla también los elimina al pasar a esa dificultad, aunque sean persistentes. No se añaden reglas de aparición natural.

La velocidad de persecución del profesor se calcula para aproximar el sprint vanilla del jugador en suelo normal; el alumno produce el 90 % de su avance. Obstáculos, agua, giros y TPS afectan el resultado y falta comparación en el juego.

Teacher detecta jugadores visibles hasta 128 bloques en nivel 3 de Percepción (96–160 según nivel). Solo lleva regla de madera y mantiene vacías las otras ranuras; rechaza intercambios. El jugador con `fake_school_haircut`, `uniform_polo`, `uniform_pants`, `uniform_shoes` en sus ranuras lo vuelve neutral, salvo agresión escolar presenciada. Ver que falta una pieza activa hostilidad; volver a ver el uniforme completo la elimina si no hay agresiones presenciadas. No actualiza prendas fuera de su visión. Cada profesor conserva su propia memoria al guardar/cargar.

Student es neutral y responde a su atacante. Los estudiantes testigos solo se provocan si ven al jugador y al estudiante agredido, dentro del rango de Percepción del testigo, y ambos estudiantes comparten nivel y grado. No avisan a otros alumnos. Los profesores pueden presenciar ataques escolares de cualquier grado. Los eventos cubren ataques cuerpo a cuerpo, incluidos bloqueados, y proyectiles atribuidos al jugador. No se implementan las otras reglas escolares o ataques a animales.

El daño y la cadencia son independientes del item, sus encantamientos, desgaste y cooldown. `lsmmod:school_npc_melee` tiene `scaling: "never"`: la dificultad se calcula una sola vez en SchoolNpcEntity. Conserva armadura, escudos y resistencias del objetivo. Mantiene la etiqueta `minecraft:bypasses_cooldown` para cumplir la cadencia incluso con ventanas de inmunidad externas; el temporizador del NPC sigue limitando los golpes. Teacher reproduce el golpe de regla. No atacan jugadores creativos/espectadores. Modelo de jugador con ciclo de caminar, sin añadir animación de ataque.

## Items, render y loot

Huevos separados: `student_spawn_egg` y `teacher_spawn_egg`, en la pestaña creativa del mod. Se mantienen las restricciones de uniforme para jugadores y armor stands. Student conserva el intercambio de equipo y el hook musical anteriores; Teacher mantiene el equipo fijo.

Modelo/render común en `PlaceholderSchoolNpcModel`, `SchoolNpcRenderer` y `SchoolNpcRenderState`; no es un tercer tipo de entidad. Se conserva exactamente la geometría, UV normalizadas y ciclo de caminar del modelo de jugador anterior. Solo se cambia el nombre de su clase auxiliar y la resolución de las texturas.

### Skins placeholder (256×256)

Se retiran las skins planas de 64×64 del runtime y se generan atlas nuevos de 256×256 RGBA (cuatro texeles por unidad UV). Polo rojo oscuro con cuello, botones, escudo claro, costuras y mangas largas hasta las manos; pantalón gris y zapatos negros. Cabeza/piel de primaria #ffffff con número #000000; secundaria #000000 con número #ffffff. Numerales de fuente bold dibujados a alta resolución en las cuatro caras de la cabeza. El respaldo genérico muestra `NPC` en mayúsculas.

| Tipo | Texturas de respaldo |
|---|---|
| Primaria 1–6 | `textures/entity/students/placeholder_1p.png`–`placeholder_6p.png` |
| Secundaria 1–5 | `textures/entity/students/placeholder_1s.png`–`placeholder_5s.png` |
| Genérico/Teacher | `textures/entity/placeholder_school_npc.png` |

Las futuras skins definitivas conservarán los nombres **sin prefijo** (`1p.png`–`6p.png`, `1s.png`–`5s.png`, `school_npc.png`), actualmente ausentes. Selector: definitiva del grado → placeholder del grado → genérica definitiva → placeholder genérica NPC → Steve. Un PNG faltante, no decodificable, con dimensiones incompatibles o sin píxeles base en cabeza/cuerpo/extremidades se descarta. Se admiten skins cuadradas de 64–4096 píxeles, ancho múltiplo de 64. No detecta errores artísticos, numerales erróneos o UV pintadas en zonas equivocadas cuando el archivo cumple ese contrato.

La validación se almacena en caché y se reinicia con la recarga de recursos (F3+T), evitando decodificar un PNG cada frame. Un PNG definitivo válido tiene prioridad sobre su placeholder. El modelo y sus referencias usan la clase `PlaceholderSchoolNpcModel` y el selector explícito; no se añaden modelos JSON de bloque para estas entidades.

Generación: `python tools/create_placeholder_student_skins.py` (Pillow y fuente DejaVu Sans Bold); para otra ruta de fuente usar `--font ruta/al/archivo.ttf`. Los originales de `newresources/skin_grados` se conservan como referencia fuera de los assets del juego; los nombres antiguos de runtime deben eliminarse según el listado de la entrega.

Comprobaciones en Minecraft pendientes: once grados y Teacher, mangas largas y numerales, definitiva ausente/corrupta/incompatible/transparente con fallback, añadir definitiva válida y recargar, y confirmar que modelo/combate no cambian.

Loot separado y vacío en `loot_table/entities/student.json` y `teacher.json`. No se define loot nuevo ni drops de equipo.

## Aplicación del reemplazo

Copiar los archivos de la entrega y eliminar los enumerados en `ELIMINAR.txt`; los archivos viejos no se borran al copiar un ZIP. Se retiran el registro, huevo, clases auxiliares, loot, tipo de daño y nombre de textura de `test_npc`, reemplazados por los recursos nuevos. El asiento invisible sigue registrado.

Los `test_npc` ya guardados y sus huevos viejos no se convierten automáticamente: el ID antiguo deja de existir. Para conservar una prueba existente, antes de actualizar exportar su NBT y recrearla con `/summon lsmmod:student` o `/summon lsmmod:teacher`, copiando StudentData/memorias/equipo que corresponda. Los flags de rol antiguos no cambian el tipo nuevo.

## Verificación manual pendiente

Comprobar ambos huevos y `/summon`, render sin excepción, StudentData al guardar/cargar y rechazo en Teacher; defensa entre alumnos solo del mismo nivel/grado; memoria individual del profesor, uniforme observado y equipo fijo; daños por dificultad, intervalo base de veinte ticks y multiplicadores de estadísticas; selector de skins y armor stands; ausencia de registro/huevo antiguo y funcionamiento de asientos.

## Huevos por salón y casacas de promoción

Se añaden once huevos `lsmmod:student_1p_spawn_egg` a `student_6p_spawn_egg` y `lsmmod:student_1s_spawn_egg` a `student_5s_spawn_egg`. Usan SpawnEggItem vanilla y el componente ENTITY_DATA con `StudentData:{Level:"primary"/"secondary",Grade:1..6/1..5}`: cada alumno nace con su salón y la skin correspondiente. Los huevos genéricos se conservan. Aparecen automáticamente en la pestaña del mod. No tienen receta. La interacción con un spawner conserva el comportamiento vanilla de elegir el tipo de entidad; no configura el salón de sus futuros alumnos.

Iconos RGBA 64×64: primaria blanca con número #701524; secundaria #701524 con número blanco. Excepciones: 6p fondo #701524/número blanco; 5s fondo #436b3e/número dorado #c8a65b. Manifest de paletas: `tools/student_classroom_eggs.json`; generador: `python tools/create_student_classroom_eggs.py` (Pillow, fuente bold configurable con `--font`).

Solo `placeholder_6p.png` y `placeholder_5s.png` cambian su uniforme por una casaca varsity de mangas crema, ribetes a rayas, botones claros, bolsillos y bordado Mc. 6p usa #701524 y espalda «Promo 2026» / «Stellaris». 5s usa #436b3e, bordados #c8a65b, «Vastos Indomitus» en dos líneas y emblema angular de Ender Dragon dorado. Se mantienen atlas 256×256, cabezas, pantalones, zapatos, geometría y animación. Los textos están rasterizados a la resolución del atlas. Se regenera con `tools/create_placeholder_student_skins.py`.

Validación de recursos y lectura de la API de Minecraft 26.2; pendiente de compilación y prueba en juego.

## Cinco NBT universales (Student y Teacher)

Cinco enteros **en la raíz**, separados de `StudentData`: `Vitality`, `Strength`, `Speed`, `Perception` y `AttackSpeed`. Las claves usan ASCII, sin espacios ni acentos. Cada una acepta 1–5; falta de campo = 3 en comandos/cargas sin datos previos; valores enteros fuera del rango se limitan a 1 o 5. Se guardan y sincronizan. Al usar un huevo se sortean los cinco niveles, independientes y uniformes entre 1 y 5; no se implementan rasgos.

| Nivel | Multiplicador | Rango de visión | Intervalo de ataque a 20 TPS |
|---|---:|---:|---:|
| 1 | 0.75 | 96 bloques | 27 ticks / 1.35 s |
| 2 | 0.875 | 112 bloques | 23 ticks / 1.15 s |
| 3 | 1 | 128 bloques | 20 ticks / 1 s |
| 4 | 1.125 | 144 bloques | 18 ticks / 0.9 s |
| 5 | 1.25 | 160 bloques | 16 ticks / 0.8 s |

Vitalidad multiplica la capacidad máxima de vida de 30/50 HP. Fuerza multiplica el daño de la tabla según la dificultad actual, tanto contra jugadores como otras entidades. Velocidad multiplica el atributo de movimiento en reposo y persecución; el número es un atributo, no bloques por segundo. Percepción multiplica FOLLOW_RANGE y las comprobaciones de visión para uniforme/agresiones; el evento busca testigos hasta 160 bloques y cada testigo aplica su límite. AttackSpeed multiplica la **frecuencia**, por lo que el intervalo es `round(20 / multiplicador)` ticks, con redondeo al tick más cercano. Los multiplicadores se calculan desde las bases, sin acumularse al cargar ni en cada tick.

La vida actual se conserva al editar Vitality y se limita al nuevo máximo; aumentar el máximo no cura automáticamente. Una invocación sin Health empieza llena. Los huevos normales nacen llenos según su Vitality aleatoria; un huevo personalizado puede sobrescribir stats y Health mediante ENTITY_DATA. Al cargar entidades antiguas se recalcula la capacidad 30/50, pero se conserva su Health. Los modifiers externos de atributos permanecen; el daño y la cadencia de estas entidades continúan independientes del equipo.

```mcfunction
/summon lsmmod:student ~ ~ ~ {StudentData:{Level:"primary",Grade:6},Vitality:5,Strength:4,Speed:3,Perception:2,AttackSpeed:1}
/summon lsmmod:teacher ~ ~ ~ {Vitality:3,Strength:3,Speed:3,Perception:3,AttackSpeed:3}
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {Vitality:4,AttackSpeed:5}
```

## Integración opcional con Jade 26.2.10

`compat/jade/SchoolNpcJadePlugin` se descubre mediante `@WailaPlugin`, sin referencias a Jade desde el arranque del mod. Registra datos y tooltip para SchoolNpcEntity (ambos tipos). Cada fila muestra nombre, nivel, multiplicador y valor efectivo: HP máximos, daño según dificultad, movimiento actual, rango en bloques e intervalo en segundos. Los datos efectivos se envían desde el servidor; sin esa respuesta solo se muestran niveles sincronizados y multiplicadores.

Se incluye el JAR suministrado en `libs/Jade-mc26.2-NeoForge-26.2.10.jar` como dependencia **compileOnly**, sin incorporarlo al JAR de LSM ni exigir Jade al jugar. Para los valores efectivos en multijugador, instalar Jade compatible en cliente y servidor; en un mundo local basta en la instalación del juego. Hay dependencia opcional en neoforge.mods.toml y traducciones es_es/en_us. El formato del panel se selecciona en la configuración cliente de LSM descrita debajo. La integración no exige Jade al cargar el mod.

Comprobación pendiente en juego: valores base en fácil/normal/difícil; ausencia y eliminación en pacífico; `/summon` y `/data merge` de los cinco niveles en ambos tipos; guardar/cargar sin multiplicar de nuevo; salud actual al reducir/subir Vitalidad; Percepción 1 y 5 con testigos del mismo salón y profesores; cadencias 27/23/20/18/16 ticks; Jade con servidor, sin datos de servidor y juego sin Jade. La API se contrastó con el JAR suministrado y fuentes de Minecraft 26.2; no se ejecutó compilación ni Minecraft.

### Nombres ingleses y sorteo por huevo

Las claves persistentes son únicamente `Vitality`, `Strength`, `Speed`, `Perception` y `AttackSpeed`. Los nombres antiguos en español se admiten **solo al leer** para migrar partidas; si existen ambas formas tiene prioridad la inglesa. Después se guardan solo las claves inglesas. Jade usa las nuevas claves; sus etiquetas visibles siguen traducidas.

`SchoolNpcEntity.finalizeSpawn` realiza cinco llamadas independientes a `nextInt(5) + 1` para `SPAWN_ITEM_USE` y `DISPENSER`. Se aplica a los huevos genéricos de Student/Teacher y a los once huevos de salón, sin modificar `StudentData.Level`, `StudentData.Grade` ni los registros de huevos. Un mismo stack puede producir valores distintos en cada uso; las repeticiones también son válidas. El sorteo no ocurre al guardar, cargar, cambiar de dimensión o editar NBT. `/summon` conserva el nivel 3 por defecto y permite fijar los cinco valores. Un spawner configurado por un huevo sigue siendo un spawner vanilla, no un uso directo de huevo.

La configuración ENTITY_DATA del huevo se aplica después del sorteo: sin stats explícitas conserva los valores generados; con claves inglesas explícitas permite elegir esos niveles. El bonus aleatorio vanilla de FOLLOW_RANGE se retira para mantener Percepción controlada por su nivel (96–160 bloques sin modifiers externos).

Verificación en juego pendiente adicional: crear varios NPC desde cada tipo de huevo, consultar los cinco NBT ingleses y comprobar diversidad de niveles 1–5 sin cambiar salón; volver a cargar y comparar; comprobar Vitality/Health inicial; migrar los campos españoles y guardar en inglés; verificar comandos con valores explícitos y panel Jade.

## Configuración de presentación de Jade

NeoForge genera `config/lsmmod-client.toml` en la instalación de Minecraft. Es una preferencia **por cliente**, no una regla de combate del servidor. Valor inicial: `FULL_STATS`. Cambiar la opción y reiniciar el cliente para aplicar de forma segura; no requiere recompilar.

```toml
[jade]
npcDisplayMode = "FULL_STATS"
```

| Valor | Contenido del tooltip de Student/Teacher |
|---|---|
| `NAME_ONLY` | Únicamente el nombre del NPC. |
| `LEVELS` | Nombre y cinco filas de nivel, como `Vitality: 3`; sin factores ni valores efectivos. |
| `FULL_STATS` | Presentación completa anterior: niveles, multiplicadores y valores efectivos, además de información estándar de Jade. |

El proveedor se ejecuta al final de los proveedores estándar; los modos compactos limpian sus líneas de vida/armadura/mod y vuelven a añadir el nombre y, si corresponde, los niveles. La política usa la configuración del mod, sin un segundo interruptor para este proveedor. Otros tipos de entidad no cambian. Los niveles pueden mostrarse con los datos sincronizados del NPC si falta una respuesta de Jade del servidor; los valores efectivos de FULL_STATS requieren esa respuesta. La configuración no contiene imports de Jade ni clases gráficas y no obliga a instalar el mod opcional en el servidor.

## Nombres de salón de Student

`getName()` utiliza el grado sincronizado: 1p–6p y 1s–5s tienen once claves traducidas. Ejemplo actual de 5s: **5th Secondary Male Student** / **5th Secondary Female Student** en en_us, **Alumno de 5to de secundaria** / **Alumna de 5to de secundaria** en es_es. El nombre cambia al editar `StudentData.Level`/`Grade`, sin tocar su NBT ni sus estadísticas. Un alumno sin salón conserva el nombre genérico Student; Teacher conserva su nombre anterior. Un `CustomName` explícito tiene prioridad. Jade usa estos nombres; no se fuerza una etiqueta flotante permanente sobre la entidad.

Comprobación adicional pendiente en Minecraft: generación del TOML, tres valores del enum, modos compactos sin filas adicionales estándar, FULL_STATS con datos del servidor, nombres de once salones en ambos idiomas, cambio de grado, CustomName, y carga sin Jade. Config/API/recursos revisados sin compilar ni iniciar el juego.

## Género de Student

NBT **solo de alumnos**: `gender` es un string `"male"` o `"female"`; no es booleano. Sin campo, valor inválido o NPC antiguo: male. Se acepta female sin distinguir mayúsculas y se guarda normalizado. Teacher no lee, guarda ni sortea gender. Student lo sincroniza con EntityDataSerializers.STRING y conserva el valor al guardar/cargar y viajar entre dimensiones.

```mcfunction
/summon lsmmod:student ~ ~ ~ {StudentData:{Level:"secondary",Grade:5},gender:"female"}
/summon lsmmod:student ~ ~ ~ {StudentData:{Level:"primary",Grade:6},gender:"male"}
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {gender:"female"}
```

Al usar cualquier huevo de Student, incluidos los once salones, `finalizeSpawn` sortea male/female con `nextBoolean` (50 % cada uno), además de las cinco stats independientes. No toca Level/Grade. ENTITY_DATA explícito en un huevo personalizado puede sobrescribir gender después del sorteo. Comandos, cargas y ediciones NBT no sortean de nuevo. Los spawners mantienen su comportamiento vanilla.

### Modelo y atlas

Se inspeccionó `Female-Gender-Mod-5.0.0+mc26.2-neoforge.jar` como referencia de geometría: dos piezas de busto de 4×5×3. Se implementa geometría propia estática y cubierta por la ropa, como hijos del torso, sin depender ni incorporar ese mod. Las piezas se muestran **únicamente** para Student female. Todo el resto del mesh usa PlayerModel wide: cabeza, brazos, piernas, overlays y ciclo de caminar anteriores. Sin físicas, rebote ni otras animaciones o cambios corporales. La armadura vanilla conserva su capa anterior.

La nueva capa `lsmmod:placeholder_school_npc` se registra con RegisterLayerDefinitions y se usa al crear el renderer. Las piezas reutilizan las UV de cada mitad del pecho del polo/casaca, conservando botones, Mc y colores; sus caras traseras quedan dentro del torso. No se cambia la resolución 256×256 ni se pinta piel en el uniforme.

Los once `placeholder_<grado>p/s.png` son la variante male, con **M** en la cara superior de la cabeza. Se añaden once `placeholder_<grado>p/s_female.png` con **F**. El resto de sus píxeles de ropa, números de grado, piel, casacas 6p/5s y textos posteriores se conserva. Alumnos sin salón tienen `placeholder_student_male.png` y `placeholder_student_female.png`; Teacher conserva `placeholder_school_npc.png` sin letra de género. Fuente de letras bold a la resolución del atlas. Generador: `python tools/create_student_gender_skins.py --font ruta/opcional.ttf`, lee los atlas ya existentes sin regenerar su ropa. El generador completo de uniformes llama también al de género al terminar.

Selección para female asignada: definitiva `<grado>p/s_female.png` → definitiva compartida `<grado>p/s.png` → placeholder female → placeholder genérica del género → respaldo genérico/Steve. Male conserva definitiva compartida → placeholder del grado → genérica male → respaldo genérico/Steve. Las definitivas conservan el contrato vanilla cuadrado; las M/F se garantizan en los placeholders, no se alteran skins definitivas de packs externos. Cambiar gender actualiza nombre/skin/geometría desde los datos sincronizados; CustomName sigue prioritario. Nombres traducidos de Alumno/Alumna y Male/Female Student disponibles en los tres modos de Jade.

Revisión estática: rutas y registro de capa, áreas UV, ambos géneros en los once grados, conservación de ropa y placeholder de Teacher, claves de nombre en es_es/en_us y sorteo solo al usar huevo. Pendiente compilar/probar en Minecraft: capa horneada, busto bajo uniforme en ambos ciclos de caminar, doce variantes por género (11 salones + sin asignar), M/F desde arriba, cambiar gender con /data, guardar/cargar, distribución de huevos en una muestra amplia, salón/estadísticas sin cambios, Jade en tres modos y equipos vanilla. Una muestra aleatoria pequeña no garantiza exactamente la mitad de cada género.
