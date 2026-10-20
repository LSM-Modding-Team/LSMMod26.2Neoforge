"""Check mesh bounds, depth, centred rotor and light switching without compiling."""
from resource_modules import module_path, module_registry_text
import json,math
import numpy as np
from PIL import Image
from create_school_decorations import ROOT,A,UV

def mesh(name):
 vertices=[];uv=[];faces=[];material=None
 for line in module_registry_text(A/f'models/block/{name}.obj').splitlines():
  s=line.split()
  if not s:continue
  if s[0]=='v':vertices.append(tuple(map(float,s[1:])))
  elif s[0]=='vt':uv.append(tuple(map(float,s[1:])))
  elif s[0]=='usemtl':material=s[1]
  elif s[0]=='f':faces.append((material,[int(v.split('/')[0])-1 for v in s[1:]]))
 return np.array(vertices),np.array(uv),faces


def check_name_sign():
 from wall_blocks.create_school_name_sign import ID,TEXT,LINES,DENSITY,WIDTH,HEIGHT,lettering,complete_model
 from wall_blocks.render_school_name_sign import scene
 from render_texture_review import add,load,render
 from audit_block_states import domains
 assert TEXT=='COLEGIO LIBERTADOR SAN MARTIN' and LINES==('COLEGIO','LIBERTADOR SAN MARTIN') and TEXT.isascii()
 assert domains('SchoolNameSignBlock')=={'facing':4,'column':10,'row':3}
 variants=json.loads(module_registry_text(A/f'blockstates/{ID}.json'))['variants'];assert len(variants)==120
 reference=lettering()
 coverage=np.zeros((HEIGHT*DENSITY,WIDTH*DENSITY),dtype=int)
 for row in range(3):
  for column in range(10):
   path=A/f'models/block/{ID}_{row}_{column}.json';model=json.loads(module_registry_text(path))
   for element in model['elements']:
    lo,hi=element['from'],element['to']
    assert all(0<=lo[axis]<hi[axis]<=16 for axis in range(3)) and lo[2]==15 and hi[2]==16
    if 'north' in element['faces']:
     x0=int(round((WIDTH-column*16-hi[0])*DENSITY));x1=int(round((WIDTH-column*16-lo[0])*DENSITY))
     y0=int(round((HEIGHT-row*16-hi[1])*DENSITY));y1=int(round((HEIGHT-row*16-lo[1])*DENSITY))
     coverage[y0:y1,x0:x1]+=1
   for turn,facing in enumerate(('north','east','south','west')):
    assert variants[f'column={column},row={row},facing={facing}']=={'model':f'lsmmod:block/{ID}_{row}_{column}','y':turn*90}
 assert np.max(coverage)==1 and np.array_equal(coverage>0,np.asarray(reference)>0), 'Split cells drop, overlap or mirror letters'
 complete=complete_model(reference);expected=[];add(expected,[complete])
 assembled=scene(False)
 for angle in (-90,-65,90):
  args=dict(center=[80,24,15.5],scale=4,size=(640,320),azimuth=angle,elevation=12,minecraft_orientation=True)
  actual=np.asarray(render(assembled,**args)).astype(int);whole=np.asarray(render(expected,**args)).astype(int)
  delta=np.max(np.abs(actual-whole),axis=2)
  assert np.mean(delta)<.2 and np.count_nonzero(delta>3)<150,('sign seams',angle,np.mean(delta))
 item=load('lsmmod:item/'+ID)[0]
 assert len(item['elements'])==len(complete['elements'])
 for leaf,original in zip(item['elements'],complete['elements']):
  for key in ('from','to'):
   x,y,z=leaf[key];assert np.allclose([x*10,24+(y-8)*10,15.5+(z-8)*10],original[key])
  assert leaf['faces']==original['faces']
 loot=json.loads(module_registry_text(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ID}.json'))
 assert loot['pools'][0]['conditions'][1]['properties']=={'column':'0','row':'0'}
 print('OK: exact ASCII wording, 30 cells and 4 orientations, glyph coverage/reading direction match the reference, 1px relief, seam-free front/back renders, undistorted item and single controller drop. Placement/client behavior still needs Minecraft.')

def main():
 atlas=Image.open(module_path(A/'textures/block/school_decoration_surface.png'))
 colors={'metal':(95,98,94),'blade':(144,147,143),'rim':(168,168,157),'body':(219,221,215),'hub':(67,71,69),'on':(240,242,237),'off':(155,160,155)}
 for material,(u,v) in UV.items():assert atlas.getpixel((int(u*16),int(v*16)))==colors[material]
 for name in ('school_clock','school_fan','ceiling_light_on','ceiling_light_off'):
  vertices,uv,faces=mesh(name)
  assert np.all(vertices>=-1e-10) and np.all(vertices<=1+1e-10)
  for material,indices in faces:
   pts=vertices[indices];assert np.linalg.norm(np.cross(pts[1]-pts[0],pts[2]-pts[0]))>1e-14
   if material!='clock':
    u,v=uv[indices[0]];assert atlas.getpixel((int(u*16),int((1-v)*16)))==colors[material]
  if name=='school_clock':assert math.isclose(vertices[:,2].max()-vertices[:,2].min(),2/16)
  if name=='school_fan':
   blade_indices=[i for material,indices in faces if material=='blade' for i in indices];blades=vertices[blade_indices]
   assert np.allclose(blades[:,:2].mean(axis=0),[.5,.5],atol=1e-10)
  if name.startswith('ceiling'):assert vertices[:,1].max()==1 and vertices[:,1].min()==13.5/16
 for name in ('school_clock','school_fan'):
  states=json.loads(module_registry_text(A/f'blockstates/{name}.json'))['variants'];assert len(states)==4
 states=json.loads(module_registry_text(A/'blockstates/ceiling_light.json'))['variants'];assert set(states)=={'lit=true','lit=false'}
 java=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/lighting/block/CeilingLightBlock.java')
 assert 'state.cycle(LIT)' in java and '!level.isClientSide()' in java and 'setValue(LIT,true)' in java
 registry=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java');assert 'CeilingLightBlock.LIT) ? 15 : 0' in registry
 print('OK: clock exact 2px depth, fan blades centred at (8,8), ceiling mount bounds, no degenerate triangles, correct palette UVs; four wall orientations, lit 15/off 0, server-only right-click toggle.')
if __name__=='__main__':
 import sys
 if '--name-sign-only' in sys.argv:check_name_sign()
 else:main()
