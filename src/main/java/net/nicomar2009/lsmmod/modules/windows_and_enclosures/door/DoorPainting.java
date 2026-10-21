package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import net.nicomar2009.lsmcore.door.integration.DoorHit;
import net.nicomar2009.lsmcore.door.model.*;
import net.nicomar2009.paintingmod.api.*;

/** Public PaintingMod provider. No hammer callback and no door mutation on paint/clean/sand. */
public final class DoorPainting implements PaintProvider {
    @Override public Optional<PaintTarget> resolve(PaintQuery query) {
        return AssemblyDoorBlock.view(query.level(),query.hit().getBlockPos()).flatMap(v->{
            var r=v.record();if(r.phase()!=DoorRecord.Phase.ACTIVE||DoorCatalog.patches(r).isEmpty())return Optional.empty();
            for(var slot:r.structure().plan().slots().values()) {
                if(query.level().isClientSide()&&!AssemblyDoorBlock.clientReady.test(slot.position()))return Optional.empty();
                if(!query.level().hasChunkAt(slot.position())||!(query.level().getBlockEntity(slot.position()) instanceof DoorPieceEntity piece)||!piece.matches(r,slot.part()))return Optional.empty();
            }
            try {
                PaintLayer layer=query.level() instanceof ServerLevel server?DoorPaintStore.get(server,r):((DoorPieceEntity)query.level().getBlockEntity(query.hit().getBlockPos())).snapshot();
                return layer==null?Optional.empty():Optional.of(layer.target(Long.toString(r.revision()),r.structure().plan().controller(),r.structure().plan().slots().keySet(),DoorCatalog.patches(r)));
            }catch(java.io.IOException|IllegalArgumentException e) {return Optional.empty();}
        });
    }
    @Override public Optional<PaintCell> select(PaintQuery query,PaintTarget target) {
        var view=AssemblyDoorBlock.view(query.level(),query.hit().getBlockPos());if(view.isEmpty())return Optional.empty();
        var hit=DoorHit.pick(view.get(),query.from(),query.to());
        if(hit.isEmpty()||hit.get().ambiguous()||hit.get().leaf()!=null||hit.get().surface()==null)return Optional.empty();
        var obstacle=query.level().clip(new ClipContext(query.from(),query.to(),ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,query.player()));
        if(obstacle.getType()!=HitResult.Type.MISS&&query.from().distanceToSqr(obstacle.getLocation())+1e-8<hit.get().distanceSquared())return Optional.empty();
        PaintCell result=null;
        for(var patch:target.surfaces())if(patch.id().startsWith(hit.get().surface()+"/")) {
            if(query.to().subtract(query.from()).dot(PaintFrame.vector(patch.frame().normal()))>=0)continue;
            var cell=patch.cellAt(hit.get().location());if(cell.isEmpty())continue;
            if(result!=null)return Optional.empty();result=cell.get();
        }
        return Optional.ofNullable(result);
    }
    @Override public boolean authorize(PaintQuery query,PaintTarget target,Action action,Set<PaintCell> cells) {
        var live=resolve(query);if(live.isEmpty()||!same(live.get(),target)||!cells.stream().allMatch(target::allows))return false;
        if(query.player().isSpectator()||!query.player().mayBuild())return false;
        for(var pos:target.permissionPositions())if(!query.level().hasChunkAt(pos)||!query.level().mayInteract(query.player(),pos)
                ||!query.player().mayUseItemAt(pos,query.hit().getDirection(),query.tool()))return false;
        // A rectangle may not paint through the current leaf, glazing or a foreign obstacle.
        if(action!=Action.CLEAN)for(var cell:cells) {
            var patch=target.surface(cell.surface());
            Vec3 center=patch.frame().point((cell.index()%patch.width()+.5)*.5,(cell.index()/patch.width()+.5)*.5);
            if(query.from().subtract(center).dot(PaintFrame.vector(patch.frame().normal()))<=0)return false;
            var view=AssemblyDoorBlock.view(query.level(),query.hit().getBlockPos()).orElseThrow();
            var hit=DoorHit.pick(view,query.from(),center.add(center.subtract(query.from()).normalize().scale(1e-5)));
            if(hit.isEmpty()||hit.get().ambiguous()||hit.get().leaf()!=null||!patch.id().startsWith(hit.get().surface()+"/")||hit.get().location().distanceToSqr(center)>1e-8)return false;
            var worldHit=query.level().clip(new ClipContext(query.from(),center,ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,query.player()));
            if(worldHit.getType()!=HitResult.Type.MISS&&query.from().distanceToSqr(worldHit.getLocation())+1e-8<query.from().distanceToSqr(center))return false;
        }
        return true;
    }
    private static boolean same(PaintTarget a,PaintTarget b) {return a.identity().equals(b.identity())&&a.revision()==b.revision()&&a.geometryKey().equals(b.geometryKey())&&a.permissionPositions().equals(b.permissionPositions())&&a.data().equals(b.data());}
    @Override public boolean commit(PaintQuery query,PaintTarget expected,Action action,PaintData replacement) {
        if(!(query.level() instanceof ServerLevel server))return false;
        var changed=new HashSet<PaintCell>();changed.addAll(expected.data().entries().keySet());changed.addAll(replacement.entries().keySet());
        changed.removeIf(c->expected.data().paint(c)==replacement.paint(c));
        if(!authorize(query,expected,action,changed))return false;
        var view=AssemblyDoorBlock.view(server,query.hit().getBlockPos());if(view.isEmpty())return false;
        try {
            var r=view.get().record();var layer=DoorPaintStore.get(server,r);
            DoorPaintStore.write(server,r,layer.next(expected,replacement));
            // Only packets/model invalidation; do not issue neighbor notifications or redstone callbacks.
            for(var pos:expected.permissionPositions()) {var state=server.getBlockState(pos);server.sendBlockUpdated(pos,state,state,net.minecraft.world.level.block.Block.UPDATE_CLIENTS);}
            return true;
        }catch(java.io.IOException|IllegalArgumentException|IllegalStateException e) {net.nicomar2009.lsmmod.LSMMod.LOGGER.error("Door paint commit rejected",e);return false;}
    }
}
