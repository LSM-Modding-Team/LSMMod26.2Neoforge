"""Validate the three missing columns and continuity of the complete 7x5 profile."""
from resource_modules import module_registry_text
import json,math
import numpy as np
from create_school_split_raised_extras import ROOT,A,pieces
from create_school_raised_inverted_slopes import pieces as existing
from check_school_railing_slopes import mesh

def main():
 extras=list(pieces());assert len(extras)==3
 assert [(p[1],p[2],p[5]) for p in extras]==[(2,-2,2),(3,-3,2),(4,-3,1)]
 blocks=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java')
 items=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModItems.java')
 complete=[(ident,col,row,low,rise) for ident,source,col,row,low,rise in existing() if '_split_' in ident]
 for ident,col,row,low,rise,top in extras:
  complete.append((ident,col,row,low,rise))
  v,uv,faces=mesh(A/f'models/block/{ident}.obj')
  assert math.isclose(v[:,1].max(),top)
  assert len(faces)==6 and len(v)==24
  floor=low+.5-rise*(1-v[:,2]);assert np.all(v[:,1]>=floor-1e-10)
  assert np.all(np.isclose(v[:,1],top)|np.isclose(v[:,1],floor))
  assert f'new SchoolInvertedSlopeBlock({low:.15g},{rise:.15g},0.5,{top:.1f},props)' in blocks
  assert f'ModBlocks.{ident.upper()}' in items
  model=json.loads(module_registry_text(A/f'models/block/{ident}.json'));assert model['textures']['wall']=='lsmmod:block/light_school_wall'
  assert model['model']==f'lsmmod:models/block/{ident}.obj'
  states=json.loads(module_registry_text(A/f'blockstates/{ident}.json'))['variants'];assert len(states)==4
  assert all(s['model']=='lsmmod:block/'+ident for s in states.values())
  assert json.loads(module_registry_text(A/f'items/{ident}.json'))['model']['model']=='lsmmod:item/'+ident
  assert json.loads(module_registry_text(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json'))['pools'][0]['entries'][0]['name']=='lsmmod:'+ident
  for turn in range(4):
   assert math.isclose(v[:,1].max(),top)
   v=np.column_stack((1-v[:,2],v[:,1],v[:,0]))
 complete.sort(key=lambda p:p[1]);assert [p[1] for p in complete]==list(range(7))
 for ident,col,row,low,rise in complete:
  assert math.isclose(row+low+.5,.5-5*col/7)
 for left,right in zip(complete,complete[1:]):
  assert math.isclose(left[2]+left[3]+.5-left[4],right[2]+right[3]+.5)
 print('OK: 3 extras, 24 states, continuous 7-column / 5-block profile; columns 2/3/4, rows -2/-3/-3, tops 32/32/16px; models/collision/items/drops match.')
if __name__=='__main__':main()
