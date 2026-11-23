package com.camperscrub.skillcraft.data;

import com.camperscrub.skillcraft.skill.Skill;

import java.util.HashMap;
import java.util.Map;

/**
 * One player's skill progress. Immutable: "changing" it returns a new object.
 */
public record PlayerSkills(Map<Skill, Long> totalXp) {

    public static final PlayerSkills EMPTY = new PlayerSkills(Map.of());

    public PlayerSkills {
        totalXp = Map.copyOf(totalXp); // our own unmodifiable copy; the caller's map can't change us later
        for (long xp : totalXp.values()) {
            if (xp < 0) throw new IllegalArgumentException("XP cannot be negative: " + xp);
        }
    }

    /**
     * Total XP in a skill. 0 if the player has never earned any.
     */
    public long xp(Skill skill) {
        return totalXp.getOrDefault(skill, 0L);
    }

    /**
     * A copy of this with one skill's total XP replaced. Never changes this object.
     */
    public PlayerSkills withXp(Skill skill, long newTotalXp) {
        Map<Skill, Long> updatedXp = new HashMap<>(totalXp);
        updatedXp.put(skill, newTotalXp);
        return new PlayerSkills(updatedXp);
    }
}