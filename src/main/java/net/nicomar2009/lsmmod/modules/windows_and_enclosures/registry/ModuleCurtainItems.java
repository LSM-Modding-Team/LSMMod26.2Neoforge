package net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry;

import net.neoforged.neoforge.registries.DeferredItem;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

/** Separate initialization preserves the existing curtain item registration order. */
public final class ModuleCurtainItems {
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.windows_and_enclosures.item.SchoolCurtainItem> SCHOOL_CURTAIN =
            ITEMS.registerItem("school_curtain",net.nicomar2009.lsmmod.modules.windows_and_enclosures.item.SchoolCurtainItem::new);

    public static void initialize() {}
    private ModuleCurtainItems() {}
}
