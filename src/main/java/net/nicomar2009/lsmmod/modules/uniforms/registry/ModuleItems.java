package net.nicomar2009.lsmmod.modules.uniforms.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.modules.uniforms.item.SchoolUniformItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<SchoolUniformItem> FAKE_SCHOOL_HAIRCUT =
            ITEMS.registerItem("fake_school_haircut", props -> new SchoolUniformItem(props, ArmorType.HELMET, "fake_school_haircut"));

    public static final DeferredItem<SchoolUniformItem> UNIFORM_POLO =
            ITEMS.registerItem("uniform_polo", props -> new SchoolUniformItem(props, ArmorType.CHESTPLATE, "uniform_polo"));

    public static final DeferredItem<SchoolUniformItem> UNIFORM_PANTS =
            ITEMS.registerItem("uniform_pants", props -> new SchoolUniformItem(props, ArmorType.LEGGINGS, "uniform_pants"));

    public static final DeferredItem<SchoolUniformItem> UNIFORM_SHOES =
            ITEMS.registerItem("uniform_shoes", props -> new SchoolUniformItem(props, ArmorType.BOOTS, "uniform_shoes"));

    private ModuleItems() {}
}
