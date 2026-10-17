package net.nicomar2009.lsmmod.modules.lockers_and_storage.registry;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.LostAndFoundBoxBlock;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.NidoStorageBucketBlock;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.LockerBlock;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.DiningWallCabinetBlock;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.ChildrenLockerBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<DiningWallCabinetBlock> DINING_WALL_CABINET = BLOCKS.registerBlock("dining_wall_cabinet", DiningWallCabinetBlock::new, props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<NidoStorageBucketBlock> NIDO_STORAGE_BUCKET = BLOCKS.registerBlock("nido_storage_bucket", NidoStorageBucketBlock::new, props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<LostAndFoundBoxBlock> LOST_AND_FOUND_BOX = BLOCKS.registerBlock("lost_and_found_box", LostAndFoundBoxBlock::new, props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<ChildrenLockerBlock> CHILDREN_LOCKER = BLOCKS.registerBlock(
            "children_locker", ChildrenLockerBlock::new,
            props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<LockerBlock> LOCKER = BLOCKS.registerBlock(
            "locker",
            LockerBlock::new,
            props -> props
                    .strength(3.0F)
                    .sound(SoundType.METAL)
                    // The model is not a full cube, so neighbouring faces must not be culled
                    .noOcclusion());
    private ModuleBlocks() {}
}
