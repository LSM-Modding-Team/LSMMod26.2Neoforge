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
 * Elementary desk: a single block (unlike the two-half desks) with a 9-slot inventory.
 * FACING only decides which side the open shelf looks at (the player who placed it).
 */
public class ElementaryDeskBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Hitbox in pixels (0-16), taken from elementary_desk.json: the 1-pixel top plate and four 2x2 legs.
    // The shape is symmetric under 90-degree turns, so it does not depend on FACING.
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 15, 0, 16, 16, 16),   // top plate
            Block.box(1, 0, 1, 3, 15, 3),      // legs
            Block.box(1, 0, 13, 3, 15, 15),
            Block.box(13, 0, 13, 15, 15, 15),
            Block.box(13, 0, 1, 15, 15, 3));

    public ElementaryDeskBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // ---- Block state ----------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // The open side faces the player who places the block
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
        return new ElementaryDeskBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // Opening the menu only runs on the server; the client just swings the arm
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof ElementaryDeskBlockEntity desk) {
            player.openMenu(desk);
        }
        return InteractionResult.SUCCESS;
    }
}
