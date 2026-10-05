package net.nicomar2009.lsmmod.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.ChairBlock;
import net.nicomar2009.lsmmod.block.DeskBlock;
import net.nicomar2009.lsmmod.block.ElementaryDeskBlock;
import net.nicomar2009.lsmmod.block.LockerBlock;
import net.nicomar2009.lsmmod.block.TeachersDeskBlock;
import net.nicomar2009.lsmmod.block.TwoTallBlock;

/** Block registry: 3 chairs, 2 two-half desks, 1 single-block desk and 1 locker. */
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

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
