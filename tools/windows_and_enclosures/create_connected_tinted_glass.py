"""An independent opaque tinted variant with the normal school's dynamic joins."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text
import copy
import json
from windows_and_enclosures.create_school_glass_assets import A, D, ROOT
from tables_and_desks.create_dining_counter_assets import write
from windows_and_enclosures.create_horizontal_glazing_assets import glazing

ID = 'school_connected_tinted_glass'
J = ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'


def main():
    textures = {'glass':'lsmmod:block/school_tinted_glass',
                'frame':'lsmmod:block/school_glass_frame','particle':'lsmmod:block/school_tinted_glass'}
    entries = []
    for mask in range(32):
        model = {'loader':'fusion:model','type':'base','parent':'minecraft:block/block',
                 'render_type':'minecraft:solid','textures':textures,
                 'elements':glazing(mask & 7,0,16,bool(mask & 16),bool(mask & 8),[])}
        name = ID+'_'+str(mask)
        write(A/f'models/block/{name}.json',model)
        entries.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:glazing_mask','mask':mask}})
    write(A/f'models/block/{ID}.json', {'loader':'fusion:model','type':'composite','textures':textures,'models':[entries]})
    # The same 17 curtain layouts and their overlays as normal school_glass.
    normal = json.loads(module_read_text(A/'blockstates/school_glass.json'))['multipart']
    parts = [copy.deepcopy(p) for p in normal if 'curtain' in p.get('when',{})]
    for turn,facing in enumerate(('north','east','south','west')):
        parts.append({'when':{'facing':facing},'apply':{'model':'lsmmod:block/'+ID,'y':turn*90}})
    write(A/f'blockstates/{ID}.json',{'multipart':parts})
    write(A/f'models/item/{ID}.json',{'parent':f'lsmmod:block/{ID}_3'})
    write(A/f'items/{ID}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ID}})
    write(D/f'lsmmod/loot_table/blocks/{ID}.json', {'type':'minecraft:block','pools':[{'rolls':1,
          'conditions':[{'condition':'minecraft:survives_explosion'}],
          'entries':[{'type':'minecraft:item','name':'lsmmod:'+ID}]}]})
    for filename,line in [
        ('ModBlocks.java','    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_CONNECTED_TINTED_GLASS = BLOCKS.registerBlock("school_connected_tinted_glass", props -> new SchoolGlassBlock(props, new double[][]{{0,0,0,16,16,8}}), props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());'),
        ('ModItems.java','    public static final DeferredItem<BlockItem> SCHOOL_CONNECTED_TINTED_GLASS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CONNECTED_TINTED_GLASS);'),
    ]:
        p = J/filename
        source = module_read_text(p)
        a,z = '    // BEGIN CONNECTED TINTED GLASS','    // END CONNECTED TINTED GLASS'
        section = a+'\n'+line+'\n'+z
        if a in source:
            source = source[:source.index(a)]+section+source[source.index(z)+len(z):]
        else:
            source = source.replace('    // BEGIN FACILITY EXPANSIONS',section+'\n\n    // BEGIN FACILITY EXPANSIONS',1)
        write(p,source)
    for locale,name in [('es_es','Cristal tintado conectado del colegio'),('en_us','Connected School Tinted Glass')]:
        p = A/f'lang/{locale}.json';values = json.loads(module_read_text(p))
        values['block.lsmmod.'+ID] = name;write(p,values)


if __name__=='__main__':
    main()
