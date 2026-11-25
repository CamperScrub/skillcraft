package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.skill.Skill;

import java.util.Optional;

/** Watches a player's skills tick by tick and remembers the most recent XP gain. Used by the client HUD. */
public final class XpChangeTracker {

    public static final int NEVER = Integer.MAX_VALUE;
    /** Gains in the same skill within this many ticks of the last one are added together. */
    public static final int COMBINE_WINDOW = 60;

    private PlayerSkills previous;
    private Skill activeSkill;
    private long recentGain;
    private int ticksSinceGain = NEVER;

    /** Call once per client tick with the player's current skills. */
    public void tick(PlayerSkills current) {
        if (ticksSinceGain != NEVER) {
            ticksSinceGain++;
        }

        if (previous == null) {
            previous = current;
            return;
        }

        for (Skill skill : Skill.values()) {
            long difference = current.xp(skill) - previous.xp(skill);
            if (difference > 0) {
                boolean differentSkill = skill != activeSkill;
                boolean tooLongAgo = ticksSinceGain > COMBINE_WINDOW;
                if (differentSkill || tooLongAgo) {
                    recentGain = 0;                      // start a fresh total
                }
                activeSkill = skill;
                recentGain += difference;
                ticksSinceGain = 0;
            }
        }

        previous = current;
    }

    /** Forget everything, for example when leaving a world. */
    public void reset() {
        previous = null;
        activeSkill = null;
        recentGain = 0;
        ticksSinceGain = NEVER;
    }

    public Optional<Skill> activeSkill() {
        return Optional.ofNullable(activeSkill);
    }

    public long recentGain() {
        return recentGain;
    }

    public int ticksSinceGain() {
        return ticksSinceGain;
    }
}