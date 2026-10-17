package net.nicomar2009.lsmmod.modules.npcs.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<SpawnEggItem> STUDENT_SPAWN_EGG =
            ITEMS.registerItem("student_spawn_egg", props -> new SpawnEggItem(props.spawnEgg(ModuleEntities.STUDENT.get())));

    public static final DeferredItem<SpawnEggItem> TEACHER_SPAWN_EGG =
            ITEMS.registerItem("teacher_spawn_egg", props -> new SpawnEggItem(props.spawnEgg(ModuleEntities.TEACHER.get())));

    public static final DeferredItem<SpawnEggItem> STUDENT_1P_SPAWN_EGG =
            registerClassroomEgg("student_1p_spawn_egg", "primary", 1);

    public static final DeferredItem<SpawnEggItem> STUDENT_2P_SPAWN_EGG =
            registerClassroomEgg("student_2p_spawn_egg", "primary", 2);

    public static final DeferredItem<SpawnEggItem> STUDENT_3P_SPAWN_EGG =
            registerClassroomEgg("student_3p_spawn_egg", "primary", 3);

    public static final DeferredItem<SpawnEggItem> STUDENT_4P_SPAWN_EGG =
            registerClassroomEgg("student_4p_spawn_egg", "primary", 4);

    public static final DeferredItem<SpawnEggItem> STUDENT_5P_SPAWN_EGG =
            registerClassroomEgg("student_5p_spawn_egg", "primary", 5);

    public static final DeferredItem<SpawnEggItem> STUDENT_6P_SPAWN_EGG =
            registerClassroomEgg("student_6p_spawn_egg", "primary", 6);

    public static final DeferredItem<SpawnEggItem> STUDENT_1S_SPAWN_EGG =
            registerClassroomEgg("student_1s_spawn_egg", "secondary", 1);

    public static final DeferredItem<SpawnEggItem> STUDENT_2S_SPAWN_EGG =
            registerClassroomEgg("student_2s_spawn_egg", "secondary", 2);

    public static final DeferredItem<SpawnEggItem> STUDENT_3S_SPAWN_EGG =
            registerClassroomEgg("student_3s_spawn_egg", "secondary", 3);

    public static final DeferredItem<SpawnEggItem> STUDENT_4S_SPAWN_EGG =
            registerClassroomEgg("student_4s_spawn_egg", "secondary", 4);

    public static final DeferredItem<SpawnEggItem> STUDENT_5S_SPAWN_EGG =
            registerClassroomEgg("student_5s_spawn_egg", "secondary", 5);

    public static final DeferredItem<Item> NPC_SUMMON_HELPER =
            ITEMS.registerItem("npc_summon_helper", props -> new Item(props.stacksTo(1)));

    private static DeferredItem<SpawnEggItem> registerClassroomEgg(String id, String level, int grade) {
        return ITEMS.registerItem(id, props -> {
            CompoundTag classroom = new CompoundTag();
            classroom.putString("Level", level);
            classroom.putInt("Grade", grade);
            CompoundTag entityData = new CompoundTag();
            entityData.put("StudentData", classroom);
            return new SpawnEggItem(props.spawnEgg(ModuleEntities.STUDENT.get())
                    .component(DataComponents.ENTITY_DATA, TypedEntityData.of(ModuleEntities.STUDENT.get(), entityData)));
        });
    }

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
