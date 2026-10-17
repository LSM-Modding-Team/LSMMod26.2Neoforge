package net.nicomar2009.lsmmod.modules.facilities.mixin;

import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.nicomar2009.lsmmod.modules.facilities.entity.CotRestEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class CotRestPoseMixin {
    @Inject(method = "getDesiredPose", at = @At("HEAD"), cancellable = true)
    private void lsmmod$restingPose(CallbackInfoReturnable<Pose> cir) {
        if (((Player) (Object) this).getVehicle() instanceof CotRestEntity) {
            cir.setReturnValue(Pose.SLEEPING);
        }
    }
}
