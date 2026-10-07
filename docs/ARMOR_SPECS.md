# ARMOR_SPECS: prendas, inmunidades, reglas de la armadura y Traje de Manuel Tirado

**Estado: implementación parcial.** Existen las cuatro prendas de uniforme de jugador descritas abajo; protección como cuero, pendientes de compilación y prueba. Las inmunidades y demás prendas permanecen sin implementar. Escrito en la sesión 3 (Tanda C) a partir de `docs/history/DESIGN_SOURCE_v1.md` §6 y §8, con el grupo de cada NPC según `docs/NPC_SPECS.md` §2 (V5 resuelto por el usuario).

**Implementan este spec (todos `PLANNED`, ver `docs/CHUNKS.md`):** `A1` (armadura; **riesgo alto**: la API de equipment de 26.x es una familia que ningún código compilado en 26.2 cubre todavía) y `N0` (la parte que se evalúa en `rules/`, ver `docs/MECHANICS_SPECS.md` §6).

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*. `V#` / `P#` / `A#` = vacío, pendiente o supuesto en `docs/DESIGN.md` §5. **Donde el diseño no dice nada, la celda dice "sin definir".** Qué hizo cada chunk distinto del plan: ninguno todavía.

**R16:** una fila por prenda en la tabla de §2; items, texturas, `lang` y recetas la siguen.

---

## 1. Las 4 ranuras

La armadura son **4 piezas**:

| Ranura | Qué se pone |
|---|---|
| Casco (espacio de arriba) | Peinado escolar |
| Pecho | Polo **o** casaca (la casaca reemplaza al polo) |
| Piernas | Pantalón, short o falda |
| Botas | Calzado: los **zapatos** van con el uniforme y las **zapatillas** con el buzo |

Cualquier combinación de piezas de alumno cuenta para el set completo *(a confirmar, A2)*.

---

## 2. Tabla de prendas

### Uniforme implementado para jugadores

| ID | Nombre | Ranura | Protección | Durabilidad | Receta |
|---|---|---|---:|---:|---|
| `fake_school_haircut` | Corte escolar falso | Cabeza | 1 | 55 | 5 lanas negras en forma de casco |
| `uniform_polo` | Polo de uniforme | Pecho | 3 | 80 | Ninguna |
| `uniform_pants` | Pantalón de uniforme | Piernas | 2 | 75 | Ninguna |
| `uniform_shoes` | Zapatos de uniforme | Pies | 1 | 65 | Ninguna |

Protección total: 7 puntos; dureza y resistencia al empuje: 0. Durabilidad, encantabilidad y reparación de cuero, sin tinte. Uso exclusivo de jugadores mediante `allowed_entities`, comprobación `canEquip`, dispensado desactivado, evento que bloquea intercambios con entidades no jugador (incluidos soportes de armadura) y rechazo explícito en el intercambio del NPC de laboratorio. El resto de las prendas se obtiene por creativo o comandos mientras no se defina su loot; no hay recetas de fabricación ni conversión para ellas.

Cabello negro con parte superior peinada y laterales cortos que dejan cara/orejas visibles; polo rojo oscuro de manga corta con cuello, botones y escudo claro estilizado; pantalón gris sin correa; zapatos negros bajos con cordones y suela oscura. Cuatro modelos 3D de item y cuatro definiciones de equipment que usan la geometría humanoide de armadura vanilla, animada con el jugador. Texturas de equipo RGBA 256×128 y copias independientes en `textures/item/` para el atlas de inventario, compatibles con brazos anchos y estrechos del render de armadura vanilla. El cabello es una capa sobre la skin, por lo que no elimina el cabello que ya tenga esa skin.

Los modelos de item apuntan a `lsmmod:item/<prenda>`; las definiciones de equipment conservan sus texturas bajo `textures/entity/equipment/`. Minecraft 26.2 no incluye automáticamente texturas de entidad en el atlas de items. El modelo de inventario del peinado es un casquete corto, sin un cubo de cara ni una base que tape la cara.

Corrección del pantalón equipado: cintura en las filas UV 30–32 del cuerpo (zona inferior del torso), piernas completas en 20–32; cuello y pecho transparentes. El recurso de piernas se carga desde `textures/entity/equipment/humanoid_leggings/uniform_pants.png` mediante `equipment/uniform_pants.json`. Verificación en Minecraft pendiente. La captura posterior mostró magenta (recurso faltante), por lo que corregir la cintura no resolvió la carga. `ClientSetup.registerItemExtensions` registra únicamente para `UNIFORM_PANTS` una ruta completa de textura equipada: `lsmmod:textures/item/uniform_pants.png`, reutilizando el PNG RGBA que ya carga el item. Evita la derivación de ruta de la capa de piernas; mantiene geometría, ranura y protección. Esta corrección requiere cargar de nuevo el mod, no basta recargar recursos.

Recursos regenerables: `python tools/create_uniform_assets.py` (Pillow). No se implementan inmunidades, neutralidad ni estadísticas propias de NPC.

Verificación manual pendiente: equipar cada pieza y comparar protección; comprobar cabello sin tapar la cara, mangas cortas y pantalón sin correa; rechazar colocación en NPC/mobs/soportes de armadura y dispensadores; confirmar que solo el corte tiene receta; verificar daño/desgaste, reparación y encantamientos.

### Diseño de otras prendas


La columna **Ranura** sigue las categorías que da el diseño (`§6`: peinado = casco, polo/casaca = pecho, pantalón/short/falda = piernas, calzado = botas). **Inmunidad** = los NPC de ese grupo **no te atacan**.

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
| **Traje de Manuel Tirado** | Sin definir (V15) | **Todos** | Ver §5. |

**Protección y durabilidad:** el diseño solo da cualitativos: casaca 6to "mayor durabilidad"; uniforme de profesores "protección y durabilidad altísimas". Números: sin definir (P4).

**Cuáles son los "Alumnos" y los "Profesores"** para esta tabla: `NPC_SPECS.md` §2, columna de grupo. Los profesores del diseño incluyen (V5) tutores, Miss Alessandra, enfermera, psicólogas, Director, Hidalgo, Junior, Moisés, Pollo y Lucio. Sin grupo: personal de limpieza, Deivis, mamá de cuarto, fantasma y boss. Si una prenda de profesor da inmunidad al Director o a Moisés cuando un item "para profesores" no los afectaría: V13.

---

## 3. Reglas de la armadura

* **Más piezas reducen el rango de visión** de los NPC. Con una pieza ven a la **mitad** de distancia *(propuesta)*. Con 2 y 3 piezas: sin definir (V6). Relación con la stat Percepción de los alumnos: V6.
* **El set completo (4 piezas) los vuelve neutrales:** solo te atacan si los atacas. Para el grupo del que lleves el uniforme. Qué pasa al mezclar piezas de alumno con piezas de profesor: sin definir (V16).
* **Trampa:** los profesores usan su uniforme con protección y durabilidad altísimas. Es casi imposible matar a uno sin romperle el uniforme, así que **no puedes robárselo fácilmente**.
* **Dónde se consigue:**
  * **Ropa de alumno:** casilleros, salones (pequeña chance) y objetos perdidos (**seguro**).
  * **Ropa de profesor:** solo en sus oficinas.
* Cómo se evalúan prenda, visión y neutralidad junto con los otros modificadores (Estudioso, Trackeo, Traje): `MECHANICS_SPECS.md` §6.2 y §6.3.

---

## 4. Estado de implementación y riesgos

* **Existe hoy:** las cuatro prendas de jugador de §2, pendientes de compilación/prueba.
* **`A1`:** armadura con la API de equipment de 26.x. **Riesgo alto**: un nombre de vanilla sin código real compilado queda ASSUMED hasta que el usuario compile (`docs/REFERENCE.md` §8). Una API nueva por chunk (R6): el primer chunk de armadura debería ser **una sola prenda** para aislar el error.
* **Arte (D3, abierta):** las texturas de armadura (capas de cuerpo) no están definidas; hoy no hay scripts de arte.
* **Recetas:** el diseño no da recetas de ropa; se consigue como loot (§3). Sin definir si habrá alguna.

---

## 5. Traje de Manuel Tirado (diseño §8)

* Mientras lo lleves puesto, **todos los NPC son completamente pacíficos contigo, sin importar lo que hagas o hayas hecho.**
* Facilita lootear **otros colegios** (los que no son el primero); ver `WORLD_SPECS.md`.
* Es el **drop del boss final** (`BOSS_SPECS.md`). No tiene otra fuente en el diseño.
* **Precedencia:** es el único modificador definido como absoluto (`MECHANICS_SPECS.md` §6.3).
* **Sin definir (V15):** si es una sola prenda o un set de 4 piezas, y en qué ranura va. El diseño lo lista como una fila de la tabla de prendas.

---

## 6. Vacíos que tocan este doc

V6 (visión con 2-3 piezas), V13 (alcance de "profesores"), V15 (Traje: una pieza o cuatro), V16 (mezclar grupos), P2 (casco y botas de profesores; peinado), P4 (números), A1, A2. Detalle en `docs/DESIGN.md` §5.
