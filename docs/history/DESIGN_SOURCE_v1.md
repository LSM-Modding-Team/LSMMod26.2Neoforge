# Mod "Colegio Libertador San Martín" — Documento de diseño

**Versión:** 1.0
**Convención:** los valores marcados como *(propuesta)* son valores de balance elegidos para completar el diseño y se pueden ajustar. Lo que dice *(a confirmar)* es un supuesto que necesita validación. Todo lo demás es una decisión de diseño ya tomada.

---

## 1. Concepto general

Mod de Minecraft tipo dungeon ambientado en el colegio Libertador San Martín. La estructura del colegio funciona como un **híbrido entre una mansión de pillagers y un bastión de piglins**: patrullas, raids y capitanes por un lado, y zonas de loot cerradas con mucha hostilidad por otro.

Principios de diseño:

- Todos los NPC son **muy OP por defecto**, para que sea necesario usar las mecánicas (apaciguar, enojar, evadir, uniformes) en lugar de pelear de frente.
- Los NPC **se quedan en su zona**: profesores en sus oficinas, tutores en sus aulas, controlado con jigsaw. Solo salen por eventos concretos (escudo, llamados, raids) y de forma temporal, para que sea una dungeon y no un caos.
- Los alumnos usan un **sistema de estadísticas y rasgos** (parecido al de los bunnidogs, pero más sencillo y sin reproducción) para generar variedad sin crear mobs nuevos.
- Se integra con el mod **bunnidogs** (mismo proyecto): los bunnidogs son hostiles a todos los alumnos y profesores, excepto a Bianca (alumna de 4to).

---

## 2. Glosario y estados

| Término | Significado |
|---|---|
| **Neutral** | No ataca salvo que lo ataques o veas que atacas (según el tipo de NPC). |
| **Enojado** | Hostil activo hacia el jugador. |
| **Apaciguado** | Hostilidad suspendida temporalmente. |
| **Trackeo** | Los NPC te localizan automáticamente (como si tuvieras glowing), se mueven más rápido y hacen más daño. |
| **Llamar** | Excepción a la regla de quedarse en su zona: el NPC llamado acude rápido desde donde esté y luego vuelve a su posición. |
| **Importantes** | NPC que hay que matar para volver vulnerable al Director (ver §9.2). Son todos salvo los no importantes. |
| **No importantes** | NPC que no hace falta matar para volver vulnerable al Director: Bianca, Lucio, Deivis y un décimo de los alumnos de primaria. |

### Efectos propios del mod

| Efecto | Origen | Descripción |
|---|---|---|
| **Bad Omen LSM** | Pisar el escudo; matar al capitán de una patrulla; atacar o robar a la mamá de cuarto | Variante propia del Bad Omen de vanilla. Dura 2 h, con niveles I–V como en vanilla *(propuesta)*. Genera raids. |
| **Trackeo** | Pisar el escudo; mamá de cuarto | Ver glosario. Escudo: 5 min con +25 % de daño *(propuesta)*. |
| **Estudioso** | Folder, cuaderno, PC | Los profesores se vuelven neutrales contigo, al mismo nivel que con el uniforme de profesor. |
| **Regeneración (San Martín)** | Click derecho al bloque San Martín de Porres | Regeneración durante ~30 s. |
| **Expulsión** | Ataque de Hidalgo | Ver §7.4. |

---

## 3. Mecánicas globales

### 3.1 Territorio y llamados

- Profesores (11 en la sala de profes más otros en oficinas) y tutores (en sus aulas) permanecen en su zona.
- Pueden salir de forma temporal por eventos especiales (escudo, mamá de cuarto, raids).
- **Quién llama a quién:**
  - Los alumnos de **primaria** llaman a su profesor al ser enojados.
  - Los alumnos de **secundaria** no llaman.
  - **Rosa Sanmartiniana** puede llamar a cualquiera del colegio.
  - **Pollo** llama a la **estudiantina** (algunos alumnos de 3ro, 4to y 5to), que cambian de apariencia y sacan sus instrumentos.
  - La **enfermera** y la **miss Alessandra** (tutora de 3ro de secundaria) se llaman mutuamente.
  - **Hidalgo** es el único que se **teletransporta** cuando lo llaman.

### 3.2 Patrullas

Un profesor acompañado de unos alumnos. Matar al profesor (capitán de la patrulla) otorga **Bad Omen LSM**. Equivalen a las patrullas de pillagers.

### 3.3 Raids

- Emulan las raids de vanilla, pero con **alumnos y profesores en lugar de pillagers**.
- Ocurren **en lugares con muchos nidos de bunnidogs**.
- Se disparan con el Bad Omen LSM.
- **Composición por olas** *(propuesta de números)*:

| Ola | Composición |
|---|---|
| 1 | 6–8 alumnos de primaria |
| 2 | ~8 alumnos de secundaria |
| 3 | 3–4 profesores más el capitán con el estandarte del escudo |

- Como los bunnidogs son hostiles a alumnos y profesores, los nidos funcionan de forma natural como defensa contra los raiders.

### 3.4 Generación de colegios

- Los colegios se generan en **planicies** y son **extremadamente raros**.
- Hay **uno garantizado** en la planicie más cercana al spawn.
- Existe un **mapa crafteable** (tipo mapa de cartógrafo de vanilla) que lleva al colegio más cercano. Se craftea con **objetos legendarios del mod bunnidogs** *(lista pendiente)*.
- Los otros colegios permiten lootear con facilidad si llevas puesto el traje de Manuel Tirado (§8).

### 3.5 Música

- Las laptops reproducen discos (música custom, p. ej. "nene malo", "bby wow").
- Los discos se obtienen como **loot**.

---

## 4. Bloques

| Bloque | Efecto e interacciones |
|---|---|
| **Sillas** (Hall negro con rojo; Aula interactiva todo gris; Auditorio todo rojo o blanco) | Decorativos. |
| **Carpetas** (pupitres) del Aula interactiva/inglés: gris, con CPU y espacio adicional para teclado | Decorativas. |
| **Mesa de kiosko** | Decorativa. Ahí está la mamá de cuarto. |
| **San Martín de Porres** | Click derecho: regeneración ~30 s. Pasivo: regenera automáticamente a profesores y alumnos cercanos, más a profesores. Legendario: tarda muchísimo en minarse. Dos de ellos se usan en el ritual de invocación (§9). |
| **Escudo** (3x3) | Al pisarlo: **Bad Omen LSM**; todos los mobs te **trackean** (5 min, +25 % de daño); llama aleatoriamente a **6–10 profesores y 10–20 alumnos** durante 60 s *(propuesta)*. Los profesores salen de su aula de forma temporal. |
| **Laptop (común)** | Reproduce el disco en un radio de **6 bloques** *(propuesta)*. |
| **Laptop de Moisés** (mayor rareza) | Reproduce el disco en **toda la estructura**. Click derecho alterna entre himno y marcha del colegio. |
| **PC** (Aula interactiva y escritorio negro) | Click derecho: **Estudioso 3 min**. 20 % de que llame a un profesor agresivo. Cooldown de 5 min por PC *(propuesta)*. Atrae al **alumno vicioso**, que se sienta a usarla. Si se la rompes, el vicioso te persigue hasta matarte (de un golpe). |
| **Campana de recreo** | Bloque. Los NPC salen al patio (salvo algunos alumnos) durante **90 s**, tiempo para lootear ~2 salones. Cooldown de 15 min *(propuesta)*. |
| **Casilleros** | Contenedores con loot table baja: huevos podridos (trampa), polos, stickers y otros. En los de 4to y 5to hay **exactamente una Ardilla PUCP** garantizada. |
| **Objetos perdidos** | Contienen ropa de alumno **segura**. |
| **Ataúd de Manuel Tirado** | Funciona como cofre del tesoro (§9). |

---

## 5. Items

### 5.1 Armas

| Item | Descripción |
|---|---|
| **Regla** | Arma de los profesores (tiers: madera, piedra, oro, hierro, diamante, netherite). Mucho rango y ataque en área como la espada. Hidalgo no la usa: él tiene su propio ataque (ver §7.4). |
| **Lápiz / lapicero** | Armas leves. |
| **Escoba / trapeador** | Armas de limpieza. Las usa el personal de limpieza. |
| **Silla arrojable** | Gran daño; rompe el bloque donde cae. |
| **Pelotas** (plástica, fútbol, básquet, vóley) | Armas a distancia (ver §7.1). |

### 5.2 Herramientas de pacificación y enojo

| Item | Efecto en profesores | Efecto en alumnos | Notas |
|---|---|---|---|
| **Bola de papel** | Los enoja | Aturde ~3 s | Los alumnos la lanzan en masa como ataque a distancia. |
| **Folder** | Los apacigua; te da Estudioso 30 s | — | Consumible, un solo uso. |
| **Cuaderno** (de colores) | Apacigua **solo a profesores de primaria**; Estudioso 20 s | — | Consumible, un solo uso. |
| **Celular** | Enoja a todos | Apacigua al alumno enojado | |
| **Parlante portátil** | Los enoja | Apacigua a todos | Reproduce cualquier canción. |
| **Sticker** | Todos los profesores atacan a quien lo lleve | — | Se pone en polos de alumnos o del jugador. |
| **Guitarra eléctrica** (legendario) | Apacigua | Apacigua | 10 s a todos. |

### 5.3 Instrumentos

- **Tipos:** guitarra, flauta (primaria), mandolina, violín (más raro), pandereta.
- **Uso del jugador:** pacifican a los NPC en 10 bloques. Duración: guitarra 6 s, flauta 8 s, mandolina 10 s, violín 14 s, pandereta 6 s. Cooldown de 60 s *(propuesta)*.
- **Uso de NPC (estudiantina y otros):** buffean +20 % a alumnos y profesores en 15 bloques durante 20 s y llaman a 2–3 más *(propuesta)*.
- La reacción exacta por tipo de profesor o alumno está *pendiente*.

### 5.4 Consumibles y utilidad

| Item | Efecto |
|---|---|
| **Lonchera** | Te da 5 muslos; se recarga con comida. Sale del kiosko. |
| **Medalla de festidanza** | Multiplica la velocidad actual. |
| **Medalla de olimpiadas** | Multiplica el daño. |
| **Diploma** | Otorga más corazones. |
| **Huevo podrido** | Veneno fuerte y náusea en un área cercana. Aparece en algunos casilleros: afecta a quien lo abre y a los cercanos. |
| **Papel higiénico** | No hace nada. Hay uno por salón. |
| **Libros** | Solo existen de 2do a 5to. Incluyen la oración sanmartiniana. |

### 5.5 Legendarios

| Item | Descripción |
|---|---|
| **Ardilla PUCP** | Solo en casilleros de 4to y 5to (exactamente una). Inmunidad ante esos salones y ante Hidalgo; además esos salones se vuelven tus aliados. |
| **Guitarra eléctrica** | Apacigua a todos por 10 s. |
| **Mapa al ataúd** | Drop del Director. Indica dónde está el ataúd. |
| **Ataúd de Manuel Tirado** | Cofre del tesoro que aparece en un lugar aleatorio a ~750 bloques del colegio. |
| **Alma de Manuel Tirado** | Drop del fantasma de Manuel. Parte del ritual. |
| **Traje de Manuel Tirado** | Drop del boss. Pacifismo total (§8). |
| **San Martín de Porres (bloque)** | Legendario: tarda mucho en minarse. |

---

## 6. Armaduras

La armadura son **4 piezas**: peinado escolar (espacio de arriba, es decir, casco), polo o casaca (pecho), pantalón, short o falda (piernas) y calzado (botas). Los **zapatos** van con el uniforme y las **zapatillas** con el buzo. La casaca reemplaza al polo. Las prendas dan **inmunidad ante un grupo**: los NPC de ese grupo **no te atacan**.

| Prenda | Inmunidad | Notas |
|---|---|---|
| Pantalón de vestir, pantalón de buzo, short, falda | Alumnos | |
| Polo de buzo (manga corta o larga) | Alumnos | |
| Polo piqué (manga corta o larga) | Alumnos | |
| Casaca de promoción 6to | Alumnos | Mayor durabilidad |
| Casaca de promoción 5to Sec. | Alumnos *(a confirmar)* | Mayor durabilidad *(a confirmar)* |
| Zapatos (uniforme) | Alumnos | Botas |
| Zapatillas (buzo) | Alumnos | Botas |
| Peinado escolar | Alumnos | Casco (espacio de arriba) |
| Pantalón de profesores | Profesores | |
| Camisa de profesores | Profesores | |
| Polo con stickers | Ninguna | No afecta nada; los profesores siguen atacándote. |
| **Traje de Manuel Tirado** | Todos | Ver §8. |

### Reglas de la armadura

- **Más piezas reducen el rango de visión** de los NPC. Con una pieza ven a la mitad de distancia *(propuesta)*.
- **El set completo (4 piezas) los vuelve neutrales**: solo te atacan si los atacas.
- **Trampa:** los profesores usan su uniforme con protección y durabilidad altísimas. Es casi imposible matar a uno sin romperle el uniforme, así que no puedes robárselo fácilmente.
- **Dónde se consigue:**
  - Ropa de alumno: casilleros, salones (pequeña chance) y objetos perdidos (seguro).
  - Ropa de profesor: solo en sus oficinas.

---

## 7. Entidades

### 7.1 Pelotas

| Entidad | Comportamiento |
|---|---|
| **Pelota de plástico** | Normal. |
| **Pelota de fútbol** | Mayor velocidad y daño. |
| **Pelota de básquet** | Mayor daño. |
| **Pelota de vóley** | Mayor velocidad. |

Como item se usan como arma a distancia.

### 7.2 Sistema de estadísticas de alumnos

- **Stats (0–5):** Vitalidad, Fuerza, Velocidad, Percepción (rango de visión) *(propuesta)*.
- **Rasgos opcionales:** Vicioso (fijo en el Aula interactiva), Líder, Atlético *(propuesta)*.
- Se asignan al aparecer. Sin reproducción.

### 7.3 Personas

| Entidad | Comportamiento |
|---|---|
| **Personal de limpieza** | Neutrales; te atacan si te ven atacar a cualquiera del colegio. Usan escobas y trapeadores. |
| **Deivis** | El más fuerte de todos (velocidad normal). Muy importante que no te vea atacar a nadie. |
| **Alumnos de primaria** | Si los enojas, atacan como mini zombies y llaman a su profesor. |
| **Alumnos de 6to** | Se comportan como secundaria pero con tamaño de primaria; casaca de minipromo. No llaman profesor. |
| **Alumnos de secundaria** | Stats pendientes. No llaman profesor. |
| **Alumnos de 4to / 5to** | Una entidad distinta por grado. Sus stats están pendientes. |
| **Alumno vicioso** | No sale del Aula interactiva; si le rompes la PC te mata de un golpe. |
| **Estudiantina** | Alumnos de 3ro a 5to; se transforman al ser llamados por Pollo. |
| **Rosa Sanmartiniana** | Alumna especial que puede llamar a cualquiera del colegio. |
| **Brigadieres** | Uno de 6to de primaria y otro de 5to de secundaria. |
| **Tutores** | En sus aulas. |
| **Profesores** | 11 en la sala de profes, más otros en sus oficinas. Intentan quedarse en su zona. |
| **Junior** | Lesionado: no muy débil, pero lento. |
| **Psicólogas** | Pendiente. |
| **Director** | Invulnerable hasta cumplir la condición de §9. |
| **Pollo** | Llama a la estudiantina. |
| **Lucio** | Neutral. |
| **Enfermera** | Se llama con Miss Alessandra. |
| **Miss Alessandra** | Tutora de 3ro de secundaria. Se llama con la enfermera. |
| **Moisés** | Dueño de la laptop mayor. |
| **Mamá de cuarto** | Vendedora en el kiosko (rol de trueque). Si la atacas o le robas, actúa como pisar el escudo pero **más fuerte** (Bad Omen más alto, más llamados, trackeo de 10 min *(propuesta)*). **Todo el colegio te ataca excepto 5to.** |
| **Bianca** | Alumna de 4to; los bunnidogs no la atacan. |

### 7.4 Minibosses

| Miniboss | Detalle |
|---|---|
| **Hidalgo** | Único NPC que se teletransporta al ser llamado. No usa regla. Su ataque aplica **Expulsión**: si sigues dentro de la estructura del colegio 10 s después, cada 8 s invoca 3 alumnos de primaria (mini zombies) o 2 vexes de Manuel Tirado, **alternando** ambos *(propuesta de números)*. La Ardilla PUCP da inmunidad contra él. |
| **Yahu (Alcalde)** | Alumno de 2do de secundaria. |
| **Fantasma de Manuel Tirado** | Estilo fantasma de Ice and Fire. Aparece en el **auditorio** una vez derrotado Hidalgo. Su drop es el **alma**. |

### 7.5 Boss final

**Manuel Tirado Andrade (invocado)**

- Solo se puede invocar dentro de la estructura del colegio.
- Tiene **fases**. Mientras su vida baja con tus ataques, va **reviviendo a todos los muertos como fantasmas** (los fantasmas son vulnerables).
- Sigue vivo hasta que lo matas.
- Su drop es el **traje**.

---

## 8. Traje de Manuel Tirado

Mientras lo lleves puesto, todos los NPC son **completamente pacíficos contigo**, sin importar lo que hagas o hayas hecho. Facilita lootear otros colegios.

---

## 9. Progresión y ritual

### 9.1 Cadena de eventos

1. Encuentras un colegio (con el mapa craftado con legendarios de bunnidogs, o el garantizado cerca del spawn).
2. Dentro, usas las mecánicas (uniformes, folders, instrumentos, campana, etc.) para sobrevivir y avanzar.
3. Matas a Hidalgo: aparece el **fantasma de Manuel** en el auditorio. Al vencerlo obtienes el **alma**.
4. Matas a todos los NPC **importantes** (ver §9.2). El **Director** pasa a ser vulnerable.
5. Matas al Director. Entre sus drops está el **mapa al ataúd de Manuel**.
6. Sigues el mapa y consigues el **ataúd** (cofre del tesoro a ~750 bloques).
7. Vuelves al colegio y haces el **ritual** junto a dos bloques de **San Martín de Porres**: estos emiten rayos hacia el ataúd, igual que los ender crystals al revivir al ender dragon. Si el ritual no se hace dentro de la estructura y con los dos santos, no funciona.
8. Manuel Tirado revive y se convierte en el **boss final** (§7.5).

### 9.2 Condición del Director

El Director es invulnerable hasta que hayas matado a **todos los NPC importantes**.

- **No importantes (no hace falta matarlos):** Bianca, Lucio, Deivis y **un décimo de los alumnos de primaria**. No importa cuál décimo; basta con haber matado al 90 % de los alumnos de primaria.
- **Importantes (obligatorios):** todos los demás: profesores, tutores (incluida la miss Alessandra), personal de limpieza (salvo Deivis), psicólogas, enfermera, Moisés, Pollo, Junior, el resto de los alumnos y los minibosses.

Esto incluye a Yahu, el vicioso, Rosa Sanmartiniana y los brigadieres: también deben morir.

---

## 10. Integración con bunnidogs

- Los bunnidogs son **hostiles a todos los alumnos y profesores**, excepto a Bianca.
- Las **raids** ocurren en zonas con muchos nidos de bunnidogs.
- El **mapa al colegio** se craftea con objetos legendarios de bunnidogs.
- El mod es **el mismo proyecto** que bunnidogs.

---

## 11. Elementos descartados

- Pistola de juguete (invocaba a Hidalgo).
- Mazo como item del mod.

---

## 12. Pendientes y supuestos

### Pendientes (faltan datos)

1. Ingredientes del crafteo del mapa al colegio (lista de objetos legendarios de bunnidogs por definir).
2. Casco y botas del uniforme de profesores, y dónde se consigue el peinado escolar.
3. Dónde se obtiene la guitarra eléctrica (legendario).
4. Estadísticas numéricas de cada NPC (vida, daño, velocidad) y de los tiers de la regla.
5. Reacción exacta de cada instrumento según tipo de profesor o alumno.
6. Stats de alumnos de secundaria, 4to y 5to; detalles de psicólogas.
7. Disposición exacta del ritual (cómo se colocan los dos santos y el ataúd).
8. Versión de Minecraft y loader del mod.

### Supuestos a confirmar

- Casaca de promoción 5to Sec. tiene el mismo efecto que la de 6to.
- Cualquier combinación de piezas de alumno (p. ej. zapatos con buzo) cuenta para el set completo.
- Los números de las olas de raid, los valores de duración, cooldown y rango son propuestas de balance.
