# FEATURES: cómo se usa cada cosa en el juego

Guía del jugador. Crece **un párrafo por chunk** (R8): cuando un chunk añade algo que se ve o se usa en el juego, se explica aquí, con las palabras que el juego usa (R11).

**Estado: nada de esto está confirmado.** Todo lo de abajo viene de leer el código de B0 y B0.1; el usuario no ha reportado que compile ni que funcione (`docs/CHUNKS.md`). Si algo no se comporta como dice este archivo, lo que manda es lo que pase en el juego y hay que corregir este archivo.

Dónde está todo: pestaña creativa **"LSM Mod"** (icono: el libro de vanilla). Muestra todo lo registrado, así que un item nuevo aparece sin tocar la pestaña.

---

## Lo que existe hoy (B0 y B0.1, `NOT COMPILED`)

Seis bloques. Nombres del juego en inglés por ahora (`lang/en_us.json`); el archivo en español es la decisión D2, todavía sin hacer.

### Sillas (Elementary Chair, High School Chair, Teacher's Chair)

* **Se colocan** mirando hacia donde miras al ponerlas.
* **Clic derecho = sentarte.** Te sientas en una entidad invisible (`seat`) que desaparece sola cuando te levantas.
* **Craftear** (en la mesa de crafteo, forma de silla `G__` / `GGG` / `I_I`):
  * Elementary Chair: lingotes de oro (G) y de hierro (I).
  * High School Chair: losas de piedra lisa (S, arriba y en medio) y lingotes de hierro (I, abajo).
  * Teacher's Chair: cuero (L) y lingotes de hierro (I).
* Se rompen mejor con **pico**. Dureza 2.0.
* Ojo: estas tres sillas **no son** las variantes del diseño (Hall negro con rojo, aula gris, auditorio rojo o blanco); esas se añadirán después (`docs/BLOCK_SPECS.md` §3).

### Pupitres (Student's Desk, Teacher's Desk)

* **Ocupan 2 bloques.** Hay que **colocarlos con el item**: la segunda mitad la crea el juego al colocar, así que un `/setblock` deja una sola mitad.
* **Clic derecho** abre un inventario de **27 slots** (como un cofre). Los dos pupitres usan el mismo tipo de inventario.
* Se rompen mejor con **hacha**. Dureza 2.5. Al romper una mitad desaparece la otra y suelta el item una sola vez.
* **Craftear:**
  * Student's Desk: tablones (P, arriba), palos (S) y un cofre (C) en medio: `PPP` / `SCS` / `S_S`. *(propuesta de B0.1)*
  * Teacher's Desk: tablones (P, arriba), lingotes de hierro (I) y un cofre (C): `PPP` / `ICI` / `I_I`.
* Ojo: el diseño dice que las carpetas son **decorativas**; el código les da inventario. Está abierto (V14, `docs/BLOCK_SPECS.md` §3).

### Casillero (Locker)

* **Clic derecho** abre un inventario de **54 slots** (como un cofre doble).
* Se rompe mejor con **pico**. Dureza 3.0, suena a metal.
* **Craftear:** lingotes de hierro (I) y de cobre (P) arriba y abajo, bloque de hierro (B) y bloque de cobre (C) a los lados en medio: `IPI` / `B_C` / `IPI`.
* **Todavía no tiene botín:** los casilleros de hoy salen vacíos. Las loot tables de contenido (huevos podridos, polos, la Ardilla PUCP...) son del chunk `B2`.

---

## Lógica de hostilidad (N0, `NOT COMPILED`): todavía no se nota en el juego

`N0` añade solo las reglas de "quién te ataca" como código puro; **no hay ningún NPC que las use**, así que no hay nada que probar dentro del juego. Cuando existan los NPC (`N1`), las reglas serán: el **Traje de Manuel Tirado** hace que nadie te ataque; un NPC **apaciguado** no te ataca; **una prenda** del grupo (alumno o profesor) hace que ese grupo no te ataque; **Estudioso** hace que los profesores te vean neutral; el **sticker** hace que los profesores te ataquen igual; y un NPC al que atacas **sí** te ataca. Las prendas reducen además lo lejos que te ven los NPC (una pieza: la mitad). El orden entre el sticker y atacar al NPC es una propuesta mía que puedes cambiar (`docs/MECHANICS_SPECS.md` §6.5).

---

## Lo que todavía NO existe

Todo el resto del diseño está planeado y **no se puede usar ni encontrar**: items (regla, folder, cuaderno, instrumentos...), prendas y uniformes, NPC (alumnos, profesores, jefes), el colegio como estructura del mundo, el mapa, las raids, los efectos (Bad Omen LSM, Trackeo, Estudioso...), los discos y la música. Dónde está planeado cada uno: `docs/ROADMAP.md` y los specs de `START_HERE.md` §7.

Cuando un chunk añada una de esas cosas, aquí aparecerá un párrafo con cómo se obtiene, cómo se usa, qué se ve o se oye en el juego al activarla y qué palabra nueva introduce. Ejemplos de palabras que van a necesitar explicación dentro del juego (R11): *neutral*, *enojado*, *apaciguado*, *Trackeo*, *Estudioso*, *Bad Omen LSM*, *Expulsión*.
