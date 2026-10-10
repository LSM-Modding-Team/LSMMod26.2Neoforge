package net.nicomar2009.lsmmod.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;

/** Registers the school furniture, statue and shield. */
public final class ModBlocks {
    // Specialized register: it sets the block's resource key (required since 1.21.2) automatically
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LSMMod.MOD_ID);

    // BEGIN SCHOOL RAILING SLOPES
    // BEGIN SCHOOL INVERTED SLOPES
    // BEGIN SCHOOL RAISED INVERTED SLOPES
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_1 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_1", props -> new SchoolInvertedSlopeBlock(0,0.666666666666667,0.5,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_2 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_2", props -> new SchoolInvertedSlopeBlock(0.333333333333333,0.666666666666667,0.5,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_3 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_3", props -> new SchoolInvertedSlopeBlock(0.666666666666667,0.666666666666667,0.5,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_4 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_4", props -> new SchoolInvertedSlopeBlock(0,0.666666666666667,0.5,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_5 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_5", props -> new SchoolInvertedSlopeBlock(0.333333333333333,0.666666666666667,0.5,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_6 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_6", props -> new SchoolInvertedSlopeBlock(0.666666666666666,0.666666666666667,0.5,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    // END SCHOOL RAISED INVERTED SLOPES

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_1 = BLOCKS.registerBlock(
            "school_inverted_slope_split_1", props -> new SchoolInvertedSlopeBlock(0,0.714285714285714,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_2 = BLOCKS.registerBlock(
            "school_inverted_slope_split_2", props -> new SchoolInvertedSlopeBlock(0.285714285714286,0.714285714285714,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_3 = BLOCKS.registerBlock(
            "school_inverted_slope_split_3", props -> new SchoolInvertedSlopeBlock(0.428571428571429,0.714285714285714,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_4 = BLOCKS.registerBlock(
            "school_inverted_slope_split_4", props -> new SchoolInvertedSlopeBlock(0.714285714285714,0.714285714285714,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_1 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_1", props -> new SchoolInvertedSlopeBlock(0,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_2 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_2", props -> new SchoolInvertedSlopeBlock(0.333333333333333,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_3 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_3", props -> new SchoolInvertedSlopeBlock(0.666666666666667,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_4 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_4", props -> new SchoolInvertedSlopeBlock(0,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_5 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_5", props -> new SchoolInvertedSlopeBlock(0.333333333333333,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_6 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_6", props -> new SchoolInvertedSlopeBlock(0.666666666666666,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    // END SCHOOL INVERTED SLOPES

    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_1 = BLOCKS.registerBlock(
            "school_railing_slope_split_1", props -> new SchoolRailingSlopeBlock(0.75,0.357142857142857,0.357142857142857,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_2 = BLOCKS.registerBlock(
            "school_railing_slope_split_2", props -> new SchoolRailingSlopeBlock(0.464285714285714,0.357142857142857,0.357142857142857,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_3 = BLOCKS.registerBlock(
            "school_railing_slope_split_3", props -> new SchoolRailingSlopeBlock(0.321428571428571,0.357142857142857,0.357142857142857,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_4 = BLOCKS.registerBlock(
            "school_railing_slope_split_4", props -> new SchoolRailingSlopeBlock(0.0357142857142856,0.357142857142857,0.357142857142857,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_1 = BLOCKS.registerBlock(
            "school_railing_slope_flight_1", props -> new SchoolRailingSlopeBlock(0.75,0.333333333333333,0.333333333333333,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_2 = BLOCKS.registerBlock(
            "school_railing_slope_flight_2", props -> new SchoolRailingSlopeBlock(0.416666666666667,0.333333333333333,0.333333333333333,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_3 = BLOCKS.registerBlock(
            "school_railing_slope_flight_3", props -> new SchoolRailingSlopeBlock(0.083333333333333,0.333333333333333,0.333333333333333,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_4 = BLOCKS.registerBlock(
            "school_railing_slope_flight_4", props -> new SchoolRailingSlopeBlock(0.75,0.333333333333333,0.333333333333333,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_5 = BLOCKS.registerBlock(
            "school_railing_slope_flight_5", props -> new SchoolRailingSlopeBlock(0.416666666666667,0.333333333333333,0.333333333333333,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_6 = BLOCKS.registerBlock(
            "school_railing_slope_flight_6", props -> new SchoolRailingSlopeBlock(0.0833333333333339,0.333333333333333,0.333333333333333,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    // END SCHOOL RAILING SLOPES

    public static final DeferredBlock<SchoolWallRailingBlock> LIGHT_SCHOOL_WALL_RAILING = BLOCKS.registerBlock(
            "light_school_wall_railing", SchoolWallRailingBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_GLASS = BLOCKS.registerBlock(
            "school_glass", props -> new SchoolGlassBlock(props, new double[][]{{0, 0, 0, 16, 16, 8}}),
            props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> SUPPORTED_SCHOOL_GLASS = BLOCKS.registerBlock(
            "supported_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> MIXED_SCHOOL_GLASS = BLOCKS.registerBlock(
            "mixed_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> SUPPORTED_SCHOOL_GLASS_REVERSED = BLOCKS.registerBlock(
            "supported_school_glass_reversed", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> MIXED_SCHOOL_GLASS_REVERSED = BLOCKS.registerBlock(
            "mixed_school_glass_reversed", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<DoubleSchoolEntranceBlock> DOUBLE_SCHOOL_ENTRANCE = BLOCKS.registerBlock(
            "double_school_entrance", DoubleSchoolEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_CLASSROOM_ENTRANCE = BLOCKS.registerBlock(
            "tall_classroom_entrance", TallClassroomEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_BATHROOM_DOOR = BLOCKS.registerBlock(
            "tall_bathroom_door", TallClassroomEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_TEACHERS_OFFICE_DOOR = BLOCKS.registerBlock(
            "tall_teachers_office_door", TallClassroomEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_DINING_DOOR = BLOCKS.registerBlock(
            "tall_dining_door", TallClassroomEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SportsGoalBlock> FOOTBALL_BASKETBALL_GOAL = BLOCKS.registerBlock(
            "football_basketball_goal", SportsGoalBlock::new,
            props -> props.strength(3.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<ToiletBlock> TOILET = BLOCKS.registerBlock(
            "toilet", ToiletBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallBathroomBlock> ADULT_MEN_URINAL = BLOCKS.registerBlock(
            "adult_men_urinal", props -> new TallBathroomBlock(props, TallBathroomShapes.ADULT_URINAL,
                    TallBathroomShapes.ADULT_URINAL, true),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallDecorationBlock> MEN_URINAL = BLOCKS.registerBlock(
            "men_urinal", props -> new WallDecorationBlock(props, BathroomShapes.URINAL),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TrashBinBlock> GREEN_TRASH_BIN = BLOCKS.registerBlock(
            "green_trash_bin", props -> new TrashBinBlock(props, TrashBinShapes.GREEN),
            props -> props.strength(0.8F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<TrashBinBlock> BLACK_TRASH_BIN = BLOCKS.registerBlock(
            "black_trash_bin", props -> new TrashBinBlock(props, TrashBinShapes.BLACK),
            props -> props.strength(0.8F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<ProjectorScreenBlock> PROJECTOR_SCREEN = BLOCKS.registerBlock(
            "projector_screen", ProjectorScreenBlock::new,
            props -> props.strength(0.5F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallProjectorBlock> WALL_PROJECTOR = BLOCKS.registerBlock(
            "wall_projector", WallProjectorBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallDecorationBlock> CLASSROOM_TIMETABLE = BLOCKS.registerBlock(
            "classroom_timetable", props -> new WallDecorationBlock(props, SchoolDecorationShapes.TIMETABLE),
            props -> props.strength(0.2F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallDecorationBlock> EMERGENCY_BACKPACK = BLOCKS.registerBlock(
            "emergency_backpack", props -> new WallDecorationBlock(props, SchoolDecorationShapes.BACKPACK),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<Block> CLASSROOM_FLOOR = BLOCKS.registerBlock(
            "classroom_floor", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> LIGHT_SCHOOL_WALL = BLOCKS.registerBlock(
            "light_school_wall", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> DARK_SCHOOL_WALL = BLOCKS.registerBlock(
            "dark_school_wall", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<SchoolGateBlock> SCHOOL_GATE = BLOCKS.registerBlock(
            "school_gate", SchoolGateBlock::new,
            props -> props.strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()
                    .pushReaction(PushReaction.BLOCK));

    /** Block 1.1 */
    public static final DeferredBlock<ChairBlock> HIGH_SCHOOL_CHAIR = BLOCKS.registerBlock(
            "high_school_chair",
            ChairBlock::new,
            props -> props
                    .strength(2.0F)
                    .noOcclusion());

    /** Block 1.2 */
    public static final DeferredBlock<ChairBlock> ELEMENTARY_CHAIR = BLOCKS.registerBlock(
            "elementary_chair",
            ChairBlock::new,
            props -> props
                    .strength(2.0F)
                    .noOcclusion());

    /** Block 1.3 */
    public static final DeferredBlock<DeskBlock> STUDENTS_DESK = BLOCKS.registerBlock(
            "students_desk",
            DeskBlock::new,
            props -> props
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    // The RIGHT half has no block entity, so pistons must never move just one half
                    .pushReaction(PushReaction.BLOCK));

    /** Block 1.4 */
    public static final DeferredBlock<LockerBlock> LOCKER = BLOCKS.registerBlock(
            "locker",
            LockerBlock::new,
            props -> props
                    .strength(3.0F)
                    .sound(SoundType.METAL)
                    // The model is not a full cube, so neighbouring faces must not be culled
                    .noOcclusion());

    /** Block 1.5 */
    public static final DeferredBlock<ChairBlock> TEACHERS_CHAIR = BLOCKS.registerBlock(
            "teachers_chair",
            ChairBlock::new,
            props -> props
                    .strength(2.0F)
                    .noOcclusion());

    public static final DeferredBlock<ChairBlock> ENGLISHROOMCHAIR = BLOCKS.registerBlock(
            "englishroomchair",
            props -> new ChairBlock(props, NewChairShapes.ENGLISHROOMCHAIR),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> HALLCHAIR = BLOCKS.registerBlock(
            "hallchair",
            props -> new ChairBlock(props, NewChairShapes.HALLCHAIR),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_WHITE = BLOCKS.registerBlock(
            "plasticchair_white",
            props -> new ChairBlock(props, NewChairShapes.PLASTICCHAIR),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_RED = BLOCKS.registerBlock(
            "plasticchair_red",
            props -> new ChairBlock(props, NewChairShapes.PLASTICCHAIR),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_WHITE_ARMS = BLOCKS.registerBlock(
            "plasticchair_white_arms",
            props -> new ChairBlock(props, NewChairShapes.PLASTICCHAIR_ARMS),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_RED_ARMS = BLOCKS.registerBlock(
            "plasticchair_red_arms",
            props -> new ChairBlock(props, NewChairShapes.PLASTICCHAIR_ARMS),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<LaptopBlock> LAPTOP = BLOCKS.registerBlock(
            "laptop", LaptopBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<LaptopBlock> MOISES_LAPTOP = BLOCKS.registerBlock(
            "moises_laptop", LaptopBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<MonitorBlock> PC = BLOCKS.registerBlock(
            "pc", MonitorBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<ComputerDeskBlock> COMPUTER_DESK = BLOCKS.registerBlock(
            "computer_desk", ComputerDeskBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<GrayComputerDeskBlock> GRAY_COMPUTER_DESK = BLOCKS.registerBlock(
            "gray_computer_desk", GrayComputerDeskBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolBellBlock> SCHOOL_BELL = BLOCKS.registerBlock(
            "school_bell", SchoolBellBlock::new,
            props -> props.strength(1.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<DiningTableBlock> DINING_TABLE = BLOCKS.registerBlock(
            "dining_table", DiningTableBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<ChairBlock> DINING_CHAIR = BLOCKS.registerBlock(
            "dining_chair",
            props -> new ChairBlock(props, NewChairShapes.DINING_CHAIR),
            props -> props.strength(2.0F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<SpeakerBlock> SPEAKER = BLOCKS.registerBlock(
            "speaker", SpeakerBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<TallSpeakerBlock> TALL_SPEAKER = BLOCKS.registerBlock(
            "tall_speaker", TallSpeakerBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<DrinkingFountainBlock> DRINKING_FOUNTAIN = BLOCKS.registerBlock(
            "drinking_fountain", DrinkingFountainBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<KioskTableBlock> KIOSK_TABLE = BLOCKS.registerBlock(
            "kiosk_table", KioskTableBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<ArtTableBlock> ART_TABLE = BLOCKS.registerBlock(
            "art_table", ArtTableBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<StoolBlock> STOOL = BLOCKS.registerBlock(
            "stool", StoolBlock::new,
            props -> props.strength(2.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<VaultingBoxBlock> VAULTING_BOX = BLOCKS.registerBlock(
            "vaulting_box", VaultingBoxBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<DoorBlock> CLASSROOM_DOOR = BLOCKS.registerBlock(
            "classroom_door", props -> new DoorBlock(BlockSetType.OAK, props),
            props -> props.strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<DoorBlock> DINING_DOOR = BLOCKS.registerBlock(
            "dining_door", props -> new DoorBlock(BlockSetType.OAK, props),
            props -> props.strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<DoorBlock> TEACHERS_OFFICE_DOOR = BLOCKS.registerBlock(
            "teachers_office_door", props -> new DoorBlock(BlockSetType.OAK, props),
            props -> props.strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<DoorBlock> BATHROOM_DOOR = BLOCKS.registerBlock(
            "bathroom_door", props -> new DoorBlock(BlockSetType.OAK, props),
            props -> props.strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<ManuelTiradoBustBlock> MANUEL_TIRADO_BUST = BLOCKS.registerBlock(
            "manuel_tirado_bust", ManuelTiradoBustBlock::new,
            props -> props.strength(4.0F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<InfirmaryCotBlock> INFIRMARY_COT = BLOCKS.registerBlock(
            "infirmary_cot", InfirmaryCotBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    private ModBlocks() {
    }

    /** Block 1.6 */
    public static final DeferredBlock<TeachersDeskBlock> TEACHERS_DESK = BLOCKS.registerBlock(
            "teachers_desk",
            TeachersDeskBlock::new,
            props -> props
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    // The RIGHT half has no block entity, so pistons must never move just one half
                    .pushReaction(PushReaction.BLOCK));

    /** Block 1.7 */
    public static final DeferredBlock<ElementaryDeskBlock> ELEMENTARY_DESK = BLOCKS.registerBlock(
            "elementary_desk",
            ElementaryDeskBlock::new,
            props -> props
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    // The model is not a full cube, so neighbouring faces must not be culled
                    .noOcclusion());

    /** Block 1.8 */
    public static final DeferredBlock<TwoTallBlock> SAN_MARTIN_DE_PORRES = BLOCKS.registerBlock(
            "san_martin_de_porres",
            TwoTallBlock::new,
            props -> props
                    .strength(4.0f)
                    .sound(SoundType.AMETHYST)
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolShieldBlock> SCHOOL_SHIELD = BLOCKS.registerBlock(
            "school_shield", SchoolShieldBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningSupportBlock> AWNING_SUPPORT = BLOCKS.registerBlock(
            "awning_support", AwningSupportBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> AWNING = BLOCKS.registerBlock(
            "awning", AwningBlock::new,
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> PLAYGROUND_AWNING = BLOCKS.registerBlock(
            "playground_awning", props -> new AwningBlock(props, "playground_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> ELEMENTARY_PLAYGROUND_AWNING = BLOCKS.registerBlock(
            "elementary_playground_awning", props -> new AwningBlock(props, "elementary_playground_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> WHITE_AWNING = BLOCKS.registerBlock(
            "white_awning", props -> new AwningBlock(props, "white_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> ORANGE_AWNING = BLOCKS.registerBlock(
            "orange_awning", props -> new AwningBlock(props, "orange_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> MAGENTA_AWNING = BLOCKS.registerBlock(
            "magenta_awning", props -> new AwningBlock(props, "magenta_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> LIGHT_BLUE_AWNING = BLOCKS.registerBlock(
            "light_blue_awning", props -> new AwningBlock(props, "light_blue_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> YELLOW_AWNING = BLOCKS.registerBlock(
            "yellow_awning", props -> new AwningBlock(props, "yellow_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> LIME_AWNING = BLOCKS.registerBlock(
            "lime_awning", props -> new AwningBlock(props, "lime_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> PINK_AWNING = BLOCKS.registerBlock(
            "pink_awning", props -> new AwningBlock(props, "pink_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> GRAY_AWNING = BLOCKS.registerBlock(
            "gray_awning", props -> new AwningBlock(props, "gray_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> LIGHT_GRAY_AWNING = BLOCKS.registerBlock(
            "light_gray_awning", props -> new AwningBlock(props, "light_gray_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> CYAN_AWNING = BLOCKS.registerBlock(
            "cyan_awning", props -> new AwningBlock(props, "cyan_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> PURPLE_AWNING = BLOCKS.registerBlock(
            "purple_awning", props -> new AwningBlock(props, "purple_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> BLUE_AWNING = BLOCKS.registerBlock(
            "blue_awning", props -> new AwningBlock(props, "blue_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> BROWN_AWNING = BLOCKS.registerBlock(
            "brown_awning", props -> new AwningBlock(props, "brown_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> GREEN_AWNING = BLOCKS.registerBlock(
            "green_awning", props -> new AwningBlock(props, "green_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> RED_AWNING = BLOCKS.registerBlock(
            "red_awning", props -> new AwningBlock(props, "red_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> BLACK_AWNING = BLOCKS.registerBlock(
            "black_awning", props -> new AwningBlock(props, "black_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static AwningBlock awningVariant(String id) {
        return switch (id) {
            case "playground_awning" -> PLAYGROUND_AWNING.get();
            case "elementary_playground_awning" -> ELEMENTARY_PLAYGROUND_AWNING.get();
            case "white_awning" -> WHITE_AWNING.get();
            case "orange_awning" -> ORANGE_AWNING.get();
            case "magenta_awning" -> MAGENTA_AWNING.get();
            case "light_blue_awning" -> LIGHT_BLUE_AWNING.get();
            case "yellow_awning" -> YELLOW_AWNING.get();
            case "lime_awning" -> LIME_AWNING.get();
            case "pink_awning" -> PINK_AWNING.get();
            case "gray_awning" -> GRAY_AWNING.get();
            case "light_gray_awning" -> LIGHT_GRAY_AWNING.get();
            case "cyan_awning" -> CYAN_AWNING.get();
            case "purple_awning" -> PURPLE_AWNING.get();
            case "blue_awning" -> BLUE_AWNING.get();
            case "brown_awning" -> BROWN_AWNING.get();
            case "green_awning" -> GREEN_AWNING.get();
            case "red_awning" -> RED_AWNING.get();
            case "black_awning" -> BLACK_AWNING.get();
            default -> AWNING.get();
        };
    }

    public static net.minecraft.world.level.block.Block[] awningEntityBlocks() {
        return new net.minecraft.world.level.block.Block[]{AWNING_SUPPORT.get(), AWNING.get(),
                PLAYGROUND_AWNING.get(),
                ELEMENTARY_PLAYGROUND_AWNING.get(),
                WHITE_AWNING.get(),
                ORANGE_AWNING.get(),
                MAGENTA_AWNING.get(),
                LIGHT_BLUE_AWNING.get(),
                YELLOW_AWNING.get(),
                LIME_AWNING.get(),
                PINK_AWNING.get(),
                GRAY_AWNING.get(),
                LIGHT_GRAY_AWNING.get(),
                CYAN_AWNING.get(),
                PURPLE_AWNING.get(),
                BLUE_AWNING.get(),
                BROWN_AWNING.get(),
                GREEN_AWNING.get(),
                RED_AWNING.get(),
                BLACK_AWNING.get()};
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}

