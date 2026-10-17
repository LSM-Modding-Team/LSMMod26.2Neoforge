package net.nicomar2009.lsmmod.modules.awnings.block;
import net.nicomar2009.lsmmod.block.SimpleBlockOutline;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Exact cloth volume, built on its coordinate grid without iterative shape unions. */
public final class AwningShapes {
    private record Profile(int start, int end, boolean upper, Direction facing) {}
    private static final Map<Profile, VoxelShape> COLLISIONS = new ConcurrentHashMap<>();
    private static final Map<Profile, VoxelShape> OUTLINES = new ConcurrentHashMap<>();

    private AwningShapes() {}

    public static VoxelShape collision(int start, int end, boolean upper, Direction facing) {
        Profile profile = new Profile(start, end, upper, facing);
        return COLLISIONS.computeIfAbsent(profile, AwningShapes::build);
    }

    public static VoxelShape outline(int start, int end, boolean upper, Direction facing) {
        Profile profile = new Profile(start, end, upper, facing);
        return OUTLINES.computeIfAbsent(profile,
                key -> SimpleBlockOutline.boundingBox(collision(key.start, key.end, key.upper, key.facing)));
    }

    private static VoxelShape build(Profile profile) {
        List<double[]> boxes = new ArrayList<>(8);
        double offset = profile.upper ? 16 : 0;
        for (int i = 0; i < 8; i++) {
            double top = 2 * (profile.start + (profile.end - profile.start) * (i + 0.5) / 8.0) - offset;
            double low = Math.max(0, top - 0.75), high = Math.min(16, top);
            if (low >= high) continue;
            double z1 = i * 2, z2 = z1 + 2;
            double[] box = switch (profile.facing) {
                case SOUTH -> new double[]{0, low, z1, 16, high, z2};
                case NORTH -> new double[]{0, low, 16-z2, 16, high, 16-z1};
                case EAST -> new double[]{z1, low, 0, z2, high, 16};
                case WEST -> new double[]{16-z2, low, 0, 16-z1, high, 16};
                default -> throw new IllegalArgumentException("Awning direction must be horizontal");
            };
            boxes.add(box);
        }
        if (boxes.isEmpty()) return Shapes.empty();
        double[] x = coordinates(boxes, 0), y = coordinates(boxes, 1), z = coordinates(boxes, 2);
        BitSetDiscreteVoxelShape grid = new BitSetDiscreteVoxelShape(x.length-1, y.length-1, z.length-1);
        for (double[] box : boxes) {
            int x0 = Arrays.binarySearch(x, box[0]/16), x1 = Arrays.binarySearch(x, box[3]/16);
            int y0 = Arrays.binarySearch(y, box[1]/16), y1 = Arrays.binarySearch(y, box[4]/16);
            int z0 = Arrays.binarySearch(z, box[2]/16), z1 = Arrays.binarySearch(z, box[5]/16);
            for (int ix = x0; ix < x1; ix++)
                for (int iy = y0; iy < y1; iy++)
                    for (int iz = z0; iz < z1; iz++) grid.fill(ix, iy, iz);
        }
        return new ClothShape(grid, x, y, z);
    }

    private static double[] coordinates(List<double[]> boxes, int axis) {
        return boxes.stream().flatMapToDouble(box -> java.util.stream.DoubleStream.of(box[axis]/16, box[axis+3]/16))
                .distinct().sorted().toArray();
    }

    /** Vanilla VoxelShape operations work on the same exact boundaries as the original boxes. */
    private static final class ClothShape extends VoxelShape {
        private final DoubleList x, y, z;
        ClothShape(BitSetDiscreteVoxelShape grid, double[] x, double[] y, double[] z) {
            super(grid);
            this.x = DoubleLists.unmodifiable(DoubleArrayList.wrap(x));
            this.y = DoubleLists.unmodifiable(DoubleArrayList.wrap(y));
            this.z = DoubleLists.unmodifiable(DoubleArrayList.wrap(z));
        }
        @Override public DoubleList getCoords(Direction.Axis axis) {
            return switch (axis) { case X -> x; case Y -> y; case Z -> z; };
        }
    }
}
