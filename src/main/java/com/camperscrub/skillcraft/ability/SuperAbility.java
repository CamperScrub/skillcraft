package com.camperscrub.skillcraft.ability;

import com.camperscrub.skillcraft.skill.Skill;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * One skill's super ability. {@link AbilityService} runs the timer and messages; an ability only
 * says which tool readies it, which blocks start it, and what happens while it's on.
 * Adding Lumber Frenzy later means adding a class like this, not editing the service.
 */
public interface SuperAbility {

    /** Stable id, used for the timer and translation keys, such as "super_breaker". */
    String id();

    Skill skill();

    /** True if holding {@code stack} lets the player ready this ability. */
    boolean isTool(ItemStack stack);

    /** True if starting to break {@code state} with the tool activates a readied ability. */
    boolean activatesOn(BlockState state);

    /**
     * How much faster {@code state} breaks while the ability is active. 1 means no change.
     * Applied on both sides by {@link BreakSpeedBoosts}.
     */
    default float breakSpeedMultiplier(BlockState state) {
        return 1;
    }

    /**
     * What the growing effect is at {@code level}, for the "improved" message. The default is the
     * gathering abilities' Yield bonus ("+10 Yield"); Swordsmanship's ability will override it.
     */
    default Component improvement(int level) {
        return Component.translatable("skillcraft.ability.bonus_yield",
                AbilityYield.YIELD_PER_STEP * AbilityTimings.steps(level));
    }

    /** Called once when the ability starts. */
    void start(ServerPlayer player, int level);

    /** Called once when it ends, to undo whatever {@link #start} did. */
    void end(ServerPlayer player);

    default Component name() {
        return Component.translatable("ability.skillcraft." + id());
    }
}
