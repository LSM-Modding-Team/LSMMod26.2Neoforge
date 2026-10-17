package net.nicomar2009.lsmmod.modules.balls.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> FOOTBALL_BASKETBALL_GOAL =
            ITEMS.registerSimpleBlockItem(ModBlocks.FOOTBALL_BASKETBALL_GOAL);

    public static final DeferredItem<Item> PLASTIC_BALL = ball("plastic_ball", net.nicomar2009.lsmmod.modules.balls.ball.BallKind.PLASTIC);

    public static final DeferredItem<Item> FOOTBALL_BALL = ball("football_ball", net.nicomar2009.lsmmod.modules.balls.ball.BallKind.FOOTBALL);

    public static final DeferredItem<Item> BASKETBALL_BALL = ball("basketball_ball", net.nicomar2009.lsmmod.modules.balls.ball.BallKind.BASKETBALL);

    public static final DeferredItem<Item> VOLLEYBALL_BALL = ball("volleyball_ball", net.nicomar2009.lsmmod.modules.balls.ball.BallKind.VOLLEYBALL);

    private static DeferredItem<Item> ball(String id, net.nicomar2009.lsmmod.modules.balls.ball.BallKind kind) {
        return ITEMS.registerItem(id, props -> new net.nicomar2009.lsmmod.modules.balls.ball.BallItem(props.stacksTo(16), kind));
    }

    private ModuleItems() {}
}
