package com.camperscrub.skillcraft.event;

import com.camperscrub.skillcraft.data.PlacedBlockTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlacedBlockPistonHandlerTest {

    /** Remembers placed positions in a set. Ignores the level, so tests can pass null. */
    private static final class FakeTracker implements PlacedBlockTracker {
        final Set<BlockPos> placed = new HashSet<>();

        @Override
        public void markPlaced(Level level, BlockPos pos) {
            placed.add(pos.immutable());
        }

        @Override
        public boolean isPlaced(Level level, BlockPos pos) {
            return placed.contains(pos);
        }

        @Override
        public boolean unmark(Level level, BlockPos pos) {
            return placed.remove(pos);
        }
    }

    // A row along x, pushed east (+x): A → B → C → D
    private static final BlockPos A = new BlockPos(0, 64, 0);
    private static final BlockPos B = new BlockPos(1, 64, 0);
    private static final BlockPos C = new BlockPos(2, 64, 0);
    private static final BlockPos D = new BlockPos(3, 64, 0);

    private final FakeTracker tracker = new FakeTracker();
    private final PlacedBlockPistonHandler handler = new PlacedBlockPistonHandler(tracker);

    @Test
    void aPlacedBlockKeepsItsMarkWhenPushed() {
        tracker.markPlaced(null, A);
        handler.onBlocksPushed(null, List.of(A), List.of(B), List.of());
        assertEquals(Set.of(B), tracker.placed);
    }

    @Test
    void aNaturalBlockStaysNatural() {
        handler.onBlocksPushed(null, List.of(A), List.of(B), List.of());
        assertTrue(tracker.placed.isEmpty());
    }

    @Test
    void aRowOfPlacedBlocksAllKeepTheirMarks() {
        tracker.markPlaced(null, A);
        tracker.markPlaced(null, B);
        tracker.markPlaced(null, C);
        // Pistons list blocks in any order; try the order that breaks "unmark and mark as you go"
        handler.onBlocksPushed(null, List.of(A, B, C), List.of(B, C, D), List.of());
        assertEquals(Set.of(B, C, D), tracker.placed);
    }

    @Test
    void aNaturalBlockPushedIntoAPlacedBlocksOldSpotIsStillNatural() {
        tracker.markPlaced(null, B); // B is placed, A is natural
        handler.onBlocksPushed(null, List.of(B, A), List.of(C, B), List.of());
        assertEquals(Set.of(C), tracker.placed);
    }

    @Test
    void blocksThePistonBreaksAreForgotten() {
        tracker.markPlaced(null, D);
        handler.onBlocksPushed(null, List.of(), List.of(), List.of(D));
        assertTrue(tracker.placed.isEmpty());
    }
}
