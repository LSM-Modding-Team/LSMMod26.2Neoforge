package net.nicomar2009.lsmmod.modules.tables_and_desks.block;
import net.nicomar2009.lsmmod.block.SimpleBlockOutline;
import net.nicomar2009.lsmmod.block.SharedChairRotations;
import net.nicomar2009.lsmmod.block.ExactVoxelShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
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
 * Two-block storage desk. Same placement/removal logic as the bed, but its only
 * functionality is storage (like a barrel).
 *
 * Layout: the LEFT half is the block that was clicked, the RIGHT half sits at
 * pos.relative(FACING). Both halves share ONE inventory, stored in the LEFT half's block entity.
 */
public class DeskBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DeskPart> PART = EnumProperty.create("part", DeskPart.class);

    // ---- Hitbox ---------------------------------------------------------------------------
    // Boxes are in pixels (0-16) for the model's native orientation (FACING = SOUTH, y rotation 0),
    // derived from studentsdeskleft.json after applying each element's 180-degree rotation.
    // The 1 px gap between the grille slats (y 12-13) is filled so the selection outline stays clean.
    private static final VoxelShape STUDENTS_LEFT_NATIVE = ExactVoxelShapes.union(
            ExactVoxelShapes.box(0, 15, 0, 16, 16, 16),     // tabletop
            ExactVoxelShapes.box(2, 0, 12, 4, 8, 14),       // leg
            ExactVoxelShapes.box(12, 0, 12, 14, 8, 14),     // leg
            ExactVoxelShapes.box(2, 3, 0, 4, 5, 12),        // low side rail
            ExactVoxelShapes.box(4, 3, 12, 12, 5, 14),      // low back rail
            ExactVoxelShapes.box(2, 8, 0, 4, 10, 14),       // frame (left side)
            ExactVoxelShapes.box(12, 8, 0, 14, 10, 14),     // frame (right side)
            ExactVoxelShapes.box(4, 8, 12, 12, 10, 14),     // frame (back)
            ExactVoxelShapes.box(4, 9, 0, 12, 10, 12),      // shelf
            ExactVoxelShapes.box(2, 10, 0, 4, 15, 14),      // left wall + post
            ExactVoxelShapes.box(4, 10, 11, 14, 15, 13),    // back wall
            ExactVoxelShapes.box(12, 10, 12, 14, 15, 14),   // back-right post
            ExactVoxelShapes.box(4, 10, 0, 13, 15, 0.5));   // end panel

    // Per-instance shapes: each desk type (student's / teacher's) passes its own native LEFT shape.
    // The RIGHT shape is always the LEFT one mirrored on the Z axis.
    // Indexed by Direction.get2DDataValue(): SOUTH=0, WEST=1, NORTH=2, EAST=3.
    // This is also the number of clockwise 90-degree turns, matching the blockstate "y" rotations.
    private final VoxelShape[] leftShapes;
    private final VoxelShape[] rightShapes;

    public DeskBlock(Properties properties) {
        this(properties, STUDENTS_LEFT_NATIVE);
    }

    protected DeskBlock(Properties properties, VoxelShape leftNative) {
        super(properties);
        this.leftShapes = SharedChairRotations.horizontal(leftNative);
        this.rightShapes = SharedChairRotations.horizontal(mirrorZ(leftNative));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(PART, DeskPart.LEFT));
    }

    // ---- Shape helpers --------------------------------------------------------------------

    private static VoxelShape mirrorZ(VoxelShape shape) {
        return Shapes.rotate(shape, com.mojang.math.OctahedralGroup.INVERT_Z);
    }

    /** Direction from the given half towards its other half. */
    private static Direction getNeighbourDirection(DeskPart part, Direction facing) {
        return part == DeskPart.LEFT ? facing : facing.getOpposite();
    }

    // ---- Block state ----------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return state.getValue(PART) == DeskPart.LEFT ? this.leftShapes[index] : this.rightShapes[index];
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    // ---- Placement ------------------------------------------------------------------------

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos otherPos = context.getClickedPos().relative(facing);
        Level level = context.getLevel();

        // Both positions must be free (and inside the world border), otherwise the desk can't be placed
        if (level.getBlockState(otherPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(otherPos)) {
            return this.defaultBlockState().setValue(FACING, facing).setValue(PART, DeskPart.LEFT);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            // Place the RIGHT half next to the LEFT one
            BlockPos otherPos = pos.relative(state.getValue(FACING));
            level.setBlock(otherPos, state.setValue(PART, DeskPart.RIGHT), Block.UPDATE_ALL);
        }
    }

    // ---- Keeping both halves in sync ------------------------------------------------------

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
                                     RandomSource random) {
        if (directionToNeighbour == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            // If the other half is gone (or is not our matching half), this half disappears too
            boolean otherHalfIsValid = neighbourState.is(this)
                    && neighbourState.getValue(PART) != state.getValue(PART)
                    && neighbourState.getValue(FACING) == state.getValue(FACING);
            return otherHalfIsValid ? state : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // Only the LEFT half drops the item (see loot table). In creative mode nothing drops from the RIGHT half,
        // so the LEFT half would still drop through the neighbour update: remove it silently instead.
        if (!level.isClientSide() && player.isCreative() && state.getValue(PART) == DeskPart.RIGHT) {
            BlockPos otherPos = pos.relative(getNeighbourDirection(DeskPart.RIGHT, state.getValue(FACING)));
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(PART) == DeskPart.LEFT) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(),
                        Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, otherPos, Block.getId(otherState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    // ---- Storage --------------------------------------------------------------------------

    /** Whether this desk has an inventory. Subclasses without storage (the teacher's desk) return false. */
    protected boolean hasStorage() {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Only the LEFT half has an inventory; the RIGHT half redirects to it
        return hasStorage() && state.getValue(PART) == DeskPart.LEFT ? new DeskBlockEntity(pos, state) : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // Decorative desks (no storage) ignore the click
        if (!hasStorage()) {
            return InteractionResult.PASS;
        }
        // Opening the menu only runs on the server; the client just swings the arm
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Whichever half was clicked, the inventory lives in the LEFT half
        BlockPos mainPos = state.getValue(PART) == DeskPart.LEFT
                ? pos
                : pos.relative(getNeighbourDirection(DeskPart.RIGHT, state.getValue(FACING)));

        if (level.getBlockEntity(mainPos) instanceof DeskBlockEntity desk) {
            player.openMenu(desk);
        }
        return InteractionResult.SUCCESS;
    }
}
