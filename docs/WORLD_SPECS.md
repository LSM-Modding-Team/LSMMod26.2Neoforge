# WORLD_SPECS: generación del colegio, zonas, colocación de NPC y mapa

**Estado: generación SPEC, catálogo del mundo suministrado implementado.** No existe todavía generación jigsaw ni mapa crafteable. `data/lsmmod/school_spaces.json` contiene las delimitaciones del colegio de `world (3).zip`; no constituye una estructura generable. Escrito en la sesión 3 (Tanda C) a partir de `docs/history/DESIGN_SOURCE_v1.md` §3.4 y de lo que el resto del diseño dice sobre dónde está cada cosa.

**Implementan este spec (todos `PLANNED`, ver `docs/CHUNKS.md`):** `W1` (estructura por jigsaw, aulas, colocación de NPC), `W2` (uno garantizado cerca del spawn y mapa crafteable). Hito M5, **riesgo alto** (estructuras jigsaw, mapa de exploración).

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*. `V#` / `P#` / `D#` = vacío, pendiente o decisión abierta en `docs/DESIGN.md` §5 y §6. **El diseño casi no da datos de tamaño ni de distribución: lo que falta dice "sin definir" y no se rellena.** Qué hizo cada chunk distinto del plan: ninguno todavía.

---

## 1. Generación de colegios (diseño §3.4)

* Los colegios se generan en **planicies** y son **extremadamente raros**. (Frecuencia, separación y biomas exactos: sin definir.)
* Hay **uno garantizado** en la planicie más cercana al spawn.
* Existe un **mapa crafteable** (tipo mapa de cartógrafo de vanilla) que lleva al **colegio más cercano**. Se craftea con **objetos legendarios del mod bunnidogs**; la lista está pendiente (P1; candidatos en `docs/INTEGRATION_BUNNIDOGS.md` §2).
* **Los otros colegios** (los que no son el primero) permiten lootear con facilidad si llevas puesto el **traje de Manuel Tirado** (`ARMOR_SPECS.md` §5).

---

## 2. El colegio: estructura y zonas

La estructura es un **híbrido entre una mansión de pillagers y un bastión de piglins** (`DESIGN.md` §1): patrullas, raids y capitanes por un lado; zonas de loot cerradas y con mucha hostilidad por otro. Los NPC **se quedan en su zona**, controlado **con jigsaw** (cada zona es una pieza; el NPC de la pieza no sale salvo por eventos).

**Zonas que el diseño nombra** (todas `PLANNED`; el tamaño y la posición de cada una: sin definir, V19):

| Zona | Qué hay según el diseño | Fuente |
|---|---|---|
| **Sala de profes** | 11 profesores | §3.1, §7.3 |
| **Oficinas** | Otros profesores; ropa de profesor ("solo en sus oficinas") | §3.1, §6 |
| **Aulas** | Un tutor por aula; un rollo de papel higiénico por salón; libros de 2do a 5to; los casilleros de 4to y 5to guardan la Ardilla PUCP | §4, §5.4, §5.5, §7.3 |
| **Aula de 3ro de secundaria** | Su tutora, Miss Alessandra | §7.3 |
| **Aula interactiva** (y de inglés) | Carpetas grises con CPU, sillas grises, PC; el **alumno vicioso**, que no sale | §4, §7.3 |
| **Escritorio negro** | Una PC | §4 |
| **Hall** | Sillas negras con rojo | §4 |
| **Auditorio** | Sillas rojas o blancas; aquí aparece el **fantasma de Manuel** al vencer a Hidalgo | §4, §7.4 |
| **Kiosko** | La mesa de kiosko y la **mamá de cuarto** | §4, §7.3 |
| **Patio** | A donde salen los NPC cuando suena la campana (90 s) | §4 |

**Sin zona en el diseño:** dónde están el escudo, la campana, los dos San Martín de Porres, los objetos perdidos, la laptop de Moisés, Hidalgo, Yahu, el Director, Pollo, la enfermera, Junior, las psicólogas, Lucio, Deivis y el personal de limpieza, Rosa, los brigadieres y la estudiantina. `NPC_SPECS.md` §2 lleva la columna de zona con las mismas celdas vacías.

---

## 3. Colocación de NPC

* Cada NPC pertenece a una zona (jigsaw); salvo eventos, **no sale**. Qué NPC va en qué zona: la tabla de §2 más la columna "Zona" de `NPC_SPECS.md` §2; el resto, sin definir.
* **Cuántos** NPC por zona (más allá de "11 en la sala de profes") y cuántos alumnos por salón: sin definir (V19).
* Los stats se asignan **al aparecer** (`NPC_SPECS.md` §1), así que la colocación no necesita crear mobs nuevos.
* **Un tutor por aula** y las aulas por grado: qué grados existen y cuántas aulas: el diseño nombra 6to de primaria, 2do, 3ro y 5to de secundaria, y 4to sin decir el nivel; no da la lista completa de grados (V19).

---

## 4. "Dentro de la estructura del colegio"

Tres reglas del diseño dependen de saber si alguien está **dentro del colegio**:

| Regla | Dónde |
|---|---|
| **Expulsión** (si sigues dentro 10 s después del ataque de Hidalgo, invoca alumnos o vexes) | `MECHANICS_SPECS.md` §5 |
| **Ritual** (si no se hace dentro de la estructura y con los dos santos, no funciona) | `BOSS_SPECS.md` §7 |
| **Invocar al boss** (solo dentro de la estructura) | `BOSS_SPECS.md` §8 |

El código debe poder preguntar "¿esta posición está dentro de un colegio?" y "¿de cuál?" (relevante para D4: el progreso es por colegio, ver `DESIGN.md` §6). Cómo se resuelve (la caja de la estructura, una pieza, un dato): es parte del plan de `W1`, no se decide aquí.

---

## 5. Mapa crafteable

* Tipo mapa de cartógrafo de vanilla; **lleva al colegio más cercano**.
* Ingredientes: **objetos legendarios de bunnidogs** (P1: lista por definir). Cruce con el otro mod: `docs/INTEGRATION_BUNNIDOGS.md` §6.
* Es la forma de llegar a los colegios que no son el garantizado cerca del spawn (cadena de eventos, paso 1: `BOSS_SPECS.md` §1).

---

## 6. Riesgos y dónde viven los archivos

* **Riesgo alto** (M5): estructuras jigsaw y mapa de exploración son familias de API que ningún código compilado en 26.2 cubre. **Una API nueva por chunk** (R6): `W1` (estructura) y `W2` (mapa y colocación garantizada) ya están separados por eso. Índice: `docs/REFERENCE.md` §8.
* **Archivos** (formato 26.x; las carpetas van en singular, `docs/REFERENCE.md` §4): `data/lsmmod/structure/` (piezas), `data/lsmmod/worldgen/` (estructura, template pools, conjunto de estructuras, tags de bioma o de planicie), `data/lsmmod/tags/`. Hoy vacías.
* **Patrones de bunnidogs** (registro de estructura y pieza, comando de depuración `/lsm ...` planeado): su código compilado **no está en este zip** (`DESIGN.md` §6, D5); solo su `API_NOTES.md`.
* **Arte (D3):** las estructuras las construye el usuario en el juego y se guardan como NBT; el sandbox no tiene Minecraft.

---

## 7. Vacíos que tocan este doc

V19 (tamaño, distribución, grados y cantidad de NPC), P1 (ingredientes del mapa), D4 (progreso por colegio), D6 (raids por nidos). Detalle en `docs/DESIGN.md` §5 y §6.

## 8. Catálogo del mundo suministrado (2026-10-05)

Se leen regiones Anvil del ZIP sin modificar el mundo. Se revisan pisos, paredes, puertas y mobiliario en cuatro niveles norte y tres sur. Se catalogan 93 espacios con cajas interiores, incluidas divisiones de una celda y espacios bajos. Los usos probables están marcados; no hay carteles que permitan asignar grados. Coordenadas y evidencia de cada espacio: `SCHOOL_SPACES.md`. Las superficies parciales de FramedBlocks se aproximan a celdas; varias cajas conservan recodos. Las zonas de circulación pueden superponerse y `where` devuelve todos los espacios coincidentes.

Comandos `/lsmmod rooms [página]`, `/lsmmod tp <espacio>`, `/lsmmod boundingbox <espacio> [segundos]`, `/lsmmod boundingbox off`, `/lsmmod where`. Requieren permisos equivalentes a `/tp` vanilla. TP valida en el mundo activo apoyo, espacio de pie, colisiones, fluidos y peligros; no mueve al jugador si el recinto no cabe. Boundingbox usa aristas con partículas privadas durante 30 segundos (5–120 configurable), sin poner ni quitar bloques. Una delimitación activa por jugador; se limpia al salir o detener el servidor.

El catálogo es fijo para el Overworld del mundo original. No detecta habitaciones nuevas automáticamente, no asigna NPC y no implementa W1/W2. BUILD SUCCESSFUL con Java 25 offline; pendiente de prueba en Minecraft con los mods usados por el mundo, incluido FramedBlocks.

### Nombres propios y entrega de resultados

`/lsmmod rename 5to secundaria` renombra el lugar actual y crea el alias `5to_secundaria`; `/lsmmod tp 5to_secundaria` y `/lsmmod boundingbox 5to_secundaria` lo aceptan, además del ID original. Los nombres admiten espacios y tildes, hasta 80 caracteres; los alias omiten tildes, usan minúsculas y sustituyen separadores por guiones bajos. Se rechazan nombres vacíos, caracteres de control, alias duplicados o que oculten el ID de otro espacio. `off` está reservado.

En superposiciones se elige el espacio de menor volumen de cajas y se comunica la selección; si los dos menores empatan, no modifica ninguno. `/lsmmod renameid <espacio> <nombre>` permite seleccionar explícitamente uno de los espacios que contienen al jugador. Tras guardar se muestra su delimitación durante 30 segundos. `rooms`, `where`, Tab, TP y contornos muestran o aceptan los nombres nuevos. El catálogo original y sus cajas permanecen intactos.

Los nombres se guardan inmediatamente en `<mundo>/lsmmod/school_space_names.json` usando archivo temporal y reemplazo atómico cuando el sistema lo admite. Cada comando lee los datos de su mundo; no hay una caché global de nombres. Errores de lectura/escritura se comunican sin sustituir los nombres por un catálogo vacío. Un mundo nuevo empieza con los nombres originales.

`/lsmmod export` escribe `<mundo>/lsmmod/school_spaces_export.json` con los 93 espacios, ID estable, nombre original y actual, alias, estado de renombrado, cajas y evidencia original. Ofrece **[Copiar nombres para pegar en el chat]**, que copia un JSON reducido con los nombres reasignados y sus IDs; sirve para pegarlo directamente en Codex. También se puede adjuntar el archivo exportado. En servidor el archivo queda en el servidor, pero el botón copia en el equipo del jugador. Nueva exportación reemplaza el informe anterior; incluye los cambios más recientes.

BUILD SUCCESSFUL y comprobación independiente de persistencia, conflictos de alias, separación entre mundos, geometría exportada y fallos de escritura. La interacción del botón y los comandos dentro de Minecraft siguen pendientes de prueba.
