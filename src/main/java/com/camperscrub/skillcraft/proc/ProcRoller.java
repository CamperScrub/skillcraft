package com.camperscrub.skillcraft.proc;

import java.util.UUID;

/** Rolls procs for players. Passives depend on this interface, not on a particular roller. */
public interface ProcRoller {

    boolean roll(UUID player, ProcKey key, double chance);

    /** Forget a player's counters, for example when they log out. */
    void forget(UUID player);
}
