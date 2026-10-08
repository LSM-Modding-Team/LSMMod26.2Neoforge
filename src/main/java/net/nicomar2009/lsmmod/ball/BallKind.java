package net.nicomar2009.lsmmod.ball;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.nicomar2009.lsmmod.registry.ModEntities;
import net.nicomar2009.lsmmod.registry.ModItems;

public enum BallKind {
    PLASTIC(18, 16, 5, 0.035, 0.982, 0.945, 0.32, 0.10, 0.55),
    FOOTBALL(30, 24, 8, 0.050, 0.995, 0.975, 0.65, 0.08, 0.72),
    BASKETBALL(40, 14, 4, 0.065, 0.992, 0.940, 0.88, 0.52, 0.80),
    VOLLEYBALL(26, 28, 10, 0.038, 0.987, 0.960, 0.78, 0.62, 0.76);

    public final float hardDamage;
    public final double projectileSpeed, toySpeed, gravity, airDrag, groundDrag, bounce, kickHeight, wallBounce;
    BallKind(float damage, double projectile, double toy, double gravity, double airDrag,
             double groundDrag, double bounce, double kickHeight, double wallBounce) {
        hardDamage = damage; projectileSpeed = projectile; toySpeed = toy;
        this.gravity = gravity; this.airDrag = airDrag; this.groundDrag = groundDrag;
        this.bounce = bounce; this.kickHeight = kickHeight; this.wallBounce = wallBounce;
    }
    public float damage(Difficulty difficulty, double distance) {
        // A strong close-range hit grows smoothly to twice its damage at 32 travelled blocks.
        double distanceMultiplier = 1 + Math.clamp(distance / 32.0, 0.0, 1.0);
        return (float) (hardDamage * distanceMultiplier * switch (difficulty) {
            case PEACEFUL -> 0; case EASY -> 0.5; case NORMAL -> 0.75; case HARD -> 1;
        });
    }
    public EntityType<BallEntity> entityType() {
        return switch (this) { case PLASTIC -> ModEntities.PLASTIC_BALL.get(); case FOOTBALL -> ModEntities.FOOTBALL_BALL.get();
            case BASKETBALL -> ModEntities.BASKETBALL_BALL.get(); case VOLLEYBALL -> ModEntities.VOLLEYBALL_BALL.get(); };
    }
    public Item item() {
        return switch (this) { case PLASTIC -> ModItems.PLASTIC_BALL.get(); case FOOTBALL -> ModItems.FOOTBALL_BALL.get();
            case BASKETBALL -> ModItems.BASKETBALL_BALL.get(); case VOLLEYBALL -> ModItems.VOLLEYBALL_BALL.get(); };
    }
}
