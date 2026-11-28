package com.camperscrub.skillcraft.skill.mining;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OreSenseScanTest {

    private static final BlockPos CENTRE = new BlockPos(0, 64, 0);
    private static final BlockPos NEAR = new BlockPos(1, 64, 0);
    private static final BlockPos MIDDLE = new BlockPos(0, 60, 0);
    private static final BlockPos FAR = new BlockPos(10, 64, 10);

    @Test
    void sortsClosestFirst() {
        assertEquals(List.of(NEAR, MIDDLE, FAR), OreSenseScan.nearest(CENTRE, List.of(FAR, NEAR, MIDDLE), 10));
    }

    @Test
    void keepsOnlyTheClosestWhenThereAreTooMany() {
        assertEquals(List.of(NEAR, MIDDLE), OreSenseScan.nearest(CENTRE, List.of(FAR, NEAR, MIDDLE), 2));
    }

    @Test
    void nothingFoundGivesNothing() {
        assertTrue(OreSenseScan.nearest(CENTRE, List.of(), 200).isEmpty());
    }

    @Test
    void leavesTheCallersListAlone() {
        List<BlockPos> found = new ArrayList<>(List.of(FAR, NEAR, MIDDLE));
        OreSenseScan.nearest(CENTRE, found, 2);
        assertEquals(List.of(FAR, NEAR, MIDDLE), found);
    }
}
