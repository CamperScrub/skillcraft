package com.camperscrub.skillcraft.yield;

import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.SkillLevels;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A level 0 Yield passive: 0.1 Yield per level in {@code skill} (100 at level 1000), on the
 * blocks in {@code blocks}. Mining's Bountiful Veins and Woodcutting's Timber Yield are both
 * one of these, with a different skill and tag.
 */
public final class LevelYield implements YieldSource {

    private final Skill skill;
    private final TagKey<Block> blocks;
    private final SkillLevels levels;

    public LevelYield(Skill skill, TagKey<Block> blocks, SkillLevels levels) {
        this.skill = skill;
        this.blocks = blocks;
        this.levels = levels;
    }

    /**
     * Yield at a level: 0 → 0, 500 → 50, 812 → 81.2, 1000 → 100. Divides by 10.0 rather than
     * multiplying by 0.1, so every level gives the exact tenth.
     */
    public static double yieldAtLevel(int level) {
        return level / 10.0;
    }

    @Override
    public double yieldFor(ServerPlayer player, BlockState state) {
        if (!state.is(blocks)) {
            return 0;
        }
        return yieldAtLevel(levels.level(player, skill));
    }
}