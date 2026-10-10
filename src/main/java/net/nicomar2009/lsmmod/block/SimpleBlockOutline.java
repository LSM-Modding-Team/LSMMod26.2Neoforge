package net.nicomar2009.lsmmod.block;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Cheap ray-picking and outline geometry; physical collision stays in each block. */
public final class SimpleBlockOutline {
    // Current furniture geometry depends only on state, not world position or context.
    private static final Map<BlockState, VoxelShape> OUTLINES = new ConcurrentHashMap<>();

    private SimpleBlockOutline() {}

    public static VoxelShape forState(BlockState state, Supplier<VoxelShape> collision) {
        VoxelShape cached = OUTLINES.get(state);
        if (cached != null) {
            return cached;
        }
        // Collision queries may request another outline during state initialization.
        // Compute outside the map update: nested computeIfAbsent calls can throw
        // IllegalStateException("Recursive update"), even for different keys.
        VoxelShape calculated = boundingBox(collision.get());
        VoxelShape existing = OUTLINES.putIfAbsent(state, calculated);
        return existing != null ? existing : calculated;
    }

    public static VoxelShape boundingBox(VoxelShape shape) {
        return shape.isEmpty() ? Shapes.empty() : Shapes.create(shape.bounds());
    }
}
