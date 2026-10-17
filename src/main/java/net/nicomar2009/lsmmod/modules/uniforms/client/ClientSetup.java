package net.nicomar2009.lsmmod.modules.uniforms.client;
import net.nicomar2009.lsmmod.modules.uniforms.registry.ModuleItems;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.nicomar2009.lsmmod.LSMMod;

@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public final class ClientSetup {
    @SubscribeEvent
    public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type,
                                               EquipmentClientInfo.Layer layer, Identifier defaultTexture) {
                // A complete resource path: reuse the same PNG that the item already loads.
                return Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "textures/item/uniform_pants.png");
            }
        }, ModuleItems.UNIFORM_PANTS.get());
    }
}
