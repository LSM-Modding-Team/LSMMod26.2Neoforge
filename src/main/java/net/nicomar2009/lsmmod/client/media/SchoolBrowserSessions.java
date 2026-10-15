package net.nicomar2009.lsmmod.client.media;

import de.keksuccino.rinku.Rinku;
import de.keksuccino.rinku.RinkuBrowser;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.media.SchoolMediaDevices;
import net.nicomar2009.lsmmod.rules.SchoolMediaRules;

/** A device has one Chromium surface shared by its GUI, projectors and audio outputs. */
public final class SchoolBrowserSessions {
    public static final int WIDTH = 1280, HEIGHT = 720;
    private static final Map<BlockPos, Session> SESSIONS = new HashMap<>();
    public static final class Session {
        public final RinkuBrowser browser;
        private final BlockPos source;
        private final Block block;
        private final SchoolBrowserAudio audio;
        private int users, idleTicks;
        private Session(BlockPos source) {
            this.source = source.immutable();
            block = Minecraft.getInstance().level.getBlockState(source).getBlock();
            SchoolBrowserAudio.install();
            // Register capture before native creation, including the first page's audio setup.
            browser = new RinkuBrowser(Rinku.getClient(), homePage(source), false);
            audio = SchoolBrowserAudio.attach(browser, source);
            try {
                browser.setCloseAllowed();
                browser.createImmediately();
                browser.resize(WIDTH, HEIGHT);
            } catch (RuntimeException | LinkageError failure) {
                audio.close(); browser.close(); throw failure;
            }
        }
        private void close() {
            audio.close();
            try { browser.close(); }
            catch (RuntimeException | LinkageError failure) { LSMMod.LOGGER.warn("Could not close a device browser", failure); }
        }
    }
    private SchoolBrowserSessions() { }
    public static String homePage(BlockPos source) {
        var level = Minecraft.getInstance().level;
        return level != null && SchoolMediaDevices.role(level.getBlockState(source)) == SchoolMediaRules.Role.MOISES_LAPTOP
                ? "https://lsm-modding-team.github.io/lsm-music/"
                : "https://www.google.com/";
    }
    public static Session acquire(BlockPos source) {
        SchoolMediaClient.prepareLevel();
        Session session = SESSIONS.get(source);
        if (session == null) {
            if (SESSIONS.size() >= 16) throw new IllegalStateException("Too many active school browsers");
            session = new Session(source);
            SESSIONS.put(source.immutable(), session);
        }
        session.users++; session.idleTicks = 0;
        return session;
    }
    public static void release(Session session) {
        if (session != null) session.users = Math.max(0, session.users - 1);
    }
    public static Identifier texture(BlockPos source) {
        Session session = SESSIONS.get(source);
        return session != null && session.browser.isTextureReady() ? session.browser.getTextureIdentifier() : null;
    }
    public static void tick() {
        var minecraft = Minecraft.getInstance();
        var iterator = SESSIONS.values().iterator();
        while (iterator.hasNext()) {
            Session session = iterator.next();
            boolean valid = minecraft.level != null && minecraft.level.hasChunkAt(session.source)
                    && minecraft.level.getBlockState(session.source).is(session.block)
                    && SchoolMediaRules.source(SchoolMediaDevices.role(minecraft.level.getBlockState(session.source)));
            boolean keep = session.users > 0 || SchoolMediaClient.hasOutputs(session.source);
            if (!valid || !Rinku.isInitialized() || !keep && ++session.idleTicks > 100) {
                session.close(); iterator.remove(); continue;
            }
            if (keep) session.idleTicks = 0;
            session.audio.tick(SchoolMediaClient.speakers(session.source));
        }
    }
    public static void closeAll() {
        for (Session session : SESSIONS.values()) session.close();
        SESSIONS.clear();
    }
}
