package com.camperscrub.skillcraft.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Remembers which blocks were placed by players.
 */
public interface PlacedBlockTracker {

    void markPlaced(Level level, BlockPos pos);

    /** True if a player placed the block at this position. Changes nothing. */
    boolean isPlaced(Level level, BlockPos pos);

    /**
     * Forgets a position.
     *
     * @return true if the block there had been placed by a player
     */
    boolean unmark(Level level, BlockPos pos);
}