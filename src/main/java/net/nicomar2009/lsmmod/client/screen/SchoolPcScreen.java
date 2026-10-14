package net.nicomar2009.lsmmod.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.nicomar2009.lsmmod.media.SchoolMediaDevices;
import net.nicomar2009.lsmmod.rules.SchoolMediaRules;

/** Shell and missing-library screen; loading this class requires no Rinku or CEF classes. */
public class SchoolPcScreen extends Screen {
    protected static final int MARGIN = 8;
    protected static final int NAV_Y = 26;
    protected static final int VIEW_Y = 52;
    private final BlockPos pcPos;
    private final ClientLevel pcLevel;
    private final Block pcBlock;

    protected SchoolPcScreen(BlockPos pos) {
        super(Component.translatable("screen.lsmmod.pc.title"));
        pcPos = pos.immutable();
        pcLevel = Minecraft.getInstance().level;
        pcBlock = pcLevel.getBlockState(pcPos).getBlock();
    }

    public static boolean hasPc(BlockState state) {
        return SchoolMediaRules.source(SchoolMediaDevices.role(state));
    }

    public static void open(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        pos = SchoolMediaDevices.anchor(minecraft.level, pos);
        // The optional implementation is resolved only after the loader has found its library.
        minecraft.setScreenAndShow(ModList.get().isLoaded("rinku")
                ? new RinkuSchoolPcScreen(pos) : new SchoolPcScreen(pos));
    }

    protected int viewWidth() { return Math.max(1, width - MARGIN * 2); }
    protected int viewHeight() { return Math.max(1, height - VIEW_Y - 22); }
    protected BlockPos sourcePos() { return pcPos; }

    @Override
    protected void init() {
        Button close = addRenderableWidget(Button.builder(Component.literal("X"), b -> onClose())
                .bounds(width - MARGIN - 20, 4, 20, 20).build());
        close.setTooltip(Tooltip.create(Component.translatable("gui.close")));
    }

    @Override
    public void tick() {
        super.tick();
        // Multiplayer can remove the PC, teleport the player, or unload its world while the GUI is open.
        if (minecraft.level != pcLevel || minecraft.player == null || !minecraft.player.isAlive()
                || minecraft.player.distanceToSqr(Vec3.atCenterOf(pcPos)) > 64
                || !pcLevel.getBlockState(pcPos).is(pcBlock) || !hasPc(pcLevel.getBlockState(pcPos))) onClose();
    }

    protected Component status() { return Component.translatable("screen.lsmmod.pc.missing"); }
    protected Component footer() { return Component.translatable("screen.lsmmod.pc.help"); }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xFF161B24);
        graphics.text(font, title, MARGIN, 10, 0xFFFFFFFF);
        graphics.fill(MARGIN, VIEW_Y, MARGIN + viewWidth(), VIEW_Y + viewHeight(), 0xFF252D39);
        Component message = status();
        if (message != null) graphics.textWithWordWrap(font, message, MARGIN + 12, VIEW_Y + 16,
                Math.max(1, viewWidth() - 24), 0xFFE1E7EE);
        graphics.text(font, footer(), MARGIN, height - 14, 0xFFB4BFCE);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override public boolean isPauseScreen() { return false; }
}
