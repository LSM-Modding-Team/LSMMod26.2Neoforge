package net.nicomar2009.lsmmod.block;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
/** Collision shapes matching the three vertical model cells. */
public final class ManuelTiradoBustShapes {
    public static final VoxelShape[][] SHAPES = new VoxelShape[3][4];
    static {
        SHAPES[0][0] = ExactVoxelShapes.union(
                ExactVoxelShapes.box(0, 0, 0, 16, 2, 16),
                ExactVoxelShapes.box(1, 2, 1, 15, 16, 15),
                ExactVoxelShapes.box(4, 8, 15, 12, 16, 15.125));
        SHAPES[1][0] = ExactVoxelShapes.union(
                ExactVoxelShapes.box(1, 0, 1, 15, 14, 15),
                ExactVoxelShapes.box(0.5, 14, 0.5, 15.5, 16, 15.5),
                ExactVoxelShapes.box(4, 0, 15, 12, 8, 15.125));
        SHAPES[2][0] = ExactVoxelShapes.union(
                ExactVoxelShapes.box(4, 0, 4.5, 12, 2, 11.5),
                ExactVoxelShapes.box(3.5, 2, 4, 12.5, 5.5, 11.5),
                ExactVoxelShapes.box(2, 5.5, 4, 14, 8, 11),
                ExactVoxelShapes.box(3, 8, 4.5, 13, 8.5, 10.5),
                ExactVoxelShapes.box(6.5, 7.5, 6, 9.5, 9.5, 10),
                ExactVoxelShapes.box(6, 9, 5.5, 10, 10, 10),
                ExactVoxelShapes.box(5.5, 10, 5, 10.5, 14.5, 10),
                ExactVoxelShapes.box(6, 14.5, 5.5, 10, 15.5, 9.5),
                ExactVoxelShapes.box(6.5, 15.5, 6, 9.5, 16, 9),
                ExactVoxelShapes.box(5, 10.5, 7, 5.5, 12.5, 9),
                ExactVoxelShapes.box(10.5, 10.5, 7, 11, 12.5, 9),
                ExactVoxelShapes.box(7.25, 11, 10, 8.75, 12.5, 11),
                ExactVoxelShapes.box(6.75, 9.75, 10, 9.25, 10.25, 10.25));
        for (int layer = 0; layer < 3; layer++) {
            for (int i = 1; i < 4; i++) {
                SHAPES[layer][i] = Shapes.rotate(SHAPES[layer][i - 1], com.mojang.math.OctahedralGroup.BLOCK_ROT_Y_90);
            }
        }
    }
    private ManuelTiradoBustShapes() {}
}
