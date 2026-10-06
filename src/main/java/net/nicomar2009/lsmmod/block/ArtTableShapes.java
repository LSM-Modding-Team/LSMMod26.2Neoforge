package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Collision geometry generated from the eight art-table model cells. */
public final class ArtTableShapes {
    public static final VoxelShape[][][] SHAPES = new VoxelShape[2][4][4];
    static {
        SHAPES[0][0][0] = Shapes.or(
                Block.box(0, 14, 0, 16, 16, 16),
                Block.box(3, 0, 3, 6, 14, 6),
                Block.box(3, 11, 3, 5, 14, 16),
                Block.box(5, 11, 3, 16, 14, 5));
        SHAPES[0][1][0] = Shapes.or(
                Block.box(0, 14, 0, 16, 16, 16),
                Block.box(3, 0, 14, 6, 14, 16),
                Block.box(3, 11, 0, 5, 14, 16),
                Block.box(5, 11, 14, 16, 14, 16));
        SHAPES[0][2][0] = Shapes.or(
                Block.box(0, 14, 0, 16, 16, 16),
                Block.box(3, 0, 0, 6, 14, 1),
                Block.box(3, 11, 0, 5, 14, 16));
        SHAPES[0][3][0] = Shapes.or(
                Block.box(0, 14, 0, 16, 16, 16),
                Block.box(3, 0, 10, 6, 14, 13),
                Block.box(3, 11, 0, 5, 14, 13),
                Block.box(5, 11, 11, 16, 14, 13));
        SHAPES[1][0][0] = Shapes.or(
                Block.box(0, 14, 0, 16, 16, 16),
                Block.box(10, 0, 3, 13, 14, 6),
                Block.box(11, 11, 3, 13, 14, 16),
                Block.box(0, 11, 3, 11, 14, 5));
        SHAPES[1][1][0] = Shapes.or(
                Block.box(0, 14, 0, 16, 16, 16),
                Block.box(10, 0, 14, 13, 14, 16),
                Block.box(11, 11, 0, 13, 14, 16),
                Block.box(0, 11, 14, 11, 14, 16));
        SHAPES[1][2][0] = Shapes.or(
                Block.box(0, 14, 0, 16, 16, 16),
                Block.box(10, 0, 0, 13, 14, 1),
                Block.box(11, 11, 0, 13, 14, 16));
        SHAPES[1][3][0] = Shapes.or(
                Block.box(0, 14, 0, 16, 16, 16),
                Block.box(10, 0, 10, 13, 14, 13),
                Block.box(11, 11, 0, 13, 14, 13),
                Block.box(0, 11, 11, 11, 14, 13));
        for (int column = 0; column < 2; column++) {
            for (int row = 0; row < 4; row++) {
                for (int i = 1; i < 4; i++) {
                    VoxelShape[] rotated = {Shapes.empty()};
                    SHAPES[column][row][i - 1].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                            rotated[0] = Shapes.or(rotated[0], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
                    SHAPES[column][row][i] = rotated[0];
                }
            }
        }
    }
    private ArtTableShapes() {}
}
