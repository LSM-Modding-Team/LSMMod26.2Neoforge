"""Full cube and paint using the exact existing pavement border yellow."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text, module_write_text, output_path
import json
from PIL import Image
from painting_and_editable_blocks.create_paintable_school_block import ROOT, A, write


def main():
    ident = 'yellow_school_block'
    # Same RGB as create_school_yellow_border_assets.py; flat finish on all six faces.
    Image.new('RGB', (16, 16), (255, 211, 0)).save(output_path(A/f'textures/block/{ident}.png'))
    write(A/f'blockstates/{ident}.json', {'variants': {'': {'model': 'lsmmod:block/'+ident}}})
    write(A/f'models/block/{ident}.json', {'parent': 'minecraft:block/cube_all', 'textures': {'all': 'lsmmod:block/'+ident}})
    write(A/f'models/item/{ident}.json', {'parent': 'lsmmod:block/'+ident})
    write(A/f'items/{ident}.json', {'model': {'type': 'minecraft:model', 'model': 'lsmmod:item/'+ident}})
    write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json', {'type': 'minecraft:block',
        'pools': [{'rolls': 1, 'conditions': [{'condition': 'minecraft:survives_explosion'}], 'entries': [{'type': 'minecraft:item', 'name': 'lsmmod:'+ident}]}]})
    block='''    public static final DeferredBlock<Block> YELLOW_SCHOOL_BLOCK = BLOCKS.registerBlock(
            "yellow_school_block", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());'''
    item='    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.YELLOW_SCHOOL_BLOCK);'
    for file, body in [('ModBlocks.java', block), ('ModItems.java', item)]:
        p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/file;s=module_read_text(p)
        start='    // BEGIN YELLOW SCHOOL BLOCK';end='    // END YELLOW SCHOOL BLOCK'
        if start in s:
            a=s.index(start);b=s.index(end)+len(end);s=s[:a]+start+'\n'+body+'\n'+end+s[b:]
        else:
            marker='    // BEGIN CLASSROOM SURFACES';s=s.replace(marker,start+'\n'+body+'\n'+end+'\n\n'+marker,1)
        module_write_text(p, s)
    for locale, name in [('es_es', 'Bloque amarillo del colegio'), ('en_us', 'Yellow School Block')]:
        p=A/f'lang/{locale}.json';d=json.loads(module_read_text(p));d['block.lsmmod.'+ident]=name;write(p,d)
    p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';d=json.loads(module_read_text(p))
    d['values']=list(dict.fromkeys(d['values']+['lsmmod:'+ident]));write(p,d)
    from painting_and_editable_blocks.create_paintable_school_block import main as paint_faces
    from painting_and_editable_blocks.create_custom_paintable_blocks import main as custom_faces
    paint_faces();custom_faces()


if __name__ == '__main__': main()
