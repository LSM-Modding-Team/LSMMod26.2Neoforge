# Muro escolar claro con baranda

`light_school_wall_railing` es un bloque individual con base completa de 16×12×16 px: se rebaja cuatro píxeles la altura de `light_school_wall`. Mantiene su textura original. El pasamanos de 2×2 px queda entre y18 y y20; su soporte central de 2×2 px va de y12 a y18. Todo el metal usa directamente la textura `school_gate_edge` del portón.

Conecta automáticamente en las cuatro direcciones como una valla: a otras barandas, vallas no leñosas, puertas de valla y caras sólidas compatibles. Una pieza aislada orienta el tubo según el jugador. Los tramos rectos alternan soporte/sin soporte cada dos bloques, contando desde el extremo norte u oeste del tramo. La pieza intermedia conserva siempre el tubo horizontal. Esquinas, cruces y piezas aisladas llevan soporte. Los cambios de vecinos recalculan el componente conectado cargado, sin cargar chunks.

Un solo ítem coloca una pieza; romperla devuelve una sola pieza. Admite agua, rotación y espejo. La selección usa un contorno simple separado de la colisión: base, postes y tubo conservan su geometría física. Los brazos no solapan sus volúmenes en el centro del tubo, evitando superficies duplicadas en las uniones. Los modelos llegan hasta 20 px de altura, dentro del rango de modelos vanilla.

Se eliminan la escalera de baldosas y las tres pendientes del intento anterior, sus registros, recursos, herramientas y guía. Las escaleras de cristal conservan su función existente.

Generador: `python tools/create_school_wall_railing_assets.py`.
Verificación estática: `python tools/check_school_wall_railing.py` (256 estados).
No se ha compilado ni probado en Minecraft.
