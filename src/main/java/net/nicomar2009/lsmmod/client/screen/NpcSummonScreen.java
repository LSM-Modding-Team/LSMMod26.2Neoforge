package net.nicomar2009.lsmmod.client.screen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Paged editor with retained inputs and an inspectable summon command. */
public final class NpcSummonScreen extends Screen {
    private final Map<String, String> values = new LinkedHashMap<>();
    private boolean teacher;
    private int page;
    private String position = "~ ~ ~";
    private Component status = Component.empty();
    private EditBox preview;
    private int left, top, panelWidth, rows;
    private List<NpcSummonForm.Field> fields;

    public NpcSummonScreen() { super(Component.translatable("screen.lsmmod.npc_helper")); }

    @Override
    protected void init() {
        left = Math.max(8, (width - 440) / 2);
        panelWidth = Math.min(440, width - 16);
        top = 28;
        rows = Math.max(1, Math.min(5, (height - 165) / 29));
        fields = NpcSummonForm.fields(teacher);
        int pages = (fields.size() + rows - 1) / rows;
        page = Math.clamp(page, 0, pages - 1);
        addRenderableWidget(Button.builder(Component.translatable(teacher ? "entity.lsmmod.teacher" : "entity.lsmmod.student"), b -> {
            teacher = !teacher; page = 0; rebuild();
        }).bounds(left, top, panelWidth / 2 - 4, 20).build());
        EditBox coordinates = new EditBox(font, left + panelWidth / 2, top, panelWidth / 2, 20, Component.translatable("screen.lsmmod.position"));
        coordinates.setMaxLength(100); coordinates.setValue(position); coordinates.setResponder(s -> { position = s; refreshPreview(); });
        addRenderableWidget(coordinates);
        for (int row = 0; row < rows; row++) {
            int index = page * rows + row;
            if (index >= fields.size()) break;
            var field = fields.get(index);
            int y = top + 36 + row * 29;
            EditBox box = new EditBox(font, left + panelWidth / 2, y, panelWidth / 2, 20, Component.literal(field.key() + " " + field.range()));
            box.setMaxLength(8000); box.setValue(values.getOrDefault(field.key(), ""));
            box.setHint(Component.literal(field.range()));
            box.setResponder(s -> { values.put(field.key(), s); refreshPreview(); });
            addRenderableWidget(box);
        }
        int bottom = height - 77;
        addRenderableWidget(Button.builder(Component.literal("<"), b -> { page--; rebuild(); }).bounds(left, bottom, 24, 20).build()).active = page > 0;
        addRenderableWidget(Button.builder(Component.literal(">"), b -> { page++; rebuild(); }).bounds(left + 28, bottom, 24, 20).build()).active = page < pages - 1;
        addRenderableWidget(Button.builder(Component.translatable("screen.lsmmod.copy"), b -> generate(false)).bounds(left + 58, bottom, (panelWidth - 66) / 2, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.lsmmod.summon"), b -> generate(true)).bounds(left + 62 + (panelWidth - 66) / 2, bottom, (panelWidth - 66) / 2, 20).build());
        preview = new EditBox(font, left, height - 52, panelWidth, 20, Component.translatable("screen.lsmmod.command"));
        preview.setMaxLength(32767); preview.setEditable(false); addRenderableWidget(preview);
        refreshPreview();
    }

    private void refreshPreview() {
        if (preview == null) return;
        try { preview.setValue(NpcSummonForm.command(teacher, position, values)); }
        catch (Exception invalid) { preview.setValue(""); }
    }

    private void rebuild() { clearWidgets(); init(); }

    private void generate(boolean summon) {
        try {
            String command = NpcSummonForm.command(teacher, position, values);
            preview.setValue(command);
            if (summon) {
                if (minecraft.getConnection() != null) {
                    minecraft.getConnection().sendCommand(command.substring(1));
                    status = Component.translatable("screen.lsmmod.sent");
                }
            } else {
                minecraft.keyboardHandler.setClipboard(command);
                status = Component.translatable("screen.lsmmod.copied");
            }
        } catch (Exception invalid) {
            status = Component.translatable("screen.lsmmod.invalid", invalid.getMessage() == null ? "NBT" : invalid.getMessage());
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.centeredText(font, title, width / 2, 8, 0xFFFFFFFF);
        graphics.text(font, Component.translatable("screen.lsmmod.blank_default"), left, top + 23, 0xFFBBBBBB);
        for (int row = 0; row < rows; row++) {
            int index = page * rows + row;
            if (index < fields.size()) graphics.text(font, fields.get(index).key(), left, top + 42 + row * 29, 0xFFFFFFFF);
        }
        graphics.text(font, status, left, height - 27, 0xFFFFCC66);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }
    @Override public boolean isPauseScreen() { return false; }
}
