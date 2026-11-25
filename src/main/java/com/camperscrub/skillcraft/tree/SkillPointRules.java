package com.camperscrub.skillcraft.tree;

import java.util.List;

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

    /** True if a milestone lies in (oldLevel, newLevel]: passed on the way up, not already reached before. */
    public boolean crossedMilestone(int oldLevel, int newLevel) {
        for (int milestone : MILESTONES) {
            if (oldLevel < milestone && newLevel >= milestone) {
                return true;   // found one, no need to look further
            }
        }
        return false;          // checked them all, none crossed
    }
}