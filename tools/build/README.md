# Harness Java 25 / NeoForge 26.2

Codex no compila mientras siga la instrucción en [AGENTS](../../AGENTS.md). Estos comandos quedan disponibles para el desarrollador:

```sh
bash tools/build/compile.sh               # JAR en build/libs/
bash tools/build/compile.sh build --offline
bash tools/build/compile.sh runClient      # necesita pantalla
```

El script reutiliza Java 25 de JAVA_HOME, del entorno cloud o PATH. Si falta, instala el JDK oficial verificado por SHA-256 en Linux x86_64 (curl, Python 3, tar). En Windows usa JDK 25 y `gradlew.bat`.

La API de Jade usada por `compat/jade` requiere el JAR oficial `libs/Jade-mc26.2-NeoForge-26.2.10.jar` para compilar. Fusion se descarga por Gradle. Los JARs externos quedan fuera de Git; instala mods opcionales de juego en `run/mods/` cuando los necesites.

Respeta GRADLE_USER_HOME; reutiliza `/workspace/.gradle-cloud` cuando existe y, si no, `.build-tools/gradle`. Conserva proxy/TLS y permisos del entorno. Offline exige dependencias ya descargadas. Los sockets locales de Gradle bloqueados requieren el mecanismo de permisos del entorno.

No versionar JDK, credenciales, cachés, JARs externos, backups, ZIPs de entregas, previews ni `build`/`run`. El instalador elimina el comprimido del JDK después de verificarlo y extraerlo; conserva el JDK y las cachés reutilizables. Un build exitoso no verifica Minecraft. Logs del cliente de desarrollo: `run/logs/latest.log`.
