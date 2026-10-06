package net.nicomar2009.lsmmod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, LSMMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MY_DISC =
            SOUNDS.register("music_disc.lsm_anthem", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.lsm_anthem")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MY_MARCH =
            SOUNDS.register("music_disc.lsm_march", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.lsm_march")));
}