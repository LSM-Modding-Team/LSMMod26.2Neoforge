package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Generated from the same geometry as the desk models. */
final class ComputerDeskShapes {
    static final VoxelShape[][][] SHAPES = new VoxelShape[2][2][4];
    static {
        SHAPES[0][0][0] = ExactVoxelShapes.union(
                ExactVoxelShapes.box(1, 13, 0, 15, 16, 1),
                ExactVoxelShapes.box(1, 5, 1, 15, 8, 2),
                ExactVoxelShapes.box(1, 12.4, 2, 1.2, 13.4, 12),
                ExactVoxelShapes.box(14.8, 12.4, 2, 15, 13.4, 12),
                ExactVoxelShapes.box(0, 0, 1, 1, 16, 12),
                ExactVoxelShapes.box(15, 0, 1, 16, 16, 12),
                ExactVoxelShapes.box(1.25, 12.6, 2, 14.75, 13.2, 9));
        SHAPES[0][1][0] = ExactVoxelShapes.union(
                ExactVoxelShapes.box(1, 13, 0, 15, 16, 1),
                ExactVoxelShapes.box(1, 5, 1, 15, 8, 2),
                ExactVoxelShapes.box(1, 12.4, 2, 1.2, 13.4, 12),
                ExactVoxelShapes.box(14.8, 12.4, 2, 15, 13.4, 12),
                ExactVoxelShapes.box(0, 0, 1, 1, 16, 12),
                ExactVoxelShapes.box(15, 0, 1, 16, 16, 12),
                ExactVoxelShapes.box(1.25, 12.6, 7, 14.75, 13.2, 15.5),
                ExactVoxelShapes.box(2, 13.2, 10, 14, 13.85, 14.5));
        SHAPES[1][0][0] = ExactVoxelShapes.union(
                ExactVoxelShapes.box(1, 0, 0, 15, 12, 1),
                ExactVoxelShapes.box(0, 12, 0, 16, 13, 2),
                ExactVoxelShapes.box(1, 0, 1, 15, 1, 12),
                ExactVoxelShapes.box(0, 0, 1, 1, 8, 12),
                ExactVoxelShapes.box(0, 8, 1, 1, 12, 9),
                ExactVoxelShapes.box(0, 8, 9, 1, 10, 11),
                ExactVoxelShapes.box(15, 0, 1, 16, 8, 12),
                ExactVoxelShapes.box(15, 8, 1, 16, 12, 9),
                ExactVoxelShapes.box(15, 8, 9, 16, 10, 11));
        SHAPES[1][1][0] = ExactVoxelShapes.union(
                ExactVoxelShapes.box(1, 0, 0, 15, 12, 1),
                ExactVoxelShapes.box(0, 12, 0, 16, 13, 2),
                ExactVoxelShapes.box(1, 0, 1, 15, 1, 12),
                ExactVoxelShapes.box(0, 0, 1, 1, 8, 12),
                ExactVoxelShapes.box(0, 8, 1, 1, 12, 9),
                ExactVoxelShapes.box(0, 8, 9, 1, 10, 11),
                ExactVoxelShapes.box(15, 0, 1, 16, 8, 12),
                ExactVoxelShapes.box(15, 8, 1, 16, 12, 9),
                ExactVoxelShapes.box(15, 8, 9, 16, 10, 11),
                ExactVoxelShapes.box(10.5, 1, 3, 14.5, 3.3999999999999986, 8),
                ExactVoxelShapes.box(10.5, 1, 8, 14.5, 3.3999999999999986, 8.08),
                ExactVoxelShapes.box(6.8, 1, 9.1, 8.4, 1.25, 11.3),
                ExactVoxelShapes.box(7, 1.25, 9.3, 8.2, 1.6499999999999986, 11.1),
                ExactVoxelShapes.box(7.48, 1.6499999999999986, 9.65, 7.72, 1.75, 10.05),
                ExactVoxelShapes.box(3.48, 1.0, 4.1, 8.44, 1.620000000000001, 8.44),
                ExactVoxelShapes.box(5.185, 1.620000000000001, 5.34, 6.735, 4.100000000000001, 6.58),
                ExactVoxelShapes.box(1.62, 3.4800000000000004, 4.72, 10.3, 9.68, 6.27),
                ExactVoxelShapes.box(2.24, 4.100000000000001, 4.1, 9.68, 9.060000000000002, 4.72),
                ExactVoxelShapes.box(3.48, 4.719999999999999, 3.79, 8.44, 8.439999999999998, 4.1),
                ExactVoxelShapes.box(2.302, 4.533999999999999, 6.27, 9.618, 8.998000000000001, 6.301));
        for (int half = 0; half < 2; half++) {
            for (int occupied = 0; occupied < 2; occupied++) {
                for (int rotation = 1; rotation < 4; rotation++) {
                    SHAPES[half][occupied][rotation] = Shapes.rotate(SHAPES[half][occupied][rotation - 1], com.mojang.math.OctahedralGroup.BLOCK_ROT_Y_90);
                }
            }
        }
    }
    private ComputerDeskShapes() {}
}
