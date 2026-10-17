# Módulos del mod principal

## Regla de revisión

Solo se revisan los módulos cuando el usuario lo pida. De lo contrario, no se revisan.

Cuando el usuario solicite revisar uno o varios módulos, limitar la revisión a los solicitados. No inspeccionar ni auditar automáticamente los demás módulos.

## Estructura

Son módulos internos del mismo mod `lsmmod`, no mods independientes ni subproyectos Gradle. Los módulos `npcs`, `balls`, `school_supplies`, `uniforms`, `music_discs`, `musical_instruments` y `books_notebooks_and_folders` contienen su código, registros y recursos. Los demás reservan sus carpetas hasta que el usuario solicite su migración. Los registros generales conservan alias para los consumidores existentes; los contenedores compartidos están en `registry/ModRegistries.java`. Los IDs públicos `lsmmod:*`, NBT y rutas lógicas de Minecraft se conservan.

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

Los generadores de contenido migrado escriben en el módulo correspondiente. Los generadores mixtos usan `output_path`/`write_resource`; las herramientas de lectura compartidas usan `source_assets` para resolver el conjunto. Las texturas de construcción utilizadas por las pinturas permanecen como dependencias compartidas. `balls` incluye la portería de fútbol/básquet; `npcs` excluye las entidades técnicas de asiento y camilla.
