# START_HERE: lee SOLO este archivo al empezar

Proyecto **LSM Mod** (`lsmmod`): mod de NeoForge para Minecraft 26.2. Los demás docs se leen **solo** para el trabajo que nombra el mapa de la sección 7. No leas `DESIGN`, `API_NOTES` ni `history/` "para orientarte" (R1).

**Git:** trabajar y hacer push siempre a la rama existente `master`. No crear ramas nuevas. Antes de publicar, traer los cambios de `origin/master` e integrarlos conservando el trabajo de otras conversaciones. No usar force-push.

Idioma: los docs están en español, el código, los identificadores y los comentarios en inglés. Respondes en el idioma en que escribe el usuario.

---

## 1. Qué es (30 s) y vocabulario fijo

Mod ambientado en un **colegio** (dungeon): estructura del colegio, NPC (alumnos y profesores) muy fuertes que se quedan en su zona, armas y armadura, raids, minibosses y un boss final. Hoy existen 6 bloques (3 sillas, 2 pupitres, 1 casillero) y la lógica pura de hostilidad en `rules/` (sin conectar al juego); todo lo demás está `PLANNED`. Detalle: `docs/REFERENCE.md` §1.

Marcas de los docs (se conservan literales en todos): sin marca = **decidido**; *(propuesta)* = valor de balance elegido, ajustable; *(a confirmar)* = supuesto sin validar.

**Vocabulario fijo** (una palabra, un significado; no uses sinónimos). Definiciones del diseño original §2 (`docs/DESIGN.md` §2); ya no son provisionales.

| Palabra | Significado único |
|---|---|
| neutral | No ataca salvo que lo ataques o veas que atacas (según el tipo de NPC) |
| enojado | Hostil activo hacia el jugador |
| apaciguado | Hostilidad suspendida temporalmente |
| trackeo | El efecto propio "Trackeo" del jugador: los NPC te localizan (como con glowing), se mueven más rápido y hacen más daño; no se usa la palabra para nada más |
| llamar | Excepción a quedarse en su zona: el NPC llamado acude rápido desde donde esté y luego vuelve a su posición |
| importantes / no importantes | Importantes: hay que matarlos para volver vulnerable al Director (son todos salvo los no importantes). No importantes: Bianca, Lucio, Deivis y un décimo de los alumnos de primaria. Una columna en `NPC_SPECS` |
| colegio | La estructura generada del mundo (jigsaw), no el concepto de escuela |
| patrulla | Un profesor (capitán) acompañado de unos alumnos; matar al capitán da Bad Omen LSM. Distinta de una raid |
| raid | Ataque por olas con alumnos y profesores, propio de LSM (no la raid de aldeas de vanilla); la dispara el Bad Omen LSM |

---

## 2. ESTADO ACTUAL (se edita al final de CADA sesión y debe ser siempre verdadero)

**Escritorio gris 2026-10-05:** se añade `gray_computer_desk`, de 2×1×2 bloques, con la misma textura `chair_light_gray` de la silla del aula interactiva. Cubierta, bandeja inferior de teclado y alojamiento lateral abierto para CPU; ambos vacíos hasta instalar una sola PC. Comparte la interacción de `computer_desk`: clic derecho con PC, rechazo de otros items/segunda PC, retirada con Shift y mano vacía. Las cuatro partes comparten un estado persistente; colocación valida los cuatro espacios, retirada conjunta y drops únicamente en el origen inferior. Modelos vacío/ocupado y colisiones generados juntos. BUILD SUCCESSFUL con Java 25 y wrapper Windows offline; prueba en Minecraft pendiente.

**Mesa de computación 2026-10-05:** se añade `computer_desk`, con huella 1×1 y dos mitades verticales. Madera y estructura oscura inspiradas en la foto del usuario, bandeja para teclado y repisa superior. Un alojamiento exclusivo para una unidad de `pc`, insertada con clic derecho; estado `has_pc` persistente y sincronizado entre mitades. Ocupada muestra monitor, teclado y CPU pequeña. Shift + clic derecho con mano vacía retira la PC. La loot table de la mitad inferior entrega mesa y PC instalada; se reutiliza la retirada vertical existente y se bloquean pistones. Sin efectos de PC ni menú. BUILD SUCCESSFUL con Java 25, wrapper Windows offline. Prueba dentro de Minecraft pendiente.

**PC escolar 2026-10-05:** se añade `pc`, representada únicamente por un monitor LCD escolar de gama media-baja: marco oscuro, carcasa de grosor moderado con trasera escalonada y rejillas, soporte y base. Modelo y textura propios, registro de bloque/item, cuatro orientaciones, colisión ajustada, traducciones y drop. Sin torre, teclado ni funciones. Compilación verificada con Java 25 y wrapper Windows offline: BUILD SUCCESSFUL. Prueba dentro de Minecraft pendiente.

**Laptops 2026-10-05:** se añaden `laptop` (gris/plateada, portátil convencional de gama media-baja) y `moises_laptop` (oro con detalles celestes de diamante). Modelos abiertos con teclado, panel táctil, pantalla y cámara; texturas propias de 64×64, registros de bloque/item, nombres en español/inglés y drops. Bloques decorativos orientables, sin interacción, inventario ni música. Aparecen automáticamente en la pestaña LSM Mod. Compilación verificada con Java 25 y `gradlew.bat build --offline --max-workers=4`: BUILD SUCCESSFUL. El intento con Bash encontró scripts con CRLF; se usó la alternativa Windows documentada. Prueba dentro de Minecraft pendiente.

**Actualización de registros 2026-10-05:** 2026-10-05: se completan los registros de bloque e item de englishroomchair, hallchair y las cuatro plasticchair, utilizando NewChairShapes. La pestaña creativa las incluye automáticamente. Se conservan las tres sillas originales y school_shield. Compilación verificada en Windows con Java 25 y gradlew.bat build --offline: BUILD SUCCESSFUL. Prueba en Minecraft pendiente.

**Actualización 2026-10-05:** `school_shield` utiliza los modelos y las nueve texturas de `escudo.zip`, de 16×16 por pieza, con las uniones del símbolo y de la franja dorada corregidas y fondo real de `end_stone_bricks`. Se coloca completo como un suelo horizontal de bloques completos 3×3. Distribución: `6 4 7 / 3 1 2 / 8 5 9`; lsm1 es el centro. Se corrigen las rutas de textura de los modelos, se preservan geometría y caras originales, y se guarda el proyecto editable en `newresources/escudo/bbmodels/`. El item muestra el escudo completo de 48×48. Se mantiene la retirada conjunta con un solo drop, sin ampliación ni comportamiento de alfombra. **Compilación verificada:** `bash tools/build/compile.sh build --offline`, BUILD SUCCESSFUL. Prueba en Minecraft pendiente.

**Harness persistente:** `bash tools/build/compile.sh` (o `bash tools/build/compile.sh build --offline` con las dependencias en caché). Configura Java 25, proxy/TLS y cachés; instalación inicial cuando hace falta. Ver `tools/build/README.md` y `AGENTS.md`. Este proyecto puede compilarse desde otras conversaciones; las prohibiciones antiguas por falta de Gradle no aplican al entorno actual.

**Historial anterior:** sesión 5, 2026-10-04 (`T0.2`: arreglo de la configuration cache; antes, `T0.1`: nombre de la tarea de Gradle y carpeta `newresources/`; antes, chunk `N0`: lógica pura de hostilidad en `rules/`; nace `tools/RulesCheck.java`; después, tooling: `./gradlew runClient` con las opciones del usuario, `T0`).

* **Chunk histórico de tooling:** **T0.2 (arreglo de tooling: `applyClientOptions` sin `file()` dentro de `doLast`). Estado: NOT COMPILED.** `T0` (nombre de tarea duplicado) y `T0.1` (rompía la configuration cache) fallaron en Gradle y quedaron `SUPERSEDED`. En el último reporte Gradle ya descargó assets y generó los artefactos de Minecraft (`createMinecraftArtifacts` terminó); el mod todavía no se ha compilado ni arrancado.
* **Chunk histórico de lógica:** **N0 (matriz de hostilidad en `rules/`). Estado: NOT COMPILED.** Los anteriores, **B0.1** y **B0** (los 6 bloques), siguen **sin confirmar**: nadie ha reportado si compilan ni si funcionan.
* **Qué hizo N0 (sin API de Minecraft, nada visible en el juego):** paquete `rules/` con `NpcGroup`, `NpcState`, `PlayerGear` y `Hostility` (`attacksPlayer`, `visionMultiplier`), más `tools/RulesCheck.java` (19 comprobaciones, 19 pasan; `ParseAll` 19 archivos, 0 errores). **La precedencia (`MECHANICS_SPECS.md` §6.3) es un pick MÍO sin aceptar**: está aislado en un solo método (`Hostility.attacksPlayer`). Lo no definido (V4, V6, V13, V16) NO se rellenó. Detalle: `docs/MECHANICS_SPECS.md` §6.5.
* **Qué hizo B0.1 (sin features, sin API nueva):** clave `lang` del asiento corregida (`entity.lsmmod.seat`); receta nueva de `students_desk` (`PPP`/`SCS`/`S S`: tablones, palos, cofre; *(propuesta)*); descripción real en `neoforge.mods.toml`; comentarios corregidos en `ModBlocks`, `ModItems`, `SeatEntity`, `ModEntities`. Detalle: `docs/history/CHUNKS_FULL.md`.
* **Qué verificar en el juego (cierra B0 y B0.1):** `./gradlew build` y `./gradlew runClient`; en la pestaña creativa "LSM Mod" salen 6 bloques; las sillas se montan con clic derecho; el pupitre de alumno y el del profesor ocupan 2 bloques y abren 27 slots; el casillero abre 54 slots; la receta del pupitre de alumno funciona (`docs/FEATURES.md` lo explica).
* **Docs: el plan está completo (Tandas A-D).** Los 5 specs de la Tanda C (`ITEM`, `ARMOR`, `BLOCK`, `WORLD`, `BOSS`) no estaban en el zip: el usuario los subió en la sesión 4 y se copiaron a `docs/`. En esta sesión: `DESIGN.md`, `NPC_SPECS.md` y `MECHANICS_SPECS.md` se sincronizaron con lo que el usuario resolvió (V1, V5, V12); nuevos `INTEGRATION_BUNNIDOGS.md`, `API_NOTES.md` (tabla vacía y mapa de riesgo), `FEATURES.md`, `history/SESSION_LOG.md`, `history/CHUNKS_FULL.md`; `DOCS_PLAN.md` pasó a `docs/history/`. Ninguna decisión de balance nueva.
* **Hay que decirle al usuario:** (1) **Resueltos por él:** V1, V5, V12. **Abiertos:** V2-V4, V6-V11 y **V13-V26** (`docs/DESIGN.md` §5.3), más los pendientes P1-P7 y los supuestos A1-A3. (2) Los picks de `docs/BLOCK_SPECS.md` §3 (no borrar ni renombrar bloques, dejar que las sillas se monten, dejar los 27 slots de las carpetas) y D1-D7 siguen sin respuesta; "sigue con tus picks" acepta todos. (3) **Hace falta `bunnidogs-mod.zip`** antes del hito M4: aquí solo está su `API_NOTES.md`, no su código compilado (D5), y los datos de `docs/INTEGRATION_BUNNIDOGS.md` §2 no se han re-verificado.
* **Próximos candidatos:** (a) el usuario confirma B0 y B0.1 ("compiló" / "funcionó" / errores); (b) el usuario responde los vacíos y las decisiones, o dice "sigue con tus picks"; (c) el usuario acepta o cambia el pick de precedencia de `N0` (`docs/MECHANICS_SPECS.md` §6.5); luego `B1` (alinear bloques; depende de los picks abiertos de V14), `B2` (casilleros con loot, sin API nueva) o `I1`; (d) el usuario acepta o ajusta `docs/RAID_PLAN.md` (escrito en la sesión 4, solo docs); falta el plan de `N1` (R10) antes de M4.
* **Decisiones abiertas:** D1-D7 y los vacíos viven en `docs/DESIGN.md` §6 y §5.3. **D2 (docs en español) se aplicó como pick** y se cambia si el usuario dice otra cosa.
* **Discrepancias código ↔ diseño sin resolver:** tabla en `docs/BLOCK_SPECS.md` §3; inventario en `docs/REFERENCE.md` §1.

---

## 3. Cómo leer al usuario (R4)

El usuario habla suelto, en español o inglés. Nunca le pidas plantillas ni formularios (R12).

| Dice | Haces |
|---|---|
| "funcionó / anduvo / todo bien" | El último chunk pasa a `WORKS`; haces el siguiente chunk |
| "compiló" | El último chunk pasa a `COMPILES` |
| Errores o log pegado | Arreglarlos PRIMERO, como chunk propio, sin features nuevas |
| "continúa" a secas | Dices en una línea que el último chunk está sin confirmar y haces un chunk pequeño |
| "continúa con X" | Haces X; si es grande o no tiene spec, plan en `docs/` primero y sin codificar hasta que lo diga |
| "elige tú" / "tus picks" / "sigue con tus picks" | Eliges y justificas en una línea |
| "solo docs" / "limpia" | Tocas solo docs y tooling |

---

## 4. Quién hace qué

* **Compila con el harness** (R3 actualizado por el usuario). Usa `bash tools/build/compile.sh`; informa COMPILES únicamente si Gradle terminó correctamente. La prueba dentro de Minecraft se verifica aparte: WORKS exige una prueba real o el reporte del usuario. No afirmes funcionamiento solo porque compiló.
* **Un chunk por turno** (R5): algo que el usuario compile y pruebe en pocos minutos (2-6 archivos). Di qué dejaste fuera a propósito. Ante la duda se recorta el chunk, nunca los docs ni el zip.
* **A lo sumo UNA API nueva de Minecraft por chunk** (R6), señalada como el error de compilación más probable. Copia patrones que ya compilaron. La lógica que pueda ser Java puro va a `rules/`; el pegamento con Minecraft es delgado.
* **Verificación mínima** (R9): el usuario pidió "menos checks". Autocomprobación solo si cambió lógica pura, una vez por turno, sin pruebas de sabotaje, tests chicos, nunca tras cambios de recursos, arte, docs o pegamento.
* **Plan primero** (R10) para lo grande; en decisiones menores propones un pick y el usuario lo acepta en una línea. Una función debe poder leerse **en el mundo** (R11): señal visible o audible y palabra explicada en el juego y en `FEATURES`.

Proceso completo (R1-R13 y R17 con su texto largo): `docs/REFERENCE.md` §3. Reglas técnicas: R14 y R15 en §8, R16 en §5.

---

## 5. Comandos para copiar y pegar

Descomprimir (la carpeta del zip se llama `LSMMod26.2Neoforge-master`, con punto):
```
cd /home/claude && unzip -q /mnt/user-data/uploads/LSMMod26_2Neoforge-master.zip && cd LSMMod26.2Neoforge-master
```

Probar el mod (requiere Minecraft y una pantalla): `./gradlew runClient` abre el juego directo desde el código, sin tocar la carpeta `mods`, con las opciones de `tools/run-defaults/options.txt`. Recordar este comando al pedir pruebas.

Sintaxis de todo el Java (`ParseAll`, solo parse; el JRE del sandbox es 21 y el mod usa Java 25):
```
java tools/ParseAll.java
```

Autocomprobación de `rules/` (existe desde `N0`; una corrida por turno, R9):
```
java -m jdk.compiler/com.sun.tools.javac.Main -d /tmp/rc src/main/java/net/nicomar2009/lsmmod/rules/*.java tools/RulesCheck.java && java -cp /tmp/rc RulesCheck
```

Arte: **no hay script todavía** (decisión D3 abierta). El sandbox tiene Python con Pillow.

**Lista exacta de archivos tocados (R17).** Al PRINCIPIO del turno, antes de editar nada, guarda una copia base de la carpeta ya extraída (si aún no la extrajiste, extráela primero con el comando de arriba):
```
rm -rf /tmp/base && cp -r /home/claude/LSMMod26.2Neoforge-master /tmp/base
```
Al FINAL del turno, justo antes del zip, saca la lista real (no de memoria):
```
cd /home/claude && diff -rq /tmp/base LSMMod26.2Neoforge-master -x run -x build -x .gradle
```
`Only in LSMMod26.2Neoforge-master...` = **añadido**; `Files ... differ` = **modificado**; `Only in /tmp/base...` = **borrado** (debe decirse también). Si el turno no usó el sandbox o no tocó archivos, se dice "ningún archivo modificado".

Entrega (R13): mismo nombre de zip y de carpeta que se subió, sin `run/` ni `build/`:
```
cd /home/claude && zip -qr -y /mnt/user-data/outputs/LSMMod26_2Neoforge-master.zip LSMMod26.2Neoforge-master -x '*/run/*' '*/build/*' '*/.gradle/*'
```
Luego `present_files` y la respuesta, **en este orden** (R17):
1. **Archivos añadidos / modificados / borrados en ESTE turno**, uno por línea con su ruta completa desde la raíz del proyecto, tomados del `diff` de arriba y agrupados por tipo.
2. Qué hace / qué NO está compilado / qué probar / qué sigue (corto).

---

## 6. El bucle de un chunk (R7, R8)

0. **Copia base** (`/tmp/base`, sección 5) antes de editar.
1. **Código y recursos** del chunk (2-6 archivos).
2. `java tools/ParseAll.java`.
3. Solo si cambió lógica pura: un grupo pequeño en `RulesCheck` y UNA corrida.
4. **Docs.** Toca solo esto:
   * ESTADO ACTUAL de este archivo (sección 2);
   * una línea en `docs/CHUNKS.md`;
   * el spec del área: qué hizo el chunk, diferencias con el plan, riesgos de compilación;
   * una fila nueva en `docs/API_NOTES.md` si se consultó algo de la API;
   * "decisiones sin preguntar" en `docs/DESIGN.md` y un párrafo en `docs/FEATURES.md`;
   * 2-4 líneas en `docs/history/SESSION_LOG.md`.
   Si un doc de la sección 7 queda desactualizado, corrígelo en el mismo turno (R2).
5. **Lista exacta de archivos tocados** (`diff` de la sección 5) y **zip**; la respuesta empieza por esa lista (R17).

El orden es así a propósito: quedarse sin espacio cuesta pulido, nunca un zip roto.

---

## 7. Mapa de docs: trabajo → archivo

Todos los docs del plan existen desde la sesión 4. `PLANNED` = se escribe solo cuando haga falta.

| Trabajo | Archivo | Estado |
|---|---|---|
| Qué chunks hubo y su estado | `docs/CHUNKS.md` | existe |
| Hitos, qué sigue, ideas sin spec | `docs/ROADMAP.md` | existe |
| Hechos estables, proceso largo, paquetes, convenciones, depuración | `docs/REFERENCE.md` | existe |
| Investigar una API de Minecraft/NeoForge (índice rápido, mapa de riesgo) | `docs/API_NOTES.md` | existe (tabla de búsqueda vacía); lo que compiló en bunnidogs: `docs/history/bunnidogs_patterns/API_NOTES.md` |
| Cómo se usa cada cosa en el juego | `docs/FEATURES.md` | existe |
| Diseño, glosario, pendientes P/A/V, decisiones abiertas D, decisiones sin preguntar | `docs/DESIGN.md` | existe |
| Territorio, llamados, patrullas, raids, matriz de hostilidad, efectos | `docs/MECHANICS_SPECS.md` | existe |
| NPC, stats, rasgos | `docs/NPC_SPECS.md` | existe |
| Armas, pacificar/enojar, instrumentos, consumibles, legendarios, discos | `docs/ITEM_SPECS.md` | existe |
| Armadura, prendas, inmunidades, Traje de Manuel Tirado | `docs/ARMOR_SPECS.md` | existe |
| Bloques, casilleros y loot, tabla "existe hoy / falta" | `docs/BLOCK_SPECS.md` | existe |
| Generación del colegio, zonas, aulas, mapa | `docs/WORLD_SPECS.md` | existe |
| Cadena de eventos, Director, Hidalgo, Yahu, ritual, boss | `docs/BOSS_SPECS.md` | existe |
| Cruce con el mod bunnidogs | `docs/INTEGRATION_BUNNIDOGS.md` | existe |
| Patrullas y raids propias (plan, sin código) | `docs/RAID_PLAN.md` | existe |
| Recursos (texturas, modelos...) que todavía no usa nada | `newresources/README.md` | existe (carpeta vacía) |
| Otro tema grande antes de codificarlo (p. ej. el plan de `N1`) | `docs/<TEMA>_PLAN.md` | `PLANNED` (se escribe cuando haga falta) |
| El diseño original, literal | `docs/history/DESIGN_SOURCE_v1.md` | existe (literal; nunca se edita) |
| El plan de documentación que se ejecutó | `docs/history/DOCS_PLAN.md` | existe (histórico) |
| Registro de sesiones, filas completas de chunks | `docs/history/SESSION_LOG.md`, `docs/history/CHUNKS_FULL.md` | existen (no hacen falta para continuar) |
| Historia de la API | `docs/history/API_HISTORY.md` | `PLANNED` (solo cuando `API_NOTES.md` tenga secciones viejas) |

**El spec de un sistema gana sobre cualquier otro doc.** Los comentarios del código que citan "README section 9.1" o "Phase 3/4" vienen de un README anterior que no está en el zip: no te fíes de ellos.
