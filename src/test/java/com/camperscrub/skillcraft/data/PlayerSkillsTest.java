package com.camperscrub.skillcraft.data;

import com.camperscrub.skillcraft.skill.Skill;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlayerSkillsTest {

    @Test
    void emptyHasZeroXpEverywhere() {
        for (Skill skill : Skill.values()) {
            assertEquals(0, PlayerSkills.EMPTY.xp(skill));
        }
    }

    @Test
    void withXpReturnsANewObjectAndLeavesTheOldOneAlone() {
        PlayerSkills before = PlayerSkills.EMPTY;
        PlayerSkills after = before.withXp(Skill.MINING, 500);

        assertEquals(500, after.xp(Skill.MINING));
        assertEquals(0, before.xp(Skill.MINING));      // unchanged: immutability
        assertEquals(0, after.xp(Skill.WOODCUTTING));  // other skills untouched
    }

    @Test
    void callerCannotChangeItFromOutside() {
        Map<Skill, Long> map = new HashMap<>();
        map.put(Skill.MINING, 10L);
        PlayerSkills skills = new PlayerSkills(map);

        map.put(Skill.MINING, 9999L);                   // sneaky edit after construction
        assertEquals(10, skills.xp(Skill.MINING));
    }

    @Test
    void negativeXpIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> PlayerSkills.EMPTY.withXp(Skill.MINING, -1));
    }

    @Test
    void skillByIdRoundTrips() {
        for (Skill skill : Skill.values()) {
            assertEquals(Optional.of(skill), Skill.byId(skill.id()));
        }
        assertEquals(Optional.empty(), Skill.byId("fishing"));
    }
}