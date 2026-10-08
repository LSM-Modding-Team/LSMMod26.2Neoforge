package net.nicomar2009.lsmmod.client.screen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.nicomar2009.lsmmod.entity.SchoolNpcStat;
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

    public NpcSummonScreen() {
        super(Component.translatable("screen.lsmmod.npc_helper"));
        for (SchoolNpcStat stat : SchoolNpcStat.values()) values.put(stat.nbtKey(), "3");
        values.put("Width", "14"); values.put("Height", "14");
        values.put("gender", "male"); values.put("StudentData.Level", "unassigned");
        values.put("StudentData.Grade", "0"); values.put("freeHair", "false");
    }

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
            fieldControl(field, left + panelWidth / 2, y, panelWidth / 2);
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

    private boolean compatible(String key) {
        boolean female = values.getOrDefault("gender", "male").equals("female");
        return switch (key) {
            case "haircutMale" -> !teacher && !female;
            case "haircutFemale", "freeHair" -> !teacher && female;
            case "StudentData.Grade" -> !values.getOrDefault("StudentData.Level", "unassigned").equals("unassigned");
            default -> true;
        };
    }

    private void select(String key, String option) {
        values.put(key, option);
        if (key.equals("StudentData.Level")) {
            int grade = Integer.parseInt(values.getOrDefault("StudentData.Grade", "0"));
            values.put("StudentData.Grade", option.equals("unassigned") ? "0"
                    : Integer.toString(Math.clamp(grade, 1, option.equals("primary") ? 6 : 5)));
        }
        status = Component.empty();
        rebuild();
    }

    private void choices(String key, String[] options, int x, int y, int span) {
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            boolean selected = option.equals(values.get(key));
            Component label = switch (option) {
                case "male", "female" -> Component.translatable("appearance.lsmmod.gender." + option);
                case "true", "false" -> Component.translatable("appearance.lsmmod.hair." + (option.equals("true") ? "loose" : "tied"));
                case "primary", "secondary", "unassigned" -> Component.translatable("screen.lsmmod.level." + option);
                default -> Component.literal(option);
            };
            if (selected) label = Component.literal("[").append(label).append("]");
            int start = x + i * span / options.length;
            int end = x + (i + 1) * span / options.length;
            Button button = addRenderableWidget(Button.builder(label, b -> select(key, option))
                    .bounds(start, y, end - start - 2, 20).build());
            button.active = compatible(key) && !selected;
            button.setTooltip(Tooltip.create(!compatible(key) ? Component.translatable("screen.lsmmod.incompatible")
                    : selected ? Component.translatable("screen.lsmmod.selected") : Component.literal(key + ": " + option)));
        }
    }

    private void fieldControl(NpcSummonForm.Field field, int x, int y, int span) {
        String key = field.key();
        for (SchoolNpcStat stat : SchoolNpcStat.values()) {
            if (key.equals(stat.nbtKey())) { choices(key, new String[]{"1", "2", "3", "4", "5"}, x, y, span); return; }
        }
        switch (key) {
            case "gender" -> choices(key, new String[]{"male", "female"}, x, y, span);
            case "freeHair" -> choices(key, new String[]{"false", "true"}, x, y, span);
            case "StudentData.Level" -> choices(key, new String[]{"unassigned", "primary", "secondary"}, x, y, span);
            case "StudentData.Grade" -> {
                String level = values.getOrDefault("StudentData.Level", "unassigned");
                choices(key, level.equals("unassigned") ? new String[]{"0"}
                        : level.equals("primary") ? new String[]{"1", "2", "3", "4", "5", "6"}
                        : new String[]{"1", "2", "3", "4", "5"}, x, y, span);
            }
            case "Width", "Height", "skinColor", "eyeColor", "haircutMale", "haircutFemale", "glassesType" -> {
                boolean size = key.equals("Width") || key.equals("Height");
                int min = size ? 1 : -1;
                int max = switch (key) {
                    case "Width", "Height" -> 16;
                    case "skinColor" -> 63;
                    case "eyeColor" -> 7;
                    case "glassesType" -> 8;
                    default -> 31;
                };
                int initial = Integer.parseInt(values.getOrDefault(key, size ? "14" : "-1"));
                NpcValueSlider slider = addRenderableWidget(new NpcValueSlider(x, y, span, key, min, max, initial, n -> {
                    values.put(key, Integer.toString(n)); status = Component.empty(); refreshPreview();
                }));
                slider.active = compatible(key);
            }
            default -> {
                // Only advanced teacher UUID memories remain editable text; all ordinary NBT use widgets.
                EditBox box = new EditBox(font, x, y, span, 20, Component.literal(key + " " + field.range()));
                box.setMaxLength(8000); box.setValue(values.getOrDefault(key, ""));
                box.setHint(Component.literal(field.range()));
                box.setResponder(v -> { values.put(key, v); refreshPreview(); }); addRenderableWidget(box);
            }
        }
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
        graphics.text(font, Component.translatable("screen.lsmmod.control_help"), left, top + 23, 0xFFBBBBBB);
        for (int row = 0; row < rows; row++) {
            int index = page * rows + row;
            if (index < fields.size()) graphics.text(font, fields.get(index).key(), left, top + 42 + row * 29, 0xFFFFFFFF);
        }
        graphics.text(font, status, left, height - 27, 0xFFFFCC66);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }
    @Override public boolean isPauseScreen() { return false; }
}
