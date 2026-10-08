# Apariencia en Jade y asistente de invocación

Cambios sobre `4d8eeff`. Ese commit contiene las puertas nuevas en `newresources` y ya incluye las entidades, sus NBT y los retratos 2D anteriores. No se modifican los modelos, texturas de NPC, estadísticas ni comportamiento de combate.

## Configuración de apariencia

En `config/lsmmod-client.toml`:

```toml
[jade]
npcDisplayMode = "FULL_STATS"

[jade.appearance]
displayMode = "FULL"
```

`jade.appearance.displayMode` admite:

| Opción | Resultado |
|---|---|
| `NONE` | No añade información de apariencia |
| `TYPES` | Solo muestra los nombres de los campos, sin sus valores |
| `FULL` | Muestra nombres, claves NBT y todos sus valores; predeterminado |

La configuración de estadísticas se conserva y es independiente: por ejemplo, `NAME_ONLY` con apariencia `FULL` muestra el nombre más la apariencia, sin estadísticas. Para ver solo el nombre, seleccionar `NAME_ONLY` y apariencia `NONE`.

Campos comunes: `Width`, `Height`. Solo Student: `StudentData.Level`, `StudentData.Grade`, `gender`, `skinColor`, `eyeColor`, el corte correspondiente al género y `glassesType`. Solo alumnas: `freeHair`. No se muestra el corte del otro género ni se atribuyen NBT de alumno a Teacher.

En FULL, piel y ojos incluyen el índice y color hexadecimal de la paleta integrada; el corte incluye índice, nombre de variante y tipo; los lentes incluyen índice y color. `glassesType:0` indica sin lentes. `-1` indica valor sin seleccionar/placeholder; no se presenta como un índice válido. Género y freeHair muestran valor original y descripción localizada. Los colores/descripciones corresponden a los recursos incluidos, no se analizan imágenes de packs externos.

El servidor de Jade envía los datos; cuando no hay respuesta de Jade se usan los valores ya sincronizados por el mod. La recarga/configuración de Jade sigue el mecanismo existente. Jade continúa siendo opcional.

## Ítem y pantalla

Ítem: `lsmmod:npc_summon_helper`, disponible en la pestaña creativa del mod. Usa el icono vanilla de libro, sin textura adicional ni receta.

```mcfunction
/give @s lsmmod:npc_summon_helper
```

Clic derecho abre la pantalla incluso apuntando a un bloque o entidad, consumiendo esa interacción para no activar también el objetivo. El cliente abre la interfaz; los eventos comunes consumen la interacción sin cargar clases de pantalla en servidores dedicados.

Seleccionar Student o Teacher con el botón superior. La casilla de la derecha indica las coordenadas del comando; acepta coordenadas absolutas, relativas (`~`) o locales (`^`), sin mezclar locales con las otras. La pantalla muestra páginas de campos, mantiene los valores al cambiar de página/tipo y ofrece una vista previa del comando que se actualiza al editar. Las estadísticas empiezan seleccionadas en 3, los tamaños en 14, el género en male y el salón sin asignar. Las barras de apariencia empiezan en -1 (sin seleccionar); no se activa una apariencia por abrir la herramienta. Las memorias avanzadas vacías se omiten.

- Ambos: `Vitality`, `Strength`, `Speed`, `Perception`, `AttackSpeed` (1–5), `Width` y `Height` (1–16).
- Student: `StudentData.Level` y `StudentData.Grade`, `gender`, `skinColor`, `eyeColor`, `haircutMale`, `haircutFemale`, `freeHair`, `glassesType`.
- Teacher: `WitnessedAttackers` y `ObservedUniforms`, memorias avanzadas del profesor.

Solo se emite el corte del género seleccionado; freeHair solo se emite para female. Si gender está vacío, el summon normal usa male. `StudentData` se crea como compuesto anidado: primary acepta 1–6, secondary 1–5 y unassigned se guarda con Grade 0. Para primary/secondary hay que indicar el grado. Los NBT de alumno se omiten al seleccionar Teacher y viceversa, aunque los valores se conservan en el formulario al cambiar de tipo.

Los índices de apariencia aceptan -1 (sin seleccionar), con límites 63/7/31/31/8. El asistente no fuerza una apariencia: campos vacíos conservan el placeholder. No incluye NBT vanilla como equipo, Health o CustomName, ni aliases antiguos ni flags obsoletos isStudent/isTeacher. El tipo de entidad sustituye a estos flags.

Ejemplos para los campos avanzados Teacher:

```snbt
["12345678-1234-1234-1234-123456789abc"]
{"12345678-1234-1234-1234-123456789abc":true}
```

El primer valor corresponde a WitnessedAttackers y el segundo a ObservedUniforms. Se comprueban sus tipos y UUID; los datos se normalizan al formato que lee la entidad. No se permite introducir comandos o NBT arbitrarios.

`Copiar comando` lo copia al portapapeles. `Invocar` envía el mismo `/summon` al servidor a través del canal normal de comandos. Requiere permisos habituales de comandos: la interfaz no concede permisos ni crea NPC por un canal privilegiado. El resultado real aparece en el chat del servidor. En pacífico siguen vigentes las restricciones existentes de aparición; el asistente no las cambia.

La pantalla funciona sin Jade. Traducciones en inglés y español; claves NBT en inglés. Los valores se conservan durante la sesión de la pantalla, pero no se guardan como presets en el ítem al cerrarla.

## Verificación

Comprobación estática de recursos JSON, referencias/traducciones, cobertura de todos los NBT canónicos del mod y APIs en las fuentes disponibles de Minecraft 26.2/NeoForge 26.2.0.88 (incluida la nueva interfaz GuiGraphicsExtractor). Registro del ítem con API comprobada. No se ejecutó compilación ni juego.

Pendiente en Minecraft: nueve combinaciones de modos stats/apariencia, alumnos de ambos géneros y Teacher, Jade presente/ausente, cambio de páginas/tipo y tamaño de ventana, entradas inválidas, copiar/pegar, invocar con y sin permisos y abrir la pantalla mirando aire/bloque/entidad. La entrega no afirma validación dentro del juego.

## Controles de selección

- Vitality, Strength, Speed, Perception y AttackSpeed: cinco botones (1–5) por fila. La opción seleccionada aparece entre corchetes y no se puede volver a pulsar; seleccionar otra sustituye el valor de esa fila.
- gender: dos botones, male/female. La forma del pelo y el corte del otro género se desactivan visualmente y no se emiten en el comando.
- StudentData.Level: tres botones, sin salón, primaria y secundaria.
- StudentData.Grade: botones 1–6 para primaria y 1–5 para secundaria. Sin salón, muestra 0 desactivado. Al cambiar el nivel, el grado se ajusta al rango válido; un grado vacío/0 pasa a 1 si se elige un nivel asignado.
- skinColor, eyeColor, haircutMale, haircutFemale, glassesType: barras discretas con sus rangos NBT completos, incluido -1 para sin seleccionar. glassesType 0 indica sin lentes.
- Width y Height: barras 1–16, por defecto 14.
- freeHair: dos botones, recogido/false y suelto/true, disponibles solo para female.

Al pasar el cursor sobre una barra, el tooltip indica el valor que se elegiría en esa posición, con hexadecimal o descripción del corte cuando corresponde. El número de la barra indica el valor actualmente seleccionado. Los cambios actualizan inmediatamente el comando. Se conservan los valores al navegar entre páginas y al cambiar tipo/género; los incompatibles quedan desactivados y el generador los omite, pudiendo recuperarse al volver a la configuración compatible.

Solo coordenadas y las dos memorias UUID avanzadas de Teacher siguen siendo entradas de texto; la vista previa del comando continúa siendo de solo lectura. La pantalla no pausa el mundo. Sin compilación; pendiente probar botones, barras, hover, teclado, grado y cambios de género en Minecraft.
