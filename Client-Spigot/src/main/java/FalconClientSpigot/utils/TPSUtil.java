package FalconClientSpigot.utils;

import java.text.DecimalFormat;
import java.util.function.LongPredicate;

public final class TPSUtil {
    private static final DecimalFormat FORMAT = new DecimalFormat("###.00");
    private static final LongPredicate NOT_ZERO = l -> l != 0;

    private TPSUtil() {
    }

    public static double toMilliseconds(final long time) {
        return time * 1.0E-6D;
    }

    public static double toMilliseconds(final double time) {
        return time * 1.0E-6D;
    }

    public static double average(final long[] longs) {
        long i = 0L;
        for (final long l : longs) {
            i += l;
        }
        return i / (double) longs.length;
    }
}