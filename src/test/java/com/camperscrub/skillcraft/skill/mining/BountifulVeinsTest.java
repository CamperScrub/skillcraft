package com.camperscrub.skillcraft.skill.mining;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BountifulVeinsTest {

    @Test
    void startsAtZero() {
        assertEquals(0, BountifulVeins.yieldAtLevel(0));
    }

    @Test
    void halfwayIsFifty() {
        assertEquals(50, BountifulVeins.yieldAtLevel(500));
    }

    @Test
    void maxLevelIsExactlyOneHundred() {
        assertEquals(100, BountifulVeins.yieldAtLevel(1000));
    }

    @Test
    void everyLevelIsAnExactTenth() {
        for (int level = 0; level <= 1000; level++) {
            double expected = Double.parseDouble(level / 10 + "." + level % 10); // 812 → "81.2"
            assertEquals(expected, BountifulVeins.yieldAtLevel(level), "level " + level);
        }
    }
}