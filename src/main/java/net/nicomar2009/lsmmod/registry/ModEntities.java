package net.nicomar2009.lsmmod.registry;

import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.modules.chairs.entity.SeatEntity;
import net.nicomar2009.lsmmod.modules.facilities.entity.CotRestEntity;

import java.util.function.Supplier;

public final class ModEntities {
    // Specialized register: it builds the EntityType with its resource key (required in 26.x)
    public static final DeferredRegister.Entities ENTITIES = ModRegistries.ENTITIES;

    // Invisible seat entity: tiny hitbox, not summonable with /summon (not marked noSave(); it discards itself when empty)
    public static final Supplier<EntityType<SeatEntity>> SEAT = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleEntities.SEAT;

    public static final Supplier<EntityType<CotRestEntity>> COT_REST = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleEntities.COT_REST;

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
