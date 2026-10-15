package net.nicomar2009.lsmmod.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.block.SchoolStudyInteraction;

/** The browser intercepts vanilla block use; study still requires a server-validated click. */
public record SchoolStudyPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<SchoolStudyPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "study_pc"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SchoolStudyPayload> CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, SchoolStudyPayload::pos, SchoolStudyPayload::new);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, CODEC, (request, context) ->
                context.enqueueWork(() -> SchoolStudyInteraction.use(context.player(), request.pos())));
    }
}
