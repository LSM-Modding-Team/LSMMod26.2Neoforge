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
        return OUTLINES.computeIfAbsent(state, key -> boundingBox(collision.get()));
    }

    public static VoxelShape boundingBox(VoxelShape shape) {
        return shape.isEmpty() ? Shapes.empty() : Shapes.create(shape.bounds());
    }
}
