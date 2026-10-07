package net.nicomar2009.lsmmod.item;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.sounds.SoundEvents;

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
                          float volume, float pitch, int cooldownTicks,
                          float attackDamage, int durability) {
        super(properties.durability(durability)
                .component(DataComponents.WEAPON, new Weapon(1))
                .component(DataComponents.USE_COOLDOWN, new UseCooldown(cooldownTicks / 20.0F))
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage - 1.0,
                                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .build()));
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.cooldownTicks = cooldownTicks;
    }

    public InstrumentItem(Properties properties, Supplier<SoundEvent> sound,
                          float attackDamage, int durability, int cooldownTicks) {
        this(properties, sound, 1.0F, 1.0F, cooldownTicks, attackDamage, durability);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        return player.getCooldowns().isOnCooldown(stack);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            if (attacker instanceof Player player) {
                player.getCooldowns().addCooldown(stack, cooldownTicks);
            }
            attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.MACE_SMASH_GROUND, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
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
            return InteractionResult.FAIL;
        }

        // Broadcast once from the server, including to the musician.
        if (!level.isClientSide()) {
            playFor(player);
            if (cooldownTicks > 0) {
                player.getCooldowns().addCooldown(stack, cooldownTicks);
            }
        }

        return InteractionResult.SUCCESS;
    }
}