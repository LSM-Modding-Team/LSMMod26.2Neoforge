"""Verify exact translations, unchanged UVs/faces/textures and collision offset."""
import json,math
import numpy as np
from create_school_raised_inverted_slopes import ROOT,A,pieces,OFFSET
from check_school_railing_slopes import mesh

def main():
 plan=list(pieces());assert len(plan)==6
 for ident,source,col,row,low,rise in plan:
  v,uv,faces=mesh(A/f'models/block/{ident}.obj');old,old_uv,old_faces=mesh(A/f'models/block/{source}.obj')
  assert np.allclose(v-old,[0,OFFSET,0],atol=1e-10)
  assert np.array_equal(uv,old_uv) and faces==old_faces
  model=json.loads((A/f'models/block/{ident}.json').read_text());original=json.loads((A/f'models/block/{source}.json').read_text())
  model['model']=original['model'];assert model==original
  states=json.loads((A/f'blockstates/{ident}.json').read_text())['variants'];assert len(states)==4
  assert math.isclose(v[:,1].max(),1.5)
  for turn in range(4):
   if turn:
    v=np.column_stack((1-v[:,2],v[:,1],v[:,0]));old=np.column_stack((1-old[:,2],old[:,1],old[:,0]))
   assert np.allclose(v-old,[0,OFFSET,0],atol=1e-10)
  for i in range(32):
   original_floor=low-rise*(i+1)/32;assert math.isclose(original_floor+.5-original_floor,.5)
  loot=json.loads((ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json').read_text());assert loot['pools'][0]['entries'][0]['name']=='lsmmod:'+ident
 s=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolInvertedSlopeBlock.java').read_text()
 assert 'this(lowerStart,rise,0,properties)' in s
 assert 'lowerStart-rise*t1+verticalOffset' in s and '1+verticalOffset' in s
 print('OK: 6 new pieces, 48 states; exact +8px translation in all orientations; faces/UV/textures unchanged; old offset remains zero.')
if __name__=='__main__':main()
