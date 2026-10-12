# Jefes y ritual

Diseño pendiente: todavía no hay jefes ni cadena de progresión implementados.

Sin marca = decidido; *(propuesta)* = ajustable; *(a confirmar)* = supuesto. Datos sin definir e IDs P/V/D/A: [DESIGN](DESIGN.md).

## Cadena de eventos (diseño)

| Paso | Qué pasa |
|---|---|
| 1 | Encuentras un colegio (con el mapa craftado con legendarios de bunnidogs, o el garantizado cerca del spawn). |
| 2 | Dentro, usas las mecánicas (uniformes, folders, instrumentos, campana, etc.) para sobrevivir y avanzar. |
| 3 | Matas a **Hidalgo**: aparece el **fantasma de Manuel** en el **auditorio**. Al vencerlo obtienes el **alma**. |
| 4 | Matas a todos los NPC **importantes**: el **Director** pasa a ser vulnerable. |
| 5 | Matas al Director. Entre sus drops está el **mapa al ataúd de Manuel**. |
| 6 | Sigues el mapa y consigues el **ataúd** (cofre del tesoro a ~750 bloques). |
| 7 | Vuelves al colegio y haces el **ritual** junto a **dos San Martín de Porres**. |
| 8 | Manuel Tirado revive y se convierte en el **boss final**. |

El diseño no impone orden entre los pasos 3 y 4 más allá de la numeración; que Hidalgo y el fantasma son "importantes" (minibosses) sí está dicho.

## Hidalgo (miniboss)

* Único NPC que **se teletransporta** al ser llamado.
* **No usa regla.** Su ataque aplica **Expulsión** ([MECHANICS_SPECS](MECHANICS_SPECS.md)): si sigues dentro del colegio 10 s después, cada 8 s invoca 3 alumnos de primaria (mini zombies) o 2 vexes de Manuel Tirado, alternando *(propuesta de números)*.
* La **Ardilla PUCP** da inmunidad contra él.
* Al matarlo aparece el fantasma de Manuel.
* Grupo de inmunidad de prendas: **Profesores** (V5, resuelto). Stats y zona: sin definir (P4).

## Condición del Director (diseño)

El Director es **invulnerable hasta que hayas matado a todos los NPC importantes.** Una columna de [NPC_SPECS](NPC_SPECS.md) marca cada NPC.

* **No importantes (no hace falta matarlos):** Bianca, Lucio, Deivis y **un décimo de los alumnos de primaria**. No importa cuál décimo: basta con haber matado al **90 % de los alumnos de primaria**. **Los alumnos de 6to cuentan como primaria**, así que entran en ese 100 %.
* **Importantes (obligatorios):** todos los demás: profesores, tutores (incluida Miss Alessandra), personal de limpieza (salvo Deivis), psicólogas, enfermera, Moisés, Pollo, Junior, el resto de los alumnos y los minibosses. Esto incluye a **Yahu, el vicioso, Rosa Sanmartiniana y los brigadieres**.
* **Lógica pura:** la condición es lógica pura (¿quedan importantes vivos?, ¿se alcanzó el 90 % de primaria con 6to incluido?). Con una muestra pequeña de NPC basta para comprobarla.
* El estado "invulnerable" del Director necesita una señal visible o audible y su palabra explicada en el juego; no está diseñada.
* **Progreso (D4, pick):** se cuenta **por colegio**, guardado en la instancia de la estructura. Abierta hasta que el usuario responda.
* Qué cuenta como "matar" (quién da el golpe final, muertes por bunnidogs o por raids, NPC que reaparecen): sin definir.

## Fantasma de Manuel Tirado (miniboss)

* Estilo fantasma de *Ice and Fire*.
* Aparece en el **auditorio** una vez derrotado Hidalgo.
* Su drop es el **alma de Manuel Tirado** (legendario, parte del ritual).
* Es un miniboss, por tanto **importante**. Stats: sin definir (P4).

## Director

* Invulnerable hasta cumplir la condición.
* Entre sus drops (la lista completa: sin definir) está el **mapa al ataúd**.
* Stats, zona, ataque y comportamiento: sin definir (V20).

## Ataúd de Manuel Tirado

* Funciona como **cofre del tesoro** que aparece en un lugar **aleatorio a ~750 bloques del colegio**.
* Es un bloque ([BLOCK_SPECS](BLOCK_SPECS.md)) y a la vez un legendario ([ITEM_SPECS](ITEM_SPECS.md)).
* A cuál colegio se refieren los ~750 bloques si hay varios, y qué contiene aparte del alma si se entrega en él: ver D4 y D7. **Sin definir:** si el ataúd se mueve al colegio o el ritual se hace con él en su sitio (P7).

## Ritual

1. Vuelves **al colegio** con lo necesario.
2. Haces el ritual junto a **dos bloques de San Martín de Porres**: estos **emiten rayos hacia el ataúd**, igual que los ender crystals al revivir al ender dragon.
3. **Si el ritual no se hace dentro de la estructura y con los dos santos, no funciona.**
4. Manuel Tirado revive y se convierte en el boss final.

* **Pendiente (P7):** la disposición exacta (cómo se colocan los dos santos y el ataúd).
* **D7 (pick, abierto):** el **alma** se entrega **en el ataúd**. El diseño la llama "parte del ritual" pero el paso del ritual solo nombra los dos santos y el ataúd; se confirma con el usuario.
* Es reproducible o no, y qué pasa si se hace fuera: sin definir. "Dentro de la estructura": [WORLD_SPECS](WORLD_SPECS.md).

## Boss final: Manuel Tirado Andrade (invocado)

* Solo se puede invocar **dentro de la estructura del colegio** ([WORLD_SPECS](WORLD_SPECS.md)).
* Tiene **fases**. Mientras su vida baja con tus ataques, va **reviviendo a todos los muertos como fantasmas** (los fantasmas son **vulnerables**).
* **Sigue vivo hasta que lo matas.**
* Su drop es el **traje de Manuel Tirado** ([ARMOR_SPECS](ARMOR_SPECS.md)).
* **Sin definir:** cuántas fases y a qué vida cambia cada una (V21); vida, daño y ataques (P4); qué es un "fantasma" de cada NPC muerto y con qué stats (V8); si los muertos de otros colegios cuentan; la barra de jefe.

## Yahu (Alcalde, miniboss)

* **Alumno de 2do de secundaria.** Es miniboss, por tanto **importante**.
* Es todo lo que dice el diseño: comportamiento, stats, zona, drops y por qué es "Alcalde": sin definir (V20).
* Grupo de inmunidad de prendas: **Alumnos** (por ser alumno).
