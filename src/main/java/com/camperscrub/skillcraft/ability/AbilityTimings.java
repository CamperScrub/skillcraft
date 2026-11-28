package com.camperscrub.skillcraft.ability;

/**
 * Numbers shared by every super ability. Pure logic: no Minecraft classes.
 *
 * <p>Abilities unlock at level 50. They last 5 seconds at level 50 plus 1 second every 50 levels
 * after that (24 seconds at level 1000), and their growing effect steps up at the same times.
 */
public final class AbilityTimings {

    public static final int UNLOCK_LEVEL = 50;
    public static final int TICKS_PER_SECOND = 20;
    public static final int READIED_SECONDS = 4;
    public static final int COOLDOWN_SECONDS = 240;

    private AbilityTimings() {
    }

    /**
     * How long the ability lasts, in seconds.
     *
     * <p>EXERCISE 3a. 0 below level 50. From 50: 5 seconds plus 1 for every whole 50 levels past
     * 50. So 50 and 99 → 5, 100 → 6, 500 → 14, 1000 → 24.
     */
    public static int durationSeconds(int level) {
        if (level < UNLOCK_LEVEL) {
            return 0;
        }
        return 4 + (level / 50);
    }

    /**
     * How many times the growing effect has stepped up: 1 at level 50, 2 at 100 … 20 at 1000.
     * 0 below level 50.
     *
     * <p>EXERCISE 3b. Gathering abilities give +5 Yield per step, so this is what makes Super
     * Breaker's bonus +5 at level 50, +50 at 500 and +100 at 1000.
     */
    public static int steps(int level) {
        if (level < UNLOCK_LEVEL) {
            return 0;
        }
        return level / 50;
    }

    /** True if a level-up from {@code oldLevel} to {@code newLevel} crosses level 50. */
    public static boolean unlockedBetween(int oldLevel, int newLevel) {
        return oldLevel < UNLOCK_LEVEL && newLevel >= UNLOCK_LEVEL;
    }

    /** "3:12" for 192 seconds. */
    public static String minutesAndSeconds(long seconds) {
        return "%d:%02d".formatted(seconds / 60, seconds % 60);
    }
}