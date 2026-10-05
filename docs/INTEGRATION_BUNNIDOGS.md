# INTEGRATION_BUNNIDOGS: todo lo que cruza entre LSM y el mod bunnidogs

**Estado: SPEC, sin implementar.** No existe ningún código de integración, ni dependencia de Gradle entre los dos mods, ni `integration/bunnidogs/`. Escrito en la sesión 4 (Tanda D) a partir de `docs/history/DESIGN_SOURCE_v1.md` §10, de lo que los otros specs dicen sobre bunnidogs y de los datos que `docs/history/DOCS_PLAN.md` §3 leyó del zip de bunnidogs en la sesión 2.

**Implementan este spec (todos `PLANNED`, ver `docs/CHUNKS.md`):** `X1` (integración, hito M6; la decisión D1 va antes) y la parte de datos de `W2` (receta del mapa). Las raids que usan los nidos van a `RAID_PLAN.md` (`R1`).

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*. `V#` / `P#` / `D#` = vacío, pendiente o decisión abierta en `docs/DESIGN.md` §5 y §6. **Donde el diseño no da un dato, dice "sin definir": no se rellena.** Qué hizo cada chunk distinto del plan: ninguno todavía.

**Aviso sobre los datos de bunnidogs.** El zip de bunnidogs **no está** en este proyecto (D5): lo de §2 se leyó de allí en la sesión 2 y se arrastró tal cual; **no se ha vuelto a verificar**. Antes de codificar cualquier cosa de este doc hay que pedir ese zip o confirmar cada dato contra el código real. Lo único de bunnidogs que sí está en el zip es su `API_NOTES.md` (`docs/history/bunnidogs_patterns/API_NOTES.md`).

---

## 1. Qué cruza entre los dos mods (diseño §10)

| Qué | Dirección | Dónde se detalla |
|---|---|---|
| Los bunnidogs son **hostiles a todos los alumnos y profesores**, excepto a **Bianca** (alumna de 4to) | bunnidogs → NPC de LSM | §3, §4 |
| Las **raids** ocurren en lugares con **muchos nidos** de bunnidogs; los nidos funcionan como defensa natural contra los raiders | LSM lee datos de bunnidogs | §5 |
| El **mapa al colegio** se craftea con **objetos legendarios** de bunnidogs | LSM usa items de bunnidogs | §6 |
| "El mod es **el mismo proyecto** que bunnidogs" | decisión D1 | §7 |

Nada más del diseño cruza. En particular, **los bunnidogs no aparecen en la estructura del colegio**: el diseño no los pone allí.

---

## 2. Datos de bunnidogs que usa el diseño *(leídos del zip de bunnidogs en la sesión 2; sin re-verificar)*

* **Entidad** `bunnidogs:bunnidog` (clase `entity/Bunnidog`). Su `registerGoals()` corre **dentro del constructor de la superclase, antes de que exista el genoma**: los goals se registran para todos los perros y se filtran en `canUse()`. Hoy solo ataca con `HurtByTargetGoal`; hacerlo hostil a alumnos y profesores exige un goal nuevo **con el mismo filtrado**.
* **Nido:** bloque `bunnidogs:nest` y tipo de punto de interés (POI) `bunnidogs:nest`. Los perros lo reclaman con el `PoiManager`; el worldgen coloca nidos en cada casa de los asentamientos. Para "raids donde hay muchos nidos" basta **contar POIs `bunnidogs:nest`** en un radio, sin datos nuevos (ver el cuidado de §5).
* **Asentamientos:** estructura `bunnidogs:warren_village`, features `bunnidogs:warren` y `bunnidogs:surface_warren`, niveles 1-5. Depuración: `/bunnidogs warren [1-5]`.
* **Legendarios** (rareza EPIC, caen de perros muy criados, se exhiben en el Trophy Pedestal): `bunnidogs:titan_haunch`, `gilded_foot`, `moon_sinew`, `listening_shell`, `everbloom`. Son los **candidatos naturales** para el mapa (P1; decide el usuario).
* **Bianca:** bunnidogs no tiene noción de "alumno con nombre". La excepción se resuelve con una marca en la entidad de LSM, no en bunnidogs (§4).
* Mismas versiones y misma cadena de herramientas que LSM (Minecraft 26.2, NeoForge 26.2.0.88, Java 25).

---

## 3. Hostilidad de los bunnidogs hacia los NPC

* **Regla del diseño:** los bunnidogs atacan a todos los alumnos y a todos los profesores, salvo a Bianca.
* **Cómo se haría** *(propuesta, sin código)*: LSM escucha `EntityJoinLevelEvent`; si la entidad es un `bunnidogs:bunnidog`, le añade un goal de objetivo que apunte a los NPC de LSM que corresponda. Se hace **desde LSM** para que bunnidogs no dependa de LSM (D1). Como `registerGoals()` corre antes del genoma (§2), el goal nuevo debe registrarse para todos los perros y filtrar en `canUse()`, igual que los existentes.
* **Qué NPC cuentan como "alumnos y profesores":** el diseño no lo dice con tabla. Los grupos de `NPC_SPECS.md` §2 (V5 resuelto) dan una lectura posible, pero dejan sin grupo al personal de limpieza, Deivis, la mamá de cuarto, el fantasma y el boss, y hay NPC que no son ni alumno ni profesor y que viven en el colegio. Alcance exacto: **V23** (abierto). Hasta que el usuario responda, `X1` solo debe cubrir los NPC de los grupos *Alumnos* y *Profesores*, **y decirlo** *(propuesta)*.
* **Contraataque:** el diseño solo da la dirección bunnidog → NPC. Si los NPC de LSM devuelven el ataque o ni siquiera se defienden de un perro: **V24** (abierto).
* **Efecto en el juego:** los nidos hacen de defensa natural contra los raiders (§5). Los NPC muy OP (principio 1 de `DESIGN.md` §1) frente a perros de otro mod: el balance no está definido.
* **R11:** si un perro ataca a un NPC, el jugador debería poder ver por qué (el bunnidog se ve enojado con el NPC); señal no diseñada.

---

## 4. Excepción de Bianca

* Bianca es una alumna de 4to **marcada** como inmune a los bunnidogs; los bunnidogs no la atacan.
* La marca vive **en la entidad de LSM** (una etiqueta o un dato; cuál, lo decide el plan de `N1`), no en bunnidogs. El goal de §3 la lee y salta a Bianca.
* Bianca es un NPC **no importante** (`NPC_SPECS.md` §2); su grupo de prendas es *Alumnos*.
* Si hay más de una Bianca (una por colegio): el diseño no lo dice; es un nombre propio, se asume una por colegio *(a confirmar)*.

---

## 5. Nidos y raids

* Las raids de LSM ocurren **donde hay muchos nidos** `bunnidogs:nest`; las dispara el **Bad Omen LSM** (`MECHANICS_SPECS.md` §4, §5).
* **Qué falta decidir (D6):** cuántos nidos son "muchos", en qué radio se cuentan y cómo se elige el lugar. El plan (`RAID_PLAN.md` §5) propone dos parámetros de datos, valores sin definir.
* **Contar los nidos:** la idea del plan es contar POIs de tipo `bunnidogs:nest` en un radio. **Cuidado:** de bunnidogs solo se sabe que `PoiManager#take` *toma un ticket* del POI libre más cercano (los perros lo usan para reclamar su nido). Eso **no sirve para contar**, porque le quitaría el nido a un perro. Hace falta una consulta que solo lea; su nombre en 26.2 no está en ningún `API_NOTES`: es una fila nueva de investigación (R14) en el chunk que lo use.
* **Alternativa a contar nidos** *(`RAID_PLAN.md` no la adopta; queda para revisar en `R1c`, sin decidir)*: la estructura `bunnidogs:warren_village` tiene id propio y se puede consultar como estructura; las cuevas y el Nether son *features* y no. No cubriría todos los nidos, solo los de la aldea de superficie.
* **Depuración:** `/bunnidogs warren [1-5]` genera un asentamiento; sirve para probar raids sin esperar worldgen.
* **Composición de las olas** (`MECHANICS_SPECS.md` §4) *(propuesta de números)*: 1 = 6-8 alumnos de primaria; 2 = ~8 de secundaria; 3 = 3-4 profesores más el capitán con el estandarte del escudo.

---

## 6. Mapa al colegio (ingredientes)

* Mapa tipo cartógrafo de vanilla; lleva al colegio **más cercano** (`WORLD_SPECS.md` §5). Es la forma de llegar a los colegios que no son el garantizado cerca del spawn.
* **P1 (abierto):** qué legendarios lleva y cuántos. **Candidatos:** `titan_haunch`, `gilded_foot`, `moon_sinew`, `listening_shell`, `everbloom` (§2). Si el mapa pide uno solo, uno de cada, o varios: sin definir. Decide el usuario.
* Los legendarios de bunnidogs **solo caen de perros muy criados**: el mapa obliga a jugar la parte de crianza de bunnidogs antes de llegar al colegio, salvo el colegio garantizado cerca del spawn. Es una consecuencia del diseño, no una decisión nueva.
* La receta es un archivo de datos que nombra items de `bunnidogs:`; por tanto **LSM necesita bunnidogs instalado** para cargarla (D1). El formato de receta que ya funcionó en bunnidogs está en su `API_NOTES.md` (filas "Recipe JSON (26.x)").
* El resultado (el mapa) y la lógica de localizar el colegio más cercano son de `W2`; el cruce con bunnidogs es solo la lista de ingredientes.

---

## 7. D1: ¿uno o dos mods? *(pick, abierto)*

El diseño dice "mismo proyecto"; los zips son dos proyectos con ids distintos.

* **Pick: dos jars.** `lsmmod` depende de `bunnidogs`, **nunca al revés**; todo el código del cruce vive en `integration/bunnidogs/`; los goals de §3 se añaden con `EntityJoinLevelEvent`. Razón: bunnidogs sigue funcionando solo y LSM no lo contamina.
* **Costo:** hay que declarar la dependencia en `neoforge.mods.toml` y poder ejecutar los dos mods juntos en `./gradlew runClient`. Cómo se configura en el Gradle de este proyecto: **por investigar** (R14); no se ha probado nunca.
* **Alternativa:** un solo mod que absorba el otro. No se recomienda: mezcla dos proyectos grandes y rompe la regla "un doc por trabajo".
* Se responde **antes del hito M6**, no antes. Mientras no se responda, ningún chunk de LSM debe importar clases de bunnidogs.

---

## 8. Plan de chunks de `X1` *(propuesta; una API nueva por chunk, R6)*

| Id provisional | Qué | API nueva (el error de compilación más probable) |
|---|---|---|
| `X1a` | Dependencia de Gradle y de `neoforge.mods.toml`; `runClient` con los dos mods. Sin código de juego. | Configuración de Gradle, no de Minecraft |
| `X1b` | Escuchar `EntityJoinLevelEvent` y detectar un `bunnidogs:bunnidog` (solo un log). | `EntityJoinLevelEvent` |
| `X1c` | Goal de objetivo hacia alumnos y profesores (V23 respondido antes). | objetivos de ataque |
| `X1d` | Marca de Bianca y excepción en el goal. | etiqueta o dato de entidad |

La receta del mapa (§6) y el conteo de nidos (§5) **no** son de `X1`: van a `W2` y a `R1`. Requisito común: el zip de bunnidogs (D5) o su código verificado.

---

## 9. Estado de implementación y riesgos

* **Existe hoy:** nada. No hay dependencia, ni paquete `integration/`, ni código de bunnidogs en este zip.
* **Riesgos:** (1) trabajar sobre datos de §2 sin re-verificar; (2) `EntityJoinLevelEvent` y objetivos de ataque son familias que ningún código compilado de LSM cubre (`docs/API_NOTES.md`, mapa de riesgo); (3) ejecutar dos mods en `runClient` nunca se ha probado; (4) dos mods con versiones iguales hoy pueden dejar de estarlo si uno actualiza.
* Tras `X1`, anotar aquí qué hizo cada chunk distinto del plan.

---

## 10. Vacíos que tocan este doc

P1 (ingredientes del mapa), D1 (uno o dos mods), D5 (código compilado de bunnidogs), D6 (nidos y raids), V23 (alcance de la hostilidad), V24 (contraataque), V5 (grupos; resuelto). Detalle en `docs/DESIGN.md` §5 y §6.
