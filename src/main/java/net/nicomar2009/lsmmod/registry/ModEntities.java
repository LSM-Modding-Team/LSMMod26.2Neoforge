package net.nicomar2009.lsmmod.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.entity.SeatEntity;

import java.util.function.Supplier;

public final class ModEntities {
    // Specialized register: it builds the EntityType with its resource key (required in 26.x)
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(LSMMod.MOD_ID);

    // Invisible seat entity: tiny hitbox, not summonable with /summon (not marked noSave(); it discards itself when empty)
    public static final Supplier<EntityType<SeatEntity>> SEAT = ENTITIES.registerEntityType(
            "seat",
            SeatEntity::new,
            MobCategory.MISC,
            builder -> builder
                    .sized(0.001F, 0.001F)
                    // Passengers attach at the entity's feet (y = 0), so the entity's Y is the seat height
                    .passengerAttachments(0.0F)
                    .noSummon());

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.entity.StudentEntity>> STUDENT = ENTITIES.registerEntityType(
            "student", net.nicomar2009.lsmmod.entity.StudentEntity::new, MobCategory.CREATURE,
            builder -> builder.notInPeaceful().sized(0.6F, 1.8F).clientTrackingRange(8));

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.entity.TeacherEntity>> TEACHER = ENTITIES.registerEntityType(
            "teacher", net.nicomar2009.lsmmod.entity.TeacherEntity::new, MobCategory.CREATURE,
            builder -> builder.notInPeaceful().sized(0.6F, 1.8F).clientTrackingRange(8));

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.ball.BallEntity>> PLASTIC_BALL = ball("plastic_ball");
    public static final Supplier<EntityType<net.nicomar2009.lsmmod.ball.BallEntity>> FOOTBALL_BALL = ball("football_ball");
    public static final Supplier<EntityType<net.nicomar2009.lsmmod.ball.BallEntity>> BASKETBALL_BALL = ball("basketball_ball");
    public static final Supplier<EntityType<net.nicomar2009.lsmmod.ball.BallEntity>> VOLLEYBALL_BALL = ball("volleyball_ball");
    private static Supplier<EntityType<net.nicomar2009.lsmmod.ball.BallEntity>> ball(String name) {
        return ENTITIES.registerEntityType(name, net.nicomar2009.lsmmod.ball.BallEntity::new,
                MobCategory.MISC, builder -> builder.sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(1));
    }

    private ModEntities() {
    }

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
    }
}
