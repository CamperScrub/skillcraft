package com.camperscrub.skillcraft.skill.mining;

import net.minecraft.core.BlockPos;

import java.util.Comparator;
import java.util.List;

/** Picks which ores Ore Sense shows when there are too many to draw them all. */
public final class OreSenseScan {

    private OreSenseScan() {
    }

    /**
     * The {@code max} positions in {@code found} closest to {@code centre}, closest first.
     * Returns all of them (still sorted) if there are {@code max} or fewer.
     *
     * <p>EXERCISE. Sort a copy of {@code found} by {@code pos.distSqr(centre)} and keep the first
     * {@code max}. Hint: {@code found.stream().sorted(Comparator.comparingDouble(...)).limit(max)}.
     * Don't sort {@code found} itself: the caller owns that list.
     */
    public static List<BlockPos> nearest(BlockPos centre, List<BlockPos> found, int max) {
        return found.stream()
                .sorted(Comparator.comparingDouble(pos -> pos.distSqr(centre)))
                .limit(max)
                .toList();
    }
}