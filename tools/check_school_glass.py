"""Static checks only: door profile, stair resources/materials and shared glass joins."""
import json
from PIL import Image
from create_school_glass_assets import A,D,ROOT
from create_school_glass_stair_assets import QUARTERS

def read(p):return json.loads(p.read_text())

def main():
    names={'school_glass':16,'supported_school_glass':320,'mixed_school_glass':320,'supported_school_glass_reversed':320,'mixed_school_glass_reversed':320,'tall_classroom_entrance':384}
    for name,count in names.items():
        definition=read(A/f'blockstates/{name}.json')
        variants=definition.get('variants')
        if variants is None:
            variants={','.join(f'{k}={v}' for k,v in p['when'].items()):p['apply'] for p in definition['multipart'] if 'curtain' not in p['when']}
        assert len(variants)==count*8
        for key,value in variants.items():
            state=dict(x.split('=') for x in key.split(','))
            model=read(A/('models/'+value['model'].split(':')[1]+'.json'))
            for e in model['elements']:
                assert all(0<=e['from'][a]<e['to'][a]<=16 for a in range(3))
                for f in e['faces'].values():assert f['texture'][1:] in model['textures']
            if 'school_glass' in name and name!='school_glass':
                assert state['half'] in ('top','bottom') and state['shape'] in QUARTERS
                assert state['waterlogged'] in ('true','false')
            if name=='tall_classroom_entrance' and state['row']=='2':
                assert 'tall_classroom_transom' in value['model']
    for col in range(2):
        for row in range(2):
            model=read(A/f'models/block/tall_classroom_entrance_{col}_{row}.json')
            assert len(model['elements'])==1
            assert model['elements'][0]['from']==[0,0,0] and model['elements'][0]['to']==[16,16,4]
            closed=model['elements'][0]
            opened=read(A/f'models/block/tall_classroom_entrance_{col}_{row}_open.json')['elements'][0]
            assert opened['from'][0]==(0 if col==0 else 12) and opened['to'][0]==(4 if col==0 else 16)
            # Original handle's x13.5 maps toward the center on both sides.
            for face in ('north','south'):
                uv=closed['faces'][face]['uv'];t=(13.5-uv[0])/(uv[2]-uv[0]);x=16*(1-t) if face=='north' else 16*t
                assert (x>8) if col==0 else (x<8)
    for col in range(2):
        for top in (0,1):
            for opened in (0,1):
                model=read(A/f'models/block/tall_classroom_transom_{col}_{opened}_{top}_0.json')
                apron=next(e for e in model['elements'] if e['faces'].get('north',{}).get('texture')=='#top' or any(f['texture']=='#top' for f in e['faces'].values()))
                assert apron['from']==([0,0,0] if not opened or col==0 else [12,0,0])
                assert apron['to']==([16,8,4] if not opened else [4,8,16] if col==0 else [16,8,16])
                glass=next(e for e in model['elements'] if e['faces']['north']['texture']=='#glass')
                assert glass['from'][1]==9 and glass['to'][1]==(16 if top else 15)
                assert glass['to'][2]-glass['from'][2]==8
    for name,under,side in [('supported_school_glass','light_wall','support'),('mixed_school_glass','concrete','light_wall'),('supported_school_glass_reversed','support','light_wall'),('mixed_school_glass_reversed','light_wall','concrete')]:
        model=read(A/f'models/block/{name}.json')
        assert model['textures']['light_wall']=='lsmmod:block/light_school_wall'
        assert model['textures']['support']=='lsmmod:block/dark_school_wall'
        assert model['textures']['concrete']=='minecraft:block/white_concrete'
        assert any(f['texture']=='#'+under for e in model['elements'] for f in e['faces'].values())
        assert any(f['texture']=='#'+side for e in model['elements'] for f in e['faces'].values())
        for shape,quarters in QUARTERS.items():
            # Expected physical volumes from vanilla's half slab plus quarter-step arrangement.
            assert 2048+512*len(quarters)=={'straight':3072,'outer_left':2560,'outer_right':2560,'inner_left':3584,'inner_right':3584}[shape]
    for original,reversed_id in [('supported_school_glass','supported_school_glass_reversed'),('mixed_school_glass','mixed_school_glass_reversed')]:
        for half in ('bottom','top'):
            for shape in QUARTERS:
                for top in (0,1):
                    for bottom in (0,1):
                        regular=read(A/f'models/block/{original}_{half}_{shape}_{top}_{bottom}.json')
                        swapped=read(A/f'models/block/{reversed_id}_{half}_{shape}_{top}_{bottom}.json')
                        def glass_frame(m):
                            return sorted(json.dumps(e,sort_keys=True) for e in m['elements'] if any(f['texture'] in ('#glass','#frame') for f in e['faces'].values()))
                        assert glass_frame(regular)==glass_frame(swapped)
                        assert regular['textures']['glass']=='lsmmod:block/school_glass'
        # Joined straight support meets the standalone glass with the exact same physical posts.
        joined=read(A/f'models/block/{original}_bottom_straight_1_0.json')
        glass_faces=[e for e in joined['elements'] if any(f['texture']=='#glass' for f in e['faces'].values())]
        assert max(e['to'][1] for e in glass_faces)==16
        assert min(e['from'][0] for e in glass_faces)==1 and max(e['to'][0] for e in glass_faces)==15
        inverted=read(A/f'models/block/{original}_top_straight_0_1.json')
        glass_faces=[e for e in inverted['elements'] if any(f['texture']=='#glass' for f in e['faces'].values())]
        assert min(e['from'][1] for e in glass_faces)==0
    stair=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolGlassStairBlock.java').read_text()
    assert 'extends StairBlock' in stair and 'super.getStateForPlacement(context)' in stair
    assert 'super.updateShape' in stair and 'return super.getShape' in stair and 'SimpleBlockOutline.forState' in stair
    joins=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolGlazing.java').read_text()
    assert 'footprint(state)!=footprint(neighbor)' in joins and 'reachesTop(state)&&reachesBottom(neighbor)' in joins
    loot=read(D/'lsmmod/loot_table/blocks/tall_classroom_entrance.json')
    assert loot['pools'][0]['conditions'][1]['properties']=={'row':'0'}
    print('OK: edge-aligned 2-row door + moving third-row apron; centered handles; 13440 base states plus curtain overlays; four vanilla stairs (corners/top/waterlogging), material layout, shared glazing and single door drop.')

if __name__=='__main__':main()

# Regression: transparent panes must not remove the inner faces of opaque posts.
for name in ('supported_school_glass','mixed_school_glass','supported_school_glass_reversed','mixed_school_glass_reversed'):
    for half in ('bottom','top'):
        for top in (0,1):
            for bottom in (0,1):
                m=json.loads((A/f'models/block/{name}_{half}_straight_{top}_{bottom}.json').read_text())
                y0,y1=(9,15) if half=='bottom' else (1,7)
                for face,x in (('east',1),('west',15)):
                    inner=[e for e in m['elements'] if face in e['faces'] and e['faces'][face]['texture']=='#frame'
                           and (e['to'][0] if face=='east' else e['from'][0])==x]
                    assert any(e['from'][1]<=y0 and e['to'][1]>=y1 and e['from'][2]==0 and e['to'][2]==8 for e in inner),(name,half,face)
    # Normal stair, standalone pane, inverted stair: no transverse frame at either seam.
    for half,top,bottom,seam,face in (('bottom',1,0,16,'up'),('top',0,1,0,'down')):
        m=json.loads((A/f'models/block/{name}_{half}_straight_{top}_{bottom}.json').read_text())
        for e in m['elements']:
            if face in e['faces'] and e['faces'][face]['texture']=='#frame' and (e['to'][1] if face=='up' else e['from'][1])==seam:
                # Only the outer side posts can terminate at the connected boundary.
                assert e['to'][0]<=1 or e['from'][0]>=15,(name,half,e)
print('OK: visible inner frame faces retained through transparent glass in all four stairs, both halves and vertical joins.')
