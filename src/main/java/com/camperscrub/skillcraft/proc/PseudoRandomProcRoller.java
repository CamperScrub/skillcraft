package com.camperscrub.skillcraft.proc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Pseudo-random rolls with one counter per player and {@link ProcKey}. Counters live in memory
 * only and are forgotten on logout, as the design doc says. Server thread only.
 */
public final class PseudoRandomProcRoller implements ProcRoller {

    private final ChanceSource source;
    private final Map<UUID, Map<ProcKey, PrdCounter>> counters = new HashMap<>();

    public PseudoRandomProcRoller(ChanceSource source) {
        this.source = source;
    }

    @Override
    public boolean roll(UUID player, ProcKey key, double chance) {
        PrdCounter counter = counters
                .computeIfAbsent(player, id -> new HashMap<>())
                .computeIfAbsent(key, k -> new PrdCounter());
        return counter.roll(chance, source);
    }

    @Override
    public void forget(UUID player) {
        counters.remove(player);
    }
}
