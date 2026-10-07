# Reglas escolares

Implementación sobre `c0fa4c3`. No compilada ni probada en Minecraft.

## Combate y materiales

Las seis reglas usan el material de su espada vanilla: durabilidad, reparación en yunque, encantabilidad, minería y desgaste por ataque/minería. Daño total y tiers: `ITEM_SPECS.md` §1. Daño 0.5 puntos menor que la espada equivalente; velocidad 1.6 ataques por segundo.

En la mano principal añaden 1.5 bloques al alcance de entidades: normalmente 4.5 en supervivencia y 6.5 en creativo. Se suma a otros modificadores. Este atributo también amplía la interacción con entidades; no amplía la colocación ni rotura de bloques. En la mano secundaria no aporta atributos.

Se incluyen en `minecraft:swords`: barrido y elegibilidad para encantamientos de espada mediante etiquetas vanilla. Barrido bajo las condiciones vanilla: ataque cargado, en el suelo, sin sprint ni crítico y movimiento lento. El daño y la zona secundaria son los de vanilla; el alcance adicional amplía la selección del objetivo principal, no el radio del barrido. Netherite tiene resistencia al fuego.

## Sonido de impacto

Las seis reglas reproducen `lsmmod:ruler_hit` al golpear una entidad viva. El archivo proporcionado `ruler_hit.mp3` se convirtió a Ogg Vorbis mono, 44100 Hz, conservando la duración aproximada de 0.362 segundos. Ruta: `assets/lsmmod/sounds/ruler_hit.ogg`, registro en `ModSounds` y definición en `sounds.json`, con subtítulos español/inglés. Se emite desde el servidor para evitar duplicados.

## Obtención

Madera, piedra, oro, hierro y diamante: dos materiales en columna sobre un palo. Madera admite tablones; piedra usa la etiqueta vanilla de materiales de herramientas de piedra. Netherite: plantilla de mejora a netherite, regla de diamante y material de netherite en mesa de herrería. Aparecen en la pestaña creativa del mod.

## Modelo y texturas

Tabla estrecha de 32 píxeles de largo, escala 0.5 en tercera persona: longitud aproximada de un bloque (un metro). Mango central elevado sobre dos apoyos inspirado en la referencia. Graduación 0–100 en ambas caras, con números cada diez y colores por material. Atlas de 128×128: cara frontal, reverso y zona independiente del mango/laterales.

Recursos regenerables con `python tools/create_ruler_assets.py` (requiere Pillow). Tiers en `tools/ruler_tiers.json`. Las traducciones y los registros Java se mantienen junto a esa tabla.

## Verificación manual pendiente

- Equipar cada tier: alcance extra únicamente en mano principal y daño/velocidad del cuadro.
- Comparar espada/regla a distancias de 3 a 4.5 bloques; verificar barrido cargado y ausencia de barrido en salto/sprint.
- Comprobar recetas, mejora a netherite, reparación y encantamientos de espada.
- Revisar mango, graduación y tamaño en inventario, manos, marco y suelo.
- Comprobar desgaste y resistencia al fuego de netherite.
