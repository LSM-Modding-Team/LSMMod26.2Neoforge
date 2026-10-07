package net.nicomar2009.lsmmod.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

/** Leather-strength school clothing, equippable only by players. */
public class SchoolUniformItem extends Item {
    public SchoolUniformItem(Properties properties, ArmorType type, String asset) {
        super(properties.humanoidArmor(ArmorMaterials.LEATHER, type)
                .component(DataComponents.EQUIPPABLE, Equippable.builder(type.getSlot())
                        .setEquipSound(ArmorMaterials.LEATHER.equipSound())
                        .setAsset(ResourceKey.create(EquipmentAssets.ROOT_ID,
                                Identifier.fromNamespaceAndPath("lsmmod", asset)))
                        .setAllowedEntities(EntityTypes.PLAYER)
                        .setDispensable(false)
                        .build()));
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) {
        return entity instanceof Player && entity.isEquippableInSlot(stack, slot);
    }
}
