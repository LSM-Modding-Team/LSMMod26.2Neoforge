package net.nicomar2009.lsmmod.media;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public record SchoolMediaRoute(int kind, BlockPos output, BlockPos input) {
    public static final int PROJECTOR = 0, SCREEN = 1, SPEAKER = 2;
    public static final Codec<SchoolMediaRoute> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.intRange(0, 2).fieldOf("kind").forGetter(SchoolMediaRoute::kind),
            BlockPos.CODEC.fieldOf("output").forGetter(SchoolMediaRoute::output),
            BlockPos.CODEC.fieldOf("input").forGetter(SchoolMediaRoute::input)).apply(i, SchoolMediaRoute::new));
    public SchoolMediaRoute { output = output.immutable(); input = input.immutable(); }
}
