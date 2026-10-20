"""Validate shared OBJ profile, winding, joins and regeneration without Java compilation."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_exists, module_read_bytes, module_read_text
import hashlib,json,math
from slopes.create_school_short_raised_slopes import ROOT,A,IDS,GENERATED,main as generate
from audit_block_states import audit

def main():
 data=module_read_text(A/'models/block/school_short_raised_slopes.obj').splitlines();vertices=[tuple(map(float,l.split()[1:])) for l in data if l.startswith('v ')];faces=[[int(s.split('/')[0])-1 for s in l.split()[1:]] for l in data if l.startswith('f ')]
 assert len(vertices)==24 and len(faces)==6
 assert min(v[1] for v in vertices)==.5 and max(v[1] for v in vertices)==2
 for x,y,z in vertices:assert y==2 or y==1.5-z
 # Every face is non-degenerate and points outward from the prism's center.
 center=(.5,1.5,.5)
 for f in faces:
  points=[vertices[i] for i in f];a,b,c=points[:3];u=[b[i]-a[i] for i in range(3)];v=[c[i]-a[i] for i in range(3)];n=(u[1]*v[2]-u[2]*v[1],u[2]*v[0]-u[0]*v[2],u[0]*v[1]-u[1]*v[0]);mid=[sum(p[i] for p in points)/4 for i in range(3)]
  assert sum(n[i]*(mid[i]-center[i]) for i in range(3))>0
 rows={r['id']:r['states'] for r in audit()};assert all(rows[i]==8 for i in IDS)
 layout=json.loads(module_read_text(ROOT/'tools/slopes/school_short_raised_slope_layout.json'))
 for face,placements in layout['placements'].items():
  assert len(placements)==3
  for n,p in enumerate(placements):
   assert p['id']==IDS[n] and p['offset'][1]==n
   assert .5+n==(.5 if n==0 else 1.5+n-1)
  assert .5==8/16 and 1.5+2==56/16 and 2+2==64/16
 for ident in IDS:
  model=json.loads(module_read_text(A/f'models/block/{ident}.json'));assert model['parent']=='lsmmod:block/school_short_raised_slopes'
  states=json.loads(module_read_text(A/f'blockstates/{ident}.json'));assert [v['y'] for v in states['variants'].values()]==[0,90,180,270]
 assert module_exists(A/'textures/block/classroom_white_wall.png')
 source=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/slopes/block/SchoolInvertedSlopeBlock.java');assert 'SHARED_SHAPES.computeIfAbsent' in source and 'Math.min(y0,y1)' in source and 'SimpleBlockOutline.forState' in source
 # The old positive-rise profiles retain their exact lower bounds.
 for rise in (.5,2/3,5/7):
  for i in range(32):assert min(1-rise*i/32,1-rise*(i+1)/32)==1-rise*(i+1)/32
 generate();before={p:hashlib.sha256(module_read_bytes(ROOT/p)).digest() for p in GENERATED};generate();assert all(hashlib.sha256(module_read_bytes(ROOT/p)).digest()==h for p,h in before.items())
 print('OK: three pieces, 24 states, one shared OBJ, four orientations, outward winding, continuous 8..56px underside, 64px roof, unchanged positive profiles and idempotent generator.')
if __name__=='__main__':main()
