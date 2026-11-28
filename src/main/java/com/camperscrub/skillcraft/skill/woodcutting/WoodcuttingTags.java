package com.camperscrub.skillcraft.skill.woodcutting;

import com.camperscrub.skillcraft.SkillCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class WoodcuttingTags {

    private WoodcuttingTags() {
    }

    /**
     * Blocks that Woodcutting Yield works on. Filled from
     * {@code data/skillcraft/tags/block/woodcutting_yield.json}, so modpacks can add their own.
     */
    public static final TagKey<Block> WOODCUTTING_YIELD = TagKey.create(Registries.BLOCK, SkillCraft.id("woodcutting_yield"));
}