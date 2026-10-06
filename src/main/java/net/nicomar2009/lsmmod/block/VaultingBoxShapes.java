package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Collision geometry generated from the four vaulting-box model cells. */
public final class VaultingBoxShapes {
    public static final VoxelShape[][][] SHAPES = new VoxelShape[2][2][4];
    static {
        SHAPES[0][0][0] = Shapes.or(
                Block.box(0.0, 0, 0, 16.0, 1, 16),
                Block.box(0.125, 1, 0, 15.875, 2, 16),
                Block.box(0.25, 2, 0, 15.75, 3, 16),
                Block.box(0.25, 3, 0, 15.75, 4, 16),
                Block.box(0.375, 4, 0, 15.625, 5, 16),
                Block.box(0.5, 5, 0, 15.5, 5.5, 16),
                Block.box(0.5948275862068966, 5.5, 0, 15.405172413793103, 6, 16),
                Block.box(0.625, 6, 0, 15.375, 7, 16),
                Block.box(0.75, 7, 0, 15.25, 8, 16),
                Block.box(0.875, 8, 0, 15.125, 9, 16),
                Block.box(0.875, 9, 0, 15.125, 10, 16),
                Block.box(1.0, 10, 0, 15.0, 11, 16),
                Block.box(1.125, 11, 0, 14.875, 11.5, 16),
                Block.box(1.2155172413793103, 11.5, 0, 14.78448275862069, 12, 16),
                Block.box(1.25, 12, 0, 14.75, 13, 16),
                Block.box(1.375, 13, 0, 14.625, 14, 16),
                Block.box(1.5, 14, 0, 14.5, 15, 16),
                Block.box(1.5, 15, 0, 14.5, 16, 16));
        SHAPES[0][1][0] = Shapes.or(
                Block.box(1.625, 0, 0, 14.375, 1, 16),
                Block.box(1.75, 1, 0, 14.25, 1.5, 16),
                Block.box(1.8362068965517242, 1.5, 0, 14.163793103448276, 2, 16),
                Block.box(1.875, 2, 0, 14.125, 3, 16),
                Block.box(2.0, 3, 0, 14.0, 4, 16),
                Block.box(2.125, 4, 0, 13.875, 5, 16),
                Block.box(2.125, 5, 0, 13.875, 6, 16),
                Block.box(2.25, 6, 0, 13.75, 7, 16),
                Block.box(2.375, 7, 0, 13.625, 7.5, 16),
                Block.box(2.456896551724138, 7.5, 0, 13.543103448275861, 8, 16),
                Block.box(2.5, 8, 0, 13.5, 9, 16),
                Block.box(2.625, 9, 0, 13.375, 10, 16),
                Block.box(2.75, 10, 0, 13.25, 11, 16),
                Block.box(2.75, 11, 0, 13.25, 12, 16),
                Block.box(2.875, 12, 0, 13.125, 13, 16),
                Block.box(2.5, 13, 0, 13.5, 15.5, 16),
                Block.box(3, 15.5, 0, 13, 16, 16));
        SHAPES[1][0][0] = Shapes.or(
                Block.box(0.0, 0, 0, 16.0, 1, 16),
                Block.box(0.125, 1, 0, 15.875, 2, 16),
                Block.box(0.25, 2, 0, 15.75, 3, 16),
                Block.box(0.25, 3, 0, 15.75, 4, 16),
                Block.box(0.375, 4, 0, 15.625, 5, 16),
                Block.box(0.5, 5, 0, 15.5, 5.5, 16),
                Block.box(0.5948275862068966, 5.5, 0, 15.405172413793103, 6, 16),
                Block.box(0.625, 6, 0, 15.375, 7, 16),
                Block.box(0.75, 7, 0, 15.25, 8, 16),
                Block.box(0.875, 8, 0, 15.125, 9, 16),
                Block.box(0.875, 9, 0, 15.125, 10, 16),
                Block.box(1.0, 10, 0, 15.0, 11, 16),
                Block.box(1.125, 11, 0, 14.875, 11.5, 16),
                Block.box(1.2155172413793103, 11.5, 0, 14.78448275862069, 12, 16),
                Block.box(1.25, 12, 0, 14.75, 13, 16),
                Block.box(1.375, 13, 0, 14.625, 14, 16),
                Block.box(1.5, 14, 0, 14.5, 15, 16),
                Block.box(1.5, 15, 0, 14.5, 16, 16));
        SHAPES[1][1][0] = Shapes.or(
                Block.box(1.625, 0, 0, 14.375, 1, 16),
                Block.box(1.75, 1, 0, 14.25, 1.5, 16),
                Block.box(1.8362068965517242, 1.5, 0, 14.163793103448276, 2, 16),
                Block.box(1.875, 2, 0, 14.125, 3, 16),
                Block.box(2.0, 3, 0, 14.0, 4, 16),
                Block.box(2.125, 4, 0, 13.875, 5, 16),
                Block.box(2.125, 5, 0, 13.875, 6, 16),
                Block.box(2.25, 6, 0, 13.75, 7, 16),
                Block.box(2.375, 7, 0, 13.625, 7.5, 16),
                Block.box(2.456896551724138, 7.5, 0, 13.543103448275861, 8, 16),
                Block.box(2.5, 8, 0, 13.5, 9, 16),
                Block.box(2.625, 9, 0, 13.375, 10, 16),
                Block.box(2.75, 10, 0, 13.25, 11, 16),
                Block.box(2.75, 11, 0, 13.25, 12, 16),
                Block.box(2.875, 12, 0, 13.125, 13, 16),
                Block.box(2.5, 13, 0, 13.5, 15.5, 16),
                Block.box(3, 15.5, 0, 13, 16, 16));
        for (int column = 0; column < 2; column++) {
            for (int row = 0; row < 2; row++) {
                for (int i = 1; i < 4; i++) {
                    VoxelShape[] rotated = {Shapes.empty()};
                    SHAPES[column][row][i - 1].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                            rotated[0] = Shapes.or(rotated[0], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
                    SHAPES[column][row][i] = rotated[0];
                }
            }
        }
    }
    private VaultingBoxShapes() {}
}
