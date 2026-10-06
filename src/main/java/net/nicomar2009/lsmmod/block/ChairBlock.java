package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nicomar2009.lsmmod.entity.SeatEntity;
import net.nicomar2009.lsmmod.registry.ModEntities;

import java.util.List;

/**
 * Sittable classroom chair.
 *
 * The chair has a FACING property because its blockstate files select the model (and its y rotation)
 * by "facing". Without this property the blockstate variants never match any state and the game
 * renders the missing-model placeholder instead of the chair textures.
 *
 * FACING is the direction the sitter looks at, so the chair's front always faces the player who placed it.
 */
public class ChairBlock extends Block {
    /** Horizontal direction the chair is facing (the direction the sitter looks at). */
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    /** Height (in model pixels, out of 16) at which the player's seat should rest. */
    private static final double SEAT_HEIGHT_PIXELS = 7.0D;

    // Hitbox in pixels (0-16) for the model's native orientation (FACING = SOUTH, blockstate y rotation 0),
    // derived from classroom_chair_base.json: the backrest is on the north side, the sitter looks south.
    private static final VoxelShape NATIVE_SHAPE = Shapes.or(
            Block.box(2, 0, 4, 4, 5, 6),        // front-left leg
            Block.box(11, 0, 4, 13, 5, 6),      // front-right leg
            Block.box(2, 0, 12, 4, 5, 14),      // back-left leg
            Block.box(11, 0, 12, 13, 5, 14),    // back-right leg
            Block.box(2, 5, 3, 13, 7, 14),      // seat
            Block.box(2, 6, 1, 13, 16, 3));     // backrest

    // Indexed by Direction.get2DDataValue(): SOUTH=0, WEST=1, NORTH=2, EAST=3.
    // This is also the number of clockwise 90-degree turns, matching the blockstate "y" rotations.
    private final VoxelShape[] shapes;

    public ChairBlock(Properties properties) {
        this(properties, NATIVE_SHAPE);
    }

    /** Allows new chair models to provide their own collision and selection geometry. */
    public ChairBlock(Properties properties, VoxelShape nativeShape) {
        super(properties);
        this.shapes = buildRotatedShapes(nativeShape);
        // Default orientation matches the model's native orientation
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH));
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // The chair faces the player: the sitter looks in the direction opposite to the player's view
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

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes[state.getValue(FACING).get2DDataValue()];
    }

    /** Seat surface height; taller stools override it. */
    protected double getSeatHeightPixels() {
        return SEAT_HEIGHT_PIXELS;
    }

    // ---- Sitting --------------------------------------------------------------------------

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // Seating logic only runs on the server; the client just swings the arm
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Look for a seat entity that already exists inside this block
        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));

        if (seats.isEmpty()) {
            // The seat entity's PASSENGER attachment is at its feet (y = 0), and the player's
            // VEHICLE attachment (the hip/seat contact point) is aligned with it, so placing the
            // entity at 7/16 of the block puts the player's seat exactly at pixel 7.
            double seatY = pos.getY() + getSeatHeightPixels() / 16.0D;

            SeatEntity seat = new SeatEntity(ModEntities.SEAT.get(), level);
            seat.setPos(pos.getX() + 0.5D, seatY, pos.getZ() + 0.5D);
            level.addFreshEntity(seat);
            player.startRiding(seat);
        } else {
            // Reuse the existing seat only if nobody is sitting on it
            SeatEntity seat = seats.get(0);
            if (seat.getPassengers().isEmpty()) {
                player.startRiding(seat);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
