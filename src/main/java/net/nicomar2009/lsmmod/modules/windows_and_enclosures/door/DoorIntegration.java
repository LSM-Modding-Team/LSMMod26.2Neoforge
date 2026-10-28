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

/** Owner world writes only; Core owns transactions, membership, poses, permissions and the single drop claim. */
@EventBusSubscriber(modid="lsmmod")
public final class DoorIntegration implements DoorAdapter {
    private static final ThreadLocal<net.minecraft.nbt.CompoundTag> PLACEMENT_DATA=new ThreadLocal<>();
    private static final Set<UUID> POWER_BUSY=new HashSet<>();
    public static void setup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(()->{
            var adapter=new DoorIntegration();
            DoorCatalog.STYLES.keySet().forEach(id->Doors.register(DoorCatalog.id(id),adapter));
        });
    }
    public static Block[] blocks() {return DoorCatalog.STYLES.keySet().stream().map(id->net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(DoorCatalog.id(id))).toArray(Block[]::new);}
    private static Block block(DoorRecord r) {return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(r.structure().definition().type());}
    public static void feedback(ServerPlayer player,Exception e) {
        String code=e instanceof IOException?"storage_unavailable":e.getMessage();
        String key=switch(code==null?"":code) {
            case "space_occupied","occupied_structure" -> "occupied";
            case "permission" -> "permission";
            case "incomplete","pending_chunks" -> "chunks";
            case "storage_unavailable","unknown_structure","structure_broken","structure_pending","not_synchronized" -> "unavailable";
            default -> "blocked";
        };
        player.sendOverlayMessage(Component.translatable("message.lsmmod.assembly_door."+key));
        if(e instanceof IOException)net.nicomar2009.lsmmod.LSMMod.LOGGER.error("Door persistence failed",e);
    }
    public static net.minecraft.world.InteractionResult place(BlockPlaceContext context,AssemblyDoorBlock block) {
        if(!(context.getPlayer() instanceof ServerPlayer player))return net.minecraft.world.InteractionResult.SUCCESS;
        try {
            var definition=DoorCatalog.STYLES.get(block.style()).definition();
            // North-native front follows the player's direction, as with a vanilla door.
            var facing=player.getDirection();
            int turns=switch(facing) {case EAST->1;case SOUTH->2;case WEST->3;default->0;};
            var style=DoorCatalog.STYLES.get(block.style());
            var anchor=anchor(definition);
            boolean mirror=chooseMirror(context,definition,anchor,turns);
            var paintTag=context.getItemInHand().getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
            PLACEMENT_DATA.set(paintTag);
            var placement=DoorPlacement.anchored(context.getClickedPos(),anchor,new DoorOrientation(turns,mirror));
            var result=DoorService.place(player,definition,placement,context);
            if(result.accepted()) {
                if(!player.isCreative())context.getItemInHand().shrink(1);
                power(player.level(),context.getClickedPos());
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
        } catch(java.io.UncheckedIOException e) {feedback(player,e.getCause());} catch(IOException|IllegalArgumentException e) {feedback(player,e);}finally {PLACEMENT_DATA.remove();}
        return net.minecraft.world.InteractionResult.FAIL;
    }
    /** Singles: bottom hinge cell. Double: local column 0, the doorright exterior corner. */
    public static BlockPos anchor(DoorDefinition definition) {
        if(definition.leaves().size()==2)return BlockPos.ZERO;
        var pivot=definition.leaves().getFirst().pivot();
        return new BlockPos((int)Math.floor(pivot.x()),0,(int)Math.floor(pivot.z()));
    }
    private static boolean chooseMirror(BlockPlaceContext context,DoorDefinition definition,BlockPos anchor,int turns) {
        var level=context.getLevel();int[] scores=new int[2];
        for(int m=0;m<2;m++) {
            var p=DoorPlacement.anchored(context.getClickedPos(),anchor,new DoorOrientation(turns,m==1));
            for(var cell:definition.cells()) {
                var o=cell.offset();var b=DoorTransform.box(p,null,false,new DoorBox(o.getX(),o.getY(),o.getZ(),o.getX()+1,o.getY()+1,o.getZ()+1));
                var pos=new BlockPos((int)Math.round(b.minX()),(int)Math.round(b.minY()),(int)Math.round(b.minZ()));
                if(!level.hasChunkAt(pos)||!level.getBlockState(pos).canBeReplaced(context))scores[m]++;
            }
        }
        boolean selected;
        if(scores[0]!=scores[1])selected=scores[1]<scores[0];
        else {
            var relative=context.getClickLocation().subtract(net.minecraft.world.phys.Vec3.atLowerCornerOf(context.getClickedPos()));
            var across=new DoorOrientation(turns,false).apply(new DoorPoint(1,0,0));
            selected=(relative.x-.5)*across.x()+(relative.z-.5)*across.z()<0;
        }
        return context.getPlayer().isShiftKeyDown()?!selected:selected;
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
        if(DoorCatalog.hasPaintableWall(r))try {DoorPaintStore.initialize(level,r,PLACEMENT_DATA.get());}catch(IOException e) {throw new java.io.UncheckedIOException(e);}
        level.setBlock(slot.position(),block(r).defaultBlockState(),Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
        if(!(level.getBlockEntity(slot.position()) instanceof DoorPieceEntity piece))throw new IllegalStateException("missing_carrier");
        piece.stamp(r,slot.part());level.sendBlockUpdated(slot.position(),piece.getBlockState(),piece.getBlockState(),Block.UPDATE_CLIENTS);
    }
    @Override public void removeWithoutDrops(ServerLevel level,DoorRecord r,DoorPlan.Slot slot) {
        if(matches(level,r,slot))level.setBlock(slot.position(),Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
    }
    @Override public boolean authorize(ServerLevel level,ServerPlayer actor,DoorAction action,DoorRecord r,Set<BlockPos> affected) {
        // Null actor is Core-authorized redstone or integrity cleanup; Core posts DoorPermissionEvent.
        return actor==null||affected.stream().filter(level::hasChunkAt).allMatch(p->level.mayInteract(actor,p));
    }
    @Override public net.minecraft.world.entity.EntityType<net.nicomar2009.lsmcore.door.entity.DoorBody> bodyType() {return net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleDoorEntities.BODY.get();}
    @Override public boolean allowsEntityUse(ServerPlayer player,net.minecraft.world.InteractionHand hand) {
        var item=player.getItemInHand(hand).getItem();
        return !(item instanceof net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem
                ||item instanceof net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolSandpaperItem
                ||item instanceof net.nicomar2009.lsmmod.modules.school_supplies.item.PaintCleanerItem
                ||item instanceof net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolHammerItem);
    }
    @Override public boolean canMove(ServerLevel level,DoorRecord r,DoorMotion motion) {return true;}
    @Override public Optional<DoorDefinition> upgrade(DoorRecord r) {
        var style=DoorCatalog.STYLES.get(r.structure().definition().type().getPath());
        if(!r.structure().definition().type().getNamespace().equals("lsmmod")||style==null||r.structure().definition().schema()!=1)return Optional.empty();
        // Preserve unknown/custom snapshots: only the exact known v1 definitions can be upgraded.
        var old=r.structure().definition();var next=style.definition();
        if(!old.cells().equals(next.cells())||!old.envelope().equals(next.envelope())||!old.controller().equals(next.controller())||old.removalPolicy()!=next.removalPolicy()||old.motionPolicy()!=DoorDefinition.MotionPolicy.RESTRICTIVE||!DoorCatalog.knownLegacy(r))return Optional.empty();
        return Optional.of(next);
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
        // Retain archived surface files: removing the external provider must not erase saved data.
    }
    @Override public void drops(ServerLevel level,DoorRecord removed) {
        ItemStack stack=new ItemStack(block(removed));
        if(DoorCatalog.hasPaintableWall(removed))try {
            var tag=DoorPaintStore.itemData(level,removed);
            if(!tag.isEmpty())stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        }catch(IOException e) {throw new IllegalStateException("Refusing to discard archived door data",e);}
        Block.popResource(level,removed.structure().plan().controller(),stack);
    }
    /** Input cells: all three rows of column 0 => doorright; column 2 => doorleft.
     * Center column is neutral in the double. Singles use every required (closed-envelope) cell.
     * Sweep proxies never supply redstone. Placement orientation maps these cells through Core.
     */
    public static void power(ServerLevel level,BlockPos pos) {
        Optional<DoorView> view;try {view=DoorService.at(level,pos);}catch(IllegalArgumentException unavailable) {return;}
        if(view.isEmpty())return;var r=view.get().record();
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
        event.setCanceled(true);event.setNotifyClient(true);
        try {var view=DoorService.at(player.level(),event.getPos());
            if(view.isPresent())DoorService.breakPiece(player,event.getPos(),view.get().record().revision());
            else throw new IllegalArgumentException("unknown_structure");
        }catch(IOException|IllegalArgumentException e) {feedback(player,e);}
    }
}
