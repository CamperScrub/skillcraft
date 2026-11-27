package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.SkillCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class MiningTags {

    private MiningTags() {
    }

    /**
     * Valuable blocks that Mining Yield works on. Filled from
     * {@code data/skillcraft/tags/block/mining_yield.json}, so modpacks can add their own.
     */
    public static final TagKey<Block> MINING_YIELD = TagKey.create(Registries.BLOCK, SkillCraft.id("mining_yield"));
}