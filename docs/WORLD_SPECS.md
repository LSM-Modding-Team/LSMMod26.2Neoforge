# Colegio, zonas y catálogo de espacios

Implementado: catálogo editable del mundo suministrado. Pendiente: generación jigsaw, mapas y aparición de NPC por zonas. Sin marca = decidido; *(propuesta)* = ajustable; *(a confirmar)* = supuesto. Pendientes: [DESIGN](DESIGN.md).

## Generación y mapas (diseño)

Colegios extremadamente raros en planicies; uno garantizado en la planicie más cercana al spawn. Frecuencia, separación y biomas exactos sin definir. Mapa tipo cartógrafo al colegio más cercano, fabricado con legendarios de bunnidogs (P1). El traje final facilita lootear otros colegios.

Piezas jigsaw delimitan zonas; NPC no salen salvo eventos temporales y luego vuelven. Debe poder consultarse si una posición está dentro de un colegio y de cuál, para Expulsión, ritual y boss. Representación exacta y progreso por colegio/global (D4) pendientes; el catálogo fijo no sustituye una instancia generada.

| Zona | Diseño |
|---|---|
| Sala de profes/oficinas | 11 profesores en sala, otros en oficinas; ropa de profesor solo en oficinas |
| Aulas | Tutor por aula; papel higiénico por salón, libros 2do–5to, casilleros 4to/5to con Ardilla |
| 3ro secundaria | Miss Alessandra como tutora |
| Aula interactiva/inglés | Mobiliario gris, PC; vicioso fijo en interactiva |
| Hall | Sillas negras/rojas |
| Auditorio | Sillas rojas/blancas; fantasma de Manuel tras Hidalgo |
| Kiosko | Mesa y mamá de cuarto |
| Patio | Salida temporal al sonar campana |

Faltan cantidad de NPC por salón y distribución generable (V19), y zonas de escudo/campana/santos/objetos perdidos/Moisés/Hidalgo/Yahu/Director/otros personajes. Student ya soporta primaria 1–6 y secundaria 1–5; usar esos once grados actuales como referencia sin inventar cantidades por salón.

Recursos 26.x: `data/lsmmod/structure/`, `worldgen/`, `tags/`. Construcciones exportadas como NBT; verifica API 26.2 antes de registrar estructuras/mapas.

## Catálogo actual y comandos

38 espacios del mundo original: 32 nombres del usuario más arte, auditorio, enfermería, hall, kiosko y patio. Cajas/nombres: [SCHOOL_SPACES](SCHOOL_SPACES.md). Fuente activa: `src/main/resources/data/lsmmod/school_spaces.json`; tabla interna `school_spaces_migration.json` migra IDs antiguos, sin aceptarlos como alias. No hay detección automática de ampliaciones ni modificación de bloques.

Comandos Overworld con permisos equivalentes a `/tp`:

| Comando `/lsmmod ...` | Acción |
|---|---|
| `define <id> <x1> <y1> <z1> <x2> <y2> <z2>` | Crea o reemplaza todas las cajas de un espacio |
| `addbox <id> <x1> <y1> <z1> <x2> <y2> <z2>` | Añade sector; rechaza duplicados |
| `delete <id>` | Elimina y guarda baja; define puede recrear deliberadamente |
| `rename <nombre>` / `renameid <id> <nombre>` | Renombra y cambia ID; requiere estar dentro |
| `rooms [página]`, `where`, `tp <id>` | Consulta/TP seguro según catálogo activo |
| `boundingbox <id> [segundos]` / `boundingbox off` | Partículas privadas, 30 s por defecto (5–120) |
| `export` | Guarda archivo y ofrece copia del catálogo activo con cajas/bajas |

Esquinas de comandos inclusivas/en cualquier orden, con relativas vanilla. JSON tiene máximos exclusivos. Límites: 128 bloques por lado, 16 cajas/espacio, volumen 65536, posiciones finitas/altura válida. Rename elige espacio menor contenedor; empate requiere renameid. Nombres hasta 80 caracteres, IDs minúsculas/sin tildes/guion bajo; sin duplicados, off reservado. TP comprueba apoyo, colisión, fluidos y peligros. Editar/eliminar cancela visualizaciones anteriores.

## Persistencia

Ediciones/bajas por mundo en `<mundo>/lsmmod/school_space_edits.json`, esquema 2, guardado inmediato con archivo temporal/reemplazo atómico. Bajas prevalecen sobre base. Si no existe esquema nuevo, migra school_space_names.json, omite retirados y rechaza conflictos; original intacto. Después no vuelve a importar para no resucitar regiones. Fallos no deben reiniciar catálogo ni confirmar éxito.

Export en school_spaces_export.json: ID/nombre/cajas/bajas, dimensión Overworld y máximos exclusivos. Archivo en servidor, copia en cliente; informes >60000 caracteres se entregan completos en archivo. Código: `world/SchoolSpaceCatalog.java`, `SchoolSpaceCommands.java`, `SchoolSpaces.java`.
