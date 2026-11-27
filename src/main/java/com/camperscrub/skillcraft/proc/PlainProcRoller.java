package com.camperscrub.skillcraft.proc;

import java.util.UUID;

/** Plain random: every roll is independent. Used for frequent, low-value procs. */
public final class PlainProcRoller implements ProcRoller {

    private final ChanceSource source;

    public PlainProcRoller(ChanceSource source) {
        this.source = source;
    }

    @Override
    public boolean roll(UUID player, ProcKey key, double chance) {
        return chance > 0 && source.nextDouble() < chance;
    }

    @Override
    public void forget(UUID player) {
        // nothing to remember
    }
}
