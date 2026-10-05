# LSM Mod

Mod de **NeoForge** (`lsmmod`) para **Minecraft 26.2** (NeoForge 26.2.0.88, Java 25), ambientado en un colegio: un dungeon con NPC (alumnos y profesores) muy fuertes que se quedan en su zona, armas y armadura, raids y un boss final.

**Qué existe hoy:** 3 sillas, 2 pupitres de dos mitades, un pupitre de primaria, un casillero y el escudo del colegio. El escudo se coloca completo como un suelo de 3×3 con el escudo pixelado de `escudo.zip` (nueve piezas de 16×16). Todos aparecen en la pestaña creativa LSM Mod. La compilación del proyecto está verificada; prueba del escudo dentro de Minecraft pendiente. Los NPC, estructura, raids y jefes siguen planeados: `docs/ROADMAP.md`.

## Ejecutar

Hace falta JDK 25 (el proyecto puede descargarlo solo con el plugin foojay de `settings.gradle`).

```
bash tools/build/compile.sh # prepara Java 25 y genera build/libs/lsmmod-1.0.0.jar
./gradlew runClient    # abre Minecraft con el mod
```

En Windows: `gradlew.bat` en lugar de `./gradlew`. Más detalle (servidor, logs, depuración): `docs/REFERENCE.md` §2 y §10.

## Continuar con Claude

Este zip está hecho para continuarse solo. Sube el zip a una conversación nueva y di una de estas cosas:

* **"funcionó, sigue"**: lo último que se entregó anduvo; Claude hace el siguiente paso.
* **"compiló"**: compila, pero todavía no lo has probado en el juego.
* **Pega los errores o el log**: Claude los arregla antes de añadir nada nuevo.
* **"continúa con X"**: Claude hace X (si es grande, primero escribe un plan).

Lee `AGENTS.md` y `START_HERE.md` al empezar. El harness guardado permite compilar en futuras conversaciones: `bash tools/build/compile.sh`. Consulta `tools/build/README.md` para Java 25, cachés y proxy. La prueba dentro de Minecraft es independiente de la compilación.

## Docs de un vistazo

| Qué buscas | Dónde |
|---|---|
| Por dónde empezar (para Claude) y el estado actual | `START_HERE.md` |
| El diseño, el glosario y lo que falta decidir | `docs/DESIGN.md` |
| Reglas transversales (estados, llamados, raids, efectos, hostilidad) y los NPC | `docs/MECHANICS_SPECS.md`, `docs/NPC_SPECS.md` |
| Items, armadura, bloques | `docs/ITEM_SPECS.md`, `docs/ARMOR_SPECS.md`, `docs/BLOCK_SPECS.md` |
| El colegio como estructura, y los jefes y el ritual | `docs/WORLD_SPECS.md`, `docs/BOSS_SPECS.md` |
| Cruce con el mod bunnidogs | `docs/INTEGRATION_BUNNIDOGS.md` |
| Plan de patrullas y raids (sin código) | `docs/RAID_PLAN.md` |
| Cómo se usa cada cosa en el juego | `docs/FEATURES.md` |
| Qué se ha hecho y en qué estado está cada chunk | `docs/CHUNKS.md` |
| Qué sigue | `docs/ROADMAP.md` |
| Versiones, paquetes, proceso, convenciones, nota de licencia Mojang | `docs/REFERENCE.md` |
| Índice rápido de la API de Minecraft/NeoForge y mapa de riesgo | `docs/API_NOTES.md` |
| El plan de documentación que se ejecutó, el diseño original, el registro de sesiones | `docs/history/` |

El mapa completo trabajo → archivo, con el estado de cada doc, está en `START_HERE.md` §7.

## Licencia

El mod: All Rights Reserved. Los archivos de la plantilla (MDK de NeoForged): MIT, ver `TEMPLATE_LICENSE.txt`.
