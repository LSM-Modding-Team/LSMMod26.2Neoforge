package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.InteractionResult;

/** One item places the entire Core structure; BlockItem never places an individual carrier. */
public final class AssemblyDoorItem extends BlockItem {
    public AssemblyDoorItem(AssemblyDoorBlock block,Properties properties) {super(block,properties);}
    @Override public InteractionResult place(BlockPlaceContext context) {return DoorIntegration.place(context,(AssemblyDoorBlock)getBlock());}
}
