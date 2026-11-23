package com.camperscrub.skillcraft.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlacedBlocksTest {

    @Test
    void addAndFind() {
        PlacedBlocks placed = PlacedBlocks.EMPTY.with(42L);
        assertTrue(placed.contains(42L));
        assertFalse(placed.contains(7L));
        assertFalse(PlacedBlocks.EMPTY.contains(42L)); // EMPTY never changes
    }

    @Test
    void remove() {
        PlacedBlocks placed = PlacedBlocks.EMPTY.with(1L).with(2L).without(1L);
        assertFalse(placed.contains(1L));
        assertTrue(placed.contains(2L));
    }

    @Test
    void noChangeReturnsTheSameObject() {
        PlacedBlocks placed = PlacedBlocks.EMPTY.with(5L);
        assertSame(placed, placed.with(5L));     // already there
        assertSame(placed, placed.without(99L)); // wasn't there
    }

    @Test
    void emptiesOut() {
        assertTrue(PlacedBlocks.EMPTY.with(3L).without(3L).isEmpty());
    }
}