# Pendientes raised cortas y proporciones de columna

## Pendiente invertida de 3 × 4 bloques

Tres piezas nuevas en **LSM Mod School Blocks**:
`school_inverted_slope_short_raised_1`, `_2` y `_3`.
Todas usan `classroom_white_wall`, un OBJ compartido y cuatro colisiones compartidas
por perfil. Cada pieza tiene ocho estados (orientación y waterlogged).

La cara inferior comienza en y=8 px y termina en y=56 px, ocho píxeles por debajo
del techo del conjunto (y=64 px). Recorre 48 px. Son pendientes decorativas
invertidas para la zona bajo las escaleras.

Coloca cada pieza un bloque más alto que la anterior. Con orientación norte:

| Pieza | Desplazamiento desde la primera (x,y,z) |
|---|---|
| 1 | (0,0,0) |
| 2 | (0,1,-1) |
| 3 | (0,2,-2) |

Este: avanza +x; sur: +z; oeste: -x. Cada pieza se orienta en la dirección de
avance. El archivo `tools/school_short_raised_slope_layout.json` incluye las cuatro
distribuciones. Los modelos superan la altura de su celda y no colocan bloques
auxiliares automáticamente.

## Columna

El fuste mide 28 px de diámetro (límites -6..22 en X/Z). La base circular mide
34 px de diámetro (-9..25) y ocupa y=0..8. El fuste comienza en y=8 sobre la base.
La base solo aparece cuando no hay otra columna debajo; las columnas superiores
usan el fuste completo de y=0..16.

Se mantienen los recortes por cuadrante, los materiales de las paredes vecinas,
los huecos de bloques pintables y el límite de mampostería de 12 px en las
barandas. La caché física incluye la presencia de la base; el color no crea otra
forma. El contorno de selección sigue siendo una caja exterior simple.

## Verificación

`check_school_short_raised_slopes.py` verifica perfil, orientación, normales,
continuidad, referencias, 24 estados e idempotencia. `check_dark_tiles_drain_column.py`
verifica 141862 casos de modelo y geometría, incluyendo ambas versiones físicas
(con/sin base), paredes, barandas y cuadrantes retirados. La comprobación general
de recursos valida las referencias de modelos/texturas. Sin compilación Java ni
prueba en Minecraft.
