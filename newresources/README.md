# newresources: recursos que todavía no se usan

Aquí van **únicamente** los recursos (texturas, modelos, sonidos, blockstates, etc.) que **todavía no está usando ningún bloque, item ni entidad del mod**. Es una sala de espera: el material está listo o a medias, pero nada lo carga.

**Estado: carpeta vacía** (solo este README). Nace en la sesión 5 a pedido del usuario.

Marcas de los docs (se conservan literales): sin marca = **decidido**; *(propuesta)* = valor elegido, ajustable; *(a confirmar)* = supuesto sin validar.

---

## Reglas de la carpeta

* **No es parte del mod.** Está fuera de `src/`, así que Gradle no la empaqueta ni el juego la carga. Un archivo aquí **no se ve en el juego**, y es a propósito.
* **Solo recursos sin usar.** Nada de código, docs de diseño ni archivos de trabajo (para eso están `docs/` y `tools/`). Si un recurso ya lo usa algo, **no va aquí**.
* **Nombres en inglés, minúsculas y con guion bajo**, como en `assets/lsmmod/` (`teachers_chair.png`). Los identificadores y nombres de archivo son en inglés; este README y los docs, en español.
* **Misma estructura que `src/main/resources/`**, para que al promover un archivo baste con moverlo: `assets/lsmmod/textures/block/`, `assets/lsmmod/models/block/`, `assets/lsmmod/sounds/`, etc. (formato 26.x: las carpetas van en singular; ver `docs/REFERENCE.md` §4).
* **Un recurso a medias se anota:** si falta algo para usarlo (p. ej. una textura sin modelo), se dice en la tabla de abajo.

---

## Cómo se promueve un recurso

1. Un chunk (`docs/CHUNKS.md`) decide usarlo y lo **mueve** (no lo copia) a `src/main/resources/` en la misma ruta relativa.
2. Ese chunk lo registra en el código y en `lang/en_us.json` si hace falta (R16: las tablas mandan).
3. El chunk borra su fila de la tabla de abajo y lo dice en la lista de archivos tocados (R17).

Arte generado con scripts (decisión D3, abierta): iría en `tools/art/`; lo que produzca y aún no se use, aquí.

---

## Recursos que hay ahora

Puerta de baños: `assets/lsmmod/models/block/bathroom_door_*.json`, ocho
modelos con sus 32 estados, texturas de ambas mitades e item. Madera opaca sin
ventana ni cartel, conservando perilla, grosor y paleta de la puerta de salones.
Vista: `bathroom_door_render.png`; paquete: `bathroom_door_assets.zip`.
Recursos comprobados; pendiente registro de bloque/item y prueba en Minecraft.

Puertas nuevas: `assets/lsmmod/models/block/dining_door_*.json` y
`teachers_office_door_*.json`, con ocho modelos por puerta, estados, texturas de
ambas mitades e items. Reutilizan los padres vanilla y la madera/perilla de
`classroom_door`, sin modificar sus archivos. Comedor: ventana rectangular larga
con centro transparente y reflejos cutout. Oficina: puerta opaca con papel de
borde claro, fondo turquesa y texto «SALA DE / PROFESORES». La textura superior de
oficina es 128×128 para conservar la legibilidad. Falta registrar los dos bloques
e items y promover los recursos a `src/`; todavía no aparecen en Minecraft.
Vista previa: `door_variants_render.png`. Paquete: `door_variants_assets.zip`.
Generador: `tools/create_door_variants.py`. JSON, PNG y referencias comprobados;
sin compilación ni prueba dentro del juego.

| Archivo | Para qué es | Qué falta para usarlo | Descripción del bloque |
|---|---|---|---|
| *school_bell.json* | modelo de la campana | añadir el bloque | Los NPC salen al patio (salvo algunos alumnos) durante 90 s, tiempo para lootear ~2 salones. Cooldown de 15 min (propuesta). |
| *school_bell.png* | textura de la campana | añadir el bloque | Los NPC salen al patio (salvo algunos alumnos) durante 90 s, tiempo para lootear ~2 salones. Cooldown de 15 min (propuesta). |
| *elementary_desk.json* | modelo de la carpeta | añadir el bloque | Función decorativa y de almacenamiento, ofreciendo 9 slots para almacenar objetos. **A diferencia de las otras carpetas, esta únicamente consiste de un único bloque, no de una combinación de 2** |
| *elementary_desk.png* | textura de la carpeta | añadir el bloque | Función decorativa y de almacenamiento, ofreciendo 9 slots para almacenar objetos. **A diferencia de las otras carpetas, esta únicamente consiste de un único bloque, no de una combinación de 2** |
| *sanmartindeporresdown.json* |modelo de la parte inferior del bloque de San Martín de Porres | añadir el bloque | Parte inferior del bloque. Click derecho: regeneración ~30 s. Pasivo: regenera automáticamente a profesores y alumnos cercanos, más a profesores. Legendario: tarda muchísimo en minarse. Dos se usan en el ritual (BOSS_SPECS.md). |
| *sanmartindeporresdown.png* |textura de la parte inferior del bloque de San Martín de Porres | añadir el bloque | Parte inferior del bloque. Click derecho: regeneración ~30 s. Pasivo: regenera automáticamente a profesores y alumnos cercanos, más a profesores. Legendario: tarda muchísimo en minarse. Dos se usan en el ritual (BOSS_SPECS.md). |
| *sanmartindeporresup.json* |modelo de la parte superior del bloque de San Martín de Porres | añadir el bloque | Parte superior del bloque. Click derecho: regeneración ~30 s. Pasivo: regenera automáticamente a profesores y alumnos cercanos, más a profesores. Legendario: tarda muchísimo en minarse. Dos se usan en el ritual (BOSS_SPECS.md). |
| *sanmartindeporresup.png* |textura de la parte superior del bloque de San Martín de Porres | añadir el bloque | Parte superior del bloque. Click derecho: regeneración ~30 s. Pasivo: regenera automáticamente a profesores y alumnos cercanos, más a profesores. Legendario: tarda muchísimo en minarse. Dos se usan en el ritual (BOSS_SPECS.md). |
