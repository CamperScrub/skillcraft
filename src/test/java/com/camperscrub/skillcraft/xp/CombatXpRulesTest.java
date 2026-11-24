package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.data.SpawnOrigin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CombatXpRulesTest {

    private final CombatXpRules rules = new CombatXpRules(0.5);

    @Test
    void hitXpMatchesTheDesignDoc() {
        assertEquals(400, rules.hitXp(20, 2.0));    // a whole zombie
        assertEquals(165, rules.hitXp(5.5, 3.0));   // part of a skeleton
        assertEquals(20, rules.hitXp(20, 0.1));     // a witch
    }

    @Test
    void hitXpCapsHealthAt100() {
        assertEquals(1_000, rules.hitXp(150, 1.0));
    }

    @Test
    void hitXpIsNeverNegative() {
        assertEquals(0, rules.hitXp(-3, 2.0));
        assertEquals(0, rules.hitXp(20, 0.0));      // armor stands, snow golems
    }

    @Test
    void killBonusIsHalfOfAFullKill() {
        assertEquals(200, rules.killBonus(20, 2.0, false));   // zombie
        assertEquals(50, rules.killBonus(10, 1.0, false));    // cow
    }

    @Test
    void normalMobsCapHealthForTheBonus() {
        assertEquals(3_000, rules.killBonus(500, 6.0, false)); // 100 × 6 × 10 × 0.5
    }

    @Test
    void bossesSkipTheCap() {
        assertEquals(10_000, rules.killBonus(200, 10, true));  // Ender Dragon
        assertEquals(9_000, rules.killBonus(300, 6, true));    // Wither
        assertEquals(9_000, rules.killBonus(500, 3.6, true));  // Warden (the rounding trap)
    }

    @Test
    void factorZeroTurnsTheBonusOff() {
        assertEquals(0, new CombatXpRules(0).killBonus(20, 2.0, false));
    }

    @Test
    void negativeFactorIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CombatXpRules(-0.1));
    }

    @Test
    void originFactorsMatchTheDesignDoc() {
        assertEquals(1.0, rules.originFactor(SpawnOrigin.NATURAL));
        assertEquals(0.5, rules.originFactor(SpawnOrigin.SPAWNER));
        assertEquals(0.5, rules.originFactor(SpawnOrigin.NETHER_PORTAL));
        assertEquals(0.0, rules.originFactor(SpawnOrigin.SPAWN_EGG));
    }
}