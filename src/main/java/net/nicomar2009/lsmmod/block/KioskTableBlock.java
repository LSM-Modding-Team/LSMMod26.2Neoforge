package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Two-block dining-style kiosk table, covered by a hanging tablecloth. */
public class KioskTableBlock extends DeskBlock {
    // Match the half-pixel bands of the inset, tapered X supports in the models.
    private static final VoxelShape LEFT_SHAPE = Shapes.or(
            Block.box(0, 14, 0, 16, 15.5, 16),
            Block.box(0, 15.5, 0, 16, 16, 16),
            Block.box(0, 8, 0, 0.5, 15.5, 16),
            Block.box(15.5, 8, 0, 16, 15.5, 16),
            Block.box(0.5, 8, 0, 15.5, 15.5, 0.5),
            Block.box(3.0, 0.0, 4, 4.5, 0.5, 5),
            Block.box(11.5, 0.0, 4, 13.0, 0.5, 5),
            Block.box(3.375, 0.5, 4, 4.875, 1.0, 5),
            Block.box(11.125, 0.5, 4, 12.625, 1.0, 5),
            Block.box(3.875, 1.0, 4, 5.25, 1.5, 5),
            Block.box(10.75, 1.0, 4, 12.125, 1.5, 5),
            Block.box(4.25, 1.5, 4, 5.625, 2.0, 5),
            Block.box(10.375, 1.5, 4, 11.75, 2.0, 5),
            Block.box(4.625, 2.0, 4, 6.0, 2.5, 5),
            Block.box(10.0, 2.0, 4, 11.375, 2.5, 5),
            Block.box(5.0, 2.5, 4, 6.25, 3.0, 5),
            Block.box(9.75, 2.5, 4, 11.0, 3.0, 5),
            Block.box(5.375, 3.0, 4, 6.625, 3.5, 5),
            Block.box(9.375, 3.0, 4, 10.625, 3.5, 5),
            Block.box(5.625, 3.5, 4, 6.875, 4.0, 5),
            Block.box(9.125, 3.5, 4, 10.375, 4.0, 5),
            Block.box(6.0, 4.0, 4, 7.25, 4.5, 5),
            Block.box(8.75, 4.0, 4, 10.0, 4.5, 5),
            Block.box(6.25, 4.5, 4, 7.5, 5.0, 5),
            Block.box(8.5, 4.5, 4, 9.75, 5.0, 5),
            Block.box(6.625, 5.0, 4, 7.75, 5.5, 5),
            Block.box(8.25, 5.0, 4, 9.375, 5.5, 5),
            Block.box(6.875, 5.5, 4, 8.0, 6.0, 5),
            Block.box(8.0, 5.5, 4, 9.125, 6.0, 5),
            Block.box(7.0, 6.0, 4, 8.125, 6.5, 5),
            Block.box(7.875, 6.0, 4, 9.0, 6.5, 5),
            Block.box(7.25, 6.5, 4, 8.25, 7.0, 5),
            Block.box(7.75, 6.5, 4, 8.75, 7.0, 5),
            Block.box(7.25, 7.0, 4, 8.25, 7.5, 5),
            Block.box(7.75, 7.0, 4, 8.75, 7.5, 5),
            Block.box(7.0, 7.5, 4, 8.125, 8.0, 5),
            Block.box(7.875, 7.5, 4, 9.0, 8.0, 5),
            Block.box(6.875, 8.0, 4, 8.0, 8.5, 5),
            Block.box(8.0, 8.0, 4, 9.125, 8.5, 5),
            Block.box(6.625, 8.5, 4, 7.75, 9.0, 5),
            Block.box(8.25, 8.5, 4, 9.375, 9.0, 5),
            Block.box(6.25, 9.0, 4, 7.5, 9.5, 5),
            Block.box(8.5, 9.0, 4, 9.75, 9.5, 5),
            Block.box(6.0, 9.5, 4, 7.25, 10.0, 5),
            Block.box(8.75, 9.5, 4, 10.0, 10.0, 5),
            Block.box(5.625, 10.0, 4, 6.875, 10.5, 5),
            Block.box(9.125, 10.0, 4, 10.375, 10.5, 5),
            Block.box(5.375, 10.5, 4, 6.625, 11.0, 5),
            Block.box(9.375, 10.5, 4, 10.625, 11.0, 5),
            Block.box(5.0, 11.0, 4, 6.25, 11.5, 5),
            Block.box(9.75, 11.0, 4, 11.0, 11.5, 5),
            Block.box(4.625, 11.5, 4, 6.0, 12.0, 5),
            Block.box(10.0, 11.5, 4, 11.375, 12.0, 5),
            Block.box(4.25, 12.0, 4, 5.625, 12.5, 5),
            Block.box(10.375, 12.0, 4, 11.75, 12.5, 5),
            Block.box(3.875, 12.5, 4, 5.25, 13.0, 5),
            Block.box(10.75, 12.5, 4, 12.125, 13.0, 5),
            Block.box(3.375, 13.0, 4, 4.875, 13.5, 5),
            Block.box(11.125, 13.0, 4, 12.625, 13.5, 5),
            Block.box(3.0, 13.5, 4, 4.5, 14.0, 5),
            Block.box(11.5, 13.5, 4, 13.0, 14.0, 5),
            Block.box(3.0, 12, 5, 4, 14, 16),
            Block.box(12, 12, 5, 13.0, 14, 16),
            Block.box(3.0, 13, 4, 13.0, 14, 5));

    public KioskTableBlock(Properties properties) {
        super(properties, LEFT_SHAPE);
    }

    @Override
    protected boolean hasStorage() {
        return false;
    }
}
