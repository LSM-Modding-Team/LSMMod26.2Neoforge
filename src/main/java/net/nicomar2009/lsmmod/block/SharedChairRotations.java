package net.nicomar2009.lsmmod.block;

import com.mojang.math.OctahedralGroup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Shared physical chair rotations; no world, state or texture dependencies. */
final class SharedChairRotations {
    private static final Map<VoxelShape, VoxelShape[]> BY_IDENTITY = new IdentityHashMap<>();
    private static final Map<List<AABB>, VoxelShape[]> BY_BOXES = new HashMap<>();
    private static final Comparator<AABB> BOX_ORDER = Comparator.comparingDouble((AABB b) -> b.minX)
            .thenComparingDouble(b -> b.minY).thenComparingDouble(b -> b.minZ)
            .thenComparingDouble(b -> b.maxX).thenComparingDouble(b -> b.maxY).thenComparingDouble(b -> b.maxZ);

    private SharedChairRotations() {}

    /** SOUTH, WEST, NORTH, EAST, matching get2DDataValue and native model rotations. */
    static synchronized VoxelShape[] horizontal(VoxelShape nativeShape) {
        VoxelShape[] cached = BY_IDENTITY.get(nativeShape);
        if (cached == null) {
            // Usually the same static native shape is reused by differently textured chairs.
            // Also share independently supplied shapes with identical box decompositions.
            List<AABB> boxes = new ArrayList<>(nativeShape.toAabbs());
            boxes.sort(BOX_ORDER);
            List<AABB> key = List.copyOf(boxes);
            cached = BY_BOXES.get(key);
            if (cached == null) {
                cached = new VoxelShape[]{nativeShape,
                        Shapes.rotate(nativeShape, OctahedralGroup.BLOCK_ROT_Y_90),
                        Shapes.rotate(nativeShape, OctahedralGroup.BLOCK_ROT_Y_180),
                        Shapes.rotate(nativeShape, OctahedralGroup.BLOCK_ROT_Y_270)};
                BY_BOXES.put(key, cached);
            }
            BY_IDENTITY.put(nativeShape, cached);
        }
        // Each block owns its array; the immutable-by-convention shape objects are shared.
        return cached.clone();
    }
}
