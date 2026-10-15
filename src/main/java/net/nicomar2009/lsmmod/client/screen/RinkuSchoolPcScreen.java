package net.nicomar2009.lsmmod.client.screen;

import de.keksuccino.rinku.Rinku;
import de.keksuccino.rinku.RinkuBrowser;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.client.media.SchoolBrowserSessions;
import org.cef.misc.CefCursorType;
import org.lwjgl.glfw.GLFW;

/** Interactive view of the device session also used by projectors and speakers. */
final class RinkuSchoolPcScreen extends SchoolPcScreen {
    private static final String SEARCH_URL = "https://www.google.com/search?q=";
    private final Map<Integer, KeyEvent> heldKeys = new HashMap<>();
    private RinkuBrowser browser;
    private SchoolBrowserSessions.Session session;
    private EditBox address;
    private Button back, forward, reload, home, go;
    private boolean failed, browserFocused, addressDirty;
    private int heldMouseButtons;
    private Component addressError;

    RinkuSchoolPcScreen(BlockPos pos) { super(pos); }

    @Override
    protected void init() {
        unfocusBrowser();
        super.init();
        String draft = address == null ? SchoolBrowserSessions.homePage(sourcePos()) : address.getValue();
        back = navigation("<", "back", MARGIN, () -> browser.goBack());
        forward = navigation(">", "forward", MARGIN + 24, () -> browser.goForward());
        reload = navigation("R", "reload", MARGIN + 48, () -> browser.reload());
        home = navigation("H", "home", MARGIN + 72, () -> navigate(SchoolBrowserSessions.homePage(sourcePos())));
        int addressX = MARGIN + 96;
        address = addRenderableWidget(new EditBox(font, addressX, NAV_Y,
                Math.max(1, width - MARGIN - 28 - addressX), 20,
                Component.translatable("screen.lsmmod.pc.address")));
        address.setMaxLength(2048);
        address.setValue(draft);
        address.setResponder(value -> addressDirty = true);
        address.setHint(Component.translatable("screen.lsmmod.pc.address"));
        go = navigation(">", "go", width - MARGIN - 24, () -> navigate(address.getValue()));
        tryCreateBrowser();
        refreshNavigation();
    }

    private Button navigation(String label, String tooltip, int x, Runnable action) {
        Button button = addRenderableWidget(Button.builder(Component.literal(label), b -> {
            if (ready()) {
                addressDirty = false;
                addressError = null;
                action.run();
            }
        }).bounds(x, NAV_Y, 22, 20).build());
        button.setTooltip(Tooltip.create(Component.translatable("screen.lsmmod.pc." + tooltip)));
        return button;
    }

    private boolean ready() { return browser != null && Rinku.isInitialized() && browser.isValid(); }

    private void tryCreateBrowser() {
        if (browser != null || failed || !Rinku.isInitialized()) return;
        try {
            session = SchoolBrowserSessions.acquire(sourcePos());
            browser = session.browser;
        } catch (RuntimeException | LinkageError failure) {
            failed = true;
            LSMMod.LOGGER.error("Could not open the school PC browser", failure);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (minecraft.gui.screen() != this) return;
        tryCreateBrowser();
        refreshNavigation();
    }

    private void refreshNavigation() {
        boolean available = ready();
        back.active = available && browser.canGoBack();
        forward.active = available && browser.canGoForward();
        reload.active = home.active = go.active = address.active = available;
        if (available && !address.isFocused() && !addressDirty && addressError == null) {
            String url = browser.getURL();
            if (url != null && !url.isBlank() && !url.equals(address.getValue())) {
                address.setValue(url);
                addressDirty = false;
            }
        }
    }

    private void navigate(String input) {
        if (!ready()) return;
        String value = input.strip();
        if (value.isEmpty()) return;
        try {
            String url;
            if (value.matches("(?i)^[a-z][a-z0-9+.-]*://.*")) {
                url = value;
            } else if (!value.contains(" ") && (value.contains(".") || value.startsWith("localhost"))) {
                url = "https://" + value;
            } else {
                url = SEARCH_URL + URLEncoder.encode(value, StandardCharsets.UTF_8);
            }
            URI uri = URI.create(url);
            if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null || uri.getRawUserInfo() != null) throw new IllegalArgumentException();
            browser.loadURL(uri.toASCIIString());
            address.setValue(uri.toASCIIString());
            addressDirty = false;
            addressError = null;
            focusBrowser();
        } catch (IllegalArgumentException invalid) {
            addressError = Component.translatable("screen.lsmmod.pc.invalid_address");
        }
    }

    @Override
    protected Component status() {
        if (failed) return Component.translatable("screen.lsmmod.pc.failed");
        if (!ready() || !browser.isTextureReady()) return Component.translatable("screen.lsmmod.pc.loading");
        return null;
    }

    @Override
    protected Component footer() { return addressError == null ? super.footer() : addressError; }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        if (ready() && browser.isTextureReady()) {
            Identifier texture = browser.getTextureIdentifier();
            if (texture != null) graphics.blit(RenderPipelines.GUI_TEXTURED, texture,
                    MARGIN, VIEW_Y, 0, 0, viewWidth(), viewHeight(), viewWidth(), viewHeight());
        }
        if (ready() && browser.isLoading())
            graphics.fill(MARGIN, VIEW_Y - 3, width - MARGIN, VIEW_Y - 1, 0xFF66B7FF);
    }

    private boolean inView(double x, double y) {
        return x >= MARGIN && x < MARGIN + viewWidth() && y >= VIEW_Y && y < VIEW_Y + viewHeight();
    }
    private int browserX(double x) { return (int) ((x - MARGIN) * SchoolBrowserSessions.WIDTH / viewWidth()); }
    private int browserY(double y) { return (int) ((y - VIEW_Y) * SchoolBrowserSessions.HEIGHT / viewHeight()); }

    private void focusBrowser() {
        setFocused(null);
        address.setFocused(false);
        browserFocused = true;
        browser.setFocus(true);
    }

    private void unfocusBrowser() {
        browserFocused = false;
        if (!ready()) return;
        for (KeyEvent key : heldKeys.values()) browser.sendKeyRelease(key.key(), key.scancode(), 0);
        heldKeys.clear();
        browser.cancelDrag();
        for (int button = 0; button < 8; button++)
            if ((heldMouseButtons & 1 << button) != 0) browser.sendMouseRelease(-1, -1, button);
        heldMouseButtons = 0;
        browser.setFocus(false);
        browser.setCursor(CefCursorType.POINTER);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (ready() && inView(event.x(), event.y())) {
            focusBrowser();
            browser.sendMousePress(browserX(event.x()), browserY(event.y()), event.button());
            heldMouseButtons |= 1 << event.button();
            return true;
        }
        unfocusBrowser();
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (browser != null && (heldMouseButtons & 1 << event.button()) != 0) {
            heldMouseButtons &= ~(1 << event.button());
            if (ready()) browser.sendMouseRelease(browserX(event.x()), browserY(event.y()), event.button());
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void mouseMoved(double x, double y) {
        if (ready()) {
            browser.sendMouseMove(browserX(x), browserY(y));
            if (!inView(x, y)) browser.setCursor(CefCursorType.POINTER);
        }
        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (ready() && (heldMouseButtons & 1 << event.button()) != 0) {
            browser.sendMouseMove(browserX(event.x()), browserY(event.y()));
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (ready() && inView(x, y)) {
            browser.sendMouseWheel(browserX(x), browserY(y), scrollY, modifiers());
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    private int modifiers() {
        var window = minecraft.getWindow();
        int mods = 0;
        for (int[] pair : new int[][]{{GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL, GLFW.GLFW_MOD_CONTROL},
                {GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_MOD_SHIFT},
                {GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT, GLFW.GLFW_MOD_ALT}}) {
            if (com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, pair[0])
                    || com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, pair[1])) mods |= pair[2];
        }
        return mods;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) { onClose(); return true; }
        if (ready() && event.key() == GLFW.GLFW_KEY_L && (event.modifiers() & GLFW.GLFW_MOD_CONTROL) != 0) {
            unfocusBrowser();
            setFocused(address);
            address.setFocused(true);
            address.setCursorPosition(address.getValue().length());
            address.setHighlightPos(0);
            return true;
        }
        if (address.isFocused() && (event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER)) {
            navigate(address.getValue());
            return true;
        }
        if (ready() && browserFocused) {
            heldKeys.put(event.key(), event);
            browser.sendKeyPress(event.key(), event.scancode(), event.modifiers());
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (heldKeys.remove(event.key()) != null) {
            if (ready()) browser.sendKeyRelease(event.key(), event.scancode(), event.modifiers());
            return true;
        }
        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (ready() && browserFocused) {
            // JCEF's input API accepts UTF-16 units, including both halves of supplementary characters.
            for (char unit : Character.toChars(event.codepoint())) browser.sendKeyTyped(unit, modifiers());
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public void removed() {
        // The device, not this GUI, owns Chromium while a projector or speaker still needs it.
        if (browser != null) {
            unfocusBrowser();
            SchoolBrowserSessions.release(session);
            session = null;
            browser = null;
            heldKeys.clear();
            heldMouseButtons = 0;
        }
        super.removed();
    }
}
