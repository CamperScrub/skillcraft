package com.camperscrub.skillcraft.ability;

import com.camperscrub.skillcraft.xp.XpAward;
import com.camperscrub.skillcraft.xp.XpListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/** Shows "✦ Super Breaker unlocked!" in the action bar when a skill reaches level 50. */
public final class AbilityUnlockMessage implements XpListener {

    private final List<SuperAbility> abilities;

    public AbilityUnlockMessage(List<SuperAbility> abilities) {
        this.abilities = List.copyOf(abilities);
    }

    @Override
    public void onXpAwarded(Player player, XpAward award) {
        if (!AbilityTimings.unlockedBetween(award.oldLevel(), award.newLevel())) {
            return;
        }
        for (SuperAbility ability : abilities) {
            if (ability.skill() == award.skill()) {
                player.sendOverlayMessage(Component.translatable("skillcraft.unlock.ability", ability.name())
                        .withStyle(ChatFormatting.GOLD));
            }
        }
    }
}