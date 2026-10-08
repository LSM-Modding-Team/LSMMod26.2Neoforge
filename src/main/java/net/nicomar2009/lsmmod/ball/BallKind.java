package net.nicomar2009.lsmmod.ball;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.nicomar2009.lsmmod.registry.ModEntities;
import net.nicomar2009.lsmmod.registry.ModItems;

public enum BallKind {
    PLASTIC(10, 1, 1), FOOTBALL(20, 2, 0.8), BASKETBALL(28, 0.8, 0.5), VOLLEYBALL(16, 3, 6);
    public final float hardDamage;
    public final double projectileSpeed, toySpeed;
    BallKind(float damage, double projectile, double toy) {
        hardDamage = damage; projectileSpeed = projectile; toySpeed = toy;
    }
    public float damage(Difficulty difficulty) {
        return hardDamage * switch (difficulty) { case PEACEFUL -> 0; case EASY -> 0.5F; case NORMAL -> 0.75F; case HARD -> 1; };
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
