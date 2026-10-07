package net.nicomar2009.lsmmod.client;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.nicomar2009.lsmmod.registry.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.ModEntities;

// The bus parameter no longer exists: NeoForge detects the correct bus from the event type
@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type,
                                               EquipmentClientInfo.Layer layer, Identifier defaultTexture) {
                // A complete resource path: reuse the same PNG that the item already loads.
                return Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "textures/item/uniform_pants.png");
            }
        }, ModItems.UNIFORM_PANTS.get());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.STUDENT.get(), net.nicomar2009.lsmmod.client.renderer.SchoolNpcRenderer::new);
        event.registerEntityRenderer(ModEntities.TEACHER.get(), net.nicomar2009.lsmmod.client.renderer.SchoolNpcRenderer::new);
        // Assign NoopRenderer to ensure the seat entity remains invisible
        event.registerEntityRenderer(ModEntities.SEAT.get(), NoopRenderer::new);
    }
}
