# Student y Teacher: implementación actual

Tipos `lsmmod:student` y `lsmmod:teacher`, clase común SchoolNpcEntity. No uses test_npc ni flags isStudent/isTeacher. Personas concretas y reglas futuras: [NPC_SPECS](NPC_SPECS.md), [MECHANICS_SPECS](MECHANICS_SPECS.md).

## Datos y combate

StudentData: Level primary/secondary y Grade 1–6/1–5, sin secciones. Sin salón válido Student solo responde al atacante. Cambiar grado limpia objetivo; Teacher ignora estos campos. Teacher guarda WitnessedAttackers (UUIDs) y ObservedUniforms (UUID→último uniforme visible).

Vitality, Strength, Speed, Perception, AttackSpeed: enteros raíz 1–5, ausencia=3; multiplicadores 0.75/0.875/1/1.125/1.25. Huevos sortean cinco valores independientes; ENTITY_DATA explícito prevalece. Comandos/cargas no sortean. Aliases españoles solo para lectura; guardado inglés.

| Tipo | HP base | Daño fácil/normal/difícil | Movimiento base/persecución |
|---|---:|---|---|
| Student | 30 | 1.5 / 2 / 2.5 | 0.25 / sqrt(0.117) |
| Teacher | 50 | 3.5 / 7 / 10.5 | 0.25 / sqrt(0.13) |

Cadencia base 20 ticks; AttackSpeed produce 27/23/20/18/16 ticks. Percepción base 128 bloques (96–160). Multiplicadores parten de bases, no se acumulan al cargar. Subir Vitality no cura; salud actual limitada al nuevo máximo. Daño/cadencia independientes del equipo; daño propio scaling never para no aplicar dificultad dos veces. Se conservan armadura/escudos/resistencias.

Student testigo solo responde a una agresión visible a otro Student de su mismo nivel/grado; ambos deben ser visibles. Teacher puede presenciar agresiones de cualquier salón. Set exacto fake_school_haircut/uniform_polo/uniform_pants/uniform_shoes lo vuelve neutral salvo agresión presenciada; observa cambios de ropa solo con visión y mantiene memoria individual. No atacan creativo/espectador; notInPeaceful impide spawn y elimina entidades al pasar a pacífico. Sin aparición natural ni límites de zona todavía.

## Tamaño y arte

Width/Height: enteros independientes 1–16, ausencia=14, guardados/sincronizados; no se sortean ni son stats. Escala visual de 0.5 en 1, 0.9375 en 14 y 1 en 16, por tramos. Width modifica X/Z y Height Y, con pies en el suelo y equipo incluido. Colisión: 0.3×0.975, 0.6×1.8 y 0.64×2.0 en esos niveles. `getDefaultDimensions` conserva SCALE vanilla; cambios refrescan caja/ojos y navegación. Escala el mesh, no dupliques PNG/modelos por tamaño.

Modelo humanoide masculino/femenino en PlaceholderSchoolNpcModel; renderer/state compartidos, caminar y UV existentes. Atlas placeholder RGBA 256×256. Definitivas: `textures/entity/students/1p.png`…`6p.png`, `1s.png`…`5s.png`; genérica `textures/entity/school_npc.png`. Fallback: definitiva grado → placeholder grado → genérica definitiva → placeholder genérica → Steve. Skins cuadradas 64–4096 y múltiplo de 64 con píxeles base en cabeza/cuerpo/extremidades; validación cacheada y reiniciada en F3+T.

6p/5s conservan casacas de promoción; retratos no cambian uniforme, busto ni tamaño. [STUDENT_APPEARANCE](STUDENT_APPEARANCE.md) define capas y selección. Generadores: [ASSETS](ASSETS.md). Loot de ambas entidades vacío, equipo fijo de Teacher (regla de madera); no inventar drops.

## Uso y Jade

Huevos genéricos y once `student_<grado>p/s_spawn_egg` asignan StudentData. Spawner vanilla solo adopta el tipo, no el salón. `npc_summon_helper` abre formulario con botones/barras para NBT; copia o envía el mismo summon mediante permisos normales. No fuerza retrato (campos -1) ni otorga permisos. Teacher permite las dos memorias UUID, Student solo sus datos. No se guardan presets al cerrar.

```mcfunction
/summon lsmmod:student ~ ~ ~ {StudentData:{Level:"primary",Grade:1},Width:14,Height:14}
/summon lsmmod:teacher ~ ~ ~ {Vitality:3,Strength:3,Speed:3,Perception:3,AttackSpeed:3}
/give @s lsmmod:npc_summon_helper
```

Jade opcional: `[jade] npcDisplayMode` controla stats; `[jade.appearance] displayMode` admite NONE/TYPES/FULL, FULL por defecto. NAME_ONLY + apariencia NONE deja solo el nombre. Datos efectivos de servidor cuando Jade responde, niveles sincronizados como fallback. NBT siempre en inglés, etiquetas es/en; CustomName prevalece.

Al cambiar este sistema, revisa persistencia, ambos géneros, tamaños extremos/14, once salones, fallback/recarga de skins, memoria/visión, dificultad y juego sin Jade. Las últimas modificaciones tienen revisión estática, no prueba completa en Minecraft.
