# Pelotas

Cuatro ítems y cuatro tipos de entidad `lsmmod:plastic_ball`, `football_ball`, `basketball_ball` y `volleyball_ball`. La misma entidad cambia entre proyectil y juguete; `Projectile`, `FlightTicks` y `Owner` se guardan junto con posición y movimiento. No se duplica ni se consume otra pelota al impactar. Valores numéricos: [ITEM_SPECS.md §2](ITEM_SPECS.md#2-pelotas-item-juguete-y-proyectil).

## Interacción

- Clic derecho sobre un bloque: coloca una pelota de medio bloque de diámetro si el espacio está libre.
- Clic derecho en el aire: lanza hacia la mirada; el lanzador no recibe su propio proyectil.
- Clic derecho sobre la pelota: recoge una unidad; con inventario lleno conserva la entidad.
- Empuje por contacto de una entidad: plástico y fútbol ruedan; básquet y vóley rebotan.
- Clic izquierdo: impulsa los juguetes; vóley solo admite ese golpe cuando está en el aire y sale hacia la mirada del jugador.
- Las pelotas conservan su modo y movimiento al guardar el mundo. Un proyectil que golpea un bloque o un ser vivo pasa a juguete; tras 20 segundos sin impacto también pasa a juguete y cae.

La física se calcula en el servidor. El vóley juguete limita sus choques dañinos a uno por segundo y evita golpear a quien lo acaba de impulsar durante 8 ticks. Otros juguetes no causan daño por contacto. La fricción y las paredes frenan o redirigen el movimiento. El alcance visual usa el modelo del ítem para ambos modos.

## Recursos

`python tools/create_ball_assets.py` regenera los cuatro atlas pixelados de 16×16, las esferas de cubos escalonados y los archivos de definición de ítem. No necesita ejecutar Minecraft.

## Validación

Revisión estática de JSON, referencias de recursos, registro de las cuatro entidades/renderers y firmas de API en las fuentes locales de Minecraft 26.2. No se ha compilado ni ejecutado Minecraft.

Al probar en el juego: comprobar los cuatro daños por dificultad; velocidad de vuelo (20 ticks = 1 segundo); colocación junto a paredes; recogida con inventario lleno; rebotes y redirección del vóley; sincronización en multijugador; guardar/cargar durante el movimiento. Verificar también modelos/texturas en inventario, mano y mundo.
