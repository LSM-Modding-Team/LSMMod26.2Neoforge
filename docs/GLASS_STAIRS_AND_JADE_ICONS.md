# Escaleras de cristal y vista de pintura en Jade

Las cuatro familias de escaleras `mixed_school_glass`, `mixed_school_glass_reversed`, `supported_school_glass` y `supported_school_glass_reversed` usan `classroom_white_wall` en lugar de `minecraft:block/white_concrete`, en todos sus modelos y variantes. Geometría, UV, cristales, otros materiales y estados se conservan; ambos generadores mantienen las referencias nuevas.

Jade 26.2 permite sustituir el icono con `IComponentProvider.getIcon` y `registerBlockIcon`. El proveedor existente del nombre ahora también devuelve `JadeUI.item` con el bloque del color apuntado: pared clara, oscura, de calle, blanca, suelo de aula o bloque gris sin pintar. Jade dibuja su modelo de ítem con las texturas normales de Minecraft. Funciona con la cara del bloque original y con el cuadrado del bloque personalizable, incluidas superficies interiores; usa los mismos datos sincronizados que el nombre.

El icono muestra el bloque correspondiente a la pintura apuntada. No reproduce los demás colores ni los recortes del bloque personalizable: esa vista representaría una composición diferente del material al que corresponde el nombre. No se crean estados, modelos, renderizadores ni paquetes adicionales para Jade. El ajuste general de Jade para mostrar iconos debe estar activado. Sin Jade instalado, los bloques siguen funcionando.

API comprobada con los fuentes de Jade `26.2-neoforge`; regresiones estáticas de escaleras y preparación de modelos, texturas y referencias correctas. No se compiló ni se ejecutó Minecraft por instrucción vigente; falta comprobar el icono en el juego.
