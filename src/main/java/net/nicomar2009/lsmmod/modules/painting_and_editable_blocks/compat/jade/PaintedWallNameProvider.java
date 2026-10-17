package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
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
            case LIGHT -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.LIGHT_SCHOOL_WALL.get();
            case DARK -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.DARK_SCHOOL_WALL.get();
            case STREET -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.STREET_SCHOOL_WALL.get();
            case WHITE -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.CLASSROOM_WHITE_WALL.get();
            case FLOOR -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.CLASSROOM_FLOOR.get();
            case YELLOW -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.YELLOW_SCHOOL_BLOCK.get();
            case PLAYGROUND_WHITE -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.WHITE_PLAYGROUND_FLOOR.get();
            case PLAYGROUND_GRAY -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.GRAY_PLAYGROUND_FLOOR.get();
            case PLAYGROUND_GREEN -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.GREEN_PLAYGROUND_FLOOR.get();
            case PLAYGROUND_ORANGE -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.ORANGE_PLAYGROUND_FLOOR.get();
            case DARK_TILED -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.DARK_TILED_FLOOR.get();
            case RED_STREET -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.RED_STREET_WALL.get();
            case LABORATORY_TILE -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.SCHOOL_LABORATORY_TILE.get();
            case LABORATORY_TABLE_TILE -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.LABORATORY_TABLE_TILE.get();
            case AUDITORIUM_PARQUET -> net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.AUDITORIUM_PARQUET.get();
            case UNPAINTED -> net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.SCHOOL_PAINTABLE_BLOCK.get();
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
