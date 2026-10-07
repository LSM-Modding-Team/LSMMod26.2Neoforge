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

## Comportamiento heredado

| Entidad | Daño base | Cadencia | Movimiento |
|---|---:|---:|---|
| Student | 4 | 6 ticks / 0.3 s | Normal 0.25; provocado sqrt(0.117) ≈ 0.3421 |
| Teacher | 8 | 6 ticks / 0.3 s | Normal 0.25; persiguiendo sqrt(0.13) ≈ 0.3606 |

La velocidad de persecución del profesor se calcula para aproximar el sprint vanilla del jugador en suelo normal; el alumno produce el 90 % de su avance. Obstáculos, agua, giros y TPS afectan el resultado y falta comparación en el juego.

Teacher detecta jugadores visibles hasta 128 bloques. Solo lleva regla de madera y mantiene vacías las otras ranuras; rechaza intercambios. El jugador con `fake_school_haircut`, `uniform_polo`, `uniform_pants`, `uniform_shoes` en sus ranuras lo vuelve neutral, salvo agresión escolar presenciada. Ver que falta una pieza activa hostilidad; volver a ver el uniforme completo la elimina si no hay agresiones presenciadas. No actualiza prendas fuera de su visión. Cada profesor conserva su propia memoria al guardar/cargar.

Student es neutral y responde a su atacante. Los estudiantes testigos solo se provocan si ven al jugador y al estudiante agredido, dentro de 128 bloques, y ambos estudiantes comparten nivel y grado. No avisan a otros alumnos. Los profesores pueden presenciar ataques escolares de cualquier grado. Los eventos cubren ataques cuerpo a cuerpo, incluidos bloqueados, y proyectiles atribuidos al jugador. No se implementan las otras reglas escolares o ataques a animales.

El daño y la cadencia son independientes del item, sus encantamientos, desgaste y cooldown. `lsmmod:school_npc_melee` usa las reglas de daño mob vanilla y la etiqueta `minecraft:bypasses_cooldown`, para admitir los golpes cada seis ticks conservando armadura/escudos/dificultad/resistencias. Teacher reproduce el golpe de regla. No atacan jugadores creativos/espectadores. Modelo de jugador con ciclo de caminar, sin añadir animación de ataque.

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

Comprobar ambos huevos y `/summon`, render sin excepción, StudentData al guardar/cargar y rechazo en Teacher; defensa entre alumnos solo del mismo nivel/grado; memoria individual del profesor, uniforme observado y equipo fijo; daños 4/8, intervalo seis ticks y velocidades; selector de skins y armor stands; ausencia de registro/huevo antiguo y funcionamiento de asientos.
