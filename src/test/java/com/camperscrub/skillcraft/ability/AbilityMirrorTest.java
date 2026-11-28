package com.camperscrub.skillcraft.ability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbilityMirrorTest {

    private static final String BREAKER = "super_breaker";

    private final AbilityMirror mirror = new AbilityMirror();

    @Test
    void unknownAbilitiesAreNotActive() {
        assertFalse(mirror.isActive(BREAKER, 1000));
    }

    @Test
    void activeUntilItsTimeRunsOut() {
        mirror.update(BREAKER, AbilityPhase.ACTIVE, 1000, 100, 100);
        assertTrue(mirror.isActive(BREAKER, 1000));
        assertTrue(mirror.isActive(BREAKER, 1099));
        assertFalse(mirror.isActive(BREAKER, 1100));
    }

    @Test
    void otherPhasesAreNotActive() {
        mirror.update(BREAKER, AbilityPhase.READIED, 1000, 80, 100);
        assertFalse(mirror.isActive(BREAKER, 1000));
        mirror.update(BREAKER, AbilityPhase.COOLDOWN, 1000, 4800, 100);
        assertFalse(mirror.isActive(BREAKER, 1000));
    }

    @Test
    void miningWhileReadiedStartsItRightAway() {
        mirror.update(BREAKER, AbilityPhase.READIED, 1000, 80, 100);
        assertTrue(mirror.predictActivation(BREAKER, 1010));
        assertTrue(mirror.isActive(BREAKER, 1010));
        assertTrue(mirror.isActive(BREAKER, 1109));
        assertFalse(mirror.isActive(BREAKER, 1110));
    }

    @Test
    void cannotPredictWithoutReadying() {
        assertFalse(mirror.predictActivation(BREAKER, 1000));
        mirror.update(BREAKER, AbilityPhase.COOLDOWN, 1000, 4800, 100);
        assertFalse(mirror.predictActivation(BREAKER, 1010));
        assertFalse(mirror.isActive(BREAKER, 1010));
    }

    @Test
    void cannotPredictAfterTheReadyLapsed() {
        mirror.update(BREAKER, AbilityPhase.READIED, 1000, 80, 100);
        assertFalse(mirror.predictActivation(BREAKER, 1080));
    }

    @Test
    void theServersNextMessageWins() {
        mirror.update(BREAKER, AbilityPhase.READIED, 1000, 80, 100);
        mirror.predictActivation(BREAKER, 1010);
        mirror.update(BREAKER, AbilityPhase.READY, 1011, 0, 100);
        assertFalse(mirror.isActive(BREAKER, 1011));
    }

    @Test
    void clearForgetsEverything() {
        mirror.update(BREAKER, AbilityPhase.ACTIVE, 1000, 100, 100);
        mirror.clear();
        assertFalse(mirror.isActive(BREAKER, 1000));
    }
}
