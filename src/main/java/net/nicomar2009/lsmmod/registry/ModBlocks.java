package net.nicomar2009.lsmmod.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.LostAndFoundBoxBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolStairProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.technology.block.GeneralSpeakerBlock;
import net.nicomar2009.lsmmod.modules.slopes.block.YellowSchoolSlopeBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.WideBathroomBlock;
import net.nicomar2009.lsmmod.modules.technology.block.WallProjectorBlock;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.WallDecorationBlock;
import net.nicomar2009.lsmmod.modules.special_blocks.block.VaultingBoxBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.TrashBinBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.ToiletBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.TeachersDeskBlock;
import net.nicomar2009.lsmmod.modules.technology.block.TallSpeakerBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.TallClassroomEntranceBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.TallBathroomBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.StreetSchoolBarsBlock;
import net.nicomar2009.lsmmod.modules.chairs.block.StoolBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SpeakerBlock;
import net.nicomar2009.lsmmod.modules.chairs.block.SofaBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolYellowBorderWallBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolWoodenHandrailBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolWallRailingBlock;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.SchoolWallDecorationBlock;
import net.nicomar2009.lsmmod.modules.special_blocks.block.SchoolTrophyGalleryBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolTintedGlassBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolSlidingEntranceBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolSinkBlock;
import net.nicomar2009.lsmmod.modules.special_blocks.block.SchoolShieldBlock;
import net.nicomar2009.lsmmod.modules.shelving.block.SchoolShelvingBlock;
import net.nicomar2009.lsmmod.modules.special_blocks.block.SchoolSanMartinBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SchoolRouterBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolRefrigeratorBlock;
import net.nicomar2009.lsmmod.modules.slopes.block.SchoolRailingSlopeBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolPassiveAccessoryBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableGlassStairBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableEntranceDoorBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableDoubleEntranceBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableCustomBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBathroomDoorBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBathroomColumnDoorBlock;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.SchoolNameSignBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolMiniCubePaintableBlock;
import net.nicomar2009.lsmmod.modules.slopes.block.SchoolInvertedSlopeBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolGlassBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolGateBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolFramedDiningDoorBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolDrainBlock;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.SchoolCrucifixBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolConnectedSinkBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolCircularColumnBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SchoolCabinetBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SchoolBellBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolBathroomDoor2x4Block;
import net.nicomar2009.lsmmod.modules.facilities.block.RaisedDrinkingFountainBlock;
import net.nicomar2009.lsmmod.modules.technology.block.ProjectorScreenBlock;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.NidoStorageBucketBlock;
import net.nicomar2009.lsmmod.modules.technology.block.MonitorBlock;
import net.nicomar2009.lsmmod.modules.special_blocks.block.ManuelTiradoBustBlock;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.LockerBlock;
import net.nicomar2009.lsmmod.modules.technology.block.LaptopBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.KioskTableBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.InfirmaryCotBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.GrayComputerDeskBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.FullBathroomDoorBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.FloorTransitionBlock;
import net.nicomar2009.lsmmod.modules.slopes.block.EntrancePavementSlopeBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.EntrancePavementSlabBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ElementaryDeskBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.DrinkingFountainBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.DoubleSchoolEntranceBlock;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.DiningWallCabinetBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.DiningTableBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.DiningRoomCounterBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.DeskBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ComputerDeskBlock;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.ChildrenLockerBlock;
import net.nicomar2009.lsmmod.modules.chairs.block.ChairBlock;
import net.nicomar2009.lsmmod.modules.technology.block.CeilingProjectorBlock;
import net.nicomar2009.lsmmod.modules.lighting.block.CeilingLightBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.BathroomFloorBlock;
import net.nicomar2009.lsmmod.modules.awnings.block.AwningSupportBlock;
import net.nicomar2009.lsmmod.modules.awnings.block.AwningBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ArtTableBlock;
import net.nicomar2009.lsmmod.modules.balls.block.SportsGoalBlock;

/** Registers the school furniture, statue and shield. */
public final class ModBlocks {
    // Specialized register: it sets the block's resource key (required since 1.21.2) automatically
    public static final DeferredRegister.Blocks BLOCKS = ModRegistries.BLOCKS;

    // BEGIN SCHOOL RAILING SLOPES
    // BEGIN SHORT RAISED SLOPES
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SHORT_RAISED_1;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SHORT_RAISED_2;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SHORT_RAISED_3;
    // END SHORT RAISED SLOPES

    // BEGIN SCHOOL INVERTED SLOPES
    // BEGIN SCHOOL SPLIT RAISED EXTRAS
    // BEGIN ENTRANCE PAVEMENT SHAPES
    // BEGIN DINING COUNTER
    public static final DeferredBlock<DiningRoomCounterBlock> DINING_ROOM_COUNTER = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.DINING_ROOM_COUNTER;
    // END DINING COUNTER

    // BEGIN CONNECTED TINTED GLASS
    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_CONNECTED_TINTED_GLASS = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.SCHOOL_CONNECTED_TINTED_GLASS;
    // END CONNECTED TINTED GLASS

    public static final DeferredBlock<SchoolRefrigeratorBlock> SCHOOL_REFRIGERATOR = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.SCHOOL_REFRIGERATOR;

    public static final DeferredBlock<DiningWallCabinetBlock> DINING_WALL_CABINET = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlocks.DINING_WALL_CABINET;

    public static final DeferredBlock<SchoolShelvingBlock> SCHOOL_SHELVING = net.nicomar2009.lsmmod.modules.shelving.registry.ModuleBlocks.SCHOOL_SHELVING;

    public static final DeferredBlock<NidoStorageBucketBlock> NIDO_STORAGE_BUCKET = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlocks.NIDO_STORAGE_BUCKET;

    public static final DeferredBlock<SchoolProjectingWindowBlock> SCHOOL_PROJECTING_WINDOW = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.SCHOOL_PROJECTING_WINDOW;

    public static final DeferredBlock<SchoolPaintableProjectingWindowBlock> SCHOOL_PAINTABLE_PROJECTING_WINDOW = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW;

    public static final DeferredBlock<SchoolNameSignBlock> SCHOOL_NAME_SIGN = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleBlocks.SCHOOL_NAME_SIGN;

    public static final DeferredBlock<SchoolPaintableBathroomDoorBlock> BATHROOM_DOOR_WIDE_PAINTABLE = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.BATHROOM_DOOR_WIDE_PAINTABLE;

    public static final DeferredBlock<SchoolPaintableEntranceDoorBlock> SCHOOL_ENTRANCE_DOOR_PAINTABLE = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.SCHOOL_ENTRANCE_DOOR_PAINTABLE;

    public static final DeferredBlock<SchoolPaintableGlassStairBlock> SCHOOL_PAINTABLE_GLASS_STAIRS = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.SCHOOL_PAINTABLE_GLASS_STAIRS;

    public static final DeferredBlock<SchoolPaintableBathroomColumnDoorBlock> BATHROOM_DOOR_WIDE_PAINTABLE_COLUMN = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.BATHROOM_DOOR_WIDE_PAINTABLE_COLUMN;

    public static final DeferredBlock<SchoolPaintableDoubleEntranceBlock> DOUBLE_SCHOOL_ENTRANCE_PAINTABLE_SLAB = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.DOUBLE_SCHOOL_ENTRANCE_PAINTABLE_SLAB;

    public static final DeferredBlock<SchoolBathroomDoor2x4Block> BATHROOM_DOOR_WIDE_2X4 = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.BATHROOM_DOOR_WIDE_2X4;

    public static final DeferredBlock<Block> SCHOOL_LABORATORY_TILE = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_LABORATORY_TILE;

    public static final DeferredBlock<Block> LABORATORY_TABLE_TILE = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.LABORATORY_TABLE_TILE;

    public static final DeferredBlock<Block> AUDITORIUM_PARQUET = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.AUDITORIUM_PARQUET;

    public static final DeferredBlock<Block> AUDITORIUM_STAGE = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.AUDITORIUM_STAGE;

    public static final DeferredBlock<SchoolSlabProjectingWindowBlock> SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB;

    public static final DeferredBlock<SchoolStairProjectingWindowBlock> SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS;

    // BEGIN FACILITY EXPANSIONS
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> DINING_DOOR_WIDE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.DINING_DOOR_WIDE;
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> BATHROOM_DOOR_WIDE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.BATHROOM_DOOR_WIDE;
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> INFIRMARY_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.INFIRMARY_DOOR;
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> DINING_DOOR_WIDE_LIGHT = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.DINING_DOOR_WIDE_LIGHT;
    public static final DeferredBlock<SchoolPassiveAccessoryBlock> SCHOOL_MICROWAVE = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.SCHOOL_MICROWAVE;
    public static final DeferredBlock<SchoolTrophyGalleryBlock> SCHOOL_TROPHY_GALLERY = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleBlocks.SCHOOL_TROPHY_GALLERY;
    // END FACILITY EXPANSIONS

    // BEGIN KIOSK ENTRANCE ACCESSORIES
    public static final DeferredBlock<SchoolPassiveAccessoryBlock> SCHOOL_WOOD_PANEL = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleBlocks.SCHOOL_WOOD_PANEL;
    public static final DeferredBlock<FullBathroomDoorBlock> SCHOOL_WOOD_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.SCHOOL_WOOD_DOOR;
    public static final DeferredBlock<SchoolSlidingEntranceBlock> SCHOOL_SLIDING_ENTRANCE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.SCHOOL_SLIDING_ENTRANCE;
    public static final DeferredBlock<RaisedDrinkingFountainBlock> DRINKING_FOUNTAIN_RAISED = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.DRINKING_FOUNTAIN_RAISED;
    public static final DeferredBlock<LostAndFoundBoxBlock> LOST_AND_FOUND_BOX = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlocks.LOST_AND_FOUND_BOX;
    // END KIOSK ENTRANCE ACCESSORIES

    // BEGIN SCHOOL CRUCIFIX
    public static final DeferredBlock<SchoolCrucifixBlock> SCHOOL_CRUCIFIX = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleBlocks.SCHOOL_CRUCIFIX;
    // END SCHOOL CRUCIFIX

    // BEGIN DARK TILE DRAIN COLUMN
    public static final DeferredBlock<Block> DARK_TILED_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.DARK_TILED_FLOOR;
    public static final DeferredBlock<SchoolDrainBlock> SCHOOL_DRAIN = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.SCHOOL_DRAIN;
    public static final DeferredBlock<SchoolCircularColumnBlock> SCHOOL_CIRCULAR_COLUMN = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_CIRCULAR_COLUMN;
    // END DARK TILE DRAIN COLUMN

    // BEGIN CHILDREN LOCKER AND CEILING PROJECTOR
    public static final DeferredBlock<ChildrenLockerBlock> CHILDREN_LOCKER = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlocks.CHILDREN_LOCKER;
    public static final DeferredBlock<CeilingProjectorBlock> CEILING_PROJECTOR = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.CEILING_PROJECTOR;
    // END CHILDREN LOCKER AND CEILING PROJECTOR

    public static final DeferredBlock<SchoolMiniCubePaintableBlock> SCHOOL_PAINTABLE_MINI_CUBE_BLOCK = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.SCHOOL_PAINTABLE_MINI_CUBE_BLOCK;

    // BEGIN CUSTOM PAINTABLE BLOCKS
    public static final DeferredBlock<Block> SCHOOL_FOUNDATION = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_FOUNDATION;
    public static final DeferredBlock<SchoolPaintableCustomBlock> SCHOOL_PAINTABLE_CUSTOM_BLOCK = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.SCHOOL_PAINTABLE_CUSTOM_BLOCK;
    // END CUSTOM PAINTABLE BLOCKS

    // BEGIN PAINTABLE SCHOOL BLOCK
    public static final DeferredBlock<SchoolPaintableBlock> SCHOOL_PAINTABLE_BLOCK = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.SCHOOL_PAINTABLE_BLOCK;
    // END PAINTABLE SCHOOL BLOCK

    // BEGIN YELLOW SCHOOL BLOCK
    public static final DeferredBlock<Block> YELLOW_SCHOOL_BLOCK = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.YELLOW_SCHOOL_BLOCK;
    // END YELLOW SCHOOL BLOCK

    // BEGIN TILED PARQUET FLOOR
    public static final DeferredBlock<FloorTransitionBlock> TILED_PARQUET_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.TILED_PARQUET_FLOOR;
    // END TILED PARQUET FLOOR

    // BEGIN SCHOOL CABINET AND ROUTER
    public static final DeferredBlock<SchoolCabinetBlock> SCHOOL_CABINET = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.SCHOOL_CABINET;
    public static final DeferredBlock<SchoolRouterBlock> SCHOOL_ROUTER = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.SCHOOL_ROUTER;
    // END SCHOOL CABINET AND ROUTER

    // BEGIN SCHOOL SINK
    public static final DeferredBlock<SchoolSinkBlock> SCHOOL_SINK = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.SCHOOL_SINK;
    // END SCHOOL SINK

    // BEGIN YELLOW SCHOOL SLOPES
    public static final DeferredBlock<YellowSchoolSlopeBlock> YELLOW_SCHOOL_SLOPE_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.YELLOW_SCHOOL_SLOPE_1;

    public static final DeferredBlock<YellowSchoolSlopeBlock> YELLOW_SCHOOL_SLOPE_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.YELLOW_SCHOOL_SLOPE_2;

    public static final DeferredBlock<YellowSchoolSlopeBlock> YELLOW_SCHOOL_SLOPE_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.YELLOW_SCHOOL_SLOPE_3;

    public static final DeferredBlock<YellowSchoolSlopeBlock> YELLOW_SCHOOL_SLOPE_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.YELLOW_SCHOOL_SLOPE_4;
    // END YELLOW SCHOOL SLOPES

    // BEGIN PLAYGROUND FLOORS
    public static final DeferredBlock<Block> WHITE_PLAYGROUND_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.WHITE_PLAYGROUND_FLOOR;
    public static final DeferredBlock<Block> GRAY_PLAYGROUND_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.GRAY_PLAYGROUND_FLOOR;
    public static final DeferredBlock<Block> GREEN_PLAYGROUND_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.GREEN_PLAYGROUND_FLOOR;
    public static final DeferredBlock<Block> ORANGE_PLAYGROUND_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.ORANGE_PLAYGROUND_FLOOR;
    // END PLAYGROUND FLOORS

    // BEGIN CONNECTED SCHOOL SINK
    public static final DeferredBlock<SchoolConnectedSinkBlock> SCHOOL_CONNECTED_SINK = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.SCHOOL_CONNECTED_SINK;
    // END CONNECTED SCHOOL SINK

    // BEGIN CLASSROOM SURFACES
    public static final DeferredBlock<Block> CLASSROOM_WHITE_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.CLASSROOM_WHITE_WALL;
    // Kept as a registry alias for saved worlds and inventory stacks.
    public static final DeferredBlock<Block> CLASSROOM_PAINTED_BLOCK = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.CLASSROOM_PAINTED_BLOCK;
    public static final DeferredBlock<Block> SCHOOL_SYNTHETIC_GRASS = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_SYNTHETIC_GRASS;
    public static final DeferredBlock<Block> RED_STREET_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.RED_STREET_WALL;
    // END CLASSROOM SURFACES

    public static final DeferredBlock<SchoolWallDecorationBlock> SCHOOL_CLOCK = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleBlocks.SCHOOL_CLOCK;
    public static final DeferredBlock<SchoolWallDecorationBlock> SCHOOL_FAN = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleBlocks.SCHOOL_FAN;
    public static final DeferredBlock<SchoolWoodenHandrailBlock> SCHOOL_WOODEN_HANDRAIL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_WOODEN_HANDRAIL;

    public static final DeferredBlock<CeilingLightBlock> CEILING_LIGHT = net.nicomar2009.lsmmod.modules.lighting.registry.ModuleBlocks.CEILING_LIGHT;

    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_1;
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_2;
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_3;
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_4;
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_5 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_5;
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_6 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLOPE_6;
    public static final DeferredBlock<EntrancePavementSlabBlock> SCHOOL_ENTRANCE_PAVEMENT_SLAB = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT_SLAB;
    // END ENTRANCE PAVEMENT SHAPES

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_1;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_2;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_3;
    // END SCHOOL SPLIT RAISED EXTRAS

    // BEGIN SCHOOL RAISED INVERTED SLOPES
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_1;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_2;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_3;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_4;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_1;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_2;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_3;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_4;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_5 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_5;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_6 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_6;
    // END SCHOOL RAISED INVERTED SLOPES

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_1;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_2;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_3;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_SPLIT_4;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_1;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_2;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_3;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_4;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_5 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_5;
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_6 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_INVERTED_SLOPE_FLIGHT_6;
    // END SCHOOL INVERTED SLOPES

    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_SPLIT_1;
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_SPLIT_2;
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_SPLIT_3;
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_SPLIT_4;
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_1 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_1;
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_2 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_2;
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_3 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_3;
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_4 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_4;
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_5 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_5;
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_6 = net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.SCHOOL_RAILING_SLOPE_FLIGHT_6;
    // END SCHOOL RAILING SLOPES

    public static final DeferredBlock<SchoolWallRailingBlock> LIGHT_SCHOOL_WALL_RAILING = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.LIGHT_SCHOOL_WALL_RAILING;

    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_GLASS = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.SCHOOL_GLASS;

    public static final DeferredBlock<DoubleSchoolEntranceBlock> DOUBLE_SCHOOL_ENTRANCE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.DOUBLE_SCHOOL_ENTRANCE;

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_CLASSROOM_ENTRANCE = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.TALL_CLASSROOM_ENTRANCE;

    public static final DeferredBlock<FullBathroomDoorBlock> TALL_BATHROOM_DOOR_FULL_3 = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.TALL_BATHROOM_DOOR_FULL_3;
    public static final DeferredBlock<FullBathroomDoorBlock> TALL_BATHROOM_DOOR_FULL_4 = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.TALL_BATHROOM_DOOR_FULL_4;
    public static final DeferredBlock<SchoolTintedGlassBlock> SCHOOL_TINTED_GLASS = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.SCHOOL_TINTED_GLASS;

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_BATHROOM_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.TALL_BATHROOM_DOOR;

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_TEACHERS_OFFICE_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.TALL_TEACHERS_OFFICE_DOOR;

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_DINING_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.TALL_DINING_DOOR;

    public static final DeferredBlock<SportsGoalBlock> FOOTBALL_BASKETBALL_GOAL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleBlocks.FOOTBALL_BASKETBALL_GOAL;

    public static final DeferredBlock<ToiletBlock> TOILET = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.TOILET;

    public static final DeferredBlock<TallBathroomBlock> ADULT_MEN_URINAL = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.ADULT_MEN_URINAL;

    public static final DeferredBlock<WideBathroomBlock> MEN_URINAL = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.MEN_URINAL;

    public static final DeferredBlock<WideBathroomBlock> STANDALONE_TOILET = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.STANDALONE_TOILET;

    public static final DeferredBlock<TrashBinBlock> GREEN_TRASH_BIN = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.GREEN_TRASH_BIN;

    public static final DeferredBlock<TrashBinBlock> BLACK_TRASH_BIN = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.BLACK_TRASH_BIN;

    public static final DeferredBlock<ProjectorScreenBlock> PROJECTOR_SCREEN = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.PROJECTOR_SCREEN;

    public static final DeferredBlock<WallProjectorBlock> WALL_PROJECTOR = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.WALL_PROJECTOR;

    public static final DeferredBlock<WallDecorationBlock> CLASSROOM_TIMETABLE = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleBlocks.CLASSROOM_TIMETABLE;

    public static final DeferredBlock<WallDecorationBlock> FIRE_ALARM = net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleBlocks.FIRE_ALARM;

    public static final DeferredBlock<WallDecorationBlock> EMERGENCY_BACKPACK = net.nicomar2009.lsmmod.modules.backpacks.registry.ModuleBlocks.EMERGENCY_BACKPACK;

    public static final DeferredBlock<Block> SCHOOL_PARQUET = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_PARQUET;

    public static final DeferredBlock<BathroomFloorBlock> DARK_SCHOOL_TILED_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.DARK_SCHOOL_TILED_FLOOR;

    public static final DeferredBlock<BathroomFloorBlock> LIGHT_SCHOOL_TILED_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.LIGHT_SCHOOL_TILED_FLOOR;

    public static final DeferredBlock<Block> CLASSROOM_FLOOR = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.CLASSROOM_FLOOR;

    public static final DeferredBlock<SchoolYellowBorderWallBlock> SCHOOL_ENTRANCE_PAVEMENT = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_ENTRANCE_PAVEMENT;

    public static final DeferredBlock<Block> STREET_SCHOOL_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.STREET_SCHOOL_WALL;
    public static final DeferredBlock<StreetSchoolBarsBlock> STREET_SCHOOL_BARS = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.STREET_SCHOOL_BARS;

    public static final DeferredBlock<Block> LIGHT_SCHOOL_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.LIGHT_SCHOOL_WALL;

    public static final DeferredBlock<Block> DARK_SCHOOL_WALL = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.DARK_SCHOOL_WALL;

    public static final DeferredBlock<SchoolGateBlock> SCHOOL_GATE = net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_GATE;

    /** Block 1.1 */
    public static final DeferredBlock<ChairBlock> HIGH_SCHOOL_CHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.HIGH_SCHOOL_CHAIR;

    /** Block 1.2 */
    public static final DeferredBlock<ChairBlock> ELEMENTARY_CHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.ELEMENTARY_CHAIR;

    /** Block 1.3 */
    public static final DeferredBlock<DeskBlock> STUDENTS_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.STUDENTS_DESK;

    /** Block 1.4 */
    public static final DeferredBlock<LockerBlock> LOCKER = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlocks.LOCKER;

    /** Block 1.5 */
    public static final DeferredBlock<ChairBlock> TEACHERS_CHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.TEACHERS_CHAIR;

    public static final DeferredBlock<ChairBlock> ENGLISHROOMCHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.ENGLISHROOMCHAIR;

    public static final DeferredBlock<ChairBlock> HALLCHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.HALLCHAIR;

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_WHITE = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.PLASTICCHAIR_WHITE;

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_RED = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.PLASTICCHAIR_RED;

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_WHITE_ARMS = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.PLASTICCHAIR_WHITE_ARMS;

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_RED_ARMS = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.PLASTICCHAIR_RED_ARMS;

    public static final DeferredBlock<LaptopBlock> LAPTOP = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.LAPTOP;

    public static final DeferredBlock<LaptopBlock> MOISES_LAPTOP = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.MOISES_LAPTOP;

    public static final DeferredBlock<MonitorBlock> PC = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.PC;

    public static final DeferredBlock<ComputerDeskBlock> COMPUTER_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.COMPUTER_DESK;

    public static final DeferredBlock<GrayComputerDeskBlock> GRAY_COMPUTER_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.GRAY_COMPUTER_DESK;

    public static final DeferredBlock<SchoolBellBlock> SCHOOL_BELL = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.SCHOOL_BELL;

    public static final DeferredBlock<DiningTableBlock> DINING_TABLE = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.DINING_TABLE;

    public static final DeferredBlock<ChairBlock> DINING_CHAIR = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.DINING_CHAIR;

    public static final DeferredBlock<SofaBlock> SOFA = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.SOFA;

    public static final DeferredBlock<SpeakerBlock> SPEAKER = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.SPEAKER;

    public static final DeferredBlock<GeneralSpeakerBlock> GENERAL_SPEAKER = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.GENERAL_SPEAKER;

    public static final DeferredBlock<TallSpeakerBlock> TALL_SPEAKER = net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.TALL_SPEAKER;

    public static final DeferredBlock<DrinkingFountainBlock> DRINKING_FOUNTAIN = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.DRINKING_FOUNTAIN;

    public static final DeferredBlock<KioskTableBlock> KIOSK_TABLE = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.KIOSK_TABLE;

    public static final DeferredBlock<ArtTableBlock> ART_TABLE = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.ART_TABLE;

    public static final DeferredBlock<StoolBlock> STOOL = net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.STOOL;

    public static final DeferredBlock<VaultingBoxBlock> VAULTING_BOX = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleBlocks.VAULTING_BOX;

    public static final DeferredBlock<DoorBlock> CLASSROOM_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.CLASSROOM_DOOR;

    public static final DeferredBlock<DoorBlock> DINING_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.DINING_DOOR;

    public static final DeferredBlock<DoorBlock> TEACHERS_OFFICE_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.TEACHERS_OFFICE_DOOR;

    public static final DeferredBlock<DoorBlock> BATHROOM_DOOR = net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.BATHROOM_DOOR;

    public static final DeferredBlock<ManuelTiradoBustBlock> MANUEL_TIRADO_BUST = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleBlocks.MANUEL_TIRADO_BUST;

    public static final DeferredBlock<InfirmaryCotBlock> INFIRMARY_COT = net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.INFIRMARY_COT;

    private ModBlocks() {
    }

    /** Block 1.6 */
    public static final DeferredBlock<TeachersDeskBlock> TEACHERS_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.TEACHERS_DESK;

    /** Block 1.7 */
    public static final DeferredBlock<ElementaryDeskBlock> ELEMENTARY_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.ELEMENTARY_DESK;

    /** Block 1.8 */
    public static final DeferredBlock<SchoolSanMartinBlock> SAN_MARTIN_DE_PORRES = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleBlocks.SAN_MARTIN_DE_PORRES;

    public static final DeferredBlock<SchoolShieldBlock> SCHOOL_SHIELD = net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleBlocks.SCHOOL_SHIELD;

    public static final DeferredBlock<AwningSupportBlock> AWNING_SUPPORT = net.nicomar2009.lsmmod.modules.awnings.registry.ModuleBlocks.AWNING_SUPPORT;

    public static final DeferredBlock<AwningBlock> PLAYGROUND_AWNING = net.nicomar2009.lsmmod.modules.awnings.registry.ModuleBlocks.PLAYGROUND_AWNING;

    public static final DeferredBlock<AwningBlock> ELEMENTARY_PLAYGROUND_AWNING = net.nicomar2009.lsmmod.modules.awnings.registry.ModuleBlocks.ELEMENTARY_PLAYGROUND_AWNING;

    public static AwningBlock awningVariant(String id) {
        return switch (id) {
            case "playground_awning" -> PLAYGROUND_AWNING.get();
            case "elementary_playground_awning" -> ELEMENTARY_PLAYGROUND_AWNING.get();
            default -> PLAYGROUND_AWNING.get();
        };
    }

    public static net.minecraft.world.level.block.Block[] awningEntityBlocks() {
        return new net.minecraft.world.level.block.Block[]{AWNING_SUPPORT.get(), PLAYGROUND_AWNING.get(),
                ELEMENTARY_PLAYGROUND_AWNING.get()};
    }

    public static void register(IEventBus modEventBus) {
        // Resolve saved block palettes under the new names without duplicate blocks.
        BLOCKS.addAlias(net.minecraft.resources.Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "bathroom_floor"), DARK_SCHOOL_TILED_FLOOR.getId());
        BLOCKS.addAlias(net.minecraft.resources.Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "light_bathroom_floor"), LIGHT_SCHOOL_TILED_FLOOR.getId());
        BLOCKS.register(modEventBus);
    }
}

