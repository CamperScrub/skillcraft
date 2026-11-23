package com.camperscrub.skillcraft.xp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinearTailXpCurveTest {

    private static final int MAX_LEVEL = 1000;
    private static final long MAX_XP = 15_005_851;          // total XP for level 1000
    private static final long LEVEL_999_XP = 14_958_708;    // total XP for level 999

    private final XpCurve curve = new LinearTailXpCurve(LinearTailSettings.DEFAULTS);

    @Test
    void maxLevelComesFromSettings() {
        assertEquals(MAX_LEVEL, curve.maxLevel());
    }

    @Test
    void totalsMatchTheDesignDoc() {
        assertEquals(0, curve.totalXpForLevel(0));
        assertEquals(2_055, curve.totalXpForLevel(1));
        assertEquals(4_127, curve.totalXpForLevel(2));
        assertEquals(3_311_032, curve.totalXpForLevel(500));
        assertEquals(LEVEL_999_XP, curve.totalXpForLevel(999));
        assertEquals(MAX_XP, curve.totalXpForLevel(MAX_LEVEL));
    }

    @Test
    void levelsOutsideTheRangeAreClamped() {
        assertEquals(0, curve.totalXpForLevel(-5));
        assertEquals(MAX_XP, curve.totalXpForLevel(5000));
    }

    // One test run per line: xp, expected level
    @ParameterizedTest
    @CsvSource({
            "-100,       0",    // negative XP counts as level 0
            "0,          0",
            "2054,       0",    // one short of level 1
            "2055,       1",    // exactly level 1
            "4127,       2",
            "1000000,    240",
            "14958707,   998",  // one short of level 999
            "14958708,   999",  // exactly level 999
            "15005850,   999",  // one short of max
            "15005851,   1000", // exactly max
            "99999999,   1000"  // way past max
    })
    void levelForXp(long xp, int expectedLevel) {
        assertEquals(expectedLevel, curve.levelForXp(xp));
    }

    @Test
    void progressThroughALevel() {
        assertEquals(0f, curve.progressToNextLevel(0), 1e-6);
        assertEquals(0.321f, curve.progressToNextLevel(1_000_000), 0.001);
    }

    @Test
    void progressThroughLevelZero() {
        assertEquals(0.4866f, curve.progressToNextLevel(1_000), 0.001);
    }

    @Test
    void progressIsNeverNegative() {
        assertEquals(0f, curve.progressToNextLevel(-100), 1e-6);
    }

    @Test
    void progressStartsAtZeroOnTheLastLevel() {
        assertEquals(0f, curve.progressToNextLevel(LEVEL_999_XP), 1e-6);
    }

    @Test
    void progressIsFullAtMaxLevel() {
        assertEquals(1f, curve.progressToNextLevel(MAX_XP), 1e-6);
        assertEquals(1f, curve.progressToNextLevel(99_999_999), 1e-6);
    }

    @Test
    void invalidSettingsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new LinearTailSettings(MAX_LEVEL, 0, 17, 55, 111));
        assertThrows(IllegalArgumentException.class, () -> new LinearTailSettings(MAX_LEVEL, 2000, -1, 55, 111));
        assertThrows(IllegalArgumentException.class, () -> new LinearTailSettings(0, 2000, 17, 55, 111));
    }
}
