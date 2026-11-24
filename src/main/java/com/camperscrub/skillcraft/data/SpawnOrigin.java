package com.camperscrub.skillcraft.data;

import java.util.Locale;
import java.util.Optional;

/**
 * Where a mob came from, as far as XP is concerned. Mobs without a stored origin are NATURAL.
 */
public enum SpawnOrigin {
    NATURAL,
    SPAWNER,
    NETHER_PORTAL,
    SPAWN_EGG;

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Optional<SpawnOrigin> byId(String id) {
        for (SpawnOrigin origin : values()) {
            if (origin.id().equals(id)) {
                return Optional.of(origin);
            }
        }
        return Optional.empty();
    }
}