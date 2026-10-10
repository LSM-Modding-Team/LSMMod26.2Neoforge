# Bloque conectable con borde amarillo

school_yellow_border_wall es un bloque completo gris oscuro, de aspecto de vereda, con un borde amarillo intenso de 3 píxeles. Al colocar vecinos del mismo tipo, el borde compartido desaparece: solo queda el perímetro del conjunto en cada cara visible. Se aplica horizontal y verticalmente, y también en el suelo/techo, mediante seis conexiones independientes. No hay límite propio de altura o ancho; solo se consultan los vecinos inmediatos, sin escaneos de grupos.

Texturas 16×16, gris RGB 73/74/72 con variación discreta y amarillo RGB 255/211/0. Dieciséis máscaras compartidas por los 64 modelos/estados. Retirar un vecino restaura el borde; giro y espejo transforman las conexiones. Colisión de cubo completo y un drop por bloque.

Generador tools/create_school_yellow_border_assets.py; verificación tools/check_school_yellow_border.py: 64 estados, seis caras, grosor exacto de 3 px y panel estático 5×1024 sin costuras internas. Sin compilar ni probar dentro de Minecraft.
