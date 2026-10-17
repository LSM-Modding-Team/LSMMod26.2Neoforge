"""Four smooth OBJ wedges, uniform existing yellow, no connection states or new textures."""
from resource_modules import module_read_text, module_write_text
from resource_json import resource_json
import json
from create_entrance_pavement_shapes import ROOT,A,D,SIDES,obj
GENERATED=[]

def write(path,value):
    path.parent.mkdir(parents=True,exist_ok=True);module_write_text(path, resource_json(path, value, ensure_ascii=False)+'\n',encoding='utf-8');GENERATED.append(str(path.relative_to(ROOT)))
def text(path,value):module_write_text(path, value);GENERATED.append(str(path.relative_to(ROOT)))

def main():
    text(A/'models/block/school_yellow_slopes.mtl','newmtl yellow\nKd 1 1 1\nd 1\nmap_Kd yellow\n')
    blocks=[];items=[];layout=[]
    for i,high in enumerate((.5,.375,.25,.125),1):
        name=f'yellow_school_slope_{i}'
        mesh=obj(high,-.125,0,dict.fromkeys(SIDES,True)).replace('school_entrance_pavement_shapes.mtl','school_yellow_slopes.mtl').replace('usemtl gray','usemtl yellow')
        text(A/f'models/block/{name}.obj',mesh)
        write(A/f'models/block/{name}.json',{'loader':'neoforge:obj','model':f'lsmmod:models/block/{name}.obj','automatic_culling':False,'flip_v':True,'shade_quads':True,'textures':{'yellow':'lsmmod:block/yellow_school_block','particle':'lsmmod:block/yellow_school_block'}})
        write(A/f'blockstates/{name}.json',{'variants':{f'facing={side}':{'model':'lsmmod:block/'+name,'y':angle} for side,angle in [('north',0),('east',90),('south',180),('west',270)]}})
        write(A/f'models/item/{name}.json',{'parent':'lsmmod:block/'+name})
        write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+name}})
        write(D/f'lsmmod/loot_table/blocks/{name}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+name}]}]})
        blocks.append(f'''    public static final DeferredBlock<YellowSchoolSlopeBlock> {name.upper()} = BLOCKS.registerBlock(
            "{name}", props -> new YellowSchoolSlopeBlock({high},props),
            props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());''')
        items.append(f'    public static final DeferredItem<BlockItem> {name.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{name.upper()});')
        layout.append({'id':name,'high_pixels':high*16,'low_pixels':(high-.125)*16})
        for locale,label in [('es_es',f'Pendiente amarilla del colegio {i}'),('en_us',f'Yellow School Slope {i}')]:
            p=A/f'lang/{locale}.json';v=json.loads(module_read_text(p));v['block.lsmmod.'+name]=label;write(p,v)
    for filename,body in [('ModBlocks.java','\n\n'.join(blocks)),('ModItems.java','\n'.join(items))]:
        p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=module_read_text(p);start='    // BEGIN YELLOW SCHOOL SLOPES';end='    // END YELLOW SCHOOL SLOPES';section=start+'\n'+body+'\n'+end
        if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section+s[b:]
        else:s=s.replace('    // BEGIN CLASSROOM SURFACES',section+'\n\n    // BEGIN CLASSROOM SURFACES',1)
        module_write_text(p, s);GENERATED.append(str(p.relative_to(ROOT)))
    p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(module_read_text(p));v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+e['id'] for e in layout]));write(p,v)
    write(ROOT/'tools/yellow_school_slope_layout.json',{'length':4,'drop_pixels':8,'descending_toward':'facing','pieces':layout})
if __name__=='__main__':main()
