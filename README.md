# LSM Mod

Mod de NeoForge ambientado en el colegio Libertador San Martín, con mobiliario, materiales, objetos escolares y alumnos/profesores configurables. El diseño futuro incluye el colegio como dungeon, raids y jefes.

Minecraft **26.2** · NeoForge **26.2.0.88** · Java **25**. Fusion **1.3.16** es obligatorio en el cliente; Jade es opcional.

Navegador opcional para PC/laptops: instala [Rinku 3.0.4 para NeoForge 26.2](https://modrinth.com/mod/rinku/version/uQ2Zmf4D) en el cliente. Admite proyección en pantallas y audio en parlantes mediante conexiones con Ctrl + clic derecho. Rinku descarga su runtime nativo al iniciar por primera vez. Las sesiones web son locales a cada cliente. La integración compila en CI; la prueba en juego sigue pendiente. [Uso y límites](docs/BLOCK_SPECS.md#pc-navegador-opcional).

Para continuar con Codex: [START_HERE.md](START_HERE.md). Para crear modelos y texturas: [docs/ASSETS.md](docs/ASSETS.md). Estado y pendientes: [docs/ROADMAP.md](docs/ROADMAP.md).

## Descargar el mod

Abre [GitHub Actions → Build](https://github.com/LSM-Modding-Team/LSMMod26.2Neoforge/actions/workflows/build.yml),
elige una ejecución **exitosa** y descarga `lsmmod-<sha>` en **Artifacts**.
El ZIP contiene el JAR instalable. El workflow se ejecuta en pushes, pull requests
y manualmente, prepara los recursos con Python y ejecuta `build`, incluida la
comprobación de contornos del mobiliario. Fusion sigue siendo una instalación
separada obligatoria en el cliente; no se incluye en el artefacto.
Una compilación CI exitosa no demuestra funcionamiento en Minecraft.

Comandos para el desarrollador que decida compilar o jugar:

```sh
bash tools/build/compile.sh build --offline
bash tools/build/compile.sh runClient
```

El modo offline necesita dependencias ya descargadas. Preparación de Java/cachés: [tools/build/README.md](tools/build/README.md). Codex mantiene la prohibición de compilar localmente indicada en [AGENTS.md](AGENTS.md); la compilación CI para entregar artefactos está autorizada.

Licencia del mod: All Rights Reserved. Plantilla NeoForged MDK: MIT, [TEMPLATE_LICENSE.txt](TEMPLATE_LICENSE.txt). Licencia de nombres Mojang: [NeoForm/Mojang.md](https://github.com/NeoForged/NeoForm/blob/main/Mojang.md).
