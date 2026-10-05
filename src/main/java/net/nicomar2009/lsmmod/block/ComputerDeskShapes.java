package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Generated from the same geometry as the desk models. */
final class ComputerDeskShapes {
    static final VoxelShape[][][] SHAPES = new VoxelShape[2][2][4];
    static {
        SHAPES[0][0][0] = Shapes.or(
                Block.box(0, 12, 1, 16, 13, 16),
                Block.box(0, 13, 0, 16, 16, 1),
                Block.box(0, 0, 0, 1, 16, 1),
                Block.box(15, 0, 0, 16, 16, 1),
                Block.box(0, 0, 14, 1, 12, 16),
                Block.box(15, 0, 14, 16, 12, 16),
                Block.box(0, 2, 0, 1, 3, 16),
                Block.box(15, 2, 0, 16, 3, 16),
                Block.box(1, 9, 0, 15, 11, 1));
        SHAPES[0][1][0] = Shapes.or(
                Block.box(0, 12, 1, 16, 13, 16),
                Block.box(0, 13, 0, 16, 16, 1),
                Block.box(0, 0, 0, 1, 16, 1),
                Block.box(15, 0, 0, 16, 16, 1),
                Block.box(0, 0, 14, 1, 12, 16),
                Block.box(15, 0, 14, 16, 12, 16),
                Block.box(0, 2, 0, 1, 3, 16),
                Block.box(15, 2, 0, 16, 3, 16),
                Block.box(1, 9, 0, 15, 11, 1),
                Block.box(2, 13, 10.2, 14, 13.65, 14.5));
        SHAPES[1][0][0] = Shapes.or(
                Block.box(0, 0, 0, 16, 1, 10),
                Block.box(0, 0, 0, 16, 15, 1),
                Block.box(0, 13, 1, 16, 15, 10),
                Block.box(0, 0, 0, 1, 13, 1),
                Block.box(15, 0, 0, 16, 13, 1));
        SHAPES[1][1][0] = Shapes.or(
                Block.box(0, 0, 0, 16, 1, 10),
                Block.box(0, 0, 0, 16, 15, 1),
                Block.box(0, 13, 1, 16, 15, 10),
                Block.box(0, 0, 0, 1, 13, 1),
                Block.box(15, 0, 0, 16, 13, 1),
                Block.box(10, 1, 3, 15, 3.3999999999999986, 8),
                Block.box(10, 1, 8, 15, 3.3999999999999986, 8.08),
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
