package net.nicomar2009.lsmmod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.item.AwningItem;
import net.nicomar2009.lsmmod.item.RulerItem;
import net.nicomar2009.lsmmod.item.SchoolUniformItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.nicomar2009.lsmmod.item.InstrumentItem;

/** Item registry for the school blocks; standalone items are planned. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LSMMod.MOD_ID);

    public static final java.util.List<DeferredItem<Item>> FOLDERS = registerStationery("folder");
    public static final java.util.List<DeferredItem<Item>> NOTEBOOKS = registerStationery("notebook");

    private static java.util.List<DeferredItem<Item>> registerStationery(String kind) {
        var variants = new java.util.ArrayList<DeferredItem<Item>>();
        for (String color : new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime",
                "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"}) {
            variants.add(ITEMS.registerItem(color + "_" + kind, Item::new));
        }
        return java.util.List.copyOf(variants);
    }

    // Block item for the elementary chair (registry name taken from the block: "lsmmod:elementary_chair")
    public static final DeferredItem<BlockItem> ELEMENTARY_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.ELEMENTARY_CHAIR);

    // Block item for the high school chair (registry name taken from the block: "lsmmod:high_school_chair")
    public static final DeferredItem<BlockItem> HIGH_SCHOOL_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.HIGH_SCHOOL_CHAIR);

    // Block item for the desk (placing it creates both halves)
    public static final DeferredItem<BlockItem> STUDENTS_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.STUDENTS_DESK);

    // Block item for the locker
    public static final DeferredItem<BlockItem> LOCKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.LOCKER);

    // Block item for the teachers chair (registry name taken from the block: "lsmmod:teachers_chair")
    public static final DeferredItem<BlockItem> TEACHERS_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEACHERS_CHAIR);

    // Block item for the desk (placing it creates both halves)
    public static final DeferredItem<BlockItem> TEACHERS_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEACHERS_DESK);

    // Block item for the elementary desk (a single block with 9 slots)
    public static final DeferredItem<BlockItem> ELEMENTARY_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.ELEMENTARY_DESK);

    // Block item for the statue of San Martín De Porres (a double block)
    public static final DeferredItem<BlockItem> SAN_MARTIN_DE_PORRES =
            ITEMS.registerSimpleBlockItem(ModBlocks.SAN_MARTIN_DE_PORRES);

    public static final DeferredItem<BlockItem> SCHOOL_SHIELD =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SHIELD);

    public static final DeferredItem<BlockItem> ENGLISHROOMCHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.ENGLISHROOMCHAIR);

    public static final DeferredItem<BlockItem> HALLCHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.HALLCHAIR);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_WHITE);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_RED);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE_ARMS =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_WHITE_ARMS);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED_ARMS =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_RED_ARMS);

    public static final DeferredItem<BlockItem> LAPTOP =
            ITEMS.registerSimpleBlockItem(ModBlocks.LAPTOP);

    public static final DeferredItem<BlockItem> MOISES_LAPTOP =
            ITEMS.registerSimpleBlockItem(ModBlocks.MOISES_LAPTOP);

    public static final DeferredItem<BlockItem> PC =
            ITEMS.registerSimpleBlockItem(ModBlocks.PC);

    public static final DeferredItem<BlockItem> COMPUTER_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.COMPUTER_DESK);

    public static final DeferredItem<BlockItem> GRAY_COMPUTER_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.GRAY_COMPUTER_DESK);

    public static final DeferredItem<BlockItem> SCHOOL_BELL =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_BELL);

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

    public static final DeferredItem<BlockItem> DINING_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.DINING_TABLE);

    public static final DeferredItem<BlockItem> DINING_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.DINING_CHAIR);

    public static final DeferredItem<BlockItem> SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPEAKER);

    public static final DeferredItem<BlockItem> TALL_SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.TALL_SPEAKER);

    public static final DeferredItem<BlockItem> DRINKING_FOUNTAIN =
            ITEMS.registerSimpleBlockItem(ModBlocks.DRINKING_FOUNTAIN);

    public static final DeferredItem<BlockItem> KIOSK_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.KIOSK_TABLE);

    public static final DeferredItem<BlockItem> ART_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.ART_TABLE);

    public static final DeferredItem<BlockItem> STOOL =
            ITEMS.registerSimpleBlockItem(ModBlocks.STOOL);

    public static final DeferredItem<BlockItem> VAULTING_BOX =
            ITEMS.registerSimpleBlockItem(ModBlocks.VAULTING_BOX);

    public static final DeferredItem<BlockItem> CLASSROOM_DOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_DOOR);

    public static final DeferredItem<BlockItem> MANUEL_TIRADO_BUST =
            ITEMS.registerSimpleBlockItem(ModBlocks.MANUEL_TIRADO_BUST);

    public static final DeferredItem<BlockItem> INFIRMARY_COT =
            ITEMS.registerSimpleBlockItem(ModBlocks.INFIRMARY_COT);

    public static final DeferredItem<Item> GUITAR =
            ITEMS.registerItem("guitar",
                    props -> new InstrumentItem(props.stacksTo(1), ModSounds.GUITAR, 10.0F, 8, 320));

    public static final DeferredItem<Item> MANDOLIN =
            ITEMS.registerItem("mandolin",
                    props -> new InstrumentItem(props.stacksTo(1), ModSounds.MANDOLIN, 10.0F, 8, 320));

    public static final DeferredItem<Item> FLUTE =
            ITEMS.registerItem("flute",
                    props -> new InstrumentItem(props.stacksTo(1), ModSounds.FLUTE, 2.0F, 16, 160));

    public static final DeferredItem<Item> TAMBOURINE =
            ITEMS.registerItem("tambourine",
                    props -> new InstrumentItem(props.stacksTo(1), ModSounds.TAMBOURINE, 2.0F, 16, 160));

    public static final DeferredItem<Item> VIOLIN =
            ITEMS.registerItem("violin",
                    props -> new InstrumentItem(props.stacksTo(1), ModSounds.VIOLIN, 10.0F, 8, 320));

    public static final DeferredItem<Item> MELODICA =
            ITEMS.registerItem("melodica",
                    props -> new InstrumentItem(props.stacksTo(1), ModSounds.MELODICA, 2.0F, 16, 160));

    public static final DeferredItem<BlockItem> AWNING_SUPPORT =
            ITEMS.registerItem("awning_support", props -> new net.nicomar2009.lsmmod.item.AwningSupportItem(ModBlocks.AWNING_SUPPORT.get(), props));

    public static final DeferredItem<Item> AWNING =
            ITEMS.registerItem("awning", props -> new AwningItem(props, ModBlocks.AWNING));

    public static final DeferredItem<Item> PLAYGROUND_AWNING =
            ITEMS.registerItem("playground_awning", props -> new AwningItem(props, ModBlocks.PLAYGROUND_AWNING));

    public static final DeferredItem<Item> ELEMENTARY_PLAYGROUND_AWNING =
            ITEMS.registerItem("elementary_playground_awning", props -> new AwningItem(props, ModBlocks.ELEMENTARY_PLAYGROUND_AWNING));

    public static final DeferredItem<Item> WHITE_AWNING =
            ITEMS.registerItem("white_awning", props -> new AwningItem(props, ModBlocks.WHITE_AWNING));

    public static final DeferredItem<Item> ORANGE_AWNING =
            ITEMS.registerItem("orange_awning", props -> new AwningItem(props, ModBlocks.ORANGE_AWNING));

    public static final DeferredItem<Item> MAGENTA_AWNING =
            ITEMS.registerItem("magenta_awning", props -> new AwningItem(props, ModBlocks.MAGENTA_AWNING));

    public static final DeferredItem<Item> LIGHT_BLUE_AWNING =
            ITEMS.registerItem("light_blue_awning", props -> new AwningItem(props, ModBlocks.LIGHT_BLUE_AWNING));

    public static final DeferredItem<Item> YELLOW_AWNING =
            ITEMS.registerItem("yellow_awning", props -> new AwningItem(props, ModBlocks.YELLOW_AWNING));

    public static final DeferredItem<Item> LIME_AWNING =
            ITEMS.registerItem("lime_awning", props -> new AwningItem(props, ModBlocks.LIME_AWNING));

    public static final DeferredItem<Item> PINK_AWNING =
            ITEMS.registerItem("pink_awning", props -> new AwningItem(props, ModBlocks.PINK_AWNING));

    public static final DeferredItem<Item> GRAY_AWNING =
            ITEMS.registerItem("gray_awning", props -> new AwningItem(props, ModBlocks.GRAY_AWNING));

    public static final DeferredItem<Item> LIGHT_GRAY_AWNING =
            ITEMS.registerItem("light_gray_awning", props -> new AwningItem(props, ModBlocks.LIGHT_GRAY_AWNING));

    public static final DeferredItem<Item> CYAN_AWNING =
            ITEMS.registerItem("cyan_awning", props -> new AwningItem(props, ModBlocks.CYAN_AWNING));

    public static final DeferredItem<Item> PURPLE_AWNING =
            ITEMS.registerItem("purple_awning", props -> new AwningItem(props, ModBlocks.PURPLE_AWNING));

    public static final DeferredItem<Item> BLUE_AWNING =
            ITEMS.registerItem("blue_awning", props -> new AwningItem(props, ModBlocks.BLUE_AWNING));

    public static final DeferredItem<Item> BROWN_AWNING =
            ITEMS.registerItem("brown_awning", props -> new AwningItem(props, ModBlocks.BROWN_AWNING));

    public static final DeferredItem<Item> GREEN_AWNING =
            ITEMS.registerItem("green_awning", props -> new AwningItem(props, ModBlocks.GREEN_AWNING));

    public static final DeferredItem<Item> RED_AWNING =
            ITEMS.registerItem("red_awning", props -> new AwningItem(props, ModBlocks.RED_AWNING));

    public static final DeferredItem<Item> BLACK_AWNING =
            ITEMS.registerItem("black_awning", props -> new AwningItem(props, ModBlocks.BLACK_AWNING));

    public static Item awningVariant(String id) {
        return switch (id) {
            case "playground_awning" -> PLAYGROUND_AWNING.get();
            case "elementary_playground_awning" -> ELEMENTARY_PLAYGROUND_AWNING.get();
            case "white_awning" -> WHITE_AWNING.get();
            case "orange_awning" -> ORANGE_AWNING.get();
            case "magenta_awning" -> MAGENTA_AWNING.get();
            case "light_blue_awning" -> LIGHT_BLUE_AWNING.get();
            case "yellow_awning" -> YELLOW_AWNING.get();
            case "lime_awning" -> LIME_AWNING.get();
            case "pink_awning" -> PINK_AWNING.get();
            case "gray_awning" -> GRAY_AWNING.get();
            case "light_gray_awning" -> LIGHT_GRAY_AWNING.get();
            case "cyan_awning" -> CYAN_AWNING.get();
            case "purple_awning" -> PURPLE_AWNING.get();
            case "blue_awning" -> BLUE_AWNING.get();
            case "brown_awning" -> BROWN_AWNING.get();
            case "green_awning" -> GREEN_AWNING.get();
            case "red_awning" -> RED_AWNING.get();
            case "black_awning" -> BLACK_AWNING.get();
            default -> AWNING.get();
        };
    }

    public static final DeferredItem<SpawnEggItem> STUDENT_SPAWN_EGG =
            ITEMS.registerItem("student_spawn_egg", props -> new SpawnEggItem(props.spawnEgg(ModEntities.STUDENT.get())));

    public static final DeferredItem<SpawnEggItem> TEACHER_SPAWN_EGG =
            ITEMS.registerItem("teacher_spawn_egg", props -> new SpawnEggItem(props.spawnEgg(ModEntities.TEACHER.get())));

    public static final DeferredItem<RulerItem> WOODEN_RULER =
            ITEMS.registerItem("wooden_ruler", props -> new RulerItem(props, ToolMaterial.WOOD));

    public static final DeferredItem<RulerItem> STONE_RULER =
            ITEMS.registerItem("stone_ruler", props -> new RulerItem(props, ToolMaterial.STONE));

    public static final DeferredItem<RulerItem> GOLDEN_RULER =
            ITEMS.registerItem("golden_ruler", props -> new RulerItem(props, ToolMaterial.GOLD));

    public static final DeferredItem<RulerItem> IRON_RULER =
            ITEMS.registerItem("iron_ruler", props -> new RulerItem(props, ToolMaterial.IRON));

    public static final DeferredItem<RulerItem> DIAMOND_RULER =
            ITEMS.registerItem("diamond_ruler", props -> new RulerItem(props, ToolMaterial.DIAMOND));

    public static final DeferredItem<RulerItem> NETHERITE_RULER =
            ITEMS.registerItem("netherite_ruler", props -> new RulerItem(props.fireResistant(), ToolMaterial.NETHERITE));

    public static final DeferredItem<SchoolUniformItem> FAKE_SCHOOL_HAIRCUT =
            ITEMS.registerItem("fake_school_haircut", props -> new SchoolUniformItem(props, ArmorType.HELMET, "fake_school_haircut"));

    public static final DeferredItem<SchoolUniformItem> UNIFORM_POLO =
            ITEMS.registerItem("uniform_polo", props -> new SchoolUniformItem(props, ArmorType.CHESTPLATE, "uniform_polo"));

    public static final DeferredItem<SchoolUniformItem> UNIFORM_PANTS =
            ITEMS.registerItem("uniform_pants", props -> new SchoolUniformItem(props, ArmorType.LEGGINGS, "uniform_pants"));

    public static final DeferredItem<SchoolUniformItem> UNIFORM_SHOES =
            ITEMS.registerItem("uniform_shoes", props -> new SchoolUniformItem(props, ArmorType.BOOTS, "uniform_shoes"));

    public static final DeferredItem<SpawnEggItem> STUDENT_1P_SPAWN_EGG =
            registerClassroomEgg("student_1p_spawn_egg", "primary", 1);

    public static final DeferredItem<SpawnEggItem> STUDENT_2P_SPAWN_EGG =
            registerClassroomEgg("student_2p_spawn_egg", "primary", 2);

    public static final DeferredItem<SpawnEggItem> STUDENT_3P_SPAWN_EGG =
            registerClassroomEgg("student_3p_spawn_egg", "primary", 3);

    public static final DeferredItem<SpawnEggItem> STUDENT_4P_SPAWN_EGG =
            registerClassroomEgg("student_4p_spawn_egg", "primary", 4);

    public static final DeferredItem<SpawnEggItem> STUDENT_5P_SPAWN_EGG =
            registerClassroomEgg("student_5p_spawn_egg", "primary", 5);

    public static final DeferredItem<SpawnEggItem> STUDENT_6P_SPAWN_EGG =
            registerClassroomEgg("student_6p_spawn_egg", "primary", 6);

    public static final DeferredItem<SpawnEggItem> STUDENT_1S_SPAWN_EGG =
            registerClassroomEgg("student_1s_spawn_egg", "secondary", 1);

    public static final DeferredItem<SpawnEggItem> STUDENT_2S_SPAWN_EGG =
            registerClassroomEgg("student_2s_spawn_egg", "secondary", 2);

    public static final DeferredItem<SpawnEggItem> STUDENT_3S_SPAWN_EGG =
            registerClassroomEgg("student_3s_spawn_egg", "secondary", 3);

    public static final DeferredItem<SpawnEggItem> STUDENT_4S_SPAWN_EGG =
            registerClassroomEgg("student_4s_spawn_egg", "secondary", 4);

    public static final DeferredItem<SpawnEggItem> STUDENT_5S_SPAWN_EGG =
            registerClassroomEgg("student_5s_spawn_egg", "secondary", 5);

    private static DeferredItem<SpawnEggItem> registerClassroomEgg(String id, String level, int grade) {
        return ITEMS.registerItem(id, props -> {
            CompoundTag classroom = new CompoundTag();
            classroom.putString("Level", level);
            classroom.putInt("Grade", grade);
            CompoundTag entityData = new CompoundTag();
            entityData.put("StudentData", classroom);
            return new SpawnEggItem(props.spawnEgg(ModEntities.STUDENT.get())
                    .component(DataComponents.ENTITY_DATA, TypedEntityData.of(ModEntities.STUDENT.get(), entityData)));
        });
    }

    public static final DeferredItem<Item> NPC_SUMMON_HELPER =
            ITEMS.registerItem("npc_summon_helper", props -> new Item(props.stacksTo(1)));

    public static final DeferredItem<Item> PLASTIC_BALL = ball("plastic_ball", net.nicomar2009.lsmmod.ball.BallKind.PLASTIC);
    public static final DeferredItem<Item> FOOTBALL_BALL = ball("football_ball", net.nicomar2009.lsmmod.ball.BallKind.FOOTBALL);
    public static final DeferredItem<Item> BASKETBALL_BALL = ball("basketball_ball", net.nicomar2009.lsmmod.ball.BallKind.BASKETBALL);
    public static final DeferredItem<Item> VOLLEYBALL_BALL = ball("volleyball_ball", net.nicomar2009.lsmmod.ball.BallKind.VOLLEYBALL);
    private static DeferredItem<Item> ball(String id, net.nicomar2009.lsmmod.ball.BallKind kind) {
        return ITEMS.registerItem(id, props -> new net.nicomar2009.lsmmod.ball.BallItem(props.stacksTo(16), kind));
    }

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
