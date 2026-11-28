package com.camperscrub.skillcraft.yield;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelYieldTest {

    @Test
    void startsAtZero() {
        assertEquals(0, LevelYield.yieldAtLevel(0));
    }

    @Test
    void halfwayIsFifty() {
        assertEquals(50, LevelYield.yieldAtLevel(500));
    }

    @Test
    void maxLevelIsExactlyOneHundred() {
        assertEquals(100, LevelYield.yieldAtLevel(1000));
    }

    @Test
    void everyLevelIsAnExactTenth() {
        for (int level = 0; level <= 1000; level++) {
            double expected = Double.parseDouble(level / 10 + "." + level % 10); // 812 → "81.2"
            assertEquals(expected, LevelYield.yieldAtLevel(level), "level " + level);
        }
    }
}
