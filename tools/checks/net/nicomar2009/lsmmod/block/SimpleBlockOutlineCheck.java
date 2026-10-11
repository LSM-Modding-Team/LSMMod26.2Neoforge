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
    private static final Pattern UNION = Pattern.compile("(?:Shapes\\.or|ExactVoxelShapes\\.union)\\((.*?)\\);", Pattern.DOTALL);
    private static final Pattern BOX = Pattern.compile("(?:Block|ExactVoxelShapes)\\.box\\(([\\d., \\-]+)\\)");
    private static int checks;

    public static void main(String[] args) throws Exception {
        checkChairGridRotations();
        net.minecraft.world.phys.AABB[] volumes = {
            new net.minecraft.world.phys.AABB(.125,0,.25,.25,.875,.75),
            new net.minecraft.world.phys.AABB(.75,0,.25,.875,.875,.75),
            new net.minecraft.world.phys.AABB(.125,.5,.25,.875,.625,.75),
            new net.minecraft.world.phys.AABB(-.125,.8,.35,.625,1.3,.65)};
        VoxelShape original = Shapes.empty();
        for(var b:volumes) original=Shapes.or(original,Shapes.create(b));
        VoxelShape direct=ExactVoxelShapes.union(volumes);
        if(Shapes.joinIsNotEmpty(original,direct,net.minecraft.world.phys.shapes.BooleanOp.NOT_SAME))
            throw new AssertionError("Direct union must preserve physical gaps and overlaps");
        if(!ExactVoxelShapes.union().isEmpty()) throw new AssertionError("Empty input must remain empty");
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

    private static void checkChairGridRotations() {
        if (com.mojang.math.OctahedralGroup.BLOCK_ROT_Y_90.rotate(net.minecraft.core.Direction.SOUTH)
                != net.minecraft.core.Direction.WEST) throw new AssertionError("Chair rotation direction changed");
        VoxelShape[] natives = {Shapes.empty(), Shapes.block(),
                Shapes.box(-.25, 0, .125, .375, 1.5, .625),
                Shapes.or(Shapes.box(.125,0,.125,.25,.75,.875),
                        Shapes.box(.75,0,.125,.875,.75,.875),
                        Shapes.box(.125,.75,.125,.875,.875,.875))};
        for (VoxelShape nativeShape : natives) {
            VoxelShape[] rotations = SharedChairRotations.horizontal(nativeShape);
            VoxelShape previous = nativeShape;
            for (int turn = 0; turn < 4; turn++) {
                if (Shapes.joinIsNotEmpty(previous, rotations[turn], net.minecraft.world.phys.shapes.BooleanOp.NOT_SAME))
                    throw new AssertionError("Chair collision changed at turn " + turn);
                VoxelShape[] next = {Shapes.empty()};
                previous.forAllBoxes((x1,y1,z1,x2,y2,z2) -> next[0] = Shapes.or(next[0],
                        Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
                previous = next[0];
            }
            if (Shapes.joinIsNotEmpty(previous,nativeShape,net.minecraft.world.phys.shapes.BooleanOp.NOT_SAME))
                throw new AssertionError("Four chair rotations must restore the original volume");
            VoxelShape[] again = SharedChairRotations.horizontal(nativeShape);
            if (again == rotations) throw new AssertionError("Array ownership must be independent");
            for (int turn=0; turn<4; turn++)
                if (again[turn] != rotations[turn]) throw new AssertionError("Physical rotations must be shared");
        }
        VoxelShape a=Shapes.box(.125,.25,.375,.75,.875,1.125);
        VoxelShape b=Shapes.box(.125,.25,.375,.75,.875,1.125);
        VoxelShape[] first=SharedChairRotations.horizontal(a),second=SharedChairRotations.horizontal(b);
        for(int turn=0;turn<4;turn++)
            if(first[turn]!=second[turn])throw new AssertionError("Equal native volumes must share rotations");
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
