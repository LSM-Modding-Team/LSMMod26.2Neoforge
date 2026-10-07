package net.nicomar2009.lsmmod.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class StudentEntity extends SchoolNpcEntity {
    private static final EntityDataAccessor<String> GENDER =
            SynchedEntityData.defineId(StudentEntity.class, EntityDataSerializers.STRING);

    public StudentEntity(EntityType<? extends StudentEntity> type, Level level) { super(type, level); }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {
        super.defineSynchedData(data);
        data.define(GENDER, "male");
    }

    public String getGender() { return entityData.get(GENDER); }
    public boolean isFemale() { return "female".equals(getGender()); }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("gender", getGender());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        entityData.set(GENDER, "female".equalsIgnoreCase(input.getStringOr("gender", "male")) ? "female" : "male");
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            EntitySpawnReason reason, @Nullable SpawnGroupData groupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, groupData);
        if (reason == EntitySpawnReason.SPAWN_ITEM_USE || reason == EntitySpawnReason.DISPENSER) {
            entityData.set(GENDER, level.getRandom().nextBoolean() ? "female" : "male");
        }
        return result;
    }

    @Override
    public boolean isTeacher() { return false; }
}
