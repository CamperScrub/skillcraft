package com.camperscrub.skillcraft.yield;

/**
 * Yield maths. Pure logic: no Minecraft classes.
 *
 * <p>Every 100 Yield is one guaranteed extra copy of a block's drops. Whatever is left over is
 * the percent chance of one more copy. So 81.2 Yield is an 81.2% chance of one extra copy, and
 * 250 Yield is two extra copies plus a 50% chance of a third.
 */
public final class Yield {

    private Yield() {
    }

    /**
     * Extra copies that are guaranteed.
     *
     * <p>EXERCISE 1a. How many whole hundreds fit in {@code yield}: 250 → 2, 99.9 → 0, 100 → 1.
     * Return 0 for 0 or less.
     */
    public static int guaranteedCopies(double yield) {
        if (yield <= 0) {
            return 0;
        }
        return (int) (yield / 100.0);
    }

    /**
     * The chance (0 to 1) of one more copy on top of the guaranteed ones.
     *
     * <p>EXERCISE 1b. What's left after taking out the whole hundreds, as a fraction of 100:
     * 250 → 0.5, 81.2 → 0.812, 100 → 0. Return 0 for 0 or less.
     * Hint: reuse {@link #guaranteedCopies}.
     */
    public static double leftoverChance(double yield) {
        if (yield <= 0) {
            return 0;
        }
        return (yield / 100.0) - guaranteedCopies(yield);
    }
}