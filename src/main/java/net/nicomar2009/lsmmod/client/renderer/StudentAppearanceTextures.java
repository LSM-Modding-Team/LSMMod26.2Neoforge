package net.nicomar2009.lsmmod.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.client.renderer.state.SchoolNpcRenderState;

/** Compose only requested portraits; the uniform and female bust UVs come from the base skin. */
@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public final class StudentAppearanceTextures {
    private record Key(Identifier base, boolean female, boolean freeHair, int skin, int eyes, int hair, int glasses) { }
    private static final Map<Key, Identifier> CACHE = new LinkedHashMap<>(16, 0.75F, true);
    private static final Map<Identifier, NativeImage> LAYERS = new HashMap<>();
    private static final Set<Key> FAILED = new HashSet<>();
    private static long nextId;
    private StudentAppearanceTextures() { }

    public static Identifier get(Identifier base, SchoolNpcRenderState state) {
        if (!state.hasPortrait()) return base;
        Key key = new Key(base, state.isFemale, state.isFemale && state.freeHair, Math.max(0, state.skinColor), Math.max(0, state.eyeColor),
                Math.max(0, state.haircut), Math.max(0, state.glassesType));
        Identifier cached = CACHE.get(key);
        if (cached != null) return cached;
        if (FAILED.contains(key)) return base;
        NativeImage output = null;
        try {
            output = new NativeImage(256, 256, false);
            try (NativeImage source = read(base)) {
                // Keep atlas coordinates stable even for a resource-pack skin of another supported resolution.
                for (int y = 0; y < 256; y++) for (int x = 0; x < 256; x++) {
                    output.setPixel(x, y, source.getPixel(x * source.getWidth() / 256, y * source.getHeight() / 256));
                }
            }
            // Remove old head overlays (including placeholder numbers), but preserve body/female-bust regions.
            for (int y = 0; y < 64; y++) for (int x = 128; x < 256; x++) output.setPixel(x, y, 0);
            overlay(output, layer("skin_%02d".formatted(key.skin)));
            overlay(output, layer("eye_" + key.eyes));
            overlay(output, layer((key.female ? (key.freeHair ? "female_free" : "female") : "male")
                    + "_hair_%02d".formatted(key.hair)));
            if (key.glasses > 0) overlay(output, layer("glasses_" + key.glasses));
            Identifier id = Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "runtime/student_portrait_" + nextId++);
            DynamicTexture texture = new DynamicTexture(() -> id.toString(), output);
            Minecraft.getInstance().getTextureManager().register(id, texture);
            output = null; // DynamicTexture owns the native pixels.
            CACHE.put(key, id);
            return id;
        } catch (IOException | RuntimeException invalid) {
            FAILED.add(key);
            LSMMod.LOGGER.warn("Could not compose student portrait {}; retaining base skin", key, invalid);
            return base;
        } finally {
            if (output != null) output.close();
        }
    }

    private static NativeImage read(Identifier path) throws IOException {
        var resource = Minecraft.getInstance().getResourceManager().getResource(path)
                .orElseThrow(() -> new IOException("Missing appearance texture " + path));
        try (var stream = resource.open()) { return NativeImage.read(stream); }
    }

    private static NativeImage layer(String name) throws IOException {
        Identifier path = Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,
                "textures/entity/student_appearance/" + name + ".png");
        NativeImage image = LAYERS.get(path);
        if (image == null) {
            image = read(path);
            if (image.getWidth() != 256 || image.getHeight() != 256) {
                image.close();
                throw new IOException("Appearance layer must be 256x256: " + path);
            }
            LAYERS.put(path, image);
        }
        return image;
    }

    private static void overlay(NativeImage target, NativeImage layer) {
        for (int y = 0; y < 256; y++) for (int x = 0; x < 256; x++) {
            int pixel = layer.getPixel(x, y), alpha = pixel >>> 24;
            if (alpha == 0) continue;
            if (alpha == 255) { target.setPixel(x, y, pixel); continue; }
            int base = target.getPixel(x, y), inverse = 255 - alpha;
            int r = (((pixel >>> 16) & 255) * alpha + ((base >>> 16) & 255) * inverse) / 255;
            int g = (((pixel >>> 8) & 255) * alpha + ((base >>> 8) & 255) * inverse) / 255;
            int b = ((pixel & 255) * alpha + (base & 255) * inverse) / 255;
            target.setPixel(x, y, 0xFF000000 | r << 16 | g << 8 | b);
        }
    }

    @SubscribeEvent
    public static void trimCache(ClientTickEvent.Post event) {
        // Release between frames, not during submit: queued draws may still refer to this frame's textures.
        while (CACHE.size() > 128) {
            var iterator = CACHE.entrySet().iterator();
            var oldest = iterator.next();
            Minecraft.getInstance().getTextureManager().release(oldest.getValue());
            iterator.remove();
        }
    }

    public static void clear() {
        var textures = Minecraft.getInstance().getTextureManager();
        CACHE.values().forEach(textures::release);
        CACHE.clear();
        LAYERS.values().forEach(NativeImage::close);
        LAYERS.clear();
        FAILED.clear();
    }
}
