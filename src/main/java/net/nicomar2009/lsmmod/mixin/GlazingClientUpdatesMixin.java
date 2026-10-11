package net.nicomar2009.lsmmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.block.SchoolCurtains;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Equal-state row notifications still need a new world-dependent Fusion selection. */
@Mixin(ClientPacketListener.class)
public abstract class GlazingClientUpdatesMixin {
    @Unique private static void lsmmod$refresh(BlockPos pos,BlockState state) {
        ClientLevel level=Minecraft.getInstance().level;
        if(level!=null&&SchoolCurtains.canAttach(state))
            level.setSectionDirtyWithNeighbors(pos.getX()>>4,pos.getY()>>4,pos.getZ()>>4);
    }
    @Inject(method="handleBlockUpdate",at=@At("TAIL"))
    private void lsmmod$single(ClientboundBlockUpdatePacket packet,CallbackInfo ci) {
        lsmmod$refresh(packet.getPos(),packet.getBlockState());
    }
    @Inject(method="handleChunkBlocksUpdate",at=@At("TAIL"))
    private void lsmmod$batch(ClientboundSectionBlocksUpdatePacket packet,CallbackInfo ci) {
        packet.runUpdates(GlazingClientUpdatesMixin::lsmmod$refresh);
    }
}
