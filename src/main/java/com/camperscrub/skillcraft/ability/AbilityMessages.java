package com.camperscrub.skillcraft.ability;

import com.camperscrub.skillcraft.feedback.ActionBar;
import com.camperscrub.skillcraft.xp.XpAward;
import com.camperscrub.skillcraft.xp.XpListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Action bar messages for super abilities: "✦ Super Breaker unlocked!" at level 50, then
 * "✦ Super Breaker improved: +10 Yield" every 50 levels after that.
 */
public final class AbilityMessages implements XpListener {

    private final List<SuperAbility> abilities;
    private final ActionBar actionBar;

    public AbilityMessages(List<SuperAbility> abilities, ActionBar actionBar) {
        this.abilities = List.copyOf(abilities);
        this.actionBar = actionBar;
    }

    @Override
    public void onXpAwarded(Player player, XpAward award) {
        for (SuperAbility ability : abilities) {
            if (ability.skill() != award.skill()) {
                continue;
            }
            if (AbilityTimings.unlockedBetween(award.oldLevel(), award.newLevel())) {
                actionBar.show(player, Component.translatable("skillcraft.unlock.ability", ability.name())
                        .withStyle(ChatFormatting.GOLD));
            } else if (AbilityTimings.improvedBetween(award.oldLevel(), award.newLevel())) {
                actionBar.show(player, Component.translatable("skillcraft.ability.improved", ability.name(),
                        ability.improvement(award.newLevel())).withStyle(ChatFormatting.GOLD));
            }
        }
    }
}