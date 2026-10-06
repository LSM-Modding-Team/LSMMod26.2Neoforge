# Espacios del colegio de world (3).zip

38 espacios revisados por paredes, pisos, puertas y mobiliario. Coordenadas originales; no es generación de estructuras.

32 nombres confirmados por el usuario sustituyen los IDs provisionales; se conservan además arte, auditorio, enfermería, hall, kiosko y patio. Los otros 55 espacios provisionales están retirados. Los IDs anteriores sustituidos no son alias de comandos. La tabla interna school_spaces_migration.json solo permite migrar los nombres de mundos anteriores.

Para editar: /lsmmod define <id> <x1> <y1> <z1> <x2> <y2> <z2> crea o reemplaza la delimitación; ambas esquinas son bloques inclusivos y pueden darse en cualquier orden. /lsmmod addbox añade un sector y /lsmmod delete elimina el espacio. /lsmmod rename cambia también el ID. /lsmmod export copia o guarda el catálogo activo completo y sus eliminaciones. Los cambios son persistentes por mundo.

Los extremos máximos de las cajas del JSON son exclusivos. Cajas múltiples representan sectores del mismo espacio; la visualización muestra cada sector. El mínimo vertical admite la media celda inferior para pisos de losas, respetando Y=134. Las divisiones de FramedBlocks pueden ocupar solo parte de una celda: los límites son aproximaciones a la cuadrícula de bloques, no mediciones de sus superficies.

| ID para comandos | Descripción | Cajas interiores (mínimo → máximo exclusivo) | Evidencia del mobiliario |
|---|---|---|---|
| comedor | comedor | [-86, 135.5, -10] → [-81, 139, 1] | dining_chair: 12, dining_table: 6 |
| 3roprim | 3roprim | [-80, 135.5, -10] → [-75, 139, 0] | high_school_chair: 12, students_desk: 12, teachers_desk: 2, teachers_chair: 1 |
| hall | Hall | [-74, 135.5, -10] → [-69, 139, 2] | hallchair: 10, manuel_tirado_bust: 3 |
| oficina_betsy | oficina_betsy | [-68, 135.5, -10] → [-64, 139, -3] | teachers_desk: 4, teachers_chair: 1, laptop: 1 |
| bano_direccion | bano_direccion | [-63, 135.5, -10] → [-62, 138, -6] |  |
| direccion | direccion | [-61, 135.5, -10] → [-57, 139, -5] | hallchair: 6, teachers_desk: 4, teachers_chair: 1, laptop: 1 |
| enfermeria | Enfermería | [-64, 135.5, -2] → [-62, 139, 1] | infirmary_cot: 2 |
| limpieza_vicky | limpieza_vicky | [-86, 134, 3] → [-84, 136, 7] |  |
| kiosko | Kiosko | [-79, 134.5, 4] → [-75, 138, 8] | dining_chair: 1, kiosk_table: 2 |
| psicologia3 | psicologia3 | [-86, 139.5, -10] → [-85, 143, -5] | locker: 3, high_school_chair: 1 |
| psicologia2 | psicologia2 | [-84, 139.5, -10] → [-82, 143, -5] | high_school_chair: 2, students_desk: 2 |
| psicologia | psicologia | [-86, 139.5, -4] → [-82, 143, 2] | high_school_chair: 4, students_desk: 4, locker: 2 |
| computacion | computacion | [-81, 139.5, -10] → [-75, 143, 1] | computer_desk: 48, locker: 12 |
| subdireccion2 | subdireccion2 | [-74, 139.5, -10] → [-69, 143, -7] | teachers_desk: 2, teachers_chair: 1, laptop: 1 |
| subdireccion1 | subdireccion1 | [-74, 139.5, -6] → [-69, 143, -3] | students_desk: 2, high_school_chair: 1, laptop: 1 |
| 5toprim | 5toprim | [-68, 139.5, -10] → [-62, 143, 1] | high_school_chair: 14, students_desk: 14, teachers_desk: 2, teachers_chair: 1, pc: 1, classroom_door: 4, locker: 6 |
| 4toprim | 4toprim | [-61, 139.5, -10] → [-57, 143, 2] | students_desk: 12, high_school_chair: 9, teachers_desk: 2, teachers_chair: 1, pc: 1 |
| arte | Salón de arte | [-86, 143.5, -10] → [-82, 147, 2] | stool: 10, art_table: 16 |
| laboratorio | laboratorio | [-81, 143.5, -10] → [-75, 147, 1] | stool: 18, locker: 6, classroom_door: 4 |
| aula_interactiva | aula_interactiva | [-68, 143.5, -10] → [-62, 147, -1] | gray_computer_desk: 32, englishroomchair: 8 |
| aula_ingles | aula_ingles | [-61, 143.5, -10] → [-57, 147, 1] | gray_computer_desk: 40, englishroomchair: 9 |
| auditorio | Auditorio (uso probable) | [-80, 147.5, -10] → [-69, 152, 2]; [-86, 147.5, -10] → [-81, 152, 2] | vaulting_box: 4, plasticchair_red_arms: 31, students_desk: 6, plasticchair_red: 9, plasticchair_white_arms: 12, laptop: 1, speaker: 1 |
| auditorio_atras | auditorio_atras | [-68, 147.5, -10] → [-57, 152, -4] | students_desk: 22, speaker: 1 |
| 2doprim | 2doprim | [-83, 135.5, 41] → [-75, 140, 45]; [-79, 135.5, 40] → [-75, 140, 41]; [-79, 135.5, 45] → [-75, 140, 46]; [-83, 135.5, 40] → [-80, 140, 41] | elementary_chair: 8, elementary_desk: 8, teachers_chair: 1, teachers_desk: 2, pc: 1 |
| 1roprim | 1roprim | [-68, 135.5, 38] → [-60, 140, 46] | elementary_desk: 8, elementary_chair: 8, teachers_desk: 2, teachers_chair: 1, pc: 1 |
| bano_mujeres | bano_mujeres | [-68, 135.5, 33] → [-66, 139, 37] |  |
| bano_hombres | bano_hombres | [-65, 135.5, 36] → [-57, 138, 37]; [-65, 135.5, 33] → [-63, 139, 34] |  |
| 2dosec | 2dosec | [-86, 140.5, 39] → [-77, 145, 46] | high_school_chair: 8, students_desk: 8, locker: 5, classroom_door: 4, teachers_desk: 2, teachers_chair: 1, pc: 1 |
| 1rosec | 1rosec | [-72, 140.5, 40] → [-66, 145, 46] | students_desk: 6, high_school_chair: 6, teachers_desk: 2, teachers_chair: 1, locker: 3, pc: 1 |
| 6toprim | 6toprim | [-65, 140.5, 40] → [-57, 145, 46] | teachers_desk: 2, pc: 1, students_desk: 10, teachers_chair: 1, locker: 5 |
| 5tosec | 5tosec | [-86, 145.5, 39] → [-77, 150, 46] | high_school_chair: 12, students_desk: 12, locker: 5, classroom_door: 4, teachers_desk: 2, teachers_chair: 1, pc: 1 |
| 4tosec | 4tosec | [-72, 145.5, 40] → [-66, 150, 46] | students_desk: 6, high_school_chair: 6, teachers_desk: 2, teachers_chair: 1, locker: 3, pc: 1 |
| 3rosec | 3rosec | [-65, 145.5, 40] → [-57, 150, 46] | teachers_desk: 2, pc: 1, students_desk: 8, high_school_chair: 8, teachers_chair: 1, locker: 5 |
| oficina_cynthia | oficina_cynthia | [-76, 145.5, 42] → [-73, 150, 45]; [-76, 145.5, 40] → [-75, 150, 41] | teachers_desk: 2, teachers_chair: 1, laptop: 1 |
| patio | Patio central | [-86, 135.5, 8] → [-57, 140, 32] |  |
| escaleras_este_strauss | escaleras_este_strauss | [-65, 137.5, 32] → [-57, 150, 38] | teachers_desk: 2, laptop: 1 |
| patio_2doprim | patio_2doprim | [-86, 135.5, 47] → [-74, 140, 53] |  |
| patio_1roprim | patio_1roprim | [-69, 135.5, 47] → [-57, 140, 53] |  |
