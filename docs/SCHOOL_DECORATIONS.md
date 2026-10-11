# Reloj, ventilador y lámpara de techo

school_clock es un reloj circular de pared, de 14 px de diámetro y exactamente 2 px de profundidad. Aro gris, esfera gris clara, marcas horarias, números cardinales y dos agujas negras estáticas. school_fan es un ventilador de pared de 6 px de profundidad, con tres aspas centradas en (8,8), motor y turbina coaxiales, rejilla de aros y ocho radios, y soporte junto a la pared. Es decorativo, sin animación ni interacción. Ambos se colocan sobre caras laterales de una pared sólida, con cuatro orientaciones y retirada si pierden el apoyo.

ceiling_light es una lámpara circular blanca, pegada al techo. Se coloca sobre la cara inferior de un bloque de apoyo y empieza encendida. Clic derecho alterna LIT: nivel máximo 15 encendida, 0 apagada. Solo el servidor cambia el estado; se actualiza el bloque y su luz con UPDATE_ALL. Difusor claro encendido y gris apagado, aro blanco. No depende de redstone.

Los modelos usan mallas OBJ redondas y una paleta de 16×16, con esfera de reloj 16×16 independiente. Selección/collisión con cajas simples para evitar resaltar cada alambre. Los tres bloques tienen ítems, traducciones, drops y etiqueta de pico.

Generador tools/create_school_decorations.py; comprobación tools/check_school_decorations.py: grosor del reloj, centrado de aspas, límites de montaje, caras sin degeneraciones, UV de paleta, cuatro orientaciones y dos estados de luz. Sin compilar ni probar dentro de Minecraft.
