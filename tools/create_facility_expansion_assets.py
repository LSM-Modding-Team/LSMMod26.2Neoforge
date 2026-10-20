"""Opaque 3x3 entrance grids, 3-cell cot, 2x3 statue/door, microwave and trophy display."""
from resource_modules import module_glob, module_read_text, module_unlink, module_write_text, output_path
from resource_json import resource_json
import copy,json,re
from pathlib import Path
from PIL import Image
from facilities.create_bathroom_statue_assets import solid_model
from create_kiosk_entrance_assets import rotated
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod';D=ROOT/'src/main/resources/data';J=ROOT/'src/main/java/net/nicomar2009/lsmmod';GENERATED=[]
IDS=['dining_door_wide','bathroom_door_wide','school_microwave','school_trophy_gallery','infirmary_door','dining_door_wide_light']
FACES=('north','south','east','west','up','down')
MICROWAVE=[([1,1,2,15,10,14],'metal'),([1,1,1,11,9,2],'black'),([2,2,.75,10,8,1],'glass'),([11,1,1,15,9,2],'metal'),([11.5,3,.5,12.25,7,1],'metal'),([13,5,.75,14,7,1],'black'),([13,2.5,.75,14,3.5,1],'button')]
DINING_FIXED=[([24,0,0,32,40,16],'white',{'east':'light'}),([0,40,0,32,48,8],'glass')]
# A real opening in the wooden leaf, with vanilla bordered glass inset into it.
DINING_LEAF=[([0,0,0,24,20,4],'bottom'),([0,20,0,24,40,4],'top'),([7.5,7.5,1,16.5,35,3],'window')]
DINING_CLOSED=DINING_FIXED+DINING_LEAF
DINING_OPEN=DINING_FIXED+[([b[2],b[1],b[0],b[5],b[4],b[3]],mat) for b,mat in DINING_LEAF]

def write(p,v):
 p.parent.mkdir(parents=True,exist_ok=True);module_write_text(p, v if isinstance(v,str) else resource_json(p, v, ensure_ascii=False)+'\n');GENERATED.append(str(p.relative_to(ROOT)))
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

def layered_item(ident,layers,textures,width,height):
 references=[]
 for layer,parts in layers:
  m=scaled_item(parts,textures,width,height);m.update({'render_type':'minecraft:'+layer,'loader':'fusion:model','type':'base'})
  name=ident+'_item_'+layer;write(A/f'models/item/{name}.json',m);references.append('lsmmod:item/'+name)
 write(A/f'models/item/{ident}.json',{'loader':'fusion:model','type':'composite','textures':textures,'models':[references]})

def cot():
 source=json.loads(module_read_text(A/'models/block/infirmary_cot_1.json'));states={}
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
 item=json.loads(module_read_text(A/'models/block/infirmary_cot_0.json'));elements=copy.deepcopy(item['elements'])
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
 source_java=module_read_text(J/'modules/facilities/block/InfirmaryCotShapes.java');tail=source_java[source_java.index('SHAPES[1][0]'):source_java.index('for (int segment')]
 boxes=[list(map(float,v.split(','))) for v in re.findall(r'ExactVoxelShapes.box\(([\d., \-]+)\)',tail)]
 arrays=[]
 for segment in range(2):
  local=[]
  for b in boxes:
   z0,z1=max(b[2]*2,segment*16),min(b[5]*2,segment*16+16)
   if z0<z1:local.append([b[0],b[1],z0-segment*16,b[3],b[4],z1-segment*16])
  arrays.append(local)
 java='package net.nicomar2009.lsmmod.block;\n\n/** Extended cot reuses the original angled headrest and shares direct grid rotations. */\npublic final class InfirmaryCotExtendedShapes {\n public static final net.minecraft.world.phys.shapes.VoxelShape[][] SHAPES=new net.minecraft.world.phys.shapes.VoxelShape[3][];\n static {\n  SHAPES[0]=InfirmaryCotShapes.SHAPES[0];\n'
 for i,boxes in enumerate(arrays,1):java+=f'  SHAPES[{i}]=SharedChairRotations.horizontal(ExactVoxelShapes.pixels(new double[][] '+json.dumps(boxes).replace('[','{').replace(']','}')+'));\n'
 write(J/'modules/facilities/block/InfirmaryCotExtendedShapes.java',java+' }\n private InfirmaryCotExtendedShapes() {}\n}\n')

def main():
 cot()
 from special_blocks.create_san_martin_display import generate as rebuild_san_martin
 GENERATED.extend(rebuild_san_martin())
 # Solid enclosure collision; the centered sculpture is decorative inside it.
 statue=[([0,0,0,32,40,16],'white')]
 loot=json.loads(module_read_text(D/'lsmmod/loot_table/blocks/san_martin_de_porres.json'));loot['pools'][0]['conditions']=[{'condition':'minecraft:survives_explosion'},{'condition':'minecraft:any_of','terms':[{'condition':'minecraft:block_state_property','block':'lsmmod:san_martin_de_porres','properties':{'expanded':'true','column':'0','row':'0'}},{'condition':'minecraft:block_state_property','block':'lsmmod:san_martin_de_porres','properties':{'expanded':'false','half':'lower'}}]}];write(D/'lsmmod/loot_table/blocks/san_martin_de_porres.json',loot)
 # Six columns, four shelves, 12px total depth; 18 gold and six silver/bronze awards.
 trophies=[([0,0,10.5,96,64,12],'wood')]
 for x in (0,95):trophies.append(([x,0,0,x+1,64,10.5],'wood'))
 for y in (0,16,32,48,63):trophies.append(([0,y,0,96,y+1,10.5],'wood'))
 for col in range(6):
  for row in range(4):
   x,y=col*16,row*16
   variant=(col+row)%4;metal='gold' if (col*4+row)%4!=3 else ('silver' if col%2 else 'bronze')
   height=(10,13,8,12)[variant]
   trophies.append(([x+4,y+1,3,x+12,y+2,9],'base'))
   if variant in (0,1):
    neck=height-5
    pieces=[[6,2,4,10,3,8],[7,3,5,9,neck,7],[5,neck,4,11,height-1,8],[4,height-1,3,12,height,9],[3,neck+1,5,4,height-1,7],[12,neck+1,5,13,height-1,7],[4,height-2,5,5,height-1,7],[11,height-2,5,12,height-1,7]]
   elif variant==2:
    # Broad shield on a short pedestal.
    pieces=[[6,2,4,10,4,8],[5,4,5,11,5,7],[4,5,5,12,7,7],[5,7,5,11,8,7]]
   else:
    # Tall stepped award with a contrasting inset plaque.
    pieces=[[6,2,4,10,4,8],[7,4,5,9,9,7],[5,9,4,11,11,8],[6,11,5,10,12,7]]
   trophies.extend([([x+b[0],y+b[1],b[2],x+b[3],y+b[4],b[5]],metal) for b in pieces])
 # Glass front is a separate translucent multipart layer, never mixed with opaque cups.
 trophy_textures={'wood':'lsmmod:block/school_wood_panel','base':'lsmmod:block/dark_school_wall','gold':'lsmmod:block/school_gold','silver':'lsmmod:block/school_trophy_silver','bronze':'lsmmod:block/school_trophy_bronze','glass':'lsmmod:block/school_glass','particle':'lsmmod:block/school_wood_panel'}
 ordinary_rectangle('school_trophy_gallery',trophies,trophy_textures,6,4)
 layered_item('school_trophy_gallery',[('solid',trophies),('translucent',[([0,0,0,96,64,.125],'glass')])],trophy_textures,96,64)
 for material,color in [('silver',(192,199,207)),('bronze',(164,104,55))]:
  image=Image.new('RGB',(16,16))
  for y in range(16):
   for x in range(16):image.putpixel((x,y),tuple(c+((x*3+y*5)%5-2) for c in color))
  image.save(output_path(A/f'textures/block/school_trophy_{material}.png'))
 glass=mesh([([0,0,0,16,16,.125],'glass')],{'glass':'lsmmod:block/school_glass','particle':'lsmmod:block/school_glass'});glass['render_type']='minecraft:translucent';glass['elements']=[e for e in glass['elements'] if any(f in ('north','south') for f in e['faces'])];write(A/'models/block/school_trophy_gallery_glass.json',glass)
 old=json.loads(module_read_text(A/'blockstates/school_trophy_gallery.json'))['variants'];multipart=[]
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
 tex={'bottom':'lsmmod:block/dining_door_bottom','top':'lsmmod:block/dining_door_top','white':'lsmmod:block/classroom_white_wall','light':'lsmmod:block/light_school_wall','glass':'lsmmod:block/school_glass','window':'minecraft:block/glass','particle':'lsmmod:block/dining_door_bottom'}
 multipart=[];shared={}
 for op in (False,True):
  for right in (False,True):
   for row in range(3):
    for col in range(2):
     for depth in range(2):
      parts=clipped(DINING_OPEN if op else DINING_CLOSED,col,row,depth,right)
      if not op and depth>0:parts=[]
      for layer in ('solid','translucent','cutout'):
       subset=[p for p in parts if p[1] not in ('glass','window') and ('translucent' if p[1]=='glass' else 'cutout' if p[1] in ('window','bottom','top') else 'solid')==layer]
       if not subset:continue
       value=mesh(subset,tex);value['render_type']='minecraft:'+layer
       for e in value['elements']:
        for face,s in list(e['faces'].items()):
         # Do not expose caps where one glass volume was split across grid cells.
         axis,positive={'west':(0,False),'east':(0,True),'down':(1,False),'up':(1,True),'north':(2,False),'south':(2,True)}[face]
         plane=e['to' if positive else 'from'][axis]+(col*16,row*16,depth*16)[axis]
         # Clipping a full leaf into cells must not invent wood caps inside
         # its texture-cutout window. Those caps looked like dividing bars.
         if s['texture'] in ('#bottom','#top') and ((axis==1 and plane in (16,32)) or (axis==(2 if op else 0) and plane==16)):
          del e['faces'][face];continue
         if (s['texture']=='#glass' and axis==0 and plane==16) or (s['texture']=='#window' and plane==16 and (axis==1 or axis==(2 if op else 0))):
          del e['faces'][face];continue
         # Side-wall outer face alone is light; lower/front/back/bottom remain white.
         if s['texture'] in ('#white','#light'):s['uv']=[0,0,16,16]
         if s['texture']=='#glass' and face in ('north','south','east','west'):s['uv']=[0,48-e['to'][1]-row*16,16,48-e['from'][1]-row*16]
         if s['texture']=='#window' and face in (('west','east') if op else ('north','south')):
          axis=2 if op else 0;offset=depth*16 if op else col*16;lo=e['from'][axis]+offset;hi=e['to'][axis]+offset
          if right and not op:lo,hi=32-hi,32-lo
          u0,u1=(lo-8)*16/12,(hi-8)*16/12
          if face==('west' if op else 'north'):u0,u1=u1,u0
          s['uv']=[u0,(32-e['to'][1]-row*16)*16/26,u1,(32-e['from'][1]-row*16)*16/26]
         if s['texture'] in ('#bottom','#top'):
          from painting_and_editable_blocks.framed_door_texture_uv import leaf_face
          leaf_face(e,face,s,col,row,depth,op,right)
       value['elements']=[e for e in value['elements'] if e['faces']]
       if not value['elements']:continue
       key=json.dumps(value,sort_keys=True)
       if key not in shared:
        name=f'dining_door_wide_part_{len(shared)}';shared[key]=name;write(A/f'models/block/{name}.json',value)
       for turn,f in enumerate(('north','east','south','west')):
        multipart.append({'when':{'facing':f,'open':str(op).lower(),'hinge':'right' if right else 'left','row':str(row),'column':str(col),'depth':str(depth)},'apply':{'model':'lsmmod:block/'+shared[key],'y':turn*90}})
 from painting_and_editable_blocks.create_framed_door_glazing import add_visual_parts
 add_visual_parts(multipart,tex,write)
 write(A/'blockstates/dining_door_wide.json',{'multipart':multipart})
 # Keep opaque wood and both kinds of glass in their own inventory layers as well.
 layered_item('dining_door_wide',[(layer,[p for p in DINING_CLOSED if p[1] in materials]) for layer,materials in [('solid',('white','bottom','top')),('translucent',('glass',)),('cutout',('window',))]],tex,32,48)
 from painting_and_editable_blocks.framed_door_texture_uv import fix_item
 fix_item(write)
 from painting_and_editable_blocks.create_framed_door_glazing import item_transom
 item_transom(write)
 basic('dining_door_wide',controller={'column':'0','row':'0','depth':'0'})
 from painting_and_editable_blocks.create_framed_door_glazing import bathroom_variant
 bathroom_variant(multipart,tex,write)
 basic('bathroom_door_wide',controller={'column':'0','row':'0','depth':'0'})
 from windows_and_enclosures.create_infirmary_door_assets import generate,INFIRMARY_CLOSED,INFIRMARY_OPEN
 generate(multipart,tex,write,clipped,mesh,layered_item,basic)
 # One generated geometry source for physical cells and models.
 arrays={'STATUE':[b for b,*_ in statue],'TROPHIES':[b for b,*_ in trophies]+[[0,0,0,96,64,.125]],'MICROWAVE':[b for b,*_ in MICROWAVE],'DINING_CLOSED':[b for b,*_ in DINING_CLOSED],'DINING_OPEN':[b for b,*_ in DINING_OPEN],'INFIRMARY_CLOSED':[b for b,*_ in INFIRMARY_CLOSED],'INFIRMARY_OPEN':[b for b,*_ in INFIRMARY_OPEN]}
 java='package net.nicomar2009.lsmmod.block;\n\n/** Model-identical facility boxes; per-cell grids and rotations are cached. */\npublic final class SchoolFacilityShapes {\n private SchoolFacilityShapes() {}\n'
 for name,boxes in arrays.items():java+=' public static final double[][] '+name+' = '+json.dumps(boxes).replace('[','{').replace(']','}')+';\n'
 write(J/'modules/facilities/block/SchoolFacilityShapes.java',java+'}\n')
 registrations='''    public static final DeferredBlock<SchoolFramedDiningDoorBlock> DINING_DOOR_WIDE = BLOCKS.registerBlock("dining_door_wide", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> BATHROOM_DOOR_WIDE = BLOCKS.registerBlock("bathroom_door_wide", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> INFIRMARY_DOOR = BLOCKS.registerBlock("infirmary_door", props -> new SchoolFramedDiningDoorBlock(props,true), props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> DINING_DOOR_WIDE_LIGHT = BLOCKS.registerBlock("dining_door_wide_light", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolPassiveAccessoryBlock> SCHOOL_MICROWAVE = BLOCKS.registerBlock("school_microwave", props -> new SchoolPassiveAccessoryBlock(props,SchoolFacilityShapes.MICROWAVE), props -> props.strength(2F).sound(SoundType.METAL).noOcclusion());
    public static final DeferredBlock<SchoolTrophyGalleryBlock> SCHOOL_TROPHY_GALLERY = BLOCKS.registerBlock("school_trophy_gallery", SchoolTrophyGalleryBlock::new, props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));'''
 for filename,body in [('ModBlocks.java',registrations),('ModItems.java','\n'.join(f'    public static final DeferredItem<BlockItem> {i.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{i.upper()});' for i in IDS))]:
  p=J/'registry'/filename;s=module_read_text(p);a='    // BEGIN FACILITY EXPANSIONS';z='    // END FACILITY EXPANSIONS';section=a+'\n'+body+'\n'+z
  if a in s:start=s.index(a);end=s.index(z)+len(z);s=s[:start]+section+s[end:]
  else:s=s.replace('    // BEGIN KIOSK ENTRANCE ACCESSORIES',section+'\n\n    // BEGIN KIOSK ENTRANCE ACCESSORIES',1)
  if filename=='ModBlocks.java':s=s.replace('DeferredBlock<TwoTallBlock> SAN_MARTIN_DE_PORRES','DeferredBlock<SchoolSanMartinBlock> SAN_MARTIN_DE_PORRES').replace('"san_martin_de_porres",\n            TwoTallBlock::new','"san_martin_de_porres",\n            SchoolSanMartinBlock::new')
  write(p,s)
 for locale,names in [('es_es',['Puerta del comedor con marco (2×3)','Puerta del baño con marco (2×3)','Microondas del colegio','Galería de trofeos del colegio','Puerta de enfermería','Puerta del comedor con frente claro']),('en_us',['Framed Dining Door (2×3)','Framed Bathroom Door (2×3)','School Microwave','School Trophy Gallery','Infirmary Door','Dining Door with Light Front'])]:
  p=A/f'lang/{locale}.json';v=json.loads(module_read_text(p));v.update({'block.lsmmod.'+i:n for i,n in zip(IDS,names)});write(p,v)
 for tool,ids in [('axe',['dining_door_wide','bathroom_door_wide','school_trophy_gallery','infirmary_door','dining_door_wide_light']),('pickaxe',['school_microwave'])]:
  p=D/f'minecraft/tags/block/mineable/{tool}.json';v=json.loads(module_read_text(p));v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+i for i in ids]));write(p,v)
 module_write_text(Path('/tmp/facility-generated.json'), json.dumps(GENERATED))
 # Retire unused cells when changed geometry produces a different shared model count.
 for prefix in ('dining_door_wide_part_','bathroom_door_wide_part_','school_trophy_gallery_part_','infirmary_door_part_','dining_door_wide_light_part_'):
  for p in module_glob(A/'models/block', prefix+'*.json'):
   if str(p.relative_to(ROOT)) not in GENERATED:module_unlink(p)
if __name__=='__main__':main()
