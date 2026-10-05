package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Generated from the same geometry as the desk models. */
final class ComputerDeskShapes {
    static final VoxelShape[][][] SHAPES = new VoxelShape[2][2][4];
    static {
        SHAPES[0][0][0] = Shapes.or(
                Block.box(1, 13, 0, 15, 16, 1),
                Block.box(1, 5, 1, 15, 8, 2),
                Block.box(1, 12.4, 2, 1.2, 13.4, 12),
                Block.box(14.8, 12.4, 2, 15, 13.4, 12),
                Block.box(0, 0, 1, 1, 16, 12),
                Block.box(15, 0, 1, 16, 16, 12),
                Block.box(1.25, 12.6, 2, 14.75, 13.2, 9));
        SHAPES[0][1][0] = Shapes.or(
                Block.box(1, 13, 0, 15, 16, 1),
                Block.box(1, 5, 1, 15, 8, 2),
                Block.box(1, 12.4, 2, 1.2, 13.4, 12),
                Block.box(14.8, 12.4, 2, 15, 13.4, 12),
                Block.box(0, 0, 1, 1, 16, 12),
                Block.box(15, 0, 1, 16, 16, 12),
                Block.box(1.25, 12.6, 7, 14.75, 13.2, 15.5),
                Block.box(2, 13.2, 10, 14, 13.85, 14.5));
        SHAPES[1][0][0] = Shapes.or(
                Block.box(1, 0, 0, 15, 12, 1),
                Block.box(0, 12, 0, 16, 13, 2),
                Block.box(1, 0, 1, 15, 1, 12),
                Block.box(0, 0, 1, 1, 8, 12),
                Block.box(0, 8, 1, 1, 12, 9),
                Block.box(0, 8, 9, 1, 10, 11),
                Block.box(15, 0, 1, 16, 8, 12),
                Block.box(15, 8, 1, 16, 12, 9),
                Block.box(15, 8, 9, 16, 10, 11));
        SHAPES[1][1][0] = Shapes.or(
                Block.box(1, 0, 0, 15, 12, 1),
                Block.box(0, 12, 0, 16, 13, 2),
                Block.box(1, 0, 1, 15, 1, 12),
                Block.box(0, 0, 1, 1, 8, 12),
                Block.box(0, 8, 1, 1, 12, 9),
                Block.box(0, 8, 9, 1, 10, 11),
                Block.box(15, 0, 1, 16, 8, 12),
                Block.box(15, 8, 1, 16, 12, 9),
                Block.box(15, 8, 9, 16, 10, 11),
                Block.box(10.5, 1, 3, 14.5, 3.3999999999999986, 8),
                Block.box(10.5, 1, 8, 14.5, 3.3999999999999986, 8.08),
                Block.box(6.8, 1, 9.1, 8.4, 1.25, 11.3),
                Block.box(7, 1.25, 9.3, 8.2, 1.6499999999999986, 11.1),
                Block.box(7.48, 1.6499999999999986, 9.65, 7.72, 1.75, 10.05),
                Block.box(3.48, 1.0, 4.1, 8.44, 1.620000000000001, 8.44),
                Block.box(5.185, 1.620000000000001, 5.34, 6.735, 4.100000000000001, 6.58),
                Block.box(1.62, 3.4800000000000004, 4.72, 10.3, 9.68, 6.27),
                Block.box(2.24, 4.100000000000001, 4.1, 9.68, 9.060000000000002, 4.72),
                Block.box(3.48, 4.719999999999999, 3.79, 8.44, 8.439999999999998, 4.1),
                Block.box(2.302, 4.533999999999999, 6.27, 9.618, 8.998000000000001, 6.301));
        for (int half = 0; half < 2; half++) {
            for (int occupied = 0; occupied < 2; occupied++) {
                for (int rotation = 1; rotation < 4; rotation++) {
                    VoxelShape[] result = {Shapes.empty()};
                    SHAPES[half][occupied][rotation - 1].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                            result[0] = Shapes.or(result[0], Shapes.box(1-z2, y1, x1, 1-z1, y2, x2)));
                    SHAPES[half][occupied][rotation] = result[0];
                }
            }
        }
    }
    private ComputerDeskShapes() {}
}
