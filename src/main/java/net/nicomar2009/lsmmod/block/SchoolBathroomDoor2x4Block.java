package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** The wide bathroom lifecycle and fixed transom, extended to four rows. */
public class SchoolBathroomDoor2x4Block extends SchoolFramedDiningDoorBlock {
    public static final IntegerProperty ROW = IntegerProperty.create("row",0,3);
    public SchoolBathroomDoor2x4Block(Properties properties) {super(properties);}
    @Override public int rows() {return 4;}
    @Override protected IntegerProperty rowProperty() {return ROW;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(FACING,OPEN,COLUMN,ROW,DEPTH,HINGE);
    }
}
