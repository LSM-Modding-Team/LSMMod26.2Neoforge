package net.nicomar2009.lsmmod.client.media;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.nicomar2009.lsmmod.LSMMod;

/** An ordinary positional Minecraft streaming source, with live browser PCM instead of an OGG file. */
final class SchoolBrowserSound extends AbstractTickableSoundInstance {
    private static final AtomicLong NEXT_ID = new AtomicLong();
    private final SchoolPcmStream stream;
    SchoolBrowserSound(BlockPos pos, SchoolPcmStream stream, int radius) {
        super(SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,
                "browser_pcm/" + NEXT_ID.incrementAndGet())), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.stream = stream;
        x = pos.getX() + .5; y = pos.getY() + .5; z = pos.getZ() + .5;
        volume = pitch = 1; relative = false; looping = false;
        sound = new Sound(identifier, ConstantFloat.of(1), ConstantFloat.of(1), 1, Sound.Type.FILE, true, false, radius);
        SchoolPcmStream.register(sound.getPath(), stream);
    }
    @Override public WeighedSoundEvents resolve(SoundManager manager) {
        WeighedSoundEvents event = new WeighedSoundEvents(identifier, null);
        event.addSound(sound);
        return event;
    }
    @Override public void tick() { if (stream.closed()) stop(); }
    @Override public boolean canStartSilent() { return true; }
    void dispose() { SchoolPcmStream.unregister(sound.getPath(), stream); stop(); }
}
