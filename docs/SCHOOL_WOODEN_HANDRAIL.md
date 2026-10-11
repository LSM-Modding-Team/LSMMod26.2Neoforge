# Pasamanos de entrada

`school_wooden_handrail`: un solo ítem coloca el tramo completo de seis bloques de largo, con subida de cuatro píxeles por bloque (24 en total), igual a la rampa de entrada. Dos barras de madera marrón oscura de 2 píxeles de grosor y montantes finos, sin poste largo. Se extiende en la dirección horizontal hacia la que mira el jugador: colocar desde el extremo bajo mirando hacia el alto, junto a la rampa. Sus apoyos inferiores siguen la inclinación y su altura es de un bloque sobre ellos.

La pieza ocupa varias celdas internas, pero se coloca y retira como una unidad. Comprueba espacio, altura, chunks cargados y permisos antes de colocar; un solo controlador genera el drop. No sustituye terreno ni partes de la rampa. Colocar en el espacio libre lateral, con el comienzo a la altura del soporte elegido. Contorno simple separado de la colisión inclinada estrecha.

Verificación estática: recursos, 72 estados, pendientes, continuidad de barras, límites de cada celda y controlador. No compilado ni probado en Minecraft por instrucción del usuario.
