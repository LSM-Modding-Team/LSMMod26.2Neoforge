# Pelotas

Cuatro ítems y cuatro tipos de entidad `lsmmod:plastic_ball`, `football_ball`, `basketball_ball` y `volleyball_ball`. La misma entidad cambia entre proyectil y juguete; `Projectile`, `FlightTicks`, `Owner`, `TravelledDistance`, `Roll` y `Heading` se guardan junto con posición y movimiento. No se duplica ni se consume otra pelota al impactar. Valores numéricos: [ITEM_SPECS.md §2](ITEM_SPECS.md#2-pelotas-item-juguete-y-proyectil).

## Interacción

- Clic derecho sobre un bloque: coloca una pelota de medio bloque de diámetro si el espacio está libre.
- Clic derecho en el aire: lanza hacia la mirada; el lanzador no recibe su propio proyectil.
- Clic derecho sobre la pelota: recoge una unidad; con inventario lleno conserva la entidad.
- Empuje por contacto de una entidad: plástico y fútbol ruedan; básquet y vóley rebotan.
- Clic izquierdo: impulsa los juguetes; vóley solo admite ese golpe cuando está en el aire y sale hacia la mirada del jugador.
- Las pelotas conservan su modo y movimiento al guardar el mundo. Un proyectil que golpea un bloque o un ser vivo pasa a juguete; tras 20 segundos sin impacto también pasa a juguete y cae.

La física se calcula en el servidor y el cliente interpola las posiciones recibidas. Cada pelota tiene gravedad, resistencia del aire, fricción del suelo y coeficientes de rebote propios. Plástico se frena antes; fútbol rueda más; básquet rebota alto y repetidamente; vóley hace arcos largos. El impulso de juguete es ahora mayor (valores en ITEM_SPECS). Los rebotes pierden energía y se detienen bajo un umbral para evitar vibración permanente.

El proyectil se mueve por pasos de hasta 0.18 bloques usando su caja de colisión completa: no atraviesa paredes ni entidades por el aumento de velocidad. Al tocar un bloque pasa a juguete con rebote; al golpear un ser vivo hace daño y retrocede con parte de su impulso. No vuelve a aplicar daño de proyectil al rebotar. Conserva su movimiento al agotar los 20 segundos de vuelo.

El daño crece desde un valor cercano considerable hasta el doble después de recorrer 32 bloques. La distancia recorrida se acumula únicamente durante el modo proyectil y se guarda en `TravelledDistance`; un nuevo lanzamiento reinicia el cálculo. No es un NBT de apariencia ni de NPC. Los valores anteriores siguen siendo cargables, con distancia inicial 0 si no existía ese campo.

El vóley juguete limita sus choques dañinos a uno por segundo y evita golpear a quien lo acaba de impulsar durante 8 ticks. Otros juguetes no causan daño por contacto. El clic izquierdo en el aire devuelve el vóley siguiendo la dirección de la mirada con un pequeño impulso ascendente. Las pelotas giran según su desplazamiento horizontal y radio, con orientación en el mundo, sin mirar siempre a la cámara. El giro se sincroniza y guarda en `Roll` y `Heading`.

## Recursos

`python tools/create_ball_assets.py` regenera los cuatro atlas pixelados de 16×16, las esferas de cubos escalonados y los archivos de definición de ítem. No necesita ejecutar Minecraft.

## Validación

Revisión estática de JSON, referencias de recursos, registro de las cuatro entidades/renderers y firmas de API en las fuentes locales de Minecraft 26.2. No se ha compilado ni ejecutado Minecraft.

Al probar en el juego: comprobar daño cercano y creciente hasta 32 bloques en cada dificultad; velocidad inicial y arcos de vuelo (20 ticks = 1 segundo); colocación junto a paredes; recogida con inventario lleno; rebotes y redirección del vóley; sincronización en multijugador; guardar/cargar durante el movimiento. Verificar también modelos/texturas en inventario, mano y mundo.
