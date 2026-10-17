# Módulos del mod principal

## Regla de revisión

Solo se revisan los módulos cuando el usuario lo pida. De lo contrario, no se revisan.

Cuando el usuario solicite revisar uno o varios módulos, limitar la revisión a los solicitados. No inspeccionar ni auditar automáticamente los demás módulos.

## Estructura

Son 22 módulos internos del mismo mod `lsmmod`, no mods independientes ni subproyectos Gradle. Todo el contenido registrado tiene su código, registros y recursos en el módulo correspondiente. La raíz conserva el arranque del mod, fachadas de compatibilidad, configuración, catálogo del mundo y utilidades compartidas. Los registros generales conservan alias para los consumidores existentes; los contenedores compartidos están en `registry/ModRegistries.java`. Los IDs públicos `lsmmod:*`, NBT y rutas lógicas de Minecraft se conservan.

- Java: `src/main/java/net/nicomar2009/lsmmod/modules/<module>/`.
- Recursos: `src/main/resources/<module>/`.
- Los archivos `.gitkeep` conservan las carpetas vacías de recursos en Git y no se incluyen en los recursos preparados del mod.

| Módulo | Nombre |
|---|---|
| `building_blocks` | Building blocks |
| `slopes` | Slopes |
| `painting_and_editable_blocks` | Painting and editable blocks |
| `windows_and_enclosures` | Windows and enclosures |
| `tables_and_desks` | Tables and desks |
| `chairs` | Chairs |
| `facilities` | Facilities |
| `technology` | Technology |
| `special_blocks` | Special blocks |
| `awnings` | Awnings |
| `wall_blocks` | Wall blocks |
| `lighting` | Lighting |
| `lockers_and_storage` | Lockers and storage |
| `shelving` | Shelving |
| `musical_instruments` | Musical instruments |
| `music_discs` | Music discs |
| `books_notebooks_and_folders` | Books, notebooks and folders |
| `uniforms` | Uniforms |
| `backpacks` | Backpacks |
| `school_supplies` | School supplies |
| `balls` | Balls |
| `npcs` | NPCs |

## Destino y preparación de recursos

`building_blocks` incluye materiales de construcción y paredes, aunque su ID contenga `wall`. `wall_blocks` corresponde a decoración colocada en paredes, como `classroom_timetable` y `school_name_sign`; el nombre del ID no decide su destino.

Cada módulo migrado conserva las raíces `assets/<namespace>/` y `data/<namespace>/` dentro de su carpeta de recursos. `tools/resource_modules.py` compone estas raíces con los recursos comunes antes de optimizar y empaquetar. Las traducciones, sonidos y etiquetas se dividen por propietario y se combinan sin sobrescribir definiciones; los demás recursos duplicados provocan error. El JAR usa las rutas estándar `assets/` y `data/`, sin prefijo de módulo. Los modelos continúan apuntando a sus identificadores lógicos `lsmmod:*`; no se les añade el nombre de la carpeta física.

Los generadores de contenido migrado escriben en el módulo correspondiente. Los generadores mixtos usan `output_path`/`write_resource`; las herramientas de lectura compartidas usan `source_assets` para resolver el conjunto. Las pinturas reutilizan las texturas de `building_blocks` mediante sus identificadores lógicos. `balls` incluye la portería de fútbol/básquet; `npcs` excluye las entidades técnicas: los asientos están en `chairs` y la camilla en `facilities`.

La asignación de bloques y clases usada por las herramientas está en `tools/module_layout.json`. No añade IDs: permite resolver archivos existentes y dirigir nuevos modelos/variantes al propietario correcto. Las funciones `module_read_text`, `module_glob`, `module_path`, `module_write_text` y `output_path` resuelven rutas de origen entre módulos; los generadores mixtos redistribuyen diccionarios y actualizan las declaraciones de registro del módulo, manteniendo alias en las fachadas. Las rutas temporales de preparación/render se dejan intactas.

`backpacks` contiene la mochila de emergencia colocable; no se registran mochilas nuevas durante esta migración. Las ventanas pintables y sus entidades de pintura pertenecen a `painting_and_editable_blocks` y reutilizan la lógica conectable de `windows_and_enclosures`. Las pendientes y sus barandas están en `slopes`; las barandas, pasamanos y barras rectas de construcción están en `building_blocks`. `school_cabinet` pertenece a `technology` por su control de audio; los armarios de almacenamiento están en `lockers_and_storage`. El escudo de suelo 4×4 pertenece a `special_blocks`.
