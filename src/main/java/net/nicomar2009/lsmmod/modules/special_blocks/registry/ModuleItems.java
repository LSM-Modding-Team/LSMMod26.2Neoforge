package net.nicomar2009.lsmmod.modules.special_blocks.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_TROPHY_GALLERY = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_TROPHY_GALLERY);

    public static final DeferredItem<BlockItem> SAN_MARTIN_DE_PORRES =
            ITEMS.registerItem("san_martin_de_porres",
                    props -> new BlockItem(ModuleBlocks.SAN_MARTIN_DE_PORRES.get(), props),
                    props -> props.rarity(Rarity.EPIC));

    public static final DeferredItem<BlockItem> VAULTING_BOX =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.VAULTING_BOX);

    public static final DeferredItem<BlockItem> MANUEL_TIRADO_BUST =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.MANUEL_TIRADO_BUST);
    public static final DeferredItem<BlockItem> SCHOOL_SHIELD =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_SHIELD);

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
