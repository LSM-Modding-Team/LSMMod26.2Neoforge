package net.nicomar2009.lsmmod.block;

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
