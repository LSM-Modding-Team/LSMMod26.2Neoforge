# LSM Mod — trabajo en este proyecto

- Responde en español. Sigue primero la petición actual del usuario.
- **Git:** trabajar y hacer push siempre a la rama existente `master`. No crear ramas nuevas. Antes de publicar, traer los cambios de `origin/master` e integrarlos conservando el trabajo de otras conversaciones. No usar force-push.
- Lee START_HERE.md para el contexto; usa los specs únicamente para el área que estás cambiando.
- La compilación sí está disponible. Usa `bash tools/build/compile.sh` desde la raíz. Para builds sin nuevas dependencias: `bash tools/build/compile.sh build --offline`.
- El harness reutiliza Java 25 y las cachés o instala Java 25 cuando falta; detalles en tools/build/README.md. Respeta los permisos y la política de red del entorno. Ante un bloqueo de sockets locales de Gradle, usa el mecanismo de permisos del entorno para ejecutar el mismo comando.
- No confundas compilación correcta con prueba dentro de Minecraft. Informa el resultado real de cada una. Las notas históricas que decían que el sandbox no tenía Gradle están obsoletas.
- school_shield se coloca completo como un suelo de bloques completos de 3×3, con un único escudo pixelado en la cara superior y borde blanco. No requiere interacción para ampliarlo.
- No incluyas .build-tools, .gradle, build ni run en el ZIP del código fuente.
