# NPC_SPECS: un NPC por fila, sistema de stats y rasgos

**Estado: SPEC, sin implementar.** No existe ninguna entidad de NPC en el código (la única entidad es el asiento `SeatEntity`). Escrito en la sesión 3 (Tanda B); en la sesión 4 se actualizaron los grupos de inmunidad y la fila de 6to con lo que el usuario resolvió (V1, V5; ver `ARMOR_SPECS.md` §2 y `BOSS_SPECS.md` §3). Fuente: `docs/history/DESIGN_SOURCE_v1.md` §7.2, §7.3, §7.4, §7.5, §9.2.

**Implementan este spec (todos `PLANNED`, ver `docs/CHUNKS.md`):** `N1` (NPC base: una entidad de alumno), luego los demás NPC de uno en uno; `K1` (minibosses y boss). La matriz de hostilidad que usan está en `docs/MECHANICS_SPECS.md` §6.

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*. `V#` / `P#` = vacío o pendiente en `docs/DESIGN.md` §5. **Donde el diseño no dice nada la celda dice `—` o "sin definir": no se rellena por inferencia.** Qué hizo cada chunk distinto del plan: ninguno todavía.

---

## 1. Sistema de stats y rasgos de los alumnos

Para dar variedad **sin crear mobs nuevos** (principio 3 de `DESIGN.md` §1). Parecido al de los bunnidogs pero más sencillo y **sin reproducción**.

| Elemento | Valores |
|---|---|
| **Stats** (0-5) *(propuesta)* | Vitalidad, Fuerza, Velocidad, Percepción (= rango de visión) |
| **Rasgos opcionales** *(propuesta)* | Vicioso (**fijo** en el Aula interactiva), Líder, Atlético |
| **Cuándo se asignan** | Al aparecer. No cambian ni se heredan. |

**Sin definir:** qué número real de vida, daño y velocidad corresponde a cada punto de stat (P4); qué hace exactamente cada rasgo salvo Vicioso; los stats de secundaria, 4to y 5to (P6); cómo se combina la Percepción con la reducción de visión por prendas (V6). Cada NPC de abajo es "muy OP por defecto" (principio 1): los números no se eligen todavía.

---

## 2. Tabla de NPC

**Imp.** = importante para la condición del Director (`DESIGN_SOURCE_v1.md` §9.2): **Sí** = hay que matarlo; **No** = no hace falta. **Grupo de inmunidad** (prendas, `MECHANICS_SPECS.md` §6.2), **V5 resuelto por el usuario**: *Alumnos*, *Profesores* o *Sin grupo*. Qué profesor es "de primaria" y si los items "para profesores" alcanzan a todo el grupo: V13.

| NPC | Grupo de inmunidad | Imp. | Zona | Comportamiento y stats (lo que dice el diseño) | Llama / lo llaman |
|---|---|---|---|---|---|
| **Personal de limpieza** | Sin grupo | Sí | — | Neutrales; te atacan si te ven atacar a cualquiera del colegio. Usan escobas y trapeadores. | — |
| **Deivis** | Sin grupo | **No** | — | El más fuerte de todos (velocidad normal). Muy importante que no te vea atacar a nadie. Se lee como personal de limpieza especial (V10). | — |
| **Alumnos de primaria** | Alumnos | Sí, salvo **un décimo** (basta con matar al 90 %; no importa cuáles; **los de 6to entran en ese 100 %**) | — | Si los enojas, atacan como **mini zombies**. Lanzan bolas de papel en masa como ataque a distancia. | **Llaman a su profesor** al ser enojados (V2) |
| **Alumnos de 6to** | Alumnos | Sí; **cuentan como primaria** para el 90 % (V1, resuelto) | — | Se comportan como secundaria pero con **tamaño de primaria**; casaca de minipromo. | No llaman |
| **Alumnos de secundaria** | Alumnos | Sí | — | Stats pendientes (P6). | No llaman |
| **Alumnos de 4to / 5to** | Alumnos | Sí (Bianca es la excepción) | — | **Una entidad distinta por grado.** Stats pendientes (P6). Sus casilleros guardan la Ardilla PUCP (exactamente una). | — |
| **Alumno vicioso** | Alumnos (rasgo Vicioso, fijo) | Sí | **Aula interactiva** (no sale) | Se sienta a usar la PC. Si le rompes la PC te persigue y te mata **de un golpe**. | — |
| **Estudiantina** | Alumnos | Sí | — | Alumnos de **3ro a 5to** (algunos). Se transforman (cambian de apariencia, sacan instrumentos) al ser llamados por Pollo. Con instrumento buffean +20 % a alumnos y profesores en 15 bloques durante 20 s y llaman a 2-3 más *(propuesta)*. | Los llama **Pollo** |
| **Rosa Sanmartiniana** | Alumnos | Sí | — | Alumna especial. | **Puede llamar a cualquiera** del colegio (V3: cuándo) |
| **Brigadieres** | Alumnos | Sí | — | Dos: uno de **6to de primaria** y otro de **5to de secundaria**. | — |
| **Yahu (Alcalde)** | Alumnos | Sí (miniboss) | — | Alumno de **2do de secundaria**. Miniboss; stats sin definir. | — |
| **Bianca** | Alumnos | **No** | — | Alumna de **4to**. Los bunnidogs **no la atacan** (marca propia en la entidad de LSM; ver `INTEGRATION_BUNNIDOGS.md`). | — |
| **Tutores** | Profesores | Sí | **Sus aulas** | Permanecen en su aula. | — |
| **Profesores** | Profesores | Sí | **11 en la sala de profes**, más otros en sus oficinas | Intentan quedarse en su zona. Usan la regla. Uniforme con protección y durabilidad altísimas (casi imposible matarlos sin romperlo). Capitanes de patrulla. Los enoja la bola de papel; **todos atacan a quien lleve un sticker**. | Los llaman los alumnos de primaria; el escudo llama a 6-10 |
| **Miss Alessandra** | Profesores | Sí | Su aula (los tutores permanecen en su aula; *a confirmar*) | **Tutora de 3ro de secundaria.** | Se llaman mutuamente con la **enfermera** |
| **Enfermera** | Profesores | Sí | — | — | Se llama con **Miss Alessandra** |
| **Psicólogas** | Profesores | Sí | — | **Pendiente** (P6). | — |
| **Junior** | Profesores | Sí | — | **Lesionado:** no muy débil, pero **lento**. | — |
| **Pollo** | Profesores | Sí | — | Llama a la estudiantina. | **Llama a la estudiantina** |
| **Lucio** | Profesores | **No** | — | Neutral. | — |
| **Moisés** | Profesores | Sí | — | Dueño de la **laptop mayor** (bloque de mayor rareza que suena en toda la estructura). | — |
| **Mamá de cuarto** | Sin grupo | Sí | **Kiosko** (mesa de kiosko) | Vendedora, rol de trueque. Si la atacas o le robas: como pisar el escudo pero más fuerte; **todo el colegio te ataca excepto 5to** (V9). | — |
| **Director** | Profesores | — (es el objetivo) | — | **Invulnerable** hasta matar a todos los importantes. Entre sus drops: el **mapa al ataúd**. | — |
| **Hidalgo** | Profesores | Sí (miniboss) | — | **No usa regla.** Su ataque aplica **Expulsión**. La Ardilla PUCP da inmunidad contra él. Al matarlo aparece el fantasma de Manuel. | Es el **único que se teletransporta** al ser llamado |
| **Fantasma de Manuel Tirado** | Sin grupo | Sí (miniboss) | **Auditorio**, aparece tras vencer a Hidalgo | Estilo fantasma de *Ice and Fire*. Su drop es el **alma**. | — |
| **Manuel Tirado Andrade** (boss final, invocado) | Sin grupo | — | Solo se invoca **dentro de la estructura del colegio** | Tiene **fases**. Mientras su vida baja, revive a todos los muertos como **fantasmas** (los fantasmas son vulnerables). Sigue vivo hasta que lo matas. Su drop es el **traje**. | — |

**Mencionados sin fila propia en el diseño (V8):** los **vexes de Manuel Tirado** (los invoca Expulsión) y los **fantasmas** que revive el boss (no se sabe si uno por NPC muerto ni con qué stats). Se definen en `BOSS_SPECS.md`.

**Cuenta de importantes** (para `N0`): importantes = todos salvo Bianca, Lucio, Deivis y un décimo de primaria. Incluye, **por nombre en el diseño**: Yahu, el vicioso, Rosa Sanmartiniana y los brigadieres; profesores, tutores (incluida Miss Alessandra), personal de limpieza salvo Deivis, psicólogas, enfermera, Moisés, Pollo, Junior, el resto de los alumnos y los minibosses. El 90 % de primaria se cuenta **con los alumnos de 6to dentro** (V1).

---

## 3. Lo que sí está en el mundo hoy

Nada de esto existe aún: **ningún NPC está implementado.** Ningún huevo de aparición, ni modelo, ni renderer, ni estructura donde colocarlos (`WORLD_SPECS.md`).

---

## 4. Preguntas para el plan de `N1` *(a confirmar; no son decisiones)*

Antes de codificar el primer NPC hace falta un plan (R10). Lo que el diseño deja abierto y el plan debe proponer como pick:

* Cuántos tipos de entidad: el diseño pide variedad "sin crear mobs nuevos" y una entidad distinta por grado en 4to/5to; para el resto no dice nada.
* Cómo se ve cada estado (neutral, enojado, apaciguado) en el mundo (R11).
* Cómo se materializa "se quedan en su zona" (jigsaw + una zona por NPC) y el llamado de ida y vuelta.
* Qué patrones de entidad reutilizar de bunnidogs: sus filas COMPILED están en `docs/history/bunnidogs_patterns/API_NOTES.md`; **su código compilado no está en este zip** (`DESIGN.md` §6, D5).
