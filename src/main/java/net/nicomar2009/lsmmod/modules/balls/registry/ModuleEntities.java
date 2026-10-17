package net.nicomar2009.lsmmod.modules.balls.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import java.util.function.Supplier;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ENTITIES;

public final class ModuleEntities {
    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.balls.ball.BallEntity>> PLASTIC_BALL = ball("plastic_ball");

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.balls.ball.BallEntity>> FOOTBALL_BALL = ball("football_ball");

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.balls.ball.BallEntity>> BASKETBALL_BALL = ball("basketball_ball");

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.balls.ball.BallEntity>> VOLLEYBALL_BALL = ball("volleyball_ball");

    private static Supplier<EntityType<net.nicomar2009.lsmmod.modules.balls.ball.BallEntity>> ball(String name) {
        return ENTITIES.registerEntityType(name, net.nicomar2009.lsmmod.modules.balls.ball.BallEntity::new,
                MobCategory.MISC, builder -> builder.sized(0.5F, 0.5F).clientTrackingRange(name.equals("football_ball") ? 16 : 8).updateInterval(1));
    }

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleEntities() {}
}
