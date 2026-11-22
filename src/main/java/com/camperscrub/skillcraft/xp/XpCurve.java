package com.camperscrub.skillcraft.xp;

/**
 * Converts between total XP and levels. Levels run from 0 to maxLevel().
 * Implementations decide the shape of the curve.
 */
public interface XpCurve {

    /**
     * Highest achievable level, for example 999.
     */
    int maxLevel();

    /**
     * Total XP needed to reach a level from 0 XP.
     * Level 0 needs 0 XP. Levels outside 0..maxLevel() are clamped into that range.
     */
    long totalXpForLevel(int level);

    /**
     * Highest level whose total XP requirement is less than or equal to currentXp. Never above maxLevel().
     */
    int levelForXp(long currentXp);

    /**
     * Progress through the current level, from 0.0 to 1.0.
     * Returns 1.0 at maxLevel(), since there is no next level.
     */
    float progressToNextLevel(long currentXp);
}
