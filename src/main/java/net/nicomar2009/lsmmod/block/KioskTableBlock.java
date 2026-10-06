package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Two-block dining-style kiosk table, covered by a hanging tablecloth. */
public class KioskTableBlock extends DeskBlock {
    private static final VoxelShape LEFT_SHAPE = Shapes.or(
            Block.box(0, 14, 0, 16, 15.5, 16),
            Block.box(0, 15.5, 0, 16, 16, 16),
            Block.box(0, 8, 0, 0.5, 15.5, 16),
            Block.box(15.5, 8, 0, 16, 15.5, 16),
            Block.box(0.5, 8, 0, 15.5, 15.5, 0.5),
            Block.box(2, 0, 2, 4, 14, 4),
            Block.box(12, 0, 2, 14, 14, 4),
            Block.box(2, 12, 4, 4, 14, 16),
            Block.box(12, 12, 4, 14, 14, 16),
            Block.box(4, 12, 2, 12, 14, 4));

    public KioskTableBlock(Properties properties) {
        super(properties, LEFT_SHAPE);
    }

    @Override
    protected boolean hasStorage() {
        return false;
    }
}
