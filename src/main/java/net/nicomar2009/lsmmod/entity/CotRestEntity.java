package net.nicomar2009.lsmmod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.block.InfirmaryCotBlock;

/** Invisible, stationary support for lying awake; never uses vanilla sleep state. */
public class CotRestEntity extends SeatEntity {
    public CotRestEntity(EntityType<? extends CotRestEntity> type, Level level) {
        super(type, level);
    }

    public Direction cotFacing() {
        return getDirection();
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction move) {
        // Sleeping dimensions have a different vehicle attachment from sitting dimensions.
        move.accept(passenger, getX(), getY(), getZ());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide() || isRemoved()) return;
        BlockPos root = blockPosition();
        BlockState state = level().getBlockState(root);
        if (!(state.getBlock() instanceof InfirmaryCotBlock cot)
                || state.getValue(InfirmaryCotBlock.SEGMENT) != 0
                || state.getValue(InfirmaryCotBlock.FACING) != cotFacing()
                || !cot.isComplete(level(), root, state)) {
            ejectPassengers();
            discard();
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        BlockPos root = blockPosition();
        BlockState state = level().getBlockState(root);
        int length = state.getBlock() instanceof InfirmaryCotBlock
                && state.getValue(InfirmaryCotBlock.EXTENDED) ? 3 : 2;
        Direction side = cotFacing().getClockWise();
        for (int segment = 0; segment < length; segment++) {
            BlockPos cell = root.relative(cotFacing(), segment);
            for (Direction direction : new Direction[] {side, side.getOpposite()}) {
                BlockPos candidate = cell.relative(direction);
                if (!level().hasChunkAt(candidate)) continue;
                Vec3 safe = DismountHelper.findSafeDismountLocation(passenger.getType(), level(), candidate, true);
                if (safe != null) {
                    passenger.setPose(Pose.STANDING);
                    return safe;
                }
            }
        }
        passenger.setPose(Pose.STANDING);
        return Vec3.atBottomCenterOf(root.above()).add(0.0D, 0.1D, 0.0D);
    }
}
