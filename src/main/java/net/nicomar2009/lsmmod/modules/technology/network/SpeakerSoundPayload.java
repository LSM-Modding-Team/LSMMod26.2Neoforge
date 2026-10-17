package net.nicomar2009.lsmmod.modules.technology.network;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.nicomar2009.lsmmod.LSMMod;

/** Start, move or stop a particular physical speaker's current clip. */
public record SpeakerSoundPayload(UUID id, String dimension, int track, int remaining,
                                  boolean start, double x, double y, double z)
        implements CustomPacketPayload {
    public static final Type<SpeakerSoundPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "speaker_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SpeakerSoundPayload> CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeUUID(value.id());
                buf.writeUtf(value.dimension());
                buf.writeVarInt(value.track());
                buf.writeVarInt(value.remaining());
                buf.writeBoolean(value.start());
                buf.writeDouble(value.x());
                buf.writeDouble(value.y());
                buf.writeDouble(value.z());
            }, buf -> new SpeakerSoundPayload(buf.readUUID(), buf.readUtf(), buf.readVarInt(),
                    buf.readVarInt(), buf.readBoolean(), buf.readDouble(), buf.readDouble(), buf.readDouble()));

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
