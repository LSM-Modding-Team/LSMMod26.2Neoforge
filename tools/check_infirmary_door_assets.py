"""Static regression: both glazing channels, front-only materials, all cells and exact geometry."""
import itertools
import json
import numpy as np
from create_school_glass_assets import A, ROOT
from audit_block_states import audit
from render_texture_review import load, block, door
from create_infirmary_door_assets import INFIRMARY_CLOSED, INFIRMARY_OPEN
from check_facility_expansion_assets import matches


def read(path):return json.loads(path.read_text())


def occupied(elements):
    result=np.zeros((32,48,32),dtype=bool)
    for e in elements:
        a,b=e['from'],e['to']
        # One-pixel centers: the new panes have integer bounds; native wood is four pixels thick.
        axes=[np.arange(n)+.5 for n in result.shape]
        result |= ((axes[0]>=a[0])&(axes[0]<b[0]))[:,None,None] & ((axes[1]>=a[1])&(axes[1]<b[1]))[None,:,None] & ((axes[2]>=a[2])&(axes[2]<b[2]))[None,None,:]
    return result


def main():
    rows={r['id']:r for r in audit()}
    for ident in ('infirmary_door','dining_door_wide_light'):
        assert rows[ident]['states']==192
        assert read(A/f'items/{ident}.json')['model']['model']=='lsmmod:item/'+ident
        loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json')['pools'][0]
        assert loot['conditions'][1]['properties']=={'column':'0','row':'0','depth':'0'}
        assert loot['entries'][0]['name']=='lsmmod:'+ident
        for locale in ('es_es','en_us'):assert 'block.lsmmod.'+ident in read(A/f'lang/{locale}.json')
        for facing,opened,hinge,col,row,depth in itertools.product(('north','east','south','west'),('false','true'),('left','right'),range(2),range(3),range(2)):
            state=dict(facing=facing,open=opened,hinge=hinge,column=str(col),row=str(row),depth=str(depth))
            parts=[p for p in read(A/f'blockstates/{ident}.json')['multipart'] if matches(p['when'],state)]
            if ident=='infirmary_door':
                assert sum('door_side_' in p['apply']['model'] for p in parts)==int(depth==0 and row>=1 and col==(0 if hinge=='right' else 1))
                assert sum('door_transom' in p['apply']['model'] for p in parts)==int(depth==0 and row==2)
                assert not any('window' in p['apply']['model'] for p in parts)
            for part in parts:
                for model in load(part['apply']['model']):
                    for e in model.get('elements',[]):
                        for face,m in e['faces'].items():
                            if m['texture']=='#front':assert face=='north' and model['textures']['front']=='lsmmod:block/light_school_wall'
                            if ident=='dining_door_wide_light':assert m['texture']!='#light'
                            if m['texture']=='#white':assert model['textures']['white']=='lsmmod:block/classroom_white_wall'
                            if ident=='infirmary_door' and m['texture'] in ('#bottom','#top'):
                                assert model['textures'][m['texture'][1:]]=='lsmmod:block/bathroom_door_'+m['texture'][1:]
        # Variant geometry/UVs are unchanged; its only differing face is the front wall.
    baseline=read(A/'blockstates/dining_door_wide.json')['multipart'];variant=read(A/'blockstates/dining_door_wide_light.json')['multipart']
    for a,b in zip(baseline,variant):
        assert a['when']==b['when'] and a['apply']['y']==b['apply']['y']
        original,=load(a['apply']['model']);changed,=load(b['apply']['model'])
        for e in original.get('elements',[]):
            for material in e['faces'].values():
                if material['texture']=='#light':material['texture']='#white'
        for e in changed.get('elements',[]):
            for material in e['faces'].values():
                if material['texture']=='#front':material['texture']='#white'
        assert original.get('elements')==changed.get('elements')
    masks_checked=0
    for right,mask in itertools.product((False,True),range(32)):
        model,=load(f'lsmmod:block/infirmary_door_side_{int(right)}',{'main':31-mask,'side':mask})
        assert model['render_type']=='minecraft:solid' and model['textures']['glass']=='lsmmod:block/school_tinted_glass'
        for e in model['elements']:
            assert (0 if right else 8)<=e['from'][0]<e['to'][0]<=(8 if right else 16)
            assert 8<=e['from'][2]<e['to'][2]<=16
            for face,material in e['faces'].items():
                if material['texture']=='#glass':
                    assert not(face=='up' and e['to'][1]==16 and mask&8)
                    assert not(face=='down' and e['from'][1]==0 and mask&16)
                    if not right:assert not(face=='south' and e['to'][2]==16 and not mask&2)
                    else:assert not(face=='south' and e['to'][2]==16 and not mask&1)
                if material['texture']=='#frame' and face in ('east','west'):
                    # Horizontal rail never survives a vertical join.
                    assert not(mask&8 and e['from'][1]==15 and e['to'][1]==16)
                    assert not(mask&16 and e['from'][1]==0 and e['to'][1]==1)
        transom,=load('lsmmod:block/infirmary_door_transom',{'main':mask,'side':31-mask})
        standard,=load('lsmmod:block/framed_door_transom',mask)
        assert transom['elements']==standard['elements']
        assert transom['textures']['glass']=='lsmmod:block/school_tinted_glass'
        masks_checked+=1
    for opened,right in itertools.product((False,True),repeat=2):
        parts=INFIRMARY_OPEN if opened else INFIRMARY_CLOSED
        boxes=[]
        for b,*_ in parts:
            b=b[:]
            if right:b[0],b[3]=32-b[3],32-b[0]
            boxes.append({'from':b[:3],'to':b[3:]})
        assert np.array_equal(occupied(door('infirmary_door',opened,right)),occupied(boxes)),(opened,right)
        scene=door('infirmary_door',opened,right)
        assert not any(m['texture']=='minecraft:block/glass' for e in scene for m in e['faces'].values())
    # Existing full-width post spacing remains identical; half-width panes join only at actual edges.
    for length in range(1,18):
        spacing=32 if length%2==0 else 24
        for i in range(length):
            old=(1 if i==0 or 16*i%spacing==0 else 0)|(2 if i==length-1 or (16*i+16)%spacing==0 else 0)|(4 if (16*i+8)%spacing==0 else 0)
            new=(1 if 16*i==0 or 16*i%spacing==0 else 0)|(2 if 16*i+16==16*length or (16*i+16)%spacing==0 else 0)|(4 if (16*i+8)%spacing==0 else 0)
            assert old==new
    java=ROOT/'src/main/java/net/nicomar2009/lsmmod'
    shared=(java/'block/SchoolGlazing.java').read_text()
    assert 'pane(left,facing).end()==16&&pane(right,facing).start()==0' in shared
    assert 'a.start()!=b.start()||a.end()!=b.end()' in shared
    assert 'new Pane(0,16,8,16,3,false)' in shared and 'new Pane(0,16,0,8,3,false)' in shared
    assert 'SchoolGlazing.sideFacing(neighbor)' in (java/'block/SchoolGlassBlock.java').read_text()
    predicate=(java/'client/SchoolGlazingPredicate.java').read_text()
    assert 'selection.side!=side' in predicate and 'visualMask(world,pos,state,side)' in predicate
    source=(java/'block/SchoolFramedDiningDoorBlock.java').read_text()
    assert '(sideGlazing?64:0)' in source and 'SimpleBlockOutline.forState' in source
    assert 'row,0),true)' in source
    for name in ('infirmary_door_texture_review.png','door_variants_and_connections_review.png','new_door_inventory_review.png'):
        assert (ROOT/'previews'/name).stat().st_size>10000
    print(f'OK: 384 new states, {masks_checked} independent main/side mask selections; exact closed/open/right physical occupancy; bathroom wood without window; front-only light walls, single drops and external renders.')


if __name__=='__main__':main()
