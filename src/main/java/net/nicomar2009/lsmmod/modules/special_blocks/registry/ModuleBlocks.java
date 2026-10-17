package net.nicomar2009.lsmmod.modules.special_blocks.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;
import net.nicomar2009.lsmmod.modules.special_blocks.block.SchoolShieldBlock;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.special_blocks.block.VaultingBoxBlock;
import net.nicomar2009.lsmmod.modules.special_blocks.block.SchoolTrophyGalleryBlock;
import net.nicomar2009.lsmmod.modules.special_blocks.block.SchoolSanMartinBlock;
import net.nicomar2009.lsmmod.modules.special_blocks.block.ManuelTiradoBustBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<SchoolTrophyGalleryBlock> SCHOOL_TROPHY_GALLERY = BLOCKS.registerBlock("school_trophy_gallery", SchoolTrophyGalleryBlock::new, props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<VaultingBoxBlock> VAULTING_BOX = BLOCKS.registerBlock(
            "vaulting_box", VaultingBoxBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<ManuelTiradoBustBlock> MANUEL_TIRADO_BUST = BLOCKS.registerBlock(
            "manuel_tirado_bust", ManuelTiradoBustBlock::new,
            props -> props.strength(4.0F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolSanMartinBlock> SAN_MARTIN_DE_PORRES = BLOCKS.registerBlock(
            "san_martin_de_porres",
            SchoolSanMartinBlock::new,
            props -> props
                    .strength(4.0f)
                    .sound(SoundType.AMETHYST)
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolShieldBlock> SCHOOL_SHIELD = BLOCKS.registerBlock(
            "school_shield", SchoolShieldBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlocks() {}
}
