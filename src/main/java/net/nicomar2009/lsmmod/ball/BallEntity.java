package net.nicomar2009.lsmmod.ball;

import java.util.UUID;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModEntities;

/** One persistent ball has two modes, so an impact never duplicates the inventory item. */
public final class BallEntity extends Entity implements ItemSupplier {
    private static final EntityDataAccessor<Boolean> PROJECTILE = SynchedEntityData.defineId(BallEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(BallEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEADING = SynchedEntityData.defineId(BallEntity.class, EntityDataSerializers.FLOAT);
    private final InterpolationHandler interpolation = new InterpolationHandler(this, 3);
    private float previousRoll;
    private double travelledDistance;
    private UUID owner;
    private int flightTicks, hitCooldown, pushCooldown;
    public BallEntity(EntityType<? extends BallEntity> type, Level level) { super(type, level); }
    public BallKind kind() {
        if (getType() == ModEntities.FOOTBALL_BALL.get()) return BallKind.FOOTBALL;
        if (getType() == ModEntities.BASKETBALL_BALL.get()) return BallKind.BASKETBALL;
        if (getType() == ModEntities.VOLLEYBALL_BALL.get()) return BallKind.VOLLEYBALL;
        return BallKind.PLASTIC;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(PROJECTILE, false); builder.define(ROLL, 0F); builder.define(HEADING, 0F); }
    @Override public ItemStack getItem() { return new ItemStack(kind().item()); }
    @Override public boolean isPickable() { return true; }
    @Override public boolean isPushable() { return !entityData.get(PROJECTILE); }
    public void launch(Player player, Vec3 direction) {
        owner = player.getUUID(); flightTicks = 0; travelledDistance = 0; entityData.set(PROJECTILE, true);
        setDeltaMovement(direction.normalize().scale(kind().projectileSpeed / 20.0));
    }
    private Entity source(ServerLevel level) { return owner == null ? null : level.getEntity(owner); }
    private void toy() {
        entityData.set(PROJECTILE, false); flightTicks = 0;
    }
    public float roll(float partialTick) {
        return previousRoll + (entityData.get(ROLL) - previousRoll) * partialTick;
    }
    @Override public InterpolationHandler getInterpolation() { return interpolation; }
    public float heading() { return entityData.get(HEADING); }

    @Override public void tick() {
        previousRoll = entityData.get(ROLL);
        super.tick();
        if (!(level() instanceof ServerLevel server)) { interpolation.interpolate(); return; }
        if (hitCooldown > 0) hitCooldown--;
        if (pushCooldown > 0) pushCooldown--;
        BallKind kind = kind();
        boolean projectile = entityData.get(PROJECTILE);
        Vec3 start = position();
        Vec3 motion = getDeltaMovement().add(0, -kind.gravity, 0);
        motion = new Vec3(motion.x, Math.clamp(motion.y, -1.5, 1.5), motion.z);
        boolean groundContact = false;
        // Short swept movement steps use the ball's full hitbox, including at high throwing speeds.
        int steps = Math.max(1, (int) Math.ceil(motion.length() / 0.18));
        for (int step = 0; step < steps; step++) {
            Vec3 requested = motion.scale(1.0 / steps);
            Vec3 before = position();
            move(MoverType.SELF, requested);
            Vec3 moved = position().subtract(before);
            if (projectile) travelledDistance += moved.length();
            boolean blockedX = Math.abs(requested.x - moved.x) > 0.000001;
            boolean blockedY = Math.abs(requested.y - moved.y) > 0.000001;
            boolean blockedZ = Math.abs(requested.z - moved.z) > 0.000001;
            if (blockedY && requested.y < 0) groundContact = true;
            if (blockedX || blockedY || blockedZ) {
                motion = new Vec3(blockedX ? -motion.x * kind.wallBounce : motion.x,
                        blockedY ? (motion.y < -0.10 ? -motion.y * kind.bounce : motion.y > 0 ? -motion.y * kind.wallBounce : 0) : motion.y,
                        blockedZ ? -motion.z * kind.wallBounce : motion.z);
                if (projectile) { toy(); projectile = false; }
            }
            if (motion.y > 0) groundContact = false;
            setOnGround(groundContact && motion.y <= 0);
            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.01),
                    e -> e.isAlive() && !e.isSpectator())) {
                if (projectile && !entity.getUUID().equals(owner)) {
                    entity.hurtServer(server, damageSources().thrown(this, source(server)), kind.damage(level().getDifficulty(), travelledDistance));
                    motion = motion.scale(-0.35).add(0, kind.kickHeight * 0.5, 0);
                    toy(); projectile = false; hitCooldown = 20; pushCooldown = 8;
                    break;
                }
                if (!projectile && kind == BallKind.VOLLEYBALL && !onGround()
                        && motion.horizontalDistanceSqr() > 0.0025 && hitCooldown == 0
                        && !(pushCooldown > 0 && entity.getUUID().equals(owner))) {
                    entity.hurtServer(server, damageSources().thrown(this, source(server)), 2);
                    hitCooldown = 20;
                }
            }
        }
        boolean rolling = onGround() && motion.y <= 0;
        double drag = rolling ? kind.groundDrag : kind.airDrag;
        Vec3 next = motion.multiply(drag, rolling ? 1 : kind.airDrag, drag);
        if (next.horizontalDistanceSqr() < 0.000004) next = new Vec3(0, next.y, 0);
        setDeltaMovement(next);
        double distance = position().subtract(start).horizontalDistance();
        if (distance > 0.00001) {
            // Radius 0.25: rolling distance / radius is the rotation angle in radians.
            entityData.set(ROLL, entityData.get(ROLL) + (float) Math.toDegrees(distance / 0.25));
            entityData.set(HEADING, (float) Math.toDegrees(Math.atan2(-motion.z, motion.x)));
        }
        if (projectile && ++flightTicks >= 400) toy();
        if (!projectile && pushCooldown == 0) {
            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.06),
                    e -> e.isAlive() && !e.isSpectator() && e.getDeltaMovement().horizontalDistanceSqr() > 0.00001)) {
                push(entity);
                if (pushCooldown > 0) break;
            }
        }
    }
    @Override public void push(Entity entity) {
        if (level().isClientSide() || entityData.get(PROJECTILE) || pushCooldown > 0) return;
        Vec3 direction = position().subtract(entity.position()).multiply(1, 0, 1);
        if (direction.lengthSqr() < 0.000001) direction = entity.getLookAngle().multiply(1, 0, 1);
        kick(entity, direction);
    }
    private void kick(Entity entity, Vec3 direction) {
        owner = entity.getUUID(); pushCooldown = 8;
        Vec3 horizontal = direction.multiply(1, 0, 1).normalize().scale(kind().toySpeed / 20.0);
        double up = kind().kickHeight;
        setDeltaMovement(horizontal.x, up, horizontal.z);
        hurtMarked = true;
    }
    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (entityData.get(PROJECTILE)) return false;
        if (source.getEntity() instanceof Player player) {
            if (kind() != BallKind.VOLLEYBALL || !onGround()) {
                kick(player, player.getLookAngle());
                if (kind() == BallKind.VOLLEYBALL) {
                    Vec3 look = player.getLookAngle().normalize();
                    setDeltaMovement(look.scale(kind().toySpeed / 20.0).add(0, 0.22, 0));
                }
            }
            return true;
        }
        return false; // Balls cannot be destroyed by incidental attacks.
    }
    @Override public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (!level().isClientSide() && !isRemoved()) {
            ItemStack item = getItem();
            if (!player.getInventory().add(item)) return InteractionResult.FAIL;
            discard();
        }
        return InteractionResult.SUCCESS;
    }
    @Override protected void addAdditionalSaveData(ValueOutput output) {
        output.putDouble("TravelledDistance", travelledDistance);
        output.putFloat("Roll", entityData.get(ROLL)); output.putFloat("Heading", entityData.get(HEADING));
        output.putBoolean("Projectile", entityData.get(PROJECTILE)); output.putInt("FlightTicks", flightTicks);
        if (owner != null) output.putString("Owner", owner.toString());
    }
    @Override protected void readAdditionalSaveData(ValueInput input) {
        double savedDistance = input.getDoubleOr("TravelledDistance", 0);
        travelledDistance = Double.isFinite(savedDistance) ? Math.clamp(savedDistance, 0.0, 10000.0) : 0;
        float savedRoll = input.getFloatOr("Roll", 0), savedHeading = input.getFloatOr("Heading", 0);
        entityData.set(ROLL, Float.isFinite(savedRoll) ? savedRoll : 0); previousRoll = entityData.get(ROLL);
        entityData.set(HEADING, Float.isFinite(savedHeading) ? savedHeading : 0);
        entityData.set(PROJECTILE, input.getBooleanOr("Projectile", false)); flightTicks = Math.clamp(input.getIntOr("FlightTicks", 0), 0, 400);
        try { owner = UUID.fromString(input.getStringOr("Owner", "")); } catch (IllegalArgumentException invalid) { owner = null; }
    }
}
