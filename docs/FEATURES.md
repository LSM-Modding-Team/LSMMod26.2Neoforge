# FEATURES: cómo se usa cada cosa en el juego

Guía del jugador. Crece **un párrafo por chunk** (R8): cuando un chunk añade algo que se ve o se usa en el juego, se explica aquí, con las palabras que el juego usa (R11).

**Estado:** el proyecto compila con Java 25 / NeoForge 26.2.0.88. La compilación se comprueba con el harness en `tools/build/`. La prueba del escudo corregido dentro de Minecraft está pendiente; si difiere de esta guía, hay que corregirla.

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

## Escudo del colegio (school_shield)

Selecciona el Escudo del colegio en la pestaña LSM Mod y colócalo en el suelo. El item pone el conjunto completo de 3×3 de una sola vez, con el centro en el lugar donde lo colocas. El escudo queda mirando hacia arriba y el borde recto apunta hacia donde miras al colocarlo y la punta queda hacia ti. Usa las nueve texturas de escudo.zip con las uniones corregidas, con el símbolo blanco, fondo rojo oscuro, contorno dorado y fondo de ladrillos de piedra del End. La pieza lsm1 es el centro; los bloques muestran sus texturas en las seis caras. El inventario muestra el escudo completo. Necesita nueve posiciones libres o reemplazables, a la misma altura. No requiere clic adicional y no tiene ampliación. Romper una pieza retira el conjunto y devuelve un solo item en supervivencia, ninguno en creativo. Sin receta ni efectos de NPC por ahora. Prueba en Minecraft pendiente.

## Sillas de aula interactiva, hall y auditorio

Las seis sillas nuevas se incluyen en la pestaña LSM Mod como ítems independientes. Se colocan orientadas hacia el jugador y permiten sentarse con clic derecho sin objeto en la mano. Cada variante usa la colisión de su modelo. Sus recetas y drops existentes quedan conectados a los registros. Compilación verificada; prueba en Minecraft pendiente.

### Laptops decorativas (2026-10-05)
En la pestaña LSM Mod aparecen Laptop y Laptop de Moisés. Se colocan abiertas, orientadas hacia el jugador, y se recuperan al romperlas. La común es gris/plateada; la de Moisés es dorada con detalles celestes de diamante. Todavía no reproducen música ni abren una interfaz. Compilación verificada; prueba en Minecraft pendiente.

### PC escolar (2026-10-05)
El bloque PC escolar está en la pestaña LSM Mod. Representa únicamente un monitor con marco oscuro, carcasa de grosor moderado, soporte y base. Se orienta hacia el jugador al colocarlo y deja su propio item al romperse. Todavía no tiene efectos ni interfaz. Compilación verificada; prueba en Minecraft pendiente.

### Mesa de computación (2026-10-05)
En la pestaña LSM Mod aparece Mesa de computación. Necesita dos espacios verticales libres y ocupa una sola columna. Clic derecho con PC escolar instala una unidad y muestra monitor, teclado y CPU pequeña. No acepta otros objetos ni una segunda PC. Shift + clic derecho con mano vacía retira la PC; romper la mesa en supervivencia permite recuperar la mesa y la PC. El estado se conserva en el mundo. Todavía no tiene funciones electrónicas. Compilación correcta; prueba en Minecraft pendiente.
