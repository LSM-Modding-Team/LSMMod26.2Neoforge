# Harness Java 25 / NeoForge 26.2

Codex no compila mientras siga la instrucción en [AGENTS](../../AGENTS.md). Estos comandos quedan disponibles para el desarrollador:

```sh
bash tools/build/compile.sh               # JAR en build/libs/
bash tools/build/compile.sh jar --offline # rápido: compilar/empaquetar con caché
bash tools/build/compile.sh build --offline
bash tools/build/compile.sh runClient      # necesita pantalla
```

El script reutiliza Java 25 de JAVA_HOME, del entorno cloud o PATH. Si falta, instala el JDK oficial verificado por SHA-256 en Linux x86_64 (curl, Python 3, tar). En Windows usa JDK 25 y `gradlew.bat`.

La API de Jade usada por `compat/jade` requiere el JAR oficial `libs/Jade-mc26.2-NeoForge-26.2.10.jar` para compilar. Fusion se descarga por Gradle. Los JARs externos quedan fuera de Git; instala mods opcionales de juego en `run/mods/` cuando los necesites.

**`.build-tools/` es exclusivamente para compilar; Codex debe omitirla en tareas normales.** El harness la prepara automáticamente con `jdk25`, `gradle`, `project-cache`, `libs` y `output`: reutiliza mediante enlaces el JDK, la caché compartida de Gradle, `.gradle`, las dependencias locales y `build` ya existentes. Se conservan las ubicaciones reales para mantener las claves de las cachés; no es una carpeta portable. También genera un `AGENTS.md` local con la indicación de no explorar. Todo queda ignorado por Git y por las búsquedas normales de `rg`; no usar búsqueda recursiva que siga estos enlaces.

Respeta `JAVA_HOME`, `GRADLE_USER_HOME` y `LSM_BUILD_TOOLS_DIR` explícitos; el valor predeterminado de este último es `.build-tools`. En este entorno `gradle` enlaza `/workspace/.gradle-cloud`, incluyendo Minecraft/NeoForge ya preparados. Usa compilación incremental, sin `clean`: `bash tools/build/compile.sh jar --offline`. `build` (también el valor predeterminado sin argumentos) ejecuta además las comprobaciones completas de mobiliario; `jar` no las ejecuta. Offline exige dependencias ya descargadas; al cambiar versiones, permite primero su descarga. Conserva proxy/TLS y permisos del entorno. Los sockets locales de Gradle bloqueados requieren el mecanismo de permisos del entorno.

No versionar JDK, credenciales, cachés, JARs externos, backups, ZIPs de entregas, previews ni `build`/`run`. El instalador elimina el comprimido del JDK después de verificarlo y extraerlo; conserva el JDK y las cachés reutilizables. Un build exitoso no verifica Minecraft. Logs del cliente de desarrollo: `run/logs/latest.log`.
