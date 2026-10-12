# Bloques: contratos actuales y funciones pendientes

Inventario real: `registry/ModBlocks.java` y `ModItems.java`. Para generar modelos/texturas, [ASSETS](ASSETS.md). Evita mantener una segunda lista de todos los recursos; busca el ID en registros y `tools/`.

## Compatibilidad y comportamiento actual

| Familia | Contrato que debe conservarse |
|---|---|
| Sillas | Orientación y asiento mediante SeatEntity; geometría física separada del contorno sencillo |
| Pupitres/casillero | Pupitres students/teachers: dos mitades y 27 slots; elementary_desk: una celda y 9 slots; locker: 54 slots. No confundir con accesorios decorativos |
| Mesas de computación | `has_pc` representa una PC instalada; insertar con PC y retirar con Shift+mano vacía; sincronización de celdas y drop de PC único |
| Kiosko | Mesa actual 3×1, con lectura de pares antiguos; decorativa |
| Escudo | `school_shield`: slab de suelo 4×4, altura 8 px, 16 texturas 16×16, classroom_floor/borde dorado/emblema sin encabezado. Controlador column=1,row=1; un objeto coloca todo, retirada conjunta y drop único |
| Portón/entrada | school_gate: 6×4, dos hojas de 3 bloques; school_sliding_entrance: 6×4 con centro móvil 4×4. Colocación/apertura requieren espacio y apoyo, un drop por conjunto |
| Puertas anchas | dining_door_wide, bathroom_door_wide, dining_door_wide_light e infirmary_door: rejilla 2×3, hoja 24×40 px. Bisagras/UV por celda, cristal superior fijo. Comedor: una ventana original; baño/enfermería: hoja maciza |
| Enfermería | Sobreluz tintado y cristal posterior lateral desde y16; conexiones `main`/`side` independientes. dining_door_wide_light cambia solo el frente del lateral a light_school_wall |
| Vidrio/cortinas | Cristal 8 px; conexiones por huella real y marcos por paridad de fila. Cortinas en áreas completas 1–4 de ancho × 1–8 de alto, dos clics; Shift al aire cancela. Un ítem/reembolso por área; puertas excluidas |
| Vidrio tintado | school_tinted_glass: opaco sin conexión; school_connected_tinted_glass: misma opacidad con conexiones/cortinas. No convertirlo en vidrio transparente |
| Baldosas | dark_school_tiled_floor / light_school_tiled_floor conservan alias de bloque/ítem bathroom_floor / light_bathroom_floor. No retirar esos alias |
| Cubierta | dining_room_counter solo Y14..16 px, RGB22/31/37; nueve fases anteriores conservadas pero comparten modelo. Colisión/selección dejan hueco debajo; cara inferior visible sin cullface=down |
| Columna | Fuste Ø28 px; base Ø34 px × 8 px solo abajo. Recorte contra paredes/pintables y uniones por material; caché física sin colores. Componentes Fusion con geometría válida |
| Lavadero | school_connected_sink oculta tabiques interiores al alinear piezas; formas dependientes de vecinos y sin nuevos estados visuales |
| Camilla/estatua | Camilla actual 3×1 y estatua 2×3, conservando lectura de versiones anteriores; generador de instalaciones |
| Pantalla/proyector/crucifijo | Pantalla mural 4×3; proyector centrado en dos celdas; crucifijo mural en seis celdas. Apoyos completos y drop único |
| Baranda/armario | light_school_wall_railing sirve como soporte virtual sin cambiar colisión; school_cabinet exige pared firme en sus cuatro celdas, sin inventario/OPEN |

Paredes mixtas `light_dark_school_wall_1..5`, `light_street_school_wall_1..5`, `dark_street_school_wall_1..5` retiradas por petición del usuario. No las reintroduzcas al regenerar. También se retiró awning base/colores; quedan playground_awning, elementary_playground_awning y awning_support. school_wood es RGB150/108/62 con grano 16×16; school_wood_panel es otro material.

Si cambias dimensiones o rejilla de un conjunto, conserva estados antiguos o explica su migración/recolocación. Los datos y recursos actuales prevalecen sobre fuentes originales de `newresources`.

## Pintura y edición

`school_paintable_block` guarda seis caras; `school_paintable_custom_block` guarda ocho medios cubos y 24 cuadrados. Ambos tienen un estado y entidades sin ticker. Colores: enumeración SchoolPaint; conserva sus IDs. Guardado actual de cuatro bits con `paint_bits=4`; paletas antiguas sin marca se leen con tres bits. No guardes nuevos colores con el formato viejo.

Pintar, copiar con Ctrl, limpiar, lijar y retirar medios cubos con martillo conservan permisos y sincronización en servidor. Los huecos deben permitir apuntar a caras interiores reales; el contorno del personalizable usa medios cubos presentes. Fusion dibuja solo caras expuestas, sin hornear todas las paletas.

Pintura/lija por rectángulos: Shift+clic marca inicio, siguiente clic completa, Shift al aire cancela. Misma dimensión/cara/plano, hasta 256 bloques; admite coordenadas negativas y ambas clases. Valida carga/permisos antes de modificar; una actualización por bloque. Lija devuelve colores y respeta otras caras/huecos. Selection en CUSTOM_DATA asociada a jugador/dimensión; copiar cancela selección.

## Funciones de diseño pendientes

Que exista el modelo no significa que exista su mecánica.

| Bloque | Diseño futuro |
|---|---|
| Escudo | Al pisarlo: Bad Omen LSM, Trackeo 5 min/+25 % daño, llamado de 6–10 profesores y 10–20 alumnos por 60 s *(propuesta)* |
| San Martín de Porres | Clic: regeneración ~30 s; pasivo cura NPC, más a profesores. Radio/cantidad/minado sin definir; dos para ritual |
| PC | Estudioso 3 min; 20 % de llamado agresivo; cooldown 5 min *(propuesta)*. Vicioso usa PC y persigue a quien la rompa |
| Laptop/Moisés | Discos a 6 bloques *(propuesta)* / toda la estructura; Moisés alterna himno/marcha. Carga de discos y canciones sin definir |
| Campana | Salida al patio 90 s; cooldown 15 min *(propuesta)*. Excepciones de alumnos pendientes |
| Kiosko/objetos perdidos | Trueque de mamá sin definir; objetos perdidos deben contener ropa segura (caja actual decorativa) |
| Casilleros | Loot bajo: ropa, stickers y otros; huevo podrido como trampa. Ardilla PUCP garantizada en 4to/5to: alcance de «una» pendiente V22 |
| Ataúd | Tesoro y ritual de [BOSS_SPECS](BOSS_SPECS.md) |

Pesos/cantidades de loot, discos por contenedor y prendas de profesor en oficinas quedan pendientes. Reglas de efectos: [MECHANICS_SPECS](MECHANICS_SPECS.md); objetos: [ITEM_SPECS](ITEM_SPECS.md).
