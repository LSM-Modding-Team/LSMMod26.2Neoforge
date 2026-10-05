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

    // Invisible seat entity: tiny hitbox, never saved, not summonable with /summon
    public static final Supplier<EntityType<SeatEntity>> SEAT = ENTITIES.registerEntityType(
            "seat",
            SeatEntity::new,
            MobCategory.MISC,
            builder -> builder
                    .sized(0.001F, 0.001F)
                    // Passengers attach at the entity's feet (y = 0), so the entity's Y is the seat height
                    .passengerAttachments(0.0F)
                    .noSummon());

    private ModEntities() {
    }

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
    }
}
