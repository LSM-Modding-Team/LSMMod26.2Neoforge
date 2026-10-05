package net.nicomar2009.lsmmod.block;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
/** Generated from the gray desk model geometry. */
final class GrayComputerDeskShapes {
    static final VoxelShape[][][][] SHAPES = new VoxelShape[2][2][2][4];
    static {
        SHAPES[0][0][0][0] = Shapes.or(
                Block.box(1, 0, 1, 3, 16, 15),
                Block.box(3, 2, 1, 16, 6, 2),
                Block.box(3, 13, 4, 4, 16, 14),
                Block.box(3, 12, 4, 16, 13, 15));
        SHAPES[0][0][1][0] = Shapes.or(
                Block.box(1, 0, 1, 3, 16, 15),
                Block.box(3, 2, 1, 16, 6, 2),
                Block.box(3, 13, 4, 4, 16, 14),
                Block.box(3, 12, 5, 16, 13, 16),
                Block.box(5, 13, 9, 16, 13.7, 15));
        SHAPES[0][1][0][0] = Shapes.or(
                Block.box(0, 0, 0, 16, 2, 16));
        SHAPES[0][1][1][0] = Shapes.or(
                Block.box(0, 0, 0, 16, 2, 16),
                Block.box(9.4, 2.0, 4.25, 16, 2.8500000000000014, 10.2),
                Block.box(11.7375, 2.8500000000000014, 5.95, 13.8625, 6.25, 7.6499999999999995),
                Block.box(6.85, 5.399999999999999, 5.1, 16, 13.899999999999999, 7.225),
                Block.box(7.7, 6.25, 4.25, 16, 13.049999999999997, 5.1),
                Block.box(9.4, 7.100000000000001, 3.8249999999999997, 16, 12.2, 4.25),
                Block.box(7.785, 6.844999999999999, 7.225, 16, 12.965, 7.2675));
        SHAPES[1][0][0][0] = Shapes.or(
                Block.box(13, 0, 1, 15, 16, 15),
                Block.box(0, 2, 1, 13, 6, 2),
                Block.box(6, 13, 4, 7, 16, 14),
                Block.box(8, 2, 3, 13, 3, 14),
                Block.box(8, 3, 3, 9, 16, 4),
                Block.box(8, 3, 13, 9, 16, 14),
                Block.box(0, 12, 4, 7, 13, 15));
        SHAPES[1][0][1][0] = Shapes.or(
                Block.box(13, 0, 1, 15, 16, 15),
                Block.box(0, 2, 1, 13, 6, 2),
                Block.box(6, 13, 4, 7, 16, 14),
                Block.box(8, 2, 3, 13, 3, 14),
                Block.box(8, 3, 3, 9, 16, 4),
                Block.box(8, 3, 13, 9, 16, 14),
                Block.box(0, 12, 5, 7, 13, 16),
                Block.box(0, 13, 9, 5, 13.7, 15),
                Block.box(9, 3, 4, 12.5, 11, 12),
                Block.box(9, 3, 12, 12.5, 11, 12.08));
        SHAPES[1][1][0][0] = Shapes.or(
                Block.box(0, 0, 0, 16, 2, 16));
        SHAPES[1][1][1][0] = Shapes.or(
                Block.box(0, 0, 0, 16, 2, 16),
                Block.box(0, 2.0, 4.25, 0.1999999999999993, 2.8500000000000014, 10.2),
                Block.box(0, 5.399999999999999, 5.1, 2.75, 13.899999999999999, 7.225),
                Block.box(0, 6.25, 4.25, 1.8999999999999986, 13.049999999999997, 5.1),
                Block.box(0, 7.100000000000001, 3.8249999999999997, 0.1999999999999993, 12.2, 4.25),
                Block.box(0, 6.844999999999999, 7.225, 1.8149999999999977, 12.965, 7.2675));
        for (int c=0; c<2; c++) for (int h=0; h<2; h++) for (int p=0; p<2; p++) {
            for (int r=1; r<4; r++) {
                VoxelShape[] result = {Shapes.empty()};
                SHAPES[c][h][p][r-1].forAllBoxes((x1,y1,z1,x2,y2,z2) ->
                        result[0] = Shapes.or(result[0], Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
                SHAPES[c][h][p][r] = result[0];
            }
        }
    }
    private GrayComputerDeskShapes() {}
}
