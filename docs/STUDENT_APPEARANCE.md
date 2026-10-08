# Apariencia de Student

Solo `lsmmod:student`; Teacher ignora estos NBT. Se conservan los modelos masculino/femenino y los 16 niveles independientes de `Width` y `Height` descritos en [NPC_SIZES.md](NPC_SIZES.md). Los atlas son RGBA de 256×256 y reutilizan los mismos UV a cualquier tamaño.

## Selección

| NBT | Valores |
|---|---|
| `skinColor` | 0–63, paleta exacta de abajo |
| `eyeColor` | 0–7, negro, marrones y verdes; sin azul |
| `haircutMale` | 0–31, únicamente con `gender:"male"` |
| `haircutFemale` | 0–31, únicamente con `gender:"female"` |
| `glassesType` | 0 sin lentes; 1–8 modelos de blanco a negro |

Sin ninguno de estos NBT se conserva el placeholder existente, incluyendo número y M/F. Los huevos y `/summon` sin apariencia siguen funcionando así: no sortean los nuevos valores. La aleatoriedad anterior de género y estadísticas de los huevos se conserva.

Especificar cualquier valor válido activa el retrato. Los componentes omitidos usan piel 0, ojos 0, corte 0 del género correspondiente y ningún lente. Los datos omitidos permanecen sin seleccionar (`-1`) y no se añaden al NBT guardado. Valores negativos desactivan ese componente; valores mayores al máximo se limitan al máximo. Solo se lee y guarda el corte del género actual; el del otro género se descarta.

```mcfunction
/summon lsmmod:student ~ ~ ~ {gender:"male",StudentData:{Level:"primary",Grade:3},skinColor:20,eyeColor:5,haircutMale:16,glassesType:3,Width:14,Height:14}
/summon lsmmod:student ~ ~ ~ {gender:"female",StudentData:{Level:"secondary",Grade:5},skinColor:12,eyeColor:7,haircutFemale:5,glassesType:0,Width:16,Height:16}
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {skinColor:-1,eyeColor:-1,haircutMale:-1,haircutFemale:-1,glassesType:-1,freeHair:false}
```

El último comando restaura el placeholder. Los retratos reemplazan toda la cabeza, incluyendo números y letra de género, y adaptan la piel de las manos. No modifican uniforme, casacas, pantalones, zapatos ni UV del busto femenino. El salón y el género siguen disponibles en el nombre de Jade.

## Paleta de piel

| Índice | Color | Índice | Color |
|---:|---|---:|---|
| 0 | `#F3D8C5` | 32 | `#BD9177` |
| 1 | `#EFD1BC` | 33 | `#BE9065` |
| 2 | `#EBCAB3` | 34 | `#C28D70` |
| 3 | `#E8C8B9` | 35 | `#C28D67` |
| 4 | `#E7C5AF` | 36 | `#BA8E74` |
| 5 | `#E4C0A8` | 37 | `#C08B65` |
| 6 | `#DFC0AC` | 38 | `#BB8C62` |
| 7 | `#DEB99F` | 39 | `#BF896C` |
| 8 | `#D8AE8B` | 40 | `#BE8963` |
| 9 | `#D3A580` | 41 | `#B78B71` |
| 10 | `#CDA477` | 42 | `#BC8761` |
| 11 | `#D3A17C` | 43 | `#B8885E` |
| 12 | `#D1A184` | 44 | `#BC8568` |
| 13 | `#D0A07A` | 45 | `#B4886E` |
| 14 | `#CAA073` | 46 | `#BA855F` |
| 15 | `#CE9D80` | 47 | `#B8835D` |
| 16 | `#C99D83` | 48 | `#B1856B` |
| 17 | `#CE9B75` | 49 | `#B5845B` |
| 18 | `#C79C70` | 50 | `#B98164` |
| 19 | `#CB997C` | 51 | `#B6815B` |
| 20 | `#CC9974` | 52 | `#AE8268` |
| 21 | `#C69A80` | 53 | `#B47F59` |
| 22 | `#CA9771` | 54 | `#B28057` |
| 23 | `#C4986C` | 55 | `#B67D60` |
| 24 | `#C3977D` | 56 | `#AB7F65` |
| 25 | `#C89578` | 57 | `#AF7C54` |
| 26 | `#C89570` | 58 | `#B3795C` |
| 27 | `#C0947A` | 59 | `#A87C62` |
| 28 | `#C6926C` | 60 | `#AC7850` |
| 29 | `#C19469` | 61 | `#B07558` |
| 30 | `#C59174` | 62 | `#754C36` |
| 31 | `#C49069` | 63 | `#533526` |

## Ojos

| Índice | Color |
|---:|---|
| 0 | `#16100c` |
| 1 | `#2b1b12` |
| 2 | `#43291b` |
| 3 | `#60422c` |
| 4 | `#7b5a37` |
| 5 | `#8a7046` |
| 6 | `#647044` |
| 7 | `#3d703a` |

Los seis primeros son negro/marrones; los dos últimos son verde oliva y verde.

## Cortes femeninos: 32 variantes

`haircutFemale` acepta 0–31, exclusivamente para alumnas. Hay 16 cortes lisos, 8 ondulados y 8 rulosos (25 %, menos de un tercio). Se mantienen los seis primeros índices de la entrega anterior y se añaden 26. Todo es 2D, dibujado con píxeles vanilla sobre el modelo existente; no se añade geometría de pelo.

| Índice | Nombre | Tipo | Versión |
|---:|---|---|---|
| 0 | `short_straight` | straight | Recogido / suelto |
| 1 | `short_wavy` | wavy | Recogido / suelto |
| 2 | `short_curly` | curly | Recogido / suelto |
| 3 | `school_ponytail` | straight | Recogido / suelto |
| 4 | `long_straight` | straight | Recogido / suelto |
| 5 | `long_curly` | curly | Recogido / suelto |
| 6 | `high_ponytail` | straight | Recogido / suelto |
| 7 | `double_ponytail` | straight | Recogido / suelto |
| 8 | `straight_bob` | straight | Recogido / suelto |
| 9 | `long_side_part` | straight | Recogido / suelto |
| 10 | `single_braid` | straight | Recogido / suelto |
| 11 | `double_braid` | straight | Recogido / suelto |
| 12 | `long_fringe` | straight | Recogido / suelto |
| 13 | `long_center_part` | straight | Recogido / suelto |
| 14 | `school_pixie` | straight | Recogido / suelto |
| 15 | `rounded_bob` | straight | Recogido / suelto |
| 16 | `long_layers` | straight | Recogido / suelto |
| 17 | `school_side_part` | straight | Recogido / suelto |
| 18 | `long_tucked` | straight | Recogido / suelto |
| 19 | `wavy_ponytail` | wavy | Recogido / suelto |
| 20 | `wavy_bob` | wavy | Recogido / suelto |
| 21 | `wavy_fringe` | wavy | Recogido / suelto |
| 22 | `long_wavy` | wavy | Recogido / suelto |
| 23 | `wavy_double_ponytail` | wavy | Recogido / suelto |
| 24 | `wavy_layers` | wavy | Recogido / suelto |
| 25 | `wavy_side_part` | wavy | Recogido / suelto |
| 26 | `curly_crop` | curly | Recogido / suelto |
| 27 | `curly_bob` | curly | Recogido / suelto |
| 28 | `curly_ponytail` | curly | Recogido / suelto |
| 29 | `long_curly_side_part` | curly | Recogido / suelto |
| 30 | `curly_double_ponytail` | curly | Recogido / suelto |
| 31 | `curly_layers` | curly | Recogido / suelto |

Sin ninguno de los cinco NBT de apariencia se conserva el placeholder. Los huevos no sortean colores, cortes ni lentes. Solo el corte del género actual se lee y guarda; el otro se ignora. `glassesType:0` representa no llevar lentes, mientras que 1–8 eligen sus monturas.

`freeHair` es ahora un booleano independiente, exclusivo de alumnas: false (valor por defecto) recoge el pelo y true lo deja suelto. Funciona con los 32 índices de `haircutFemale` sin cambiar el corte seleccionado. Cada corte tiene dos atlas 2D; los nombres de la tabla identifican el diseño del flequillo/mechones, pero la forma final recogida o suelta la controla este booleano. Si falta el corte, se usa el índice 0. El booleano se guarda y sincroniza tal como fue elegido, no se deduce del índice. Los chicos y Teacher lo ignoran.

Los huevos no sortean `freeHair`. Sin NBT de apariencia se conserva el placeholder. Solo `freeHair:true` activa una apariencia con corte 0 por defecto; `freeHair:false` por sí solo conserva el placeholder. Para restaurarlo, desactivar los cinco campos con -1 y usar `freeHair:false`.

```mcfunction
/summon lsmmod:student ~ ~ ~ {gender:"male",skinColor:20,eyeColor:4,haircutMale:16,glassesType:0}
/summon lsmmod:student ~ ~ ~ {gender:"female",skinColor:12,eyeColor:7,haircutFemale:29,glassesType:3,Width:14,Height:14}
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {haircutFemale:23}
```

El pelo largo se pinta sobre cabeza, torso y esquinas UV del busto hasta su base, como en una skin convencional, cubriendo esos píxeles del uniforme. No modifica el modelo corporal, el busto, la armadura, los tamaños ni las colisiones. Todos los salones reutilizan estas capas sobre su propio uniforme. Teacher no utiliza estos NBT.

## Cortes masculinos sin cambios

| Índice | Nombre | Tipo |
|---:|---|---|
| 0 | `school_side_part` | straight |
| 1 | `school_left_part` | straight |
| 2 | `short_crop` | straight |
| 3 | `textured_crop` | straight |
| 4 | `crew_cut` | straight |
| 5 | `taper_crop` | straight |
| 6 | `low_fade` | straight |
| 7 | `mid_fade` | straight |
| 8 | `high_fade` | straight |
| 9 | `short_quiff` | straight |
| 10 | `side_swept` | straight |
| 11 | `short_fringe` | straight |
| 12 | `rounded_fringe` | straight |
| 13 | `spiky_crop` | straight |
| 14 | `comb_over` | straight |
| 15 | `short_buzz` | straight |
| 16 | `wavy_side_part` | wavy |
| 17 | `wavy_crop` | wavy |
| 18 | `wavy_fringe` | wavy |
| 19 | `wavy_layers` | wavy |
| 20 | `wavy_center_part` | wavy |
| 21 | `wavy_swept` | wavy |
| 22 | `wavy_taper` | wavy |
| 23 | `wavy_rounded` | wavy |
| 24 | `curly_short` | curly |
| 25 | `curly_crop` | curly |
| 26 | `curly_fringe` | curly |
| 27 | `curly_taper` | curly |
| 28 | `curly_rounded` | curly |
| 29 | `curly_side_part` | curly |
| 30 | `curly_layers` | curly |
| 31 | `curly_compact` | curly |

## Lentes

`glassesType:0` no lleva lentes. Los índices 1–8 conservan sus diseños y colores:

| Índice | Color |
|---:|---|
| 1 | `#ffffff` |
| 2 | `#dddddd` |
| 3 | `#bbbbbb` |
| 4 | `#999999` |
| 5 | `#777777` |
| 6 | `#555555` |
| 7 | `#333333` |
| 8 | `#000000` |

## Texturas vanilla y recursos

La referencia de estilo fueron las skins originales de Steve y Alex contenidas en los recursos locales de Minecraft 26.2. Se estudiaron la escala del flequillo, ojos de una fila, boca de dos píxeles y distribución de colores planos. No se copian las caras ni se añaden sus ojos azules. Pelo lacio con una raya sencilla, ondas con mechas escalonadas y rulos con grupos de 2×2 píxeles dispersos. Monturas de lentes de un píxel, con interiores transparentes que conservan los ojos. La nariz y la boca usan tonos derivados de cada piel para evitar labios de un mismo color sobre todas las caras.

Los atlas siguen siendo RGBA 256×256, pero cada píxel del diseño equivale a uno de una skin 64×64: bloques de 4×4, escalados por vecino más cercano, sin suavizado, degradados ni detalles de fracciones de píxel. Las paletas y opciones siguen siendo 64 pieles, 8 ojos, 32 cortes masculinos, 32 femeninos en dos versiones y 8 lentes; total 176 capas.

Los 32 atlas recogidos se llaman `female_hair_00.png`–`31.png`, y los 32 sueltos `female_free_hair_00.png`–`31.png`. Todos los modelos, proyectos y atlas de pelo 3D se eliminan. Para aplicar la actualización sobre una entrega anterior, retirar los archivos listados en ELIMINADOS.txt. `tools/create_student_appearance_assets.py` es ahora el único generador, con Pillow, de las 176 capas. Los placeholders originales permanecen intactos.

El cliente compone únicamente retratos solicitados y conserva hasta 128 combinaciones al terminar cada tick. Al recargar recursos libera la caché; ante capas ausentes o inválidas conserva el placeholder. Las capas de packs deben ser 256×256. La cabeza femenina conserva la geometría original de girl.java y el busto conserva sus cajas y rotaciones. No se crean piezas de pelo adicionales para ningún género.

Verificación estática: 176 atlas, transparencia binaria, cuadrícula vanilla de 4×4, cortes distintos, selección coherente, placeholders y NBT/estadísticas/tamaños intactos; sin referencias a los modelos eliminados. Vista previa de las texturas, sin iniciar Minecraft. Pendiente comprobar retratos, armadura, cambios NBT, recarga y tamaños en juego. No se compiló.

## Cambiar solo `freeHair`

```mcfunction
/summon lsmmod:student ~ ~ ~ {gender:"female",skinColor:12,eyeColor:4,haircutFemale:29,freeHair:false}
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {freeHair:true}
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {freeHair:false}
```

Estos cambios no alteran `haircutFemale:29`: seleccionan sus dos texturas. Recogido pinta la nuca/coleta y un lazo; suelto pinta el pelo hasta la base del busto. Se mantienen modelos originales, cuadrícula vanilla, uniformes base y tamaños. El cliente incluye el booleano en la clave de caché para no confundir las dos versiones. Guardado/carga conserva el booleano; no hay cambios en la aleatoriedad de género/estadísticas anterior.
