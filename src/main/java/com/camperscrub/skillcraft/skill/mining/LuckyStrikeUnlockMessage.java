package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.XpAward;
import com.camperscrub.skillcraft.xp.XpListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Shows "✦ Lucky Strike unlocked!" in the action bar when Mining reaches level 400. */
public final class LuckyStrikeUnlockMessage implements XpListener {

    @Override
    public void onXpAwarded(Player player, XpAward award) {
        if (award.skill() == Skill.MINING && LuckyStrike.unlockedBetween(award.oldLevel(), award.newLevel())) {
            player.sendOverlayMessage(Component.translatable("skillcraft.unlock.lucky_strike")
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}