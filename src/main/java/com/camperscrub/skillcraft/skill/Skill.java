package com.camperscrub.skillcraft.skill;

import com.camperscrub.skillcraft.SkillCraft;

import java.util.Locale;

public enum Skill {
    MINING(SkillCategory.GATHERING),
    WOODCUTTING(SkillCategory.GATHERING),
    EXCAVATION(SkillCategory.GATHERING),
    SWORDSMANSHIP(SkillCategory.COMBAT);

    private final SkillCategory skillCategory;

    Skill(SkillCategory skillCategory) {
        this.skillCategory = skillCategory;
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
