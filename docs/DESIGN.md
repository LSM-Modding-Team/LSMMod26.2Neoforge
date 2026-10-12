# Diseño y decisiones pendientes

Dungeon del colegio Libertador San Martín, con NPC fuertes, zonas de loot y mecánicas para apaciguar, evadir o usar uniformes. Los NPC permanecen en su zona salvo eventos temporales; alumnos variados mediante stats/rasgos sin reproducción. Bunnidogs ataca alumnos/profesores salvo Bianca. Jigsaw, progresión e integración siguen pendientes.

El spec del área manda. Sin marca = decidido; *(propuesta)* = ajustable; *(a confirmar)* = supuesto. Resuelve solo los pendientes del sistema solicitado; no bloquees nuevos modelos/texturas por decisiones de mecánicas ajenas.

## Vocabulario

Neutral: no ataca salvo agresión directa/presenciada según tipo. Enojado: hostil activo. Apaciguado: hostilidad suspendida temporalmente. Trackeo: efecto del jugador que permite a NPC localizarlo, moverse más rápido y hacer más daño. Llamar: acudir desde su zona y volver después. Patrulla: profesor capitán con alumnos; su muerte da Bad Omen LSM. Raid: ataque por olas propio de LSM. Importantes: deben morir para volver vulnerable al Director; excepciones Bianca, Lucio, Deivis y un décimo de primaria.

Descartados: pistola de juguete que invocaba Hidalgo y mazo como ítem propio.

## Pendientes P y supuestos A

| ID | Dato pendiente |
|---|---|
| P1 | Legendarios de bunnidogs y cantidades para mapa al colegio |
| P2 | Casco/botas de profesores y obtención del peinado escolar del diseño; el corte falso actual tiene receta propia |
| P3 | Obtención de guitarra eléctrica |
| P4 | Balance de NPC concretos/armadura futura; reglas y Student/Teacher actuales ya tienen valores en sus specs |
| P5 | Reacción musical por tipo de NPC |
| P6 | Stats/rasgos específicos de secundaria, 4to/5to y psicólogas |
| P7 | Disposición exacta de santos/ataúd en ritual y traslado del ataúd |
| A1 | Casaca 5to Sec. con mismo efecto que 6to |
| A2 | Cualquier combinación de prendas de alumno cuenta como set |
| A3 | Duraciones, rangos, cooldowns y cantidades de olas marcadas como propuestas siguen ajustables |

P8 resuelto: Minecraft 26.2, NeoForge 26.2.0.88, Java 25.

## Vacíos V

V1 resuelto: 6to cuenta como primaria para el 90 %. V5 resuelto: grupos de prendas en [NPC_SPECS](NPC_SPECS.md). V12 resuelto: folder a todos los profesores/Estudioso 30 s; cuaderno solo primaria/20 s.

| ID | Dato pendiente |
|---|---|
| V2 | Profesor al que llama primaria: tutor de aula u otro |
| V3 | Disparadores de llamados de Rosa, Pollo, enfermera y Alessandra |
| V4 | Matriz completa de testigos de agresión; Student/Teacher actuales tienen reglas propias |
| V6 | Visión con 2–3 prendas y relación con Percepción/Trackeo |
| V7 | Velocidad extra de Trackeo y balance para mamá de cuarto |
| V8 | Vexes de Expulsión y fantasmas de NPC revividos: cantidad/stats |
| V9 | Mamá agredida: excepción de 5to frente a Ardilla que protege 4to/5to |
| V10 | Deivis interpretado como personal de limpieza especial |
| V11 | Celular: «enoja a todos» significa profesores o todos los NPC |
| V13 | Qué profesores son de primaria y alcance de objetos/prendas sobre todo el grupo |
| V14 | Funciones futuras de sillas/pupitres por zona; conservar asientos e inventarios actuales |
| V15 | Traje final: una prenda o cuatro, ranuras |
| V16 | Uniformes mezclados de alumno/profesor |
| V17 | Silla arrojable/obtención/cooldown; lonchera/recarga; medallas/factor/duración; diploma/corazones; huevo podrido/radio/duración; laptops/discos/carga/loot/canciones; himno/marcha como discos o sonidos; repertorio del parlante |
| V18 | Trueque de mamá de cuarto, mercancía/precio de lonchera |
| V19 | Tamaño/distribución del colegio generable, zonas y cantidad de NPC por salón; catálogo del mundo y once grados ya existen |
| V20 | Yahu/Director: comportamiento, zona, ataques, stats/drops y título de Alcalde |
| V21 | Fases del boss y umbrales de vida |
| V22 | Ardilla garantizada: una por colegio, salón o casillero |
| V23 | Alcance de hostilidad bunnidogs sobre grupos especiales |
| V24 | Contraataque LSM a bunnidogs |
| V25 | Victoria/derrota/recompensa y efecto del nivel de Bad Omen LSM en raid |
| V26 | Estandarte del capitán: objeto, aspecto y función |

## Decisiones D

| ID | Estado/propuesta |
|---|---|
| D1 | Integración: propuesta de dos JAR, dependencia LSM→bunnidogs; falta decidir |
| D2 | Aplicado: docs/respuestas en español, código/IDs/comentarios en inglés; juego en_us y es_es |
| D3 | Arte a petición: Codex crea/edita modelos/texturas con referencias, generadores y revisión por renders en ASSETS |
| D4 | Progreso por colegio o global: propuesta por colegio; falta decidir |
| D5 | Verificar código/JAR de bunnidogs antes de integrar; no es requisito para arte LSM |
| D6 | Raid: cantidad mínima/radio de nidos sin valores; propuesta de centro en jugador con omen |
| D7 | Alma del ritual: propuesta entregarla en ataúd; falta decidir |

Estas propuestas conservan su estado; resumir documentación no las acepta. La petición actual del usuario resuelve el alcance autorizado de cada trabajo.
