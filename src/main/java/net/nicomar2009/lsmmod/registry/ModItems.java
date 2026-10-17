package net.nicomar2009.lsmmod.registry;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.item.SchoolCurtainItem;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.school_supplies.item.RulerItem;
import net.nicomar2009.lsmmod.modules.uniforms.item.SchoolUniformItem;

/** Item registry for the school blocks; standalone items are planned. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = ModRegistries.ITEMS;

    // BEGIN DINING COUNTER
    public static final DeferredItem<BlockItem> DINING_ROOM_COUNTER = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.DINING_ROOM_COUNTER;
    // END DINING COUNTER

    // BEGIN CONNECTED TINTED GLASS
    public static final DeferredItem<BlockItem> SCHOOL_CONNECTED_TINTED_GLASS = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.SCHOOL_CONNECTED_TINTED_GLASS;
    // END CONNECTED TINTED GLASS

    public static final DeferredItem<BlockItem> SCHOOL_REFRIGERATOR = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.SCHOOL_REFRIGERATOR;

    public static final DeferredItem<BlockItem> DINING_WALL_CABINET = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleItems.DINING_WALL_CABINET;

    public static final DeferredItem<BlockItem> SCHOOL_SHELVING = net.nicomar2009.lsmmod.modules.shelving.registry.ModuleItems.SCHOOL_SHELVING;

    public static final DeferredItem<BlockItem> NIDO_STORAGE_BUCKET = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleItems.NIDO_STORAGE_BUCKET;

    public static final DeferredItem<BlockItem> SCHOOL_PROJECTING_WINDOW = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.SCHOOL_PROJECTING_WINDOW;

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.SCHOOL_PAINTABLE_PROJECTING_WINDOW;

    public static final DeferredItem<BlockItem> SCHOOL_NAME_SIGN = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleItems.SCHOOL_NAME_SIGN;

    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE_PAINTABLE = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.BATHROOM_DOOR_WIDE_PAINTABLE;

    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_DOOR_PAINTABLE = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.SCHOOL_ENTRANCE_DOOR_PAINTABLE;

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_GLASS_STAIRS = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.SCHOOL_PAINTABLE_GLASS_STAIRS;

    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE_PAINTABLE_COLUMN = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.BATHROOM_DOOR_WIDE_PAINTABLE_COLUMN;

    public static final DeferredItem<BlockItem> DOUBLE_SCHOOL_ENTRANCE_PAINTABLE_SLAB = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.DOUBLE_SCHOOL_ENTRANCE_PAINTABLE_SLAB;

    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE_2X4 = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.BATHROOM_DOOR_WIDE_2X4;

    public static final DeferredItem<BlockItem> SCHOOL_LABORATORY_TILE = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.SCHOOL_LABORATORY_TILE;

    public static final DeferredItem<BlockItem> LABORATORY_TABLE_TILE = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.LABORATORY_TABLE_TILE;

    public static final DeferredItem<BlockItem> AUDITORIUM_PARQUET = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.AUDITORIUM_PARQUET;

    public static final DeferredItem<BlockItem> AUDITORIUM_STAGE = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.AUDITORIUM_STAGE;

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB;

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS;

    // BEGIN FACILITY EXPANSIONS
    public static final DeferredItem<BlockItem> DINING_DOOR_WIDE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.DINING_DOOR_WIDE;
    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.BATHROOM_DOOR_WIDE;
    public static final DeferredItem<BlockItem> SCHOOL_MICROWAVE = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.SCHOOL_MICROWAVE;
    public static final DeferredItem<BlockItem> SCHOOL_TROPHY_GALLERY = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleItems.SCHOOL_TROPHY_GALLERY;
    public static final DeferredItem<BlockItem> INFIRMARY_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.INFIRMARY_DOOR;
    public static final DeferredItem<BlockItem> DINING_DOOR_WIDE_LIGHT = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.DINING_DOOR_WIDE_LIGHT;
    // END FACILITY EXPANSIONS

    // BEGIN KIOSK ENTRANCE ACCESSORIES
    public static final DeferredItem<BlockItem> SCHOOL_WOOD_PANEL = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleItems.SCHOOL_WOOD_PANEL;
    public static final DeferredItem<BlockItem> SCHOOL_WOOD_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.SCHOOL_WOOD_DOOR;
    public static final DeferredItem<BlockItem> SCHOOL_SLIDING_ENTRANCE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.SCHOOL_SLIDING_ENTRANCE;
    public static final DeferredItem<BlockItem> DRINKING_FOUNTAIN_RAISED = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.DRINKING_FOUNTAIN_RAISED;
    public static final DeferredItem<BlockItem> LOST_AND_FOUND_BOX = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleItems.LOST_AND_FOUND_BOX;
    // END KIOSK ENTRANCE ACCESSORIES

    // BEGIN SCHOOL CRUCIFIX
    public static final DeferredItem<BlockItem> SCHOOL_CRUCIFIX = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleItems.SCHOOL_CRUCIFIX;
    // END SCHOOL CRUCIFIX

    // BEGIN DARK TILE DRAIN COLUMN
    public static final DeferredItem<BlockItem> DARK_TILED_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.DARK_TILED_FLOOR;
    public static final DeferredItem<BlockItem> SCHOOL_DRAIN = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.SCHOOL_DRAIN;
    public static final DeferredItem<BlockItem> SCHOOL_CIRCULAR_COLUMN = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.SCHOOL_CIRCULAR_COLUMN;
    // END DARK TILE DRAIN COLUMN

    // BEGIN CHILDREN LOCKER AND CEILING PROJECTOR
    public static final DeferredItem<BlockItem> CHILDREN_LOCKER = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleItems.CHILDREN_LOCKER;
    public static final DeferredItem<BlockItem> CEILING_PROJECTOR = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.CEILING_PROJECTOR;
    // END CHILDREN LOCKER AND CEILING PROJECTOR

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_MINI_CUBE_BLOCK = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.SCHOOL_PAINTABLE_MINI_CUBE_BLOCK;

    // BEGIN CUSTOM PAINTABLE BLOCKS
    public static final DeferredItem<BlockItem> SCHOOL_FOUNDATION = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.SCHOOL_FOUNDATION;
    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_CUSTOM_BLOCK = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.SCHOOL_PAINTABLE_CUSTOM_BLOCK;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolSandpaperItem> SCHOOL_SANDPAPER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.SCHOOL_SANDPAPER;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolHammerItem> SCHOOL_HAMMER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.SCHOOL_HAMMER;
    // END CUSTOM PAINTABLE BLOCKS

    // BEGIN PAINTABLE SCHOOL BLOCK
    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_BLOCK = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.SCHOOL_PAINTABLE_BLOCK;
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
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_BLOCK = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.YELLOW_SCHOOL_BLOCK;
    // END YELLOW SCHOOL BLOCK

    // BEGIN TILED PARQUET FLOOR
    public static final DeferredItem<BlockItem> TILED_PARQUET_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.TILED_PARQUET_FLOOR;
    // END TILED PARQUET FLOOR

    // BEGIN SCHOOL CABINET AND ROUTER
    public static final DeferredItem<BlockItem> SCHOOL_CABINET = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.SCHOOL_CABINET;
    public static final DeferredItem<BlockItem> SCHOOL_ROUTER = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.SCHOOL_ROUTER;
    // END SCHOOL CABINET AND ROUTER

    // BEGIN SCHOOL SINK
    public static final DeferredItem<BlockItem> SCHOOL_SINK = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.SCHOOL_SINK;
    // END SCHOOL SINK

    // BEGIN YELLOW SCHOOL SLOPES
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_SLOPE_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.YELLOW_SCHOOL_SLOPE_1;
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_SLOPE_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.YELLOW_SCHOOL_SLOPE_2;
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_SLOPE_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.YELLOW_SCHOOL_SLOPE_3;
    public static final DeferredItem<BlockItem> YELLOW_SCHOOL_SLOPE_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.YELLOW_SCHOOL_SLOPE_4;
    // END YELLOW SCHOOL SLOPES

    // BEGIN PLAYGROUND FLOORS
    public static final DeferredItem<BlockItem> WHITE_PLAYGROUND_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.WHITE_PLAYGROUND_FLOOR;
    public static final DeferredItem<BlockItem> GRAY_PLAYGROUND_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.GRAY_PLAYGROUND_FLOOR;
    public static final DeferredItem<BlockItem> GREEN_PLAYGROUND_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.GREEN_PLAYGROUND_FLOOR;
    public static final DeferredItem<BlockItem> ORANGE_PLAYGROUND_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.ORANGE_PLAYGROUND_FLOOR;
    // END PLAYGROUND FLOORS

    // BEGIN CONNECTED SCHOOL SINK
    public static final DeferredItem<BlockItem> SCHOOL_CONNECTED_SINK = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.SCHOOL_CONNECTED_SINK;
    // END CONNECTED SCHOOL SINK

    // BEGIN CLASSROOM SURFACES
    public static final DeferredItem<BlockItem> CLASSROOM_WHITE_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.CLASSROOM_WHITE_WALL;
    public static final DeferredItem<BlockItem> CLASSROOM_PAINTED_BLOCK = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.CLASSROOM_PAINTED_BLOCK;
    public static final DeferredItem<BlockItem> SCHOOL_SYNTHETIC_GRASS = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.SCHOOL_SYNTHETIC_GRASS;
    public static final DeferredItem<BlockItem> RED_STREET_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.RED_STREET_WALL;
    // END CLASSROOM SURFACES

    // BEGIN SCHOOL RAILING SLOPES
    // BEGIN SHORT RAISED SLOPES
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SHORT_RAISED_1;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SHORT_RAISED_2;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SHORT_RAISED_3;
    // END SHORT RAISED SLOPES

    // BEGIN SCHOOL INVERTED SLOPES
    // BEGIN SCHOOL SPLIT RAISED EXTRAS
    // BEGIN ENTRANCE PAVEMENT SHAPES
    public static final DeferredItem<BlockItem> SCHOOL_CLOCK = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleItems.SCHOOL_CLOCK;
    public static final DeferredItem<BlockItem> SCHOOL_FAN = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleItems.SCHOOL_FAN;
    public static final DeferredItem<BlockItem> SCHOOL_WOODEN_HANDRAIL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.SCHOOL_WOODEN_HANDRAIL;

    public static final DeferredItem<BlockItem> CEILING_LIGHT = net.nicomar2009.lsmmod.modules.lighting.registry.ModuleItems.CEILING_LIGHT;

    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_1;
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_2;
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_3;
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_4;
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_5 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_5;
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_6 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_6;
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT_SLAB = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.SCHOOL_ENTRANCE_PAVEMENT_SLAB;
    // END ENTRANCE PAVEMENT SHAPES

    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_1;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_2;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_3;
    // END SCHOOL SPLIT RAISED EXTRAS

    // BEGIN SCHOOL RAISED INVERTED SLOPES
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_1;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_2;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_3;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_4;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_1;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_2;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_3;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_4;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_5 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_5;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_6 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_6;
    // END SCHOOL RAISED INVERTED SLOPES

    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_1;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_2;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_3;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_SPLIT_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_SPLIT_4;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_1;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_2;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_3;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_4;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_5 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_5;
    public static final DeferredItem<BlockItem> SCHOOL_INVERTED_SLOPE_FLIGHT_6 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_INVERTED_SLOPE_FLIGHT_6;
    // END SCHOOL INVERTED SLOPES

    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_SPLIT_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_SPLIT_1;
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_SPLIT_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_SPLIT_2;
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_SPLIT_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_SPLIT_3;
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_SPLIT_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_SPLIT_4;
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_FLIGHT_1;
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_FLIGHT_2;
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_FLIGHT_3;
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_FLIGHT_4;
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_5 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_FLIGHT_5;
    public static final DeferredItem<BlockItem> SCHOOL_RAILING_SLOPE_FLIGHT_6 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.SCHOOL_RAILING_SLOPE_FLIGHT_6;
    // END SCHOOL RAILING SLOPES

    public static final DeferredItem<BlockItem> LIGHT_SCHOOL_WALL_RAILING = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.LIGHT_SCHOOL_WALL_RAILING;

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.windows_and_enclosures.item.SchoolCurtainItem> SCHOOL_CURTAIN =
            ITEMS.registerItem("school_curtain",net.nicomar2009.lsmmod.modules.windows_and_enclosures.item.SchoolCurtainItem::new);

    public static final DeferredItem<BlockItem> SCHOOL_GLASS = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.SCHOOL_GLASS;
    public static final DeferredItem<BlockItem> DOUBLE_SCHOOL_ENTRANCE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.DOUBLE_SCHOOL_ENTRANCE;
    public static final DeferredItem<BlockItem> TALL_CLASSROOM_ENTRANCE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.TALL_CLASSROOM_ENTRANCE;

    public static final DeferredItem<BlockItem> TALL_BATHROOM_DOOR_FULL_3 = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.TALL_BATHROOM_DOOR_FULL_3;
    public static final DeferredItem<BlockItem> TALL_BATHROOM_DOOR_FULL_4 = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.TALL_BATHROOM_DOOR_FULL_4;
    public static final DeferredItem<BlockItem> SCHOOL_TINTED_GLASS = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.SCHOOL_TINTED_GLASS;
    public static final DeferredItem<BlockItem> TALL_BATHROOM_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.TALL_BATHROOM_DOOR;

    public static final DeferredItem<BlockItem> TALL_TEACHERS_OFFICE_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.TALL_TEACHERS_OFFICE_DOOR;

    public static final DeferredItem<BlockItem> TALL_DINING_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.TALL_DINING_DOOR;

    public static final DeferredItem<BlockItem> FOOTBALL_BASKETBALL_GOAL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleItems.FOOTBALL_BASKETBALL_GOAL;

    public static final DeferredItem<BlockItem> TOILET = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.TOILET;
    public static final DeferredItem<BlockItem> ADULT_MEN_URINAL = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.ADULT_MEN_URINAL;
    public static final DeferredItem<BlockItem> STANDALONE_TOILET = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.STANDALONE_TOILET;
    public static final DeferredItem<BlockItem> MEN_URINAL = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.MEN_URINAL;

    public static final DeferredItem<BlockItem> GREEN_TRASH_BIN = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.GREEN_TRASH_BIN;
    public static final DeferredItem<BlockItem> BLACK_TRASH_BIN = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.BLACK_TRASH_BIN;

    public static final DeferredItem<BlockItem> PROJECTOR_SCREEN = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.PROJECTOR_SCREEN;
    public static final DeferredItem<BlockItem> WALL_PROJECTOR = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.WALL_PROJECTOR;
    public static final DeferredItem<BlockItem> CLASSROOM_TIMETABLE = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleItems.CLASSROOM_TIMETABLE;
    public static final DeferredItem<BlockItem> FIRE_ALARM = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleItems.FIRE_ALARM;

    public static final DeferredItem<BlockItem> EMERGENCY_BACKPACK = net.nicomar2009.lsmmod.modules.backpacks.registry.ModuleItems.EMERGENCY_BACKPACK;

    public static final DeferredItem<BlockItem> SCHOOL_PARQUET = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.SCHOOL_PARQUET;

    public static final DeferredItem<BlockItem> LIGHT_SCHOOL_TILED_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.LIGHT_SCHOOL_TILED_FLOOR;
    public static final DeferredItem<BlockItem> DARK_SCHOOL_TILED_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.DARK_SCHOOL_TILED_FLOOR;

    public static final DeferredItem<BlockItem> CLASSROOM_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.CLASSROOM_FLOOR;
    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_PAVEMENT = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.SCHOOL_ENTRANCE_PAVEMENT;

    public static final DeferredItem<BlockItem> STREET_SCHOOL_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.STREET_SCHOOL_WALL;
    public static final DeferredItem<BlockItem> STREET_SCHOOL_BARS = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.STREET_SCHOOL_BARS;

    public static final DeferredItem<BlockItem> LIGHT_SCHOOL_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.LIGHT_SCHOOL_WALL;
    public static final DeferredItem<BlockItem> DARK_SCHOOL_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.DARK_SCHOOL_WALL;
    public static final DeferredItem<BlockItem> SCHOOL_GATE = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.SCHOOL_GATE;

    public static final java.util.List<DeferredItem<Item>> FOLDERS = net.nicomar2009.lsmmod.modules.books_notebooks_and_folders.registry.ModuleItems.FOLDERS;
    public static final java.util.List<DeferredItem<Item>> NOTEBOOKS = net.nicomar2009.lsmmod.modules.books_notebooks_and_folders.registry.ModuleItems.NOTEBOOKS;

    // Block item for the elementary chair (registry name taken from the block: "lsmmod:elementary_chair")
    public static final DeferredItem<BlockItem> ELEMENTARY_CHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.ELEMENTARY_CHAIR;

    // Block item for the high school chair (registry name taken from the block: "lsmmod:high_school_chair")
    public static final DeferredItem<BlockItem> HIGH_SCHOOL_CHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.HIGH_SCHOOL_CHAIR;

    // Block item for the desk (placing it creates both halves)
    public static final DeferredItem<BlockItem> STUDENTS_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.STUDENTS_DESK;

    // Block item for the locker
    public static final DeferredItem<BlockItem> LOCKER = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleItems.LOCKER;

    // Block item for the teachers chair (registry name taken from the block: "lsmmod:teachers_chair")
    public static final DeferredItem<BlockItem> TEACHERS_CHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.TEACHERS_CHAIR;

    // Block item for the desk (placing it creates both halves)
    public static final DeferredItem<BlockItem> TEACHERS_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.TEACHERS_DESK;

    // Block item for the elementary desk (a single block with 9 slots)
    public static final DeferredItem<BlockItem> ELEMENTARY_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.ELEMENTARY_DESK;

    // EPIC is the highest vanilla item rarity; placement creates the complete display.
    public static final DeferredItem<BlockItem> SAN_MARTIN_DE_PORRES = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleItems.SAN_MARTIN_DE_PORRES;

    public static final DeferredItem<BlockItem> SCHOOL_SHIELD = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleItems.SCHOOL_SHIELD;

    public static final DeferredItem<BlockItem> ENGLISHROOMCHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.ENGLISHROOMCHAIR;

    public static final DeferredItem<BlockItem> HALLCHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.HALLCHAIR;

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.PLASTICCHAIR_WHITE;

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.PLASTICCHAIR_RED;

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE_ARMS = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.PLASTICCHAIR_WHITE_ARMS;

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED_ARMS = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.PLASTICCHAIR_RED_ARMS;

    public static final DeferredItem<BlockItem> LAPTOP = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.LAPTOP;

    public static final DeferredItem<BlockItem> MOISES_LAPTOP = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.MOISES_LAPTOP;

    public static final DeferredItem<BlockItem> PC = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.PC;

    public static final DeferredItem<BlockItem> COMPUTER_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.COMPUTER_DESK;

    public static final DeferredItem<BlockItem> GRAY_COMPUTER_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.GRAY_COMPUTER_DESK;

    public static final DeferredItem<BlockItem> SCHOOL_BELL = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.SCHOOL_BELL;

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

    public static final DeferredItem<BlockItem> DINING_TABLE = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.DINING_TABLE;

    public static final DeferredItem<BlockItem> DINING_CHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.DINING_CHAIR;

    public static final DeferredItem<BlockItem> SOFA = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.SOFA;

    public static final DeferredItem<BlockItem> SPEAKER = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.SPEAKER;

    public static final DeferredItem<BlockItem> GENERAL_SPEAKER = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.GENERAL_SPEAKER;

    public static final DeferredItem<BlockItem> TALL_SPEAKER = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.TALL_SPEAKER;

    public static final DeferredItem<Item> PORTABLE_SPEAKER = net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.PORTABLE_SPEAKER;

    public static final DeferredItem<BlockItem> DRINKING_FOUNTAIN = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.DRINKING_FOUNTAIN;

    public static final DeferredItem<BlockItem> KIOSK_TABLE = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.KIOSK_TABLE;

    public static final DeferredItem<BlockItem> ART_TABLE = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.ART_TABLE;

    public static final DeferredItem<BlockItem> STOOL = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.STOOL;

    public static final DeferredItem<BlockItem> VAULTING_BOX = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleItems.VAULTING_BOX;

    public static final DeferredItem<BlockItem> CLASSROOM_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.CLASSROOM_DOOR;

    public static final DeferredItem<BlockItem> DINING_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.DINING_DOOR;

    public static final DeferredItem<BlockItem> TEACHERS_OFFICE_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.TEACHERS_OFFICE_DOOR;

    public static final DeferredItem<BlockItem> BATHROOM_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.BATHROOM_DOOR;

    public static final DeferredItem<BlockItem> MANUEL_TIRADO_BUST = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleItems.MANUEL_TIRADO_BUST;

    public static final DeferredItem<BlockItem> INFIRMARY_COT = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.INFIRMARY_COT;

    public static final DeferredItem<Item> GUITAR = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.GUITAR;

    public static final DeferredItem<Item> MANDOLIN = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.MANDOLIN;

    public static final DeferredItem<Item> FLUTE = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.FLUTE;

    public static final DeferredItem<Item> TAMBOURINE = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.TAMBOURINE;

    public static final DeferredItem<Item> VIOLIN = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.VIOLIN;

    public static final DeferredItem<Item> MELODICA = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.MELODICA;

    public static final DeferredItem<BlockItem> AWNING_SUPPORT = net.nicomar2009.lsmmod.modules.awnings.registry.ModuleItems.AWNING_SUPPORT;

    public static final DeferredItem<Item> PLAYGROUND_AWNING = net.nicomar2009.lsmmod.modules.awnings.registry.ModuleItems.PLAYGROUND_AWNING;

    public static final DeferredItem<Item> ELEMENTARY_PLAYGROUND_AWNING = net.nicomar2009.lsmmod.modules.awnings.registry.ModuleItems.ELEMENTARY_PLAYGROUND_AWNING;

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

