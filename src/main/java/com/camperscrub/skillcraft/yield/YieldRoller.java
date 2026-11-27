package com.camperscrub.skillcraft.yield;

import com.camperscrub.skillcraft.proc.ProcKey;
import com.camperscrub.skillcraft.proc.ProcRoller;

import java.util.UUID;

/** Turns a Yield value into a number of extra copies, rolling the leftover chance. */
public final class YieldRoller {

    private final ProcRoller procs;

    public YieldRoller(ProcRoller procs) {
        this.procs = procs;
    }

    /**
     * How many extra copies of the drops to add.
     *
     * <p>EXERCISE 2. The guaranteed copies, plus one more if the leftover chance procs.
     * Roll it with {@code procs.roll(player, key, chance)}. Both rollers already return false
     * for a chance of 0, so you don't need a special case for exact hundreds.
     */
    public int extraCopies(UUID player, ProcKey key, double yield) {
        int copies = Yield.guaranteedCopies(yield);
        if (procs.roll(player, key, Yield.leftoverChance(yield))) {
            copies++;
        }
        return copies;
    }
}