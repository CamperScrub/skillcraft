package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.data.SkillAttachments;
import com.camperscrub.skillcraft.skill.Skill;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Applies XpRules to real players and saves the result.
 */
public final class XpService {

    private final XpRules rules;
    private final List<XpListener> listeners;

    public XpService(XpRules rules, List<XpListener> listeners) {
        this.rules = rules;
        this.listeners = List.copyOf(listeners);
    }

    public XpAward award(Player player, Skill skill, long amount) {
        XpAward award = rules.award(skillsOf(player), skill, amount);
        if (award.gained() > 0) {
            player.setAttached(SkillAttachments.PLAYER_SKILLS, award.updated());
            for (XpListener listener : listeners) {
                listener.onXpAwarded(player, award);
            }
        }
        return award;
    }

    public PlayerSkills skillsOf(Player player) {
        return player.getAttachedOrCreate(SkillAttachments.PLAYER_SKILLS);
    }

    /** Sets a skill's total XP directly (clamped to 0..max). For admin commands; no level-up effects. */
    public void setTotalXp(Player player, Skill skill, long totalXp) {
        long clamped = Math.clamp(totalXp, 0, rules.maxTotalXp());
        player.setAttached(SkillAttachments.PLAYER_SKILLS, skillsOf(player).withXp(skill, clamped));
    }
}