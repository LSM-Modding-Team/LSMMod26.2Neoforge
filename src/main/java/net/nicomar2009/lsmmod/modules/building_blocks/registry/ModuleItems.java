package net.nicomar2009.lsmmod.modules.building_blocks.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_LABORATORY_TILE = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_LABORATORY_TILE);

    public static final DeferredItem<BlockItem> LABORATORY_TABLE_TILE = ITEMS.registerSimpleBlockItem(ModuleBlocks.LABORATORY_TABLE_TILE);

    public static final DeferredItem<BlockItem> AUDITORIUM_PARQUET = ITEMS.registerSimpleBlockItem(ModuleBlocks.AUDITORIUM_PARQUET);

    public static final DeferredItem<BlockItem> AUDITORIUM_STAGE = ITEMS.registerSimpleBlockItem(ModuleBlocks.AUDITORIUM_STAGE);

    public static final DeferredItem<BlockItem> DARK_TILED_FLOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.DARK_TILED_FLOOR);

    public static final DeferredItem<BlockItem> SCHOOL_CIRCULAR_COLUMN = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_CIRCULAR_COLUMN);

    public static final DeferredItem<BlockItem> SCHOOL_FOUNDATION = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_FOUNDATION);

    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_BLOCK = ITEMS.registerSimpleBlockItem(ModuleBlocks.YELLOW_SCHOOL_BLOCK);

    public static final DeferredItem<BlockItem> TILED_PARQUET_FLOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.TILED_PARQUET_FLOOR);

    public static final DeferredItem<BlockItem> WHITE_PLAYGROUND_FLOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.WHITE_PLAYGROUND_FLOOR);

    public static final DeferredItem<BlockItem> GRAY_PLAYGROUND_FLOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.GRAY_PLAYGROUND_FLOOR);

    public static final DeferredItem<BlockItem> GREEN_PLAYGROUND_FLOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.GREEN_PLAYGROUND_FLOOR);

    public static final DeferredItem<BlockItem> ORANGE_PLAYGROUND_FLOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.ORANGE_PLAYGROUND_FLOOR);

    public static final DeferredItem<BlockItem> CLASSROOM_WHITE_WALL = ITEMS.registerSimpleBlockItem(ModuleBlocks.CLASSROOM_WHITE_WALL);

    public static final DeferredItem<BlockItem> CLASSROOM_PAINTED_BLOCK = ITEMS.registerSimpleBlockItem(ModuleBlocks.CLASSROOM_PAINTED_BLOCK);

    public static final DeferredItem<BlockItem> SCHOOL_SYNTHETIC_GRASS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_SYNTHETIC_GRASS);

    public static final DeferredItem<BlockItem> RED_STREET_WALL = ITEMS.registerSimpleBlockItem(ModuleBlocks.RED_STREET_WALL);

    public static final DeferredItem<BlockItem> SCHOOL_WOODEN_HANDRAIL = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_WOODEN_HANDRAIL);

    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLAB = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLAB);

    public static final DeferredItem<BlockItem> LIGHT_SCHOOL_WALL_RAILING = ITEMS.registerSimpleBlockItem(ModuleBlocks.LIGHT_SCHOOL_WALL_RAILING);

    public static final DeferredItem<BlockItem> SCHOOL_PARQUET = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PARQUET);

    public static final DeferredItem<BlockItem> LIGHT_SCHOOL_TILED_FLOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.LIGHT_SCHOOL_TILED_FLOOR);

    public static final DeferredItem<BlockItem> DARK_SCHOOL_TILED_FLOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.DARK_SCHOOL_TILED_FLOOR);

    public static final DeferredItem<BlockItem> CLASSROOM_FLOOR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.CLASSROOM_FLOOR);

    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT);

    public static final DeferredItem<BlockItem> STREET_SCHOOL_WALL = ITEMS.registerSimpleBlockItem(ModuleBlocks.STREET_SCHOOL_WALL);

    public static final DeferredItem<BlockItem> STREET_SCHOOL_BARS = ITEMS.registerSimpleBlockItem(ModuleBlocks.STREET_SCHOOL_BARS);

    public static final DeferredItem<BlockItem> LIGHT_SCHOOL_WALL =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.LIGHT_SCHOOL_WALL);

    public static final DeferredItem<BlockItem> DARK_SCHOOL_WALL =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.DARK_SCHOOL_WALL);

    public static final DeferredItem<BlockItem> SCHOOL_GATE =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_GATE);
    public static void addAliases() {
        ITEMS.addAlias(net.minecraft.resources.Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "bathroom_floor"), DARK_SCHOOL_TILED_FLOOR.getId());
        ITEMS.addAlias(net.minecraft.resources.Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "light_bathroom_floor"), LIGHT_SCHOOL_TILED_FLOOR.getId());
    }

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
