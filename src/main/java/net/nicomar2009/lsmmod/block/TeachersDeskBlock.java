package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Teacher's desk: same behaviour as the student's desk (two halves, one shared inventory),
 * but with its own hitbox derived from teachersdeskleft.json / teachersdeskright.json.
 */
public class TeachersDeskBlock extends DeskBlock {
    // Boxes in pixels (0-16) for the native orientation (FACING = SOUTH, y rotation 0), for the LEFT half,
    // which the blockstate renders with the teachersdeskright model (after each element's 180-degree rotation).
    private static final VoxelShape LEFT_NATIVE = Shapes.or(
            Block.box(2, 0, 2, 4, 3, 4),        // foot
            Block.box(3, 3, 2, 5, 15, 4),       // leg
            Block.box(12, 0, 2, 14, 3, 4),      // foot
            Block.box(11, 3, 2, 13, 15, 4),     // leg
            Block.box(4, 1, 2, 12, 3, 4),       // low crossbar
            Block.box(3, 8, 4, 5, 15, 16),      // side panel
            Block.box(0, 15, 0, 16, 16, 16));   // tabletop

    public TeachersDeskBlock(Properties properties) {
        super(properties, LEFT_NATIVE);
    }
}
