# Preparación de modelos: NeoForge y Fusion

Se usa la publicación exacta **Fusion 1.3.16-neoforge-mc26.2**, archivo oficial CurseForge **9070419**, proyecto **854949**. Gradle la obtiene mediante CurseMaven y `implementation`; no se incluye una copia del JAR en Git ni se empaqueta dentro de LSM Mod. El cliente debe instalar `fusion-1.3.16-neoforge-mc26.2.jar` en su carpeta `mods`. Los clientes de desarrollo de Gradle reciben la dependencia automáticamente. Fusion es dependencia obligatoria solo del cliente, fijada a `[1.3.16]`; el servidor dedicado no la necesita para renderizar.

Fuentes verificadas:
- Publicación: https://www.curseforge.com/minecraft/mc-mods/fusion-connected-textures/files/9070419
- Configuración oficial: https://github.com/SuperMartijn642/Fusion/wiki/Setup-in-dev-environment
- Formato base: https://github.com/SuperMartijn642/Fusion/wiki/Base-Model-Type
- Implementación 26.2: https://github.com/SuperMartijn642/Fusion/blob/neoforge-26.2/src/main/java/com/supermartijn642/fusion/model/types/base/BaseModelType.java

## Qué se prepara

`optimizeModelResources` se ejecuta antes de `processResources`; combina `src/main/resources` y `src/generated/resources` en `build/generated/optimizedResources`. Los metadatos generados por NeoForge se añaden por su tarea habitual. Los originales no se borran ni se modifican: todos los generadores existentes continúan escribiendo sus recursos normales y el optimizador procesa su resultado.

1. Comparte modelos JSON exactamente idénticos y redirige referencias en estados, padres e ítems. No se elimina ningún bloque, ítem, textura ni OBJ.
2. Compacta las propiedades `powered` y `waterlogged` de los selectores visuales solo cuando todas sus combinaciones producen exactamente el mismo modelo. Estas propiedades siguen existiendo en Java, en el mundo y en la lógica. Las conexiones y los demás campos visuales se conservan.
3. Usa `fusion:model`, tipo `base`, para los cuboides explícitamente sólidos. Cuando varios modelos tienen la misma geometría y padre, comparten un padre de Fusion; los materiales se mantienen en cada hijo. Los modelos transparentes siguen en su cargador original, pues Fusion 26.2 no interpreta `render_type` como el cargador vanilla de NeoForge. El cristal opaco y las puertas completas mantienen sus texturas de alfa 255.
4. Mantiene el cargador `neoforge:obj` para pendientes, barandas y decoraciones. No intenta pasar OBJ a un lector de cuboides.

Resultado inicial: 3660 → 3286 modelos JSON; 374 modelos idénticos compartidos; 31381 → 20501 selectores explícitos; 25 modelos opacos cargados con Fusion. El informe completo queda en `lsmmod_model_optimization.json` en los recursos preparados. Estos números representan reducción de recursos, no una medición de tiempo de arranque.

## Comprobaciones sin compilar

```
python tools/check_optimized_model_resources.py
python tools/optimize_model_resources.py --output /tmp/lsmmod-model-preview
```

La prueba compara todos los modelos originales con sus referencias optimizadas: geometría, UV, texturas, transformaciones y padres externos. Evalúa cada selector original y exige que exista exactamente un resultado equivalente en el conjunto compacto. También comprueba los ítems, dependencia exclusiva del cliente y que ambos cargadores se usen. El directorio de salida debe ser distinto de los fuentes; el optimizador solo limpia directorios que él mismo marcó.

Gradle usa `python` en Windows y `python3` en otros sistemas. Puede cambiarse con `-PpythonExecutable=/ruta/al/python`. Solo se usa la biblioteca estándar de Python para optimizar y comprobar; no requiere Pillow.

No se ha compilado, resuelto la dependencia mediante Gradle ni iniciado Minecraft por instrucción del usuario. La comprobación Java `checkFurnitureOutlines` queda pendiente. Se debe medir el arranque y capturar los hilos durante el bloqueo de cierre para comprobar su causa; esta integración no demuestra que el cierre esté arreglado.

## Estados y compatibilidad

Los identificadores y las propiedades Java no cambian. Los mundos existentes conservan los datos de cortinas y conexiones. Esta fase reduce modelos y selectores visuales, pero no las 256000 combinaciones de estados de las escaleras de cristal. Sacar datos de cortinas de BlockState exige una migración específica de los mundos y del renderizado, y constituye un trabajo separado; instalar Fusion por sí solo no reduce esos estados.
