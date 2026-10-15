package net.nicomar2009.lsmmod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, LSMMod.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> STUDIOUS = EFFECTS.register(
            "studious", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x5C95D6) { });

    private ModEffects() { }
}
