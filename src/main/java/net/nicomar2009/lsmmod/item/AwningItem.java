package net.nicomar2009.lsmmod.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.nicomar2009.lsmmod.block.AwningStructure;

/** Cloth is placed only by right-clicking a complete, free suspension bar. */
public class AwningItem extends Item {
    public AwningItem(Properties properties) { super(properties); }
    @Override
    public InteractionResult useOn(UseOnContext context) { return AwningStructure.place(context); }
}
