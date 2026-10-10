# Pared y reja exterior

street_school_wall es un bloque completo de la misma familia que light_school_wall, con estuco beige cálido inspirado en la referencia: base RGB 195/183/158, variaciones pequeñas y resolución 16×16.

street_school_bars se coloca individualmente y hereda IronBarsBlock: conexiones horizontales con barras/paredes, WATERLOGGED y colisión vanilla. Su malla diagonal usa transparencia real y metal marrón oscuro. Los marcos se distribuyen en grupos de cuatro celdas de ancho por tres de alto desde el extremo oeste/norte del tramo y su base. Las piezas se recalculan tras cambios; no se coloca ni elimina automáticamente un panel completo. Las esquinas y cruces conservan las conexiones de barras; los paneles rectos son el caso de referencia de 4×3.

Las conexiones se dibujan mediante modelos multipart, con textura de 16×16 por celda y marco exterior continuo. Selección simple independiente de la colisión heredada. Los escaneos no cargan chunks y están limitados a 256 celdas. Ambos bloques tienen ítems, traducciones, drops individuales y etiqueta de pico.

Generador tools/create_street_school_assets.py; comprobación estática tools/check_street_school_assets.py: 384 estados de reja, modelos, transparencias, marcos y agrupación. Preview previews/street_school_bars_4x3.png. Sin compilar ni probar en Minecraft.
