package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;

import com.google.gson.*;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmcore.door.geometry.*;
import net.nicomar2009.lsmcore.door.model.*;
import net.nicomar2009.paintingmod.api.*;

/** Materials stay in LSM; immutable geometry snapshots and all leaf states belong to Core. */
public final class DoorCatalog {
    public record Style(DoorDefinition definition, Map<String,JsonObject> surfaces,JsonObject legacy) {}
    public static final Map<String,Style> STYLES=load();
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath("lsmmod",path); }
    private DoorCatalog() {}
    private static Map<String,Style> load() {
        var result=new LinkedHashMap<String,Style>();
        try (var stream=DoorCatalog.class.getResourceAsStream("/assets/lsmmod/assembly_doors.json")) {
            if(stream==null)throw new IllegalStateException("Missing door catalog");
            var json=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
            for(var entry:json.entrySet()) {
                var data=entry.getValue().getAsJsonObject();int width=data.get("width").getAsInt();
                var cells=new ArrayList<DoorCell>();
                for(int x=0;x<width;x++)for(int y=0;y<3;y++)cells.add(new DoorCell("cell_"+x+"_"+y,new BlockPos(x,y,0)));
                var leaves=new ArrayList<DoorLeaf>();
                for(var value:data.getAsJsonArray("leaves")) {
                    var l=value.getAsJsonObject();var p=l.getAsJsonArray("pivot");
                    leaves.add(new DoorLeaf(l.get("id").getAsString(),new DoorPoint(p.get(0).getAsDouble()/16,0,p.get(2).getAsDouble()/16),l.get("turn").getAsInt(),true,DoorLeaf.Redstone.FOLLOW_POWER));
                }
                var surfaces=new ArrayList<DoorSurface>();var appearance=new LinkedHashMap<String,JsonObject>();
                for(var value:data.getAsJsonArray("surfaces")) {
                    var s=value.getAsJsonObject();var a=s.getAsJsonArray("box");double[] b=new double[6];for(int i=0;i<6;i++)b[i]=a.get(i).getAsDouble()/16;
                    var box=new DoorBox(b[0],b[1],b[2],b[3],b[4],b[5]);String name=s.get("id").getAsString();
                    surfaces.add(new DoorSurface(name,"cell_0_0",s.get("leaf").isJsonNull()?null:s.get("leaf").getAsString(),box,List.of(box)));appearance.put(name,s);
                }
                result.put(entry.getKey(),new Style(new DoorDefinition(id(entry.getKey()),new DoorBox(0,0,0,width,3,1),"cell_0_0",cells,leaves,surfaces,DoorRemovalPolicy.ANY_BREAK,DoorDefinition.MotionPolicy.VANILLA,2),Map.copyOf(appearance),data.getAsJsonObject("legacy")));
            }
            return Map.copyOf(result);
        } catch(java.io.IOException e) { throw new IllegalStateException(e); }
    }
    public static boolean knownLegacy(DoorRecord r) {
        var style=style(r);if(style==null||style.legacy()==null)return false;var definition=r.structure().definition();
        var surfaces=style.legacy().getAsJsonArray("surfaces");if(surfaces.size()!=definition.surfaces().size())return false;
        for(var raw:surfaces) {
            var expected=raw.getAsJsonObject();var match=definition.surfaces().stream().filter(v->v.id().equals(expected.get("id").getAsString())).findFirst();if(match.isEmpty())return false;
            var s=match.get();var a=expected.getAsJsonArray("box");var box=new DoorBox(a.get(0).getAsDouble()/16,a.get(1).getAsDouble()/16,a.get(2).getAsDouble()/16,a.get(3).getAsDouble()/16,a.get(4).getAsDouble()/16,a.get(5).getAsDouble()/16);
            String leaf=expected.get("leaf").isJsonNull()?null:expected.get("leaf").getAsString();
            if(!Objects.equals(s.leaf(),leaf)||!s.part().equals("cell_0_0")||!s.selection().equals(box)||!s.collision().equals(List.of(box)))return false;
        }
        var leaves=style.legacy().getAsJsonArray("leaves");if(leaves.size()!=definition.leaves().size())return false;
        for(var raw:leaves) {
            var e=raw.getAsJsonObject();var candidate=definition.leaves().stream().filter(l->l.id().equals(e.get("id").getAsString())).findFirst();if(candidate.isEmpty())return false;var l=candidate.get();var p=e.getAsJsonArray("pivot");
            if(!l.pivot().equals(new DoorPoint(p.get(0).getAsDouble()/16,0,p.get(2).getAsDouble()/16))||l.turn()!=e.get("turn").getAsInt()||!l.manual()||l.redstone()!=DoorLeaf.Redstone.FOLLOW_POWER)return false;
        }
        return true;
    }
    public static Style style(DoorRecord r) {return STYLES.get(r.structure().definition().type().getPath());}
    /** All six faces of ONLY paintablewall; dimensions are counts of 8px cells. */
    public static List<PaintSurface> patches(DoorRecord record) {
        var result=new ArrayList<PaintSurface>();var p=record.structure().placement();var o=p.orientation();
        for(var s:record.structure().definition().surfaces()) {
            var style=style(record);if(style==null)continue;var appearance=style.surfaces().get(s.id());
            if(appearance==null)continue;
            if(!appearance.get("paintable").getAsBoolean()||s.leaf()!=null)continue;
            var b=s.selection();
            for(Direction face:Direction.values()) {
                Vec3 origin;Direction u,v;int width,height;
                switch(face.getAxis()) {
                    case Z -> {origin=new Vec3(b.minX(),b.minY(),face==Direction.NORTH?b.minZ():b.maxZ());u=Direction.EAST;v=Direction.UP;width=(int)Math.round((b.maxX()-b.minX())*2);height=6;}
                    case X -> {origin=new Vec3(face==Direction.WEST?b.minX():b.maxX(),b.minY(),b.minZ());u=Direction.SOUTH;v=Direction.UP;width=(int)Math.round((b.maxZ()-b.minZ())*2);height=6;}
                    default -> {origin=new Vec3(b.minX(),face==Direction.DOWN?b.minY():b.maxY(),b.minZ());u=Direction.EAST;v=Direction.SOUTH;width=(int)Math.round((b.maxX()-b.minX())*2);height=(int)Math.round((b.maxZ()-b.minZ())*2);}
                }
                var mask=new BitSet(width*height);mask.set(0,width*height);
                var frame=new PaintFrame(origin,u,v,face).placed(Vec3.atLowerCornerOf(p.origin()),o.quarterTurns(),o.mirrored());
                result.add(new PaintSurface(s.id()+"/"+face.getSerializedName(),id("assembly_wall_"+face.getSerializedName()),frame,face,width,height,mask,id("assembly_door_wall"),id("assembly_door_checker")));
            }
        }
        return List.copyOf(result);
    }
}
