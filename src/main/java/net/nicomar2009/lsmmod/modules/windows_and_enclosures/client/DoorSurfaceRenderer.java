package net.nicomar2009.lsmmod.modules.windows_and_enclosures.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.nicomar2009.lsmcore.door.client.ClientDoors;
import net.nicomar2009.lsmcore.door.entity.*;
import net.nicomar2009.lsmcore.door.geometry.*;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.DoorCatalog;

/** Extract only immutable per-surface meshes. Entity links/poses come from Core, materials from LSM. */
@EventBusSubscriber(modid="lsmmod",value=Dist.CLIENT)
public final class DoorSurfaceRenderer extends EntityRenderer<DoorBody,DoorSurfaceRenderer.State> {
    private record Face(Identifier texture,List<Vec3> vertices,Vec3 normal,float[] uv) {}
    public static final class State extends EntityRenderState {private List<Face> faces=List.of();}
    public DoorSurfaceRenderer(EntityRendererProvider.Context context) {super(context);}
    @SubscribeEvent public static void register(EntityRenderersEvent.RegisterRenderers event) {event.registerEntityRenderer(net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleDoorEntities.BODY.get(),DoorSurfaceRenderer::new);}
    @Override public State createRenderState() {return new State();}
    @Override public void extractRenderState(DoorBody entity,State state,float partialTick) {
        super.extractRenderState(entity,state,partialTick);state.x=entity.getX();state.y=entity.getY();state.z=entity.getZ();state.faces=List.of();
        var optional=ClientDoors.get(entity.owner());if(optional.isEmpty())return;var r=optional.get().record();if(r.phase()!=net.nicomar2009.lsmcore.door.model.DoorRecord.Phase.ACTIVE)return;
        var style=DoorCatalog.style(r);if(style==null)return;
        var surface=r.structure().definition().surfaces().stream().filter(s->s.id().equals(entity.surface())).findFirst();if(surface.isEmpty())return;
        var s=surface.get();if(s.leaf()==null||entity.box()>0)return;
        if(entity.box()==-1&&!s.collision().isEmpty())return;
        var data=style.surfaces().get(s.id());if(data==null)return;
        var coordinates=data.has("render_box")?data.getAsJsonArray("render_box"):data.getAsJsonArray("box");
        double[] b=new double[6];for(int i=0;i<6;i++)b[i]=coordinates.get(i).getAsDouble()/16;
        var box=new DoorBox(b[0],b[1],b[2],b[3],b[4],b[5]);
        var leaf=r.structure().definition().leaf(s.leaf());boolean open=r.leaves().get(leaf.id()).open();
        var faces=new ArrayList<Face>();
        for(var face:Direction.values()) {
            var material=data.getAsJsonObject("faces").getAsJsonObject(face.getSerializedName());var raw=material.getAsJsonArray("uv");
            float[] uv=new float[4];for(int i=0;i<4;i++)uv[i]=raw.get(i).getAsFloat()/16;
            var points=AssemblyDoorModels.corners(box,face);var vertices=new ArrayList<Vec3>();
            for(var point:points) {var world=DoorTransform.point(r.structure().placement(),leaf,open,new DoorPoint(point.x,point.y,point.z));vertices.add(new Vec3(world.x(),world.y(),world.z()).subtract(entity.position()));}
            var first=points.getFirst();var n=DoorTransform.point(r.structure().placement(),leaf,open,new DoorPoint(first.x+face.getStepX(),first.y+face.getStepY(),first.z+face.getStepZ()));
            var a=DoorTransform.point(r.structure().placement(),leaf,open,new DoorPoint(first.x,first.y,first.z));var normal=new Vec3(n.x()-a.x(),n.y()-a.y(),n.z()-a.z());
            if(r.structure().placement().orientation().mirrored()) {Collections.reverse(vertices);float value=uv[0];uv[0]=uv[2];uv[2]=value;}
            String texture=material.get("texture").getAsString().substring(1);
            faces.add(new Face(DoorCatalog.id("textures/block/assembly_door_"+texture+".png"),List.copyOf(vertices),normal,uv));
        }
        state.faces=List.copyOf(faces);
    }
    @Override public void submit(State state,PoseStack stack,SubmitNodeCollector collector,CameraRenderState camera) {
        for(var face:state.faces)collector.submitCustomGeometry(stack,RenderTypes.entityTranslucent(face.texture()),(pose,vertices)->{
            float[] u={face.uv()[0],face.uv()[0],face.uv()[2],face.uv()[2]},v={face.uv()[1],face.uv()[3],face.uv()[3],face.uv()[1]};
            for(int i=0;i<4;i++) {var p=face.vertices().get(i);vertices.addVertex(pose,(float)p.x,(float)p.y,(float)p.z).setColor(-1).setUv(u[i],v[i]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(state.lightCoords).setNormal(pose,(float)face.normal().x,(float)face.normal().y,(float)face.normal().z);}
        });
    }
}
