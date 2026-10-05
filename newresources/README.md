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

| Archivo | Para qué es | Qué falta para usarlo |
|---|---|---|
| *school_bell.json* | modelo de la campana | añadir el bloque |
| *school_bell.png* | textura de la campana | añadir el bloque |
