package com.camperscrub.skillcraft.feedback;

import com.camperscrub.skillcraft.tree.SkillPointRules;
import com.camperscrub.skillcraft.xp.XpAward;
import com.camperscrub.skillcraft.xp.XpListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.OptionalInt;

/** Sounds and messages when a skill levels up (see design doc: Level-up feedback). */
public final class LevelUpFeedback implements XpListener {

    private final SkillPointRules pointRules;
    private final int maxLevel;
    private final boolean announceMilestones;
    private final ActionBar actionBar;
    private final MilestoneFireworks fireworks;

    public LevelUpFeedback(SkillPointRules pointRules, MilestoneFireworks fireworks, ActionBar actionBar,
                           int maxLevel, boolean announceMilestones) {
        this.pointRules = pointRules;
        this.fireworks = fireworks;
        this.actionBar = actionBar;
        this.maxLevel = maxLevel;
        this.announceMilestones = announceMilestones;
    }

    @Override
    public void onXpAwarded(Player player, XpAward award) {
        if (!award.leveledUp()) {
            return;
        }
        OptionalInt milestone = pointRules.milestoneCrossed(award.oldLevel(), award.newLevel());
        if (milestone.isPresent()) {
            celebrateMilestone(player, award, milestone.getAsInt());
            return;
        }

        playSound(player, SoundEvents.PLAYER_LEVELUP);
        int points = pointRules.pointsEarned(award.oldLevel(), award.newLevel());
        if (points > 0) {
            actionBar.show(player, levelLine(award)
                    .append(Component.literal("   "))
                    .append(pointsLine(points)));
        }
    }

    private void celebrateMilestone(Player player, XpAward award, int milestone) {
        playSound(player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
        actionBar.show(player, Component.translatable("skillcraft.milestone.self",
                        SkillStyle.name(award.skill()), milestone)
                .withStyle(ChatFormatting.GOLD));
        fireworks.launch(player, award.skill(), milestone);

        MinecraftServer server = player.level().getServer();
        if (!announceMilestones || server == null) {
            return;
        }
        Component name = player.getDisplayName().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
        Component level = Component.literal(String.valueOf(milestone)).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        Component skill = SkillStyle.name(award.skill());

        if (milestone == maxLevel) {
            Component banner = Component.translatable("skillcraft.mastery.banner")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            server.getPlayerList().broadcastSystemMessage(banner, false);
            server.getPlayerList().broadcastSystemMessage(Component.translatable("skillcraft.mastery.announce",
                    name, skill, level).withStyle(ChatFormatting.YELLOW), false);
            server.getPlayerList().broadcastSystemMessage(banner, false);
        } else {
            server.getPlayerList().broadcastSystemMessage(Component.translatable("skillcraft.milestone.announce",
                    name, level, skill).withStyle(ChatFormatting.GOLD), false);
        }
    }

    /** "Mining ▸ Level 40" */
    private static MutableComponent levelLine(XpAward award) {
        return Component.translatable("skillcraft.levelup.level",
                        SkillStyle.name(award.skill()),
                        Component.literal(String.valueOf(award.newLevel())).withStyle(ChatFormatting.WHITE))
                .withStyle(ChatFormatting.GRAY);
    }

    /** "+1 Skill Point" / "+3 Skill Points" */
    private static MutableComponent pointsLine(int points) {
        String key = points == 1 ? "skillcraft.levelup.point" : "skillcraft.levelup.points";
        return Component.translatable(key, points).withStyle(ChatFormatting.GREEN);
    }

    private static void playSound(Player player, SoundEvent sound) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                sound, SoundSource.PLAYERS, 0.75f, 1.0f);
    }
}