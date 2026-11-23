package com.camperscrub.skillcraft.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Positions of player-placed blocks in one chunk. Immutable. Each position is a packed BlockPos.
 */
public record PlacedBlocks(Set<Long> positions) {

    public static final PlacedBlocks EMPTY = new PlacedBlocks(Set.of());

    public PlacedBlocks {
        positions = Set.copyOf(positions);
    }

    public boolean contains(long pos) {
        return positions.contains(pos);
    }

    /**
     * A copy that also contains pos. Returns this same object if pos is already present.
     */
    public PlacedBlocks with(long pos) {
        if (contains(pos)) {
            return this;
        }
        Set<Long> next = new HashSet<>(positions);
        next.add(pos);
        return new PlacedBlocks(next);
    }

    /**
     * A copy without pos. Returns this same object if pos wasn't present.
     */
    public PlacedBlocks without(long pos) {
        if (!contains(pos)) {
            return this;
        }
        if (positions.size() == 1) {
            return EMPTY;
        }
        Set<Long> next = new HashSet<>(positions);
        next.remove(pos);
        return new PlacedBlocks(next);
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }
}