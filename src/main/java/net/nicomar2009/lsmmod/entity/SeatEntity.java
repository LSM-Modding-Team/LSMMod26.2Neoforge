package net.nicomar2009.lsmmod.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SeatEntity extends Entity {

    public SeatEntity(EntityType<? extends SeatEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // No synced data needed for a basic seat entity
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        // The seat is invulnerable
        return false;
    }

    @Override
    public boolean isPickable() {
        // Prevent the invisible seat from intercepting clicks and ray casts
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        // Remove the seat entity once nobody is sitting on it
        if (!this.level().isClientSide() && this.getPassengers().isEmpty()) {
            this.discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        // Seat entities are never persisted (see noSave() in ModEntities)
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        // Seat entities are never persisted (see noSave() in ModEntities)
    }
}
