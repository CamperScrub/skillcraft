package com.camperscrub.skillcraft.feedback;

import com.camperscrub.skillcraft.tree.SkillPointRules;
import com.camperscrub.skillcraft.xp.XpAward;
import com.camperscrub.skillcraft.xp.XpListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/** Sounds and messages when a skill levels up (see design doc: Level-up feedback). */
public final class LevelUpFeedback implements XpListener {

    private final SkillPointRules pointRules;
    private final int maxLevel;
    private final boolean announceMaxLevel;

    public LevelUpFeedback(SkillPointRules pointRules, int maxLevel, boolean announceMaxLevel) {
        this.pointRules = pointRules;
        this.maxLevel = maxLevel;
        this.announceMaxLevel = announceMaxLevel;
    }

    @Override
    public void onXpAwarded(Player player, XpAward award) {
        if (!award.leveledUp()) {
            return;
        }
        if (award.newLevel() == maxLevel) {
            celebrateMaxLevel(player, award);
            return;
        }

        int points = pointRules.pointsEarned(award.oldLevel(), award.newLevel());
        boolean milestone = pointRules.crossedMilestone(award.oldLevel(), award.newLevel());

        if (milestone) {
            playSound(player, SoundEvents.PLAYER_LEVELUP, 0.6f); // lower, deeper ding
        } else {
            playSound(player, SoundEvents.PLAYER_LEVELUP, 1.0f);
        }

        if (points > 0) {
            String key = milestone ? "skillcraft.levelup.milestone"
                    : points == 1 ? "skillcraft.levelup.point" : "skillcraft.levelup.points";
            player.sendOverlayMessage(Component.translatable(key,
                    skillName(award), award.newLevel(), points));
        }
    }

    private void celebrateMaxLevel(Player player, XpAward award) {
        playSound(player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f);
        player.sendOverlayMessage(Component.translatable("skillcraft.levelup.max.self", skillName(award))
                .withStyle(ChatFormatting.GOLD));

        if (announceMaxLevel && player.level().getServer() != null) {
            Component announcement = Component.translatable("skillcraft.levelup.max.announce",
                            player.getDisplayName(), maxLevel, skillName(award))
                    .withStyle(ChatFormatting.GOLD);
            player.level().getServer().getPlayerList().broadcastSystemMessage(announcement, false);
        }
        // Fireworks come in the next step.
    }

    private static Component skillName(XpAward award) {
        return Component.translatable(award.skill().translationKey());
    }

    private static void playSound(Player player, SoundEvent sound, float pitch) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                sound, SoundSource.PLAYERS, 0.75f, pitch);
    }
}