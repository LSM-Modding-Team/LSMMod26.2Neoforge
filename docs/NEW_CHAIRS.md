# Sillas por ambiente

Se añaden seis bloques sin reemplazar las sillas anteriores. Los modelos JSON
usan cubos, texturas de 16×16 y orientación nativa hacia el sur, como las sillas
existentes. Se pueden colocar en cuatro direcciones y usar con clic derecho
sin objeto en la mano para sentarse a la altura de 7/16 de bloque.

| ID (`lsmmod:`) | Modelo |
| --- | --- |
| `englishroomchair` | Aula interactiva: gris claro, respaldo perforado |
| `hallchair` | Hall: tapizado rojo, estructura negra, sin brazos ni paleta |
| `plasticchair_white` | Auditorio: blanca sin brazos |
| `plasticchair_red` | Auditorio: roja sin brazos |
| `plasticchair_white_arms` | Auditorio: blanca con brazos |
| `plasticchair_red_arms` | Auditorio: roja con brazos |

Las cuatro sillas de auditorio son bloques e ítems independientes: no hay
apilado dentro de un bloque. Esta es la alternativa de variantes separadas.
Los agujeros del respaldo gris y las ranuras del respaldo plástico forman parte
de la geometría, no de una textura transparente. La selección del respaldo gris
usa una envolvente sólida para facilitar el clic; los otros modelos usan sus
partes sólidas como colisión. Todas aparecen en la pestaña creativa del mod,
tienen drops propios, receta y nombres en inglés y español (`es_es`).

Recetas (mesa de trabajo):

- Aula / hall: `L  / LLL / I I`, con lana gris clara / roja e hierro.
- Auditorio sin brazos: `QD / QQQ / Q Q`, con cuarzo y tinte blanco / rojo.
- Auditorio con brazos: `QDQ / QQQ / Q Q`, con cuarzo y tinte blanco / rojo.

Para revisar en el juego, colocar las seis variantes mirando en cada dirección,
comprobar asiento, colisión y drop, y revisar los íconos del inventario.

## Registro completado

2026-10-05: se completan los registros de bloque e item de englishroomchair, hallchair y las cuatro plasticchair, utilizando NewChairShapes. La pestaña creativa las incluye automáticamente. Se conservan las tres sillas originales y school_shield. Compilación verificada en Windows con Java 25 y gradlew.bat build --offline: BUILD SUCCESSFUL. Prueba en Minecraft pendiente.
