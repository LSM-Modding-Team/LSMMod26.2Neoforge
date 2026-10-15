package net.nicomar2009.lsmmod.media;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public record SchoolMediaRoute(int kind, BlockPos output, BlockPos input, int volumePercent, int speedPercent) {
    public static final int PROJECTOR = 0, SCREEN = 1, SPEAKER = 2, AUDIO_CONTROLLER = 3;
    public static final Codec<SchoolMediaRoute> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.intRange(0, 3).fieldOf("kind").forGetter(SchoolMediaRoute::kind),
            BlockPos.CODEC.fieldOf("output").forGetter(SchoolMediaRoute::output),
            BlockPos.CODEC.fieldOf("input").forGetter(SchoolMediaRoute::input),
            Codec.intRange(0, 200).optionalFieldOf("volume_percent", 100).forGetter(SchoolMediaRoute::volumePercent),
            Codec.intRange(25, 200).optionalFieldOf("speed_percent", 100).forGetter(SchoolMediaRoute::speedPercent)).apply(i, SchoolMediaRoute::new));
    public SchoolMediaRoute(int kind, BlockPos output, BlockPos input) { this(kind, output, input, 100, 100); }
    public SchoolMediaRoute {
        if (!net.nicomar2009.lsmmod.rules.SchoolMediaRules.validAudioSettings(volumePercent, speedPercent))
            throw new IllegalArgumentException("Invalid audio settings");
        output = output.immutable(); input = input.immutable();
    }
}
