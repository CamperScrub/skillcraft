package com.camperscrub.skillcraft.feedback;

import com.camperscrub.skillcraft.skill.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/** How each skill looks in text. */
public final class SkillStyle {

    private SkillStyle() {
    }

    public static ChatFormatting colour(Skill skill) {
        return switch (skill) {
            case MINING -> ChatFormatting.AQUA;
            case WOODCUTTING -> ChatFormatting.GREEN;
            case EXCAVATION -> ChatFormatting.YELLOW;
            case SWORDSMANSHIP -> ChatFormatting.RED;
        };
    }

    /** The skill's name in bold, in its colour. */
    public static MutableComponent name(Skill skill) {
        return Component.translatable(skill.translationKey()).withStyle(colour(skill), ChatFormatting.BOLD);
    }
}