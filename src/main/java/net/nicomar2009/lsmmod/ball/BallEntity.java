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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModEntities;

/** One persistent ball has two modes, so an impact never duplicates the inventory item. */
public final class BallEntity extends Entity implements ItemSupplier {
    private static final EntityDataAccessor<Boolean> PROJECTILE = SynchedEntityData.defineId(BallEntity.class, EntityDataSerializers.BOOLEAN);
    private UUID owner;
    private int flightTicks, hitCooldown, pushCooldown;
    public BallEntity(EntityType<? extends BallEntity> type, Level level) { super(type, level); }
    public BallKind kind() {
        if (getType() == ModEntities.FOOTBALL_BALL.get()) return BallKind.FOOTBALL;
        if (getType() == ModEntities.BASKETBALL_BALL.get()) return BallKind.BASKETBALL;
        if (getType() == ModEntities.VOLLEYBALL_BALL.get()) return BallKind.VOLLEYBALL;
        return BallKind.PLASTIC;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(PROJECTILE, false); }
    @Override public ItemStack getItem() { return new ItemStack(kind().item()); }
    @Override public boolean isPickable() { return true; }
    @Override public boolean isPushable() { return !entityData.get(PROJECTILE); }
    public void launch(Player player, Vec3 direction) {
        owner = player.getUUID(); flightTicks = 0; entityData.set(PROJECTILE, true);
        setDeltaMovement(direction.normalize().scale(kind().projectileSpeed / 20.0));
    }
    private Entity source(ServerLevel level) { return owner == null ? null : level.getEntity(owner); }
    private void toy() {
        entityData.set(PROJECTILE, false); flightTicks = 0; setDeltaMovement(Vec3.ZERO);
    }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return; // Position/velocity are synchronized by the server.
        if (hitCooldown > 0) hitCooldown--;
        if (pushCooldown > 0) pushCooldown--;
        if (entityData.get(PROJECTILE)) {
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this,
                    entity -> entity instanceof LivingEntity && entity.isAlive() && !entity.getUUID().equals(owner));
            if (hit.getType() != HitResult.Type.MISS) {
                if (hit instanceof EntityHitResult entityHit) {
                    entityHit.getEntity().hurtServer(server, damageSources().thrown(this, source(server)), kind().damage(level().getDifficulty()));
                }
                Vec3 point = hit.getLocation().subtract(getDeltaMovement().normalize().scale(0.27));
                setPos(point.x, point.y, point.z);
                toy(); // The same entity remains available for play or pickup after hitting.
            } else {
                Vec3 next = position().add(getDeltaMovement()); setPos(next.x, next.y, next.z);
                if (++flightTicks >= 400) toy();
            }
            return;
        }
        Vec3 motion = getDeltaMovement();
        double previousY = motion.y;
        if (!onGround()) motion = motion.add(0, -0.035, 0);
        Vec3 beforeMove = position();
        move(MoverType.SELF, motion);
        double bounce = kind() == BallKind.BASKETBALL ? 0.76 : kind() == BallKind.VOLLEYBALL ? 0.68 : 0;
        double vertical = onGround() ? (bounce > 0 && previousY < -0.07 ? -previousY * bounce : 0) : motion.y;
        if (horizontalCollision) {
            Vec3 moved = position().subtract(beforeMove);
            motion = new Vec3(Math.abs(moved.x - motion.x) > 0.000001 ? -motion.x * 0.65 : motion.x,
                    motion.y, Math.abs(moved.z - motion.z) > 0.000001 ? -motion.z * 0.65 : motion.z);
        }
        double drag = onGround() ? 0.98 : 0.997;
        Vec3 next = new Vec3(motion.x * drag, vertical, motion.z * drag);
        if (next.horizontalDistanceSqr() < 0.000001) next = new Vec3(0, next.y, 0);
        setDeltaMovement(next);
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.06), e -> e.isAlive() && !e.isSpectator())) {
            if (kind() == BallKind.VOLLEYBALL && !onGround() && next.horizontalDistanceSqr() > 0.0025 && hitCooldown == 0
                    && !(pushCooldown > 0 && entity.getUUID().equals(owner))) {
                entity.hurtServer(server, damageSources().thrown(this, source(server)), 2);
                hitCooldown = 20;
            }
            if (pushCooldown == 0 && entity.getDeltaMovement().horizontalDistanceSqr() > 0.00001) push(entity);
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
        double up = kind() == BallKind.BASKETBALL ? 0.18 : kind() == BallKind.VOLLEYBALL ? 0.28 : 0;
        setDeltaMovement(horizontal.x, up, horizontal.z);
        hurtMarked = true;
    }
    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (entityData.get(PROJECTILE)) return false;
        if (source.getEntity() instanceof Player player) {
            if (kind() != BallKind.VOLLEYBALL || !onGround()) kick(player, player.getLookAngle());
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
        output.putBoolean("Projectile", entityData.get(PROJECTILE)); output.putInt("FlightTicks", flightTicks);
        if (owner != null) output.putString("Owner", owner.toString());
    }
    @Override protected void readAdditionalSaveData(ValueInput input) {
        entityData.set(PROJECTILE, input.getBooleanOr("Projectile", false)); flightTicks = Math.clamp(input.getIntOr("FlightTicks", 0), 0, 400);
        try { owner = UUID.fromString(input.getStringOr("Owner", "")); } catch (IllegalArgumentException invalid) { owner = null; }
    }
}
