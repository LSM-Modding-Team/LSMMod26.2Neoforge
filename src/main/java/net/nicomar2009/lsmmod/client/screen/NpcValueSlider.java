package net.nicomar2009.lsmmod.client.screen;

import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.nicomar2009.lsmmod.util.NpcAppearanceCatalog;

/** Discrete vanilla slider; hover describes the value at that track position before clicking. */
public final class NpcValueSlider extends AbstractSliderButton {
    private final String key;
    private final int minimum, maximum;
    private final IntConsumer changed;
    private int lastHover = Integer.MIN_VALUE;
    private boolean lastActive;

    public NpcValueSlider(int x, int y, int width, String key, int minimum, int maximum, int initial, IntConsumer changed) {
        super(x, y, width, 20, Component.empty(), (double)(initial - minimum) / (maximum - minimum));
        this.key = key; this.minimum = minimum; this.maximum = maximum; this.changed = changed;
        updateMessage();
    }
    private int number(double fraction) {
        return minimum + (int)Math.round(Math.clamp(fraction, 0.0, 1.0) * (maximum - minimum));
    }
    private Component description(int n) {
        if (n < 0) return Component.translatable("screen.lsmmod.slider_unset", key);
        if (key.equals("glassesType") && n == 0) return Component.translatable("screen.lsmmod.slider_no_glasses");
        return Component.literal(key + ": " + n + NpcAppearanceCatalog.details(key, n));
    }
    @Override protected void updateMessage() {
        int n = number(value);
        setMessage(n < 0 ? Component.translatable("appearance.lsmmod.unset") : Component.literal(Integer.toString(n)));
    }
    @Override protected void applyValue() {
        int n = number(value);
        changed.accept(n);
    }
    @Override public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int candidate = active && isMouseOver(mouseX, mouseY)
                ? number((double)(mouseX - getX() - 4) / (getWidth() - 8)) : number(value);
        if (candidate != lastHover || active != lastActive) {
            setTooltip(Tooltip.create(active ? description(candidate) : Component.translatable("screen.lsmmod.incompatible")));
            lastHover = candidate; lastActive = active;
        }
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
    }
}
