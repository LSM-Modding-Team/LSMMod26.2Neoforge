package net.nicomar2009.lsmmod.modules.technology.client.screen;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.nicomar2009.lsmmod.modules.technology.client.media.SchoolMediaClient;
import net.nicomar2009.lsmmod.modules.technology.media.SchoolMediaDevices;
import net.nicomar2009.lsmmod.modules.technology.network.SchoolMediaPayloads;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules.Role;

/** Server-validated mixer for the Moises laptop wired to this cabinet. */
public final class SchoolAudioControlScreen extends Screen {
    private final BlockPos cabinet, interactionPos;
    private final ClientLevel level;
    private int volume = 100, speed = 100;
    private Button volumeValue, speedValue, apply;

    private SchoolAudioControlScreen(BlockPos pos) {
        super(Component.translatable("screen.lsmmod.audio.title"));
        level = Minecraft.getInstance().level;
        interactionPos = pos.immutable();
        cabinet = SchoolMediaDevices.anchor(level, pos);
        var route = SchoolMediaClient.controller(cabinet);
        if (route != null) { volume = route.volumePercent(); speed = route.speedPercent(); }
    }
    public static void open(BlockPos pos) {
        Minecraft.getInstance().setScreenAndShow(new SchoolAudioControlScreen(pos));
    }
    @Override protected void init() {
        int x = (width-280)/2, y = (height-220)/2;
        addRenderableWidget(Button.builder(Component.literal("-5%"), b -> { volume = Math.max(0, volume-5); refresh(); })
                .bounds(x, y+78, 55, 20).build());
        volumeValue = addRenderableWidget(Button.builder(Component.empty(), b -> { volume = 100; refresh(); })
                .bounds(x+60, y+78, 160, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+5%"), b -> { volume = Math.min(200, volume+5); refresh(); })
                .bounds(x+225, y+78, 55, 20).build());
        addRenderableWidget(Button.builder(Component.literal("-0.05"), b -> { speed = Math.max(25, speed-5); refresh(); })
                .bounds(x, y+126, 55, 20).build());
        speedValue = addRenderableWidget(Button.builder(Component.empty(), b -> { speed = 100; refresh(); })
                .bounds(x+60, y+126, 160, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+0.05"), b -> { speed = Math.min(200, speed+5); refresh(); })
                .bounds(x+225, y+126, 55, 20).build());
        apply = addRenderableWidget(Button.builder(Component.translatable("screen.lsmmod.audio.apply"), b ->
                ClientPacketDistributor.sendToServer(new SchoolMediaPayloads.AudioSettings(interactionPos, volume, speed)))
                .bounds(x, y+180, 135, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(x+145, y+180, 135, 20).build());
        refresh();
    }
    private void refresh() {
        volumeValue.setMessage(Component.literal(volume+"%"));
        speedValue.setMessage(Component.literal(String.format(Locale.ROOT, "x%.2f", speed/100.0)));
        apply.active = SchoolMediaClient.controller(cabinet) != null;
    }
    @Override public void tick() {
        if (minecraft.level != level || minecraft.player == null || !minecraft.player.isAlive()
                || !level.hasChunkAt(cabinet) || minecraft.player.distanceToSqr(Vec3.atCenterOf(cabinet)) > 64
                || SchoolMediaDevices.role(level.getBlockState(cabinet)) != Role.AUDIO_CONTROLLER) { onClose(); return; }
        refresh();
    }
    @Override public boolean isPauseScreen() { return false; }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (width-280)/2, y = (height-220)/2;
        graphics.fill(0, 0, width, height, 0xFF161B24);
        graphics.text(font, title, x, y, 0xFFFFFFFF);
        graphics.text(font, Component.translatable("screen.lsmmod.audio.volume"), x, y+60, 0xFFFFFFFF);
        graphics.text(font, Component.translatable("screen.lsmmod.audio.speed"), x, y+108, 0xFFFFFFFF);
        var route = SchoolMediaClient.controller(cabinet);
        Component status = Component.translatable(route == null ? "screen.lsmmod.audio.unlinked" : "screen.lsmmod.audio.linked");
        graphics.textWithWordWrap(font, status, x, y+22, 280, 0xFFB8C9DF);
        boolean saved = route != null && route.volumePercent() == volume && route.speedPercent() == speed;
        graphics.text(font, Component.translatable(saved ? "screen.lsmmod.audio.saved" : "screen.lsmmod.audio.help"), x, y+158, 0xFFB8C9DF);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }
}
