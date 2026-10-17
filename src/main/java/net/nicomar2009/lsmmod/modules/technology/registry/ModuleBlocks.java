package net.nicomar2009.lsmmod.modules.technology.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.technology.block.GeneralSpeakerBlock;
import net.nicomar2009.lsmmod.modules.technology.block.WallProjectorBlock;
import net.nicomar2009.lsmmod.modules.technology.block.TallSpeakerBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SpeakerBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SchoolRouterBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SchoolCabinetBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SchoolBellBlock;
import net.nicomar2009.lsmmod.modules.technology.block.ProjectorScreenBlock;
import net.nicomar2009.lsmmod.modules.technology.block.MonitorBlock;
import net.nicomar2009.lsmmod.modules.technology.block.LaptopBlock;
import net.nicomar2009.lsmmod.modules.technology.block.CeilingProjectorBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<CeilingProjectorBlock> CEILING_PROJECTOR = BLOCKS.registerBlock(
            "ceiling_projector", CeilingProjectorBlock::new,
            props -> props.strength(1.0F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<SchoolCabinetBlock> SCHOOL_CABINET = BLOCKS.registerBlock(
            "school_cabinet", SchoolCabinetBlock::new,
            props -> props.strength(2.0F).sound(SoundType.WOOD).pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolRouterBlock> SCHOOL_ROUTER = BLOCKS.registerBlock(
            "school_router", SchoolRouterBlock::new,
            props -> props.strength(0.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<ProjectorScreenBlock> PROJECTOR_SCREEN = BLOCKS.registerBlock(
            "projector_screen", ProjectorScreenBlock::new,
            props -> props.strength(0.5F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallProjectorBlock> WALL_PROJECTOR = BLOCKS.registerBlock(
            "wall_projector", WallProjectorBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<LaptopBlock> LAPTOP = BLOCKS.registerBlock(
            "laptop", LaptopBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<LaptopBlock> MOISES_LAPTOP = BLOCKS.registerBlock(
            "moises_laptop", LaptopBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<MonitorBlock> PC = BLOCKS.registerBlock(
            "pc", MonitorBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<SchoolBellBlock> SCHOOL_BELL = BLOCKS.registerBlock(
            "school_bell", SchoolBellBlock::new,
            props -> props.strength(1.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<SpeakerBlock> SPEAKER = BLOCKS.registerBlock(
            "speaker", SpeakerBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<GeneralSpeakerBlock> GENERAL_SPEAKER = BLOCKS.registerBlock(
            "general_speaker", GeneralSpeakerBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallSpeakerBlock> TALL_SPEAKER = BLOCKS.registerBlock(
            "tall_speaker", TallSpeakerBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlocks() {}
}
