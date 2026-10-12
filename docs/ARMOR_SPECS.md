# Uniformes y armadura

Cuatro prendas de jugador implementadas; el Teacher observa el uniforme exacto. Otras prendas e inmunidades de diseño pendientes.

Sin marca = decidido; *(propuesta)* = ajustable; *(a confirmar)* = supuesto. Datos sin definir e IDs P/V/D/A: [DESIGN](DESIGN.md).

## Las 4 ranuras

La armadura son **4 piezas**:

| Ranura | Qué se pone |
|---|---|
| Casco (espacio de arriba) | Peinado escolar |
| Pecho | Polo **o** casaca (la casaca reemplaza al polo) |
| Piernas | Pantalón, short o falda |
| Botas | Calzado: los **zapatos** van con el uniforme y las **zapatillas** con el buzo |

Cualquier combinación de piezas de alumno cuenta para el set completo *(a confirmar, A2)*.

## Tabla de prendas

### Uniforme implementado para jugadores

| ID | Nombre | Ranura | Protección | Durabilidad | Receta |
|---|---|---|---:|---:|---|
| `fake_school_haircut` | Corte escolar falso | Cabeza | 1 | 55 | 5 lanas negras en forma de casco |
| `uniform_polo` | Polo de uniforme | Pecho | 3 | 80 | Ninguna |
| `uniform_pants` | Pantalón de uniforme | Piernas | 2 | 75 | Ninguna |
| `uniform_shoes` | Zapatos de uniforme | Pies | 1 | 65 | Ninguna |

Protección total 7, dureza/resistencia al empuje 0; propiedades de cuero, sin tinte. Solo jugadores y armor stands, no mobs/dispenser; solo corte falso tiene receta. Loot de otras prendas pendiente.

Arte: pelo negro corto sin cubrir cara/orejas, polo rojo de manga corta con cuello/botones/escudo, pantalón gris sin correa y zapatos negros bajos. Equipment humanoide vanilla; atlas RGBA 256×128 y copias en textures/item para inventario, que no carga automáticamente las texturas de entidad. Ítem del peinado: casquete, sin cara/base que la tape.

Pantalón: cintura UV30–32 del torso y piernas20–32; cuello/pecho transparentes. ClientSetup.registerItemExtensions conserva para UNIFORM_PANTS la ruta equipada completa `lsmmod:textures/item/uniform_pants.png` para evitar el recurso magenta; no retirar ese override al regenerar equipment. Cambiarlo requiere reiniciar el mod, no solo F3+T. Confirmación visual en juego pendiente.

Generador: `python tools/create_uniform_assets.py`. Neutralidad del set exacto: SchoolNpcEntity. Revisa equipo/ítem con renders, ambas anchuras de brazo, ranuras/protección y permisos de equipamiento cuando se permita jugar.

### Diseño de otras prendas

**Inmunidad de diseño** = el grupo no te ataca; estas otras prendas aún no están implementadas.

| Prenda | Ranura | Inmunidad ante | Notas |
|---|---|---|---|
| Pantalón de vestir | Piernas | Alumnos | |
| Pantalón de buzo | Piernas | Alumnos | |
| Short | Piernas | Alumnos | |
| Falda | Piernas | Alumnos | |
| Polo de buzo, manga corta | Pecho | Alumnos | |
| Polo de buzo, manga larga | Pecho | Alumnos | |
| Polo piqué, manga corta | Pecho | Alumnos | |
| Polo piqué, manga larga | Pecho | Alumnos | |
| Casaca de promoción 6to | Pecho | Alumnos | Mayor durabilidad |
| Casaca de promoción 5to Sec. | Pecho | Alumnos *(a confirmar, A1)* | Mayor durabilidad *(a confirmar, A1)* |
| Zapatos (uniforme) | Botas | Alumnos | |
| Zapatillas (buzo) | Botas | Alumnos | |
| Peinado escolar | Casco | Alumnos | Dónde se consigue: sin definir (P2) |
| Pantalón de profesores | Piernas | Profesores | |
| Camisa de profesores | Pecho | Profesores | |
| Casco y botas de profesores | Casco, botas | Profesores | **No existen en el diseño** (P2) |
| Polo con stickers | Pecho | **Ninguna** | No afecta nada; los profesores siguen atacándote. |
| **Traje de Manuel Tirado** | Sin definir (V15) | **Todos** | |

**Protección y durabilidad:** el diseño solo da cualitativos: casaca 6to "mayor durabilidad"; uniforme de profesores "protección y durabilidad altísimas". Números: sin definir (P4).

**Cuáles son los "Alumnos" y los "Profesores"** para esta tabla: [NPC_SPECS](NPC_SPECS.md), columna de grupo. Los profesores del diseño incluyen (V5) tutores, Miss Alessandra, enfermera, psicólogas, Director, Hidalgo, Junior, Moisés, Pollo y Lucio. Sin grupo: personal de limpieza, Deivis, mamá de cuarto, fantasma y boss. Si una prenda de profesor da inmunidad al Director o a Moisés cuando un item "para profesores" no los afectaría: V13.

## Reglas de la armadura

* **Más piezas reducen el rango de visión** de los NPC. Con una pieza ven a la **mitad** de distancia *(propuesta)*. Con 2 y 3 piezas: sin definir (V6). Relación con la stat Percepción de los alumnos: V6.
* **El set completo (4 piezas) los vuelve neutrales:** solo te atacan si los atacas. Para el grupo del que lleves el uniforme. Qué pasa al mezclar piezas de alumno con piezas de profesor: sin definir (V16).
* **Trampa:** los profesores usan su uniforme con protección y durabilidad altísimas. Es casi imposible matar a uno sin romperle el uniforme, así que **no puedes robárselo fácilmente**.
* **Dónde se consigue:**
  * **Ropa de alumno:** casilleros, salones (pequeña chance) y objetos perdidos (**seguro**).
  * **Ropa de profesor:** solo en sus oficinas.
* Cómo se evalúan prenda, visión y neutralidad junto con los otros modificadores (Estudioso, Trackeo, Traje): [MECHANICS_SPECS](MECHANICS_SPECS.md).

## Traje de Manuel Tirado (diseño)

* Mientras lo lleves puesto, **todos los NPC son completamente pacíficos contigo, sin importar lo que hagas o hayas hecho.**
* Facilita lootear **otros colegios** (los que no son el primero); ver [WORLD_SPECS](WORLD_SPECS.md).
* Es el **drop del boss final** ([BOSS_SPECS](BOSS_SPECS.md)). No tiene otra fuente en el diseño.
* **Precedencia:** es el único modificador definido como absoluto ([MECHANICS_SPECS](MECHANICS_SPECS.md)).
* **Sin definir (V15):** si es una sola prenda o un set de 4 piezas, y en qué ranura va. El diseño lo lista como una fila de la tabla de prendas.
