package net.nicomar2009.lsmmod.media;

import java.util.ArrayDeque;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.block.ProjectorScreenBlock;

/** Adjacent complete screen assemblies can form a wider/taller rectangle without new block states. */
public record SchoolScreenArea(BlockPos origin, Direction facing, int width, int height) {
    public static SchoolScreenArea find(Level level, BlockPos start) {
        BlockState initial = level.getBlockState(start);
        if (!(initial.getBlock() instanceof ProjectorScreenBlock)) return null;
        Direction facing = initial.getValue(ProjectorScreenBlock.FACING), across = facing.getClockWise();
        var seen = new HashSet<BlockPos>();
        var queue = new ArrayDeque<BlockPos>();
        queue.add(start.immutable());
        int minU = 0, maxU = 0, minV = 0, maxV = 0;
        while (!queue.isEmpty()) {
            BlockPos pos = queue.remove();
            if (seen.contains(pos) || !level.hasChunkAt(pos)) continue;
            BlockState s = level.getBlockState(pos);
            if (!(s.getBlock() instanceof ProjectorScreenBlock) || s.getValue(ProjectorScreenBlock.FACING) != facing) continue;
            int u = (pos.getX() - start.getX()) * across.getStepX() + (pos.getZ() - start.getZ()) * across.getStepZ();
            int v = pos.getY() - start.getY();
            minU = Math.min(minU, u); maxU = Math.max(maxU, u);
            minV = Math.min(minV, v); maxV = Math.max(maxV, v);
            if (maxU - minU + 1 > 16 || maxV - minV + 1 > 12) return null;
            seen.add(pos);
            queue.add(pos.relative(across)); queue.add(pos.relative(across.getOpposite()));
            queue.add(pos.above()); queue.add(pos.below());
        }
        int width = maxU - minU + 1, height = maxV - minV + 1;
        if (seen.size() != width * height) return null;
        // Do not project on incomplete vanilla 4x3 assemblies awaiting dismantling.
        for (BlockPos pos : seen) {
            BlockPos root = SchoolMediaDevices.anchor(level, pos);
            for (int c = 0; c < 4; c++) for (int r = 0; r < 3; r++) {
                BlockPos cell = root.relative(across, c).above(r);
                if (!seen.contains(cell)) return null;
                BlockState state = level.getBlockState(cell);
                if (state.getValue(ProjectorScreenBlock.COLUMN) != c
                        || state.getValue(ProjectorScreenBlock.ROW) != r) return null;
            }
        }
        return new SchoolScreenArea(start.relative(across, minU).above(minV), facing, width, height);
    }
    public boolean contains(BlockPos pos) {
        Direction across = facing.getClockWise();
        int u = (pos.getX() - origin.getX()) * across.getStepX() + (pos.getZ() - origin.getZ()) * across.getStepZ();
        int plane = (pos.getX() - origin.getX()) * facing.getStepX() + (pos.getZ() - origin.getZ()) * facing.getStepZ();
        return plane == 0 && u >= 0 && u < width && pos.getY() >= origin.getY() && pos.getY() < origin.getY() + height;
    }
}
