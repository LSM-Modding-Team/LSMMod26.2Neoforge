package net.nicomar2009.lsmmod.registry;

import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;

/** Registers the school furniture, statue and shield. */
public final class ModBlocks {
    // Specialized register: it sets the block's resource key (required since 1.21.2) automatically
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LSMMod.MOD_ID);

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
