package net.nicomar2009.lsmmod.modules.musical_instruments.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.modules.musical_instruments.item.InstrumentItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<Item> GUITAR =
            ITEMS.registerItem("guitar",
                    props -> new InstrumentItem(props.stacksTo(1), ModuleSounds.GUITAR, 10.0F, 8, 320));

    public static final DeferredItem<Item> MANDOLIN =
            ITEMS.registerItem("mandolin",
                    props -> new InstrumentItem(props.stacksTo(1), ModuleSounds.MANDOLIN, 10.0F, 8, 320));

    public static final DeferredItem<Item> FLUTE =
            ITEMS.registerItem("flute",
                    props -> new InstrumentItem(props.stacksTo(1), ModuleSounds.FLUTE, 2.0F, 16, 160));

    public static final DeferredItem<Item> TAMBOURINE =
            ITEMS.registerItem("tambourine",
                    props -> new InstrumentItem(props.stacksTo(1), ModuleSounds.TAMBOURINE, 2.0F, 16, 160));

    public static final DeferredItem<Item> VIOLIN =
            ITEMS.registerItem("violin",
                    props -> new InstrumentItem(props.stacksTo(1), ModuleSounds.VIOLIN, 10.0F, 8, 320));

    public static final DeferredItem<Item> MELODICA =
            ITEMS.registerItem("melodica",
                    props -> new InstrumentItem(props.stacksTo(1), ModuleSounds.MELODICA, 2.0F, 16, 160));

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
