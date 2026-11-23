package com.camperscrub.skillcraft.xp;

import java.util.Arrays;

public final class LinearTailXpCurve implements XpCurve {

    private final int maxLevel;
    private final long[] totalXp; // totalXp[L] = total XP needed to reach level L

    public LinearTailXpCurve(LinearTailSettings settings) {
        this.maxLevel = settings.maxLevel();
        this.totalXp = buildTable(settings);
    }

    private static long[] buildTable(LinearTailSettings s) {
        long[] table = new long[s.maxLevel() + 1];
        for (int level = 0; level < s.maxLevel(); level++) {
            long costOfThisLevel = (long) Math.floor(
                    s.base() + s.slope() * level + s.tailSize() * Math.pow(2, level / s.tailDoubling())
            );
            table[level + 1] = table[level] + costOfThisLevel;
        }
        return table;
    }

    @Override
    public int maxLevel() {
        return maxLevel;
    }

    @Override
    public long totalXpForLevel(int level) {
        return totalXp[Math.clamp(level, 0, maxLevel)];
    }

    @Override
    public int levelForXp(long currentXp) {
        if (currentXp <= 0) {
            return 0;
        }
        if (currentXp >= totalXp[maxLevel]) {
            return maxLevel;
        }
        int index = Arrays.binarySearch(totalXp, currentXp);
        if (index >= 0) {
            return index;
        }
        int insertionPoint = -index - 1;   // where currentXp would be inserted to keep the array sorted
        return insertionPoint - 1;         // the level just below that is the one we've reached
    }

    @Override
    public float progressToNextLevel(long currentXp) {
        long xp = Math.max(currentXp, 0);
        int currentLevel = levelForXp(xp);
        if (currentLevel == maxLevel) {
            return 1.0f;
        }
        long levelStart = totalXp[currentLevel];
        long levelEnd = totalXp[currentLevel + 1];
        return (float) (xp - levelStart) / (levelEnd - levelStart);
    }
}
