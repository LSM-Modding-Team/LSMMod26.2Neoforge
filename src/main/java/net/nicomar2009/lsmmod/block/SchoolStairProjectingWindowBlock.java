package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;

/** Edge-mounted L profile: six solid 8px cubes and a projecting sash in the free quadrant. */
public class SchoolStairProjectingWindowBlock extends SchoolProjectingWindowBlock implements EntityBlock {
    public static final EnumProperty<Half> HALF=BlockStateProperties.HALF;
    public SchoolStairProjectingWindowBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(HALF,Half.BOTTOM));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);builder.add(WALL_LEFT,HALF);
    }
    @Override protected BlockState placementState(BlockPlaceContext context) {
        Direction side=context.getClickedFace();
        boolean top=side==Direction.DOWN||(side!=Direction.UP&&context.getClickLocation().y-context.getClickedPos().getY()>.5);
        return defaultBlockState().setValue(HALF,top?Half.TOP:Half.BOTTOM);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return new SchoolStairProjectingWindowPaintEntity(pos,state);
    }
}
