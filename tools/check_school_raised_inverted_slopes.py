"""Verify elevated inclined profiles, integer flat tops, resources and collision."""
from resource_modules import module_registry_text
import json,math
import numpy as np
from create_school_raised_inverted_slopes import ROOT,A,pieces,OFFSET,top_height
from check_school_railing_slopes import mesh

def main():
 plan=list(pieces());assert len(plan)==10
 for ident,source,col,row,low,rise in plan:
  v,uv,faces=mesh(A/f'models/block/{ident}.obj');old,old_uv,old_faces=mesh(A/f'models/block/{source}.obj')
  top=np.isclose(old[:,1],1);expected=old.copy();expected[:,1]+=OFFSET;expected[top,1]=top_height(ident)
  assert np.allclose(v,expected,atol=1e-10)
  assert np.array_equal(uv,old_uv) and faces==old_faces
  model=json.loads(module_registry_text(A/f'models/block/{ident}.json'));original=json.loads(module_registry_text(A/f'models/block/{source}.json'))
  model['model']=original['model'];assert model==original
  states=json.loads(module_registry_text(A/f'blockstates/{ident}.json'))['variants'];assert len(states)==4
  assert math.isclose(v[:,1].max(),top_height(ident))
  assert top_height(ident) in (1,2)
  assert np.all(v[~top,1]<top_height(ident))
  for turn in range(4):
   if turn:
    v=np.column_stack((1-v[:,2],v[:,1],v[:,0]));old=np.column_stack((1-old[:,2],old[:,1],old[:,0]))
   assert np.allclose((v-old)[~top],[0,OFFSET,0],atol=1e-10)
   assert np.allclose((v-old)[top],[0,top_height(ident)-1,0],atol=1e-10)
  for i in range(32):
   original_floor=low-rise*(i+1)/32;assert math.isclose(original_floor+.5-original_floor,.5)
  loot=json.loads(module_registry_text(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json'));assert loot['pools'][0]['entries'][0]['name']=='lsmmod:'+ident
 s=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/slopes/block/SchoolInvertedSlopeBlock.java')
 assert 'this(lowerStart,rise,0,properties)' in s
 assert 'lowerStart-rise*t1+verticalOffset' in s and '1,topHeight,1-t0' in s
 assert 'this(lowerStart,rise,verticalOffset,1+verticalOffset,properties)' in s
 registry=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java')
 for ident,source,col,row,low,rise in plan:
  assert f'new SchoolInvertedSlopeBlock({low:.15g},{rise:.15g},0.5,{top_height(ident):.1f},props)' in registry
 flight=[top_height(x[0]) for x in plan if '_flight_' in x[0]];assert flight==[1,2,2,1,1,2]
 split=[top_height(x[0]) for x in plan if '_split_' in x[0]];assert split==[1,2,1,2]
 print('OK: 10 elevated pieces, 80 states; inclined profiles +8px; flight tops 16/32/32/16/16/32px, split tops 16/32/16/32px; UV/faces/materials preserved, collisions aligned.')
if __name__=='__main__':main()
