package net.nicomar2009.lsmmod.modules.technology.media;
import net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules;
import net.nicomar2009.lsmmod.modules.technology.block.WallProjectorBlock;
import net.nicomar2009.lsmmod.block.TwoTallBlock;
import net.nicomar2009.lsmmod.modules.technology.block.TallSpeakerBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SpeakerBlock;
import net.nicomar2009.lsmmod.modules.technology.block.SchoolCabinetBlock;
import net.nicomar2009.lsmmod.modules.technology.block.ProjectorScreenBlock;
import net.nicomar2009.lsmmod.modules.technology.block.LaptopBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.GrayComputerDeskBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ComputerDeskBlock;
import net.nicomar2009.lsmmod.modules.technology.block.CeilingProjectorBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules.Role;

/** Canonical positions keep every cell of a desk, screen or projector in one circuit. */
public final class SchoolMediaDevices {
    private SchoolMediaDevices() { }
    public static Role role(BlockState state) {
        // Check the restricted speaker before its regular SpeakerBlock superclass.
        if (state.is(ModuleBlocks.GENERAL_SPEAKER.get())) return Role.GENERAL_SPEAKER;
        if (state.getBlock() instanceof SchoolCabinetBlock) return Role.AUDIO_CONTROLLER;
        if (state.is(ModuleBlocks.MOISES_LAPTOP.get())) return Role.MOISES_LAPTOP;
        if (state.getBlock() instanceof LaptopBlock) return Role.LAPTOP;
        if (state.is(ModuleBlocks.PC.get()) || state.getBlock() instanceof ComputerDeskBlock
                && state.getValue(ComputerDeskBlock.HAS_PC)) return Role.PC;
        if (state.getBlock() instanceof CeilingProjectorBlock || state.getBlock() instanceof WallProjectorBlock) return Role.PROJECTOR;
        if (state.getBlock() instanceof ProjectorScreenBlock) return Role.SCREEN;
        if (state.getBlock() instanceof SpeakerBlock || state.getBlock() instanceof TallSpeakerBlock) return Role.SPEAKER;
        return Role.NONE;
    }
    public static BlockPos anchor(Level level, BlockPos pos) {
        BlockState s = level.getBlockState(pos);
        if (s.getBlock() instanceof SchoolCabinetBlock)
            return pos.below(s.getValue(SchoolCabinetBlock.HALF) == DoubleBlockHalf.UPPER ? 1 : 0)
                    .relative(s.getValue(SchoolCabinetBlock.FACING).getClockWise(), -s.getValue(SchoolCabinetBlock.COLUMN));
        if (s.getBlock() instanceof GrayComputerDeskBlock)
            return pos.relative(s.getValue(ComputerDeskBlock.FACING).getCounterClockWise(), -s.getValue(GrayComputerDeskBlock.COLUMN))
                    .below(s.getValue(ComputerDeskBlock.HALF) == DoubleBlockHalf.UPPER ? 1 : 0);
        if (s.getBlock() instanceof ComputerDeskBlock)
            return pos.below(s.getValue(ComputerDeskBlock.HALF) == DoubleBlockHalf.UPPER ? 1 : 0);
        if (s.getBlock() instanceof ProjectorScreenBlock)
            return pos.relative(s.getValue(ProjectorScreenBlock.FACING).getClockWise(), -s.getValue(ProjectorScreenBlock.COLUMN))
                    .below(s.getValue(ProjectorScreenBlock.ROW));
        if (s.getBlock() instanceof WallProjectorBlock)
            return pos.relative(s.getValue(WallProjectorBlock.FACING).getClockWise(), -s.getValue(WallProjectorBlock.COLUMN));
        if (s.getBlock() instanceof TallSpeakerBlock)
            return pos.below(s.getValue(TwoTallBlock.HALF) == DoubleBlockHalf.UPPER ? 1 : 0);
        return pos.immutable();
    }
}
