package com.camperscrub.skillcraft.ability;

import java.util.HashMap;
import java.util.Map;

/**
 * The client's copy of its player's ability phases, kept up to date by the server. Pure logic:
 * no Minecraft classes.
 *
 * <p>The server decides everything. The one thing the client does on its own is
 * {@link #predictActivation}: when the player starts mining with a readied ability, the client
 * treats it as active straight away instead of waiting for the server's message, so the first
 * block already breaks fast. The server activates it at the same moment, and its next message
 * overwrites the guess either way.
 */
public final class AbilityMirror {

    private record Entry(AbilityPhase phase, long endsAt, long activeTicks) {
    }

    private final Map<String, Entry> entries = new HashMap<>();

    /** A status message from the server, received at tick {@code now}. */
    public void update(String abilityId, AbilityPhase phase, long now, long ticksLeft, long activeTicks) {
        entries.put(abilityId, new Entry(phase, now + ticksLeft, activeTicks));
    }

    /**
     * True if the ability is active at tick {@code now}.
     *
     * <p>EXERCISE 1. Look up the entry (no entry means false). Active means its phase is ACTIVE
     * and {@code now} is before {@code endsAt}.
     */
    public boolean isActive(String abilityId, long now) {
        Entry entry = entries.get(abilityId);
        if (entry == null) {
            return false;
        }
        return entry.phase() == AbilityPhase.ACTIVE && now < entry.endsAt();
    }

    /**
     * The player started mining: if the ability is readied, switch it to ACTIVE until
     * {@code now + activeTicks} and return true. Otherwise change nothing and return false.
     *
     * <p>EXERCISE 2. Readied means phase READIED and {@code now} before {@code endsAt}. Replace the
     * entry with a new one (records can't be changed), keeping {@code activeTicks}.
     */
    public boolean predictActivation(String abilityId, long now) {
        Entry entry = entries.get(abilityId);
        if (entry == null || entry.phase() != AbilityPhase.READIED || now >= entry.endsAt()) {
            return false;
        }
        entries.put(abilityId, new Entry(AbilityPhase.ACTIVE, now + entry.activeTicks(), entry.activeTicks()));
        return true;
    }

    /** Forgets everything, for when the player leaves the world. */
    public void clear() {
        entries.clear();
    }
}
