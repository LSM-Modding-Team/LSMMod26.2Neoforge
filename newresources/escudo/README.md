# Escudo aportado por el usuario

Origen: escudo.zip. Los nueve PNG de 16×16 se integran con las uniones del símbolo blanco y la franja dorada corregidas y el fondo de classroom_floor (baldosas 2×2) a assets/lsmmod/textures/block/lsm1.png … lsm9.png. Los modelos exportados se integran con sus geometrías y caras originales; se corrigen las referencias de textura y partículas al namespace lsmmod. Los bbmodels editables tienen sus texturas embebidas sincronizadas con los PNG del mod. El generador tools/create_school_shield_floor_assets.py conserva todos los píxeles del emblema y pre-rota únicamente el fondo de baldosas 180° para compensar la rotación de las caras superiores y alinear las juntas con el piso.

Distribución vista desde arriba, con la parte superior hacia donde mira el jugador:

```
lsm6 lsm4 lsm7
lsm3 lsm1 lsm2
lsm8 lsm5 lsm9
```

El item school_shield coloca todo el conjunto horizontal de 3×3 como bloques completos. lsm1 es el centro. Las cuatro orientaciones usan las mismas posiciones y rotaciones del bloque existente. La vista de inventario se forma uniendo los nueve PNG con las mismas texturas corregidas del mod. El item queda registrado y disponible en LSM Mod; un solo objeto al romper el conjunto.

Orientación final según la referencia del usuario: borde recto arriba y punta abajo. Cada cara superior usa rotation=180 y las posiciones se invierten conjuntamente; el símbolo negro queda sobre el centro. El mosaico de inventario coincide píxel a píxel con las caras superiores renderizadas.
