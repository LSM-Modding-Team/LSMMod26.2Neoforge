package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Round wooden stool with splayed legs and two levels of stretchers. */
public class StoolBlock extends ChairBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(5, 14, 2, 11, 16, 3),
            Block.box(3, 14, 3, 13, 16, 5),
            Block.box(2, 14, 5, 14, 16, 11),
            Block.box(3, 14, 11, 13, 16, 13),
            Block.box(5, 14, 13, 11, 16, 14),
            Block.box(3.5, 0, 3.5, 5.0, 4.5, 5.0),
            Block.box(4.0, 4.5, 4.0, 5.5, 9, 5.5),
            Block.box(4.5, 9, 4.5, 6.0, 14, 6.0),
            Block.box(3.5, 0, 11, 5.0, 4.5, 12.5),
            Block.box(4.0, 4.5, 10.5, 5.5, 9, 12.0),
            Block.box(4.5, 9, 10, 6.0, 14, 11.5),
            Block.box(11, 0, 3.5, 12.5, 4.5, 5.0),
            Block.box(10.5, 4.5, 4.0, 12.0, 9, 5.5),
            Block.box(10, 9, 4.5, 11.5, 14, 6.0),
            Block.box(11, 0, 11, 12.5, 4.5, 12.5),
            Block.box(10.5, 4.5, 10.5, 12.0, 9, 12.0),
            Block.box(10, 9, 10, 11.5, 14, 11.5),
            Block.box(4, 4, 4, 12, 5, 5),
            Block.box(4, 4, 11, 12, 5, 12),
            Block.box(4, 4, 5, 5, 5, 11),
            Block.box(11, 4, 5, 12, 5, 11),
            Block.box(4.5, 9, 4.5, 11.5, 10, 5.5),
            Block.box(4.5, 9, 10.5, 11.5, 10, 11.5),
            Block.box(4.5, 9, 5.5, 5.5, 10, 10.5),
            Block.box(10.5, 9, 5.5, 11.5, 10, 10.5));

    @Override
    protected double getSeatHeightPixels() {
        return 16.0D;
    }

    public StoolBlock(Properties properties) {
        super(properties, SHAPE);
    }
}
