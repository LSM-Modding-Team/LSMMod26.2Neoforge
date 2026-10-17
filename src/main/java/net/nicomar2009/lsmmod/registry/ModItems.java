package net.nicomar2009.lsmmod.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.item.AwningItem;
import net.nicomar2009.lsmmod.modules.school_supplies.item.RulerItem;
import net.nicomar2009.lsmmod.modules.uniforms.item.SchoolUniformItem;
import net.nicomar2009.lsmmod.item.PortableSpeakerItem;

/** Item registry for the school blocks; standalone items are planned. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = ModRegistries.ITEMS;

    // BEGIN DINING COUNTER
    public static final DeferredItem<BlockItem> DINING_ROOM_COUNTER = ITEMS.registerSimpleBlockItem(ModBlocks.DINING_ROOM_COUNTER);
    // END DINING COUNTER

    // BEGIN CONNECTED TINTED GLASS
    public static final DeferredItem<BlockItem> SCHOOL_CONNECTED_TINTED_GLASS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CONNECTED_TINTED_GLASS);
    // END CONNECTED TINTED GLASS

    public static final DeferredItem<BlockItem> SCHOOL_REFRIGERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_REFRIGERATOR);

    public static final DeferredItem<BlockItem> DINING_WALL_CABINET = ITEMS.registerSimpleBlockItem(ModBlocks.DINING_WALL_CABINET);

    public static final DeferredItem<BlockItem> SCHOOL_SHELVING = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SHELVING);

    public static final DeferredItem<BlockItem> NIDO_STORAGE_BUCKET = ITEMS.registerSimpleBlockItem(ModBlocks.NIDO_STORAGE_BUCKET);

    public static final DeferredItem<BlockItem> SCHOOL_PROJECTING_WINDOW = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PROJECTING_WINDOW);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW);

    public static final DeferredItem<BlockItem> SCHOOL_NAME_SIGN = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_NAME_SIGN);

    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE_PAINTABLE = ITEMS.registerSimpleBlockItem(ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE);

    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_DOOR_PAINTABLE = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_DOOR_PAINTABLE);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_GLASS_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_GLASS_STAIRS);

    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE_PAINTABLE_COLUMN = ITEMS.registerSimpleBlockItem(ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE_COLUMN);

    public static final DeferredItem<BlockItem> DOUBLE_SCHOOL_ENTRANCE_PAINTABLE_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.DOUBLE_SCHOOL_ENTRANCE_PAINTABLE_SLAB);

    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE_2X4 = ITEMS.registerSimpleBlockItem(ModBlocks.BATHROOM_DOOR_WIDE_2X4);

    public static final DeferredItem<BlockItem> SCHOOL_LABORATORY_TILE = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_LABORATORY_TILE);

    public static final DeferredItem<BlockItem> LABORATORY_TABLE_TILE = ITEMS.registerSimpleBlockItem(ModBlocks.LABORATORY_TABLE_TILE);

    public static final DeferredItem<BlockItem> AUDITORIUM_PARQUET = ITEMS.registerSimpleBlockItem(ModBlocks.AUDITORIUM_PARQUET);

    public static final DeferredItem<BlockItem> AUDITORIUM_STAGE = ITEMS.registerSimpleBlockItem(ModBlocks.AUDITORIUM_STAGE);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS);

    // BEGIN FACILITY EXPANSIONS
    public static final DeferredItem<BlockItem> DINING_DOOR_WIDE = ITEMS.registerSimpleBlockItem(ModBlocks.DINING_DOOR_WIDE);
    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE = ITEMS.registerSimpleBlockItem(ModBlocks.BATHROOM_DOOR_WIDE);
    public static final DeferredItem<BlockItem> SCHOOL_MICROWAVE = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_MICROWAVE);
    public static final DeferredItem<BlockItem> SCHOOL_TROPHY_GALLERY = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_TROPHY_GALLERY);
    public static final DeferredItem<BlockItem> INFIRMARY_DOOR = ITEMS.registerSimpleBlockItem(ModBlocks.INFIRMARY_DOOR);
    public static final DeferredItem<BlockItem> DINING_DOOR_WIDE_LIGHT = ITEMS.registerSimpleBlockItem(ModBlocks.DINING_DOOR_WIDE_LIGHT);
    // END FACILITY EXPANSIONS

    // BEGIN KIOSK ENTRANCE ACCESSORIES
    public static final DeferredItem<BlockItem> SCHOOL_WOOD_PANEL = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_WOOD_PANEL);
    public static final DeferredItem<BlockItem> SCHOOL_WOOD_DOOR = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_WOOD_DOOR);
    public static final DeferredItem<BlockItem> SCHOOL_SLIDING_ENTRANCE = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SLIDING_ENTRANCE);
    public static final DeferredItem<BlockItem> DRINKING_FOUNTAIN_RAISED = ITEMS.registerSimpleBlockItem(ModBlocks.DRINKING_FOUNTAIN_RAISED);
    public static final DeferredItem<BlockItem> LOST_AND_FOUND_BOX = ITEMS.registerSimpleBlockItem(ModBlocks.LOST_AND_FOUND_BOX);
    // END KIOSK ENTRANCE ACCESSORIES

    // BEGIN SCHOOL CRUCIFIX
    public static final DeferredItem<BlockItem> SCHOOL_CRUCIFIX = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CRUCIFIX);
    // END SCHOOL CRUCIFIX

    // BEGIN DARK TILE DRAIN COLUMN
    public static final DeferredItem<BlockItem> DARK_TILED_FLOOR = ITEMS.registerSimpleBlockItem(ModBlocks.DARK_TILED_FLOOR);
    public static final DeferredItem<BlockItem> SCHOOL_DRAIN = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_DRAIN);
    public static final DeferredItem<BlockItem> SCHOOL_CIRCULAR_COLUMN = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CIRCULAR_COLUMN);
    // END DARK TILE DRAIN COLUMN

    // BEGIN CHILDREN LOCKER AND CEILING PROJECTOR
    public static final DeferredItem<BlockItem> CHILDREN_LOCKER = ITEMS.registerSimpleBlockItem(ModBlocks.CHILDREN_LOCKER);
    public static final DeferredItem<BlockItem> CEILING_PROJECTOR = ITEMS.registerSimpleBlockItem(ModBlocks.CEILING_PROJECTOR);
    // END CHILDREN LOCKER AND CEILING PROJECTOR

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_MINI_CUBE_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_MINI_CUBE_BLOCK);

    // BEGIN CUSTOM PAINTABLE BLOCKS
    public static final DeferredItem<BlockItem> SCHOOL_FOUNDATION = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_FOUNDATION);
    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_CUSTOM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_CUSTOM_BLOCK);
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolSandpaperItem> SCHOOL_SANDPAPER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.SCHOOL_SANDPAPER;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolHammerItem> SCHOOL_HAMMER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.SCHOOL_HAMMER;
    // END CUSTOM PAINTABLE BLOCKS

    // BEGIN PAINTABLE SCHOOL BLOCK
    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_BLOCK);
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> LIGHT_SCHOOL = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.LIGHT_SCHOOL;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> DARK_SCHOOL = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.DARK_SCHOOL;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> STREET_SCHOOL = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.STREET_SCHOOL;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> CLASSROOM_WHITE = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.CLASSROOM_WHITE;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> CLASSROOM_FLOOR_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.CLASSROOM_FLOOR_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> YELLOW_SCHOOL_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.YELLOW_SCHOOL_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> WHITE_PLAYGROUND_FLOOR_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.WHITE_PLAYGROUND_FLOOR_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> GRAY_PLAYGROUND_FLOOR_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.GRAY_PLAYGROUND_FLOOR_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> GREEN_PLAYGROUND_FLOOR_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.GREEN_PLAYGROUND_FLOOR_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> ORANGE_PLAYGROUND_FLOOR_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.ORANGE_PLAYGROUND_FLOOR_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> DARK_TILED_FLOOR_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.DARK_TILED_FLOOR_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> RED_STREET_WALL_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.RED_STREET_WALL_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> SCHOOL_LABORATORY_TILE_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.SCHOOL_LABORATORY_TILE_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> LABORATORY_TABLE_TILE_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.LABORATORY_TABLE_TILE_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> AUDITORIUM_PARQUET_PAINT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.AUDITORIUM_PARQUET_PAINT;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.PaintCleanerItem> PAINT_CLEANER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.PAINT_CLEANER;
    // END PAINTABLE SCHOOL BLOCK

    // BEGIN YELLOW SCHOOL BLOCK
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.YELLOW_SCHOOL_BLOCK);
    // END YELLOW SCHOOL BLOCK

    // BEGIN TILED PARQUET FLOOR
    public static final DeferredItem<BlockItem> TILED_PARQUET_FLOOR = ITEMS.registerSimpleBlockItem(ModBlocks.TILED_PARQUET_FLOOR);
    // END TILED PARQUET FLOOR

    // BEGIN SCHOOL CABINET AND ROUTER
    public static final DeferredItem<BlockItem> SCHOOL_CABINET = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CABINET);
    public static final DeferredItem<BlockItem> SCHOOL_ROUTER = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ROUTER);
    // END SCHOOL CABINET AND ROUTER

    // BEGIN SCHOOL SINK
    public static final DeferredItem<BlockItem> SCHOOL_SINK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SINK);
    // END SCHOOL SINK

    // BEGIN YELLOW SCHOOL SLOPES
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_SLOPE_1 = ITEMS.registerSimpleBlockItem(ModBlocks.YELLOW_SCHOOL_SLOPE_1);
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_SLOPE_2 = ITEMS.registerSimpleBlockItem(ModBlocks.YELLOW_SCHOOL_SLOPE_2);
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_SLOPE_3 = ITEMS.registerSimpleBlockItem(ModBlocks.YELLOW_SCHOOL_SLOPE_3);
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_SLOPE_4 = ITEMS.registerSimpleBlockItem(ModBlocks.YELLOW_SCHOOL_SLOPE_4);
    // END YELLOW SCHOOL SLOPES

    // BEGIN PLAYGROUND FLOORS
    public static final DeferredItem<BlockItem> WHITE_PLAYGROUND_FLOOR = ITEMS.registerSimpleBlockItem(ModBlocks.WHITE_PLAYGROUND_FLOOR);
    public static final DeferredItem<BlockItem> GRAY_PLAYGROUND_FLOOR = ITEMS.registerSimpleBlockItem(ModBlocks.GRAY_PLAYGROUND_FLOOR);
    public static final DeferredItem<BlockItem> GREEN_PLAYGROUND_FLOOR = ITEMS.registerSimpleBlockItem(ModBlocks.GREEN_PLAYGROUND_FLOOR);
    public static final DeferredItem<BlockItem> ORANGE_PLAYGROUND_FLOOR = ITEMS.registerSimpleBlockItem(ModBlocks.ORANGE_PLAYGROUND_FLOOR);
    // END PLAYGROUND FLOORS

    // BEGIN CONNECTED SCHOOL SINK
    public static final DeferredItem<BlockItem> SCHOOL_CONNECTED_SINK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CONNECTED_SINK);
    // END CONNECTED SCHOOL SINK

    // BEGIN CLASSROOM SURFACES
    public static final DeferredItem<BlockItem> CLASSROOM_WHITE_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_WHITE_WALL);
    public static final DeferredItem<BlockItem> CLASSROOM_PAINTED_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_PAINTED_BLOCK);
    public static final DeferredItem<BlockItem> SCHOOL_SYNTHETIC_GRASS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SYNTHETIC_GRASS);
    public static final DeferredItem<BlockItem> RED_STREET_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.RED_STREET_WALL);
    // END CLASSROOM SURFACES

    // BEGIN SCHOOL RAILING SLOPES
    // BEGIN SHORT RAISED SLOPES
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_1 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SHORT_RAISED_1);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_2 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SHORT_RAISED_2);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_3 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SHORT_RAISED_3);
    // END SHORT RAISED SLOPES

    // BEGIN SCHOOL INVERTED SLOPES
    // BEGIN SCHOOL SPLIT RAISED EXTRAS
    // BEGIN ENTRANCE PAVEMENT SHAPES
    public static final DeferredItem<BlockItem> SCHOOL_CLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CLOCK);
    public static final DeferredItem<BlockItem> SCHOOL_FAN = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_FAN);
    public static final DeferredItem<BlockItem> SCHOOL_WOODEN_HANDRAIL = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_WOODEN_HANDRAIL);

    public static final DeferredItem<BlockItem> CEILING_LIGHT = ITEMS.registerSimpleBlockItem(ModBlocks.CEILING_LIGHT);

    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_1 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_1);
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_2 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_2);
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_3 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_3);
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_4 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_4);
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_5 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_5);
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_6 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_6);
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLAB);
    // END ENTRANCE PAVEMENT SHAPES

    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_1 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_1);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_2 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_2);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_3 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_3);
    // END SCHOOL SPLIT RAISED EXTRAS

    // BEGIN SCHOOL RAISED INVERTED SLOPES
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_1 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_1);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_2 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_2);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_3 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_3);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_4 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_4);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_1 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_1);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_2 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_2);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_3 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_3);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_4 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_4);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_5 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_5);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_6 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_6);
    // END SCHOOL RAISED INVERTED SLOPES

    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_1 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_1);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_2 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_2);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_3 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_3);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_4 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_4);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_1 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_1);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_2 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_2);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_3 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_3);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_4 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_4);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_5 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_5);
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_6 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_6);
    // END SCHOOL INVERTED SLOPES

    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_SPLIT_1 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_SPLIT_1);
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_SPLIT_2 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_SPLIT_2);
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_SPLIT_3 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_SPLIT_3);
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_SPLIT_4 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_SPLIT_4);
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_1 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_1);
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_2 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_2);
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_3 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_3);
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_4 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_4);
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_5 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_5);
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_6 = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_6);
    // END SCHOOL RAILING SLOPES

    public static final DeferredItem<BlockItem> LIGHT_SCHOOL_WALL_RAILING = ITEMS.registerSimpleBlockItem(ModBlocks.LIGHT_SCHOOL_WALL_RAILING);

    public static final DeferredItem<net.nicomar2009.lsmmod.item.SchoolCurtainItem> SCHOOL_CURTAIN =
            ITEMS.registerItem("school_curtain",net.nicomar2009.lsmmod.item.SchoolCurtainItem::new);

    public static final DeferredItem<BlockItem> SCHOOL_GLASS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_GLASS);
    public static final DeferredItem<BlockItem> DOUBLE_SCHOOL_ENTRANCE = ITEMS.registerSimpleBlockItem(ModBlocks.DOUBLE_SCHOOL_ENTRANCE);
    public static final DeferredItem<BlockItem> TALL_CLASSROOM_ENTRANCE = ITEMS.registerSimpleBlockItem(ModBlocks.TALL_CLASSROOM_ENTRANCE);

    public static final DeferredItem<BlockItem> TALL_BATHROOM_DOOR_FULL_3 = ITEMS.registerSimpleBlockItem(ModBlocks.TALL_BATHROOM_DOOR_FULL_3);
    public static final DeferredItem<BlockItem> TALL_BATHROOM_DOOR_FULL_4 = ITEMS.registerSimpleBlockItem(ModBlocks.TALL_BATHROOM_DOOR_FULL_4);
    public static final DeferredItem<BlockItem> SCHOOL_TINTED_GLASS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_TINTED_GLASS);
    public static final DeferredItem<BlockItem> TALL_BATHROOM_DOOR = ITEMS.registerSimpleBlockItem(ModBlocks.TALL_BATHROOM_DOOR);

    public static final DeferredItem<BlockItem> TALL_TEACHERS_OFFICE_DOOR = ITEMS.registerSimpleBlockItem(ModBlocks.TALL_TEACHERS_OFFICE_DOOR);

    public static final DeferredItem<BlockItem> TALL_DINING_DOOR = ITEMS.registerSimpleBlockItem(ModBlocks.TALL_DINING_DOOR);

    public static final DeferredItem<BlockItem> FOOTBALL_BASKETBALL_GOAL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleItems.FOOTBALL_BASKETBALL_GOAL;

    public static final DeferredItem<BlockItem> TOILET =
            ITEMS.registerSimpleBlockItem(ModBlocks.TOILET);
    public static final DeferredItem<BlockItem> ADULT_MEN_URINAL =
            ITEMS.registerSimpleBlockItem(ModBlocks.ADULT_MEN_URINAL);
    public static final DeferredItem<BlockItem> STANDALONE_TOILET = ITEMS.registerSimpleBlockItem(ModBlocks.STANDALONE_TOILET);
    public static final DeferredItem<BlockItem> MEN_URINAL =
            ITEMS.registerSimpleBlockItem(ModBlocks.MEN_URINAL);

    public static final DeferredItem<BlockItem> GREEN_TRASH_BIN =
            ITEMS.registerSimpleBlockItem(ModBlocks.GREEN_TRASH_BIN);
    public static final DeferredItem<BlockItem> BLACK_TRASH_BIN =
            ITEMS.registerSimpleBlockItem(ModBlocks.BLACK_TRASH_BIN);

    public static final DeferredItem<BlockItem> PROJECTOR_SCREEN =
            ITEMS.registerSimpleBlockItem(ModBlocks.PROJECTOR_SCREEN);
    public static final DeferredItem<BlockItem> WALL_PROJECTOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.WALL_PROJECTOR);
    public static final DeferredItem<BlockItem> CLASSROOM_TIMETABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_TIMETABLE);
    public static final DeferredItem<BlockItem> FIRE_ALARM =
            ITEMS.registerSimpleBlockItem(ModBlocks.FIRE_ALARM);

    public static final DeferredItem<BlockItem> EMERGENCY_BACKPACK =
            ITEMS.registerSimpleBlockItem(ModBlocks.EMERGENCY_BACKPACK);

    public static final DeferredItem<BlockItem> SCHOOL_PARQUET = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PARQUET);

    public static final DeferredItem<BlockItem> LIGHT_SCHOOL_TILED_FLOOR = ITEMS.registerSimpleBlockItem(ModBlocks.LIGHT_SCHOOL_TILED_FLOOR);
    public static final DeferredItem<BlockItem> DARK_SCHOOL_TILED_FLOOR = ITEMS.registerSimpleBlockItem(ModBlocks.DARK_SCHOOL_TILED_FLOOR);

    public static final DeferredItem<BlockItem> CLASSROOM_FLOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_FLOOR);
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_PAVEMENT);

    public static final DeferredItem<BlockItem> STREET_SCHOOL_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.STREET_SCHOOL_WALL);
    public static final DeferredItem<BlockItem> STREET_SCHOOL_BARS = ITEMS.registerSimpleBlockItem(ModBlocks.STREET_SCHOOL_BARS);

    public static final DeferredItem<BlockItem> LIGHT_SCHOOL_WALL =
            ITEMS.registerSimpleBlockItem(ModBlocks.LIGHT_SCHOOL_WALL);
    public static final DeferredItem<BlockItem> DARK_SCHOOL_WALL =
            ITEMS.registerSimpleBlockItem(ModBlocks.DARK_SCHOOL_WALL);
    public static final DeferredItem<BlockItem> SCHOOL_GATE =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_GATE);

    public static final java.util.List<DeferredItem<Item>> FOLDERS = net.nicomar2009.lsmmod.modules.books_notebooks_and_folders.registry.ModuleItems.FOLDERS;
    public static final java.util.List<DeferredItem<Item>> NOTEBOOKS = net.nicomar2009.lsmmod.modules.books_notebooks_and_folders.registry.ModuleItems.NOTEBOOKS;



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

    // EPIC is the highest vanilla item rarity; placement creates the complete display.
    public static final DeferredItem<BlockItem> SAN_MARTIN_DE_PORRES =
            ITEMS.registerItem("san_martin_de_porres",
                    props -> new BlockItem(ModBlocks.SAN_MARTIN_DE_PORRES.get(), props),
                    props -> props.rarity(Rarity.EPIC));

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

    public static final ResourceKey<JukeboxSong> LSM_ANTHEM_SONG = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.LSM_ANTHEM_SONG;

    public static final DeferredItem<Item> LSM_ANTHEM = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.LSM_ANTHEM;

    public static final ResourceKey<JukeboxSong> LSM_MARCH_SONG = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.LSM_MARCH_SONG;

    public static final DeferredItem<Item> LSM_MARCH = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.LSM_MARCH;

    public static final ResourceKey<JukeboxSong> QAPACKACHAMPA_SONG = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.QAPACKACHAMPA_SONG;

    public static final DeferredItem<Item> QAPACKACHAMPA = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.QAPACKACHAMPA;

    public static final ResourceKey<JukeboxSong> COFRADIA_SAN_MIGUEL_SONG = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.COFRADIA_SAN_MIGUEL_SONG;

    public static final DeferredItem<Item> COFRADIA_SAN_MIGUEL = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.COFRADIA_SAN_MIGUEL;

    public static final ResourceKey<JukeboxSong> CARNAVAL_HUACCANA_SONG = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.CARNAVAL_HUACCANA_SONG;

    public static final DeferredItem<Item> CARNAVAL_HUACCANA = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.CARNAVAL_HUACCANA;

    public static final ResourceKey<JukeboxSong> CARNAVAL_HUANUPAMPA_SONG = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.CARNAVAL_HUANUPAMPA_SONG;

    public static final DeferredItem<Item> CARNAVAL_HUANUPAMPA = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.CARNAVAL_HUANUPAMPA;

    public static final ResourceKey<JukeboxSong> CARNAVAL_MACARI_SONG = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.CARNAVAL_MACARI_SONG;

    public static final DeferredItem<Item> CARNAVAL_MACARI = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.CARNAVAL_MACARI;

    public static final ResourceKey<JukeboxSong> FORMACION_SONG = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.FORMACION_SONG;

    public static final DeferredItem<Item> FORMACION = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.FORMACION;

    public static final ResourceKey<JukeboxSong> HIMNOPERU_SONG = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.HIMNOPERU_SONG;

    public static final DeferredItem<Item> HIMNOPERU = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.HIMNOPERU;

    public static final DeferredItem<BlockItem> DINING_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.DINING_TABLE);

    public static final DeferredItem<BlockItem> DINING_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.DINING_CHAIR);

    public static final DeferredItem<BlockItem> SOFA =
            ITEMS.registerSimpleBlockItem(ModBlocks.SOFA);

    public static final DeferredItem<BlockItem> SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPEAKER);

    public static final DeferredItem<BlockItem> GENERAL_SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.GENERAL_SPEAKER);

    public static final DeferredItem<BlockItem> TALL_SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.TALL_SPEAKER);

    public static final DeferredItem<Item> PORTABLE_SPEAKER =
            ITEMS.registerItem("portable_speaker", PortableSpeakerItem::new);

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

    public static final DeferredItem<BlockItem> DINING_DOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.DINING_DOOR);

    public static final DeferredItem<BlockItem> TEACHERS_OFFICE_DOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEACHERS_OFFICE_DOOR);

    public static final DeferredItem<BlockItem> BATHROOM_DOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.BATHROOM_DOOR);

    public static final DeferredItem<BlockItem> MANUEL_TIRADO_BUST =
            ITEMS.registerSimpleBlockItem(ModBlocks.MANUEL_TIRADO_BUST);

    public static final DeferredItem<BlockItem> INFIRMARY_COT =
            ITEMS.registerSimpleBlockItem(ModBlocks.INFIRMARY_COT);

    public static final DeferredItem<Item> GUITAR = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.GUITAR;

    public static final DeferredItem<Item> MANDOLIN = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.MANDOLIN;

    public static final DeferredItem<Item> FLUTE = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.FLUTE;

    public static final DeferredItem<Item> TAMBOURINE = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.TAMBOURINE;

    public static final DeferredItem<Item> VIOLIN = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.VIOLIN;

    public static final DeferredItem<Item> MELODICA = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.MELODICA;

    public static final DeferredItem<BlockItem> AWNING_SUPPORT =
            ITEMS.registerItem("awning_support", props -> new net.nicomar2009.lsmmod.item.AwningSupportItem(ModBlocks.AWNING_SUPPORT.get(), props));

    public static final DeferredItem<Item> PLAYGROUND_AWNING =
            ITEMS.registerItem("playground_awning", props -> new AwningItem(props, ModBlocks.PLAYGROUND_AWNING));

    public static final DeferredItem<Item> ELEMENTARY_PLAYGROUND_AWNING =
            ITEMS.registerItem("elementary_playground_awning", props -> new AwningItem(props, ModBlocks.ELEMENTARY_PLAYGROUND_AWNING));

    public static Item awningVariant(String id) {
        return switch (id) {
            case "playground_awning" -> PLAYGROUND_AWNING.get();
            case "elementary_playground_awning" -> ELEMENTARY_PLAYGROUND_AWNING.get();
            default -> PLAYGROUND_AWNING.get();
        };
    }

    public static final DeferredItem<SpawnEggItem> STUDENT_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> TEACHER_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.TEACHER_SPAWN_EGG;

    public static final DeferredItem<RulerItem> WOODEN_RULER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.WOODEN_RULER;

    public static final DeferredItem<RulerItem> STONE_RULER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.STONE_RULER;

    public static final DeferredItem<RulerItem> GOLDEN_RULER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.GOLDEN_RULER;

    public static final DeferredItem<RulerItem> IRON_RULER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.IRON_RULER;

    public static final DeferredItem<RulerItem> DIAMOND_RULER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.DIAMOND_RULER;

    public static final DeferredItem<RulerItem> NETHERITE_RULER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.NETHERITE_RULER;

    public static final DeferredItem<SchoolUniformItem> FAKE_SCHOOL_HAIRCUT = net.nicomar2009.lsmmod.modules.uniforms.registry.ModuleItems.FAKE_SCHOOL_HAIRCUT;

    public static final DeferredItem<SchoolUniformItem> UNIFORM_POLO = net.nicomar2009.lsmmod.modules.uniforms.registry.ModuleItems.UNIFORM_POLO;

    public static final DeferredItem<SchoolUniformItem> UNIFORM_PANTS = net.nicomar2009.lsmmod.modules.uniforms.registry.ModuleItems.UNIFORM_PANTS;

    public static final DeferredItem<SchoolUniformItem> UNIFORM_SHOES = net.nicomar2009.lsmmod.modules.uniforms.registry.ModuleItems.UNIFORM_SHOES;

    public static final DeferredItem<SpawnEggItem> STUDENT_1P_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_1P_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_2P_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_2P_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_3P_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_3P_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_4P_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_4P_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_5P_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_5P_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_6P_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_6P_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_1S_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_1S_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_2S_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_2S_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_3S_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_3S_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_4S_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_4S_SPAWN_EGG;

    public static final DeferredItem<SpawnEggItem> STUDENT_5S_SPAWN_EGG = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.STUDENT_5S_SPAWN_EGG;



    public static final DeferredItem<Item> NPC_SUMMON_HELPER = net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.NPC_SUMMON_HELPER;

    public static final DeferredItem<Item> PLASTIC_BALL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleItems.PLASTIC_BALL;
    public static final DeferredItem<Item> FOOTBALL_BALL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleItems.FOOTBALL_BALL;
    public static final DeferredItem<Item> BASKETBALL_BALL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleItems.BASKETBALL_BALL;
    public static final DeferredItem<Item> VOLLEYBALL_BALL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleItems.VOLLEYBALL_BALL;


    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        // Keep existing inventories readable after the tiled-floor rename.
        ITEMS.addAlias(net.minecraft.resources.Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "bathroom_floor"), DARK_SCHOOL_TILED_FLOOR.getId());
        ITEMS.addAlias(net.minecraft.resources.Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "light_bathroom_floor"), LIGHT_SCHOOL_TILED_FLOOR.getId());
        ITEMS.register(modEventBus);
    }
}

