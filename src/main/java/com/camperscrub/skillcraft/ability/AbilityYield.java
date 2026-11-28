package com.camperscrub.skillcraft.ability;

import com.camperscrub.skillcraft.xp.SkillLevels;
import com.camperscrub.skillcraft.yield.YieldSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The growing effect of a gathering ability: +5 Yield per step on the skill's Yield blocks, only
 * while the ability is active. Super Breaker uses it now; Lumber Frenzy and Giga Drill Breaker
 * can reuse it.
 */
public final class AbilityYield implements YieldSource {

    public static final int YIELD_PER_STEP = 5;

    private final SuperAbility ability;
    private final TagKey<Block> blocks;
    private final AbilityService abilities;
    private final SkillLevels levels;

    public AbilityYield(SuperAbility ability, TagKey<Block> blocks, AbilityService abilities, SkillLevels levels) {
        this.ability = ability;
        this.blocks = blocks;
        this.abilities = abilities;
        this.levels = levels;
    }

    @Override
    public double yieldFor(ServerPlayer player, BlockState state) {
        if (!state.is(blocks) || !abilities.isActive(player, ability)) {
            return 0;
        }
        return YIELD_PER_STEP * AbilityTimings.steps(levels.level(player, ability.skill()));
    }
}