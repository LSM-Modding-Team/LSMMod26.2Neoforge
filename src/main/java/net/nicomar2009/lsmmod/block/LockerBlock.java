package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Locker: a full block with a 54-slot inventory. Its only functionality is storage.
 * FACING is only used so the front (door handles) looks at the player who placed it.
 */
public class LockerBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Hitbox in pixels (0-16) for the model's native orientation (FACING = EAST, blockstate y rotation 0),
    // derived from locker.json: the body plus the door frame, and the handles sticking out of the front.
    private static final VoxelShape NATIVE_SHAPE = Shapes.or(
            Block.box(0, 0, 0, 13, 16, 16),     // body and door frame
            Block.box(13, 5, 13, 15, 11, 14));  // door handles

    // Indexed by (Direction.get2DDataValue() + 1) % 4: EAST=0, SOUTH=1, WEST=2, NORTH=3.
    // This is also the number of clockwise 90-degree turns, matching the blockstate "y" rotations.
    private static final VoxelShape[] SHAPES = buildRotatedShapes(NATIVE_SHAPE);

    public LockerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // ---- Shape helpers --------------------------------------------------------------------

    private static VoxelShape[] buildRotatedShapes(VoxelShape native0) {
        VoxelShape[] shapes = new VoxelShape[4];
        shapes[0] = native0;
        for (int i = 1; i < 4; i++) {
            shapes[i] = rotateClockwise(shapes[i - 1]);
        }
        return shapes;
    }

    /** Rotates a shape 90 degrees clockwise (seen from above) around the block's vertical axis. */
    private static VoxelShape rotateClockwise(VoxelShape shape) {
        VoxelShape[] result = {Shapes.empty()};
        // (x, z) -> (1 - z, x)
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                result[0] = Shapes.or(result[0], Shapes.box(1.0D - z2, y1, x1, 1.0D - z1, y2, x2)));
        return result[0];
    }

    // ---- Block state ----------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[(state.getValue(FACING).get2DDataValue() + 1) % 4];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // The front faces the player who places the block
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    // ---- Storage --------------------------------------------------------------------------

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LockerBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // Opening the menu only runs on the server; the client just swings the arm
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof LockerBlockEntity locker) {
            player.openMenu(locker);
        }
        return InteractionResult.SUCCESS;
    }
}
