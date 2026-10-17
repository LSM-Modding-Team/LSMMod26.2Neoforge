"""Validate the material boundary, UV continuity and four orientations without compiling."""
from resource_modules import module_path, module_read_text
import json
from PIL import Image
from create_tiled_parquet_floor import ROOT,A,ID


def main():
    model=json.loads(module_read_text(A/f'models/block/{ID}.json'))
    assert model['textures']=={'tile':'lsmmod:block/classroom_floor','wood':'lsmmod:block/school_parquet','particle':'#tile'}
    tile=Image.open(module_path(A/'textures/block/classroom_floor.png'));wood=Image.open(module_path(A/'textures/block/school_parquet.png'))
    assert tile.size==wood.size==(16,16)
    # Each projected surface has complete, non-overlapping coverage of the cube face.
    normals={'down':(1,0),'up':(1,16),'north':(2,0),'south':(2,16),'west':(0,0),'east':(0,16)}
    for face,(axis,plane) in normals.items():
        axes=[i for i in range(3) if i!=axis]
        for u in range(16):
            for v in range(16):
                covering=[]
                for e in model['elements']:
                    if face not in e['faces']:continue
                    boundary=e['from'][axis] if face in ('down','north','west') else e['to'][axis]
                    assert boundary==plane
                    if all(e['from'][a]<=p+.5<e['to'][a] for a,p in zip(axes,(u,v))):covering.append(e)
                assert len(covering)==1,(face,u,v)
                surface=covering[0]['faces'][face]
                assert surface['cullface']==face
                assert surface['texture']==('#wood' if face=='up' and u>=12 else '#tile')
    tops=[e for e in model['elements'] if 'up' in e['faces']]
    assert [e['faces']['up']['uv'] for e in tops]==[[0,0,12,16],[12,0,16,16]]
    states=json.loads(module_read_text(A/f'blockstates/{ID}.json'))['variants']
    assert states=={f'facing={side}':{'model':'lsmmod:block/'+ID,'y':angle} for side,angle in [('east',0),('south',90),('west',180),('north',270)]}
    from audit_block_states import audit
    row=next(r for r in audit() if r['id']==ID);assert row['states']==4
    java=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/building_blocks/block/FloorTransitionBlock.java')
    assert 'getHorizontalDirection().getClockWise()' in java and 'BlockEntity' not in java
    loot=json.loads(module_read_text(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ID}.json'))
    assert loot['pools'][0]['entries']==[{'type':'minecraft:item','name':'lsmmod:'+ID}]
    print('OK: seven non-overlapping surface regions, 12px tile + 4px parquet top, original UV scales, four orientations, full cube and item/drop resources.')


if __name__=='__main__':main()
