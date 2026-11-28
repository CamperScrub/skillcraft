package com.camperscrub.skillcraft.ability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbilityTimingsTest {

    @Test
    void lockedBelowLevel50() {
        assertEquals(0, AbilityTimings.durationSeconds(0));
        assertEquals(0, AbilityTimings.durationSeconds(49));
        assertEquals(0, AbilityTimings.steps(49));
    }

    @Test
    void fiveSecondsAtUnlock() {
        assertEquals(5, AbilityTimings.durationSeconds(50));
        assertEquals(5, AbilityTimings.durationSeconds(99));
    }

    @Test
    void oneMoreSecondEvery50Levels() {
        assertEquals(6, AbilityTimings.durationSeconds(100));
        assertEquals(14, AbilityTimings.durationSeconds(500));
        assertEquals(20, AbilityTimings.durationSeconds(812));
    }

    @Test
    void twentyFourSecondsAtMaxLevel() {
        assertEquals(24, AbilityTimings.durationSeconds(1000));
    }

    @Test
    void stepsMatchTheDocsYieldTable() {
        // +5 Yield per step: +5 at 50, +10 at 100, +50 at 500, +80 at 812, +100 at 1000
        assertEquals(1, AbilityTimings.steps(50));
        assertEquals(2, AbilityTimings.steps(100));
        assertEquals(10, AbilityTimings.steps(500));
        assertEquals(16, AbilityTimings.steps(812));
        assertEquals(20, AbilityTimings.steps(1000));
    }

    @Test
    void unlockOnlyWhenCrossingLevel50() {
        assertTrue(AbilityTimings.unlockedBetween(49, 50));
        assertTrue(AbilityTimings.unlockedBetween(30, 70));
        assertFalse(AbilityTimings.unlockedBetween(50, 51));
        assertFalse(AbilityTimings.unlockedBetween(10, 49));
    }

    @Test
    void formatsMinutesAndSeconds() {
        assertEquals("3:12", AbilityTimings.minutesAndSeconds(192));
        assertEquals("0:05", AbilityTimings.minutesAndSeconds(5));
    }
}
