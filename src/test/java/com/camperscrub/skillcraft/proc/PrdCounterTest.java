package com.camperscrub.skillcraft.proc;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PrdCounterTest {

    private static final ChanceSource ALWAYS_MISS = () -> 0.999_999;
    private static final ChanceSource ALWAYS_HIT = () -> 0.0;

    @Test
    void aMissMovesToTheNextAttempt() {
        PrdCounter counter = new PrdCounter();
        assertFalse(counter.roll(0.25, ALWAYS_MISS));
        assertEquals(2, counter.attempt());
    }

    @Test
    void aHitResetsTheCounter() {
        PrdCounter counter = new PrdCounter();
        counter.roll(0.25, ALWAYS_MISS);
        counter.roll(0.25, ALWAYS_MISS);
        assertTrue(counter.roll(0.25, ALWAYS_HIT));
        assertEquals(1, counter.attempt());
    }

    @Test
    void aZeroChanceNeverHitsAndNeverBuildsUp() {
        PrdCounter counter = new PrdCounter();
        assertFalse(counter.roll(0, ALWAYS_HIT));
        assertEquals(1, counter.attempt());
    }

    @Test
    void twentyFivePercentNeverMissesMoreThanElevenTimes() {
        PrdCounter counter = new PrdCounter();
        for (int i = 0; i < 11; i++) {
            assertFalse(counter.roll(0.25, ALWAYS_MISS), "attempt " + (i + 1));
        }
        assertTrue(counter.roll(0.25, ALWAYS_MISS), "the 12th attempt is guaranteed");
    }

    @Test
    void theFirstAttemptUsesTheStartingChance() {
        double c = PseudoRandom.startingChance(0.25);
        PrdCounter justBelow = new PrdCounter();
        PrdCounter justAbove = new PrdCounter();
        assertTrue(justBelow.roll(0.25, () -> c - 1e-6));
        assertFalse(justAbove.roll(0.25, () -> c + 1e-6));
    }

    @Test
    void longRunAverageMatchesTheNominalChance() {
        Random random = new Random(42);
        PrdCounter counter = new PrdCounter();
        int rolls = 200_000;
        int hits = 0;
        for (int i = 0; i < rolls; i++) {
            if (counter.roll(0.25, random::nextDouble)) {
                hits++;
            }
        }
        assertEquals(0.25, hits / (double) rolls, 0.005);
    }
}
