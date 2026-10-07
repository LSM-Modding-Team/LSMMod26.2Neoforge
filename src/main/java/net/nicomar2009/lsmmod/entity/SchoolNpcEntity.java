package net.nicomar2009.lsmmod.entity;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.sounds.SoundSource;
import net.nicomar2009.lsmmod.registry.ModItems;
import net.nicomar2009.lsmmod.registry.ModSounds;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.nicomar2009.lsmmod.item.InstrumentItem;

/** Shared school-NPC behavior; concrete entity types fix the student/teacher role. */
public abstract class SchoolNpcEntity extends PathfinderMob {
    // 0 = unassigned, 1..6 = primary, 7..11 = secondary.
    private static final EntityDataAccessor<Integer> CLASS_GRADE =
            SynchedEntityData.defineId(SchoolNpcEntity.class, EntityDataSerializers.INT);
    private static final ResourceKey<DamageType> MELEE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath("lsmmod", "school_npc_melee"));
    private static final int ATTACK_INTERVAL_TICKS = 20;
    public static final double MAX_PERCEPTION_RANGE = 128.0 * SchoolNpcStat.multiplier(5);
    private static final EntityDataAccessor<Integer> VITALITY =
            SynchedEntityData.defineId(SchoolNpcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STRENGTH =
            SynchedEntityData.defineId(SchoolNpcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SPEED =
            SynchedEntityData.defineId(SchoolNpcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PERCEPTION =
            SynchedEntityData.defineId(SchoolNpcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_SPEED =
            SynchedEntityData.defineId(SchoolNpcEntity.class, EntityDataSerializers.INT);

    private static final double NORMAL_SPEED = 0.25;
    // Ground move control uses this value for both speed and forward input (Mob#setSpeed).
    // Match the player's 0.1 movement attribute with the vanilla +30% sprint modifier.
    private static final double TEACHER_CHASE_SPEED = Math.sqrt(0.1 * 1.3);
    private static final double STUDENT_CHASE_SPEED = Math.sqrt(0.1 * 1.3 * 0.9);
    private static final Codec<Map<String, Boolean>> UNIFORM_MEMORY_CODEC = Codec.unboundedMap(Codec.STRING, Codec.BOOL);
    private final Set<String> witnessedAttackers = new HashSet<>();
    private final Map<String, Boolean> observedUniforms = new HashMap<>();

    protected SchoolNpcEntity(EntityType<? extends SchoolNpcEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
        setCanPickUpLoot(false);
        for (EquipmentSlot slot : EquipmentSlot.values()) setDropChance(slot, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        // Student defaults. Teachers override health and normal-difficulty damage.
        return createMobAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, NORMAL_SPEED)
                .add(Attributes.FOLLOW_RANGE, 128.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ATTACK_SPEED, 20.0 / ATTACK_INTERVAL_TICKS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {
        super.defineSynchedData(data);
        data.define(CLASS_GRADE, 0);
        for (SchoolNpcStat stat : SchoolNpcStat.values()) data.define(statAccessor(stat), 3);
    }

    private static EntityDataAccessor<Integer> statAccessor(SchoolNpcStat stat) {
        return switch (stat) {
            case VITALITY -> VITALITY;
            case STRENGTH -> STRENGTH;
            case SPEED -> SPEED;
            case PERCEPTION -> PERCEPTION;
            case ATTACK_SPEED -> ATTACK_SPEED;
        };
    }

    public int getStatLevel(SchoolNpcStat stat) { return entityData.get(statAccessor(stat)); }
    public double getStatMultiplier(SchoolNpcStat stat) { return SchoolNpcStat.multiplier(getStatLevel(stat)); }
    public double getPerceptionRange() { return getAttributeValue(Attributes.FOLLOW_RANGE); }
    public int getAttackIntervalTicks() {
        return Math.max(1, (int)Math.round(ATTACK_INTERVAL_TICKS / getStatMultiplier(SchoolNpcStat.ATTACK_SPEED)));
    }

    /** Explicit difficulty values: the custom damage type must not apply vanilla scaling again. */
    public double getMeleeDamage() {
        double base = switch (level().getDifficulty()) {
            case PEACEFUL -> 0.0;
            case EASY -> isTeacher() ? 3.5 : 1.5;
            case NORMAL -> isTeacher() ? 7.0 : 2.0;
            case HARD -> isTeacher() ? 10.5 : 2.5;
        };
        return base * getStatMultiplier(SchoolNpcStat.STRENGTH);
    }

    private void refreshStats() {
        getAttribute(Attributes.MAX_HEALTH).setBaseValue((isTeacher() ? 50.0 : 30.0)
                * getStatMultiplier(SchoolNpcStat.VITALITY));
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getMeleeDamage());
        // Keep both fresh and migrated NPC perception controlled solely by the configured level.
        getAttribute(Attributes.FOLLOW_RANGE).removeModifier(RANDOM_SPAWN_BONUS_ID);
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(128.0 * getStatMultiplier(SchoolNpcStat.PERCEPTION));
        getAttribute(Attributes.ATTACK_SPEED).setBaseValue(getStatMultiplier(SchoolNpcStat.ATTACK_SPEED));
        refreshMovementSpeed();
        if (getHealth() > getMaxHealth()) setHealth(getMaxHealth());
    }

    /** Roll once per egg-created NPC, before vanilla applies its optional entity-data overrides. */
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
        boolean fromEgg = spawnReason == EntitySpawnReason.SPAWN_ITEM_USE || spawnReason == EntitySpawnReason.DISPENSER;
        if (fromEgg) {
            for (SchoolNpcStat stat : SchoolNpcStat.values()) {
                entityData.set(statAccessor(stat), level.getRandom().nextInt(5) + 1);
            }
        }
        refreshStats();
        if (fromEgg) setHealth(getMaxHealth());
        return result;
    }

    public abstract boolean isTeacher();
    public final boolean isStudent() { return !isTeacher(); }

    @Override
    public Component getName() {
        if (getCustomName() != null) return super.getName();
        if (isTeacher()) return Component.translatable("entity.lsmmod.teacher");
        if (isStudent()) {
            int classroom = getClassGrade();
            if (classroom > 0) {
                String suffix = classroom <= 6 ? classroom + "p" : (classroom - 6) + "s";
                return Component.translatable("entity.lsmmod.student." + suffix
                        + (this instanceof StudentEntity student && student.isFemale() ? ".female" : ""));
            }
            return Component.translatable(this instanceof StudentEntity student && student.isFemale()
                    ? "entity.lsmmod.student.female" : "entity.lsmmod.student.male");
        }
        return super.getName();
    }

    public int getClassGrade() { return isStudent() ? entityData.get(CLASS_GRADE) : 0; }

    public void setClassroom(String level, int grade) {
        if (!isStudent()) {
            entityData.set(CLASS_GRADE, 0);
            return;
        }
        int encoded = "primary".equals(level) && grade >= 1 && grade <= 6 ? grade
                : "secondary".equals(level) && grade >= 1 && grade <= 5 ? grade + 6 : 0;
        boolean changed = getClassGrade() != encoded;
        if (changed && isStudent() && !level().isClientSide()) {
            setTarget(null);
            setLastHurtByMob(null);
            getNavigation().stop();
            setAggressive(false);
            refreshMovementSpeed();
        }
        entityData.set(CLASS_GRADE, encoded);
    }

    public boolean isSameClassroom(SchoolNpcEntity other) {
        return isStudent() && other.isStudent() && getClassGrade() != 0
                && getClassGrade() == other.getClassGrade();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        for (SchoolNpcStat stat : SchoolNpcStat.values()) output.putInt(stat.nbtKey(), getStatLevel(stat));
        if (isStudent()) {
            ValueOutput student = output.child("StudentData");
            int classroom = getClassGrade();
            student.putString("Level", classroom == 0 ? "unassigned" : classroom <= 6 ? "primary" : "secondary");
            student.putInt("Grade", classroom <= 6 ? classroom : classroom - 6);
        }
        if (isTeacher()) {
            output.store("WitnessedAttackers", Codec.STRING.listOf(), witnessedAttackers.stream().sorted().toList());
            output.store("ObservedUniforms", UNIFORM_MEMORY_CODEC, observedUniforms);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        for (SchoolNpcStat stat : SchoolNpcStat.values()) {
            entityData.set(statAccessor(stat), SchoolNpcStat.clampLevel(input.getIntOr(stat.nbtKey(), input.getIntOr(stat.legacyNbtKey(), 3))));
        }
        refreshStats();
        // Read again after setting the new capacity: super may have clamped against the old max health.
        setHealth(input.getFloatOr("Health", getMaxHealth()));
        if (isStudent()) {
            ValueInput student = input.childOrEmpty("StudentData");
            setClassroom(student.getStringOr("Level", "unassigned"), student.getIntOr("Grade", 0));
        } else {
            entityData.set(CLASS_GRADE, 0);
        }
        witnessedAttackers.clear();
        observedUniforms.clear();
        if (isTeacher()) {
            witnessedAttackers.addAll(input.read("WitnessedAttackers", Codec.STRING.listOf()).orElse(java.util.List.of()));
            observedUniforms.putAll(input.read("ObservedUniforms", UNIFORM_MEMORY_CODEC).orElse(Map.of()));
            enforceTeacherEquipment();
        }
        refreshMovementSpeed();
    }

    public static boolean hasCompleteSchoolUniform(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.FAKE_SCHOOL_HAIRCUT.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.UNIFORM_POLO.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.UNIFORM_PANTS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.UNIFORM_SHOES.get());
    }

    private boolean seesPlayer(Player player) {
        return distanceToSqr(player) <= getPerceptionRange() * getPerceptionRange() && getSensing().hasLineOfSight(player);
    }

    /** Only visible equipment updates this teacher's knowledge about a specific player. */
    public boolean shouldAttackPlayer(Player player) {
        if (!isTeacher() || player.isCreative() || player.isSpectator()) return false;
        String id = player.getUUID().toString();
        if (seesPlayer(player)) observedUniforms.put(id, hasCompleteSchoolUniform(player));
        return witnessedAttackers.contains(id) || !observedUniforms.getOrDefault(id, false);
    }

    /** Combat observers call this only for attacks against a teacher/student NPC. */
    public void witnessSchoolAttack(Player attacker, SchoolNpcEntity victim) {
        if (isStudent()) {
            if (victim != this && isSameClassroom(victim) && seesPlayer(attacker)
                    && distanceToSqr(victim) <= getPerceptionRange() * getPerceptionRange() && getSensing().hasLineOfSight(victim)
                    && !attacker.isCreative() && !attacker.isSpectator()) {
                setLastHurtByMob(attacker);
                setTarget(attacker);
            }
            return;
        }
        if (!isTeacher() || !seesPlayer(attacker)
                || distanceToSqr(victim) > getPerceptionRange() * getPerceptionRange()
                || (victim != this && !getSensing().hasLineOfSight(victim))) return;
        witnessedAttackers.add(attacker.getUUID().toString());
        if (shouldAttackPlayer(attacker)) setTarget(attacker);
    }

    private void refreshMovementSpeed() {
        double speed = getTarget() == null ? NORMAL_SPEED
                : isTeacher() ? TEACHER_CHASE_SPEED : isStudent() ? STUDENT_CHASE_SPEED : NORMAL_SPEED;
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed * getStatMultiplier(SchoolNpcStat.SPEED));
    }

    protected final void enforceTeacherEquipment() {
        if (!getMainHandItem().is(ModItems.WOODEN_RULER.get())) {
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WOODEN_RULER.get()));
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot != EquipmentSlot.MAINHAND && !getItemBySlot(slot).isEmpty()) setItemSlot(slot, ItemStack.EMPTY);
            setDropChance(slot, 0.0F);
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (isTeacher()) {
            enforceTeacherEquipment();
            // Refresh visible players even while neutral; unseen clothing changes cannot provoke this NPC.
            for (Player player : level.players()) {
                if (seesPlayer(player)) shouldAttackPlayer(player);
            }
            if (getTarget() instanceof Player player && !shouldAttackPlayer(player)) {
                setTarget(null);
                getNavigation().stop();
                setAggressive(false);
            }
        }
        refreshStats();
        if (!isTeacher() && !isStudent() && getTarget() != null) setTarget(null);
    }

    /** Role/difficulty damage with Strength; held-item modifiers and enchantments do not participate. */
    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        if (level.getDifficulty() == Difficulty.PEACEFUL || (!isTeacher() && !isStudent())) return false;
        if (isTeacher() && target instanceof Player player && !shouldAttackPlayer(player)) return false;
        DamageSource source = new DamageSource(damageSources().damageTypes.getOrThrow(MELEE_DAMAGE), this);
        boolean hurt = target.hurtServer(level, source, (float)getMeleeDamage());
        if (hurt) {
            setLastHurtMob(target);
            if (isTeacher()) level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.RULER_HIT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        }
        return hurt;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new RoleMeleeGoal());
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
        targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() { return isStudent() && super.canUse(); }
            @Override
            public boolean canContinueToUse() { return isStudent() && super.canContinueToUse(); }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true) {
            @Override
            public boolean canUse() { return isTeacher() && super.canUse(); }
            {
                // Selection and continuation both consult per-player uniform/violence memory.
                targetConditions.selector((candidate, serverLevel) ->
                        candidate instanceof Player player && shouldAttackPlayer(player));
            }
            @Override
            public boolean canContinueToUse() {
                return isTeacher() && getTarget() instanceof Player player
                        && shouldAttackPlayer(player) && super.canContinueToUse();
            }
        });
    }

    private final class RoleMeleeGoal extends MeleeAttackGoal {
        private int nextAttackTick;
        RoleMeleeGoal() { super(SchoolNpcEntity.this, 1.0, true); }

        @Override
        public boolean canUse() { return (isTeacher() || isStudent()) && super.canUse(); }

        @Override
        public boolean canContinueToUse() { return (isTeacher() || isStudent()) && super.canContinueToUse(); }

        @Override
        protected void checkAndPerformAttack(LivingEntity target) {
            // Stat-scaled absolute timer, independent of the held item's attack speed.
            if (!(isTeacher() && target instanceof Player player && !shouldAttackPlayer(player))
                    && tickCount >= nextAttackTick && isWithinMeleeAttackRange(target)
                    && getSensing().hasLineOfSight(target)) {
                nextAttackTick = tickCount + getAttackIntervalTicks();
                swing(InteractionHand.MAIN_HAND);
                doHurtTarget(getServerLevel(SchoolNpcEntity.this), target);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) { return false; }

    /** Consumables use LivingEntity's normal use pipeline; instruments expose a mob-safe hook. */
    public boolean useHeldItem(InteractionHand hand) {
        ItemStack stack = getItemInHand(hand);
        if (stack.isEmpty()) return false;
        if (isUsingItem()) { releaseUsingItem(); return true; }
        if (stack.getItem() instanceof InstrumentItem instrument) { instrument.playFor(this); return true; }
        if (stack.getUseDuration(this) > 0) { startUsingItem(hand); return true; }
        return false; // Player-only actions and world placement require their own future adapter.
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        if (isTeacher()) return InteractionResult.FAIL;
        if (player.isShiftKeyDown()) {
            return useHeldItem(hand) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        // This exchange bypasses normal vanilla equipping: enforce player-only clothing here.
        if (held.getItem() instanceof net.nicomar2009.lsmmod.item.SchoolUniformItem) return InteractionResult.PASS;
        EquipmentSlot slot = held.isEmpty() ? (hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)
                : getEquipmentSlotForItem(held);
        if (slot == EquipmentSlot.MAINHAND && hand == InteractionHand.OFF_HAND) slot = EquipmentSlot.OFFHAND;
        if (!canUseSlot(slot)) return InteractionResult.PASS;
        ItemStack previous = getItemBySlot(slot).copy();
        if (held.isEmpty()) {
            if (previous.isEmpty()) return InteractionResult.PASS;
            setItemSlot(slot, ItemStack.EMPTY);
            player.setItemInHand(hand, previous);
        } else {
            setItemSlot(slot, held.copyWithCount(1));
            if (!player.isCreative()) held.shrink(1);
            if (!previous.isEmpty() && !player.getInventory().add(previous)) player.drop(previous, false);
        }
        // Exchanging gear does not decide the future death-loot policy.
        setDropChance(slot, 0.0F);
        return InteractionResult.SUCCESS;
    }
}
