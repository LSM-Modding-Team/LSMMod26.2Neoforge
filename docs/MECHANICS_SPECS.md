# MECHANICS_SPECS: reglas transversales (estados, territorio, llamados, patrullas, raids, efectos, hostilidad)

**Estado: SPEC, sin implementar.** Ningún chunk la usa todavía. Escrito en la sesión 3 (Tanda B); en la sesión 4 se actualizó con lo que el usuario resolvió (V1, V5, V12) y se quitaron los `PLANNED` de los specs de la Tanda C, que ya existen. Fuente: `docs/history/DESIGN_SOURCE_v1.md` §2 (efectos), §3.1-3.3, y de lo que §4, §5, §6, §7.3, §8 y §9.2 dicen sobre hostilidad.

**Implementan este spec (todos `PLANNED`, ver `docs/CHUNKS.md`):** `N0` (matriz de hostilidad en `rules/`, nace `RulesCheck`), `I2` (efectos), `N1` (llamados), `R1` (patrullas y raids; antes `RAID_PLAN.md`).

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*. `V#` = vacío detectado, en `docs/DESIGN.md` §5.3. **Si algo no está en el diseño, aquí dice "sin definir": no se rellena.** Qué hizo cada chunk distinto del plan: `N0` (§6.5).

---

## 1. Estados de un NPC

| Estado | Significado |
|---|---|
| **Neutral** | No ataca salvo que lo ataques o veas que atacas (según el tipo de NPC). |
| **Enojado** | Hostil activo hacia el jugador. |
| **Apaciguado** | Hostilidad suspendida temporalmente. |

* Aparte de los estados está la **inmunidad**: algunas prendas hacen que los NPC de un grupo "no te ataquen" (§6.2). El **set completo** (4 piezas) los vuelve **neutrales**.
* Al terminar el tiempo de un apaciguamiento, la hostilidad vuelve *(a confirmar: el diseño dice solo "temporalmente")*.
* **R11:** cada estado necesita una señal visible o audible en el mundo y su palabra explicada en el juego. La señal no está diseñada; se decide en el plan de `N1`.

---

## 2. Territorio y llamados

* Profesores (11 en la sala de profes, más otros en oficinas) y tutores (en sus aulas) **permanecen en su zona**.
* Salen **de forma temporal** solo por eventos especiales: escudo, mamá de cuarto, raids, llamados.
* **Llamar** = excepción a quedarse en la zona: el NPC llamado acude rápido desde donde esté y luego vuelve a su posición.

**Quién llama a quién:**

| Quien llama | A quién | Cuándo llama |
|---|---|---|
| Alumnos de **primaria** | A su profesor (V2) | Al ser enojados |
| Alumnos de **secundaria** | No llaman | — |
| Alumnos de **6to** | No llaman (§7.3) | — |
| **Rosa Sanmartiniana** | A cualquiera del colegio | Sin definir (V3) |
| **Pollo** | A la **estudiantina** (algunos alumnos de 3ro, 4to y 5to), que cambian de apariencia y sacan sus instrumentos | Sin definir (V3) |
| **Enfermera** y **Miss Alessandra** (tutora de 3ro de secundaria) | Se llaman mutuamente | Sin definir (V3) |
| **Hidalgo** | Es el **único que se teletransporta** cuando lo llaman | — |
| **Escudo** (no es un NPC) | Llama al azar a 6-10 profesores y 10-20 alumnos durante 60 s *(propuesta)* | Al pisarlo |
| **Instrumentos usados por NPC** (estudiantina y otros) | Llaman a 2-3 más *(propuesta)* | Al usarlos |

---

## 3. Patrullas

Un **profesor acompañado de unos alumnos**. El profesor es el **capitán**: matarlo otorga **Bad Omen LSM**. Equivalen a las patrullas de pillagers. Tamaño del grupo, dónde salen y cada cuánto: sin definir.

---

## 4. Raids y olas

* Emulan las raids de vanilla, pero con **alumnos y profesores en lugar de pillagers**. La raid de vanilla está pensada para aldeas y raiders, así que probablemente haga falta un gestor de olas propio *(por verificar)*; el plan está en `RAID_PLAN.md` (sin código; falta que el usuario lo acepte).
* **Dónde:** en lugares con muchos nidos de bunnidogs (cuántos, radio y cómo se elige el sitio: decisión D6, pendiente; la idea es contar POIs `bunnidogs:nest`, ver `docs/INTEGRATION_BUNNIDOGS.md` §5).
* **Disparador:** Bad Omen LSM.
* Los bunnidogs son hostiles a alumnos y profesores, así que los nidos funcionan como defensa natural contra los raiders.

**Composición por olas** *(propuesta de números)*:

| Ola | Composición |
|---|---|
| 1 | 6-8 alumnos de primaria |
| 2 | ~8 alumnos de secundaria |
| 3 | 3-4 profesores más el capitán con el estandarte del escudo |

---

## 5. Efectos propios del mod

| Efecto | Origen | Descripción y valores |
|---|---|---|
| **Bad Omen LSM** | Pisar el escudo; matar al capitán de una patrulla; atacar o robar a la mamá de cuarto | Variante propia del Bad Omen de vanilla. Dura 2 h, niveles I-V como en vanilla *(propuesta)*. **Genera raids.** La mamá da un nivel "más alto" (valor sin definir). |
| **Trackeo** | Pisar el escudo; mamá de cuarto | Los NPC te localizan automáticamente (como si tuvieras glowing), se mueven más rápido y hacen más daño. Escudo: 5 min con +25 % de daño *(propuesta)*. Mamá de cuarto: 10 min *(propuesta)*. Velocidad extra: sin número (V7). |
| **Estudioso** | Folder (30 s), cuaderno (20 s), PC (3 min) | Los profesores se vuelven **neutrales** contigo, al mismo nivel que con el uniforme de profesor. El del cuaderno vale solo para profesores de primaria (V12, resuelto; qué profesor es de primaria: V13). |
| **Regeneración (San Martín)** | Click derecho al bloque San Martín de Porres | Regeneración durante ~30 s. |
| **Expulsión** | Ataque de Hidalgo | Si sigues dentro de la estructura del colegio **10 s** después, **cada 8 s** invoca 3 alumnos de primaria (mini zombies) o 2 vexes de Manuel Tirado, **alternando** ambos *(propuesta de números)*. La Ardilla PUCP da inmunidad. Detalle de Hidalgo: `BOSS_SPECS.md` §2. Los vexes no tienen fila propia (V8). |

Los datos de cada item/bloque que da el efecto (folder, cuaderno, PC, escudo, San Martín) están en `ITEM_SPECS.md` y `BLOCK_SPECS.md`.

---

## 6. Matriz de hostilidad

La matriz es **disparador × grupo × estado**, más los modificadores que la cambian. Con lo que dice el diseño, muchas celdas están **sin definir**; se marcan así a propósito (V3, V4, V9, V11, V13, V16). Esta es la tabla que `N0` pasa a `rules/` y comprueba con `RulesCheck`.

### 6.1 Disparadores: qué los enciende y qué provocan

| Disparador | Quién reacciona | Resultado |
|---|---|---|
| **Atacar al NPC** | Ese NPC | Pasa a enojado. |
| **Verte atacar a otros** | **Personal de limpieza:** te atacan si ves atacar a cualquiera del colegio. **Deivis:** "muy importante que no te vea atacar a nadie". Resto: "según el tipo de NPC", sin definir (V4). | Enojados. |
| **Enojar a un alumno de primaria** | El alumno | Ataca como mini zombie y **llama a su profesor** (V2). |
| **Pisar el escudo** (bloque 3x3) | Todos los mobs | Bad Omen LSM + Trackeo (5 min, +25 % daño *(propuesta)*) + llama a 6-10 profesores y 10-20 alumnos durante 60 s *(propuesta)*. Los profesores salen de su aula de forma temporal. |
| **Atacar o robar a la mamá de cuarto** | **Todo el colegio, excepto 5to** (V9) | Como pisar el escudo pero **más fuerte**: Bad Omen más alto, más llamados, Trackeo de 10 min *(propuesta)*. |
| **Matar al capitán de una patrulla** | — | Bad Omen LSM. |
| **Romper una PC** | El **alumno vicioso** | Te persigue hasta matarte, **de un golpe**. |
| **Usar una PC** (click derecho) | — | Estudioso 3 min; 20 % de que llame a un profesor agresivo; cooldown de 5 min por PC *(propuesta)*. Atrae al vicioso, que se sienta a usarla. |
| **Campana de recreo** | Los NPC | Salen al patio (salvo algunos alumnos) durante 90 s; cooldown de 15 min *(propuesta)*. |
| **Ataque de Hidalgo** | Tú | Expulsión (§5). |
| **Items de pacificar/enojar** | Profesores y alumnos | Ver 6.1.1. |

**6.1.1 Dirección de cada item** (números y recetas: `ITEM_SPECS.md` §3 y §4):

| Item | Profesores | Alumnos |
|---|---|---|
| Bola de papel | Los enoja | Aturde ~3 s |
| Folder | Los apacigua (a todos) | — |
| Cuaderno | Apacigua **solo a los de primaria** | — |
| Celular | "Enoja a todos" (V11) | Apacigua al alumno enojado |
| Parlante portátil | Los enoja | Apacigua a todos |
| Sticker | **Todos los profesores atacan a quien lo lleve** (se pone en polos de alumnos o del jugador) | — |
| Guitarra eléctrica (legendario) | Apacigua | Apacigua (10 s a todos) |
| Instrumentos (uso del jugador) | Pacifican a los NPC en 10 bloques; la reacción exacta por tipo está pendiente (P5) | igual |

### 6.2 Modificadores que cambian la hostilidad hacia ti

**Excepción implementada para el laboratorio (2026-10-07):** el `teacher` se vuelve neutral frente a quien lleva las cuatro prendas concretas del uniforme. Ver un ataque del jugador a un profesor/alumno del laboratorio crea memoria individual persistente; ver que falta una pieza lo vuelve hostil hasta volver a ver el uniforme completo, salvo que recuerde una agresión. Cambios fuera de su visión no actualizan su observación. No modifica las reglas puras históricas de `N0` ni resuelve todas las celdas pendientes. Detalles en `docs/SCHOOL_NPCS.md`.


| Modificador | Efecto |
|---|---|
| **Traje de Manuel Tirado** | **Todos** los NPC completamente pacíficos contigo, **sin importar lo que hagas o hayas hecho**. |
| **Prenda de alumno** (pantalón de vestir/buzo, short, falda, polo de buzo o piqué, casaca de promoción 6to, zapatos, zapatillas, peinado escolar) | Los **alumnos** no te atacan (inmunidad). La casaca de 5to Sec. es igual *(a confirmar, A1)*. |
| **Prenda de profesor** (pantalón, camisa) | Los **profesores** no te atacan (inmunidad). |
| **Polo con stickers** | Ninguna inmunidad; los profesores siguen atacándote. |
| **Menos visión por prenda** | Más piezas reducen el rango de visión de los NPC. Con una pieza ven a la mitad *(propuesta)*; con 2 y 3: sin definir (V6). |
| **Set completo** (4 piezas) | Los NPC pasan a **neutrales**: solo te atacan si los atacas. Cualquier combinación de piezas de alumno cuenta *(a confirmar, A2)*. |
| **Estudioso** | Los profesores te ven **neutral** (§5). |
| **Ardilla PUCP** | Inmunidad ante los salones de 4to y 5to y ante Hidalgo; esos salones pasan a ser tus **aliados**. |
| **Trackeo** | Te localizan, más velocidad y daño (§5). Cómo interactúa con la visión reducida de las prendas: sin definir (V6). |

**Qué grupo es "Alumnos" y cuál "Profesores"** para la inmunidad (V5, resuelto por el usuario): *Profesores* = profesores, tutores, Miss Alessandra, enfermera, psicólogas, Director, Hidalgo, Junior, Moisés, Pollo y Lucio; *Alumnos* = todos los alumnos (incluidos Yahu, Rosa, brigadieres, Bianca, estudiantina y el vicioso); *sin grupo* = personal de limpieza, Deivis, mamá de cuarto, fantasma y boss. `NPC_SPECS.md` §2 lleva la columna. Huecos que quedan: alcance de "profesores" (V13) y piezas de grupos mezclados (V16).

### 6.3 Precedencia

El **único** orden definido es que el **Traje** gana a todo ("sin importar lo que hagas o hayas hecho"). Cómo se combinan inmunidad, neutral, Trackeo, escudo y mamá de cuarto cuando coinciden: **sin definir**. `N0` no debe decidirlo por su cuenta: lo propone como pick (R10) y el usuario lo acepta.

### 6.4 Forma prevista para `rules/` *(propuesta, sin código)*

Función pura, sin imports de Minecraft: entra (grupo del NPC, estado, piezas puestas por grupo, efectos activos, disparador) → sale (¿te ataca?, ¿rango de visión relativo?). Los grupos y disparadores de arriba son su vocabulario; añadir un grupo o disparador = una entrada de tabla más (R16).

### 6.5 Lo que hizo `N0` (entregado, `NOT COMPILED`)

Código en `rules/` (`Hostility.attacksPlayer(grupo, estado, provocado, equipo)` y `visionMultiplier(equipo)`). Orden que aplica:

| # | Regla | Origen |
|---|---|---|
| 1 | Traje de Manuel Tirado: no te ataca nadie | **Decidido** |
| 2 | NPC apaciguado: no te ataca | **Decidido** (por definición) |
| 3 | Profesor y llevas sticker: te ataca | **Pick sin aceptar** (gana a inmunidad y Estudioso) |
| 4 | NPC provocado (lo atacaste o un item lo enojó): te ataca | **Pick sin aceptar** (inmunidad y Estudioso solo calman la hostilidad no provocada) |
| 5 | Una pieza del grupo, o Estudioso ante profesores: no te ataca | **Decidido** el efecto; su lugar en el orden es pick |
| 6 | Si no, lo que diga el estado (enojado ataca, neutral no) | Pick |

Diferencias con el plan de §6.4: la función no recibe "disparador" ni devuelve la visión junto al ataque (son dos funciones); "provocado" es un dato que pone quien llama (el golpe, la bola de papel); el **set completo no aparece**: para el veredicto es redundante con la inmunidad por pieza (el set solo vale para el grupo del uniforme, `ARMOR_SPECS.md` §3), así que no cambia el resultado. **No modelado a propósito:** "verte atacar a otros" (V4; necesita tipos de NPC, llega con `N1`), Ardilla PUCP (depende de zonas), Trackeo y su relación con la visión (V6), cuaderno (V13), piezas mezcladas (V16). `visionMultiplier`: 0 piezas 1.0, 1 pieza 0.5 *(propuesta)*, 2 o más sin valor (V6). Riesgo de compilación: bajo; Java puro (`record`, `switch` como expresión).

---

## 7. Riesgos de compilación y de diseño

* Sin código todavía: el riesgo de diseño está en los vacíos de `DESIGN.md` §5.3. Los de este spec: V2-V4, V6, V7, V9, V11, V13 y V16 (V1, V5 y V12 ya están resueltos).
* El efecto de Bad Omen LSM y las raids propias dependen de APIs que ningún código compilado en 26.2 cubre (efectos propios, `EntityJoinLevelEvent`): `docs/REFERENCE.md` §8 y `docs/history/bunnidogs_patterns/API_NOTES.md`.
