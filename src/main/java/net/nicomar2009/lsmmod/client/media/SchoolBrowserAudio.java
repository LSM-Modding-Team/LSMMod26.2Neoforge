package net.nicomar2009.lsmmod.client.media;

import de.keksuccino.rinku.Rinku;
import de.keksuccino.rinku.RinkuClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.nicomar2009.lsmmod.LSMMod;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefAudioHandlerAdapter;
import org.cef.misc.CefAudioParameters;
import org.cef.misc.CefChannelLayout;
import org.cef.misc.DataPointer;

/** CEF capture mutes its native output; Minecraft owns positional playback and volume controls. */
final class SchoolBrowserAudio {
    private record Sink(BlockPos pos, SchoolPcmStream stream, SchoolBrowserSound sound) { }
    private static final Map<CefBrowser, SchoolBrowserAudio> CAPTURES = new ConcurrentHashMap<>();
    private static RinkuClient installed;
    private final CefBrowser browser;
    private final BlockPos source;
    private volatile List<Sink> sinks = List.of();
    private volatile boolean started, closed;
    private volatile int sampleRate = 48000, channels = 1, generation;
    private int playingGeneration = -1, ticks;
    private SchoolBrowserAudio(CefBrowser browser, BlockPos source) { this.browser = browser; this.source = source; }
    static SchoolBrowserAudio attach(CefBrowser browser, BlockPos source) {
        SchoolBrowserAudio audio = new SchoolBrowserAudio(browser, source);
        CAPTURES.put(browser, audio);
        return audio;
    }
    static void install() {
        if (installed == Rinku.getClient()) return;
        installed = Rinku.getClient();
        installed.addAudioHandler(new CefAudioHandlerAdapter() {
            @Override public boolean getAudioParameters(CefBrowser browser, CefAudioParameters params) {
                if (!CAPTURES.containsKey(browser)) return false;
                params.setChannelLayout(CefChannelLayout.CEF_CHANNEL_LAYOUT_MONO);
                params.setSampleRate(48000); params.setFramesPerBuffer(960);
                return true;
            }
            @Override public void onAudioStreamStarted(CefBrowser browser, CefAudioParameters params, int channels) {
                SchoolBrowserAudio audio = CAPTURES.get(browser);
                if (audio == null) return;
                audio.sampleRate = params.sampleRate; audio.channels = channels;
                audio.generation++; audio.started = true;
            }
            @Override public void onAudioStreamPacket(CefBrowser browser, DataPointer data, int frames, long pts) {
                SchoolBrowserAudio audio = CAPTURES.get(browser);
                if (audio == null || audio.closed || frames < 1 || frames > 48000 || audio.channels < 1 || audio.channels > 8) return;
                List<Sink> targets = audio.sinks;
                if (targets.isEmpty()) return;
                byte[] pcm = new byte[frames * 2];
                for (int frame = 0; frame < frames; frame++) {
                    float value = 0;
                    for (int c = 0; c < audio.channels; c++) value += data.getData(c).getFloat(frame) / audio.channels;
                    if (!Float.isFinite(value)) value = 0;
                    int sample = Math.round(Math.clamp(value, -1, 1) * 32767);
                    pcm[frame * 2] = (byte) sample; pcm[frame * 2 + 1] = (byte) (sample >> 8);
                }
                for (Sink sink : targets) sink.stream().offer(pcm);
            }
            @Override public void onAudioStreamStopped(CefBrowser browser) {
                SchoolBrowserAudio audio = CAPTURES.get(browser);
                if (audio != null) audio.started = false;
            }
            @Override public void onAudioStreamError(CefBrowser browser, String text) {
                onAudioStreamStopped(browser);
                if (CAPTURES.containsKey(browser)) LSMMod.LOGGER.warn("School browser audio stopped: {}", text);
            }
        });
    }
    void tick(List<BlockPos> speakers) {
        if (closed) return;
        ticks++;
        List<BlockPos> outputs = speakers.isEmpty() ? List.of(source) : speakers;
        if (!started || playingGeneration != generation) {
            disposeSinks(); playingGeneration = generation;
            if (!started) return;
        }
        var next = new ArrayList<Sink>();
        for (Sink old : sinks) {
            if (outputs.contains(old.pos()) && !old.stream().closed()) next.add(old);
            else dispose(old);
        }
        for (BlockPos pos : outputs) {
            if (next.stream().anyMatch(s -> s.pos().equals(pos))) continue;
            SchoolPcmStream stream = new SchoolPcmStream(sampleRate);
            SchoolBrowserSound sound = new SchoolBrowserSound(pos, stream, speakers.isEmpty() ? 8 : 32);
            next.add(new Sink(pos, stream, sound));
            Minecraft.getInstance().getSoundManager().play(sound);
        }
        sinks = List.copyOf(next);
        if (ticks % 20 == 0) for (Sink sink : sinks) {
            var manager = Minecraft.getInstance().getSoundManager();
            if (!manager.isActive(sink.sound())) manager.play(sink.sound());
        }
    }
    private static void dispose(Sink sink) {
        sink.sound().dispose();
        Minecraft.getInstance().getSoundManager().stop(sink.sound());
    }
    private void disposeSinks() {
        List<Sink> old = sinks; sinks = List.of();
        for (Sink sink : old) dispose(sink);
    }
    void close() { closed = true; CAPTURES.remove(browser, this); disposeSinks(); }
}
