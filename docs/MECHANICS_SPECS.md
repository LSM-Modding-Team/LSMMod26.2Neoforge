# Hostilidad, territorio, efectos y raids

Diseño pendiente salvo lógica pura `rules/` y combate/observación actuales en [SCHOOL_NPCS](SCHOOL_NPCS.md). Sin marca = decidido; *(propuesta)* = ajustable; *(a confirmar)* = supuesto. Pendientes en [DESIGN](DESIGN.md).

## Estados y territorio

Neutral: responde a agresión según tipo; enojado: hostil activo; apaciguado: hostilidad suspendida temporalmente. Que vuelva al terminar la duración es supuesto a confirmar. Prenda con inmunidad y set completo neutral son conceptos distintos. Cada estado necesita señal visible/audible en juego, todavía sin diseñar.

Profesores (11 en sala de profes y otros en oficinas) y tutores permanecen en su zona; escudo, mamá, raids y llamados permiten salir temporalmente. Llamar implica acudir rápido y volver después; Hidalgo es el único que se teletransporta.

| Quien llama | A quién | Cuándo llama |
|---|---|---|
| Alumnos de **primaria** | A su profesor (V2) | Al ser enojados |
| Alumnos de **secundaria** | No llaman | — |
| Alumnos de **6to** | No llaman | — |
| **Rosa Sanmartiniana** | A cualquiera del colegio | Sin definir (V3) |
| **Pollo** | A la **estudiantina** (algunos alumnos de 3ro, 4to y 5to), que cambian de apariencia y sacan sus instrumentos | Sin definir (V3) |
| **Enfermera** y **Miss Alessandra** (tutora de 3ro de secundaria) | Se llaman mutuamente | Sin definir (V3) |
| **Hidalgo** | Es el **único que se teletransporta** cuando lo llaman | — |
| **Escudo** (no es un NPC) | Llama al azar a 6-10 profesores y 10-20 alumnos durante 60 s *(propuesta)* | Al pisarlo |
| **Instrumentos usados por NPC** (estudiantina y otros) | Llaman a 2-3 más *(propuesta)* | Al usarlos |

## Patrullas y raids

Patrulla: profesor capitán + alumnos; matar al capitán da Bad Omen LSM. Tamaño, aparición y frecuencia sin definir. Raid: olas de alumnos/profesores disparadas por Bad Omen LSM donde hay muchos nidos de bunnidogs, defensa natural contra raiders.

| Ola | Composición |
|---|---|
| 1 | 6-8 alumnos de primaria |
| 2 | ~8 alumnos de secundaria |
| 3 | 3-4 profesores más el capitán con el estandarte del escudo |

Las cantidades de olas son propuestas. Gestor propio persistente por mundo y tabla/umbrales en `rules/` son propuestas sin aceptar. Cantidad mínima/radio de nidos sin valores (D6); consulta de POI de solo lectura, nunca `PoiManager.take` que reclama tickets. Propuestas: centro en jugador con omen, una raid por jugador y omen sigue corriendo sin nidos; mamá usa misma regla con mayor nivel. Mostrar progreso/avisos. Victoria/derrota/recompensa/nivel del omen (V25), estandarte (V26) y multijugador pendientes.

## Efectos de diseño

| Efecto | Origen | Descripción y valores |
|---|---|---|
| **Bad Omen LSM** | Pisar el escudo; matar al capitán de una patrulla; atacar o robar a la mamá de cuarto | Variante propia del Bad Omen de vanilla. Dura 2 h, niveles I-V como en vanilla *(propuesta)*. **Genera raids.** La mamá da un nivel "más alto" (valor sin definir). |
| **Trackeo** | Pisar el escudo; mamá de cuarto | Los NPC te localizan automáticamente (como si tuvieras glowing), se mueven más rápido y hacen más daño. Escudo: 5 min con +25 % de daño *(propuesta)*. Mamá de cuarto: 10 min *(propuesta)*. Velocidad extra: sin número (V7). |
| **Estudioso** | Folder (30 s), cuaderno (20 s), PC (3 min) | Los profesores se vuelven **neutrales** contigo, al mismo nivel que con el uniforme de profesor. El del cuaderno vale solo para profesores de primaria (V12, resuelto; qué profesor es de primaria: V13). |
| **Regeneración (San Martín)** | Click derecho al bloque San Martín de Porres | Regeneración durante ~30 s. |
| **Expulsión** | Ataque de Hidalgo | Si sigues dentro de la estructura del colegio **10 s** después, **cada 8 s** invoca 3 alumnos de primaria (mini zombies) o 2 vexes de Manuel Tirado, **alternando** ambos *(propuesta de números)*. La Ardilla PUCP da inmunidad. Detalle de Hidalgo: [BOSS_SPECS](BOSS_SPECS.md). Los vexes no tienen fila propia (V8). |

## Disparadores de hostilidad

| Disparador | Quién reacciona | Resultado |
|---|---|---|
| **Atacar al NPC** | Ese NPC | Pasa a enojado. |
| **Verte atacar a otros** | **Personal de limpieza:** te atacan si ves atacar a cualquiera del colegio. **Deivis:** "muy importante que no te vea atacar a nadie". Resto: "según el tipo de NPC", sin definir (V4). | Enojados. |
| **Enojar a un alumno de primaria** | El alumno | Ataca como mini zombie y **llama a su profesor** (V2). |
| **Pisar el escudo** (slab de suelo 4×4) | Todos los mobs | Bad Omen LSM + Trackeo (5 min, +25 % daño *(propuesta)*) + llama a 6-10 profesores y 10-20 alumnos durante 60 s *(propuesta)*. Los profesores salen de su aula de forma temporal. |
| **Atacar o robar a la mamá de cuarto** | **Todo el colegio, excepto 5to** (V9) | Como pisar el escudo pero **más fuerte**: Bad Omen más alto, más llamados, Trackeo de 10 min *(propuesta)*. |
| **Matar al capitán de una patrulla** | — | Bad Omen LSM. |
| **Romper una PC** | El **alumno vicioso** | Te persigue hasta matarte, **de un golpe**. |
| **Usar una PC** (click derecho) | — | Estudioso 3 min; 20 % de que llame a un profesor agresivo; cooldown de 5 min por PC *(propuesta)*. Atrae al vicioso, que se sienta a usarla. |
| **Campana de recreo** | Los NPC | Salen al patio (salvo algunos alumnos) durante 90 s; cooldown de 15 min *(propuesta)*. |
| **Ataque de Hidalgo** | Tú | Expulsión. |
| **Items de pacificar/enojar** | Profesores y alumnos | Ver 6.1.1. |

Objetos que enojan/apaciguan, duración y alcance: [ITEM_SPECS](ITEM_SPECS.md). Ropa y grupos: [ARMOR_SPECS](ARMOR_SPECS.md), [NPC_SPECS](NPC_SPECS.md).

## Modificadores y precedencia

| Modificador | Efecto |
|---|---|
| **Traje de Manuel Tirado** | **Todos** los NPC completamente pacíficos contigo, **sin importar lo que hagas o hayas hecho**. |
| **Prenda de alumno** (pantalón de vestir/buzo, short, falda, polo de buzo o piqué, casaca de promoción 6to, zapatos, zapatillas, peinado escolar) | Los **alumnos** no te atacan (inmunidad). La casaca de 5to Sec. es igual *(a confirmar, A1)*. |
| **Prenda de profesor** (pantalón, camisa) | Los **profesores** no te atacan (inmunidad). |
| **Polo con stickers** | Ninguna inmunidad; los profesores siguen atacándote. |
| **Menos visión por prenda** | Más piezas reducen el rango de visión de los NPC. Con una pieza ven a la mitad *(propuesta)*; con 2 y 3: sin definir (V6). |
| **Set completo** (4 piezas) | Los NPC pasan a **neutrales**: solo te atacan si los atacas. Cualquier combinación de piezas de alumno cuenta *(a confirmar, A2)*. |
| **Estudioso** | Los profesores te ven **neutral**. |
| **Ardilla PUCP** | Inmunidad ante los salones de 4to y 5to y ante Hidalgo; esos salones pasan a ser tus **aliados**. |
| **Trackeo** | Te localizan, más velocidad y daño. Cómo interactúa con la visión reducida de las prendas: sin definir (V6). |

Solo la prioridad absoluta del Traje está decidida. La combinación del resto sigue pendiente. `rules/Hostility` implementa una propuesta de orden: Traje → apaciguado → sticker ante profesores → provocado → prenda del grupo/Estudioso → estado. No convierte esa propuesta en decisión ni representa todas las mecánicas actuales. `visionMultiplier`: 0 piezas=1, 1=0.5 *(propuesta)*, 2+ sin valor. El set completo es redundante para ese veredicto puro; Ardilla, Trackeo, zonas, testigos y mezcla de uniformes requieren integración.

Teacher actual observa el set exacto y guarda agresiones presenciadas; Student testigo solo responde en su salón. Esas reglas viven en SCHOOL_NPCS y no equivalen a la matriz futura completa.
