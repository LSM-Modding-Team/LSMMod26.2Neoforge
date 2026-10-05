# RAID_PLAN: patrullas y raids propias de LSM (plan, sin código)

**Estado: PLAN, sin implementar.** Escrito en la sesión 4, solo docs, antes del hito M6 (`docs/ROADMAP.md`) como pedía `MECHANICS_SPECS.md` §4. **No se codifica hasta que el usuario lo diga (R10).** Implementa el chunk `R1` (que aquí se parte).

Marcas: sin marca = **decidido**; *(propuesta)*; *(a confirmar)*. **Pick** en negrita = lo que propongo; el usuario lo acepta en una línea o con "sigue con tus picks". **No hay números nuevos de balance:** los de las olas son los *(propuesta)* del diseño; los demás dicen "sin definir".

Fuentes: `MECHANICS_SPECS.md` §3, §4, §5; `INTEGRATION_BUNNIDOGS.md` §5; `docs/history/DESIGN_SOURCE_v1.md` §3.2, §3.3.

---

## 1. Problema

El diseño quiere raids como las de vanilla pero con **alumnos y profesores**, que ocurren **donde hay muchos nidos de bunnidogs** y que dispara el **Bad Omen LSM**. Además quiere **patrullas** (un profesor capitán con alumnos) cuyo capitán, al morir, da Bad Omen LSM. La raid de vanilla está hecha para aldeas y raiders; reutilizarla probablemente no sirve *(por verificar, R14)*: no hay código de vanilla en el clon de NeoForge para comprobarlo.

## 2. Idea en un párrafo

**Pick: un gestor de raids propio** (datos guardados por mundo) que, cuando un jugador con Bad Omen LSM está en una zona con "muchos" nidos `bunnidogs:nest`, abre una raid con **tres olas** (alumnos de primaria, alumnos de secundaria, profesores con su capitán), muestra su progreso y la termina cuando mueren todos o el jugador la pierde. La **lógica** (tabla de olas, umbral de nidos, nivel del omen) es Java puro en `rules/`; el pegamento con Minecraft es delgado (R6). Las patrullas son un sistema aparte que solo comparte el efecto Bad Omen LSM.

## 3. Lo que ya está dicho (no se rediscute)

* **Disparador:** Bad Omen LSM. Lo dan pisar el escudo, matar al capitán de una patrulla, y atacar o robar a la mamá de cuarto (con un nivel "más alto", valor sin definir). Dura 2 h, niveles I-V *(propuesta)*.
* **Olas** *(propuesta de números)*: 1 = 6-8 alumnos de primaria; 2 = ~8 de secundaria; 3 = 3-4 profesores más el capitán con el estandarte del escudo.
* **Dónde:** donde hay muchos nidos. Los bunnidogs son hostiles a alumnos y profesores, así que los nidos son la defensa natural.
* **Patrulla:** profesor capitán + unos alumnos; tamaño, dónde salen y cada cuánto: sin definir.

## 4. Partes

| Parte | Qué es | Dónde vive | Depende de |
|---|---|---|---|
| **Tabla de olas** | Composición por ola como dato | `rules/` | nada (Java puro) |
| **Regla de lugar (D6)** | ¿hay "muchos" nidos alrededor? | `rules/` (decisión) + pegamento (contar) | investigar cómo **solo leer** POIs: `take` quita el ticket de un perro y no sirve (`INTEGRATION_BUNNIDOGS.md` §5) |
| **Gestor de raids** | Estado de cada raid abierta, guardado con el mundo | `raid/` | familia de API nueva: datos guardados por mundo |
| **Disparo** | Bad Omen LSM + zona válida → abrir raid | `raid/`, `effect/` | `I2` (efecto), `N1` (NPC) |
| **Aparición de olas** | Crear los NPC de cada ola alrededor | `raid/` | `N1` (los NPC existen y tienen stats al aparecer) |
| **Señal visible (R11)** | Barra de progreso y aviso de cada ola | `client/` o servidor | familia de barra de jefe (compartida con `K1`) |
| **Patrullas** | Capitán + alumnos, y el Bad Omen al morir el capitán | `npc/`, `raid/` | `N1`, `I2` |
| **Estandarte del escudo** | Lo lleva el capitán de la ola 3 | item | no tiene spec: V26 |

## 5. Decisiones, con mi pick

| Id | Decisión | Pick *(propuesta)* |
|---|---|---|
| D6 | Qué es "muchos nidos" y cómo se elige el lugar | **Dos parámetros de datos: cantidad mínima de nidos y radio, ambos en un solo sitio, valores sin definir hasta que el usuario los dé.** El lugar es el **jugador con el omen**, no un punto fijo: más simple que elegir un centro. |
| R-a | ¿Reutilizar la raid de vanilla? | **No, gestor propio.** Razón: la de vanilla se ancla a aldeas y a un tipo de raider; adaptarla pide mixins (el paquete de mixins hoy no existe). Se cambia si la investigación (R14) dice lo contrario. |
| R-b | Con omen pero sin nidos cerca | **No pasa nada, el omen sigue corriendo** (como en vanilla, de memoria, *(a confirmar)*). Así el omen se puede "llevar" a una zona de nidos. |
| R-c | Raids a la vez | **Una por jugador.** Más jugadores en la misma raid: sin definir. |
| R-d | Quién abre la raid de la mamá de cuarto | **La misma regla que el escudo**, solo con nivel de omen mayor. |

## 6. Chunks propuestos (una API nueva por chunk, R6; ids provisionales)

| Id | Qué | API nueva (error de compilación más probable) |
|---|---|---|
| `R1a` | Tabla de olas, umbral de nidos y nivel del omen en `rules/`; `RulesCheck` | ninguna (Java puro). Va junto a `N0` si es posible |
| `R1b` | Gestor de raids guardado con el mundo; un comando de depuración abre una raid vacía | datos guardados por mundo |
| `R1c` | Contar nidos sin quitar tickets | consulta de POI de solo lectura |
| `R1d` | Disparo por Bad Omen LSM | nada nuevo si `I2` ya existe |
| `R1e` | Aparición de las olas con NPC | creación de entidades en el mundo (si `N1` no la cubrió) |
| `R1f` | Barra de progreso y avisos | barra de jefe |
| `R1g` | Patrullas | nada nuevo si `N1` existe |

`R1b` en adelante necesitan M4 (`N1`) y M3 (`rules/`). Con el zip de bunnidogs (D5) y la decisión D1 resueltos.

## 7. Dejado fuera a propósito

* Todo balance: cantidades de nidos, radio, duración de raid, tiempos entre olas, daño y vida de los raiders (P4).
* Qué pasa al **ganar o perder** una raid, recompensas y el efecto de los niveles I-V del omen sobre el número de olas: **V25**, sin definir. No lo decide ningún chunk por su cuenta.
* Patrullas: tamaño, zonas y frecuencia (sin definir en el diseño).
* Raids en otras dimensiones, o fuera de colegios: el diseño no habla de ello.

## 8. Vacíos nuevos

* **V25:** fin de una raid (victoria, derrota, recompensa) y efecto del nivel del Bad Omen LSM sobre las olas.
* **V26:** el **estandarte del escudo** que lleva el capitán de la ola 3: qué es (item, bloque, solo visual) y qué hace.

Registrados en `docs/DESIGN.md` §5.3.
