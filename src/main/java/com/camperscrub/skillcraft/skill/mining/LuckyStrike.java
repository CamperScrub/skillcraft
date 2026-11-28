package com.camperscrub.skillcraft.skill.mining;

/**
 * Lucky Strike numbers. Pure logic: no Minecraft classes.
 *
 * <p>From Mining level 400, natural stone, deepslate and netherrack have a chance to drop one
 * bonus item. The chance is 1% at level 400 and jumps by 0.5% at every 100 levels, reaching 4%
 * at level 1000. It stays flat between jumps.
 */
public final class LuckyStrike {

    public static final int UNLOCK_LEVEL = 400;

    private LuckyStrike() {
    }

    /**
     * The chance (0 to 1) at a Mining level.
     *
     * <p>EXERCISE 1. 0 below level 400. From 400: 1% plus 0.5% for every <em>whole</em> 100 levels
     * past 400. So 400 and 499 → 0.01, 500 → 0.015, 950 → 0.035, 1000 → 0.04.
     * Hint: integer division throws away the remainder, which is exactly the "whole 100s" part.
     */
    public static double chanceAtLevel(int level) {
        if (level < UNLOCK_LEVEL) {
            return 0;
        }
        int jumps = (level - UNLOCK_LEVEL) / 100;
        return 0.01 + 0.005 * jumps;
    }

    public static boolean unlockedBetween(int oldLevel, int newLevel) {
        return oldLevel < UNLOCK_LEVEL && newLevel >= UNLOCK_LEVEL;
    }

    /** True if a level-up from {@code oldLevel} to {@code newLevel} raised the chance after it was already unlocked. */
    public static boolean improvedBetween(int oldLevel, int newLevel) {
        return oldLevel >= UNLOCK_LEVEL && chanceAtLevel(newLevel) > chanceAtLevel(oldLevel);
    }
}