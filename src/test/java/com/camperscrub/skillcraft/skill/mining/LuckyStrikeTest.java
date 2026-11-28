package com.camperscrub.skillcraft.skill.mining;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LuckyStrikeTest {

    @Test
    void noChanceBeforeLevel400() {
        assertEquals(0, LuckyStrike.chanceAtLevel(0), 1e-9);
        assertEquals(0, LuckyStrike.chanceAtLevel(399), 1e-9);
    }

    @Test
    void onePercentAtLevel400() {
        assertEquals(0.01, LuckyStrike.chanceAtLevel(400), 1e-9);
    }

    @Test
    void staysFlatBetweenJumps() {
        assertEquals(0.01, LuckyStrike.chanceAtLevel(499), 1e-9);
        assertEquals(0.035, LuckyStrike.chanceAtLevel(950), 1e-9);
    }

    @Test
    void jumpsHalfAPercentEveryHundredLevels() {
        int[] levels = {500, 600, 700, 800, 900};
        double[] chances = {0.015, 0.02, 0.025, 0.03, 0.035};
        for (int i = 0; i < levels.length; i++) {
            assertEquals(chances[i], LuckyStrike.chanceAtLevel(levels[i]), 1e-9, "level " + levels[i]);
        }
    }

    @Test
    void fourPercentAtMaxLevel() {
        assertEquals(0.04, LuckyStrike.chanceAtLevel(1000), 1e-9);
    }

    @Test
    void reachingLevel400Unlocks() {
        assertTrue(LuckyStrike.unlockedBetween(399, 400));
    }

    @Test
    void jumpingPastLevel400Unlocks() {
        assertTrue(LuckyStrike.unlockedBetween(350, 420));
    }

    @Test
    void alreadyUnlockedDoesNotUnlockAgain() {
        assertFalse(LuckyStrike.unlockedBetween(400, 401));
    }

    @Test
    void stayingBelow400DoesNotUnlock() {
        assertFalse(LuckyStrike.unlockedBetween(100, 399));
    }

    @Test
    void improvesAtEachHundredAfterUnlocking() {
        assertTrue(LuckyStrike.improvedBetween(499, 500));
        assertTrue(LuckyStrike.improvedBetween(950, 1000));
    }

    @Test
    void doesNotImproveBetweenSteps() {
        assertFalse(LuckyStrike.improvedBetween(500, 599));
    }

    @Test
    void unlockingIsNotAnImprovement() {
        // 399 → 400 is the unlock message, not "improved"
        assertFalse(LuckyStrike.improvedBetween(399, 400));
        assertFalse(LuckyStrike.improvedBetween(350, 520));
    }
}
