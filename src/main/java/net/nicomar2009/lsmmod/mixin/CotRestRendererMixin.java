package net.nicomar2009.lsmmod.mixin;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Pose;
import net.nicomar2009.lsmmod.entity.CotRestEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class CotRestRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void lsmmod$lyingAwake(Avatar player, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        if (player.getVehicle() instanceof CotRestEntity rest) {
            state.pose = Pose.SLEEPING;
            // Cot facing points from the headrest to the feet; bed orientation points to the head.
            state.bedOrientation = rest.cotFacing().getOpposite();
            state.eyeHeight = player.getEyeHeight(Pose.STANDING);
            state.isPassenger = false;
            state.isCrouching = false;
            state.isFallFlying = false;
            state.isVisuallySwimming = false;
            state.swimAmount = 0.0F;
            state.yRot = 0.0F;
            state.xRot = 0.0F;
        }
    }
}
