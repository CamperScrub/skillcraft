package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.feedback.ActionBar;
import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.XpAward;
import com.camperscrub.skillcraft.xp.XpListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Action bar messages for Lucky Strike: "✦ Lucky Strike unlocked!" at level 400, then
 * "✦ Lucky Strike improved: 1.5%" at each step after that.
 */
public final class LuckyStrikeMessages implements XpListener {

    /** "1.5", "2", "4": no ".0", and always a dot whatever the server's language. */
    private static final DecimalFormat PERCENT = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT));

    private final ActionBar actionBar;

    public LuckyStrikeMessages(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    @Override
    public void onXpAwarded(Player player, XpAward award) {
        if (award.skill() != Skill.MINING) {
            return;
        }
        if (LuckyStrike.unlockedBetween(award.oldLevel(), award.newLevel())) {
            actionBar.show(player, Component.translatable("skillcraft.unlock.lucky_strike")
                    .withStyle(ChatFormatting.GOLD));
        } else if (LuckyStrike.improvedBetween(award.oldLevel(), award.newLevel())) {
            String percent = PERCENT.format(LuckyStrike.chanceAtLevel(award.newLevel()) * 100) + "%";
            actionBar.show(player, Component.translatable("skillcraft.improved.lucky_strike", percent)
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}