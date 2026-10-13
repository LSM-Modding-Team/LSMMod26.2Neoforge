"""Ten-by-three wall lettering: exact ASCII wording, one-pixel relief, existing rectangular lifecycle."""
import copy,json
from pathlib import Path
from PIL import Image,ImageDraw,ImageFont
from create_school_refrigerator_assets import ROOT,A,J,D,write,clip
from create_bathroom_statue_assets import solid_model
from create_facility_expansion_assets import basic

ID='school_name_sign'
TEXT='COLEGIO LIBERTADOR SAN MARTIN'
LINES=('COLEGIO','LIBERTADOR SAN MARTIN')
WIDTH,HEIGHT,DENSITY=160,48,2
GOLD=(218,199,128)
TEXTURES={'gold':'lsmmod:block/school_name_sign_gold','particle':'#gold'}


def lettering():
    # Rasterize serif strokes at half-pixel geometry precision; the material itself remains a 16px texture.
    candidates=(ROOT/'newresources/LiberationSerif-Regular.ttf',Path('/usr/share/fonts/truetype/liberation/LiberationSerif-Regular.ttf'),Path('C:/Windows/Fonts/times.ttf'))
    font_path=next((path for path in candidates if path.exists()),None)
    if font_path is None:raise RuntimeError('Install Liberation Serif or provide newresources/LiberationSerif-Regular.ttf')
    mask=Image.new('L',(WIDTH*DENSITY,HEIGHT*DENSITY))
    for text,size,top in ((LINES[0],36,14),(LINES[1],28,54)):
        font=ImageFont.truetype(str(font_path),size);bounds=font.getbbox(text)
        line=Image.new('L',(bounds[2]-bounds[0],bounds[3]-bounds[1]))
        ImageDraw.Draw(line).text((-bounds[0],-bounds[1]),text,font=font,fill=255)
        if line.width>mask.width-16:line=line.resize((mask.width-16,line.height),Image.Resampling.NEAREST)
        mask.paste(line,((mask.width-line.width)//2,top))
    return mask.point(lambda value:255 if value>=128 else 0)


def rectangles(mask):
    active={};result=[]
    for y in range(mask.height):
        spans=[];start=None
        for x in range(mask.width+1):
            filled=x<mask.width and mask.getpixel((x,y))>0
            if filled and start is None:start=x
            if not filled and start is not None:spans.append((start,x));start=None
        next_active={}
        for span in spans:
            box=active.pop(span,[span[0],y,span[1],y+1]);box[3]=y+1;next_active[span]=box
        result.extend(active.values());active=next_active
    result.extend(active.values())
    return result


def main():
    mask=lettering();reference=Image.new('RGBA',mask.size,GOLD+(0,));reference.putalpha(mask)
    reference.save(ROOT/'newresources/school_name_sign_lettering.png')
    Image.new('RGB',(16,16),GOLD).save(A/'textures/block/school_name_sign_gold.png')
    # North-facing lettering starts at high world X, matching Minecraft's front-face UV orientation.
    boxes=[([WIDTH-x1/DENSITY,HEIGHT-y1/DENSITY,15,WIDTH-x0/DENSITY,HEIGHT-y0/DENSITY,16],'gold')
           for x0,y0,x1,y1 in rectangles(mask)]
    model=solid_model(boxes);model.update(textures=TEXTURES,render_type='minecraft:solid')
    write(ROOT/'newresources/school_name_sign_complete.json',model)
    variants={}
    for row in range(3):
        for column in range(10):
            name=f'{ID}_{row}_{column}';local=clip(model,column,row)
            write(A/f'models/block/{name}.json',local)
            for turn,facing in enumerate(('north','east','south','west')):
                variants[f'column={column},row={row},facing={facing}']={'model':'lsmmod:block/'+name,'y':turn*90}
    write(A/f'blockstates/{ID}.json',{'variants':variants})
    item=copy.deepcopy(model)
    for element in item['elements']:
        for key in ('from','to'):
            x,y,z=element[key];element[key]=[x/10,8+(y-HEIGHT/2)/10,8+(z-15.5)/10]
    item['display']={'gui':{'rotation':[0,180,0],'scale':[1,1,1]}}
    write(A/f'models/item/{ID}.json',item)
    basic(ID,controller={'column':'0','row':'0'})
    (J/'block/SchoolNameSignBlock.java').write_text('''package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** One-pixel wall lettering over a 10x3 footprint; one item places/removes all 30 cells. */
public class SchoolNameSignBlock extends SchoolRectangularBlock {
    public static final IntegerProperty COLUMN=IntegerProperty.create("column",0,9);
    public static final IntegerProperty ROW=IntegerProperty.create("row",0,2);
    private static final double[][] BOXES={{0,0,15,160,48,16}};
    public SchoolNameSignBlock(Properties properties) {super(properties,BOXES,BOXES,true,10,3,false);}
    @Override protected IntegerProperty columnProperty() {return COLUMN;}
    @Override protected IntegerProperty rowProperty() {return ROW;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,COLUMN,ROW);}
}
''')
    for filename,entry in (
        ('ModBlocks.java','    public static final DeferredBlock<SchoolNameSignBlock> SCHOOL_NAME_SIGN = BLOCKS.registerBlock("school_name_sign", SchoolNameSignBlock::new, props -> props.strength(1F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java','    public static final DeferredItem<BlockItem> SCHOOL_NAME_SIGN = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_NAME_SIGN);'),
    ):
        path=J/'registry'/filename;text=path.read_text()
        if ' SCHOOL_NAME_SIGN =' not in text:path.write_text(text.replace('    // BEGIN FACILITY EXPANSIONS',entry+'\n\n    // BEGIN FACILITY EXPANSIONS',1))
    for locale,label in (('es_es','Letrero Colegio Libertador San Martin'),('en_us','Colegio Libertador San Martin Sign')):
        path=A/f'lang/{locale}.json';data=json.loads(path.read_text());data['block.lsmmod.'+ID]=label;write(path,data)
    path=D/'minecraft/tags/block/mineable/pickaxe.json';data=json.loads(path.read_text())
    if 'lsmmod:'+ID not in data['values']:data['values'].append('lsmmod:'+ID)
    write(path,data)


if __name__=='__main__':main()
