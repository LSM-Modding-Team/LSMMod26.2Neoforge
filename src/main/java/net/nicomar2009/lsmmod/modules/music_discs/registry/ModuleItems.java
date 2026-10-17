package net.nicomar2009.lsmmod.modules.music_discs.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final ResourceKey<JukeboxSong> LSM_ANTHEM_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "lsm_anthem"));

    public static final DeferredItem<Item> LSM_ANTHEM = ITEMS.registerItem(
            "lsm_anthem",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(LSM_ANTHEM_SONG));

    public static final ResourceKey<JukeboxSong> LSM_MARCH_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "lsm_march"));

    public static final DeferredItem<Item> LSM_MARCH = ITEMS.registerItem(
            "lsm_march",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(LSM_MARCH_SONG));

    public static final ResourceKey<JukeboxSong> QAPACKACHAMPA_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "qapackachampa"));

    public static final DeferredItem<Item> QAPACKACHAMPA = ITEMS.registerItem(
            "qapackachampa",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(QAPACKACHAMPA_SONG));

    public static final ResourceKey<JukeboxSong> COFRADIA_SAN_MIGUEL_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "cofradia_san_miguel"));

    public static final DeferredItem<Item> COFRADIA_SAN_MIGUEL = ITEMS.registerItem(
            "cofradia_san_miguel",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(COFRADIA_SAN_MIGUEL_SONG));

    public static final ResourceKey<JukeboxSong> CARNAVAL_HUACCANA_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "carnaval_huaccana"));

    public static final DeferredItem<Item> CARNAVAL_HUACCANA = ITEMS.registerItem(
            "carnaval_huaccana",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(CARNAVAL_HUACCANA_SONG));

    public static final ResourceKey<JukeboxSong> CARNAVAL_HUANUPAMPA_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "carnaval_huanupampa"));

    public static final DeferredItem<Item> CARNAVAL_HUANUPAMPA = ITEMS.registerItem(
            "carnaval_huanupampa",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(CARNAVAL_HUANUPAMPA_SONG));

    public static final ResourceKey<JukeboxSong> CARNAVAL_MACARI_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "carnaval_macari"));

    public static final DeferredItem<Item> CARNAVAL_MACARI = ITEMS.registerItem(
            "carnaval_macari",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(CARNAVAL_MACARI_SONG));

    public static final ResourceKey<JukeboxSong> FORMACION_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "formacion"));

    public static final DeferredItem<Item> FORMACION = ITEMS.registerItem(
            "formacion",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(FORMACION_SONG));

    public static final ResourceKey<JukeboxSong> HIMNOPERU_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "himnoperu"));

    public static final DeferredItem<Item> HIMNOPERU = ITEMS.registerItem(
            "himnoperu",
            Item::new, props -> props
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(HIMNOPERU_SONG));

    private ModuleItems() {}
}
