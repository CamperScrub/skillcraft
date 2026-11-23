package com.camperscrub.skillcraft.skill;

import com.camperscrub.skillcraft.SkillCraft;

import java.util.Locale;
import java.util.Optional;

public enum Skill {
    MINING(SkillCategory.GATHERING),
    WOODCUTTING(SkillCategory.GATHERING),
    EXCAVATION(SkillCategory.GATHERING),
    SWORDSMANSHIP(SkillCategory.COMBAT);

    private final SkillCategory skillCategory;

    Skill(SkillCategory skillCategory) {
        this.skillCategory = skillCategory;
    }

    /**
     * The skill with this id, or empty if no skill has it (for example a rename).
     *
     * @param id skill
     * @return Optional
     */
    public static Optional<Skill> byId(String id) {
        for (Skill skill : values()) {
            if (skill.id().equals(id)) {
                return Optional.of(skill);
            }
        }
        return Optional.empty();
    }

    public SkillCategory skillCategory() {
        return skillCategory;
    }

    /**
     * Stable ID used in save data and config. Never rename one after release.
     */
    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * For example "skill.skillcraft.mining". The client turns this into display text.
     */
    public String translationKey() {
        return "skill." + SkillCraft.MOD_ID + "." + id();
    }
}
