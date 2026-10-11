package net.nicomar2009.lsmmod.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.block.SchoolPaintableBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;

public enum PaintedWallNameProvider implements IBlockComponentProvider {
    INSTANCE;
    @Override public Identifier getUid() { return Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "painted_wall_name"); }
    @Override public int getDefaultPriority() { return TooltipPosition.HEAD; }
    @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof SchoolPaintableBlockEntity block)) return;
        Component name = Component.translatable("block.lsmmod." + block.paint(accessor.getSide()).blockName);
        if (!tooltip.replace(JadeIds.CORE_OBJECT_NAME, name)) tooltip.add(0, name, JadeIds.CORE_OBJECT_NAME);
    }
}
