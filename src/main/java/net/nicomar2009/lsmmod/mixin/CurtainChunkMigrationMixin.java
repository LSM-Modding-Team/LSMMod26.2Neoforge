package net.nicomar2009.lsmmod.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.nicomar2009.lsmmod.block.LegacyCurtainStates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Runs before the palette codec drops legacy properties. */
@Mixin(SerializableChunkData.class)
public abstract class CurtainChunkMigrationMixin {
    @ModifyVariable(method="parse",at=@At("HEAD"),argsOnly=true,ordinal=0)
    private static CompoundTag lsmmod$migrateChunk(CompoundTag tag) {
        return LegacyCurtainStates.migrateChunk(tag);
    }
}
