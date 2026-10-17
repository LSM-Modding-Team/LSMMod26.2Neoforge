package net.nicomar2009.lsmmod.modules.windows_and_enclosures.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.LegacyCurtainStates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Structure and other direct NBT state readers. Chunk codecs have their own hook. */
@Mixin(NbtUtils.class)
public abstract class CurtainPaletteMigrationMixin {
    @ModifyVariable(method="readBlockState",at=@At("HEAD"),argsOnly=true,ordinal=0)
    private static CompoundTag lsmmod$migrateCurtain(CompoundTag tag) {
        return LegacyCurtainStates.migrateState(tag);
    }
}
