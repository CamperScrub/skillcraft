package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.data.SpawnOrigin;

import static java.lang.Math.min;
import static java.lang.Math.round;

/**
 * The Swordsmanship XP formulas
 */
public final class CombatXpRules {

    public static final double HEALTH_CAP = 100;   // most health that counts per hit, and for normal kill bonuses
    public static final double XP_PER_HEALTH = 10;
    public static final double FARM_FACTOR = 0.5;

    private final double killBonusFactor;

    public CombatXpRules(double killBonusFactor) {
        if (killBonusFactor < 0) {
            throw new IllegalArgumentException("killBonusFactor cannot be negative, got " + killBonusFactor);
        }
        this.killBonusFactor = killBonusFactor;
    }

    /**
     * XP for one hit: min(healthRemoved, 100) × multiplier × 10, rounded to the nearest whole XP.
     * Negative healthRemoved counts as 0.
     */
    public long hitXp(double healthRemoved, double multiplier) {
        long grantedXp = round(min(healthRemoved, 100) * multiplier * 10);
        if (grantedXp < 0) return 0;
        return grantedXp;
    }

    /**
     * Bonus XP for a killing blow: health × multiplier × 10 × killBonusFactor, rounded.
     * Normal mobs use min(maxHealth, 100); bosses use their full maxHealth.
     */
    public long killBonus(double maxHealth, double multiplier, boolean boss) {
        if (boss) {
            return round(maxHealth * multiplier * 10 * killBonusFactor);
        }
        return round(min(maxHealth, 100) * multiplier * 10 * killBonusFactor);
    }

    /**
     * Share of normal XP a mob gives, based on where it came from (see design doc).
     */
    public double originFactor(SpawnOrigin origin) {
        return switch (origin) {
            case NATURAL -> 1.0;
            case SPAWNER, NETHER_PORTAL -> 0.5;
            case SPAWN_EGG -> 0;
        };
    }
}