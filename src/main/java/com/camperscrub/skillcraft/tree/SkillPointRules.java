package com.camperscrub.skillcraft.tree;

import java.util.List;

import java.util.OptionalInt;

/** When skill points are earned (see design doc: Skill points and the tree). Pure logic. */
public final class SkillPointRules {

    public static final int LEVELS_PER_POINT = 10;
    public static final List<Integer> MILESTONES = List.of(250, 500, 750, 1000);

    /** Total points a skill has earned by this level: one per 10 levels, plus one per milestone reached. */
    public int pointsAt(int level) {
        int points = level / LEVELS_PER_POINT;
        for (int milestone : MILESTONES) {
            if (level >= milestone) {
                points++;
            } else {
                break; // milestones are in order, so no later one can be reached either
            }
        }
        return points;
    }

    /** Points gained going from oldLevel to newLevel. */
    public int pointsEarned(int oldLevel, int newLevel) {
        return pointsAt(newLevel) - pointsAt(oldLevel);
    }

    /** The highest milestone in (oldLevel, newLevel], or empty if none was crossed. */
    public OptionalInt milestoneCrossed(int oldLevel, int newLevel) {
        for (int i = MILESTONES.size() - 1; i >= 0; i--) {
            int milestone = MILESTONES.get(i);
            if (milestone > oldLevel && milestone <= newLevel) {
                return OptionalInt.of(milestone);
            }
        }
        return OptionalInt.empty();
    }
}