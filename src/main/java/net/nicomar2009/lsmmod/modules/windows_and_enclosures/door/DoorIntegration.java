package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;

import java.io.IOException;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.nicomar2009.lsmcore.door.Doors;
import net.nicomar2009.lsmcore.door.geometry.*;
import net.nicomar2009.lsmcore.door.integration.*;
import net.nicomar2009.lsmcore.door.model.*;
import net.nicomar2009.lsmcore.door.server.DoorService;
import net.nicomar2009.paintingmod.api.*;

/** Owner world writes only; Core owns transactions, membership, poses, permissions and the single drop claim. */
@EventBusSubscriber(modid="lsmmod")
public final class DoorIntegration implements DoorAdapter {
    private static final ThreadLocal<PaintData> PLACEMENT_PAINT=new ThreadLocal<>();
    private static final Set<UUID> POWER_BUSY=new HashSet<>();
    public static void setup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(()->{
            var adapter=new DoorIntegration();
            DoorCatalog.STYLES.keySet().forEach(id->Doors.register(DoorCatalog.id(id),adapter));
            // Register every carrier, including swept proxies: tools must never open/carve these blocks.
            PaintProviders.register(DoorCatalog.id("assembly_door_paint"),new DoorPainting(),blocks());
        });
    }
    public static Block[] blocks() {return DoorCatalog.STYLES.keySet().stream().map(id->net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(DoorCatalog.id(id))).toArray(Block[]::new);}
    private static Block block(DoorRecord r) {return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(r.structure().definition().type());}
    public static void feedback(ServerPlayer player,Exception e) {
        player.sendOverlayMessage(Component.translatable(e instanceof IOException?"message.lsmmod.assembly_door.storage":"message.lsmmod.assembly_door.blocked"));
        if(e instanceof IOException)net.nicomar2009.lsmmod.LSMMod.LOGGER.error("Door persistence failed",e);
    }
    public static net.minecraft.world.InteractionResult place(BlockPlaceContext context,AssemblyDoorBlock block) {
        if(!(context.getPlayer() instanceof ServerPlayer player))return net.minecraft.world.InteractionResult.SUCCESS;
        try {
            var definition=DoorCatalog.STYLES.get(block.style()).definition();
            // Sneak-place reflects local X before rotating. Anchor remains the clicked bottom corner.
            int turns=switch(player.getDirection().getOpposite()) {case EAST->1;case SOUTH->2;case WEST->3;default->0;};
            var paintTag=context.getItemInHand().getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
            var schema=DoorRecord.placing(definition,new DoorPlacement(BlockPos.ZERO,new DoorOrientation(0,false)));
            var paint=paintTag.contains("assembly_door_paint")?PaintData.load(paintTag.getCompoundOrEmpty("assembly_door_paint"),DoorCatalog.patches(schema)):PaintData.EMPTY;
            PLACEMENT_PAINT.set(paint);
            var placement=new DoorPlacement(context.getClickedPos(),new DoorOrientation(turns,player.isShiftKeyDown()));
            var result=DoorService.place(player,definition,placement,context);
            if(result.accepted()) {
                if(!player.isCreative())context.getItemInHand().shrink(1);
                var anchor=DoorTransform.box(placement,null,false,new DoorBox(0,0,0,1,1,1));
                power(player.level(),new BlockPos((int)Math.round(anchor.minX()),(int)Math.round(anchor.minY()),(int)Math.round(anchor.minZ())));
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
        } catch(java.io.UncheckedIOException e) {feedback(player,e.getCause());} catch(IOException|IllegalArgumentException e) {feedback(player,e);}finally {PLACEMENT_PAINT.remove();}
        return net.minecraft.world.InteractionResult.FAIL;
    }
    @Override public boolean matches(ServerLevel level,DoorRecord r,DoorPlan.Slot slot) {
        return slot!=null&&level.getBlockState(slot.position()).is(block(r))&&level.getBlockEntity(slot.position()) instanceof DoorPieceEntity piece&&piece.matches(r,slot.part());
    }
    @Override public boolean canPlace(ServerPlayer player,DoorRecord r,DoorPlan.Slot slot) {
        return player.level().getFluidState(slot.position()).isEmpty();
    }
    @Override public void place(ServerLevel level,DoorRecord r,DoorPlan.Slot slot) {
        if(matches(level,r,slot))return;
        if(!level.getBlockState(slot.position()).canBeReplaced())throw new IllegalArgumentException("space_occupied");
        if(!DoorCatalog.patches(r).isEmpty())try {DoorPaintStore.initialize(level,r,Optional.ofNullable(PLACEMENT_PAINT.get()).orElse(PaintData.EMPTY));}catch(IOException e) {throw new java.io.UncheckedIOException(e);}
        level.setBlock(slot.position(),block(r).defaultBlockState(),Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
        if(!(level.getBlockEntity(slot.position()) instanceof DoorPieceEntity piece))throw new IllegalStateException("missing_carrier");
        piece.stamp(r,slot.part());level.sendBlockUpdated(slot.position(),piece.getBlockState(),piece.getBlockState(),Block.UPDATE_CLIENTS);
    }
    @Override public void removeWithoutDrops(ServerLevel level,DoorRecord r,DoorPlan.Slot slot) {
        if(matches(level,r,slot))level.setBlock(slot.position(),Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
    }
    @Override public boolean authorize(ServerLevel level,ServerPlayer actor,DoorAction action,DoorRecord r,Set<BlockPos> affected) {
        // Null actor is Core-authorized redstone or integrity cleanup; Core posts DoorPermissionEvent.
        if(r.phase()!=DoorRecord.Phase.PLACING&&r.removal()!=DoorRecord.Removal.FAILED_PLACEMENT&&!r.dropsClaimed()&&!DoorCatalog.patches(r).isEmpty()) {
            try {DoorPaintStore.get(level,r);}catch(IOException|IllegalArgumentException e) {return false;}
        }
        return actor==null||affected.stream().filter(level::hasChunkAt).allMatch(p->level.mayInteract(actor,p));
    }
    @Override public boolean canMove(ServerLevel level,DoorRecord r,DoorMotion motion) {
        for(var box:motion.conservativeSweep()) {
            // Same rule for opening and closing; touching the boundary is allowed, penetration is not.
            if(!level.getEntities((net.minecraft.world.entity.Entity)null,box.aabb().deflate(1e-6),e->e.isAlive()&&!e.isSpectator()).isEmpty())return false;
        }
        // Discrete Core physics: reject overlap in the target pose with the current other leaf.
        // The closed leaves share an intentional zero-width seam; no entity pushing is performed.
        for(var surface:r.structure().definition().surfaces()) {
            if(surface.leaf()==null||surface.leaf().equals(motion.leaf()))continue;
            var leaf=r.structure().definition().leaf(surface.leaf());
            for(var box:surface.collision()) {
                var other=DoorTransform.box(r.structure().placement(),leaf,r.leaves().get(leaf.id()).open(),box).aabb();
                if(motion.targetCollision().stream().anyMatch(b->b.aabb().deflate(1e-6).intersects(other)))return false;
            }
        }
        return true;
    }
    public static void invalidate(ServerLevel level,DoorRecord r) {
        for(var pos:r.structure().plan().slots().keySet())if(level.hasChunkAt(pos)) {
            var state=level.getBlockState(pos);level.sendBlockUpdated(pos,state,state,Block.UPDATE_CLIENTS);
            level.updateNeighborsAt(pos,state.getBlock());
        }
    }
    @Override public void changed(ServerLevel level,DoorRecord before,DoorRecord after) {
        var r=after==null?before:after;invalidate(level,r);
        if(before!=null&&after!=null)for(var entry:after.leaves().entrySet())if(entry.getValue().open()!=before.leaves().get(entry.getKey()).open())
            level.playSound(null,r.structure().plan().controller(),entry.getValue().open()?SoundEvents.WOODEN_DOOR_OPEN:SoundEvents.WOODEN_DOOR_CLOSE,SoundSource.BLOCKS,.7F,1F);
        if(after==null&&!DoorCatalog.patches(r).isEmpty())try {DoorPaintStore.remove(level,r.id());}catch(IOException e) {net.nicomar2009.lsmmod.LSMMod.LOGGER.error("Cannot retire door paint",e);}
    }
    @Override public void drops(ServerLevel level,DoorRecord removed) {
        ItemStack stack=new ItemStack(block(removed));
        if(!DoorCatalog.patches(removed).isEmpty())try {
            var tag=new net.minecraft.nbt.CompoundTag();tag.put("assembly_door_paint",DoorPaintStore.get(level,removed).data().save(DoorCatalog.patches(removed)));
            stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        }catch(IOException|IllegalArgumentException e) {throw new IllegalStateException("Refusing to discard unavailable paint",e);}
        Block.popResource(level,removed.structure().plan().controller(),stack);
    }
    /** Input cells: all three rows of column 0 => doorright; column 2 => doorleft.
     * Center column is neutral in the double. Singles use every required (closed-envelope) cell.
     * Sweep proxies never supply redstone. Placement orientation maps these cells through Core.
     */
    public static void power(ServerLevel level,BlockPos pos) {
        var view=DoorService.at(level,pos);if(view.isEmpty())return;var r=view.get().record();
        if(r.phase()!=DoorRecord.Phase.ACTIVE||!POWER_BUSY.add(r.id()))return;
        try {
            boolean doubled=r.structure().definition().leaves().size()==2;
            for(var leaf:r.structure().definition().leaves()) {
                boolean powered=false;
                for(var slot:r.structure().plan().slots().values()) {
                    if(slot.proxy()||doubled&&!slot.part().startsWith(leaf.id().equals("doorright")?"cell_0_":"cell_2_"))continue;
                    if(!level.hasChunkAt(slot.position()))return;
                    powered|=level.hasNeighborSignal(slot.position());
                }
                try {DoorService.power(level,r.id(),leaf.id(),powered);}catch(IOException|IllegalArgumentException e) {
                    // Obstructed edge keeps old power memory; another neighbor change can retry it.
                    if(e instanceof IOException)net.nicomar2009.lsmmod.LSMMod.LOGGER.error("Door redstone failed",e);
                }
            }
        }finally {POWER_BUSY.remove(r.id());}
    }
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.LOWEST) public static void breaking(BreakBlockEvent event) {
        if(!(event.getState().getBlock() instanceof AssemblyDoorBlock)||!(event.getPlayer() instanceof ServerPlayer player))return;
        // Always suppress vanilla per-piece removal, including rejected Core transactions.
        event.setCanceled(true);event.setNotifyClient(true);var view=DoorService.at(player.level(),event.getPos());
        if(view.isPresent())try {DoorService.breakPiece(player,event.getPos(),view.get().record().revision());}catch(IOException|IllegalArgumentException e) {feedback(player,e);}
    }
}
