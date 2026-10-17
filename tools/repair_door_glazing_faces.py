"""Prepared-resource repair of hidden glass caps; source models remain editable."""
from resource_modules import module_glob, module_read_text, module_write_text
import copy,json
from pathlib import Path
PREFIXES=('tall_classroom_', 'double_school_entrance_')
GLASS={'#glass','#glass_edge','#transom'}
NORMALS={'west':(0,False),'east':(0,True),'down':(1,False),
         'up':(1,True),'north':(2,False),'south':(2,True)}
def eligible(path):
 return path.parent.name=='block' and path.stem.startswith(PREFIXES)
def covered(element,face,boxes):
 axis,positive=NORMALS[face];plane=element['to' if positive else 'from'][axis]
 # Only a completely covered face is removed. Partial wood aprons must retain
 # the glass visible beyond their four-pixel thickness.
 return any((box[axis]<=plane<box[axis+3] if positive else box[axis]<plane<=box[axis+3])
            and all(box[a]<=element['from'][a] and box[a+3]>=element['to'][a]
                    for a in range(3) if a!=axis) for box in boxes)
def repair(data):
 result=copy.deepcopy(data);elements=result.get('elements',[])
 boxes={tuple(e['from']+e['to']) for e in elements
        if e['faces'] and all(f['texture'] not in GLASS for f in e['faces'].values())}
 kept=[]
 for element in elements:
  for face,material in list(element['faces'].items()):
   if material['texture'] not in GLASS:continue
   axis,positive=NORMALS[face];plane=element['to' if positive else 'from'][axis]
   # A frameless left/right edge is selected only inside a connected row.
   # The visual mask always adds an opaque post at each exposed row endpoint.
   internal_join=axis==0 and plane==(16 if positive else 0)
   if internal_join or covered(element,face,boxes):del element['faces'][face]
   elif plane==(16 if positive else 0):
    # Splitting the double door into cells used to discard every cullface.
    # Restore boundary culling for glass only, never for exposed wooden leaves.
    material['cullface']=face
  if element['faces']:kept.append(element)
 if 'elements' in result:result['elements']=kept
 return result
def prepare(assets):
 changed=removed=marked=0
 for path in module_glob(assets/'models/block', '*.json'):
  if not eligible(path):continue
  before=json.loads(module_read_text(path, encoding='utf-8-sig'));after=repair(before)
  if before==after:continue
  count=lambda data:sum(len(e['faces']) for e in data.get('elements',[]))
  culls=lambda data:sum('cullface' in f for e in data.get('elements',[]) for f in e['faces'].values())
  removed+=count(before)-count(after);marked+=culls(after)-culls(before);changed+=1
  module_write_text(path, json.dumps(after,ensure_ascii=False,separators=(',',':'))+'\n',encoding='utf-8')
 return {'models_changed':changed,'glass_faces_removed':removed,'net_boundary_cullfaces':marked}
