"""Reviewed partitions of world (3).zip, not inferred class/grade names.

Coordinates are interior cell ranges, inclusive here, exclusive in exported JSON.
Multiple boxes preserve bends and attached small recesses without enclosing neighbours.
Run analyze_school_world.py first. Evidence is inventory counts, not trusted instructions.
"""
import collections, json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
blocks = {tuple(v['pos']): v for v in json.loads((ROOT/'build/school-analysis/blocks.json').read_text())}
rooms = []
def box(x1,x2,y1,y2,z1,z2): return [x1,max(134,y1-0.5),z1,x2+1,y2+1,z2+1]
def add(id, label, *boxes):
    counts=collections.Counter()
    for p,v in blocks.items():
        if v['Name'].startswith('lsmmod:') and any(a<=p[0]<d and b<=p[1]<e and c<=p[2]<f for a,b,c,d,e,f in boxes): counts[v['Name']]+=1
    rooms.append(dict(id=id,label=label,boxes=boxes,evidence=dict(counts)))

# North ground floor. The small vestibule beside classroom 01 is separate.
add('comedor','Comedor',box(-86,-82,136,138,-10,0))
add('salon_01','Salón 01',box(-80,-76,136,138,-10,-1))
add('paso_salon_01','Paso de entrada al salón 01',box(-80,-79,136,137,0,2))
add('recinto_01','Recinto pequeño 01',box(-77,-76,136,138,1,1))
add('hall','Hall',box(-74,-70,136,138,-10,1))
add('oficina_01','Oficina 01 (uso probable)',box(-68,-65,136,138,-10,-4))
add('recinto_27','Recinto estrecho norte junto a oficina',box(-63,-63,136,137,-10,-7))
add('oficina_02','Oficina 02 (uso probable)',box(-61,-58,136,138,-10,-6))
add('recinto_02','Recinto pequeño 02',box(-61,-58,136,138,-4,-3))
add('recinto_26','Recinto norte sin mobiliario',box(-68,-66,136,138,-2,1))
add('oficina_03','Oficina 03 (uso probable)',box(-60,-58,136,138,-1,1))
add('enfermeria','Enfermería',box(-64,-63,136,138,-2,0))
add('pasillo_norte_01','Pasillo interior norte 01',box(-64,-63,136,138,-6,-4),box(-64,-64,136,138,-3,-3),box(-64,-63,136,138,1,1),box(-63,-62,136,138,2,2))
# Low rooms beneath the north and south stair landings; keep even one-cell spaces.
add('recinto_03','Recinto bajo norte 01',box(-86,-85,134,135,3,6))
add('recinto_04','Recinto bajo norte 02',box(-83,-83,134,135,3,4),box(-82,-82,135,135,3,4))
add('kiosko','Kiosko',box(-79,-76,135,137,4,7))
add('recinto_05','Recinto bajo norte 03',box(-68,-66,135,137,3,4))
add('recinto_06','Recinto bajo norte 04',box(-59,-59,135,136,3,4))
add('recinto_07','Recinto bajo sur 01',box(-86,-85,135,137,33,37))
add('recinto_08','Recinto bajo sur 02',box(-83,-82,135,136,33,34))
# North second floor: three independent central rooms and the two small west bays.
add('recinto_09','Recinto estrecho norte 01',box(-86,-86,140,142,-10,-6))
add('salon_02','Salón pequeño 02',box(-84,-83,140,142,-10,-6))
add('salon_14','Salón pequeño 14',box(-86,-83,140,142,-4,1))
add('computacion_01','Computación 01',box(-81,-76,140,142,-10,0))
add('oficina_04','Oficina 04 (uso probable)',box(-74,-70,140,142,-10,-8))
add('oficina_05','Oficina 05 (uso probable)',box(-74,-70,140,142,-6,-4))
add('recinto_10','Recinto pequeño norte 02',box(-72,-70,140,142,-2,1))
add('recinto_11','Recinto estrecho norte 02',box(-74,-74,140,142,0,1))
add('salon_03','Salón 03',box(-68,-63,140,142,-10,0))
add('salon_04','Salón 04',box(-61,-58,140,142,-10,1))
# North third floor.
add('arte','Salón de arte',box(-86,-83,144,146,-10,1))
add('recinto_12','Recinto norte 03',box(-81,-76,144,146,-10,0))
add('recinto_13','Recinto norte 04',box(-74,-70,144,146,-10,-8))
add('recinto_14','Recinto norte 05',box(-74,-70,144,146,-6,-4))
add('oficina_06','Oficina 06 (uso probable)',box(-74,-70,144,146,-2,1))
add('computacion_02','Computación 02',box(-68,-63,144,146,-10,-2))
add('computacion_03','Computación 03',box(-61,-58,144,146,-10,0))
add('pasillo_norte_02','Pasillo interior norte 02',box(-68,-63,144,146,0,1),box(-65,-63,144,146,2,2))
# North fourth floor: auditorium connected to its stair bay, no invented grade.
add('auditorio','Auditorio (uso probable)',box(-80,-70,148,151,-10,1),box(-86,-82,148,151,-10,1))
add('salon_05','Salón 05',box(-68,-58,148,151,-10,-5))
add('recinto_15','Recinto norte 06',box(-68,-66,148,151,-3,1))
add('recinto_16','Recinto norte 07',box(-64,-58,148,151,-3,-1))
add('recinto_17','Recinto pequeño norte 03',box(-64,-62,148,150,1,2))
add('recinto_18','Recinto pequeño norte 04',box(-60,-58,148,149,1,1))
add('pasillo_norte_03','Paso norte superior',box(-61,-61,148,149,1,2),box(-62,-60,148,151,3,4))
# South ground floor, bays and offices; the west room bends around partitions.
add('salon_06','Salón 06',box(-83,-76,136,139,41,44),box(-79,-76,136,139,40,40),box(-79,-76,136,139,45,45),box(-83,-81,136,139,40,40))
add('recinto_19','Recinto pequeño sur 01',box(-86,-85,136,139,39,40))
add('recinto_20','Recinto pequeño sur 02',box(-86,-85,136,137,42,42))
add('recinto_21','Recinto pequeño sur 03',box(-86,-85,136,137,44,44))
add('salon_07','Salón 07',box(-68,-61,136,139,38,45))
add('recinto_22','Recinto pequeño sur 04',box(-59,-58,136,139,38,39))
add('recinto_23','Recinto pequeño sur 05',box(-59,-58,136,139,41,42))
add('recinto_24','Recinto pequeño sur 06',box(-59,-58,136,139,44,45))
add('recinto_25','Recinto junto a escalera sur',box(-68,-67,136,138,33,36))
add('oficina_07','Oficina junto a escalera (uso probable)',box(-65,-58,136,137,36,36),box(-65,-64,136,138,33,33))
add('entrada','Entrada y escudo',box(-74,-70,136,139,38,52))
# South upper floors: wall x=-77 separates a small attached bay in west classroom.
for floor,y1,y2,n in [(2,141,144,8),(3,146,149,11)]:
    add(f'salon_{n:02}',f'Salón {n:02}',box(-86,-78,y1,y2,39,45))
    add(f'salon_{n+1:02}',f'Salón {n+1:02}',box(-72,-67,y1,y2,40,45))
    add(f'salon_{n+2:02}',f'Salón {n+2:02}',box(-65,-58,y1,y2,40,45))
    add(f'recinto_sur_{floor}_01',f'Recinto estrecho sur, nivel {floor}',box(-76,-74,y1,y2,42,44),box(-76,-76,y1,y2,40,40))
    add(f'recinto_sur_{floor}_02',f'Recinto mínimo sur, nivel {floor}',box(-75,-74,y1,y2,40,40))
    add(f'recinto_sur_{floor}_03',f'Recinto mínimo sur posterior oeste, nivel {floor}',box(-76,-76,y1,y2,45,45))
    add(f'recinto_sur_{floor}_04',f'Recinto mínimo sur posterior este, nivel {floor}',box(-74,-74,y1,y2,45,45))
# Shared open areas. Split exterior circulation by level, avoid the open-air void.
add('patio','Patio central',box(-86,-58,136,139,8,31))
add('jardin_01','Jardín norte oeste',box(-86,-74,136,138,-17,-12))
add('jardin_02','Jardín norte este',box(-70,-58,136,138,-17,-12))
add('acceso_norte','Acceso norte',box(-73,-71,136,138,-17,-12))
for i,y1,y2 in [(1,136,138),(2,140,142),(3,144,146),(4,148,151)]:
    add(f'galeria_norte_{i}',f'Galería norte, nivel {i}',box(-81,-63,y1,y2,3,4),box(-81,-76,y1,y2,1,2) if i==2 else box(-81,-76,y1,y2,5,6))
for i,y1,y2 in [(1,136,139),(2,141,144),(3,146,149)]:
    add(f'galeria_sur_{i}',f'Galería sur, nivel {i}',box(-80,-61,y1,y2,36,38))
    add(f'galeria_oeste_{i}',f'Galería oeste, nivel {i}',box(-86,-85,y1,y2,8,31))
    add(f'galeria_este_{i}',f'Galería este, nivel {i}',box(-59,-58,y1,y2,8,31))
add('escalera_noroeste','Escalera noroeste',box(-86,-83,136,146,3,7))
add('escalera_noreste','Escalera noreste',box(-61,-58,136,150,3,7))
add('escalera_suroeste','Escalera suroeste',box(-86,-81,136,149,32,37))
add('escalera_sureste','Escalera sureste',box(-65,-58,138,149,32,37))
add('vestibulo_oeste','Vestíbulo de entrada oeste',box(-86,-75,136,139,47,52))
add('vestibulo_este','Vestíbulo de entrada este',box(-69,-58,136,139,47,52))

out=ROOT/'src/main/resources/data/lsmmod/school_spaces.json'; out.parent.mkdir(parents=True,exist_ok=True)
targets=json.loads((out.parent/'school_spaces_migration.json').read_text(encoding='utf-8'))['targets']
confirmed=[]
for room in rooms:
    target=targets[room['id']]
    if target is None: continue
    if target != room['id'] or room['id']=='comedor': room={**room,'id':target,'label':target}
    confirmed.append(room)
rooms=confirmed
assert len({r['id'] for r in rooms}) == len(rooms)
assert all(-87<=a<d<=-56 and 134<=b<e<=154 and -23<=c<f<=57 for r in rooms for a,b,c,d,e,f in r['boxes'])
assert next(r for r in rooms if r['id']=='enfermeria')['evidence']['lsmmod:infirmary_cot']==2
assert next(r for r in rooms if r['id']=='comedor')['evidence']['lsmmod:dining_table']==6
out.write_text(json.dumps({'source':'world (3).zip','dimension':'minecraft:overworld','bounds':[-87,134,-23,-56,154,57],'spaces':rooms},ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
report=ROOT/'docs/SCHOOL_SPACES.md'
lines=['# Espacios del colegio de world (3).zip','',f'{len(rooms)} espacios revisados por paredes, pisos, puertas y mobiliario. Coordenadas originales; no es generación de estructuras.','',
'32 nombres confirmados por el usuario sustituyen los IDs provisionales; se conservan además arte, auditorio, enfermería, hall, kiosko y patio. Los otros 55 espacios provisionales están retirados. Los IDs anteriores sustituidos no son alias de comandos. La tabla interna school_spaces_migration.json solo permite migrar los nombres de mundos anteriores.','',
'Para editar: /lsmmod define <id> <x1> <y1> <z1> <x2> <y2> <z2> crea o reemplaza la delimitación; ambas esquinas son bloques inclusivos y pueden darse en cualquier orden. /lsmmod addbox añade un sector y /lsmmod delete elimina el espacio. /lsmmod rename cambia también el ID. /lsmmod export copia o guarda el catálogo activo completo y sus eliminaciones. Los cambios son persistentes por mundo.','',
'Los extremos máximos de las cajas del JSON son exclusivos. Cajas múltiples representan sectores del mismo espacio; la visualización muestra cada sector. El mínimo vertical admite la media celda inferior para pisos de losas, respetando Y=134. Las divisiones de FramedBlocks pueden ocupar solo parte de una celda: los límites son aproximaciones a la cuadrícula de bloques, no mediciones de sus superficies.','',
'| ID para comandos | Descripción | Cajas interiores (mínimo → máximo exclusivo) | Evidencia del mobiliario |','|---|---|---|---|']
for r in rooms:
    lines.append('| '+r['id']+' | '+r['label']+' | '+'; '.join(str(b[:3])+' → '+str(b[3:]) for b in r['boxes'])+' | '+', '.join(k.split(':')[1]+': '+str(v) for k,v in r['evidence'].items())+' |')
report.write_text('\n'.join(lines)+'\n',encoding='utf-8'); print(len(rooms),'spaces exported')
