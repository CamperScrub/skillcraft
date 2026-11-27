package com.camperscrub.skillcraft.event;

import com.camperscrub.skillcraft.data.PlacedBlockTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Moves "placed by a player" marks along with blocks that pistons push or pull, so a placed
 * block can't be turned natural by moving it one block over.
 */
public final class PlacedBlockPistonHandler {

    private final PlacedBlockTracker placedBlocks;

    public PlacedBlockPistonHandler(PlacedBlockTracker placedBlocks) {
        this.placedBlocks = placedBlocks;
    }

    public void register() {
        BlocksPushedCallback.EVENT.register(this::onBlocksPushed);
    }

    /**
     * EXERCISE. Update the tracker for one piston move:
     * <ol>
     *   <li>Work out which {@code from} positions are placed, before changing anything.</li>
     *   <li>Unmark every {@code from} and every {@code destroyed} position.</li>
     *   <li>Mark {@code to.get(i)} for every {@code from.get(i)} that was placed.</li>
     * </ol>
     *
     * <p>Why step 1 comes first: in a row of blocks, one block moves into the spot another block
     * just left. If you unmark and mark as you go, one block's new mark can be wiped by the next
     * block's unmark, or a natural block can inherit its neighbour's mark. The tests check both.
     */
    void onBlocksPushed(Level level, List<BlockPos> from, List<BlockPos> to, List<BlockPos> destroyed) {
        List<Boolean> wasPlaced = new ArrayList<>();
        for (BlockPos pos : from) {
            wasPlaced.add(placedBlocks.isPlaced(level, pos));
        }
        for (BlockPos pos : from) {
            placedBlocks.unmark(level, pos);
        }
        for (BlockPos pos : destroyed) {
            placedBlocks.unmark(level, pos);
        }
        for (int i = 0; i < from.size(); i++) {
            if (wasPlaced.get(i)) {
                placedBlocks.markPlaced(level, to.get(i));
            }
        }
    }
}
