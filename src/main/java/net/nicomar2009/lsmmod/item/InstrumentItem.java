package net.nicomar2009.lsmmod.item;

import java.util.function.Supplier;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InstrumentItem extends Item {

    private final Supplier<SoundEvent> sound;
    private final float volume;
    private final float pitch;
    private final int cooldownTicks;

    public InstrumentItem(Properties properties, Supplier<SoundEvent> sound,
                          float volume, float pitch, int cooldownTicks) {
        super(properties);
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.cooldownTicks = cooldownTicks;
    }

    public InstrumentItem(Properties properties, Supplier<SoundEvent> sound) {
        this(properties, sound, 1.0F, 1.0F, 10);
    }

    /** Plays the same sound for a living test entity without pretending it is a Player. */
    public void playFor(net.minecraft.world.entity.LivingEntity user) {
        if (!user.level().isClientSide()) user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
                sound.get(), SoundSource.PLAYERS, volume, pitch);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }

        // Se reproduce en servidor y todos los clientes cercanos lo escuchan.
        // Con "null" como primer argumento, también lo oye quien lo toca.
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                sound.get(), SoundSource.PLAYERS, volume, pitch);

        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(stack, cooldownTicks);
        }

        return InteractionResult.SUCCESS;
    }
}