package net.nicomar2009.lsmmod.ball;

import java.util.UUID;
import java.util.Comparator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModEntities;
import net.nicomar2009.lsmmod.block.SportsGoalBlock;
import net.nicomar2009.lsmmod.network.FootballInputPayload;
import net.nicomar2009.lsmmod.rules.FootballRules;

/** One persistent ball has two modes, so an impact never duplicates the inventory item. */
public final class BallEntity extends Entity implements ItemSupplier {
    private static final EntityDataAccessor<Boolean> PROJECTILE = SynchedEntityData.defineId(BallEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(BallEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEADING = SynchedEntityData.defineId(BallEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FOOTBALL_SHOT = SynchedEntityData.defineId(BallEntity.class, EntityDataSerializers.BOOLEAN);
    private final InterpolationHandler interpolation = new InterpolationHandler(this, 3);
    private float previousRoll;
    private double travelledDistance;
    private UUID owner;
    private int flightTicks, hitCooldown, pushCooldown;
    private UUID dribbler, chargingPlayer;
    private long chargeStart, lastChargeInput, shotTime;
    private long lastSpinTick;
    private int chargeKeys, lastFollowSample, shotCooldown;
    private double shotCharge;
    private FootballRules.Spin spin = new FootballRules.Spin(0, 0);
    private FootballRules.Spin preparedSpin = new FootballRules.Spin(0, 0);
    public BallEntity(EntityType<? extends BallEntity> type, Level level) { super(type, level); }
    public BallKind kind() {
        if (getType() == ModEntities.FOOTBALL_BALL.get()) return BallKind.FOOTBALL;
        if (getType() == ModEntities.BASKETBALL_BALL.get()) return BallKind.BASKETBALL;
        if (getType() == ModEntities.VOLLEYBALL_BALL.get()) return BallKind.VOLLEYBALL;
        return BallKind.PLASTIC;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PROJECTILE, false); builder.define(ROLL, 0F); builder.define(HEADING, 0F); builder.define(FOOTBALL_SHOT, false);
    }
    @Override public ItemStack getItem() { return new ItemStack(kind().item()); }
    @Override public boolean isPickable() { return true; }
    @Override public boolean isPushable() { return kind() != BallKind.FOOTBALL && !entityData.get(PROJECTILE); }
    public void launch(Player player, Vec3 direction) {
        cancelCharge(); dribbler = null; clearFootballShot();
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

    /** Feet-level reach is shared with client target selection, never general attack reach. */
    public boolean isAtFeet(Player player) {
        double height = getY() - player.getY();
        return kind() == BallKind.FOOTBALL && !isRemoved() && !entityData.get(PROJECTILE)
                && player.isAlive() && !player.isSpectator() && player.onGround() && !player.isPassenger()
                && !player.getAbilities().flying && height >= -0.20 && height <= 0.55
                && position().subtract(player.position()).horizontalDistanceSqr() <= 1.60 * 1.60
                && getDeltaMovement().lengthSqr() <= FootballRules.CONTROL_SPEED * FootballRules.CONTROL_SPEED;
    }

    private boolean clearFeetPath(Player player) {
        Vec3 offset = player.position().subtract(position()).multiply(1, 0, 1);
        int steps = Math.max(1, (int) Math.ceil(offset.length() / 0.18));
        for (int i = 1; i <= steps; i++) {
            if (level().getBlockCollisions(this, getBoundingBox().move(offset.scale(i / (double) steps))).iterator().hasNext()) return false;
        }
        return true;
    }

    private boolean canCharge(Player player) {
        return isAtFeet(player) && shotCooldown == 0 && clearFeetPath(player)
                && (dribbler == null || dribbler.equals(player.getUUID()));
    }

    public void footballInput(ServerPlayer player, int action, int keys, float yaw, float pitch) {
        if (kind() != BallKind.FOOTBALL || isRemoved()) return;
        long now = level().getGameTime();
        if (action >= FootballInputPayload.FOLLOW) {
            int sample = action - FootballInputPayload.FOLLOW + 1;
            if (!entityData.get(FOOTBALL_SHOT) || !player.getUUID().equals(owner) || sample != lastFollowSample + 1
                    || now - shotTime > FootballRules.FOLLOW_TICKS || now < shotTime) return;
            spin = spin.add(FootballRules.spin(keys, sample)).bounded(); lastFollowSample = sample;
            return;
        }
        if (action == FootballInputPayload.START) {
            if (chargingPlayer != null || !canCharge(player)) return;
            clearFootballShot(); chargingPlayer = player.getUUID(); chargeStart = now; lastChargeInput = now;
            preparedSpin = new FootballRules.Spin(0, 0); lastSpinTick = now;
            chargeKeys = keys; dribbler = player.getUUID(); FootballMovement.charging(player, 0); showCharge(player, now); return;
        }
        if (!player.getUUID().equals(chargingPlayer)) return;
        if (action == FootballInputPayload.CANCEL) { cancelCharge(); return; }
        if (!canCharge(player) || now - lastChargeInput > 10) { cancelCharge(); return; }
        accumulateSpin(now);
        if (action == FootballInputPayload.HOLD) { lastChargeInput = now; chargeKeys = keys; return; }
        if (action != FootballInputPayload.RELEASE) return;
        long held = now - chargeStart;
        chargingPlayer = null; dribbler = null; owner = player.getUUID(); shotTime = now; lastFollowSample = 0;
        shotCooldown = 8; pushCooldown = 8; spin = FootballRules.releaseSpin(preparedSpin, keys);
        shotCharge = FootballRules.charge(held); entityData.set(FOOTBALL_SHOT, true); FootballMovement.released(player, held);
        double yawRadians = Math.toRadians(yaw), pitchRadians = Math.toRadians(pitch);
        Vec3 look = new Vec3(-Math.sin(yawRadians) * Math.cos(pitchRadians), -Math.sin(pitchRadians),
                Math.cos(yawRadians) * Math.cos(pitchRadians));
        Vec3 eye = player.getEyePosition(), end = eye.add(look.scale(64));
        Vec3 target = level().clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getLocation();
        Vec3 center = getBoundingBox().getCenter();
        var direction = FootballRules.aimDirection(center.x, center.y, center.z, target.x, target.y, target.z);
        Vec3 aim = new Vec3(direction.x(), direction.y(), direction.z());
        if (aim.lengthSqr() < 0.000001) aim = new Vec3(-Math.sin(yawRadians), 0, Math.cos(yawRadians));
        setDeltaMovement(aim.scale(FootballRules.shotSpeed(held))); hurtMarked = true;
        player.sendOverlayMessage(Component.translatable("message.lsmmod.football_shot", (int) Math.round(FootballRules.charge(held) * 100)));
    }

    private void clearFootballShot() {
        entityData.set(FOOTBALL_SHOT, false); spin = new FootballRules.Spin(0, 0);
    }

    private void accumulateSpin(long now) {
        // Server ticks, not packet count, determine how long a direction was held.
        for (long tick = lastSpinTick + 1; tick <= now; tick++)
            preparedSpin = FootballRules.buildSpin(preparedSpin, chargeKeys, tick - chargeStart);
        lastSpinTick = now;
    }

    private void cancelCharge() {
        if (chargingPlayer != null && level() instanceof ServerLevel server
                && server.getEntity(chargingPlayer) instanceof ServerPlayer player) {
            FootballMovement.cancel(player);
            player.sendOverlayMessage(Component.translatable("message.lsmmod.football_cancelled"));
        }
        chargingPlayer = null;
    }

    private void showCharge(ServerPlayer player, long now) {
        int percent = (int) Math.round(FootballRules.charge(now - chargeStart) * 100), filled = percent / 10;
        FootballRules.Spin preview = FootballRules.releaseSpin(preparedSpin, chargeKeys);
        player.sendOverlayMessage(Component.translatable("message.lsmmod.football_charge",
                "▰".repeat(filled) + "▱".repeat(10 - filled), percent,
                Component.translatable("message.lsmmod.football_side_" + (preview.side() < 0 ? "left" : preview.side() > 0 ? "right" : "none")),
                Component.translatable("message.lsmmod.football_top_" + (preview.top() < 0 ? "back" : preview.top() > 0 ? "front" : "none"))));
    }

    private boolean canDribble(Player player) {
        if (!isAtFeet(player) || !clearFeetPath(player)) return false;
        Vec3 offset = position().subtract(player.position()).multiply(1, 0, 1);
        double reach = player.isSprinting() ? 1.38 : player.isShiftKeyDown() ? 0.95 : 1.20;
        if (offset.lengthSqr() > reach * reach) return false;
        Vec3 movement = player.getKnownMovement().multiply(1, 0, 1);
        if (offset.lengthSqr() <= 0.65 * 0.65 || (player.getUUID().equals(dribbler) && movement.lengthSqr() < 0.0001)) return true;
        Vec3 direction = movement.lengthSqr() > 0.0001 ? movement : player.getLookAngle().multiply(1, 0, 1);
        return direction.normalize().dot(offset.normalize()) >= -0.25;
    }

    private void updateFootball(ServerLevel server) {
        if (shotCooldown > 0) shotCooldown--;
        long now = level().getGameTime();
        if (chargingPlayer != null) {
            if (!(server.getEntity(chargingPlayer) instanceof ServerPlayer player) || !canCharge(player)
                    || now - lastChargeInput > 10) cancelCharge();
            else {
                accumulateSpin(now); FootballMovement.charging(player, now - chargeStart);
                if (now % 4 == 0) showCharge(player, now);
            }
        }
        if (entityData.get(PROJECTILE) || shotCooldown > 0 || !onGround()
                || getDeltaMovement().lengthSqr() > FootballRules.CONTROL_SPEED * FootballRules.CONTROL_SPEED) {
            dribbler = null; return;
        }
        Player player = server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(1.4, 0.4, 1.4), this::canDribble)
                .stream().min(Comparator.comparingDouble(p -> distanceToSqr(p) - (p.getUUID().equals(dribbler) ? 0.12 : 0))).orElse(null);
        if (player == null) { dribbler = null; return; }
        if (chargingPlayer != null && !player.getUUID().equals(chargingPlayer)) cancelCharge();
        dribbler = player.getUUID(); clearFootballShot();
        if (pushCooldown > 0) return;
        Vec3 movement = player.getKnownMovement().multiply(1, 0, 1);
        Vec3 velocity;
        if (movement.lengthSqr() < 0.0001) {
            velocity = getDeltaMovement().multiply(0.30, 1, 0.30);
        } else {
            boolean sprint = player.isSprinting(), sneak = player.isShiftKeyDown();
            Vec3 target = player.position().add(movement.normalize().scale(FootballRules.touchDistance(sprint, sneak)));
            Vec3 desired = movement.add(target.subtract(position()).multiply(0.32, 0, 0.32));
            velocity = getDeltaMovement().multiply(0.30, 0, 0.30).add(desired.scale(0.70));
            double maxSpeed = FootballRules.touchSpeed(movement.length(), sprint, sneak);
            if (velocity.horizontalDistanceSqr() > maxSpeed * maxSpeed) velocity = velocity.normalize().scale(maxSpeed);
            velocity = new Vec3(velocity.x, Math.max(0, getDeltaMovement().y), velocity.z);
        }
        setDeltaMovement(velocity); owner = player.getUUID(); pushCooldown = player.isSprinting() ? 2 : 3; hurtMarked = true;
    }

    @Override public void tick() {
        previousRoll = entityData.get(ROLL);
        super.tick();
        if (!(level() instanceof ServerLevel server)) { interpolation.interpolate(); return; }
        if (hitCooldown > 0) hitCooldown--;
        if (pushCooldown > 0) pushCooldown--;
        BallKind kind = kind();
        if (kind == BallKind.FOOTBALL) updateFootball(server);
        boolean projectile = entityData.get(PROJECTILE);
        Vec3 start = position();
        Vec3 motion = getDeltaMovement();
        if (entityData.get(FOOTBALL_SHOT)) {
            double angle = FootballRules.curveAngle(motion.horizontalDistance(), spin, shotCharge) * (onGround() ? 0.25 : 1);
            motion = new Vec3(motion.x * Math.cos(angle) - motion.z * Math.sin(angle),
                    motion.y + (onGround() ? 0 : FootballRules.lift(motion.horizontalDistance(), spin, shotCharge)),
                    motion.x * Math.sin(angle) + motion.z * Math.cos(angle));
            spin = spin.decay(onGround() ? 0.90 : 0.985);
        }
        motion = motion.add(0, -kind.gravity, 0);
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
                boolean net = SportsGoalBlock.touchesFootballNet(server, getBoundingBox(), requested, blockedX, blockedY, blockedZ);
                motion = net ? new Vec3(blockedX ? -motion.x * FootballRules.NET_REBOUND : motion.x * FootballRules.NET_DRAG,
                        blockedY ? -motion.y * FootballRules.NET_REBOUND : motion.y * FootballRules.NET_DRAG,
                        blockedZ ? -motion.z * FootballRules.NET_REBOUND : motion.z * FootballRules.NET_DRAG)
                        : new Vec3(blockedX ? -motion.x * kind.wallBounce : motion.x,
                        blockedY ? (motion.y < -0.10 ? -motion.y * kind.bounce : motion.y > 0 ? -motion.y * kind.wallBounce : 0) : motion.y,
                        blockedZ ? -motion.z * kind.wallBounce : motion.z);
                if (net) { clearFootballShot(); cancelCharge(); dribbler = null; hurtMarked = true; }
                else if (entityData.get(FOOTBALL_SHOT) && (blockedX || blockedZ || requested.y > 0)) spin = spin.decay(0.45);
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
                if (entityData.get(FOOTBALL_SHOT) && hitCooldown == 0
                        && !(shotCooldown > 0 && entity.getUUID().equals(owner))) {
                    Vec3 normal = position().subtract(entity.position()).multiply(1, 0, 1).normalize();
                    double incoming = motion.dot(normal);
                    if (incoming < 0) {
                        motion = motion.subtract(normal.scale(1.5 * incoming)).scale(0.55);
                        clearFootballShot(); hitCooldown = 8; pushCooldown = 6;
                        break;
                    }
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
        if (!projectile && kind != BallKind.FOOTBALL && pushCooldown == 0) {
            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.06),
                    e -> e.isAlive() && !e.isSpectator() && e.getDeltaMovement().horizontalDistanceSqr() > 0.00001)) {
                push(entity);
                if (pushCooldown > 0) break;
            }
        }
    }
    @Override public void push(Entity entity) {
        if (kind() == BallKind.FOOTBALL || level().isClientSide() || entityData.get(PROJECTILE) || pushCooldown > 0) return;
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
        if (kind() == BallKind.FOOTBALL || entityData.get(PROJECTILE)) return false;
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
            cancelCharge();
            discard();
        }
        return InteractionResult.SUCCESS;
    }
    @Override protected void addAdditionalSaveData(ValueOutput output) {
        output.putDouble("TravelledDistance", travelledDistance);
        output.putFloat("Roll", entityData.get(ROLL)); output.putFloat("Heading", entityData.get(HEADING));
        output.putBoolean("Projectile", entityData.get(PROJECTILE)); output.putInt("FlightTicks", flightTicks);
        if (kind() == BallKind.FOOTBALL) {
            output.putBoolean("FootballShot", entityData.get(FOOTBALL_SHOT));
            output.putDouble("SideSpin", spin.side()); output.putDouble("TopSpin", spin.top());
            output.putDouble("ShotCharge", shotCharge);
        }
        if (owner != null) output.putString("Owner", owner.toString());
    }
    @Override protected void readAdditionalSaveData(ValueInput input) {
        double savedDistance = input.getDoubleOr("TravelledDistance", 0);
        travelledDistance = Double.isFinite(savedDistance) ? Math.clamp(savedDistance, 0.0, 10000.0) : 0;
        float savedRoll = input.getFloatOr("Roll", 0), savedHeading = input.getFloatOr("Heading", 0);
        entityData.set(ROLL, Float.isFinite(savedRoll) ? savedRoll : 0); previousRoll = entityData.get(ROLL);
        entityData.set(HEADING, Float.isFinite(savedHeading) ? savedHeading : 0);
        entityData.set(PROJECTILE, input.getBooleanOr("Projectile", false)); flightTicks = Math.clamp(input.getIntOr("FlightTicks", 0), 0, 400);
        entityData.set(FOOTBALL_SHOT, kind() == BallKind.FOOTBALL && !entityData.get(PROJECTILE) && input.getBooleanOr("FootballShot", false));
        double side = input.getDoubleOr("SideSpin", 0), top = input.getDoubleOr("TopSpin", 0);
        spin = new FootballRules.Spin(Double.isFinite(side) ? Math.clamp(side, -1, 1) : 0, Double.isFinite(top) ? Math.clamp(top, -1, 1) : 0);
        double savedCharge = input.getDoubleOr("ShotCharge", 1);
        shotCharge = Double.isFinite(savedCharge) ? Math.clamp(savedCharge, 0, 1) : 0;
        // Possession and the input window never survive unloading/rejoining.
        dribbler = null; chargingPlayer = null; shotTime = Long.MIN_VALUE; lastFollowSample = FootballRules.FOLLOW_TICKS;
        try { owner = UUID.fromString(input.getStringOr("Owner", "")); } catch (IllegalArgumentException invalid) { owner = null; }
    }
}
