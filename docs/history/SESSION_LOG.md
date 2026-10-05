# SESSION_LOG: 2-4 líneas por sesión (nunca hace falta para continuar)

Historia, no estado. El estado vive en `START_HERE.md` §2. Una sesión = una conversación con Claude.

* **Sesión 1** (fecha no registrada). Se escribió el diseño (`docs/history/DESIGN_SOURCE_v1.md`, entonces `mod_colegio_dungeon.md`). Ya existían la plantilla de NeoForge y los 6 bloques (3 sillas, 2 pupitres, 1 casillero), que pasan a ser el baseline `B0`. Se escribió el plan de documentación (`DOCS_PLAN.md`).
* **Sesión 2** (2026-10-04). **Tanda A** de docs: `START_HERE.md`, `README.md`, `CHUNKS.md`, `ROADMAP.md`, `REFERENCE.md` y `tools/ParseAll.java`. De bunnidogs solo se copió su `API_NOTES.md` a `docs/history/bunnidogs_patterns/` (D5 quedó parcial). Se leyó el código de B0 y se anotaron las discrepancias diseño ↔ código.
* **Sesión 3** (2026-10-04). Chunk `B0.1` (limpieza de B0: clave `lang` del asiento, receta del pupitre de alumno, descripción del mod, comentarios). **Tanda B** de docs: `DESIGN_SOURCE_v1.md` (literal), `DESIGN.md`, `MECHANICS_SPECS.md`, `NPC_SPECS.md`; 12 vacíos V1-V12. **Tanda C** de docs (la sesión que dicen los propios specs; no quedó en el zip): `ITEM_SPECS`, `ARMOR_SPECS`, `BLOCK_SPECS`, `WORLD_SPECS`, `BOSS_SPECS` (vacíos V13-V22). El usuario resolvió V1, V5 y V12.
* **Sesión 4** (2026-10-04). El usuario subió los 5 specs de la Tanda C porque no estaban en el zip; se copiaron a `docs/`. Se sincronizaron `DESIGN.md`, `NPC_SPECS.md` y `MECHANICS_SPECS.md` con lo resuelto (V1, V5, V12) y con V13-V22. **Tanda D** de docs: `INTEGRATION_BUNNIDOGS.md`, `API_NOTES.md`, `FEATURES.md`, `history/SESSION_LOG.md`, `history/CHUNKS_FULL.md`; `DOCS_PLAN.md` pasó a `history/`. Vacíos nuevos V23-V24. Solo docs: no se tocó el mod ni se compiló nada. Después, a pedido ("lo más chico solo docs"): `docs/RAID_PLAN.md` (plan, sin código; vacíos V25-V26).
* **Sesión 5** (2026-10-04). El usuario dijo "continúa" a secas; B0 y B0.1 seguían sin confirmar. Chunk `N0`: `rules/` con `NpcGroup`, `NpcState`, `PlayerGear`, `Hostility` y `tools/RulesCheck.java` (19/19). Sin API de Minecraft. La precedencia quedó como pick sin aceptar y V4, V6, V13, V16 sin rellenar.
  Después, el usuario recordó `./gradlew runClient` y mandó su `options.txt`: tarea `prepareClientRun` en `build.gradle` y `tools/run-defaults/options.txt` (`T0`).
  Después, a pedido del usuario: nueva regla R17 (la respuesta empieza por la lista exacta de archivos añadidos/modificados/borrados, sacada con `diff -rq` contra una copia base). Solo docs.
  El usuario reportó que `T0` rompía Gradle (`prepareClientRun` ya existía en el plugin). `T0.1`: tarea renombrada a `applyClientOptions`; nueva carpeta `newresources/` con README (recursos sin usar).
  El usuario reportó que `T0.1` fallaba: `file()` dentro de `doLast` rompe la configuration cache de Gradle 9.2.1. `T0.2`: los `File` se resuelven al configurar. Nota para el futuro en `REFERENCE.md` §10.

### 2026-10-05 — Escudo solicitado por el usuario

Se añadió el bloque school_shield y su item, estados y modelos para un único escudo de 3×3. El centro crea ocho piezas al clic derecho; no sobrescribe bloques y el conjunto devuelve un solo item. Textura recreada de la referencia con fondo y borde blanco. Prueba dentro del juego pendiente.

Verificación del escudo: Gradle BUILD SUCCESSFUL (Java 25, NeoForge 26.2.0.88). Sintaxis Java y JSON válidos; comprobado el mapeo UV de las nueve piezas en cuatro orientaciones. Prueba en juego pendiente.

### 2026-10-05 — Corrección del escudo y harness persistente

El usuario corrigió la petición: suelo horizontal completo desde su colocación, sin ampliación. Se actualizan colocación, modelos y textura pixelada de 96×96, cuatro colores, 32×32 por pieza. Se guardan tools/build/compile.sh y environment.sh, con selección/instalación de Java 25, proxy/TLS y cachés. AGENTS.md, START_HERE y REFERENCE documentan la compilación para próximas conversaciones.

El harness persistente se ejecutó correctamente: BUILD SUCCESSFUL. La versión anterior queda sustituida por esta corrección. Se verifican layout horizontal, textura y recursos; sin prueba dentro de Minecraft.

### 2026-10-05 — Alfombra del escudo

A petición del usuario, se reduce la altura a 1/16 y se exige soporte como en CarpetBlock. Se elimina el fondo blanco exterior mediante transparencia, conservando el borde fino; modelos recortados al contorno. BUILD SUCCESSFUL usando el harness persistente. Prueba en Minecraft pendiente.

### 2026-10-05 — Restaurar y publicar

El usuario pide volver al bloque completo anterior a la alfombra y hacer push. Se restauran geometría, colisión, textura, dureza y sonido de B-SHIELD.1. Se conservan la colocación inmediata 3×3 y el harness reutilizable, incluyendo una excepción de .gitignore para versionar tools/build.

Compilación de la restauración: BUILD SUCCESSFUL. Rama de publicación: work.

### 2026-10-05 — Preferencia Git y corrección del destino

El usuario confirma: trabajar y pushear siempre en master, sin crear ramas. Se guarda en AGENTS.md, START_HERE.md y REFERENCE.md §9. Se aplica el escudo sobre el master remoto actual, conservando las sillas añadidas en otra conversación y combinando registros y traducciones.

### 2026-10-05 — Integrar escudo.zip

El usuario aporta nueve PNG, modelos exportados y bbmodels. Se usan sus PNG intactos y la geometría original, corrigiendo referencias a lsm2 y namespaces. Distribución 9 5 8 / 2 1 3 / 7 4 6, con lsm1 en el centro y mosaico de inventario 48×48. BUILD SUCCESSFUL; publicación directa a master.

### 2026-10-05 — Uniones del escudo y ladrillos del End

Corrección de las uniones marcadas en azul moviendo los píxeles de lsm2, lsm7 y la franja de lsm6. Fondo de end_stone_bricks real de Minecraft 26.2, mosaico de inventario y bbmodels sincronizados. BUILD SUCCESSFUL; publicación a master y entrega del JAR.

Al integrar los cambios remotos de San Martín de Porres se detectó la eliminación del registro del escudo. Se restaura su bloque, item y nombre inglés conservando el nuevo bloque de la estatua; compilación del conjunto verificada antes de entregar el JAR.
