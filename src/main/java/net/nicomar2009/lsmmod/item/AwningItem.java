package net.nicomar2009.lsmmod.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.nicomar2009.lsmmod.block.AwningStructure;
import net.nicomar2009.lsmmod.block.AwningBlock;
import java.util.function.Supplier;

/** Cloth is placed only by right-clicking a complete, free suspension bar. */
public class AwningItem extends Item {
    private final Supplier<AwningBlock> awning;
    public AwningItem(Properties properties, Supplier<AwningBlock> awning) { super(properties); this.awning = awning; }
    @Override
    public InteractionResult useOn(UseOnContext context) { return AwningStructure.place(context, awning.get()); }
}
