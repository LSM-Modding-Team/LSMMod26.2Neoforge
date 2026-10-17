package net.nicomar2009.lsmmod.modules.chairs.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.nicomar2009.lsmmod.modules.chairs.entity.SeatEntity;
import java.util.function.Supplier;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ENTITIES;

public final class ModuleEntities {
    public static final Supplier<EntityType<SeatEntity>> SEAT = ENTITIES.registerEntityType(
            "seat",
            SeatEntity::new,
            MobCategory.MISC,
            builder -> builder
                    .sized(0.001F, 0.001F)
                    // Passengers attach at the entity's feet (y = 0), so the entity's Y is the seat height
                    .passengerAttachments(0.0F)
                    .noSummon());
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleEntities() {}
}
