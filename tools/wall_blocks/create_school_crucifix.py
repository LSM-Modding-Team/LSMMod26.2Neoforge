"""Wall-mounted sculpted crucifix: 22x40px inside a two-by-three-cell controller grid."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text, module_write_text, output_path
from resource_json import resource_json
import copy,json,math,random
from pathlib import Path
from PIL import Image,ImageDraw
from facilities.create_bathroom_statue_assets import solid_model
ROOT=Path(__file__).resolve().parents[2];A=ROOT/'src/main/resources/assets/lsmmod';J=ROOT/'src/main/java/net/nicomar2009/lsmmod';D=ROOT/'src/main/resources/data';GENERATED=[]
PARTS=[([14.5,0,14,17.5,40,16],'wood'),([5,29.5,13.75,27,32.5,15.75],'wood'),
 ([13,35,12.5,19,37,14],'sign'),([14.5,29,10,17.5,32,13],'skin',{'north':'face'}),
 ([14.25,31.5,9.75,17.75,32.5,13.25],'hair'),([14.25,29.5,10,14.75,31.5,13.25],'hair'),([17.25,29.5,10,17.75,31.5,13.25],'hair'),
 ([14.75,28.75,9.75,17.25,30,11],'hair'),([15,27.5,11,17,29.25,13],'skin'),
 ([13.5,24,10.5,18.5,28.5,13.5],'skin'),([14.25,20,10.5,17.75,24,13.25],'skin'),
 ([13.5,18,10,18.5,21,13.5],'cloth'),([12.75,17,10,14,20,13],'cloth'),([13,14.5,10.5,14.25,17.5,13.25],'cloth'),
 ([12.75,19.5,9.75,14.5,21,11.5],'cloth'),([14,8,11,15.75,18,13.5],'skin'),([16,7,11,17.75,18,13.5],'skin'),
 ([13.75,11,10.75,15.75,13.5,13.5],'skin'),([16,10,10.75,18,12.5,13.5],'skin'),
 ([14,6.5,10,16,8.25,13],'skin'),([16,5.5,10,18,7.25,13],'skin'),
 ([15,7,9.8,15.5,7.5,10.2],'metal'),([17,6,9.8,17.5,6.5,10.2],'metal')]
# The head hangs below the crossbar, as in the reference, rather than sitting inside it.
for index in range(3,9):
    PARTS[index][0][1]-=2.5;PARTS[index][0][4]-=2.5
PARTS[9][0][4]=26.5
# Match the photograph's longer bare lower stem and compact sculpted figure.
PARTS[15][0][1]=11.5;PARTS[16][0][1]=10.5
for index in (17,18):
    PARTS[index][0][1]+=3;PARTS[index][0][4]+=3
for index in (19,20,21,22):
    PARTS[index][0][1]+=4;PARTS[index][0][4]+=4
LEFT=[([6.25,30.5,11.5,8.75,31.75,13.25],'skin'),([8.5,29,11,11.25,31,13.25],'skin'),([11,27.5,10.75,13.75,29.5,13.25],'skin'),([13,26.5,10.5,14.5,28.75,13.5],'skin'),([7,31,11.25,7.5,31.5,11.6],'metal')]
PARTS+=LEFT+[([32-b[3],b[1],b[2],32-b[0],b[4],b[5]],m) for b,m in LEFT]
# Small bevels on the shoulders and chest keep the relief readable at Minecraft scale.
PARTS += [([14,24.5,10.25,15.75,26,10.75],'skin_light'),([16.25,24.5,10.25,18,26,10.75],'skin_light'),([15.25,21,10.25,16.75,24,10.75],'skin_light')]

def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);module_write_text(p, resource_json(p, v, ensure_ascii=False)+'\n');GENERATED.append(str(p.relative_to(ROOT)))
def texture(name,image):p=A/f'textures/block/school_crucifix_{name}.png';image.save(output_path(p));GENERATED.append(str(p.relative_to(ROOT)))
def textures():
 for name,color in [('wood',(143,96,54)),('skin',(181,135,94)),('skin_light',(195,149,107)),('cloth',(219,213,192)),('hair',(60,43,30)),('metal',(79,77,71))]:
  im=Image.new('RGB',(16,16));rng=random.Random(261012)
  for y in range(16):
   for x in range(16):
    noise=rng.choice((-3,-2,-1,0,0,1,2,3))
    if name=='wood':noise+=int(6*math.sin(x*.8+math.sin(y*.35)*1.5))
    if name=='cloth' and x%5==0:noise-=8
    im.putpixel((x,y),tuple(max(0,min(255,c+noise)) for c in color))
  texture(name,im)
 face=Image.new('RGB',(16,16),(182,139,99));d=ImageDraw.Draw(face)
 d.rectangle((0,0,15,2),fill=(60,43,30));d.rectangle((0,2,2,15),fill=(75,48,31));d.rectangle((13,2,15,15),fill=(75,48,31))
 d.line((3,5,6,5),fill=(69,47,31),width=1);d.line((9,5,12,5),fill=(69,47,31),width=1)
 d.line((4,7,6,7),fill=(58,45,33));d.line((9,7,11,7),fill=(58,45,33));d.line((8,7,8,10),fill=(145,100,68))
 d.polygon(((3,11),(6,10),(8,12),(10,10),(13,11),(12,15),(4,15)),fill=(70,47,31));d.line((6,12,10,12),fill=(145,94,59));texture('face',face)
 im=Image.new('RGB',(32,16),(228,221,196));d=ImageDraw.Draw(im);d.rectangle((0,0,31,15),outline=(174,157,120))
 glyphs={'I':['111','010','010','010','010','010','111'],'N':['10001','11001','10101','10011','10001','10001','10001'],'R':['11110','10001','10001','11110','10100','10010','10001']};x=5
 for c in 'INRI':
  rows=glyphs[c]
  for y,row in enumerate(rows):
   for u,pixel in enumerate(row):
    if pixel=='1':d.point((x+u,y+4),fill=(66,47,28))
  x+=len(rows[0])+2
 texture('sign',im)

def mesh():
 value=solid_model(PARTS);value['textures']={m:'lsmmod:block/school_crucifix_'+m for m in ('wood','skin','skin_light','cloth','hair','metal','face','sign')};value['textures']['particle']=value['textures']['wood'];return value

def clipped(value,column,row):
 out=copy.deepcopy(value);out['elements']=[]
 chart={'north':((0,-1),(1,-1)),'south':((0,1),(1,-1)),'west':((2,1),(1,-1)),'east':((2,-1),(1,-1)),'up':((0,1),(2,1)),'down':((0,1),(2,-1))}
 for original in value['elements']:
  lo=original['from'];hi=original['to'];a=[max(lo[0],column*16),max(lo[1],row*16),lo[2]];b=[min(hi[0],column*16+16),min(hi[1],row*16+16),hi[2]]
  if any(a[i]>=b[i] for i in range(3)):continue
  e=copy.deepcopy(original)
  for axis,negative,positive in ((0,'west','east'),(1,'down','up')):
   if a[axis]>lo[axis]:e['faces'].pop(negative,None)
   if b[axis]<hi[axis]:e['faces'].pop(positive,None)
  for face,surface in e['faces'].items():
   old=surface['uv'];uv=[]
   for t,(axis,sign) in enumerate(chart[face]):
    extent=hi[axis]-lo[axis];start,end=old[t],old[t+2]
    u0=(a[axis]-lo[axis])/extent if sign>0 else (hi[axis]-b[axis])/extent
    u1=(b[axis]-lo[axis])/extent if sign>0 else (hi[axis]-a[axis])/extent
    uv.append((start+(end-start)*u0,start+(end-start)*u1))
   surface['uv']=[uv[0][0],uv[1][0],uv[0][1],uv[1][1]]
  e['from']=[a[0]-column*16,a[1]-row*16,a[2]];e['to']=[b[0]-column*16,b[1]-row*16,b[2]]
  if e['faces']:out['elements'].append(e)
 return out

def main():
 textures();value=mesh();variants={}
 for row in range(3):
  for column in range(2):
   name=f'school_crucifix_{column}_{row}';write(A/f'models/block/{name}.json',clipped(value,column,row))
   for i,facing in enumerate(('north','east','south','west')):variants[f'column={column},row={row},facing={facing}']={'model':'lsmmod:block/'+name,'y':i*90}
 write(A/'blockstates/school_crucifix.json',{'variants':variants})
 item=copy.deepcopy(value)
 for e in item['elements']:
  for key in ('from','to'):e[key]=[8+(e[key][0]-16)*.4,e[key][1]*.4,8+(e[key][2]-13)*.4]
 write(A/'models/item/school_crucifix.json',item);write(A/'items/school_crucifix.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/school_crucifix'}})
 write(D/'lsmmod/loot_table/blocks/school_crucifix.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'},{'condition':'minecraft:block_state_property','block':'lsmmod:school_crucifix','properties':{'row':'0','column':'0'}}],'entries':[{'type':'minecraft:item','name':'lsmmod:school_crucifix'}]}]})
 java='package net.nicomar2009.lsmmod.block;\n\n/** Generated by tools/wall_blocks/create_school_crucifix.py. */\npublic final class SchoolCrucifixShapes {\n private SchoolCrucifixShapes() {}\n public static final double[][] BOXES = '+json.dumps([p[0] for p in PARTS]).replace('[','{').replace(']','}')+';\n}\n';p=J/'modules/wall_blocks/block/SchoolCrucifixShapes.java';module_write_text(p, java);GENERATED.append(str(p.relative_to(ROOT)))
 block='    public static final DeferredBlock<SchoolCrucifixBlock> SCHOOL_CRUCIFIX = BLOCKS.registerBlock("school_crucifix", SchoolCrucifixBlock::new, props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));'
 item='    public static final DeferredItem<BlockItem> SCHOOL_CRUCIFIX = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CRUCIFIX);'
 for filename,body in [('ModBlocks.java',block),('ModItems.java',item)]:
  p=J/'registry'/filename;s=module_read_text(p);start='    // BEGIN SCHOOL CRUCIFIX';end='    // END SCHOOL CRUCIFIX';section=start+'\n'+body+'\n'+end
  if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section+s[b:]
  else:s=s.replace('    // BEGIN DARK TILE DRAIN COLUMN',section+'\n\n    // BEGIN DARK TILE DRAIN COLUMN',1)
  module_write_text(p, s);GENERATED.append(str(p.relative_to(ROOT)))
 for locale,label in [('es_es','Crucifijo del colegio'),('en_us','School Crucifix')]:
  p=A/f'lang/{locale}.json';v=json.loads(module_read_text(p));v['block.lsmmod.school_crucifix']=label;write(p,v)
 p=D/'minecraft/tags/block/mineable/axe.json';v=json.loads(module_read_text(p));v['values']=list(dict.fromkeys(v['values']+['lsmmod:school_crucifix']));write(p,v)
 module_write_text(Path('/tmp/crucifix-generated.json'), json.dumps(GENERATED))
if __name__=='__main__':main()
