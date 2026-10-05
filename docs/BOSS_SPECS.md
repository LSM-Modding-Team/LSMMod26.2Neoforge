# BOSS_SPECS: cadena de eventos, Director, Hidalgo, Yahu, fantasma, ritual y boss final

**Estado: SPEC, sin implementar.** No existe ningún NPC, jefe ni evento en el código. Escrito en la sesión 3 (Tanda C) a partir de `docs/history/DESIGN_SOURCE_v1.md` §7.4, §7.5 y §9.

**Implementan este spec (todos `PLANNED`, ver `docs/CHUNKS.md`):** `K1` (Hidalgo, Yahu, fantasma, condición del Director, ataúd, ritual, boss final, traje). Hito M7: riesgo medio-alto (barra de jefe, fases). Depende de M4-M6 (NPC, colegio, raids).

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*. `V#` / `P#` / `D#` = vacío, pendiente o decisión abierta en `docs/DESIGN.md` §5 y §6. **Donde el diseño no da un dato, dice "sin definir".** Qué hizo cada chunk distinto del plan: ninguno todavía.

---

## 1. Cadena de eventos (diseño §9.1)

| Paso | Qué pasa | Ver |
|---|---|---|
| 1 | Encuentras un colegio (con el mapa craftado con legendarios de bunnidogs, o el garantizado cerca del spawn). | `WORLD_SPECS.md` §1, §5 |
| 2 | Dentro, usas las mecánicas (uniformes, folders, instrumentos, campana, etc.) para sobrevivir y avanzar. | `MECHANICS_SPECS.md` |
| 3 | Matas a **Hidalgo**: aparece el **fantasma de Manuel** en el **auditorio**. Al vencerlo obtienes el **alma**. | §2, §4 |
| 4 | Matas a todos los NPC **importantes**: el **Director** pasa a ser vulnerable. | §3 |
| 5 | Matas al Director. Entre sus drops está el **mapa al ataúd de Manuel**. | §5 |
| 6 | Sigues el mapa y consigues el **ataúd** (cofre del tesoro a ~750 bloques). | §6 |
| 7 | Vuelves al colegio y haces el **ritual** junto a **dos San Martín de Porres**. | §7 |
| 8 | Manuel Tirado revive y se convierte en el **boss final**. | §8 |

El diseño no impone orden entre los pasos 3 y 4 más allá de la numeración; que Hidalgo y el fantasma son "importantes" (minibosses) sí está dicho (§3).

---

## 2. Hidalgo (miniboss)

* Único NPC que **se teletransporta** al ser llamado.
* **No usa regla.** Su ataque aplica **Expulsión** (`MECHANICS_SPECS.md` §5): si sigues dentro del colegio 10 s después, cada 8 s invoca 3 alumnos de primaria (mini zombies) o 2 vexes de Manuel Tirado, alternando *(propuesta de números)*.
* La **Ardilla PUCP** da inmunidad contra él.
* Al matarlo aparece el fantasma de Manuel (§4).
* Grupo de inmunidad de prendas: **Profesores** (V5, resuelto). Stats y zona: sin definir (P4).

---

## 3. Condición del Director (diseño §9.2)

El Director es **invulnerable hasta que hayas matado a todos los NPC importantes.** Una columna de `NPC_SPECS.md` §2 marca cada NPC.

* **No importantes (no hace falta matarlos):** Bianca, Lucio, Deivis y **un décimo de los alumnos de primaria**. No importa cuál décimo: basta con haber matado al **90 % de los alumnos de primaria**. **Los alumnos de 6to cuentan como primaria** (V1, resuelto por el usuario), así que entran en ese 100 %.
* **Importantes (obligatorios):** todos los demás: profesores, tutores (incluida Miss Alessandra), personal de limpieza (salvo Deivis), psicólogas, enfermera, Moisés, Pollo, Junior, el resto de los alumnos y los minibosses. Esto incluye a **Yahu, el vicioso, Rosa Sanmartiniana y los brigadieres**.
* **Para `N0` / `RulesCheck`:** la condición es lógica pura (¿quedan importantes vivos?, ¿se alcanzó el 90 % de primaria con 6to incluido?). Con una muestra pequeña de NPC basta para comprobarla (R9).
* **R11:** el estado "invulnerable" del Director necesita una señal visible o audible y su palabra explicada en el juego; no está diseñada.
* **Progreso (D4, pick):** se cuenta **por colegio**, guardado en la instancia de la estructura. Abierta hasta que el usuario responda.
* Qué cuenta como "matar" (quién da el golpe final, muertes por bunnidogs o por raids, NPC que reaparecen): sin definir.

---

## 4. Fantasma de Manuel Tirado (miniboss)

* Estilo fantasma de *Ice and Fire*.
* Aparece en el **auditorio** una vez derrotado Hidalgo.
* Su drop es el **alma de Manuel Tirado** (legendario, parte del ritual).
* Es un miniboss, por tanto **importante** (§3). Stats: sin definir (P4).

---

## 5. Director

* Invulnerable hasta cumplir la condición (§3).
* Entre sus drops (la lista completa: sin definir) está el **mapa al ataúd**.
* Stats, zona, ataque y comportamiento: sin definir (V20).

---

## 6. Ataúd de Manuel Tirado

* Funciona como **cofre del tesoro** que aparece en un lugar **aleatorio a ~750 bloques del colegio**.
* Es un bloque (`BLOCK_SPECS.md`) y a la vez un legendario (`ITEM_SPECS.md` §6).
* A cuál colegio se refieren los ~750 bloques si hay varios, y qué contiene aparte del alma si se entrega en él: ver D4 y D7. **Sin definir:** si el ataúd se mueve al colegio o el ritual se hace con él en su sitio (P7).

---

## 7. Ritual

1. Vuelves **al colegio** con lo necesario.
2. Haces el ritual junto a **dos bloques de San Martín de Porres**: estos **emiten rayos hacia el ataúd**, igual que los ender crystals al revivir al ender dragon.
3. **Si el ritual no se hace dentro de la estructura y con los dos santos, no funciona.**
4. Manuel Tirado revive y se convierte en el boss final (§8).

* **Pendiente (P7):** la disposición exacta (cómo se colocan los dos santos y el ataúd).
* **D7 (pick, abierto):** el **alma** se entrega **en el ataúd**. El diseño la llama "parte del ritual" pero el paso del ritual solo nombra los dos santos y el ataúd; se confirma con el usuario.
* Es reproducible o no, y qué pasa si se hace fuera: sin definir. "Dentro de la estructura": `WORLD_SPECS.md` §4.

---

## 8. Boss final: Manuel Tirado Andrade (invocado)

* Solo se puede invocar **dentro de la estructura del colegio** (`WORLD_SPECS.md` §4).
* Tiene **fases**. Mientras su vida baja con tus ataques, va **reviviendo a todos los muertos como fantasmas** (los fantasmas son **vulnerables**).
* **Sigue vivo hasta que lo matas.**
* Su drop es el **traje de Manuel Tirado** (`ARMOR_SPECS.md` §5).
* **Sin definir:** cuántas fases y a qué vida cambia cada una (V21); vida, daño y ataques (P4); qué es un "fantasma" de cada NPC muerto y con qué stats (V8); si los muertos de otros colegios cuentan; la barra de jefe.

---

## 9. Yahu (Alcalde, miniboss)

* **Alumno de 2do de secundaria.** Es miniboss, por tanto **importante** (§3).
* Es todo lo que dice el diseño: comportamiento, stats, zona, drops y por qué es "Alcalde": sin definir (V20).
* Grupo de inmunidad de prendas: **Alumnos** (por ser alumno).

---

## 10. Estado de implementación y riesgos

* **Existe hoy:** nada.
* **Riesgo (M7, medio-alto):** barra de jefe, fases, vexes propios, fantasmas. **Una API nueva por chunk** (R6): conviene partir `K1` en chunks por jefe (Hidalgo → fantasma → Director → ritual → boss).
* La lógica pura de la condición del Director (§3) va a `rules/` y se comprueba con `RulesCheck` (nace en `N0`).
* La cadena depende de: `N1` (NPC), `W1`/`W2` (colegio, mapa), `R1` (raids opcionales), `I1`/`I2` (items y efectos), `A1` (traje).

---

## 11. Vacíos que tocan este doc

V8 (vexes y fantasmas), V20 (Yahu y Director sin datos), V21 (fases del boss), P4, P7, D4, D7. Resueltos: V1, V5. Detalle en `docs/DESIGN.md` §5 y §6.
