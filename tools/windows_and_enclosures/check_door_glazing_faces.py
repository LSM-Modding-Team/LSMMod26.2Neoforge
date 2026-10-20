"""Static regression for glass caps, partial aprons, inherited doors and OBJ slots."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_glob, module_is_file, module_read_text
import copy,json,tempfile
from pathlib import Path
from windows_and_enclosures.repair_door_glazing_faces import repair,eligible,covered,GLASS,NORMALS
from optimize_model_resources import ROOT,prepare
A=ROOT/'src/main/resources/assets/lsmmod'
def read(path):return json.loads(module_read_text(path, encoding='utf-8-sig'))
def faces(elements):return sum(len(e['faces']) for e in elements)
def main():
 checked=removed=0
 for path in module_glob(A/'models/block', '*.json'):
  if not eligible(path):continue
  before=read(path);after=repair(before);assert repair(after)==after
  original=before.get('elements',[]);remaining=after.get('elements',[])
  opaque=lambda es:[e for e in es if all(f['texture'] not in GLASS for f in e['faces'].values())]
  assert opaque(original)==opaque(remaining),(path,'opaque wood/frame changed')
  assert {k:v for k,v in before.items() if k!='elements'}=={k:v for k,v in after.items() if k!='elements'}
  boxes=[tuple(e['from']+e['to']) for e in opaque(original)]
  for element in remaining:
   assert any(e['from']==element['from'] and e['to']==element['to'] for e in original)
   for face,material in element['faces'].items():
    if material['texture'] not in GLASS:continue
    assert not covered(element,face,boxes),(path,face,'cap still buried in frame')
    axis,positive=NORMALS[face];plane=element['to' if positive else 'from'][axis]
    assert not (axis==0 and plane==(16 if positive else 0)),(path,'internal row seam')
    if plane==(16 if positive else 0):assert material['cullface']==face
  removed+=faces(original)-faces(remaining);checked+=1
 # A four-pixel wood apron cannot hide the entire eight-pixel glass underside.
 pane={'from':[1,8,0],'to':[15,16,8],'faces':{'down':{'texture':'#glass','uv':[0,0,16,8]}}}
 apron={'from':[0,0,0],'to':[16,8,4],'faces':{'north':{'texture':'#top','uv':[0,0,16,8]}}}
 assert repair({'elements':[pane,apron]})['elements'][0]==pane
 # Closed/internal caps are removed without moving the pane or changing its front UV.
 frame=copy.deepcopy(apron);frame['to'][2]=8
 assert repair({'elements':[pane,frame]})['elements']==[frame]
 with tempfile.TemporaryDirectory() as t:
  output=Path(t)/'resources';report,aliases=prepare(output);B=output/'assets/lsmmod'
  assert report['door_glazing_faces']['glass_faces_removed']==removed and removed>0
  # Actual NeoForge resolveSlot -> Fusion getMaterial uses the MTL token verbatim.
  slots=[line.split()[1] for line in module_read_text(B/'models/block/school_entrance_pavement_shapes.mtl').splitlines() if line.startswith('map_Kd ')]
  assert slots==['gray','yellow']
  for ident in ['school_entrance_pavement_slab',*[f'school_entrance_pavement_slope_{i}' for i in range(1,7)]]:
   for apply in read(B/f'blockstates/{ident}.json')['variants'].values():
    textures=read(B/f'models/{apply["model"].split(":")[1]}.json')['textures']
    for slot in slots:
     assert slot in textures
     assert module_is_file(B/f'textures/{textures[slot].split(":")[1]}.png')
  # Every inherited tall-door variant still reaches the repaired native parent.
  for ident in ('tall_bathroom_door','tall_teachers_office_door','tall_dining_door'):
   roots=[read(p) for p in module_glob(B/'models/block/school_door_dynamic', ident+'_*.json')]
   assert roots
   for root in roots:
    for entry in root['models'][0]:
     path=B/f'models/{entry["model"].split(":")[1]}.json';leaf=read(path)
     assert leaf.get('parent','').startswith('lsmmod:block/tall_classroom_')
 print(f'OK: {checked} native door models inspected, {removed} glass caps removed; wood/frame/UV intact, partial aprons retained, boundary culling and inherited variants repaired; all seven pavement OBJ families resolve their MTL slots.')
if __name__=='__main__':main()
