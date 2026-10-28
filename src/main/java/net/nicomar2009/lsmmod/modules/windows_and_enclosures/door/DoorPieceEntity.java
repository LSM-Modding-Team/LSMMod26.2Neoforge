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

/** Saved structure UUID/slot only; surface archives remain in their original server files. */
public final class DoorPieceEntity extends BlockEntity {
    public record Link(UUID owner,String slot) {}
    public static final ModelProperty<Link> LINK=new ModelProperty<>();
    private UUID owner;private String slot="";
    public DoorPieceEntity(BlockPos pos,BlockState state) {super(net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleDoorEntities.PIECE.get(),pos,state);}
    public boolean matches(DoorRecord r,String part) {return r.id().equals(owner)&&slot.equals(part==null?"proxy":part);}
    public void stamp(DoorRecord r,String part) {owner=r.id();slot=part==null?"proxy":part;setChanged();}
    public UUID owner() {return owner;}
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);if(owner!=null)output.putString("door_owner",owner.toString());output.putString("door_slot",slot);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        UUID next=null;try {next=UUID.fromString(input.getStringOr("door_owner",""));}catch(IllegalArgumentException ignored) {}
        owner=next;slot=input.getStringOr("door_slot","");
        if(level!=null&&level.isClientSide()) {
            requestModelDataUpdate();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),Block.UPDATE_CLIENTS);
        }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {return saveWithoutMetadata(registries);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() {return ClientboundBlockEntityDataPacket.create(this);}
    @Override public ModelData getModelData() {return owner==null?ModelData.EMPTY:ModelData.builder().with(LINK,new Link(owner,slot)).build();}
}
