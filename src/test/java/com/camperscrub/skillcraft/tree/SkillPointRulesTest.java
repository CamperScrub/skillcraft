package com.camperscrub.skillcraft.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SkillPointRulesTest {

    private final SkillPointRules rules = new SkillPointRules();

    @Test
    void pointsAtMatchTheDesignDoc() {
        assertEquals(0, rules.pointsAt(0));
        assertEquals(0, rules.pointsAt(9));
        assertEquals(1, rules.pointsAt(10));
        assertEquals(26, rules.pointsAt(250));   // 25 regular + 1 milestone
        assertEquals(104, rules.pointsAt(1000)); // 100 regular + 4 milestones
    }

    @Test
    void pointsEarnedAcrossLevels() {
        assertEquals(0, rules.pointsEarned(11, 12));
        assertEquals(1, rules.pointsEarned(9, 10));
        assertEquals(2, rules.pointsEarned(249, 250)); // regular point + milestone
        assertEquals(104, rules.pointsEarned(0, 1000));
    }

    @Test
    void milestoneCrossing() {
        assertTrue(rules.crossedMilestone(249, 250));
        assertTrue(rules.crossedMilestone(0, 1000));
        assertFalse(rules.crossedMilestone(250, 260)); // already reached before
        assertFalse(rules.crossedMilestone(10, 20));
        assertTrue(rules.crossedMilestone(499, 500));   // a milestone other than the first
    }
}