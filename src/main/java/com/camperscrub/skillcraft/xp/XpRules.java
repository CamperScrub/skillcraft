package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.skill.Skill;

/**
 * The rules for gaining XP. Pure logic: no Minecraft classes.
 */
public final class XpRules {

    private final XpCurve curve;

    public XpRules(XpCurve curve) {
        this.curve = curve;
    }

    /**
     * Adds XP to one skill. Total XP never goes past what max level needs;
     * anything beyond that is discarded and not counted in gained().
     *
     * @throws IllegalArgumentException if amount is negative
     */
    public XpAward award(PlayerSkills current, Skill skill, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("XP cannot be negative, got " + amount);
        }
        long currentXp = current.xp(skill);
        long maxXp = curve.totalXpForLevel(curve.maxLevel());

        // Cap the amount before adding, so the sum can't overflow
        long newTotalXp = Math.min(currentXp + Math.min(amount, maxXp), maxXp);
        long actualGained = newTotalXp - currentXp;

        int oldLevel = curve.levelForXp(currentXp);
        int newLevel = curve.levelForXp(newTotalXp);
        PlayerSkills updatedSkills = current.withXp(skill, newTotalXp);
        return new XpAward(updatedSkills, skill, actualGained, oldLevel, newLevel);
    }
}