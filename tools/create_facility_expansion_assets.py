"""Opaque 3x3 entrance grids, 3-cell cot, 2x3 statue/door, microwave and trophy display."""
import copy,json,re
from pathlib import Path
from PIL import Image
from create_bathroom_statue_assets import STATUE,solid_model
from create_kiosk_entrance_assets import rotated
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod';D=ROOT/'src/main/resources/data';J=ROOT/'src/main/java/net/nicomar2009/lsmmod';GENERATED=[]
IDS=['dining_door_wide','school_microwave','school_trophy_gallery']
FACES=('north','south','east','west','up','down')
MICROWAVE=[([1,1,2,15,10,14],'metal'),([1,1,1,11,9,2],'black'),([2,2,.75,10,8,1],'glass'),([11,1,1,15,9,2],'metal'),([11.5,3,.5,12.25,7,1],'metal'),([13,5,.75,14,7,1],'black'),([13,2.5,.75,14,3.5,1],'button')]
DINING_FIXED=[([24,0,0,32,48,16],'white',{'east':'light'}),([1,41,.25,23,47,3.75],'glass'),([0,40,0,24,41,4],'frame'),([0,47,0,24,48,4],'frame'),([0,41,0,1,47,4],'frame'),([23,41,0,24,47,4],'frame')]
DINING_CLOSED=DINING_FIXED+[([0,0,0,24,16,4],'bottom'),([0,16,0,24,40,4],'top')]
DINING_OPEN=DINING_FIXED+[([0,0,0,4,16,24],'bottom'),([0,16,0,4,40,24],'top')]

def write(p,v):
 p.parent.mkdir(parents=True,exist_ok=True);p.write_text(v if isinstance(v,str) else json.dumps(v,ensure_ascii=False,indent=2)+'\n');GENERATED.append(str(p.relative_to(ROOT)))
def basic(ident,root=None,controller=None):
 write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:'+(root or 'item/'+ident)}})
 cond=[{'condition':'minecraft:survives_explosion'}]
 if controller:cond.append({'condition':'minecraft:block_state_property','block':'lsmmod:'+ident,'properties':controller})
 write(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':cond,'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
def mesh(parts,textures):
 m=solid_model(parts);m['textures']=textures;m['render_type']='minecraft:solid';return m

def clipped(parts,col,row,depth=0,right=False,width=32):
 out=[]
 for part in parts:
  b,mat=part[:2];over=copy.deepcopy(part[2]) if len(part)>2 else {};b=b[:]
  if right:b[0],b[3]=width-b[3],width-b[0];over={('west' if f=='east' else 'east' if f=='west' else f):m for f,m in over.items()}
  lo=[max(b[i],base) for i,base in enumerate((col*16,row*16,depth*16))];hi=[min(b[i+3],base+16) for i,base in enumerate((col*16,row*16,depth*16))]
  if all(lo[i]<hi[i] for i in range(3)):out.append(([lo[0]-col*16,lo[1]-row*16,lo[2]-depth*16,hi[0]-col*16,hi[1]-row*16,hi[2]-depth*16],mat,over))
 return out

def scaled_item(parts,textures,width,height):
 m=mesh(parts,textures);factor=min(16/width,16/height,1)
 for e in m['elements']:
  for k in ('from','to'):e[k]=[8+(e[k][0]-width/2)*factor,e[k][1]*factor,8+(e[k][2]-8)*factor]
 return m

def ordinary_rectangle(ident,parts,textures,width,height):
 states={};shared={}
 for row in range(height):
  for col in range(width):
   m=mesh(clipped(parts,col,row,width=width*16),textures)
   key=json.dumps(m,sort_keys=True)
   if key not in shared:
    name=f'{ident}_part_{len(shared)}';shared[key]=name;write(A/f'models/block/{name}.json',m)
   for turn,f in enumerate(('north','east','south','west')):states[f'facing={f},column={col},row={row}']={'model':'lsmmod:block/'+shared[key],'y':turn*90}
 write(A/f'blockstates/{ident}.json',{'variants':states});write(A/f'models/item/{ident}.json',scaled_item(parts,textures,width*16,height*16));basic(ident,controller={'column':'0','row':'0'})

def cot():
 source=json.loads((A/'models/block/infirmary_cot_1.json').read_text());states={}
 for segment in range(3):
  for extended in (False,True):
   name=f'infirmary_cot_{min(segment,1)}'
   if extended and segment>0:
    name=f'infirmary_cot_extended_{segment}';m=copy.deepcopy(source);m['elements']=[]
    low=(segment-1)*16;high=low+16
    for e in source['elements']:
     a,b=e['from'],e['to'];z0,z1=max(a[2]*2,low),min(b[2]*2,high)
     if z0>=z1:continue
     new=copy.deepcopy(e);new['from']=[a[0],a[1],z0-low];new['to']=[b[0],b[1],z1-low]
     if a[2]*2<z0:new['faces'].pop('north',None)
     if b[2]*2>z1:new['faces'].pop('south',None)
     for face,s in new['faces'].items():
      if face in ('east','west','up','down'):
       uv=s.get('uv',[0,0,16,16]);axis=1 if face in ('up','down') else 0;u0,u1=uv[axis],uv[axis+2];uv[axis]=u0+(u1-u0)*(z0-a[2]*2)/(2*(b[2]-a[2]));uv[axis+2]=u0+(u1-u0)*(z1-a[2]*2)/(2*(b[2]-a[2]));s['uv']=uv
     m['elements'].append(new)
    write(A/f'models/block/{name}.json',m)
   for n,f in enumerate(('south','west','north','east')):states[f'facing={f},segment={segment},extended={str(extended).lower()}']={'model':'lsmmod:block/'+name,'y':n*90}
 write(A/'blockstates/infirmary_cot.json',{'variants':states})
 # Item shows the extended mattress without changing the inclined headrest.
 item=json.loads((A/'models/block/infirmary_cot_0.json').read_text());elements=copy.deepcopy(item['elements'])
 for e in source['elements']:
  e=copy.deepcopy(e)
  for k in ('from','to'):e[k][2]=16+e[k][2]*2
  elements.append(e)
 item['elements']=elements
 for e in item['elements']:
  for k in ('from','to'):e[k]=[8+(e[k][0]-8)/3,e[k][1]/3,8+(e[k][2]-24)/3]
  if 'rotation' in e:e['rotation']['origin']=[8+(e['rotation']['origin'][0]-8)/3,e['rotation']['origin'][1]/3,8+(e['rotation']['origin'][2]-24)/3]
 write(A/'models/block/infirmary_cot_item.json',item)
 # Preserve the original two physical grids; extend only the former tail's Z span.
 source_java=(J/'block/InfirmaryCotShapes.java').read_text();tail=source_java[source_java.index('SHAPES[1][0]'):source_java.index('for (int segment')]
 boxes=[list(map(float,v.split(','))) for v in re.findall(r'ExactVoxelShapes.box\(([\d., \-]+)\)',tail)]
 arrays=[]
 for segment in range(2):
  local=[]
  for b in boxes:
   z0,z1=max(b[2]*2,segment*16),min(b[5]*2,segment*16+16)
   if z0<z1:local.append([b[0],b[1],z0-segment*16,b[3],b[4],z1-segment*16])
  arrays.append(local)
 java='package net.nicomar2009.lsmmod.block;\n\n/** Extended cot reuses the original angled headrest and shares direct grid rotations. */\npublic final class InfirmaryCotExtendedShapes {\n public static final net.minecraft.world.phys.shapes.VoxelShape[][] SHAPES=new net.minecraft.world.phys.shapes.VoxelShape[3][];\n static {\n  SHAPES[0]=InfirmaryCotShapes.SHAPES[0];\n'
 for i,boxes in enumerate(arrays,1):java+=f'  SHAPES[{i}]=SharedChairRotations.horizontal(ExactVoxelShapes.pixels('+json.dumps(boxes).replace('[','{').replace(']','}')+'));\n'
 write(J/'block/InfirmaryCotExtendedShapes.java',java+' }\n private InfirmaryCotExtendedShapes() {}\n}\n')

def main():
 cot()
 # Rotate the original east-facing sculpt to NORTH, scale to a 2x3 footprint.
 statue=[]
 textures=json.loads((A/'models/block/san_martin_de_porres_down.json').read_text())['textures'];textures.pop('robe');textures['robe_down']='lsmmod:block/san_martin_de_porres_down';textures['robe_up']='lsmmod:block/san_martin_de_porres_up'
 for part in STATUE:
  b,mat=part[:2];over=part[2] if len(part)>2 else {};mat=('robe_down' if b[1]<16 else 'robe_up') if mat=='robe' else mat
  mapping={'east':'north','north':'west','west':'south','south':'east'}
  statue.append(([b[2]*2,b[1]*1.5,16-b[3]*2,b[5]*2,b[4]*1.5,16-b[0]*2],mat,{mapping.get(f,f):m for f,m in over.items()}))
 ordinary_rectangle('san_martin_de_porres',statue,textures,2,3)
 states=json.loads((A/'blockstates/san_martin_de_porres.json').read_text())['variants'];states={key+',expanded=true':value for key,value in states.items()}
 for half,suffix in [('lower','down'),('upper','up')]:
  for face,angle in [('north',270),('east',0),('south',90),('west',180)]:states[f'facing={face},half={half},expanded=false']={'model':'lsmmod:block/san_martin_de_porres_'+suffix,'y':angle}
 write(A/'blockstates/san_martin_de_porres.json',{'variants':states})
 loot=json.loads((D/'lsmmod/loot_table/blocks/san_martin_de_porres.json').read_text());loot['pools'][0]['conditions']=[{'condition':'minecraft:survives_explosion'},{'condition':'minecraft:any_of','terms':[{'condition':'minecraft:block_state_property','block':'lsmmod:san_martin_de_porres','properties':{'expanded':'true','column':'0','row':'0'}},{'condition':'minecraft:block_state_property','block':'lsmmod:san_martin_de_porres','properties':{'expanded':'false','half':'lower'}}]}];write(D/'lsmmod/loot_table/blocks/san_martin_de_porres.json',loot)
 # Golden cup display: six columns, four shelves, one decorated cup per bay.
 trophies=[([0,0,14,96,64,16],'wood')]
 for x in (0,95):trophies.append(([x,0,0,x+1,64,14],'wood'))
 for y in (0,16,32,48,63):trophies.append(([0,y,0,96,y+1,14],'wood'))
 for col in range(6):
  for row in range(4):
   x,y=col*16,row*16
   trophies.extend([([x+4,y+1,5,x+12,y+2,12],'base'),([x+6,y+2,6,x+10,y+3,11],'gold'),([x+7,y+3,7,x+9,y+6,10],'gold'),([x+5,y+6,6,x+11,y+10,11],'gold'),([x+4,y+10,5,x+12,y+11,12],'gold'),([x+3,y+7,7,x+4,y+10,10],'gold'),([x+12,y+7,7,x+13,y+10,10],'gold'),([x+4,y+9,7,x+5,y+10,10],'gold'),([x+11,y+9,7,x+12,y+10,10],'gold')])
 # Glass front is a separate translucent multipart layer, never mixed with opaque cups.
 ordinary_rectangle('school_trophy_gallery',trophies,{'wood':'lsmmod:block/school_wood_panel','base':'lsmmod:block/dark_school_wall','gold':'lsmmod:block/school_gold','particle':'lsmmod:block/school_wood_panel'},6,4)
 glass=mesh([([0,0,0,16,16,.125],'glass')],{'glass':'lsmmod:block/school_glass','particle':'lsmmod:block/school_glass'});glass['render_type']='minecraft:translucent';glass['elements']=[e for e in glass['elements'] if any(f in ('north','south') for f in e['faces'])];write(A/'models/block/school_trophy_gallery_glass.json',glass)
 old=json.loads((A/'blockstates/school_trophy_gallery.json').read_text())['variants'];multipart=[]
 for key,value in old.items():multipart.append({'when':dict(t.split('=') for t in key.split(',')),'apply':value})
 for turn,f in enumerate(('north','east','south','west')):multipart.append({'when':{'facing':f},'apply':{'model':'lsmmod:block/school_trophy_gallery_glass','y':turn*90}})
 write(A/'blockstates/school_trophy_gallery.json',{'multipart':multipart})
 microtextures={'metal':'lsmmod:block/school_decoration_surface','black':'lsmmod:block/school_entrance_black_metal','glass':'lsmmod:block/school_tinted_glass','button':'lsmmod:block/classroom_white_wall','particle':'lsmmod:block/school_decoration_surface'}
 microwave=mesh(MICROWAVE,microtextures)
 # Use the light metallic region of the existing decoration atlas, not its dark patches.
 for e in microwave['elements']:
  for f,s in e['faces'].items():
   if s['texture']=='#metal':s['uv']=[12,0,15.5,7.5]
 write(A/'models/block/school_microwave.json',microwave);write(A/'blockstates/school_microwave.json',{'variants':{f'facing={f}':{'model':'lsmmod:block/school_microwave','y':n*90} for n,f in enumerate(('north','east','south','west'))}});basic('school_microwave','block/school_microwave')
 # Door: wood leaf, glazed upper half-row, fixed half-block side wall.
 tex={'bottom':'lsmmod:block/dining_door_bottom','top':'lsmmod:block/dining_door_top','white':'lsmmod:block/classroom_white_wall','light':'lsmmod:block/light_school_wall','glass':'lsmmod:block/school_glass','frame':'lsmmod:block/school_glass_frame','particle':'lsmmod:block/dining_door_bottom'}
 multipart=[];shared={}
 for op in (False,True):
  for right in (False,True):
   for row in range(3):
    for col in range(2):
     for depth in range(2):
      parts=clipped(DINING_OPEN if op else DINING_CLOSED,col,row,depth,right)
      if not op and depth>0:parts=[]
      for layer in ('solid','translucent'):
       subset=[p for p in parts if (p[1]=='glass')==(layer=='translucent')]
       if not subset:continue
       value=mesh(subset,tex);value['render_type']='minecraft:'+layer
       if layer=='translucent':value['elements']=[e for e in value['elements'] if any(f in ('north','south') for f in e['faces'])]
       for e in value['elements']:
        for face,s in e['faces'].items():
         # Side-wall outer face alone is light; lower/front/back/bottom remain white.
         if s['texture'] in ('#white','#light'):s['uv']=[0,0,16,16]
         if s['texture'] in ('#bottom','#top') and face in (('west','east') if op else ('north','south')):
          axis=2 if op else 0;offset=depth*16 if op else col*16;lo=e['from'][axis]+offset;hi=e['to'][axis]+offset
          if right and not op:lo,hi=32-hi,32-lo
          u0,u1=lo*16/24,hi*16/24
          if face==('west' if op else 'north'):u0,u1=u1,u0
          end,height=(16,16) if s['texture']=='#bottom' else (40,24)
          s['uv']=[u0,(end-e['to'][1]-row*16)*16/height,u1,(end-e['from'][1]-row*16)*16/height]
       key=json.dumps(value,sort_keys=True)
       if key not in shared:
        name=f'dining_door_wide_part_{len(shared)}';shared[key]=name;write(A/f'models/block/{name}.json',value)
       for turn,f in enumerate(('north','east','south','west')):
        multipart.append({'when':{'facing':f,'open':str(op).lower(),'hinge':'right' if right else 'left','row':str(row),'column':str(col),'depth':str(depth)},'apply':{'model':'lsmmod:block/'+shared[key],'y':turn*90}})
 write(A/'blockstates/dining_door_wide.json',{'multipart':multipart});write(A/'models/item/dining_door_wide.json',scaled_item(DINING_CLOSED,tex,32,48));basic('dining_door_wide',controller={'column':'0','row':'0','depth':'0'})
 # One generated geometry source for physical cells and models.
 arrays={'STATUE':[b for b,*_ in statue],'TROPHIES':[b for b,*_ in trophies]+[[0,0,0,96,64,.125]],'MICROWAVE':[b for b,*_ in MICROWAVE],'DINING_CLOSED':[b for b,*_ in DINING_CLOSED],'DINING_OPEN':[b for b,*_ in DINING_OPEN]}
 java='package net.nicomar2009.lsmmod.block;\n\n/** Model-identical facility boxes; per-cell grids and rotations are cached. */\npublic final class SchoolFacilityShapes {\n private SchoolFacilityShapes() {}\n'
 for name,boxes in arrays.items():java+=' public static final double[][] '+name+' = '+json.dumps(boxes).replace('[','{').replace(']','}')+';\n'
 write(J/'block/SchoolFacilityShapes.java',java+'}\n')
 registrations='''    public static final DeferredBlock<SchoolFramedDiningDoorBlock> DINING_DOOR_WIDE = BLOCKS.registerBlock("dining_door_wide", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolPassiveAccessoryBlock> SCHOOL_MICROWAVE = BLOCKS.registerBlock("school_microwave", props -> new SchoolPassiveAccessoryBlock(props,SchoolFacilityShapes.MICROWAVE), props -> props.strength(2F).sound(SoundType.METAL).noOcclusion());
    public static final DeferredBlock<SchoolTrophyGalleryBlock> SCHOOL_TROPHY_GALLERY = BLOCKS.registerBlock("school_trophy_gallery", SchoolTrophyGalleryBlock::new, props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));'''
 for filename,body in [('ModBlocks.java',registrations),('ModItems.java','\n'.join(f'    public static final DeferredItem<BlockItem> {i.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{i.upper()});' for i in IDS))]:
  p=J/'registry'/filename;s=p.read_text();a='    // BEGIN FACILITY EXPANSIONS';z='    // END FACILITY EXPANSIONS';section=a+'\n'+body+'\n'+z
  if a in s:start=s.index(a);end=s.index(z)+len(z);s=s[:start]+section+s[end:]
  else:s=s.replace('    // BEGIN KIOSK ENTRANCE ACCESSORIES',section+'\n\n    // BEGIN KIOSK ENTRANCE ACCESSORIES',1)
  if filename=='ModBlocks.java':s=s.replace('DeferredBlock<TwoTallBlock> SAN_MARTIN_DE_PORRES','DeferredBlock<SchoolSanMartinBlock> SAN_MARTIN_DE_PORRES').replace('"san_martin_de_porres",\n            TwoTallBlock::new','"san_martin_de_porres",\n            SchoolSanMartinBlock::new')
  write(p,s)
 for locale,names in [('es_es',['Puerta del comedor con marco (2×3)','Microondas del colegio','Galería de trofeos del colegio']),('en_us',['Framed Dining Door (2×3)','School Microwave','School Trophy Gallery'])]:
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text());v.update({'block.lsmmod.'+i:n for i,n in zip(IDS,names)});write(p,v)
 for tool,ids in [('axe',['dining_door_wide','school_trophy_gallery']),('pickaxe',['school_microwave'])]:
  p=D/f'minecraft/tags/block/mineable/{tool}.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+i for i in ids]));write(p,v)
 Path('/tmp/facility-generated.json').write_text(json.dumps(GENERATED))
if __name__=='__main__':main()
