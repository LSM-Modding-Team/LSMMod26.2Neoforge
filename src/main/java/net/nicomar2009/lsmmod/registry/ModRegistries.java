package net.nicomar2009.lsmmod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;

/** Shared registration containers; module initialization never depends on facade initialization. */
public final class ModRegistries {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LSMMod.MOD_ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LSMMod.MOD_ID);
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(LSMMod.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, LSMMod.MOD_ID);
    public static final DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LSMMod.MOD_ID);
    private ModRegistries() {}
}
