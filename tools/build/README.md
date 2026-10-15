# Harness Java 25 / NeoForge 26.2

Codex no compila localmente mientras siga la instrucción en [AGENTS](../../AGENTS.md).
El usuario sí autoriza el workflow `.github/workflows/build.yml` para compilar y
comprobar en GitHub Actions; los JARs se descargan desde **Artifacts** de una
ejecución exitosa. El workflow usa JDK 25, Python y Gradle sin el harness local,
y conserva `build`/`checkFurnitureOutlines`. No inicia Minecraft. Estos comandos
quedan disponibles para el desarrollador:

```sh
bash tools/build/compile.sh               # JAR en build/libs/
bash tools/build/compile.sh jar --offline # rápido: compilar/empaquetar con caché
bash tools/build/compile.sh build --offline
bash tools/build/compile.sh runClient      # necesita pantalla
```

El script reutiliza Java 25 de JAVA_HOME, del entorno cloud o PATH. Si falta, instala el JDK oficial verificado por SHA-256 en Linux x86_64 (curl, Python 3, tar). En Windows usa JDK 25 y `gradlew.bat`.

Gradle descarga automáticamente la API oficial de Jade 26.2.10 desde Modrinth para compilar `compat/jade`, con la versión fijada en `jade_version_id`. Es una dependencia `compileOnly`: Jade sigue siendo opcional en juego y su JAR no se incorpora al de LSM. Fusion también se descarga por Gradle. La primera compilación requiere conexión; después puede usarse `--offline` con las dependencias en caché. Instala mods opcionales de juego en `run/mods/` cuando los necesites.

**`.build-tools/` es exclusivamente para compilar; Codex debe omitirla en tareas normales.** El harness la prepara automáticamente con `jdk25`, `gradle`, `project-cache`, `libs` y `output`: reutiliza mediante enlaces el JDK, la caché compartida de Gradle, `.gradle`, las dependencias locales y `build` ya existentes. Se conservan las ubicaciones reales para mantener las claves de las cachés; no es una carpeta portable. También genera un `AGENTS.md` local con la indicación de no explorar. Todo queda ignorado por Git y por las búsquedas normales de `rg`; no usar búsqueda recursiva que siga estos enlaces.

Respeta `JAVA_HOME`, `GRADLE_USER_HOME` y `LSM_BUILD_TOOLS_DIR` explícitos; el valor predeterminado de este último es `.build-tools`. En este entorno `gradle` enlaza `/workspace/.gradle-cloud`, incluyendo Minecraft/NeoForge ya preparados. Usa compilación incremental, sin `clean`: `bash tools/build/compile.sh jar --offline`. `build` (también el valor predeterminado sin argumentos) ejecuta además las comprobaciones completas de mobiliario; `jar` no las ejecuta. Offline exige dependencias ya descargadas; al cambiar versiones, permite primero su descarga. Conserva proxy/TLS y permisos del entorno. Los sockets locales de Gradle bloqueados requieren el mecanismo de permisos del entorno.

No versionar JDK, credenciales, cachés, JARs externos, backups, ZIPs de entregas, previews ni `build`/`run`. El instalador elimina el comprimido del JDK después de verificarlo y extraerlo; conserva el JDK y las cachés reutilizables. Un build exitoso no verifica Minecraft. Logs del cliente de desarrollo: `run/logs/latest.log`.
