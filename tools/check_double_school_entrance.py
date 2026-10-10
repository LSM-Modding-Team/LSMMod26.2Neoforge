"""Static tests for dimensions, midpoint split, UV clipping, opening and one-item drops."""
import copy,itertools,json
from PIL import Image
from create_double_school_entrance import A,ROOT,NAME,wood,crop,cell_model

def boxes(m):return [e['from']+e['to'] for e in m['elements']]
def volume(b):return (b[3]-b[0])*(b[4]-b[1])*(b[5]-b[2])
def intersect(a,b):return all(max(a[i],b[i])<min(a[i+3],b[i+3]) for i in range(3))
def read(p):return json.loads(p.read_text())
def main():
    closed=wood(False);opened=wood(True)
    assert sum(volume(b) for b in boxes({'elements':closed}))==48*40*4
    assert sum(volume(b) for b in boxes({'elements':opened}))==48*40*4
    assert any(e['to'][0]==24 for e in closed) and any(e['from'][0]==24 for e in closed)
    assert not any(e['from'][0]<24<e['to'][0] for e in closed)
    assert all(e['to'][0]<=4 or e['from'][0]>=44 for e in opened)
    assert max(e['to'][2] for e in opened)==24
    assert max(e['to'][1] for e in opened)==40
    assert not any(intersect(a,b) for i,a in enumerate(boxes({'elements':opened})) for b in boxes({'elements':opened})[i+1:])
    # Cell partition conserves every piece and UV density, with no invented internal caps.
    for is_open in (False,True):
        original=wood(is_open);total=0
        for col,row,depth in itertools.product(range(3),range(3),range(2)):
            for e in original:
                part=crop(e,[col*16,row*16,depth*16,(col+1)*16,(row+1)*16,(depth+1)*16])
                if part:total+=volume(part['from']+part['to'])
        assert total==sum(volume(e['from']+e['to']) for e in original)
    # A physical quarter turn keeps both hinges stationary and maps the midline to z24.
    def turn(point,leaf):
        x,y,z=point
        return (4-z,y,x) if leaf==0 else (44+z,y,48-x)
    assert turn((2,0,2),0)==(2,0,2) and turn((46,0,2),1)==(46,0,2)
    assert turn((24,0,2),0)[2]==turn((24,0,2),1)[2]==24
    # Exactly two handle patches, in the central panel; no outer-panel handles remain.
    for col in range(3):
        im=Image.open(A/f'textures/block/{NAME}_{col}_0.png').convert('RGBA')
        metallic={(x,y) for y in range(16) for x in range(16) if im.getpixel((x,y))[1]>100}
        assert metallic==({(x,y) for x in (5,6,9,10) for y in range(6)} if col==1 else set())
    top=Image.open(A/'textures/block/classroom_door_top.png').convert('RGBA')
    window={(x,y):top.getpixel((x,y)) for y in range(16) for x in range(16)
            if top.getpixel((x,y))[1]>100 or top.getpixel((x,y))[3]<255}
    for col,offset in ((0,4),(2,-4)):
        image=Image.open(A/f'textures/block/{NAME}_{col}_1.png').convert('RGBA')
        actual={(x,y):image.getpixel((x,y)) for y in range(16) for x in range(16)
                if image.getpixel((x,y))[1]>100 or image.getpixel((x,y))[3]<255}
        assert actual=={(x+offset,y-2):color for (x,y),color in window.items()}
    apron=[e for e in wood(False) if e['from'][1]==32]
    assert all(e['faces']['north']['uv'][0]>e['faces']['north']['uv'][2] for e in apron)
    checked=0;seen=set()
    states=read(A/f'blockstates/{NAME}.json')['variants'];assert len(states)==9216
    for key,v in states.items():
        state=dict(p.split('=') for p in key.split(','));stem=v['model'].split(':')[1]
        assert v['y']=={'north':0,'east':90,'south':180,'west':270}[state['facing']]
        if stem in seen:continue
        seen.add(stem);m=read(A/f'models/{stem}.json')
        for e in m['elements']:
            b=e['from']+e['to'];assert all(0<=b[i]<b[i+3]<=16 for i in range(3))
            for f in e['faces'].values():
                assert all(0<=n<=16 for n in f['uv'])
                assert (A/f"textures/{m['textures'][f['texture'][1:]].split(':')[1]}.png").is_file()
        if state['row']=='2' and state['depth']=='0':
            fixed=[e for e in m['elements'] if all(f['texture'] in ('#glass','#frame') for f in e['faces'].values())]
            assert fixed and all(e['from'][1]>=8 and e['to'][2]<=8 for e in fixed)
        if state['open']=='true' and state['column']=='1' and state['row']!='2':assert not m['elements']
        checked+=1
    # Crop across the center: north UV half follows reversed physical X, south follows X.
    sample={'from':[16,0,0],'to':[32,16,4],'faces':{'north':{'texture':'#wood','uv':[0,0,16,16]},'south':{'texture':'#wood','uv':[0,0,16,16]}}}
    left=crop(sample,[16,0,0,24,16,4]);right=crop(sample,[24,0,0,32,16,4])
    assert left['faces']['north']['uv']==[8,0,16,16] and right['faces']['north']['uv']==[0,0,8,16]
    assert left['faces']['south']['uv']==[0,0,8,16] and right['faces']['south']['uv']==[8,0,16,16]
    # Facing-relative placement has a unique center controller and no overlapping occupied cells.
    for facing,right in [((0,-1),(1,0)),((1,0),(0,1)),((0,1),(-1,0)),((-1,0),(0,-1))]:
        for opened in (False,True):
            cells=[(right[0]*(c-1)-facing[0]*d,r,right[1]*(c-1)-facing[1]*d) for c,r,d in itertools.product(range(3),range(3),range(2)) if d==0 or opened and c in (0,2)]
            assert len(cells)==len(set(cells))==(15 if opened else 9)
    source=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/DoubleSchoolEntranceBlock.java').read_text()
    assert 'column - 1' in source and '1 - state.getValue(COLUMN)' in source
    assert source.index('// Validate the entire operation')<source.index('level.setBlock(target, next')
    for snippet in ('level.isUnobstructed','level.mayInteract','hasNeighborSignal','POWERED','SchoolGlazing.refreshHorizontal','SimpleBlockOutline.forState','buildCollisions','Block.UPDATE_SUPPRESS_DROPS'):
        assert snippet in source
    loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{NAME}.json')['pools'][0]
    assert loot['conditions'][1]['properties']=={'row':'0','column':'1','depth':'0'}
    for p in (A/'textures/block').glob(NAME+'*.png'):assert Image.open(p).size==(16,16)
    print(f'OK: {len(states)} states/{checked} models; 48x48 overall, 24px leaves split at x24; fixed upper glass; 9 closed/15 open cells; preserved wood volume and clipped UV; atomic validation/redstone/single drop checked statically.')
if __name__=='__main__':main()
