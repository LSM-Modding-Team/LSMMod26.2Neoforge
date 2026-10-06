package net.nicomar2009.lsmmod.block;

import net.nicomar2009.lsmmod.item.AwningSupportItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

/** One item places a variable-width horizontal suspension bar. */
public class AwningSupportBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 15);

    public static final IntegerProperty WIDTH = IntegerProperty.create("width", 1, 16);

    public AwningSupportBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(COLUMN, 0).setValue(WIDTH, 4));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, COLUMN, WIDTH); }
    public static BlockPos cell(BlockPos root, Direction facing, int column) { return root.relative(facing.getCounterClockWise(), column); }
    public static BlockPos root(BlockPos pos, BlockState state) { return cell(pos, state.getValue(FACING), -state.getValue(COLUMN)); }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction axis = context.getHorizontalDirection().getAxis() == Direction.Axis.Z ? Direction.SOUTH : Direction.EAST;
        int width = AwningSupportItem.width(context.getItemInHand());
        BlockState state = defaultBlockState().setValue(FACING, axis).setValue(WIDTH, width);
        for (int c = 0; c < width; c++) {
            BlockPos p = cell(context.getClickedPos(), axis, c);
            if (!context.getLevel().hasChunkAt(p) || !context.getLevel().getWorldBorder().isWithinBounds(p)
                    || !context.getLevel().getBlockState(p).canBeReplaced(context)
                    || !context.getLevel().getFluidState(p).isEmpty()
                    || (context.getPlayer() != null && (!context.getLevel().mayInteract(context.getPlayer(), p)
                    || !context.getPlayer().mayUseItemAt(p, context.getClickedFace(), context.getItemInHand())))) return null;
        }
        return state;
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide()) return;
        int width = state.getValue(WIDTH);
        for (int c = 0; c < width; c++) {
            BlockPos p = cell(pos, state.getValue(FACING), c);
            if (c != 0) level.setBlock(p, state.setValue(COLUMN, c), Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(p) instanceof AwningBlockEntity part) part.setSupportRoot(pos, width);
        }
        for (int c = 0; c < width; c++) level.updateNeighborsAt(cell(pos, state.getValue(FACING), c), this);
    }
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof AwningBlockEntity part)
            AwningStructure.removed(level, part, !player.isCreative());
        return super.playerWillDestroy(level, pos, state, player);
    }
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean alongZ = state.getValue(FACING).getAxis() == Direction.Axis.Z;
        VoxelShape shape = alongZ ? Shapes.or(Block.box(0,10,6,16,16,10),Block.box(6,13,0,10,15,16))
                : Shapes.or(Block.box(6,10,0,10,16,16),Block.box(0,13,6,16,15,10));
        int c=state.getValue(COLUMN);
        for (int edge=0; edge<2; edge++) {
            if ((edge==0 && c!=0) || (edge==1 && c!=state.getValue(WIDTH)-1)) continue;
            double low=edge==0 ? 0 : 15, high=low+1;
            shape=Shapes.or(shape,alongZ ? Block.box(low,9.5,5.5,high,16,10.5)
                    : Block.box(5.5,9.5,16-high,10.5,16,16-low));
        }
        return shape;
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new AwningBlockEntity(pos, state); }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (world, pos, blockState, entity) -> {
            if (entity instanceof AwningBlockEntity part) AwningStructure.tick(world, part);
        };
    }
}
