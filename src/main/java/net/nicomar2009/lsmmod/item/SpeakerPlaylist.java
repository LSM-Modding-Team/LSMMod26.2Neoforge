package net.nicomar2009.lsmmod.item;

import java.util.function.IntUnaryOperator;

/** Persistent shuffle-cycle selection, independent of the game API. */
public final class SpeakerPlaylist {
    public record Selection(int track, int[] order, int nextIndex) {}

    private SpeakerPlaylist() {}

    public static Selection next(int count, int[] order, int index, int last,
                                 IntUnaryOperator random) {
        if (count < 1) throw new IllegalArgumentException("Empty playlist");
        if (!validOrder(count, order) || index < 0 || index >= count) {
            order = new int[count];
            for (int i = 0; i < count; i++) order[i] = i;
            for (int i = count - 1; i > 0; i--) {
                int other = random.applyAsInt(i + 1);
                int value = order[i];
                order[i] = order[other];
                order[other] = value;
            }
            // Avoid repeating the final song immediately across cycle boundaries.
            if (count > 1 && order[0] == last) {
                int other = 1 + random.applyAsInt(count - 1);
                order[0] = order[other];
                order[other] = last;
            }
            index = 0;
        }
        return new Selection(order[index], order, index + 1);
    }

    private static boolean validOrder(int count, int[] order) {
        if (order.length != count) return false;
        boolean[] seen = new boolean[count];
        for (int track : order) {
            if (track < 0 || track >= count || seen[track]) return false;
            seen[track] = true;
        }
        return true;
    }
}
