# Auditoría de incorporación de recursos — 2026-10-08

Revisado sobre origin/master 8845c65 más la incorporación de las tres puertas.

| Recurso | Estado comprobado | Pendiente |
|---|---|---|
| Puertas de comedor, profesores y baños | Bloques/items registrados, creativo automático, 32 estados por puerta, modelos/texturas/item en src, traducciones, tags y loot de mitad inferior | Prueba dentro de Minecraft |
| Timbre escolar | Bloque/item activos; modelo con los mismos 10 elementos y textura idéntica al archivo de newresources | La copia y la tabla histórica de newresources no indican ausencia del juego |
| Pupitre de primaria | Bloque/item y contenedor activos; modelo con los mismos 12 elementos y textura idéntica | Copia histórica de newresources |
| Estatua de San Martín de Porres | Bloque de dos mitades/item activos; modelos con los mismos 16/14 elementos, referencias con namespace corregido | No hay loot_table/blocks/san_martin_de_porres.json. Los efectos previstos en BLOCK_SPECS no quedan verificados por tener el modelo registrado |
| Escudo 3×3 | Registros/modelos/texturas activos; bbmodels de newresources son fuentes editables | No falta incorporar las fuentes al runtime |
| Discos de himno y marcha | Items, modelos, sonidos y texturas activos; PNG de newresources idénticos a lsm_anthem/lsm_march | Copias con nombres antiguos |
| Golpe de regla MP3 | Sonido Ogg activo y referenciado en sounds.json | MP3 es fuente; Minecraft utiliza el Ogg |
| Folders/cuadernos | 32 items y modelos/texturas activos, incorporados por los cambios recientes de master | Renders son vistas previas |
| Once skins en newresources/skin_grados/*.png | PNG de 64×64 presentes; NO existen textures/entity/students/1p.png–6p.png y 1s.png–5s.png en src | Actualmente se usan placeholders. Revisar compatibilidad con uniforme/UV actuales antes de sustituirlos; no modificados en esta tarea |
| girl.java y fotos/referencias/renders | Fuentes y material de referencia, fuera del runtime | No son por sí mismos items o bloques pendientes |

## Verificación

Los modelos activos revisados no tienen referencias lsmmod faltantes de textura o
modelo padre. Los estados e items revisados no apuntan a modelos lsmmod ausentes.
Esta revisión de archivos no demuestra que todas las mecánicas planeadas existan
ni sustituye una prueba en Minecraft. Las puertas existentes en estructuras no
se han reemplazado automáticamente.

Compilación: BUILD SUCCESSFUL con Java 25, checkFurnitureOutlines verifica 188
formas/orientaciones. Contenido de build/libs/lsmmod-1.0.0.jar comprobado: las
puertas tienen estados, modelos, texturas, items y loot empaquetados. La
compilación requirió recuperar del historial el JAR local de Jade, ignorado por
Git y no incluido en el commit. Prueba dentro de Minecraft pendiente.
