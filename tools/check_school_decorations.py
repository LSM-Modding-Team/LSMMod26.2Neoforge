"""Check mesh bounds, depth, centred rotor and light switching without compiling."""
import json,math
import numpy as np
from PIL import Image
from create_school_decorations import ROOT,A,UV

def mesh(name):
 vertices=[];uv=[];faces=[];material=None
 for line in (A/f'models/block/{name}.obj').read_text().splitlines():
  s=line.split()
  if not s:continue
  if s[0]=='v':vertices.append(tuple(map(float,s[1:])))
  elif s[0]=='vt':uv.append(tuple(map(float,s[1:])))
  elif s[0]=='usemtl':material=s[1]
  elif s[0]=='f':faces.append((material,[int(v.split('/')[0])-1 for v in s[1:]]))
 return np.array(vertices),np.array(uv),faces

def main():
 atlas=Image.open(A/'textures/block/school_decoration_surface.png')
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
  states=json.loads((A/f'blockstates/{name}.json').read_text())['variants'];assert len(states)==4
 states=json.loads((A/'blockstates/ceiling_light.json').read_text())['variants'];assert set(states)=={'lit=true','lit=false'}
 java=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/CeilingLightBlock.java').read_text()
 assert 'state.cycle(LIT)' in java and '!level.isClientSide()' in java and 'setValue(LIT,true)' in java
 registry=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java').read_text();assert 'CeilingLightBlock.LIT) ? 15 : 0' in registry
 print('OK: clock exact 2px depth, fan blades centred at (8,8), ceiling mount bounds, no degenerate triangles, correct palette UVs; four wall orientations, lit 15/off 0, server-only right-click toggle.')
if __name__=='__main__':main()
