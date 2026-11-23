package com.camperscrub.skillcraft.event;

import com.camperscrub.skillcraft.SkillCraft;
import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.data.SkillAttachments;
import com.camperscrub.skillcraft.skill.Skill;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

/** TEMPORARY: replaced by real XP sources in the next step. */
public final class DebugXpHook {

    private DebugXpHook() {}

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            PlayerSkills current = player.getAttachedOrCreate(SkillAttachments.PLAYER_SKILLS);
            PlayerSkills updated = current.withXp(Skill.MINING, current.xp(Skill.MINING) + 100);
            player.setAttached(SkillAttachments.PLAYER_SKILLS, updated);

            SkillCraft.LOGGER.info("{} now has {} Mining XP",
                    player.getName().getString(), updated.xp(Skill.MINING));
        });
    }
}