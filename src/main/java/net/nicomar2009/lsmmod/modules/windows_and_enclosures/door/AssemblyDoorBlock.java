package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import net.nicomar2009.lsmcore.door.model.DoorView;
import net.nicomar2009.lsmcore.door.server.DoorService;

/** One carrier type per style, also used in reserved sweep slots. No OPEN or second controller. */
public final class AssemblyDoorBlock extends Block implements EntityBlock {
    public interface ClientView {Optional<DoorView> at(BlockPos pos);}
    public static ClientView client=pos->Optional.empty();
    public static java.util.function.Predicate<BlockPos> clientReady=pos->false;
    private final String style;
    public AssemblyDoorBlock(Properties properties,String style) {super(properties);this.style=style;}
    public String style() {return style;}
    public static Optional<DoorView> view(BlockGetter level,BlockPos pos) {
        if(!(level.getBlockEntity(pos) instanceof DoorPieceEntity piece))return Optional.empty();
        // Navigation/collision regions wrap the real level; use the linked entity to resolve it.
        var view=piece.getLevel() instanceof ServerLevel server?DoorService.at(server,pos):client.at(pos);
        return view.filter(v->piece.matches(v.record(),v.record().structure().plan().slots().get(pos).part()));
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {return new DoorPieceEntity(pos,state);}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return view(level,pos).map(v->v.geometry().at(pos).outline()).orElse(Shapes.block());
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return view(level,pos).map(v->v.geometry().at(pos).solid()).orElse(Shapes.block());
    }
    @Override protected VoxelShape getOcclusionShape(BlockState state) {return Shapes.empty();}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(player instanceof ServerPlayer server)view(level,pos).ifPresent(v->{
            try {DoorService.manual(server,v.record().id(),pos,v.record().revision());}
            catch(java.io.IOException|IllegalArgumentException e) {DoorIntegration.feedback(server,e);}
        });
        return InteractionResult.SUCCESS;
    }
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,net.minecraft.world.level.redstone.Orientation orientation,boolean moved) {
        if(level instanceof ServerLevel server)DoorIntegration.power(server,pos);
    }
    @Override protected void affectNeighborsAfterRemoval(BlockState state,ServerLevel level,BlockPos pos,boolean moved) {
        super.affectNeighborsAfterRemoval(state,level,pos,moved);
        try {DoorService.pieceRemoved(level,pos);}catch(java.io.IOException|IllegalArgumentException e) {net.nicomar2009.lsmmod.LSMMod.LOGGER.error("Door recovery failed",e);}
    }
}
