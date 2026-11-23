package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.skill.Skill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XpRulesTest {

    private static final long MAX_XP = 14_958_708;
    private final XpRules rules = new XpRules(new LinearTailXpCurve(LinearTailSettings.DEFAULTS));

    @Test
    void addsXpWithoutLevelling() {
        XpAward award = rules.award(PlayerSkills.EMPTY, Skill.MINING, 100);
        assertEquals(100, award.updated().xp(Skill.MINING));
        assertEquals(100, award.gained());
        assertEquals(0, award.oldLevel());
        assertEquals(0, award.newLevel());
        assertFalse(award.leveledUp());
    }

    @Test
    void reachingAThresholdLevelsUp() {
        XpAward award = rules.award(PlayerSkills.EMPTY, Skill.MINING, 2_055);
        assertEquals(1, award.newLevel());
        assertTrue(award.leveledUp());
    }

    @Test
    void xpIsCappedAtMaxLevel() {
        XpAward award = rules.award(PlayerSkills.EMPTY, Skill.MINING, 20_000_000);
        assertEquals(MAX_XP, award.updated().xp(Skill.MINING));
        assertEquals(MAX_XP, award.gained());
        assertEquals(999, award.newLevel());
    }

    @Test
    void atTheCapNothingIsGained() {
        PlayerSkills maxed = PlayerSkills.EMPTY.withXp(Skill.MINING, MAX_XP);
        XpAward award = rules.award(maxed, Skill.MINING, 50);
        assertEquals(0, award.gained());
        assertEquals(MAX_XP, award.updated().xp(Skill.MINING));
        assertFalse(award.leveledUp());
    }

    @Test
    void otherSkillsAreUntouched() {
        PlayerSkills start = PlayerSkills.EMPTY.withXp(Skill.WOODCUTTING, 777);
        XpAward award = rules.award(start, Skill.MINING, 100);
        assertEquals(777, award.updated().xp(Skill.WOODCUTTING));
    }

    @Test
    void negativeAmountsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> rules.award(PlayerSkills.EMPTY, Skill.MINING, -5));
    }
}