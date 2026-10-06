package net.nicomar2009.lsmmod.block;

import net.nicomar2009.lsmmod.item.AwningSupportItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.registry.ModBlocks;
import net.nicomar2009.lsmmod.registry.ModItems;

/** Server-side preflight, atomic placement and bounded ownership-based dismantling. */
public final class AwningStructure {
    public static final int MAX_GAP = 48;
    private static final Set<BlockPos> REMOVING = new HashSet<>();
    private record Cell(BlockPos pos, BlockState state) {}
    private AwningStructure() {}

    private static Direction direction(BlockPos a, BlockPos b) {
        if (a.getX() != b.getX()) return b.getX() > a.getX() ? Direction.EAST : Direction.WEST;
        return b.getZ() > a.getZ() ? Direction.SOUTH : Direction.NORTH;
    }
    private static boolean validPair(BlockPos a, BlockPos b) {
        int distance = Math.abs(a.getX()-b.getX()) + Math.abs(a.getZ()-b.getZ());
        return (a.getX() == b.getX() || a.getZ() == b.getZ()) && distance >= 2
                && distance <= MAX_GAP+1 && Math.abs(a.getY()-b.getY()) <= 1;
    }
    private static boolean loaded(Level level, BlockPos pos) {
        return !level.isOutsideBuildHeight(pos) && level.hasChunkAt(pos) && level.getWorldBorder().isWithinBounds(pos);
    }
    private static List<BlockPos> bar(BlockPos root, Direction axis, int width) {
        List<BlockPos> cells = new ArrayList<>(width);
        for (int c=0;c<width;c++) cells.add(AwningSupportBlock.cell(root, axis, c));
        return cells;
    }
    private static boolean completeBar(Level level, BlockPos root, Direction axis, int width) {
        for (int c=0;c<width;c++) {
            BlockPos pos = AwningSupportBlock.cell(root, axis, c);
            if (!loaded(level,pos)) return false;
            BlockState state = level.getBlockState(pos);
            if (!state.is(ModBlocks.AWNING_SUPPORT.get()) || state.getValue(AwningSupportBlock.COLUMN) != c
                    || state.getValue(AwningSupportBlock.FACING) != axis
                    || state.getValue(AwningSupportBlock.WIDTH) != width
                    || !(level.getBlockEntity(pos) instanceof AwningBlockEntity part)
                    || part.width() != width || !part.supportRoot().equals(root)) return false;
        }
        return true;
    }
    private static double height(BlockPos a, BlockPos b, double t) {
        // Attachment at 14 pixels; only six pixels of sag at the midpoint.
        return a.getY() + 14.0/16 + (b.getY()-a.getY())*t - 4*t*(1-t)*(6.0/16);
    }
    private static List<Cell> cloth(BlockPos a, BlockPos b, int width, AwningBlock awning) {
        List<Cell> cells = new ArrayList<>();
        Direction facing = direction(a,b);
        int gap = Math.abs(a.getX()-b.getX()) + Math.abs(a.getZ()-b.getZ()) - 1;
        for (int row=1;row<=gap;row++) {
            // Snap to two-pixel steps, keeping adjacent segments' boundary heights identical.
            double h1 = Math.round(height(a,b,(row-1.0)/gap)*8)/8.0;
            double h2 = Math.round(height(a,b,row/(double)gap)*8)/8.0;
            int base = (int)Math.floor(Math.min(h1,h2)-1.0/16);
            int start = (int)Math.round((h1-base)*8), end = (int)Math.round((h2-base)*8);
            BlockState state = awning.defaultBlockState().setValue(AwningBlock.FACING,facing)
                    .setValue(AwningBlock.START,start).setValue(AwningBlock.END,end);
            for (int c=0;c<width;c++) {
                BlockPos pos = AwningSupportBlock.cell(a.relative(facing,row),facing,c);
                pos = new BlockPos(pos.getX(),base,pos.getZ());
                cells.add(new Cell(pos,state));
                if (Math.max(start,end)>8) cells.add(new Cell(pos.above(),state.setValue(AwningBlock.UPPER,true)));
            }
        }
        return cells;
    }
    private static InteractionResult fail(Player player,String key) {
        if (player != null) player.sendOverlayMessage(Component.translatable("message.lsmmod.awning."+key));
        return InteractionResult.FAIL;
    }
    public static InteractionResult place(UseOnContext context, AwningBlock awning) {
        Level level = context.getLevel(); Player player = context.getPlayer();
        BlockPos clicked = context.getClickedPos(); BlockState clickedState = level.getBlockState(clicked);
        if (!clickedState.is(ModBlocks.AWNING_SUPPORT.get())) return fail(player,"support_only");
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (player == null || !player.mayBuild()) return fail(player,"permission");
        Direction axis = clickedState.getValue(AwningSupportBlock.FACING);
        BlockPos root = AwningSupportBlock.root(clicked,clickedState);
        int width = clickedState.getValue(AwningSupportBlock.WIDTH);
        if (!completeBar(level,root,axis,width)) return fail(player,"incomplete");
        for (BlockPos pos:bar(root,axis,width))
            if (((AwningBlockEntity)level.getBlockEntity(pos)).linked()) return fail(player,"occupied");
        Direction preferred = context.getClickedFace().getAxis()==axis.getAxis() ? context.getClickedFace() : axis;
        BlockPos partner = null;
        outer: for (int distance=2;distance<=MAX_GAP+1;distance++) {
            for (Direction search:new Direction[]{preferred,preferred.getOpposite()}) {
                for (int dy:new int[]{0,1,-1}) {
                    BlockPos candidate = root.relative(search,distance).offset(0,dy,0);
                    if (loaded(level,candidate) && completeBar(level,candidate,axis,width)) { partner=candidate; break outer; }
                }
            }
        }
        if (partner == null) return fail(player,"no_partner");
        for (BlockPos pos:bar(partner,axis,width))
            if (((AwningBlockEntity)level.getBlockEntity(pos)).linked()) return fail(player,"occupied");
        BlockPos a=root,b=partner;
        if (direction(a,b)!=axis) { a=partner; b=root; }
        List<Cell> cells=cloth(a,b,width,awning);
        List<BlockPos> all=new ArrayList<>(bar(a,axis,width)); all.addAll(bar(b,axis,width));
        cells.forEach(cell->all.add(cell.pos));
        for (BlockPos pos:all) {
            if (!loaded(level,pos)) return fail(player,"unloaded");
            if (!level.mayInteract(player,pos) || !player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand())) return fail(player,"permission");
        }
        for (Cell cell:cells)
            if (!level.getBlockState(cell.pos).isAir() || !level.getFluidState(cell.pos).isEmpty()) return fail(player,"blocked");
        // No writes or item consumption occur until every cell has passed preflight.
        List<BlockPos> placed=new ArrayList<>();
        for (Cell cell:cells) {
            if (!level.setBlock(cell.pos,cell.state,Block.UPDATE_CLIENTS)
                    || !(level.getBlockEntity(cell.pos) instanceof AwningBlockEntity)) {
                // Even the last write may have succeeded without its required entity.
                if (level.getBlockState(cell.pos).is(awning)) placed.add(cell.pos);
                for (BlockPos pos:placed) level.setBlock(pos,Blocks.AIR.defaultBlockState(),Block.UPDATE_CLIENTS|Block.UPDATE_SUPPRESS_DROPS);
                return fail(player,"blocked");
            }
            placed.add(cell.pos);
        }
        for (BlockPos pos:all) ((AwningBlockEntity)level.getBlockEntity(pos)).link(a,b,width,awning);
        for (Cell cell:cells) level.updateNeighborsAt(cell.pos,awning);
        if (!player.isCreative()) context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }

    private static void removeCloth(Level level, AwningBlockEntity part, boolean drop) {
        if (!part.linked()) return;
        int width=part.width();
        AwningBlock awning=part.awning();
        BlockPos a=part.first(),b=part.second();
        if (!validPair(a,b)) { part.unlink(); return; }
        if (!REMOVING.add(a)) return;
        try {
            Direction axis=direction(a,b);
            for (BlockPos root:new BlockPos[]{a,b}) {
                for (BlockPos pos:bar(root,axis,width)) {
                    if (loaded(level,pos) && level.getBlockEntity(pos) instanceof AwningBlockEntity other && other.belongsTo(a,b)) other.unlink();
                }
            }
            for (Cell cell:cloth(a,b,width,awning)) {
                if (!loaded(level,cell.pos)) continue;
                if (level.getBlockState(cell.pos).is(awning)
                        && level.getBlockEntity(cell.pos) instanceof AwningBlockEntity other && other.belongsTo(a,b)) {
                    other.unlink();
                    level.setBlock(cell.pos,Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
                }
            }
            if (drop) Block.popResource(level,part.getBlockPos(),new ItemStack(ModItems.awningVariant(awning.variant())));
        } finally { REMOVING.remove(a); }
    }
    public static void removed(Level level, AwningBlockEntity part, boolean drop) {
        if (part.removalHandled) return;
        part.removalHandled = true;
        BlockState old=part.getBlockState();
        removeCloth(level,part,drop);
        if (!old.is(ModBlocks.AWNING_SUPPORT.get())) return;
        int width=part.width();
        BlockPos root=part.supportRoot(); Direction axis=old.getValue(AwningSupportBlock.FACING);
        if (!REMOVING.add(root)) return;
        try {
            for (BlockPos pos:bar(root,axis,width)) {
                if (loaded(level,pos) && level.getBlockState(pos).is(ModBlocks.AWNING_SUPPORT.get())
                        && level.getBlockEntity(pos) instanceof AwningBlockEntity other && other.supportRoot().equals(root)) {
                    other.removalHandled = true;
                    other.unlink();
                    // The caller removes the clicked cell. Remove its other width-1 companions here.
                    if (!pos.equals(part.getBlockPos())) level.setBlock(pos,Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
                }
            }
            if (drop) Block.popResource(level,part.getBlockPos(),AwningSupportItem.returnedSupport(width));
        } finally { REMOVING.remove(root); }
    }
    public static void tick(Level level, AwningBlockEntity part) {
        if ((level.getGameTime()+part.getBlockPos().asLong())%20 != 0) return;
        BlockState state=part.getBlockState();
        int width=part.width();
        AwningBlock awning=part.awning();
        if (state.is(ModBlocks.AWNING_SUPPORT.get())) {
            Direction axis=state.getValue(AwningSupportBlock.FACING);
            for (BlockPos pos:bar(part.supportRoot(),axis,width)) if (!loaded(level,pos)) return;
            if (!completeBar(level,part.supportRoot(),axis,width)) {
                removed(level,part,false);
                level.setBlock(part.getBlockPos(),Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
                return;
            }
        }
        if (!part.linked()) {
            if (state.is(awning))
                level.setBlock(part.getBlockPos(),Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
            return;
        }
        BlockPos a=part.first(),b=part.second();
        if (!validPair(a,b)) { part.unlink(); return; }
        Direction axis=direction(a,b);
        for (BlockPos root:new BlockPos[]{a,b}) for (BlockPos pos:bar(root,axis,width)) if (!loaded(level,pos)) return;
        if (!completeBar(level,a,axis,width) || !completeBar(level,b,axis,width)) { removeCloth(level,part,false); return; }
        for (BlockPos root:new BlockPos[]{a,b}) for (BlockPos pos:bar(root,axis,width)) {
            if (!(level.getBlockEntity(pos) instanceof AwningBlockEntity owner) || owner.width()!=width || !owner.belongsTo(a,b)) {
                removeCloth(level,part,false); return;
            }
        }
        // One endpoint controller checks the fabric; all other cells only check endpoint ownership.
        if (!part.getBlockPos().equals(a)) return;
        for (Cell cell:cloth(a,b,width,awning)) {
            if (!loaded(level,cell.pos)) return;
            if (!level.getBlockState(cell.pos).equals(cell.state)
                    || !(level.getBlockEntity(cell.pos) instanceof AwningBlockEntity other) || other.width()!=width || !other.belongsTo(a,b)) {
                removeCloth(level,part,true); return;
            }
        }
    }
}
