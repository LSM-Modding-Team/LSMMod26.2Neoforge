package net.nicomar2009.lsmmod.block;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Runs against the actual model-derived collision boxes without booting a client. */
public final class SimpleBlockOutlineCheck {
    private static final Pattern UNION = Pattern.compile("Shapes\\.or\\((.*?)\\);", Pattern.DOTALL);
    private static final Pattern BOX = Pattern.compile("Block\\.box\\(([\\d., \\-]+)\\)");
    private static int checks;

    public static void main(String[] args) throws Exception {
        if (!SimpleBlockOutline.boundingBox(Shapes.empty()).isEmpty()) {
            throw new AssertionError("An empty cell must not acquire an outline");
        }
        // A ray through a chair's empty center selects its exterior box, while its
        // unchanged physical shape still leaves that gap open.
        VoxelShape legs = Shapes.or(Shapes.box(.1,0,.1,.2,.5,.9), Shapes.box(.8,0,.1,.9,.5,.9));
        Vec3 from = new Vec3(.5,.25,-1), to = new Vec3(.5,.25,2);
        if (legs.clip(from,to,BlockPos.ZERO) != null
                || SimpleBlockOutline.boundingBox(legs).clip(from,to,BlockPos.ZERO) == null) {
            throw new AssertionError("Selection must use the envelope without filling physical gaps");
        }
        Path blocks = Path.of(args[0], "src/main/java/net/nicomar2009/lsmmod/block");
        try (var files = Files.list(blocks)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).sorted().toList()) {
                var unions = UNION.matcher(Files.readString(file));
                int group = 0;
                while (unions.find()) {
                    VoxelShape source = Shapes.empty();
                    var boxes = BOX.matcher(unions.group(1));
                    while (boxes.find()) {
                        String[] values = boxes.group(1).split(",");
                        if (values.length != 6) continue;
                        double[] v = new double[6];
                        for (int i = 0; i < 6; i++) v[i] = Double.parseDouble(values[i].trim()) / 16;
                        source = Shapes.or(source, Shapes.box(v[0],v[1],v[2],v[3],v[4],v[5]));
                    }
                    if (!source.isEmpty()) checkRotations(file.getFileName() + ":" + group++, source);
                }
            }
        }
        if (checks < 40) throw new AssertionError("Insufficient furniture shape coverage: " + checks);
        System.out.println("Verified " + checks + " furniture shapes/orientations; each nonempty outline has 12 edges.");
    }

    private static void checkRotations(String name, VoxelShape source) {
        for (int facing = 0; facing < 4; facing++) {
            var before = source.toAabbs();
            VoxelShape outline = SimpleBlockOutline.boundingBox(source);
            if (!outline.bounds().equals(source.bounds()) || outline.toAabbs().size() != 1) {
                throw new AssertionError(name + ": outline must preserve bounds in one box");
            }
            int[] edges = {0};
            outline.forAllEdges((x1,y1,z1,x2,y2,z2) -> edges[0]++);
            if (edges[0] != 12 || !before.equals(source.toAabbs())) {
                throw new AssertionError(name + ": must retain physical geometry and draw only 12 edges");
            }
            checks++;
            VoxelShape[] rotated = {Shapes.empty()};
            source.forAllBoxes((x1,y1,z1,x2,y2,z2) -> rotated[0] = Shapes.or(rotated[0],
                    Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
            source = rotated[0];
        }
    }
}
