# Paredes de dos texturas retiradas

2026-10-12: por petición del usuario se retiran las quince variantes con caras
combinadas: light_dark_school_wall_1..5, light_street_school_wall_1..5 y
dark_street_school_wall_1..5. Se eliminan sus registros de bloques/ítems,
modelos, estados, referencias de ítem, drops, traducciones y etiquetas.

MixedSchoolWallBlock, create_mixed_school_walls.py y check_mixed_school_walls.py
se eliminan porque pertenecían exclusivamente a esta familia. La eliminación
reduce el inventario en 15 bloques y 240 estados; no se conservan alias de esos IDs.
Las paredes básicas light_school_wall, dark_school_wall y street_school_wall
mantienen sus recursos y texturas compartidas.

Verificación estática de registros, referencias y recursos; sin compilar ni
probar Minecraft, conforme a la instrucción vigente.
