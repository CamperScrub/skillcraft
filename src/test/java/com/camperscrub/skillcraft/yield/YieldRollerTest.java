package com.camperscrub.skillcraft.yield;

import com.camperscrub.skillcraft.proc.ProcKey;
import com.camperscrub.skillcraft.proc.ProcRoller;
import com.camperscrub.skillcraft.proc.PseudoRandomProcRoller;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class YieldRollerTest {

    private static final UUID ALEX = UUID.randomUUID();
    private static final ProcKey DIAMOND = new ProcKey("yield", "minecraft:diamond");

    /**
     * Hits or misses on demand and remembers the chance it was asked to roll. Like the real
     * rollers, a chance of 0 never hits.
     */
    private static final class FakeProcRoller implements ProcRoller {
        private final boolean hit;
        private double lastChance = -1;

        FakeProcRoller(boolean hit) {
            this.hit = hit;
        }

        @Override
        public boolean roll(UUID player, ProcKey key, double chance) {
            lastChance = chance;
            return hit && chance > 0;
        }

        @Override
        public void forget(UUID player) {
        }
    }

    @Test
    void aHitOnTheLeftoverChanceAddsOneCopy() {
        assertEquals(1, new YieldRoller(new FakeProcRoller(true)).extraCopies(ALEX, DIAMOND, 81.2));
    }

    @Test
    void aMissOnTheLeftoverChanceAddsNothing() {
        assertEquals(0, new YieldRoller(new FakeProcRoller(false)).extraCopies(ALEX, DIAMOND, 81.2));
    }

    @Test
    void rollsWithTheLeftoverChance() {
        FakeProcRoller procs = new FakeProcRoller(false);
        new YieldRoller(procs).extraCopies(ALEX, DIAMOND, 250);
        assertEquals(0.5, procs.lastChance, 1e-9);
    }

    @Test
    void guaranteedCopiesNeedNoLuck() {
        assertEquals(2, new YieldRoller(new FakeProcRoller(false)).extraCopies(ALEX, DIAMOND, 200));
        assertEquals(2, new YieldRoller(new FakeProcRoller(true)).extraCopies(ALEX, DIAMOND, 200));
    }

    @Test
    void guaranteedAndLeftoverAddUp() {
        assertEquals(3, new YieldRoller(new FakeProcRoller(true)).extraCopies(ALEX, DIAMOND, 250));
    }

    @Test
    void averagesOutToTheYieldWithTheRealRoller() {
        Random random = new Random(42);
        YieldRoller roller = new YieldRoller(new PseudoRandomProcRoller(random::nextDouble));
        int blocks = 100_000;
        long copies = 0;
        for (int i = 0; i < blocks; i++) {
            copies += roller.extraCopies(ALEX, DIAMOND, 150);
        }
        assertEquals(1.5, (double) copies / blocks, 0.01);
    }
}