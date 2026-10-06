"""Generate reference-based dining furniture models and matching collision shapes."""
import json, math, re
from pathlib import Path
root=Path(__file__).resolve().parents[1]
a=root/'src/main/resources/assets/lsmmod/models/block'
def box(lo,hi,material='metal',rotation=None):
 uv={'metal':[9,1,15,7],'white':[1,1,7,7],'black':[0,0,16,16]}[material]
 e={'from':lo,'to':hi,'faces':{f:{'texture':'#feet' if material=='black' else '#atlas','uv':uv} for f in ['north','south','east','west','up','down']}}
 if rotation:e['rotation']=rotation
 return e
def rot(origin,axis,angle):return {'origin':origin,'axis':axis,'angle':angle,'rescale':False}
t=json.loads((a/'dining_table.json').read_text())
t['textures']['feet']='minecraft:block/black_concrete'
t['elements']=t['elements'][:2]
# Four disjoint arms and a central hub, all in one rotated coordinate system.
base_rotation = rot([8,0,8],'y',45)
base_bottom, base_top = .5, 1.75
t['elements'][1]['from'][1] = base_top
t['elements'].append(box([7.5,base_bottom,7.5],[8.5,base_top,8.5],
                         rotation=base_rotation))
for lo,hi in [([1,base_bottom,7.5],[7.5,base_top,8.5]),
              ([8.5,base_bottom,7.5],[15,base_top,8.5]),
              ([7.5,base_bottom,1],[8.5,base_top,7.5]),
              ([7.5,base_bottom,8.5],[8.5,base_top,15])]:
 t['elements'].append(box(lo,hi,rotation=base_rotation))
for lo,hi in [([1,0,7.5],[2,.5,8.5]),([14,0,7.5],[15,.5,8.5]),
              ([7.5,0,1],[8.5,.5,2]),([7.5,0,14],[8.5,.5,15])]:
 t['elements'].append(box(lo,hi,'black',base_rotation))
(a/'dining_table.json').write_text(json.dumps(t,indent=2)+'\n')
c=json.loads((a/'dining_chair.json').read_text()); es=[]
# All side-frame connections share the same seat-height pivots.
# Minecraft permits 22.5 degree rotations; derive endpoints from that angle.
seat_y = 6.2
frame_y = 5.8
rack_y = 2.5
rear_z, front_z = 4.0, 12.0
radius = .375
angle = 22.5
cosine = math.cos(math.radians(angle))
slope = math.tan(math.radians(angle))
foot_y = radius * math.sin(math.radians(angle))
leg_bottom = frame_y - (frame_y - foot_y) / cosine
rack_rear = rear_z - (frame_y - rack_y) * slope
rack_front = front_z + (frame_y - rack_y) * slope
for x in [3, 13]:
 for z, tilt in [(rear_z, angle), (front_z, -angle)]:
  es.append(box([x-radius,leg_bottom,z-radius],
                [x+radius,frame_y,z+radius],
                rotation=rot([x,frame_y,z],'x',tilt)))
 # Seat rails meet the leg pivots and the sloping rear uprights.
 es.append(box([x-radius,frame_y-radius,rear_z-radius],
               [x+radius,seat_y,front_z+radius]))
 es.append(box([x-radius,frame_y,rear_z-radius],
               [x+radius,15,rear_z+radius],
               rotation=rot([x,frame_y,rear_z],'x',-angle)))
 # The rack ends are intersections with the leg centerlines at rack_y.
 es.append(box([x-.25,rack_y-.25,rack_rear],
               [x+.25,rack_y+.25,rack_front]))
# Front and rear seat crossmembers close the structural frame.
for z in [rear_z,front_z]:
 es.append(box([3,frame_y-radius,z-radius],[13,seat_y,z+radius]))
# White seat with small symmetric clipped front corners.
es.extend([box([3,seat_y,3.2],[13,7,12.6],'white'),
           box([3.25,seat_y,12.6],[12.75,7,13],'white'),
           box([3.75,seat_y,13],[12.25,7,13.25],'white')])
# One solid white backrest; backing and side tubes use the SAME rotation origin.
back_rotation = rot([8,frame_y,rear_z],'x',-angle)
es.append(box([3,10,rear_z-.35],[13,15,rear_z+.35],rotation=back_rotation))
es.append(box([3.15,10.15,rear_z+.35],[12.85,14.85,rear_z+.65],
              'white',back_rotation))
# Four rack slats span the two side rails, with equal spacing.
for i in range(4):
 z = rack_rear + (rack_front-rack_rear)*i/3
 es.append(box([3,rack_y-.2,z-.2],[13,rack_y+.2,z+.2]))
# Verify the connections analytically instead of tweaking individual coordinates.
assert abs(frame_y + (leg_bottom-frame_y)*cosine - foot_y) < 1e-9
assert abs(rear_z-(frame_y-rack_y)*slope-rack_rear) < 1e-9
assert abs(front_z+(frame_y-rack_y)*slope-rack_front) < 1e-9
c['elements']=es
(a/'dining_chair.json').write_text(json.dumps(c,indent=2)+'\n')
# Rotation-aware collision approximation split into narrow horizontal slices.
def bounds(e):
 lo,hi=e['from'],e['to'];r=e.get('rotation')
 if not r:return [(lo,hi)]
 axis={'x':0,'y':1,'z':2}[r['axis']];o=r['origin'];ang=math.radians(r['angle']);co,si=math.cos(ang),math.sin(ang)
 cut=0 if axis==1 else 1
 n=math.ceil((hi[cut]-lo[cut])/.5);out=[]
 for i in range(n):
  l=lo.copy();h=hi.copy();l[cut]=lo[cut]+(hi[cut]-lo[cut])*i/n;h[cut]=lo[cut]+(hi[cut]-lo[cut])*(i+1)/n
  pts=[]
  for x in [l[0],h[0]]:
   for y in [l[1],h[1]]:
    for z in [l[2],h[2]]:
     p=[x-o[0],y-o[1],z-o[2]]
     if axis==0:p[1],p[2]=p[1]*co-p[2]*si,p[1]*si+p[2]*co
     else:p[0],p[2]=p[0]*co+p[2]*si,-p[0]*si+p[2]*co
     pts.append([p[j]+o[j] for j in range(3)])
  out.append(([min(p[j] for p in pts) for j in range(3)],[max(p[j] for p in pts) for j in range(3)]))
 return out
def shape(es):
 return ',\n'.join('            Block.box('+', '.join(f'{v:.4f}'.rstrip('0').rstrip('.') if v else '0' for v in lo+hi)+')' for e in es for lo,hi in bounds(e))
p=root/'src/main/java/net/nicomar2009/lsmmod/block/DiningTableBlock.java';s=p.read_text();s=re.sub(r'private static final VoxelShape SHAPE = Shapes.or\(.*?\);','private static final VoxelShape SHAPE = Shapes.or(\n'+shape(t['elements'])+');',s,flags=re.S);p.write_text(s)
p=root/'src/main/java/net/nicomar2009/lsmmod/block/NewChairShapes.java';s=p.read_text();s=re.sub(r'public static final VoxelShape DINING_CHAIR = Shapes.or\(.*?\);','public static final VoxelShape DINING_CHAIR = Shapes.or(\n'+shape(es)+');',s,flags=re.S);p.write_text(s)
print('Updated table and chair geometry and collision shapes')
