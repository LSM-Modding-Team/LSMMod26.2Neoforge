package net.nicomar2009.lsmmod.util;

/** Descriptions of the shipped placeholder portrait layers, independent of Jade/client classes. */
public final class NpcAppearanceCatalog {
    private NpcAppearanceCatalog() { }
    private static final String[] SKIN = {"#F3D8C5", "#EFD1BC", "#EBCAB3", "#E8C8B9", "#E7C5AF", "#E4C0A8", "#DFC0AC", "#DEB99F", "#D8AE8B", "#D3A580", "#CDA477", "#D3A17C", "#D1A184", "#D0A07A", "#CAA073", "#CE9D80", "#C99D83", "#CE9B75", "#C79C70", "#CB997C", "#CC9974", "#C69A80", "#CA9771", "#C4986C", "#C3977D", "#C89578", "#C89570", "#C0947A", "#C6926C", "#C19469", "#C59174", "#C49069", "#BD9177", "#BE9065", "#C28D70", "#C28D67", "#BA8E74", "#C08B65", "#BB8C62", "#BF896C", "#BE8963", "#B78B71", "#BC8761", "#B8885E", "#BC8568", "#B4886E", "#BA855F", "#B8835D", "#B1856B", "#B5845B", "#B98164", "#B6815B", "#AE8268", "#B47F59", "#B28057", "#B67D60", "#AB7F65", "#AF7C54", "#B3795C", "#A87C62", "#AC7850", "#B07558", "#754C36", "#533526"};
    private static final String[] EYES = {"#16100c", "#2b1b12", "#43291b", "#60422c", "#7b5a37", "#8a7046", "#647044", "#3d703a"};
    private static final String[] GLASSES = {"#ffffff", "#dddddd", "#bbbbbb", "#999999", "#777777", "#555555", "#333333", "#000000"};
    private static final String[] MALE = {"school_side_part / straight", "school_left_part / straight", "short_crop / straight", "textured_crop / straight", "crew_cut / straight", "taper_crop / straight", "low_fade / straight", "mid_fade / straight", "high_fade / straight", "short_quiff / straight", "side_swept / straight", "short_fringe / straight", "rounded_fringe / straight", "spiky_crop / straight", "comb_over / straight", "short_buzz / straight", "wavy_side_part / wavy", "wavy_crop / wavy", "wavy_fringe / wavy", "wavy_layers / wavy", "wavy_center_part / wavy", "wavy_swept / wavy", "wavy_taper / wavy", "wavy_rounded / wavy", "curly_short / curly", "curly_crop / curly", "curly_fringe / curly", "curly_taper / curly", "curly_rounded / curly", "curly_side_part / curly", "curly_layers / curly", "curly_compact / curly"};
    private static final String[] FEMALE = {"short_straight / straight", "short_wavy / wavy", "short_curly / curly", "school_ponytail / straight", "long_straight / straight", "long_curly / curly", "high_ponytail / straight", "double_ponytail / straight", "straight_bob / straight", "long_side_part / straight", "single_braid / straight", "double_braid / straight", "long_fringe / straight", "long_center_part / straight", "school_pixie / straight", "rounded_bob / straight", "long_layers / straight", "school_side_part / straight", "long_tucked / straight", "wavy_ponytail / wavy", "wavy_bob / wavy", "wavy_fringe / wavy", "long_wavy / wavy", "wavy_double_ponytail / wavy", "wavy_layers / wavy", "wavy_side_part / wavy", "curly_crop / curly", "curly_bob / curly", "curly_ponytail / curly", "long_curly_side_part / curly", "curly_double_ponytail / curly", "curly_layers / curly"};
    public static String details(String key, int index) {
        String[] values = switch (key) {
            case "skinColor" -> SKIN;
            case "eyeColor" -> EYES;
            case "glassesType" -> GLASSES;
            case "haircutMale" -> MALE;
            case "haircutFemale" -> FEMALE;
            default -> new String[0];
        };
        int slot = key.equals("glassesType") ? index - 1 : index;
        return slot >= 0 && slot < values.length ? " (" + values[slot] + ")" : "";
    }
}
