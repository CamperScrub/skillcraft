package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.skill.Skill;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class XpChangeTrackerTest {

    private final XpChangeTracker tracker = new XpChangeTracker();

    private static PlayerSkills mining(long xp) {
        return PlayerSkills.EMPTY.withXp(Skill.MINING, xp);
    }

    private void idle(PlayerSkills skills, int ticks) {
        for (int i = 0; i < ticks; i++) {
            tracker.tick(skills);
        }
    }

    @Test
    void firstTickShowsNothing() {
        tracker.tick(mining(5_000));
        assertEquals(Optional.empty(), tracker.activeSkill());
        assertEquals(XpChangeTracker.NEVER, tracker.ticksSinceGain());
    }

    @Test
    void aGainShowsItsSkill() {
        tracker.tick(PlayerSkills.EMPTY);
        tracker.tick(mining(100));
        assertEquals(Optional.of(Skill.MINING), tracker.activeSkill());
        assertEquals(100, tracker.recentGain());
        assertEquals(0, tracker.ticksSinceGain());
    }

    @Test
    void quickGainsAddUp() {
        tracker.tick(PlayerSkills.EMPTY);
        tracker.tick(mining(100));
        idle(mining(100), 10);
        tracker.tick(mining(250));
        assertEquals(250, tracker.recentGain());
    }

    @Test
    void countsTicksSinceTheLastGain() {
        tracker.tick(PlayerSkills.EMPTY);
        tracker.tick(mining(100));
        idle(mining(100), 10);
        assertEquals(10, tracker.ticksSinceGain());
    }

    @Test
    void aLateGainStartsAFreshTotal() {
        tracker.tick(PlayerSkills.EMPTY);
        tracker.tick(mining(100));
        idle(mining(100), XpChangeTracker.COMBINE_WINDOW + 1);
        tracker.tick(mining(150));
        assertEquals(50, tracker.recentGain());
    }

    @Test
    void switchingSkillsStartsAFreshTotal() {
        tracker.tick(PlayerSkills.EMPTY);
        tracker.tick(mining(100));
        tracker.tick(mining(100).withXp(Skill.WOODCUTTING, 70));
        assertEquals(Optional.of(Skill.WOODCUTTING), tracker.activeSkill());
        assertEquals(70, tracker.recentGain());
    }

    @Test
    void losingXpIsIgnored() {
        tracker.tick(mining(500));
        tracker.tick(mining(100));
        assertEquals(Optional.empty(), tracker.activeSkill());
    }

    @Test
    void resetForgetsEverything() {
        tracker.tick(PlayerSkills.EMPTY);
        tracker.tick(mining(100));
        tracker.reset();
        assertEquals(Optional.empty(), tracker.activeSkill());
        assertEquals(XpChangeTracker.NEVER, tracker.ticksSinceGain());
    }
}