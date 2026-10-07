package net.nicomar2009.lsmmod.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.nicomar2009.lsmmod.registry.ModSounds;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/** A school ruler with vanilla sword material behavior, reduced damage and extra entity reach. */
public class RulerItem extends Item {
    private static final Identifier REACH_ID = Identifier.fromNamespaceAndPath("lsmmod", "ruler_reach");
    public RulerItem(Properties properties, ToolMaterial material) {
        super(properties.sword(material, 2.5F, -2.4F).attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 2.5 + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(REACH_ID, 1.5, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build()));
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.RULER_HIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
