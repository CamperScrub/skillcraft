package com.camperscrub.skillcraft.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

/** Stores placed positions in a persistent attachment on each chunk. */
public final class ChunkPlacedBlockTracker implements PlacedBlockTracker {

    @Override
    public void markPlaced(Level level, BlockPos pos) {
        ChunkAccess chunk = level.getChunk(pos);
        PlacedBlocks current = chunk.getAttachedOrElse(SkillAttachments.PLACED_BLOCKS, PlacedBlocks.EMPTY);
        PlacedBlocks updated = current.with(pos.asLong());
        if (updated != current) {
            chunk.setAttached(SkillAttachments.PLACED_BLOCKS, updated);
        }
    }

    @Override
    public boolean unmark(Level level, BlockPos pos) {
        ChunkAccess chunk = level.getChunk(pos);
        PlacedBlocks current = chunk.getAttachedOrElse(SkillAttachments.PLACED_BLOCKS, PlacedBlocks.EMPTY);
        if (!current.contains(pos.asLong())) {
            return false;
        }
        PlacedBlocks updated = current.without(pos.asLong());
        // Remove the attachment entirely when empty, so we don't save empty data in every chunk
        chunk.setAttached(SkillAttachments.PLACED_BLOCKS, updated.isEmpty() ? null : updated);
        return true;
    }
}