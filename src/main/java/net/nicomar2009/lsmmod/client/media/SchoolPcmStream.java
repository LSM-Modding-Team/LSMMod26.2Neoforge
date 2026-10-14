package net.nicomar2009.lsmmod.client.media;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.resources.Identifier;
import org.lwjgl.system.MemoryUtil;

/** Bounded live mono PCM, copied while CEF's callback data is valid. */
public final class SchoolPcmStream implements AudioStream {
    private static final Map<Identifier, SchoolPcmStream> STREAMS = new ConcurrentHashMap<>();
    private final AudioFormat format;
    private final ArrayDeque<byte[]> packets = new ArrayDeque<>();
    private int offset, queuedBytes;
    private volatile boolean closed;
    public SchoolPcmStream(int sampleRate) { format = new AudioFormat(sampleRate, 16, 1, true, false); }
    public static void register(Identifier path, SchoolPcmStream stream) { STREAMS.put(path, stream); }
    public static SchoolPcmStream find(Identifier path) { return STREAMS.get(path); }
    public static void unregister(Identifier path, SchoolPcmStream stream) { STREAMS.remove(path, stream); stream.close(); }
    public boolean closed() { return closed; }
    public synchronized void offer(byte[] pcm) {
        if (closed) return;
        packets.add(pcm); queuedBytes += pcm.length;
        // Keep at most half a second; pausing or a slow client cannot grow the queue indefinitely.
        while (queuedBytes > (int) format.getSampleRate() && !packets.isEmpty()) {
            queuedBytes -= packets.remove().length - offset; offset = 0;
        }
    }
    @Override public AudioFormat getFormat() { return format; }
    @Override public synchronized ByteBuffer read(int requested) {
        if (closed) return MemoryUtil.memAlloc(0);
        // Four small OpenAL buffers keep live playback responsive instead of buffering seconds of silence.
        int count = Math.min(requested & ~1, (int) format.getSampleRate() / 20 * 2);
        ByteBuffer result = MemoryUtil.memAlloc(count);
        while (result.hasRemaining() && !packets.isEmpty()) {
            byte[] packet = packets.peek();
            int length = Math.min(result.remaining(), packet.length - offset);
            result.put(packet, offset, length); offset += length; queuedBytes -= length;
            if (offset == packet.length) { packets.remove(); offset = 0; }
        }
        while (result.hasRemaining()) result.put((byte) 0);
        return result.flip();
    }
    @Override public synchronized void close() { closed = true; packets.clear(); queuedBytes = offset = 0; }
}
