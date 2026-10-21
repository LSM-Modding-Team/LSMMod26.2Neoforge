# LSM Mod: entrada rápida

Mod `lsmmod` del colegio Libertador San Martín. Minecraft **26.2**, NeoForge **26.2.0.88**, Java **25**. Versiones reales: `gradle.properties` y `build.gradle`. Instrucciones de trabajo: [AGENTS.md](AGENTS.md).

## Dependencia de desarrollo

LSMMod requiere PaintingMod. Clona ambos repositorios como carpetas vecinas:
`LSMMod26.2Neoforge/` y `PaintingMod/` (rama `main`). El build compuesto incluye
`../PaintingMod`; otra ubicación se configura con `-Ppaintingmod_dir=/ruta/PaintingMod`.
Gradle resuelve la dependencia desde sus fuentes, sin copiar JARs ni publicar en Maven.
PaintingMod sigue siendo independiente y puede desarrollarse desde su propio repositorio.
Para jugar fuera del entorno de desarrollo, instala ambos JARs y Fusion en `mods/`.
El workflow obtiene PaintingMod en `.composite/PaintingMod` y pasa esa ruta a Gradle.

## Estado

Entrega: [GitHub Actions → Build](https://github.com/LSM-Modding-Team/LSMMod26.2Neoforge/actions/workflows/build.yml)
compila con Java 25, prepara recursos con Python y ejecuta las comprobaciones
de `build`. Descarga el JAR desde **Artifacts → lsmmod-<sha>** de una ejecución
exitosa. Codex verifica la ejecución del commit publicado; no ejecuta Minecraft.

Hay mobiliario y materiales escolares, puertas y conjuntos de varias celdas, cristales/cortinas conectables, pintura y herramientas, instrumentos, pelotas, uniforme y las entidades `student`/`teacher` con estadísticas, apariencia y asistente de invocación. El catálogo de espacios del mundo suministrado es editable; **no genera el colegio**.

Faltan generación jigsaw y mapas, NPC concretos y sus zonas/llamados, efectos escolares completos, loot de progresión, integración con bunnidogs, patrullas/raids y jefes/ritual. [ROADMAP](docs/ROADMAP.md) separa lo implementado de lo pendiente. La compilación y `checkFurnitureOutlines` pasan en GitHub Actions. Las revisiones estáticas, los renders externos y la compilación CI no sustituyen las pruebas en juego.

## Lee según la petición

| Trabajo | Documento |
|---|---|
| Nuevos modelos, texturas, bloques decorativos o rendimiento de recursos | [ASSETS](docs/ASSETS.md) |
| Comportamiento y compatibilidad de bloques | [BLOCK_SPECS](docs/BLOCK_SPECS.md) |
| Modelos, skins y NBT de alumnos/profesores | [SCHOOL_NPCS](docs/SCHOOL_NPCS.md), [STUDENT_APPEARANCE](docs/STUDENT_APPEARANCE.md) |
| Hostilidad, territorio, efectos, patrullas y raids | [MECHANICS_SPECS](docs/MECHANICS_SPECS.md) |
| NPC concretos, objetos, prendas o jefes | Solo el correspondiente [NPC](docs/NPC_SPECS.md), [ITEM](docs/ITEM_SPECS.md), [ARMOR](docs/ARMOR_SPECS.md) o [BOSS](docs/BOSS_SPECS.md) |
| Colegio, zonas y comandos de espacios | [WORLD_SPECS](docs/WORLD_SPECS.md) |
| Integración con bunnidogs | [INTEGRATION_BUNNIDOGS](docs/INTEGRATION_BUNNIDOGS.md) |
| Decisiones de diseño pendientes | [DESIGN](docs/DESIGN.md) |
| API nueva o error de compatibilidad | [API_NOTES](docs/API_NOTES.md), luego código/fuentes locales |

Para crear un bloque o cambiar arte, sigue únicamente [ASSETS: flujo y herramientas por familia](docs/ASSETS.md#flujo-rápido); no hace falta leer los demás specs salvo que cambies su comportamiento.

Raíz Java: `src/main/java/net/nicomar2009/lsmmod/`. Recursos activos: `src/main/resources/`. Generadores y fixtures: `tools/`. Previews temporales, fuera de Git: `previews/`. Dependencias de herramientas: `python3 -m pip install -r tools/requirements.txt`. No hace falta leer todos los specs para editar una textura.
