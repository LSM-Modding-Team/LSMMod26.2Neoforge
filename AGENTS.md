# LSM Mod — trabajo en este proyecto

- Responde en español. Sigue primero la petición actual del usuario.
- **Instrucción vigente del usuario (2026-10-10):** hacer commits y publicar los cambios en GitHub en la misma respuesta, sin pedir autorización. Se mantiene la prohibición de compilar; las verificaciones deben ser estáticas.
- **Git:** trabajar y hacer push siempre a la rama existente `master`. No crear ramas nuevas. Antes de publicar, traer los cambios de `origin/master` e integrarlos conservando el trabajo de otras conversaciones. No usar force-push.
- Lee START_HERE.md para el contexto; usa los specs únicamente para el área que estás cambiando.
- La compilación sí está disponible. Usa `bash tools/build/compile.sh` desde la raíz. Para builds sin nuevas dependencias: `bash tools/build/compile.sh build --offline`.
- El harness reutiliza Java 25 y las cachés o instala Java 25 cuando falta; detalles en tools/build/README.md. Respeta los permisos y la política de red del entorno. Ante un bloqueo de sockets locales de Gradle, usa el mecanismo de permisos del entorno para ejecutar el mismo comando.
- No confundas compilación correcta con prueba dentro de Minecraft. Informa el resultado real de cada una. Las notas históricas que decían que el sandbox no tenía Gradle están obsoletas.
- school_shield se coloca completo como un suelo de bloques completos de 3×3, con el escudo de escudo.zip, nueve texturas de 16×16 con fondo de classroom_floor (baldosas 2×2) y lsm1 en el centro. Los modelos conservan las caras originales. No requiere interacción para ampliarlo.
- No incluyas .build-tools, .gradle, build ni run en el ZIP del código fuente.

- **Rendimiento de modelos:** al crear o modificar bloques con modelos detallados, separa la selección y el contorno (`getShape`) de la colisión física (`getCollisionShape`). Usa una caja exterior simple para seleccionar y resaltar el bloque; no dibujes cada listón, pata o escalón de la colisión. Reutiliza `SimpleBlockOutline.forState` cuando la geometría dependa solo del estado; si depende del mundo o del contexto, no uses esa caché por estado. Conserva la geometría física y comprueba las orientaciones y celdas de bloques múltiples. Mantén los generadores compatibles con esta separación y ejecuta `checkFurnitureOutlines` (incluido en `build`) cuando cambies estas formas.

