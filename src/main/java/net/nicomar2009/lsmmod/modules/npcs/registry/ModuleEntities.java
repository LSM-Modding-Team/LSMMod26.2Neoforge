package net.nicomar2009.lsmmod.modules.npcs.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import java.util.function.Supplier;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ENTITIES;

@net.neoforged.fml.common.EventBusSubscriber(modid = net.nicomar2009.lsmmod.LSMMod.MOD_ID)
public final class ModuleEntities {
    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.npcs.entity.StudentEntity>> STUDENT = ENTITIES.registerEntityType(
            "student", net.nicomar2009.lsmmod.modules.npcs.entity.StudentEntity::new, MobCategory.CREATURE,
            builder -> builder.notInPeaceful().sized(0.6F, 1.8F).clientTrackingRange(8));

    public static final Supplier<EntityType<net.nicomar2009.lsmmod.modules.npcs.entity.TeacherEntity>> TEACHER = ENTITIES.registerEntityType(
            "teacher", net.nicomar2009.lsmmod.modules.npcs.entity.TeacherEntity::new, MobCategory.CREATURE,
            builder -> builder.notInPeaceful().sized(0.6F, 1.8F).clientTrackingRange(8));

    @net.neoforged.bus.api.SubscribeEvent
    public static void registerAttributes(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(STUDENT.get(), net.nicomar2009.lsmmod.modules.npcs.entity.StudentEntity.createAttributes().build());
        event.put(TEACHER.get(), net.nicomar2009.lsmmod.modules.npcs.entity.TeacherEntity.createAttributes().build());
    }

    private ModuleEntities() {}
}
