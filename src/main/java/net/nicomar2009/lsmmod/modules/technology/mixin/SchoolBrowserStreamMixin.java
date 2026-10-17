package net.nicomar2009.lsmmod.modules.technology.mixin;

import java.util.concurrent.CompletableFuture;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.Identifier;
import net.nicomar2009.lsmmod.modules.technology.client.media.SchoolPcmStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Substitute only registered live streams; all resource-backed sounds retain the normal loader. */
@Mixin(SoundBufferLibrary.class)
public abstract class SchoolBrowserStreamMixin {
    @Inject(method = "getStream", at = @At("HEAD"), cancellable = true)
    private void lsmmod$browserStream(Identifier path, boolean looping,
                                      CallbackInfoReturnable<CompletableFuture<AudioStream>> callback) {
        SchoolPcmStream stream = SchoolPcmStream.find(path);
        if (stream != null) callback.setReturnValue(CompletableFuture.completedFuture(stream));
    }
}
