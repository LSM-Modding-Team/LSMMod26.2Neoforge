package net.nicomar2009.lsmmod.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.entity.SeatEntity;
import net.nicomar2009.lsmmod.entity.CotRestEntity;

import java.util.function.Supplier;

public final class ModEntities {
    // Specialized register: it builds the EntityType with its resource key (required in 26.x)
    public static final DeferredRegister.Entities ENTITIES = ModRegistries.ENTITIES;

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

    public static final Supplier<EntityType<CotRestEntity>> COT_REST = ENTITIES.registerEntityType(
            "cot_rest", CotRestEntity::new, MobCategory.MISC,
            builder -> builder.sized(0.001F, 0.001F).passengerAttachments(0.0F).noSummon());

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.npcs.entity.StudentEntity>> STUDENT = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleEntities.STUDENT;

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.npcs.entity.TeacherEntity>> TEACHER = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleEntities.TEACHER;

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.balls.ball.BallEntity>> PLASTIC_BALL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleEntities.PLASTIC_BALL;
    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.balls.ball.BallEntity>> FOOTBALL_BALL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleEntities.FOOTBALL_BALL;
    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.balls.ball.BallEntity>> BASKETBALL_BALL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleEntities.BASKETBALL_BALL;
    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.balls.ball.BallEntity>> VOLLEYBALL_BALL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleEntities.VOLLEYBALL_BALL;


    private ModEntities() {
    }

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
    }
}
