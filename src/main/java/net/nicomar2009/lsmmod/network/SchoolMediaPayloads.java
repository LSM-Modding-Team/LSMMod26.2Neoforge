package net.nicomar2009.lsmmod.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.media.SchoolMediaLinks;
import net.nicomar2009.lsmmod.media.SchoolMediaRoute;
import net.nicomar2009.lsmmod.media.SchoolMediaServer;

/** Only wiring crosses the server connection; Chromium credentials/frames/audio stay local. */
public final class SchoolMediaPayloads {
    private SchoolMediaPayloads() { }
    public record Request(BlockPos pos, int action) implements CustomPacketPayload {
        public static final Type<Request> TYPE = new Type<>(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "media_link"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Request::pos, ByteBufCodecs.VAR_INT, Request::action, Request::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public record AudioSettings(BlockPos pos, int volumePercent, int speedPercent) implements CustomPacketPayload {
        public static final Type<AudioSettings> TYPE = new Type<>(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "media_audio_settings"));
        public static final StreamCodec<RegistryFriendlyByteBuf, AudioSettings> CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, AudioSettings::pos, ByteBufCodecs.VAR_INT, AudioSettings::volumePercent,
                ByteBufCodecs.VAR_INT, AudioSettings::speedPercent, AudioSettings::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public record Snapshot(Identifier dimension, List<SchoolMediaRoute> routes) implements CustomPacketPayload {
        public static final Type<Snapshot> TYPE = new Type<>(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "media_links"));
        public Snapshot { routes = List.copyOf(routes); }
        public static final StreamCodec<RegistryFriendlyByteBuf, Snapshot> CODEC = StreamCodec.of((buf, value) -> {
            Identifier.STREAM_CODEC.encode(buf, value.dimension());
            buf.writeVarInt(value.routes().size());
            for (SchoolMediaRoute route : value.routes()) {
                buf.writeVarInt(route.kind()); buf.writeBlockPos(route.output()); buf.writeBlockPos(route.input());
                buf.writeVarInt(route.volumePercent()); buf.writeVarInt(route.speedPercent());
            }
        }, buf -> {
            Identifier dimension = Identifier.STREAM_CODEC.decode(buf);
            int count = buf.readVarInt();
            if (count < 0 || count > SchoolMediaLinks.MAX_ROUTES) throw new IllegalArgumentException("Too many media links");
            List<SchoolMediaRoute> routes = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                int kind = buf.readVarInt();
                if (kind < 0 || kind > 3) throw new IllegalArgumentException("Invalid media link");
                routes.add(new SchoolMediaRoute(kind, buf.readBlockPos(), buf.readBlockPos(), buf.readVarInt(), buf.readVarInt()));
            }
            return new Snapshot(dimension, routes);
        });
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("2");
        registrar.playToServer(AudioSettings.TYPE, AudioSettings.CODEC, (request, context) ->
                context.enqueueWork(() -> SchoolMediaServer.settings(request, context.player())));
        registrar.playToServer(Request.TYPE, Request.CODEC, (request, context) ->
                context.enqueueWork(() -> SchoolMediaServer.handle(request, context.player())));
        registrar.playToClient(Snapshot.TYPE, Snapshot.CODEC, (snapshot, context) ->
                context.enqueueWork(() -> net.nicomar2009.lsmmod.client.media.SchoolMediaClient.accept(snapshot)));
    }
}
