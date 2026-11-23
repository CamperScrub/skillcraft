package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.skill.Skill;

/**
 * What happened when XP was awarded.
 */
public record XpAward(PlayerSkills updated, Skill skill, long gained, int oldLevel, int newLevel) {
    public boolean leveledUp() {
        return newLevel > oldLevel;
    }
}