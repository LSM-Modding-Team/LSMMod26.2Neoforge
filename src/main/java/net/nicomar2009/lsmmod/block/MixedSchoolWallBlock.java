package net.nicomar2009.lsmmod.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Full opaque cube: grouped face materials rotate without neighbor properties. */
public class MixedSchoolWallBlock extends Block {
    // Index = X quarter turns * 4 + Y quarter turns, matching the model variants.
    public static final IntegerProperty ORIENTATION = IntegerProperty.create("orientation", 0, 15);
    private final int firstFaces;

    public MixedSchoolWallBlock(int firstFaces, Properties properties) {
        super(properties);
        if (firstFaces < 1 || firstFaces > 5) throw new IllegalArgumentException("Expected 1..5 faces");
        this.firstFaces = firstFaces;
        registerDefaultState(stateDefinition.any().setValue(ORIENTATION, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
    }

    private static int bit(Direction side) {
        return 1 << switch (side) {
            case NORTH -> 0; case EAST -> 1; case SOUTH -> 2;
            case WEST -> 3; case UP -> 4; case DOWN -> 5;
        };
    }

    private static Direction turnX(Direction side) {
        return switch (side) {
            case UP -> Direction.NORTH; case NORTH -> Direction.DOWN;
            case DOWN -> Direction.SOUTH; case SOUTH -> Direction.UP;
            default -> side;
        };
    }

    private int faceMask(int orientation) {
        int grouped = Math.min(firstFaces, 6 - firstFaces);
        int mask = 0;
        for (Direction original : new Direction[]{Direction.NORTH, Direction.EAST, Direction.UP}) {
            if (--grouped < 0) break;
            Direction side = original;
            for (int i = 0; i < orientation / 4; i++) side = turnX(side);
            for (int i = 0; i < orientation % 4; i++)
                if (side.getAxis().isHorizontal()) side = side.getClockWise();
            mask |= bit(side);
        }
        return firstFaces > 3 ? 63 ^ mask : mask;
    }

    private BlockState withMask(BlockState state, int mask) {
        for (int orientation = 0; orientation < 16; orientation++)
            if (faceMask(orientation) == mask) return state.setValue(ORIENTATION, orientation);
        throw new IllegalArgumentException("Unsupported grouped wall face mask: " + mask);
    }

    private static Direction toward(Direction.Axis axis, double value) {
        return switch (axis) {
            case X -> value >= 0 ? Direction.EAST : Direction.WEST;
            case Y -> value >= 0 ? Direction.UP : Direction.DOWN;
            case Z -> value >= 0 ? Direction.SOUTH : Direction.NORTH;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        int grouped = Math.min(firstFaces, 6 - firstFaces), mask = bit(face);
        double[] hit = {context.getClickLocation().x - context.getClickedPos().getX() - .5,
                context.getClickLocation().y - context.getClickedPos().getY() - .5,
                context.getClickLocation().z - context.getClickedPos().getZ() - .5};
        Direction closest = null;
        double distance = -1;
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis == face.getAxis()) continue;
            int index = switch (axis) { case X -> 0; case Y -> 1; case Z -> 2; };
            Direction side = toward(axis, hit[index]);
            if (grouped == 3) mask |= bit(side);
            if (Math.abs(hit[index]) > distance) { distance = Math.abs(hit[index]); closest = side; }
        }
        if (grouped == 2) mask |= bit(closest);
        return withMask(defaultBlockState(), firstFaces > 3 ? 63 ^ mask : mask);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        int original = faceMask(state.getValue(ORIENTATION)), mask = 0;
        for (Direction side : Direction.values())
            if ((original & bit(side)) != 0) mask |= bit(rotation.rotate(side));
        return withMask(state, mask);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        int original = faceMask(state.getValue(ORIENTATION)), mask = 0;
        for (Direction side : Direction.values())
            if ((original & bit(side)) != 0) mask |= bit(mirror.mirror(side));
        return withMask(state, mask);
    }
}
