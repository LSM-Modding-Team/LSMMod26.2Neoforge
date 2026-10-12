# Arco desplazado ocho píxeles

`football_basketball_goal` se desplaza **8 píxeles en el eje X local positivo**, hacia la derecha del arco según su orientación. El arco, red, tablero, aro y red de básquet se trasladan juntos, con la misma medida física de 8×6×3 bloques. Colisión y selección se regeneran en las nuevas posiciones.

Para mantener todas las cajas y elementos dentro de sus celdas, la rejilla de colocación es ahora 9×6×3: el objeto ocupa x=8..136 píxeles, con medio bloque de margen en cada extremo de la rejilla de 144 píxeles. Los índices anteriores 0..143 conservan sus coordenadas; los 18 índices de la columna extra se añaden al final, 144..161. Soportes, inversa al controlador y espejado adaptados. 72 celdas ocupadas y caída única conservada.

El tamaño y posición del ítem de inventario se mantienen mediante normalización sin el desplazamiento. Las coordenadas UV del tablero y la cesta compensan el desplazamiento para conservar sus dibujos. Texturas originales intactas.

Los arcos ya colocados deben retirarse y colocarse de nuevo para incluir la nueva columna; no se ocupan automáticamente bloques vecinos. El código comprueba integridad y soporte como antes.

Formas exactas directas y rotaciones compartidas conservadas. El optimizador sigue preparando cuatro selectores de renderizado, aunque el dominio físico pase a 648 estados. Verificaciones estáticas de límites, cuatro orientaciones, entrada abierta, mallas/texturas, espejado, coordenadas estables y traslación exacta de la colisión; regresión del optimizador correcta. Sin compilar Java/Gradle ni abrir Minecraft.
