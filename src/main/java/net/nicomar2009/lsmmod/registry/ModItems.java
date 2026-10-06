package net.nicomar2009.lsmmod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;

/** Item registry for the school blocks; standalone items are planned. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LSMMod.MOD_ID);

    // Block item for the elementary chair (registry name taken from the block: "lsmmod:elementary_chair")
    public static final DeferredItem<BlockItem> ELEMENTARY_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.ELEMENTARY_CHAIR);

    // Block item for the high school chair (registry name taken from the block: "lsmmod:high_school_chair")
    public static final DeferredItem<BlockItem> HIGH_SCHOOL_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.HIGH_SCHOOL_CHAIR);

    // Block item for the desk (placing it creates both halves)
    public static final DeferredItem<BlockItem> STUDENTS_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.STUDENTS_DESK);

    // Block item for the locker
    public static final DeferredItem<BlockItem> LOCKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.LOCKER);

    // Block item for the teachers chair (registry name taken from the block: "lsmmod:teachers_chair")
    public static final DeferredItem<BlockItem> TEACHERS_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEACHERS_CHAIR);

    // Block item for the desk (placing it creates both halves)
    public static final DeferredItem<BlockItem> TEACHERS_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEACHERS_DESK);

    // Block item for the elementary desk (a single block with 9 slots)
    public static final DeferredItem<BlockItem> ELEMENTARY_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.ELEMENTARY_DESK);

    // Block item for the statue of San Martín De Porres (a double block)
    public static final DeferredItem<BlockItem> SAN_MARTIN_DE_PORRES =
            ITEMS.registerSimpleBlockItem(ModBlocks.SAN_MARTIN_DE_PORRES);

    public static final DeferredItem<BlockItem> SCHOOL_SHIELD =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SHIELD);

    public static final DeferredItem<BlockItem> ENGLISHROOMCHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.ENGLISHROOMCHAIR);

    public static final DeferredItem<BlockItem> HALLCHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.HALLCHAIR);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_WHITE);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_RED);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE_ARMS =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_WHITE_ARMS);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED_ARMS =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_RED_ARMS);

    public static final DeferredItem<BlockItem> LAPTOP =
            ITEMS.registerSimpleBlockItem(ModBlocks.LAPTOP);

    public static final DeferredItem<BlockItem> MOISES_LAPTOP =
            ITEMS.registerSimpleBlockItem(ModBlocks.MOISES_LAPTOP);

    public static final DeferredItem<BlockItem> PC =
            ITEMS.registerSimpleBlockItem(ModBlocks.PC);

    public static final DeferredItem<BlockItem> COMPUTER_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.COMPUTER_DESK);

    public static final DeferredItem<BlockItem> GRAY_COMPUTER_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.GRAY_COMPUTER_DESK);

    public static final DeferredItem<BlockItem> SCHOOL_BELL =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_BELL);

    public static final ResourceKey<JukeboxSong> LSM_ANTHEM_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "lsm_anthem"));

    public static final DeferredItem<Item> LSM_ANTHEM = ITEMS.registerItem(
            "lsm_anthem",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(LSM_ANTHEM_SONG));

    public static final DeferredItem<BlockItem> DINING_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.DINING_TABLE);

    public static final DeferredItem<BlockItem> DINING_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.DINING_CHAIR);

    public static final DeferredItem<BlockItem> SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPEAKER);

    public static final DeferredItem<BlockItem> TALL_SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.TALL_SPEAKER);

    public static final DeferredItem<BlockItem> DRINKING_FOUNTAIN =
            ITEMS.registerSimpleBlockItem(ModBlocks.DRINKING_FOUNTAIN);

    public static final DeferredItem<BlockItem> KIOSK_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.KIOSK_TABLE);

    public static final DeferredItem<BlockItem> ART_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.ART_TABLE);

    public static final DeferredItem<BlockItem> STOOL =
            ITEMS.registerSimpleBlockItem(ModBlocks.STOOL);

    public static final DeferredItem<BlockItem> VAULTING_BOX =
            ITEMS.registerSimpleBlockItem(ModBlocks.VAULTING_BOX);

    public static final DeferredItem<BlockItem> CLASSROOM_DOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_DOOR);

    public static final DeferredItem<BlockItem> MANUEL_TIRADO_BUST =
            ITEMS.registerSimpleBlockItem(ModBlocks.MANUEL_TIRADO_BUST);

    public static final DeferredItem<BlockItem> INFIRMARY_COT =
            ITEMS.registerSimpleBlockItem(ModBlocks.INFIRMARY_COT);

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
