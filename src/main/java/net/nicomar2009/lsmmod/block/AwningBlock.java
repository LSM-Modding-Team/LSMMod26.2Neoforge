package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Thin, gently curved cloth; a second cell reserves any portion above a block boundary. */
public class AwningBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty START = IntegerProperty.create("start", 0, 16);
    public static final IntegerProperty END = IntegerProperty.create("end", 0, 16);
    public static final BooleanProperty UPPER = BooleanProperty.create("upper");
    private final String variant;
    public String variant() { return variant; }
    public AwningBlock(Properties properties) { this(properties, "awning"); }
    public AwningBlock(Properties properties, String variant) {
        super(properties);
        this.variant = variant;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(START, 7).setValue(END, 7).setValue(UPPER, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, START, END, UPPER); }
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        double offset = state.getValue(UPPER) ? 16 : 0;
        Direction direction = state.getValue(FACING);
        for (int i = 0; i < 8; i++) {
            double top = 2 * (state.getValue(START) + (state.getValue(END) - state.getValue(START)) * (i + 0.5) / 8.0) - offset;
            double low = Math.max(0, top - 0.75), high = Math.min(16, top);
            if (low >= high) continue;
            double z1 = i * 2, z2 = z1 + 2;
            VoxelShape piece = switch (direction) {
                case SOUTH -> Block.box(0, low, z1, 16, high, z2);
                case NORTH -> Block.box(0, low, 16-z2, 16, high, 16-z1);
                case EAST -> Block.box(z1, low, 0, z2, high, 16);
                case WEST -> Block.box(16-z2, low, 0, 16-z1, high, 16);
                default -> Shapes.empty();
            };
            shape = Shapes.or(shape, piece);
        }
        return shape;
    }
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof AwningBlockEntity part)
            AwningStructure.removed(level, part, !player.isCreative());
        return super.playerWillDestroy(level, pos, state, player);
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
