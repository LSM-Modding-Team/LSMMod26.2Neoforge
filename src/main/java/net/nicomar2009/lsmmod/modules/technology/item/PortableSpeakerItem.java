package net.nicomar2009.lsmmod.modules.technology.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.nicomar2009.lsmmod.modules.technology.event.PortableSpeakerPlayback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.nicomar2009.lsmmod.registry.ModSounds;

/** Plays one clip per use and saves each speaker's shuffled playlist on its stack. */
public final class PortableSpeakerItem extends Item {
    public PortableSpeakerItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) return InteractionResult.FAIL;
        if (!level.isClientSide()) {
            UUID sourceId = PortableSpeakerPlayback.id(stack);
            if (PortableSpeakerPlayback.isPlaying(sourceId)) return InteractionResult.FAIL;
            CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            CompoundTag playlist = data.getCompound("PortableSpeakerPlaylist").orElseGet(CompoundTag::new);
            SpeakerPlaylist.Selection selection = SpeakerPlaylist.next(
                    ModSounds.PORTABLE_SPEAKER_TRACKS.size(),
                    playlist.getIntArray("Order").orElseGet(() -> new int[0]),
                    playlist.getIntOr("Index", 0), playlist.getIntOr("Last", -1),
                    player.getRandom()::nextInt);
            ModSounds.SpeakerTrack track = ModSounds.PORTABLE_SPEAKER_TRACKS.get(selection.track());
            playlist.putIntArray("Order", selection.order());
            playlist.putInt("Index", selection.nextIndex());
            playlist.putInt("Last", selection.track());
            data.put("PortableSpeakerPlaylist", playlist);
            data.putString(PortableSpeakerPlayback.ID_KEY, sourceId.toString());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
            PortableSpeakerPlayback.start((ServerPlayer) player, sourceId, selection.track(), track.durationTicks());
            player.getCooldowns().addCooldown(stack, track.durationTicks());
        }
        return InteractionResult.SUCCESS;
    }
}
