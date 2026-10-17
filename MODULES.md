# Módulos del mod principal

## Regla de revisión

Solo se revisan los módulos cuando el usuario lo pida. De lo contrario, no se revisan.

Cuando el usuario solicite revisar uno o varios módulos, limitar la revisión a los solicitados. No inspeccionar ni auditar automáticamente los demás módulos.

## Estructura

Son módulos internos del mismo mod `lsmmod`, no mods independientes ni subproyectos Gradle. Esta estructura inicial reserva los paquetes y las carpetas; el código y los recursos existentes conservan sus rutas e IDs. La asignación o migración de contenido se realiza cuando el usuario la solicite.

- Java: `src/main/java/net/nicomar2009/lsmmod/modules/<module>/`.
- Recursos: `src/main/resources/<module>/`.
- Los archivos `.gitkeep` conservan las carpetas vacías de recursos en Git y no se incluyen en los recursos preparados del mod.

| Módulo | Nombre |
|---|---|
| `construction_blocks` | Construction blocks |
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
