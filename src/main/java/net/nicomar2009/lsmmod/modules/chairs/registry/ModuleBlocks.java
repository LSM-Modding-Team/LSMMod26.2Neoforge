package net.nicomar2009.lsmmod.modules.chairs.registry;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.chairs.block.StoolBlock;
import net.nicomar2009.lsmmod.modules.chairs.block.SofaBlock;
import net.nicomar2009.lsmmod.modules.chairs.block.NewChairShapes;
import net.nicomar2009.lsmmod.modules.chairs.block.ChairBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<ChairBlock> HIGH_SCHOOL_CHAIR = BLOCKS.registerBlock(
            "high_school_chair",
            ChairBlock::new,
            props -> props
                    .strength(2.0F)
                    .noOcclusion());

    public static final DeferredBlock<ChairBlock> ELEMENTARY_CHAIR = BLOCKS.registerBlock(
            "elementary_chair",
            ChairBlock::new,
            props -> props
                    .strength(2.0F)
                    .noOcclusion());

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

    public static final DeferredBlock<ChairBlock> DINING_CHAIR = BLOCKS.registerBlock(
            "dining_chair",
            props -> new ChairBlock(props, NewChairShapes.DINING_CHAIR),
            props -> props.strength(2.0F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<SofaBlock> SOFA = BLOCKS.registerBlock(
            "sofa", SofaBlock::new,
            props -> props.strength(1.0F).sound(SoundType.WOOL).noOcclusion());

    public static final DeferredBlock<StoolBlock> STOOL = BLOCKS.registerBlock(
            "stool", StoolBlock::new,
            props -> props.strength(2.0F).sound(SoundType.WOOD).noOcclusion());
    private ModuleBlocks() {}
}
