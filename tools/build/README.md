# Compilación reutilizable (Java 25 / NeoForge 26.2)

Desde la raíz del proyecto, en Linux:

```sh
bash tools/build/compile.sh               # genera build/libs/lsmmod-1.0.0.jar
bash tools/build/compile.sh build --offline # reutiliza dependencias ya descargadas
bash tools/build/compile.sh runClient      # prueba en Minecraft cuando hay pantalla
```

El harness sirve desde cualquier directorio. Selecciona un JDK 25 existente mediante JAVA_HOME, el JDK del entorno cloud o PATH. Si no existe, descarga el JDK 25 oficial en Linux x86_64 y comprueba SHA-256 antes de extraerlo. Se necesita curl, Python 3 y tar para esa instalación inicial. En otras plataformas, instala Java 25 y usa el wrapper normal (gradlew.bat en Windows).

Conserva GRADLE_USER_HOME si ya está definido; reutiliza /workspace/.gradle-cloud cuando existe; en un entorno nuevo usa .build-tools/gradle. Los archivos del JDK y cachés se guardan fuera de los archivos versionados. No se guardan credenciales. Se mantienen el proxy y la confianza TLS del entorno; los proxies con credenciales dentro de la URL no se convierten automáticamente a propiedades Java.

Antes de descargar, respeta la política de red del entorno. La primera compilación descarga NeoForge/Minecraft y prepara sus clases; puede tardar varios minutos. --offline solo funciona después de completar las descargas.

Si el sandbox bloquea los sockets locales de Gradle con «Could not determine a usable wildcard IP», ejecuta el mismo comando mediante el mecanismo de permisos del entorno. El script no desactiva el sandbox ni la verificación TLS.

Este harness verifica la compilación. Un build exitoso no verifica el comportamiento dentro de Minecraft.
