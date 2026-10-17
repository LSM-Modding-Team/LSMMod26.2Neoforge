package net.nicomar2009.lsmmod.modules.special_blocks.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_TROPHY_GALLERY = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_TROPHY_GALLERY);

    public static final DeferredItem<BlockItem> SAN_MARTIN_DE_PORRES =
            ITEMS.registerItem("san_martin_de_porres",
                    props -> new BlockItem(ModBlocks.SAN_MARTIN_DE_PORRES.get(), props),
                    props -> props.rarity(Rarity.EPIC));

    public static final DeferredItem<BlockItem> VAULTING_BOX =
            ITEMS.registerSimpleBlockItem(ModBlocks.VAULTING_BOX);

    public static final DeferredItem<BlockItem> MANUEL_TIRADO_BUST =
            ITEMS.registerSimpleBlockItem(ModBlocks.MANUEL_TIRADO_BUST);
    public static final DeferredItem<BlockItem> SCHOOL_SHIELD =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SHIELD);

    private ModuleItems() {}
}
