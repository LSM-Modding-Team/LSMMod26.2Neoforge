package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Geometry in model pixels; native orientation faces south. */
public final class NewChairShapes {
    public static final VoxelShape ENGLISHROOMCHAIR = Shapes.or(
            Block.box(2.5, 0, 3.5, 3.5, 6, 4.5),
            Block.box(2.5, 0, 12, 3.5, 6, 13),
            Block.box(12.5, 0, 3.5, 13.5, 6, 4.5),
            Block.box(12.5, 0, 12, 13.5, 6, 13),
            Block.box(3, 6, 3, 13, 7, 13),
            Block.box(2, 6, 4, 3, 7, 12),
            Block.box(13, 6, 4, 14, 7, 12),
            Block.box(4, 6, 13, 12, 7, 14),
            Block.box(2.5, 7, 2, 13.5, 15.5, 3));

    public static final VoxelShape HALLCHAIR = Shapes.or(
            Block.box(2.5, 0, 3.5, 3.5, 6, 4.5),
            Block.box(2.5, 0, 12, 3.5, 6, 13),
            Block.box(12.5, 0, 3.5, 13.5, 6, 4.5),
            Block.box(12.5, 0, 12, 13.5, 6, 13),
            Block.box(3, 6, 3, 13, 7, 13),
            Block.box(2, 6, 4, 3, 7, 12),
            Block.box(13, 6, 4, 14, 7, 12),
            Block.box(4, 6, 13, 12, 7, 14),
            Block.box(3, 7, 3.5, 13, 7.6, 13),
            Block.box(4, 7, 13, 12, 7.4, 13.5),
            Block.box(2.5, 6, 2.5, 3.5, 14, 3.5),
            Block.box(12.5, 6, 2.5, 13.5, 14, 3.5),
            Block.box(3, 9, 2, 13, 14.5, 3.5),
            Block.box(4, 14.5, 2, 12, 15, 3.5));

    public static final VoxelShape PLASTICCHAIR = Shapes.or(
            Block.box(2.5, 0, 3.5, 3.75, 6, 4.75),
            Block.box(2.5, 0, 12, 3.75, 6, 13.25),
            Block.box(12.5, 0, 3.5, 13.75, 6, 4.75),
            Block.box(12.5, 0, 12, 13.75, 6, 13.25),
            Block.box(3, 6, 3, 13, 7, 13),
            Block.box(2, 6, 4, 3, 7, 12),
            Block.box(13, 6, 4, 14, 7, 12),
            Block.box(4, 6, 13, 12, 7, 14),
            Block.box(2.5, 7, 2, 4, 14, 3.25),
            Block.box(12, 7, 2, 13.5, 14, 3.25),
            Block.box(3.5, 14, 2, 12.5, 15, 3.25),
            Block.box(4.5, 15, 2, 11.5, 15.5, 3.25),
            Block.box(4, 7, 2, 12, 8, 3.25),
            Block.box(5.5, 8, 2, 6.25, 14, 3.25),
            Block.box(7.625, 8, 2, 8.375, 14, 3.25),
            Block.box(9.75, 8, 2, 10.5, 14, 3.25),
            Block.box(5.5, 10.5, 2, 10.5, 11, 3.25));

    public static final VoxelShape PLASTICCHAIR_ARMS = Shapes.or(
            Block.box(2.5, 0, 3.5, 3.75, 6, 4.75),
            Block.box(2.5, 0, 12, 3.75, 6, 13.25),
            Block.box(12.5, 0, 3.5, 13.75, 6, 4.75),
            Block.box(12.5, 0, 12, 13.75, 6, 13.25),
            Block.box(3, 6, 3, 13, 7, 13),
            Block.box(2, 6, 4, 3, 7, 12),
            Block.box(13, 6, 4, 14, 7, 12),
            Block.box(4, 6, 13, 12, 7, 14),
            Block.box(2.5, 7, 2, 4, 14, 3.25),
            Block.box(12, 7, 2, 13.5, 14, 3.25),
            Block.box(3.5, 14, 2, 12.5, 15, 3.25),
            Block.box(4.5, 15, 2, 11.5, 15.5, 3.25),
            Block.box(4, 7, 2, 12, 8, 3.25),
            Block.box(5.5, 8, 2, 6.25, 14, 3.25),
            Block.box(7.625, 8, 2, 8.375, 14, 3.25),
            Block.box(9.75, 8, 2, 10.5, 14, 3.25),
            Block.box(5.5, 10.5, 2, 10.5, 11, 3.25),
            Block.box(1.5, 7, 11, 2.5, 10, 12),
            Block.box(1.5, 9.5, 2.5, 2.5, 10.5, 12),
            Block.box(13.5, 7, 11, 14.5, 10, 12),
            Block.box(13.5, 9.5, 2.5, 14.5, 10.5, 12));

    public static final VoxelShape DINING_CHAIR = Shapes.or(
            Block.box(2.5, 0, 2, 3.5, 7, 3),
            Block.box(2.5, 0, 12, 3.5, 6, 13),
            Block.box(12.5, 0, 2, 13.5, 7, 3),
            Block.box(12.5, 0, 12, 13.5, 6, 13),
            Block.box(3, 6, 3, 13, 7, 13),
            Block.box(2, 6, 4, 3, 7, 12),
            Block.box(13, 6, 4, 14, 7, 12),
            Block.box(4, 6, 13, 12, 7, 14),
            Block.box(2.5, 7, 2, 3.5, 14, 3),
            Block.box(12.5, 7, 2, 13.5, 14, 3),
            Block.box(3.5, 14, 2, 12.5, 15, 3),
            Block.box(4.5, 15, 2, 11.5, 15.5, 3),
            Block.box(3.5, 7, 2, 12.5, 14, 3),
            Block.box(2.5, 5, 2, 13.5, 6, 3),
            Block.box(2.5, 5, 12, 13.5, 6, 13),
            Block.box(2.5, 5, 3, 3.5, 6, 12),
            Block.box(12.5, 5, 3, 13.5, 6, 12));

    private NewChairShapes() {}
}
