package net.nicomar2009.lsmmod.modules.balls.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.SOUNDS;

public final class ModuleSounds {
    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_KICK = footballSound("kick");

    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_WALL = footballSound("wall");

    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_NET = footballSound("net");

    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_FRAME = footballSound("frame");

    private static DeferredHolder<SoundEvent, SoundEvent> footballSound(String name) {
        String id = "football." + name;
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, id)));
    }

    private ModuleSounds() {}
}
