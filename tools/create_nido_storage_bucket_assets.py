"""Passive red plastic bucket with a white lid; exact 1x1x1.5 block hitbox."""
from resource_modules import module_read_text, module_write_text, output_path
import copy
import json
import math
from PIL import Image
from create_bathroom_statue_assets import solid_model
from create_school_refrigerator_assets import A, J, D, write
from create_san_martin_display import clip
from create_facility_expansion_assets import basic

ID = 'nido_storage_bucket'
TEXTURES = {'red': f'lsmmod:block/{ID}_red',
            'white': 'lsmmod:block/school_porcelain', 'particle': '#red'}


def disc(radius, bottom, top, material):
    # Pixel-stepped circular sections, bounded by a single block footprint.
    result = []
    for z in range(math.floor(8-radius), math.ceil(8+radius)):
        edge = max(abs(z-8), abs(z+1-8))
        if edge >= radius: continue
        half = round(math.sqrt(radius*radius-edge*edge)*4)/4
        result.append(([8-half, bottom, z, 8+half, top, z+1], material))
    return result


def parts():
    result = []
    for low, high, radius in ((0, 4, 5.75), (4, 9, 6), (9, 14, 6.25),
                              (14, 19.5, 6.5), (19.5, 21, 7)):
        result.extend(disc(radius, low, high, 'red'))
    result.extend(disc(8, 21, 22.25, 'white'))
    result.extend(disc(7.5, 22.25, 22.75, 'white'))
    result.extend(disc(6.25, 22.75, 23, 'white'))
    result.extend(disc(1.5, 23, 24, 'white'))
    # Two open side handles, inside the requested 16x16 footprint.
    for left in (True, False):
        boxes = [[.25, 18, 6, 1, 20.5, 6.75], [.25, 18, 9.25, 1, 20.5, 10],
                 [.25, 17.75, 6, 1, 18.5, 10], [1, 20, 6, 2, 20.75, 10]]
        for b in boxes:
            if not left: b[0], b[3] = 16-b[3], 16-b[0]
            result.append((b, 'red'))
    return result


def main():
    image = Image.new('RGB', (16, 16), (224, 15, 23))
    for y in range(16):
        for x in range(16):
            delta = (x*13+y*7)%3-1
            image.putpixel((x, y), (224+delta, 15+delta, 23+delta))
    image.save(output_path(A/f'textures/block/{ID}_red.png'))
    full = solid_model(parts())
    full.update(textures=TEXTURES, render_type='minecraft:solid')
    variants = {}
    for row, half in ((0, 'lower'), (1, 'upper')):
        name = ID+'_'+half
        write(A/f'models/block/{name}.json', clip(full, 0, row))
        for turn, facing in enumerate(('north', 'east', 'south', 'west')):
            variants[f'facing={facing},half={half}'] = {'model': 'lsmmod:block/'+name, 'y': turn*90}
    write(A/f'blockstates/{ID}.json', {'variants': variants})
    item = copy.deepcopy(full)
    for e in item['elements']:
        for key in ('from', 'to'):
            x, y, z = e[key]
            e[key] = [8+(x-8)*2/3, y*2/3, 8+(z-8)*2/3]
    write(A/f'models/item/{ID}.json', item)
    basic(ID, controller={'half': 'lower'})
    module_write_text(J/'modules/lockers_and_storage/block/NidoStorageBucketBlock.java', '''package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Decorative two-cell bucket; full 1x1 footprint and 1.5 block selection/collision height. */
public class NidoStorageBucketBlock extends SchoolRectangularBlock {
    private static final double[][] BOXES = {{0, 0, 0, 16, 24, 16}};

    public NidoStorageBucketBlock(Properties properties) {
        super(properties, BOXES, BOXES, false, 1, 2, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }
}
''')
    for filename, entry in (
        ('ModBlocks.java', '    public static final DeferredBlock<NidoStorageBucketBlock> NIDO_STORAGE_BUCKET = BLOCKS.registerBlock("nido_storage_bucket", NidoStorageBucketBlock::new, props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java', '    public static final DeferredItem<BlockItem> NIDO_STORAGE_BUCKET = ITEMS.registerSimpleBlockItem(ModBlocks.NIDO_STORAGE_BUCKET);'),
    ):
        path = J/'registry'/filename
        text = module_read_text(path)
        if ' NIDO_STORAGE_BUCKET =' not in text:
            module_write_text(path, text.replace('    // BEGIN FACILITY EXPANSIONS', entry+'\n\n    // BEGIN FACILITY EXPANSIONS', 1))
    # Preserve the exact user-requested name, including in the inventory tooltip.
    for locale in ('es_es', 'en_us'):
        path = A/f'lang/{locale}.json'
        data = json.loads(module_read_text(path))
        data['block.lsmmod.'+ID] = ID
        write(path, data)
    path = D/'minecraft/tags/block/mineable/axe.json'
    data = json.loads(module_read_text(path))
    if 'lsmmod:'+ID not in data['values']:
        data['values'].append('lsmmod:'+ID)
        write(path, data)


if __name__ == '__main__': main()
