package net.nicomar2009.lsmmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.nicomar2009.lsmmod.client.screen.NpcSummonScreen;
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

    @SubscribeEvent(receiveCanceled = true)
    public static void openSummonHelper(PlayerInteractEvent.RightClickItem event) {
        if (!event.getLevel().isClientSide() || !event.getItemStack().is(ModItems.NPC_SUMMON_HELPER.get())) return;
        Minecraft.getInstance().setScreenAndShow(new NpcSummonScreen());
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void openHelperAtBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide() || !event.getItemStack().is(ModItems.NPC_SUMMON_HELPER.get())) return;
        net.minecraft.client.Minecraft.getInstance().setScreenAndShow(new net.nicomar2009.lsmmod.client.screen.NpcSummonScreen());
        event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void openHelperAtGeneralEntity(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide() || !event.getItemStack().is(ModItems.NPC_SUMMON_HELPER.get())) return;
        net.minecraft.client.Minecraft.getInstance().setScreenAndShow(new net.nicomar2009.lsmmod.client.screen.NpcSummonScreen());
        event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void registerSkinReloadListener(AddClientReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "school_npc_skin_validation"),
                (ResourceManagerReloadListener) net.nicomar2009.lsmmod.client.renderer.SchoolNpcRenderer::reloadSkinValidation);
    }

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
    public static void registerNpcModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(net.nicomar2009.lsmmod.client.model.PlaceholderSchoolNpcModel.LAYER,
                net.nicomar2009.lsmmod.client.model.PlaceholderSchoolNpcModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.STUDENT.get(), net.nicomar2009.lsmmod.client.renderer.SchoolNpcRenderer::new);
        event.registerEntityRenderer(ModEntities.TEACHER.get(), net.nicomar2009.lsmmod.client.renderer.SchoolNpcRenderer::new);
        // Assign NoopRenderer to ensure the seat entity remains invisible
        event.registerEntityRenderer(ModEntities.SEAT.get(), NoopRenderer::new);
    }
}
