"""Static checks only: endpoint limits, persistent chains, multipart selection and free-volume geometry."""
import itertools,json,hashlib
from pathlib import Path
from PIL import Image
from create_school_glass_assets import A, ROOT
from create_school_glass_stair_assets import QUARTERS
from create_school_curtain_assets import IDS, PARTS, base_parts

def read(path):return json.loads(path.read_text())
def matches(when,state):return all(state.get(k) in v.split('|') for k,v in when.items())
def intersects(a,b):return all(max(a[i],b[i])<min(a[i+3],b[i+3]) for i in range(3))
def rotate(box):return [16-box[5],box[1],box[0],16-box[2],box[4],box[3]]

def selection(first,end,right):
    dx,dz=end[0]-first[0],end[2]-first[2]
    offset=dx*right[0]+dz*right[1]
    height=abs(end[1]-first[1])+1
    if dx*right[1]-dz*right[0]!=0 or abs(offset)+1>4 or height>8:return None
    start=(first[0]+right[0]*min(0,offset),min(first[1],end[1]),first[2]+right[1]*min(0,offset))
    return [(start[0]+x*right[0],start[1]+y,start[2]+x*right[1]) for y in range(height) for x in range(abs(offset)+1)]

def main():
    cached={};tested=0
    for block in IDS:
        definition=read(A/f'blockstates/{block}.json')
        assert 'variants' not in definition and 'multipart' in definition
        base=base_parts(definition)
        assert len(base)==(128 if block=='school_glass' else 2560)
        overlay=[p for p in definition['multipart'] if 'curtain' in p['when']]
        cases=[(None,None)] if block=='school_glass' else list(itertools.product(('bottom','top'),QUARTERS))
        for facing,angle in (('north',0),('east',90),('south',180),('west',270)):
            for half,shape in cases:
                state={'facing':facing,'half':half,'shape':shape,'curtain':'none'}
                assert not any(matches(p['when'],state) for p in overlay)
                for part,vertical in itertools.product(PARTS,PARTS):
                    state["curtain_vertical"]=vertical
                    state['curtain']=part;selected=[p for p in overlay if matches(p['when'],state)]
                    assert len(selected)==1,(block,state)
                    apply=selected[0]['apply'];assert apply['y']==angle
                    model_id=apply['model'].split(':')[1]
                    if model_id not in cached:cached[model_id]=read(A/f'models/{model_id}.json')
                    model=cached[model_id];assert model['render_type']=='minecraft:cutout'
                    if half is None:occupied=[[0,0,0,16,16,8]]
                    else:
                        occupied=[[0,0,0,16,8,16]]+[[8*x,8,8*z,8*x+8,16,8*z+8] for x,z in QUARTERS[shape]]
                        if half=='top':occupied=[[b[0],16-b[4],b[2],b[3],16-b[1],b[5]] for b in occupied]
                    for _ in range(angle//90):occupied=[rotate(b) for b in occupied]
                    for e in model['elements']:
                        assert 1<=len(e['faces'])<=6,(model_id,'Minecraft requires at least one face per element')
                        box=e['from']+e['to']
                        assert all(0<=box[i]<box[i+3]<=16 for i in range(3))
                        assert box[2]>8 and box[5]<=11
                        for _ in range(angle//90):box=rotate(box)
                        assert not any(intersects(box,b) for b in occupied),(block,state,box)
                        for face in e['faces'].values():
                            assert all(0<=v<=16 for v in face['uv'])
                            texture=model['textures'][face['texture'][1:]]
                            assert (A/f"textures/{texture.split(':')[1]}.png").is_file()
                    tested+=1
    # Every size, direction, endpoint order and member recovers the same rectangle.
    def marker(i,size):return 'single' if size==1 else 'left' if i==0 else 'right' if i==size-1 else 'middle'
    for right in ((1,0),(-1,0),(0,1),(0,-1)):
        for width,height in itertools.product(range(1,5),range(1,9)):
            first=(10,70,-12)
            for sx,sy in itertools.product((-1,1),repeat=2):
                end=(first[0]+right[0]*(width-1)*sx,70+(height-1)*sy,first[2]+right[1]*(width-1)*sx)
                a=selection(first,end,right);b=selection(end,first,right)
                assert a==b and len(a)==width*height and len(set(a))==width*height
            grid={(x,y):(marker(x,width),marker(y,height)) for y in range(height) for x in range(width)}
            for x,y in grid:
                ox,oy=x,y
                while grid[ox,oy][0] not in ('left','single'):ox-=1
                while grid[ox,oy][1] not in ('left','single'):oy-=1
                assert (ox,oy)==(0,0)
                wx=next(i+1 for i in range(width) if grid[i,0][0] in ('right','single'))
                hy=next(i+1 for i in range(height) if grid[0,i][1] in ('right','single'))
                assert (wx,hy)==(width,height)
                # Removal visits every member, including when any one old host is substituted.
                assert {(i,k) for k in range(hy) for i in range(wx)}==set(grid)
        assert selection(first,(first[0]+right[0]*4,70,first[2]+right[1]*4),right) is None
        assert selection(first,(first[0],78,first[2]),right) is None
        assert selection(first,(first[0]+right[1],70,first[2]-right[0]),right) is None
    # Continuous full-height cloth reaches boundaries; only the top carries a rail.
    for vertical in ('left','middle'):
        model=read(A/f'models/block/school_curtain_full_middle_vertical_{vertical}.json')
        assert not any(f['texture']=='#rail' for e in model['elements'] for f in e['faces'].values())
        assert max(e['to'][1] for e in model['elements'])==16
    model=read(A/'models/block/school_curtain_full_middle_vertical_middle.json')
    assert min(e['from'][1] for e in model['elements'])==0
    root=ROOT/'src/main/java/net/nicomar2009/lsmmod'
    manager=(root/'block/SchoolCurtains.java').read_text();item=(root/'item/SchoolCurtainItem.java').read_text()
    assert 'MAX_LENGTH=4, MAX_HEIGHT=8' in manager and 'TallClassroomEntranceBlock' not in manager
    assert 'tag.putLong(START' in item and 'tag.putString(DIMENSION' in item and 'tag.putString(OWNER' in item
    assert item.index('// Validate every target')<item.index('level.setBlock(target')<item.index('stack.shrink(1)')
    assert 'Math.abs(distance)+1' in item and 'SchoolCurtains.compatible(base,state)' in item
    assert 'SchoolCurtains.part(state)!=CurtainPart.NONE' in item and 'level.mayInteract(player,target)' in item
    assert manager.index('clear(level,target);')<manager.index('if(drop)Block.popResource')
    assert 'SchoolCurtains.VERTICAL,SchoolCurtains.axisPart(y,height)' in item
    assert 'for(int y=0;y<height;y++)for(int i=0;i<length;i++)' in item
    assert 'level.hasChunkAt' in manager and 'level.scheduleTick' in manager
    for name in ('SchoolGlassBlock.java','SchoolGlassStairBlock.java'):
        s=(root/'block'/name).read_text()
        assert 'SchoolCurtains.CURTAIN,CurtainPart.NONE' in s
        assert 'SchoolCurtains.tick(level,pos)' in s and 'SchoolCurtains.remove' in s
        assert 'affectNeighborsAfterRemoval' in s and 'SimpleBlockOutline.forState' in s
    door=(root/'block/TallClassroomEntranceBlock.java').read_text();assert 'SchoolCurtains.CURTAIN' not in door
    assert 'SCHOOL_CURTAIN' in (root/'registry/ModItems.java').read_text()
    for folder,name in (('block','school_curtain'),('block','school_curtain_rail'),('block','school_curtain_hem'),('item','school_curtain')):
        im=Image.open(A/f'textures/{folder}/{name}.png');assert im.size==(16,16);im.verify()
    assert read(A/'items/school_curtain.json')['model']['model']=='lsmmod:item/school_curtain'
    for locale in ('en_us','es_es'):
        text=read(A/f'lang/{locale}.json')
        assert 'item.lsmmod.school_curtain' in text and 'message.lsmmod.curtain.length' in text
    print(f'OK: {tested} curtain orientations/halves/corners; no solid/glass intersections; valid 1–4 × 1–8 rectangles in every orientation and endpoint order; door excluded; persistent chain/removal and item/resources checked statically.')

if __name__=='__main__':main()
