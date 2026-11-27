package com.camperscrub.skillcraft.proc;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PseudoRandomProcRollerTest {

    private static final UUID ALEX = UUID.randomUUID();
    private static final UUID STEVE = UUID.randomUUID();
    private static final ProcKey STONE = new ProcKey("lucky_strike", "stone");
    private static final ProcKey DEEPSLATE = new ProcKey("lucky_strike", "deepslate");

    /** Always rolls 0.5, so a fresh 25% counter misses 5 times, then hits once C * attempt passes 0.5. */
    private final ChanceSource source = () -> 0.5;
    private final PseudoRandomProcRoller roller = new PseudoRandomProcRoller(source);

    private int missesBeforeHit(UUID player, ProcKey key) {
        int misses = 0;
        while (!roller.roll(player, key, 0.25)) {
            misses++;
        }
        return misses;
    }

    @Test
    void eachKeyHasItsOwnCounter() {
        roller.roll(ALEX, STONE, 0.25);
        roller.roll(ALEX, STONE, 0.25);
        // Deepslate hasn't been rolled, so it starts fresh and needs the full run of misses.
        assertEquals(missesBeforeHit(STEVE, DEEPSLATE), missesBeforeHit(ALEX, DEEPSLATE));
    }

    @Test
    void eachPlayerHasTheirOwnCounter() {
        roller.roll(ALEX, STONE, 0.25);
        roller.roll(ALEX, STONE, 0.25);
        int alexLeft = missesBeforeHit(ALEX, STONE);
        int steveLeft = missesBeforeHit(STEVE, STONE);
        assertEquals(steveLeft - 2, alexLeft);
    }

    @Test
    void forgettingAPlayerResetsTheirCounters() {
        int fresh = missesBeforeHit(STEVE, STONE);
        roller.roll(ALEX, STONE, 0.25);
        roller.roll(ALEX, STONE, 0.25);
        roller.forget(ALEX);
        assertEquals(fresh, missesBeforeHit(ALEX, STONE));
    }

    @Test
    void plainRollerUsesTheChanceDirectly() {
        PlainProcRoller plain = new PlainProcRoller(() -> 0.3);
        assertTrue(plain.roll(ALEX, STONE, 0.31));
        assertFalse(plain.roll(ALEX, STONE, 0.29));
        assertFalse(plain.roll(ALEX, STONE, 0));
    }
}
