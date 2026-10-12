# Cristal tintado conectado y madera aclarada

`school_connected_tinted_glass` añade la versión conectable del cristal opaco
existente. Reutiliza `SchoolGlassBlock`, el espesor de 8px, la orientación y el
sistema SchoolGlazing: uniones verticales, separadores horizontales por longitud
par/impar y compatibilidad con las puertas/demás piezas que compartan huella.
Conserva el tono, los reflejos y la opacidad de school_tinted_glass y el marco
normal de school_glass. School Blocks, ítem/drop propios y traducciones es/en.

Las 32 máscaras son una selección visual Fusion, sin propiedades nuevas de
conexión. Sus 68 estados son los mismos 4 facing × 17 curtain que school_glass;
las cortinas y overlays existentes se reutilizan. El school_tinted_glass original
sigue siendo un bloque independiente de cuatro estados y sin conexiones.

Generador tools/create_connected_tinted_glass.py. El panel de tres columnas por
dos filas de previews/tinted_glass_and_counter_review.png comprueba la eliminación
de la unión horizontal entre filas y el espaciado de separadores. Se revisa la
opacidad, las 32 máscaras, los estados y la idempotencia en
tools/check_texture_review_changes.py. Colisiones originales almacenadas y
contornos simples heredados; ninguna entidad ni ticker continuo nuevo.

La textura canónica school_wood.png se aclara de RGB114/75/35 a RGB150/108/62,
conservando su grano de 16×16. Cambio reproducible en la paleta de
tools/create_bathroom_statue_assets.py. Los modelos que usan ese material,
incluidas las partes de madera de San Martín, reciben el nuevo tono. Render del
modelo real revisado en previews/school_wood_texture_review.png. Es el material
school_wood; school_wood_panel es otra textura.

Renders externos de los recursos finales, no capturas de Minecraft. Sin compilar.
