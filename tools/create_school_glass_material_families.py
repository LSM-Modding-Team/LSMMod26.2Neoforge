"""Eight missing glass stair pairs: inherited native meshes, grouped Fusion masks and shared curtains."""
import json
from collections import OrderedDict
from create_school_glass_assets import ROOT,A,D,write
from school_glass_material_families import MATERIALS,NEW_PAIRS

TEMPLATE='mixed_school_glass'
GENERATED=[]

def emit(path, value):
    write(path,value);GENERATED.append(str(path.relative_to(ROOT)))


def main():
    definition=json.loads((A/f'blockstates/{TEMPLATE}.json').read_text())
    blocks=[];items=[]
    labels={'white':('Blanco de aula','Classroom White'),'dark':('Oscuro','Dark'),'light':('Claro','Light'),'street':('Calle','Street')}
    for (front,body),ident in NEW_PAIRS.items():
        textures={'concrete':'lsmmod:block/'+MATERIALS[front],'light_wall':'lsmmod:block/'+MATERIALS[body]}
        memo={}
        def clone(reference):
            if not reference.startswith('lsmmod:block/'):return reference
            stem=reference.split('block/',1)[1]
            native=stem==TEMPLATE or stem.startswith(TEMPLATE+'_')
            dynamic=stem.startswith('school_glazing_dynamic/'+TEMPLATE+'_')
            if not(native or dynamic):return reference # Curtains remain shared across all materials.
            if reference in memo:return memo[reference]
            target=stem.replace(TEMPLATE,ident,1);result='lsmmod:block/'+target;memo[reference]=result
            old=json.loads((A/f'models/block/{stem}.json').read_text())
            if native:
                # Vanilla resolves child textures after inheriting the full existing mesh.
                emit(A/f'models/block/{target}.json',{'parent':reference,'textures':textures})
            else:
                groups=OrderedDict()
                for entry in old['models'][0]:
                    masks=entry['condition'].get('masks',[entry['condition'].get('mask')])
                    groups.setdefault(entry['model'],[]).extend(masks)
                series=[]
                for source,masks in groups.items():
                    assert masks and all(0<=m<32 for m in masks)
                    condition={'type':'lsmmod:glazing_mask'}
                    condition['mask' if len(masks)==1 else 'masks']=masks[0] if len(masks)==1 else sorted(masks)
                    series.append({'model':clone(source),'condition':condition})
                assert 0 in series[0]['condition'].get('masks',[series[0]['condition'].get('mask')])
                emit(A/f'models/block/{target}.json',{'loader':'fusion:model','type':'composite',
                     'textures':{'particle':'lsmmod:block/school_glass_frame'},'models':[series]})
            return result
        result=json.loads(json.dumps(definition))
        for part in result['multipart']:part['apply']['model']=clone(part['apply']['model'])
        emit(A/f'blockstates/{ident}.json',result)
        clone('lsmmod:block/'+TEMPLATE)
        emit(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident})
        emit(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
        emit(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,
             'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
        blocks.append(f'''    public static final DeferredBlock<SchoolGlassStairBlock> {ident.upper()} = BLOCKS.registerBlock(
            "{ident}", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());''')
        items.append(f'    public static final DeferredItem<BlockItem> {ident.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{ident.upper()});')
        for locale,index in [('es_es',0),('en_us',1)]:
            p=A/f'lang/{locale}.json';values=json.loads(p.read_text())
            f,b=labels[front][index],labels[body][index]
            values['block.lsmmod.'+ident]=(f'Escalera de cristal: frente {f.lower()} / cuerpo {b.lower()}' if index==0 else f'Glass Stairs: {f} Front / {b} Body')
            emit(p,values)
    for filename,body in [('ModBlocks.java','\n\n'.join(blocks)),('ModItems.java','\n'.join(items))]:
        p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=p.read_text()
        start='    // BEGIN REMAINING GLASS MATERIAL FAMILIES';end='    // END REMAINING GLASS MATERIAL FAMILIES';section=start+'\n'+body+'\n'+end
        if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section+s[b:]
        else:marker='    // BEGIN CLASSROOM SURFACES';s=s.replace(marker,section+'\n\n'+marker,1)
        p.write_text(s);GENERATED.append(str(p.relative_to(ROOT)))
    p=D/'minecraft/tags/block/mineable/pickaxe.json';values=json.loads(p.read_text())
    values['values']=list(dict.fromkeys(values['values']+['lsmmod:'+ident for ident in NEW_PAIRS.values()]));emit(p,values)


if __name__=='__main__':main()
