"""Three-cell kiosk, timber panel/door, telescopic 6x4 entrance, raised fountain and lost box."""
import copy,json,random,re,hashlib
from pathlib import Path
from PIL import Image,ImageDraw,ImageFont
from create_bathroom_statue_assets import solid_model
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod';D=ROOT/'src/main/resources/data';J=ROOT/'src/main/java/net/nicomar2009/lsmmod';GENERATED=[]
IDS=['school_wood_panel','school_wood_door','school_sliding_entrance','drinking_fountain_raised','lost_and_found_box']

def write(p,v):
 p.parent.mkdir(parents=True,exist_ok=True);p.write_text(v if isinstance(v,str) else json.dumps(v,ensure_ascii=False,indent=2)+'\n');GENERATED.append(str(p.relative_to(ROOT)))
def save(im,name):
 p=A/f'textures/block/{name}.png';im.save(p);GENERATED.append(str(p.relative_to(ROOT)))
def basic(ident,model=None,controller=None):
 write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:'+(model or 'block/'+ident)}})
 conditions=[{'condition':'minecraft:survives_explosion'}]
 if controller:conditions.append({'condition':'minecraft:block_state_property','block':'lsmmod:'+ident,'properties':controller})
 write(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':conditions,'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
def rotated(value):
 v=copy.deepcopy(value);mapping={'north':'west','west':'south','south':'east','east':'north'}
 for e in v['elements']:
  a,b=e['from'],e['to'];e['from']=[a[2],a[1],16-b[0]];e['to']=[b[2],b[1],16-a[0]]
  e['faces']={mapping.get(f,f):{k:t for k,t in surface.items() if k!='cullface'} for f,surface in e['faces'].items()}
  for f,surface in e['faces'].items():
   if f in ('up','down'):surface['rotation']=(surface.get('rotation',0)+(270 if f=='up' else 90))%360
 return v

def gate_parts(opened):
 parts=[]
 # Fixed decorative lattice sides, full-height black boundary posts and upper rail.
 for x in (0,80):
  for u in range(0,16,4):parts.append(([x+u,0,6,x+u+1,64,8],'metal'))
  for y in range(0,64,4):parts.append(([x,y,6,x+16,y+1,8],'metal'))
 for x in (0,15,80,95):parts.append(([x,0,5,x+1,64,9],'metal'))
 # The two internal black posts at x16 and x80 delimit the moving 4x4 section.
 # Two one-block leaves per side retract into a one-block decorative pocket.
 for panel in range(4):
  x=(16+panel*16) if not opened else (0 if panel<2 else 80)
  z=8 if not opened else 9+(panel%2)*2
  for u in range(0,16,4):parts.append(([x+u,0,z,x+u+1,64,z+1],'metal'))
  for y in range(0,64,4):parts.append(([x,y,z,x+16,y+1,z+1],'metal'))
  parts.append(([x+15,0,z,x+16,64,z+1],'metal'))
 return parts
GATE_CLOSED=gate_parts(False);GATE_OPEN=gate_parts(True)

def cell_model(parts,column,row,textures):
 # solid_model clips on exact grid boundaries and removes hidden internal faces.
 clipped=[]
 for b,material in parts:
  x0,x1=max(b[0],column*16),min(b[3],(column+1)*16);y0,y1=max(b[1],row*16),min(b[4],(row+1)*16)
  if x0<x1 and y0<y1:clipped.append(([x0-column*16,y0-row*16,b[2],x1-column*16,y1-row*16,b[5]],material))
 if not clipped:return None
 v=solid_model(clipped);v['textures']=textures;v['render_type']='minecraft:solid'
 for e in v['elements']:
  for s in e['faces'].values():s.pop('cullface',None)
 return v

def rectangular(ident,closed,opened,width,height,textures):
 states={};shared={}
 for row in range(4 if height>1 else 1):
  for col in range(width):
   for op in ((False,True) if opened is not closed else (False,)):
    mesh=cell_model(opened if op else closed,min(col,width-1),min(row,height-1),textures)
    if mesh is None:apply={'model':'minecraft:block/air'}
    else:
     key=json.dumps(mesh,sort_keys=True)
     if key not in shared:
      name=f'{ident}_part_{len(shared)}';shared[key]=name;write(A/f'models/block/{name}.json',mesh)
     apply={'model':'lsmmod:block/'+shared[key]}
    for turn,face in enumerate(('north','east','south','west')):
     selector=f'facing={face},column={col}'+(f',row={row}' if height>1 else '')+(f',open={str(op).lower()}' if opened is not closed else '')
     states[selector]={**apply,'y':turn*90}
 write(A/f'blockstates/{ident}.json',{'variants':states})
 if ident=='school_sliding_entrance':
  icon=Image.new('RGBA',(96,96));draw=ImageDraw.Draw(icon)
  for b,material in closed:draw.rectangle((int(b[0]),16+64-int(b[4]),int(b[3])-1,16+64-int(b[1])-1),fill=(34,35,33,255))
  path=A/'textures/item/school_sliding_entrance.png';path.parent.mkdir(parents=True,exist_ok=True);icon.save(path);GENERATED.append(str(path.relative_to(ROOT)))
  write(A/f'models/item/{ident}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'lsmmod:item/school_sliding_entrance'}})
  basic(ident,'item/'+ident,{'column':'0','row':'0'});return
 item=solid_model(closed);item['textures']=textures
 for e in item['elements']:
  for key in ('from','to'):
   e[key]=[8+(e[key][0]-width*8)*min(1,1/width),e[key][1]/height,8+(e[key][2]-8)*min(1,1/width)]
 write(A/f'models/item/{ident}.json',item);basic(ident,'item/'+ident,{'column':'0',**({'row':'0'} if height>1 else {})})

def main():
 rng=random.Random(73191);im=Image.new('RGB',(16,16))
 for y in range(16):
  for x in range(16):
   grain=rng.choice((-3,-1,0,1,2))+(2 if x%5==0 else 0);im.putpixel((x,y),tuple(c+grain for c in (103,67,44)))
 save(im,'school_wood_panel');metal=Image.new('RGB',(16,16));metal.putdata([tuple(c+rng.choice((-2,-1,0,1,2)) for c in (34,35,33)) for _ in range(256)]);save(metal,'school_entrance_black_metal')
 # Simple pixel lettering, black on a white label, without antialiasing.
 label=Image.new('RGB',(64,32),(239,238,229));draw=ImageDraw.Draw(label);font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf',8)
 for y,text in ((5,'OBJETOS'),(17,'PERDIDOS')):
  mask=Image.new('1',(64,32));d=ImageDraw.Draw(mask);box=d.textbbox((0,0),text,font=font);d.text(((64-(box[2]-box[0]))//2,y),text,font=font,fill=1);label.paste((36,35,31),(0,0,64,32),mask)
 draw.rectangle((0,0,63,31),outline=(135,132,120));save(label,'lost_and_found_label')
 textures={'wood':'lsmmod:block/school_wood_panel','particle':'lsmmod:block/school_wood_panel'}
 panel=solid_model([([0,0,8,16,16,16],'wood')]);panel['textures']=textures;write(A/'models/block/school_wood_panel.json',panel)
 write(A/'blockstates/school_wood_panel.json',{'variants':{f'facing={f}':{'model':'lsmmod:block/school_wood_panel','y':n*90} for n,f in enumerate(('north','east','south','west'))}});basic('school_wood_panel')
 # Three fully moving wooden rows, reusing existing synchronized door behavior.
 for row in range(3):
  im=Image.open(A/'textures/block/school_wood_panel.png').copy();draw=ImageDraw.Draw(im);draw.line((1,0,1,15),fill=(78,47,30));draw.line((14,0,14,15),fill=(78,47,30))
  if row==0:draw.line((2,14,13,14),fill=(77,46,29))
  if row>=2:draw.line((2,1,13,1),fill=(77,46,29))
  if row==1:draw.rectangle((12,10,13,12),fill=(168,157,124));draw.point((12,12),fill=(50,45,34))
  save(im,f'school_wood_door_{row}')
 states={}
 for row in range(4):
  for hinge in ('left','right'):
   for op in (False,True):
    source=json.loads((A/f'models/block/tall_bathroom_door_full_3_{row}_{hinge}_{str(op).lower()}.json').read_text());source['textures']={'panel':f'lsmmod:block/school_wood_door_{min(row,2)}','particle':'lsmmod:block/school_wood_panel'}
    name=f'school_wood_door_{row}_{hinge}_{str(op).lower()}';write(A/f'models/block/{name}.json',source)
    for n,f in enumerate(('north','east','south','west')):states[f'facing={f},hinge={hinge},row={row},open={str(op).lower()}']={'model':'lsmmod:block/'+name,'y':n*90}
 write(A/'blockstates/school_wood_door.json',{'variants':states});write(A/'models/item/school_wood_door.json',{'parent':'lsmmod:block/school_wood_door_0_left_false'});basic('school_wood_door','item/school_wood_door',{'row':'0'})
 rectangular('school_sliding_entrance',GATE_CLOSED,GATE_OPEN,6,4,{'metal':'lsmmod:block/school_entrance_black_metal','particle':'lsmmod:block/school_entrance_black_metal'})
 # Raise the original fountain eight pixels and reserve both affected world cells.
 source=json.loads((A/'models/block/drinking_fountain.json').read_text());parts=[]
 for e in source['elements']:
  a,b=e['from'],e['to'];parts.append(([16-b[0],a[1]+8,16-b[2],16-a[0],b[1]+8,16-a[2]],'atlas'))
 # Preserve the atlas UV of every original face, including the tap and basin.
 fountainstates={}
 for row in range(2):
  mesh=copy.deepcopy(source);elements=[]
  for e in source['elements']:
   a,b=e['from'],e['to'];lo,hi=max(a[1]+8,row*16),min(b[1]+8,row*16+16)
   if lo>=hi:continue
   new=copy.deepcopy(e);new['from']=[16-b[0],lo-row*16,16-b[2]];new['to']=[16-a[0],hi-row*16,16-a[2]]
   mapping={'north':'south','south':'north','east':'west','west':'east'};new['faces']={mapping.get(f,f):s for f,s in new['faces'].items()}
   for f,s in new['faces'].items():
    s.pop('cullface',None)
    if f in ('up','down'):s['rotation']=(s.get('rotation',0)+180)%360
    else:
     uv=s['uv'];start,end=uv[1],uv[3];uv[1]=start+(end-start)*(b[1]+8-hi)/(b[1]-a[1]);uv[3]=start+(end-start)*(b[1]+8-lo)/(b[1]-a[1])
   # Internal caps at the boundary between the two cells must not be drawn.
   if a[1]+8<lo:new['faces'].pop('down',None)
   if b[1]+8>hi:new['faces'].pop('up',None)
   elements.append(new)
  mesh['elements']=elements
  if elements:name=f'drinking_fountain_raised_{row}';write(A/f'models/block/{name}.json',mesh);reference='lsmmod:block/'+name
  else:reference='minecraft:block/air'
  for n,f in enumerate(('north','east','south','west')):fountainstates[f'facing={f},half={"lower" if row==0 else "upper"}']={'model':reference,'y':n*90}
 write(A/'blockstates/drinking_fountain_raised.json',{'variants':fountainstates});basic('drinking_fountain_raised','block/drinking_fountain',{'half':'lower'})
 boxparts=[([0,0,0,16,2,16],'wood'),([0,2,0,1,14,16],'wood'),([15,2,0,16,14,16],'wood'),([1,2,.125,15,14,1],'wood'),([1,2,15,15,14,16],'wood'),([1,5,0,15,11,.125],'label')]
 box=solid_model(boxparts);box['textures']={**textures,'label':'lsmmod:block/lost_and_found_label'}
 for e in box['elements']:
  for f,s in e['faces'].items():
   if s['texture']=='#label':s['uv']=[0,0,16,16]
 write(A/'models/block/lost_and_found_box.json',box);write(A/'blockstates/lost_and_found_box.json',{'variants':{f'facing={f}':{'model':'lsmmod:block/lost_and_found_box','y':n*90} for n,f in enumerate(('north','east','south','west'))}});basic('lost_and_found_box')
 # Retain the original kiosk supports and tablecloth; insert a support-free middle cell.
 models=[rotated(json.loads((A/f'models/block/kiosk_table_{part}.json').read_text())) for part in ('left','right')]
 center=copy.deepcopy(models[0]);center['elements']=[e for e in center['elements'] if e['from'][1]>=14 or e['from'][1]>=8 and e['from'][0]<e['to'][0] and e['to'][2]-e['from'][2]<=.5]
 kioskparts=[]
 for col,mesh in enumerate((models[0],center,models[1])):
  for e in mesh['elements']:
   a,b=e['from'],e['to'];material=next(iter(e['faces'].values()))['texture'][1:];kioskparts.append(([a[0]+col*16,a[1],a[2],b[0]+col*16,b[1],b[2]],material))
 rectangular('kiosk_table',kioskparts,kioskparts,3,1,models[0]['textures'])
 # Preserve original orientation, textures and serialized left/right states.
 native=[json.loads((A/f'models/block/kiosk_table_{p}.json').read_text()) for p in ('left','right')]
 native_center=copy.deepcopy(native[0]);native_center['elements']=[e for e in native_center['elements'] if e['from'][1]>=14 or e['from'][1]>=8 and e['to'][0]-e['from'][0]<=.5]
 meshes=(native[0],native_center,native[1]);states={}
 for col,(part,mesh) in enumerate(zip(('left','center','right'),meshes)):
  write(A/f'models/block/kiosk_table_part_{col}.json',mesh)
  for n,face in enumerate(('south','west','north','east')):states[f'facing={face},part={part}']={'model':f'lsmmod:block/kiosk_table_part_{col}','y':n*90}
 write(A/'blockstates/kiosk_table.json',{'variants':states});basic('kiosk_table','block/kiosk_table_item',{'part':'left'})
 item=copy.deepcopy(native[0]);item['elements']=[]
 for col,mesh in enumerate(meshes):
  for e in mesh['elements']:
   e=copy.deepcopy(e)
   for key in ('from','to'):e[key]=[8+(e[key][0]-8)/3,e[key][1],8+(e[key][2]+col*16-24)/3]
   item['elements'].append(e)
 write(A/'models/item/kiosk_table.json',item);write(A/'models/block/kiosk_table_item.json',item)
 arrays={'KIOSK':[b for b,m in kioskparts],'GATE_CLOSED':[b for b,m in GATE_CLOSED],'GATE_OPEN':[b for b,m in GATE_OPEN],'FOUNTAIN':[b for b,m in parts],'LOST_BOX':[b for b,m in boxparts]}
 java='package net.nicomar2009.lsmmod.block;\n\n/** Generated physical grids shared with school accessory models. */\npublic final class SchoolEntranceAccessoryShapes {\n private SchoolEntranceAccessoryShapes() {}\n'
 for name,boxes in arrays.items():java+=' public static final double[][] '+name+' = '+json.dumps(boxes).replace('[','{').replace(']','}')+';\n'
 write(J/'block/SchoolEntranceAccessoryShapes.java',java+'}\n')
 registrations='''    public static final DeferredBlock<SchoolPassiveAccessoryBlock> SCHOOL_WOOD_PANEL = BLOCKS.registerBlock("school_wood_panel", props -> new SchoolPassiveAccessoryBlock(props, new double[][]{{0,0,8,16,16,16}}), props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredBlock<FullBathroomDoorBlock> SCHOOL_WOOD_DOOR = BLOCKS.registerBlock("school_wood_door", props -> new FullBathroomDoorBlock(3,props), props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolSlidingEntranceBlock> SCHOOL_SLIDING_ENTRANCE = BLOCKS.registerBlock("school_sliding_entrance", SchoolSlidingEntranceBlock::new, props -> props.strength(3F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<RaisedDrinkingFountainBlock> DRINKING_FOUNTAIN_RAISED = BLOCKS.registerBlock("drinking_fountain_raised", RaisedDrinkingFountainBlock::new, props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolPassiveAccessoryBlock> LOST_AND_FOUND_BOX = BLOCKS.registerBlock("lost_and_found_box", props -> new SchoolPassiveAccessoryBlock(props,SchoolEntranceAccessoryShapes.LOST_BOX), props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion());'''
 for filename,body in [('ModBlocks.java',registrations),('ModItems.java','\n'.join(f'    public static final DeferredItem<BlockItem> {i.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{i.upper()});' for i in IDS))]:
  p=J/'registry'/filename;s=p.read_text();begin='    // BEGIN KIOSK ENTRANCE ACCESSORIES';end='    // END KIOSK ENTRANCE ACCESSORIES';section=begin+'\n'+body+'\n'+end
  if begin in s:a=s.index(begin);b=s.index(end)+len(end);s=s[:a]+section+s[b:]
  else:s=s.replace('    // BEGIN SCHOOL CRUCIFIX',section+'\n\n    // BEGIN SCHOOL CRUCIFIX',1)
  write(p,s)
 for locale,names in [('es_es',['Panel de madera del colegio','Puerta de madera del colegio','Entrada deslizable del colegio','Bebedero elevado','Caja de objetos perdidos']),('en_us',['School Wood Panel','School Wood Door','School Sliding Entrance','Raised Drinking Fountain','Lost and Found Box'])]:
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text());v.update({'block.lsmmod.'+i:n for i,n in zip(IDS,names)});write(p,v)
 for tool,ids in [('axe',['school_wood_panel','school_wood_door','lost_and_found_box']),('pickaxe',['school_sliding_entrance','drinking_fountain_raised'])]:
  p=D/f'minecraft/tags/block/mineable/{tool}.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+i for i in ids]));write(p,v)
 Path('/tmp/kiosk-generated.json').write_text(json.dumps(GENERATED))
if __name__=='__main__':main()
