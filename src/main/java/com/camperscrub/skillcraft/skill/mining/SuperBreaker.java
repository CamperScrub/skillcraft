package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.ability.SuperAbility;
import com.camperscrub.skillcraft.skill.Skill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mining's super ability. While active: near-instant mining of pickaxe blocks and bonus Mining
 * Yield ({@code AbilityYield}, wired in SkillCraft). Ore Sense comes next.
 */
public final class SuperBreaker implements SuperAbility {

    /** 5× break speed: most pickaxe blocks break in one to three ticks. */
    private static final float SPEED_MULTIPLIER = 5;

    @Override
    public String id() {
        return "super_breaker";
    }

    @Override
    public Skill skill() {
        return Skill.MINING;
    }

    @Override
    public boolean isTool(ItemStack stack) {
        return stack.is(ItemTags.PICKAXES);
    }

    @Override
    public boolean activatesOn(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public float breakSpeedMultiplier(BlockState state) {
        return activatesOn(state) ? SPEED_MULTIPLIER : 1;
    }

    @Override
    public void start(ServerPlayer player, int level) {
        // Speed comes from breakSpeedMultiplier and Yield from AbilityYield. Ore Sense will start here.
    }

    @Override
    public void end(ServerPlayer player) {
    }
}
