package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.nicomar2009.lsmmod.registry.ModBlocks;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlockEntity;
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
    @Override public Element getIcon(BlockAccessor accessor, IPluginConfig config, Element currentIcon) {
        if (!(accessor.getBlockEntity() instanceof SchoolPaintableBlockEntity block)) return currentIcon;
        if (!block.canPaintAt(accessor.getSide(),accessor.getHitResult().getLocation())) return currentIcon;
        var color = block.paintAt(accessor.getSide(), accessor.getHitResult().getLocation());
        var paintedBlock = switch (color) {
            case LIGHT -> ModBlocks.LIGHT_SCHOOL_WALL.get();
            case DARK -> ModBlocks.DARK_SCHOOL_WALL.get();
            case STREET -> ModBlocks.STREET_SCHOOL_WALL.get();
            case WHITE -> ModBlocks.CLASSROOM_WHITE_WALL.get();
            case FLOOR -> ModBlocks.CLASSROOM_FLOOR.get();
            case YELLOW -> ModBlocks.YELLOW_SCHOOL_BLOCK.get();
            case PLAYGROUND_WHITE -> ModBlocks.WHITE_PLAYGROUND_FLOOR.get();
            case PLAYGROUND_GRAY -> ModBlocks.GRAY_PLAYGROUND_FLOOR.get();
            case PLAYGROUND_GREEN -> ModBlocks.GREEN_PLAYGROUND_FLOOR.get();
            case PLAYGROUND_ORANGE -> ModBlocks.ORANGE_PLAYGROUND_FLOOR.get();
            case DARK_TILED -> ModBlocks.DARK_TILED_FLOOR.get();
            case RED_STREET -> ModBlocks.RED_STREET_WALL.get();
            case LABORATORY_TILE -> ModBlocks.SCHOOL_LABORATORY_TILE.get();
            case LABORATORY_TABLE_TILE -> ModBlocks.LABORATORY_TABLE_TILE.get();
            case AUDITORIUM_PARQUET -> ModBlocks.AUDITORIUM_PARQUET.get();
            case UNPAINTED -> ModBlocks.SCHOOL_PAINTABLE_BLOCK.get();
        };
        return JadeUI.item(new ItemStack(paintedBlock));
    }
    @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof SchoolPaintableBlockEntity block)) return;
        if (!block.canPaintAt(accessor.getSide(),accessor.getHitResult().getLocation())) return;
        Component name = Component.translatable("block.lsmmod." + block.paintAt(accessor.getSide(), accessor.getHitResult().getLocation()).blockName);
        if (!tooltip.replace(JadeIds.CORE_OBJECT_NAME, name)) tooltip.add(0, name, JadeIds.CORE_OBJECT_NAME);
    }
}
