package net.nicomar2009.lsmmod.modules.facilities.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.nicomar2009.lsmmod.modules.facilities.entity.CotRestEntity;
import java.util.function.Supplier;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ENTITIES;

public final class ModuleEntities {
    public static final Supplier<EntityType<CotRestEntity>> COT_REST = ENTITIES.registerEntityType(
            "cot_rest", CotRestEntity::new, MobCategory.MISC,
            builder -> builder.sized(0.001F, 0.001F).passengerAttachments(0.0F).noSummon());
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleEntities() {}
}
