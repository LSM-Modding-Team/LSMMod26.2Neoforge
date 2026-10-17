package net.nicomar2009.lsmmod.modules.npcs.compat.jade;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableGlassStairBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableDoubleEntranceBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBathroomDoorBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBathroomColumnDoorBlock;

import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.compat.jade.PaintedWallNameProvider;
import net.nicomar2009.lsmmod.modules.npcs.compat.jade.SchoolNpcStatsProvider;

import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.npcs.entity.SchoolNpcEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/** Jade discovers this plugin only when installed; the main mod never references Jade classes. */
@WailaPlugin(LSMMod.MOD_ID)
public final class SchoolNpcJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(SchoolNpcStatsProvider.INSTANCE, SchoolNpcEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockIcon(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableProjectingWindowBlock.class);
        registration.registerBlockComponent(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableProjectingWindowBlock.class);
        registration.registerBlockIcon(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBathroomDoorBlock.class);
        registration.registerBlockComponent(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBathroomDoorBlock.class);
        registration.registerBlockIcon(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableGlassStairBlock.class);
        registration.registerBlockComponent(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableGlassStairBlock.class);
        registration.registerBlockIcon(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBathroomColumnDoorBlock.class);
        registration.registerBlockComponent(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBathroomColumnDoorBlock.class);
        registration.registerBlockIcon(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableDoubleEntranceBlock.class);
        registration.registerBlockComponent(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableDoubleEntranceBlock.class);
        registration.registerBlockIcon(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowBlock.class);
        registration.registerBlockComponent(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowBlock.class);
        registration.registerBlockIcon(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlock.class);
        registration.registerBlockComponent(PaintedWallNameProvider.INSTANCE, net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlock.class);
        registration.registerEntityComponent(SchoolNpcStatsProvider.Client.INSTANCE, SchoolNpcEntity.class);
    }
}
