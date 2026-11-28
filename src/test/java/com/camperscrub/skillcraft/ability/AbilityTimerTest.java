package com.camperscrub.skillcraft.ability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbilityTimerTest {

    private static final long READIED = 80;   // 4 s
    private static final long ACTIVE = 100;   // 5 s
    private static final long COOLDOWN = 4800; // 240 s

    @Test
    void startsReady() {
        assertEquals(AbilityPhase.READY, new AbilityTimer().phase(1000));
    }

    @Test
    void readyingMakesItReadiedUntilItLapses() {
        AbilityTimer timer = new AbilityTimer();
        assertTrue(timer.ready(1000, READIED));
        assertEquals(AbilityPhase.READIED, timer.phase(1000));
        assertEquals(AbilityPhase.READIED, timer.phase(1079));
        assertEquals(AbilityPhase.READY, timer.phase(1080));
    }

    @Test
    void loweringGoesBackToReady() {
        AbilityTimer timer = new AbilityTimer();
        timer.ready(1000, READIED);
        timer.lower();
        assertEquals(AbilityPhase.READY, timer.phase(1001));
    }

    @Test
    void cannotActivateWithoutReadyingFirst() {
        AbilityTimer timer = new AbilityTimer();
        assertFalse(timer.activate(1000, ACTIVE, COOLDOWN));
        assertEquals(AbilityPhase.READY, timer.phase(1000));
    }

    @Test
    void activatingRunsThenCoolsDownThenIsReadyAgain() {
        AbilityTimer timer = new AbilityTimer();
        timer.ready(1000, READIED);
        assertTrue(timer.activate(1010, ACTIVE, COOLDOWN));
        assertEquals(AbilityPhase.ACTIVE, timer.phase(1010));
        assertEquals(AbilityPhase.ACTIVE, timer.phase(1109));
        assertEquals(AbilityPhase.COOLDOWN, timer.phase(1110));
        assertEquals(AbilityPhase.COOLDOWN, timer.phase(1110 + COOLDOWN - 1));
        assertEquals(AbilityPhase.READY, timer.phase(1110 + COOLDOWN));
    }

    @Test
    void activatingUsesUpTheReadiedState() {
        AbilityTimer timer = new AbilityTimer();
        timer.ready(1000, READIED);
        timer.activate(1010, ACTIVE, COOLDOWN);
        assertFalse(timer.activate(1020, ACTIVE, COOLDOWN), "can't start again while active");
    }

    @Test
    void cannotReadyDuringCooldown() {
        AbilityTimer timer = new AbilityTimer();
        timer.ready(1000, READIED);
        timer.activate(1000, ACTIVE, COOLDOWN);
        assertFalse(timer.ready(2000, READIED));
        assertEquals(AbilityPhase.COOLDOWN, timer.phase(2000));
    }

    @Test
    void cannotReadyWhileAlreadyReadied() {
        AbilityTimer timer = new AbilityTimer();
        timer.ready(1000, READIED);
        assertFalse(timer.ready(1010, READIED));
        assertEquals(AbilityPhase.READY, timer.phase(1080), "the second press didn't extend it");
    }

    @Test
    void ticksLeftCountsDownTheCurrentPhase() {
        AbilityTimer timer = new AbilityTimer();
        timer.ready(1000, READIED);
        timer.activate(1000, ACTIVE, COOLDOWN);
        assertEquals(60, timer.ticksLeft(1040));
        assertEquals(COOLDOWN, timer.ticksLeft(1100));
    }
}
