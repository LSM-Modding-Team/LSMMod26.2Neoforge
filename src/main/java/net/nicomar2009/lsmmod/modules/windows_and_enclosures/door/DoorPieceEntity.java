package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;

import java.util.UUID;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.*;
import net.neoforged.neoforge.model.data.*;
import net.nicomar2009.lsmcore.door.model.*;
import net.nicomar2009.lsmcore.door.server.DoorService;
import net.nicomar2009.paintingmod.api.PaintLayer;

/** Saved UUID/slot only. Paint in update packets is an immutable derived rendering snapshot. */
public final class DoorPieceEntity extends BlockEntity {
    public record Link(UUID owner,String slot,PaintLayer paint) {}
    public static final ModelProperty<Link> LINK=new ModelProperty<>();
    private UUID owner;private String slot="";private PaintLayer snapshot;
    public DoorPieceEntity(BlockPos pos,BlockState state) {super(net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleDoorEntities.PIECE.get(),pos,state);}
    public boolean matches(DoorRecord r,String part) {return r.id().equals(owner)&&slot.equals(part==null?"proxy":part);}
    public void stamp(DoorRecord r,String part) {owner=r.id();slot=part==null?"proxy":part;setChanged();}
    public UUID owner() {return owner;}
    public PaintLayer snapshot() {return snapshot;}
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);if(owner!=null)output.putString("door_owner",owner.toString());output.putString("door_slot",slot);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        UUID next=null;try {next=UUID.fromString(input.getStringOr("door_owner",""));}catch(IllegalArgumentException ignored) {}
        if(!java.util.Objects.equals(next,owner))snapshot=null;owner=next;slot=input.getStringOr("door_slot","");
        var tag=input.read("paint_snapshot",CompoundTag.CODEC);
        if(tag.isPresent()&&owner!=null&&getBlockState().getBlock() instanceof AssemblyDoorBlock b) {
            try {
                var r=DoorRecord.placing(DoorCatalog.STYLES.get(b.style()).definition(),new DoorPlacement(BlockPos.ZERO,new net.nicomar2009.lsmcore.door.geometry.DoorOrientation(0,false)));
                var layer=PaintLayer.load(tag.get(),owner,DoorCatalog.patches(r));
                if(snapshot==null||layer.revision()>=snapshot.revision())snapshot=layer;
            }catch(IllegalArgumentException e) {snapshot=null;}
        }
        if(level!=null&&level.isClientSide()) {
            requestModelDataUpdate();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),Block.UPDATE_CLIENTS);
        }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var tag=saveWithoutMetadata(registries);
        if(level instanceof net.minecraft.server.level.ServerLevel server) {
            java.util.Optional<DoorView> view;try {view=DoorService.at(server,worldPosition);}catch(IllegalArgumentException unavailable) {return tag;}
            if(view.isPresent()&&matches(view.get().record(),view.get().record().structure().plan().slots().get(worldPosition).part())&&!DoorCatalog.patches(view.get().record()).isEmpty()) {
                try {tag.put("paint_snapshot",DoorPaintStore.get(server,view.get().record()).save(DoorCatalog.patches(view.get().record())));}
                catch(java.io.IOException|IllegalArgumentException e) {net.nicomar2009.lsmmod.LSMMod.LOGGER.error("Door paint unavailable",e);}
            }
        }
        return tag;
    }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() {return ClientboundBlockEntityDataPacket.create(this);}
    @Override public ModelData getModelData() {return owner==null?ModelData.EMPTY:ModelData.builder().with(LINK,new Link(owner,slot,snapshot)).build();}
}
