package net.nicomar2009.lsmmod.block;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
/** Collision shapes matching the three vertical model cells. */
public final class ManuelTiradoBustShapes {
    public static final VoxelShape[][] SHAPES = new VoxelShape[3][4];
    static {
        SHAPES[0][0] = Shapes.or(
                Block.box(0, 0, 0, 16, 2, 16),
                Block.box(1, 2, 1, 15, 16, 15),
                Block.box(4, 8, 15, 12, 16, 15.125));
        SHAPES[1][0] = Shapes.or(
                Block.box(1, 0, 1, 15, 14, 15),
                Block.box(0.5, 14, 0.5, 15.5, 16, 15.5),
                Block.box(4, 0, 15, 12, 8, 15.125));
        SHAPES[2][0] = Shapes.or(
                Block.box(4, 0, 4.5, 12, 2, 11.5),
                Block.box(3.5, 2, 4, 12.5, 5.5, 11.5),
                Block.box(2, 5.5, 4, 14, 8, 11),
                Block.box(3, 8, 4.5, 13, 8.5, 10.5),
                Block.box(6.5, 7.5, 6, 9.5, 9.5, 10),
                Block.box(6, 9, 5.5, 10, 10, 10),
                Block.box(5.5, 10, 5, 10.5, 14.5, 10),
                Block.box(6, 14.5, 5.5, 10, 15.5, 9.5),
                Block.box(6.5, 15.5, 6, 9.5, 16, 9),
                Block.box(5, 10.5, 7, 5.5, 12.5, 9),
                Block.box(10.5, 10.5, 7, 11, 12.5, 9),
                Block.box(7.25, 11, 10, 8.75, 12.5, 11),
                Block.box(6.75, 9.75, 10, 9.25, 10.25, 10.25));
        for (int layer = 0; layer < 3; layer++) {
            for (int i = 1; i < 4; i++) {
                VoxelShape[] rotated = {Shapes.empty()};
                SHAPES[layer][i - 1].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                        rotated[0] = Shapes.or(rotated[0], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
                SHAPES[layer][i] = rotated[0];
            }
        }
    }
    private ManuelTiradoBustShapes() {}
}
