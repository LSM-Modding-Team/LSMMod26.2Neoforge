# Suelo del baño

bathroom_floor usa el gris oscuro anterior del pavimento (RGB 73/74/72), con baldosas de paso 24×24 px, es decir 1,5×1,5 bloques, y juntas claras de 1 px. Resolución vanilla: 16×16 por textura. Nueve fases (X/Z módulo 3) aseguran continuidad en cualquier superficie y orden de colocación, incluidas coordenadas negativas: cada tres bloques hay dos baldosas por eje. La retícula está anclada al mundo, sin escaneos de vecinos ni límite de extensión. Lados y base son gris oscuro sin juntas. Bloque completo, sonidos de piedra, etiqueta de pico, ítem y drop individual.

Generador tools/create_bathroom_floor_assets.py; comprobación tools/check_bathroom_floor_assets.py: nueve fases, paso exacto de 24 px y continuidad en coordenadas positivas/negativas. school_entrance_pavement conserva el borde amarillo de 3 px y conexiones del anterior school_yellow_border_wall, con nuevo gris base #878d96. Sin compilar ni probar Minecraft.
