package net.nicomar2009.lsmmod.modules.windows_and_enclosures.client;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.*;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.*;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.nicomar2009.lsmcore.door.client.*;
import net.nicomar2009.lsmcore.door.geometry.*;
import net.nicomar2009.lsmcore.door.model.*;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.*;

/** Cell-local chunk meshes: exact Core rigid poses, source UVs and real material layers. */
@EventBusSubscriber(modid="lsmmod",value=Dist.CLIENT)
public final class AssemblyDoorModels {
    private AssemblyDoorModels() {}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) {
        AssemblyDoorBlock.client=ClientDoors::at;
        AssemblyDoorBlock.clientReady=pos->ClientDoors.availability(pos)==ClientDoors.Availability.READY;
        var models=event.getBakingResult().blockStateModels();
        models.replaceAll((state,model)->state.getBlock() instanceof AssemblyDoorBlock?new DoorModel(model):model);
    }
    @SubscribeEvent public static void changed(DoorClientChangedEvent event) {
        var mc=Minecraft.getInstance();if(mc.level==null||event.chunk==null)return;
        // Core publishes immutable geometry before this event. Invalidate only its affected chunk.
        int x=event.chunk.x()<<4,z=event.chunk.z()<<4;
        for(int y=mc.level.getMinY();y<mc.level.getMaxY();y+=16)mc.level.setSectionDirtyWithNeighbors(x>>4,y>>4,z>>4);
    }
    private record Vertex(Vec3 point,float u,float v) {
        Vertex between(Vertex b,double t) {return new Vertex(point.add(b.point.subtract(point).scale(t)),(float)(u+(b.u-u)*t),(float)(v+(b.v-v)*t));}
    }
    private static final class Part implements BlockStateModelPart {
        final List<BakedQuad> quads=new ArrayList<>();final Material.Baked particle;int flags;
        Part(Material.Baked particle) {this.particle=particle;}
        void add(BakedQuad q) {quads.add(q);flags|=q.materialInfo().flags();}
        @Override public List<BakedQuad> getQuads(Direction face) {return face==null?quads:List.of();}
        @Override public boolean useAmbientOcclusion() {return true;}
        @Override public Material.Baked particleMaterial() {return particle;}
        @Override public int materialFlags() {return flags;}
    }
    private static final class DoorModel extends DelegateBlockStateModel implements DynamicBlockStateModel {
        // One entry per currently referenced immutable view and position.
        final Map<DoorView,Map<BlockPos,Part>> cache=Collections.synchronizedMap(new WeakHashMap<>());
        DoorModel(BlockStateModel fallback) {super(fallback);}
        @Override public int materialFlags() {return delegate.materialFlags()|BakedQuad.FLAG_ANIMATED;}
        @Override public int materialFlags(BlockAndTintGetter level,BlockPos pos,BlockState state) {return materialFlags();}
        @Override public void collectParts(BlockAndTintGetter level,BlockPos pos,BlockState state,RandomSource random,List<BlockStateModelPart> parts) {
            var link=level.getModelData(pos).get(DoorPieceEntity.LINK);var view=ClientDoors.at(pos);
            // Explicit missing-sync fallback: no guessed closed leaf; block shapes remain a safe barrier.
            if(link==null||view.isEmpty())return;var v=view.get();var r=v.record();var slot=r.structure().plan().slots().get(pos);
            if(slot==null||!r.id().equals(link.owner())||!link.slot().equals(slot.proxy()?"proxy":slot.part())||!BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(r.structure().definition().type())||r.phase()!=DoorRecord.Phase.ACTIVE)return;
            synchronized(cache) {
                var byPos=cache.computeIfAbsent(v,k->new HashMap<>());var cached=byPos.get(pos);
                if(cached==null) {cached=mesh(level,pos,state,v);byPos.put(pos.immutable(),cached);}
                parts.add(cached);
            }
        }
        private Part mesh(BlockAndTintGetter level,BlockPos pos,BlockState state,DoorView view) {
            var result=new Part(delegate.particleMaterial());var swatches=new ArrayList<BlockStateModelPart>();
            delegate.collectParts(level,pos,state,RandomSource.create(0),swatches);
            var materials=new HashMap<Integer,BakedQuad>();
            for(var part:swatches)for(var q:all(part))materials.putIfAbsent(q.materialInfo().tintIndex(),q);
            var r=view.record();var style=DoorCatalog.style(r);var placement=r.structure().placement();
            for(var surface:r.structure().definition().surfaces()) {
                var data=style.surfaces().get(surface.id());if(data==null)continue;if(surface.leaf()!=null&&r.structure().definition().motionPolicy()==DoorDefinition.MotionPolicy.VANILLA)continue;
                var leaf=surface.leaf()==null?null:r.structure().definition().leaf(surface.leaf());boolean open=leaf!=null&&r.leaves().get(leaf.id()).open();
                String material=data.get("material").getAsString();int index=List.of("wood","trim","glass","handle","wall","checker").indexOf(material);var source=materials.get(index);if(source==null)continue;
                for(Direction face:Direction.values()) {
                    var faceData=data.getAsJsonObject("faces").getAsJsonObject(face.getSerializedName());
                    var faceSource=materials.get(List.of("wood","trim","glass","handle","wall","checker").indexOf(faceData.get("texture").getAsString().substring(1)));
                    var uv=faceData.getAsJsonArray("uv");
                    var renderBox=surface.selection();
                    if(data.has("render_box")) {var a=data.getAsJsonArray("render_box");renderBox=new DoorBox(a.get(0).getAsDouble()/16,a.get(1).getAsDouble()/16,a.get(2).getAsDouble()/16,a.get(3).getAsDouble()/16,a.get(4).getAsDouble()/16,a.get(5).getAsDouble()/16);}
                    var points=corners(renderBox,face);float u0=uv.get(0).getAsFloat()/16,v0=uv.get(1).getAsFloat()/16,u1=uv.get(2).getAsFloat()/16,v1=uv.get(3).getAsFloat()/16;
                    var sprite=faceSource.materialInfo().sprite();
                    float[] us={u0,u0,u1,u1},vs={v0,v1,v1,v0};var polygon=new ArrayList<Vertex>();
                    for(int i=0;i<4;i++)polygon.add(new Vertex(vec(DoorTransform.point(placement,leaf,open,point(points.get(i)))),sprite.getU(us[i]),sprite.getV(vs[i])));
                    var normal=vec(DoorTransform.point(placement,leaf,open,point(points.get(0).add(vector(face))))).subtract(polygon.get(0).point());
                    emit(polygon,direction(normal),faceSource,-1,faceSource.materialInfo().shade(),pos,result);
                }
            }
            return result;
        }
    }
    private static List<BakedQuad> all(BlockStateModelPart part) {
        var result=new ArrayList<BakedQuad>(part.getQuads(null));for(var d:Direction.values())result.addAll(part.getQuads(d));return result;
    }
    private static Vec3 vector(Direction direction) {return new Vec3(direction.getStepX(),direction.getStepY(),direction.getStepZ());}
    private static DoorPoint point(Vec3 p) {return new DoorPoint(p.x,p.y,p.z);}
    private static Vec3 vec(DoorPoint p) {return new Vec3(p.x(),p.y(),p.z());}
    private static Direction direction(Vec3 v) {for(var d:Direction.values())if(v.dot(vector(d))>.99)return d;throw new IllegalArgumentException("normal");}
    static List<Vec3> corners(DoorBox b,Direction face) {
        double x=b.minX(),y=b.minY(),z=b.minZ(),X=b.maxX(),Y=b.maxY(),Z=b.maxZ();
        return switch(face) {
            case NORTH->List.of(new Vec3(X,Y,z),new Vec3(X,y,z),new Vec3(x,y,z),new Vec3(x,Y,z));
            case SOUTH->List.of(new Vec3(x,Y,Z),new Vec3(x,y,Z),new Vec3(X,y,Z),new Vec3(X,Y,Z));
            case EAST->List.of(new Vec3(X,Y,Z),new Vec3(X,y,Z),new Vec3(X,y,z),new Vec3(X,Y,z));
            case WEST->List.of(new Vec3(x,Y,z),new Vec3(x,y,z),new Vec3(x,y,Z),new Vec3(x,Y,Z));
            case UP->List.of(new Vec3(x,Y,z),new Vec3(x,Y,Z),new Vec3(X,Y,Z),new Vec3(X,Y,z));
            case DOWN->List.of(new Vec3(X,y,z),new Vec3(X,y,Z),new Vec3(x,y,Z),new Vec3(x,y,z));
        };
    }
    private static double axis(Vec3 p,int axis) {return axis==0?p.x:axis==1?p.y:p.z;}
    private static ArrayList<Vertex> cut(List<Vertex> input,int axis,double edge,boolean lower) {
        var output=new ArrayList<Vertex>();if(input.isEmpty())return output;var previous=input.getLast();double p=axis(previous.point,axis);
        for(var current:input) {double c=axis(current.point,axis);boolean inside=lower?c>=edge-1e-9:c<=edge+1e-9,wasInside=lower?p>=edge-1e-9:p<=edge+1e-9;
            if(inside!=wasInside)output.add(previous.between(current,(edge-p)/(c-p)));if(inside)output.add(current);previous=current;p=c;}
        return output;
    }
    private static void emit(List<Vertex> input,Direction normal,BakedQuad source,int color,boolean ao,BlockPos pos,Part output) {
        var polygon=new ArrayList<>(input);if(polygon.size()<3)return;
        for(int a=0;a<3;a++) {int base=a==0?pos.getX():a==1?pos.getY():pos.getZ();polygon=cut(polygon,a,base,true);polygon=cut(polygon,a,base+1,false);}
        var points=new ArrayList<Vertex>();for(var v:polygon)if(points.isEmpty()||v.point.distanceToSqr(points.getLast().point)>1e-16)points.add(v);
        if(points.size()>1&&points.getFirst().point.distanceToSqr(points.getLast().point)<1e-16)points.removeLast();
        if(points.size()<3)return;
        Vec3 cross=points.get(1).point.subtract(points.get(0).point).cross(points.get(2).point.subtract(points.get(0).point));
        if(cross.lengthSqr()<1e-16)return;if(cross.dot(vector(normal))<0)Collections.reverse(points);
        // Axis-aligned rectangular source quads remain rectangles after rigid poses and cell clipping.
        if(points.size()!=4)return;
        var material=source.materialInfo();var builder=new QuadBakingVertexConsumer();builder.setDirection(normal);
        builder.setSprite(material.sprite(),material.layer(),material.itemRenderType());builder.setTintIndex(-1);builder.setShade(material.shade());builder.setAmbientOcclusion(ao);builder.setLightEmission(0);
        for(var v:points) {builder.addVertex((float)(v.point.x-pos.getX()),(float)(v.point.y-pos.getY()),(float)(v.point.z-pos.getZ()));builder.setUv(v.u,v.v);builder.setColor(color);builder.setNormal(normal.getStepX(),normal.getStepY(),normal.getStepZ());}
        output.add(builder.bakeQuad());
    }
}
