package com.camperscrub.skillcraft.yield;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YieldTest {

    @Test
    void noYieldGivesNothing() {
        assertEquals(0, Yield.guaranteedCopies(0));
        assertEquals(0, Yield.leftoverChance(0), 1e-9);
    }

    @Test
    void negativeYieldGivesNothing() {
        assertEquals(0, Yield.guaranteedCopies(-5));
        assertEquals(0, Yield.leftoverChance(-5), 1e-9);
    }

    @Test
    void underOneHundredIsOnlyAChance() {
        assertEquals(0, Yield.guaranteedCopies(81.2));
        assertEquals(0.812, Yield.leftoverChance(81.2), 1e-9);
    }

    @Test
    void justUnderOneHundredIsStillNotGuaranteed() {
        assertEquals(0, Yield.guaranteedCopies(99.9));
        assertEquals(0.999, Yield.leftoverChance(99.9), 1e-9);
    }

    @Test
    void exactlyOneHundredIsOneCopyAndNoChance() {
        assertEquals(1, Yield.guaranteedCopies(100));
        assertEquals(0, Yield.leftoverChance(100), 1e-9);
    }

    @Test
    void overOneHundredIsCopiesPlusAChance() {
        assertEquals(2, Yield.guaranteedCopies(250));
        assertEquals(0.5, Yield.leftoverChance(250), 1e-9);
    }
}