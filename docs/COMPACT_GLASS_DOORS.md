# Puertas con cristal: selección visual Fusion

Las cinco puertas con cristal conservan sus IDs, ítems, modelos nativos, texturas, colisiones, bisagras, apertura, redstone y drops. Únicamente se retiran las tres propiedades visuales de conexión de su definición Java: `frame_layout`, `top_connected` y `bottom_connected`.

| Bloque | Antes | Ahora |
|---|---:|---:|
| double_school_entrance | 9216 | 288 |
| tall_classroom_entrance | 3072 | 96 |
| tall_bathroom_door | 3072 | 96 |
| tall_teachers_office_door | 3072 | 96 |
| tall_dining_door | 3072 | 96 |
| Total de estas puertas | 21504 | 672 |

Se eliminan 20832 estados (96,875 % de estas puertas). El total del mod pasa de 37981 a 17149 estados, conservando los 116 bloques. La auditoría JSON refleja el recuento actual.

La parte de madera usa directamente el modelo anterior. Para los cristales superiores, 22 modelos compuestos compartidos entre orientaciones/estados de redstone seleccionan mediante `lsmmod:glazing_mask` los modelos nativos anteriores. Las puertas altas conservan las texturas sustituidas de baño, sala de profesores y comedor en los modelos seleccionados, sin depender de que un compuesto herede sustituciones de materiales.

`SchoolGlazing.visualMask` ya contempla la tercera fila de las puertas altas y la tercera fila frontal de la puerta doble (`row=2, depth=0`). Mantiene la continuidad vertical, los separadores pares/impares y las filas mixtas con escaleras/cristales. Las celdas auxiliares de las hojas abiertas de la puerta doble no cuentan como cristal. La madera/apéndices móviles permanecen en el mismo modelo por cada estado de apertura. Los avisos cliente individuales y agrupados también invalidan las secciones con cristal de puerta, aunque el estado Java no haya cambiado.

## Compatibilidad y generación

La migración de paletas de chunks y de estados NBT directos se amplía a estos cinco IDs. Elimina únicamente las propiedades visuales retiradas antes de que vanilla decodifique los estados; conserva orientación, fila, columna, profundidad, apertura, bisagra y redstone. No altera índices de paleta ni datos empaquetados. Los mundos se guardan con el nuevo formato al salvarse; no se exige recolocar puertas.

`compact_school_door_assets.py` compacta selectores y puede expandirlos para que los generadores sigan produciendo los mismos modelos nativos. El generador de variantes altas, el de la puerta doble y la secuencia completa de cristal escolar terminan con los recursos compactos. La regeneración de las variantes altas desde la puerta base compacta es idempotente. No se cambiaron archivos de geometría nativa ni texturas en esta sesión.

## Verificación

- Comparación exhaustiva de las 21504 selecciones antiguas contra una copia anterior al cambio: mismos modelos y transformaciones.
- `check_compact_school_doors.py`: 96/288 estados, selección ordenada de máscaras, recursos idempotentes, registros, migración e invalidación cliente comprobados estáticamente.
- Regresiones de las tres variantes altas, puerta doble (9 celdas cerrada/15 abierta, hoja y UV, validación/redstone/drop), cristal, separadores horizontales, caras expuestas y cortinas correctas.
- Recursos preparados: 3723→3349 modelos, 10549→10085 selectores explícitos; equivalencia de modelos, referencias y transformaciones verificada.

Sin compilar ni ejecutar Minecraft por la instrucción vigente. La última medición del usuario (130 segundos) corresponde al cambio anterior de cristales/cortinas. El nuevo tiempo y el funcionamiento dentro del juego siguen pendientes de comprobar.
