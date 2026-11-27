package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.skill.Skill;
import net.minecraft.world.entity.player.Player;

/** Looks up a player's current level in a skill. */
@FunctionalInterface
public interface SkillLevels {

    int level(Player player, Skill skill);
}