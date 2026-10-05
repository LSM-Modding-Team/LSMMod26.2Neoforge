package net.nicomar2009.lsmmod.registry;

import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.SchoolShieldBlock;
import net.nicomar2009.lsmmod.block.ChairBlock;
import net.nicomar2009.lsmmod.block.LaptopBlock;
import net.nicomar2009.lsmmod.block.MonitorBlock;
import net.nicomar2009.lsmmod.block.SchoolBellBlock;
import net.nicomar2009.lsmmod.block.ComputerDeskBlock;
import net.nicomar2009.lsmmod.block.GrayComputerDeskBlock;
import net.nicomar2009.lsmmod.block.NewChairShapes;
import net.nicomar2009.lsmmod.block.DeskBlock;
import net.nicomar2009.lsmmod.block.ElementaryDeskBlock;
import net.nicomar2009.lsmmod.block.LockerBlock;
import net.nicomar2009.lsmmod.block.TeachersDeskBlock;
import net.nicomar2009.lsmmod.block.TwoTallBlock;

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

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
