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

    private static final EntityDataAccessor<Boolean> FREE_HAIR =
            SynchedEntityData.defineId(StudentEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> SKIN_COLOR =
            SynchedEntityData.defineId(StudentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EYE_COLOR =
            SynchedEntityData.defineId(StudentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HAIRCUT_MALE =
            SynchedEntityData.defineId(StudentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HAIRCUT_FEMALE =
            SynchedEntityData.defineId(StudentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GLASSES_TYPE =
            SynchedEntityData.defineId(StudentEntity.class, EntityDataSerializers.INT);

    public StudentEntity(EntityType<? extends StudentEntity> type, Level level) { super(type, level); }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {
        super.defineSynchedData(data);
        data.define(GENDER, "male");
        data.define(FREE_HAIR, false);
        data.define(SKIN_COLOR, -1);
        data.define(EYE_COLOR, -1);
        data.define(HAIRCUT_MALE, -1);
        data.define(HAIRCUT_FEMALE, -1);
        data.define(GLASSES_TYPE, -1);
    }

    public String getGender() { return entityData.get(GENDER); }
    public boolean isFemale() { return "female".equals(getGender()); }

    public boolean hasFreeHair() { return isFemale() && entityData.get(FREE_HAIR); }

    public int getSkinColor() { return entityData.get(SKIN_COLOR); }
    public int getEyeColor() { return entityData.get(EYE_COLOR); }
    public int getHaircut() { return entityData.get(isFemale() ? HAIRCUT_FEMALE : HAIRCUT_MALE); }
    public int getGlassesType() { return entityData.get(GLASSES_TYPE); }

    private static int appearanceLevel(int value, int maximum) {
        return value < 0 ? -1 : Math.min(value, maximum);
    }

    private void saveAppearance(ValueOutput output, String key, int value) {
        if (value >= 0) output.putInt(key, value);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("gender", getGender());
        if (isFemale()) output.putBoolean("freeHair", hasFreeHair());
        saveAppearance(output, "skinColor", getSkinColor());
        saveAppearance(output, "eyeColor", getEyeColor());
        saveAppearance(output, isFemale() ? "haircutFemale" : "haircutMale", getHaircut());
        saveAppearance(output, "glassesType", getGlassesType());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        entityData.set(GENDER, "female".equalsIgnoreCase(input.getStringOr("gender", "male")) ? "female" : "male");
        entityData.set(FREE_HAIR, isFemale() && input.getBooleanOr("freeHair", false));
        entityData.set(SKIN_COLOR, appearanceLevel(input.getIntOr("skinColor", -1), 63));
        entityData.set(EYE_COLOR, appearanceLevel(input.getIntOr("eyeColor", -1), 7));
        entityData.set(HAIRCUT_MALE, isFemale() ? -1 : appearanceLevel(input.getIntOr("haircutMale", -1), 31));
        entityData.set(HAIRCUT_FEMALE, isFemale() ? appearanceLevel(input.getIntOr("haircutFemale", -1), 31) : -1);
        entityData.set(GLASSES_TYPE, appearanceLevel(input.getIntOr("glassesType", -1), 8));
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
