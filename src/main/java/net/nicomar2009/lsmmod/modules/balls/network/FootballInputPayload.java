package net.nicomar2009.lsmmod.modules.balls.network;



import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.balls.ball.FootballActions;

/** Inputs only: the server owns reach, possession, charge duration and shot physics. */
public record FootballInputPayload(int ballId, int action, int keys, float yaw, float pitch) implements CustomPacketPayload {
    public static final int START = 0, HOLD = 1, RELEASE = 2, CANCEL = 3, FOLLOW = 4;
    public static final Type<FootballInputPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "football_input"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FootballInputPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.ballId); buf.writeVarInt(payload.action); buf.writeVarInt(payload.keys);
                buf.writeFloat(payload.yaw); buf.writeFloat(payload.pitch);
            }, buf -> new FootballInputPayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readFloat(), buf.readFloat()));

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, CODEC, FootballInputPayload::handle);
    }
    private static void handle(FootballInputPayload input, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player) || !player.isAlive() || player.isSpectator()
                    || input.keys < 0 || input.keys > 15 || input.action < START || input.action > FOLLOW + 2
                    || !Float.isFinite(input.yaw) || !Float.isFinite(input.pitch) || Math.abs(input.pitch) > 90) return;
            // Keep the existing wire field, but select the ball on the server at release time.
            FootballActions.input(player, input.action, input.keys, input.yaw, input.pitch);
        });
    }
}
