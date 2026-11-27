package com.camperscrub.skillcraft.proc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maths for pseudo-random chance (the same idea Dota uses).
 *
 * <p>A proc with nominal chance p does not roll p every time. It starts at a smaller chance C,
 * and every miss adds another C: C, 2C, 3C... A hit resets it to C. C is picked so that the
 * long-run average is exactly p, but long dry streaks can't happen.
 */
public final class PseudoRandom {

    private static final Map<Double, Double> CACHE = new ConcurrentHashMap<>();

    private PseudoRandom() {
    }

    /**
     * The long-run proc chance you get from starting chance {@code c}.
     *
     * <p>EXERCISE 1. Walk through the attempts n = 1, 2, 3... On attempt n the chance is
     * {@code min(1, n * c)}. Keep track of:
     * <ul>
     *   <li>the chance we haven't proced yet before this attempt (starts at 1),</li>
     *   <li>the expected number of attempts per proc: add {@code n * notYet * chanceNow} each step.</li>
     * </ul>
     * Stop once the chance reaches 1 (the proc is guaranteed). The answer is
     * {@code 1 / expectedAttempts}. Return 0 if {@code c <= 0}.
     */
    public static double averageChance(double c) {
        if (c <= 0) {
            return 0;                       // otherwise the loop below never ends
        }
        double expected = 0;
        double notYet = 1;
        for (int n = 1; notYet > 0; n++) {
            double chanceNow = Math.min(1, n * c);
            expected += n * notYet * chanceNow;
            notYet *= (1 - chanceNow);
        }
        return 1 / expected;
    }

    /**
     * The starting chance C that gives a long-run chance of {@code p}.
     *
     * <p>EXERCISE 2. There's no neat formula, so search for it. {@link #averageChance} goes up
     * when C goes up, which means a binary search works: start with low = 0 and high = p
     * (C is never bigger than p), try the middle, and keep the half the answer must be in.
     * 60 rounds is plenty. Return 0 for p <= 0 and 1 for p >= 1.
     *
     * <p>This method is already cached for you: write the search in {@link #search}.
     */
    public static double startingChance(double p) {
        if (p <= 0) {
            return 0;
        }
        if (p >= 1) {
            return 1;
        }
        return CACHE.computeIfAbsent(p, PseudoRandom::search);
    }

    private static double search(double p) {
        double low = 0;
        double high = p;
        for (int i = 0; i < 60; i++) {
            double mid = (low + high) / 2;
            if (averageChance(mid) < p) {
                low = mid;
            } else {
                high = mid;
            }
        }
        return (low + high) / 2;
    }
}
