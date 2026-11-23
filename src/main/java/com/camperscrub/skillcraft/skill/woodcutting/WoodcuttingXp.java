package com.camperscrub.skillcraft.skill.woodcutting;

import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.BlockXpTable;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.world.level.block.Blocks;

public final class WoodcuttingXp {

    private WoodcuttingXp() {
    }

    public static BlockXpTable table() {
        return BlockXpTable.builder(Skill.WOODCUTTING)
                .blocks(130, Blocks.PALE_OAK_LOG, Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_WOOD)
                .blocks(105, Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_CHERRY_WOOD)
                .blocks(100, Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD)
                .blocks(95, Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_WOOD)
                .blocks(90, Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD)
                .blocks(90, Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD)
                .blocks(90, Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD)
                .blocks(80, Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD)
                .blocks(70, Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD)
                .blocks(50, Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE,
                        Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_WARPED_STEM)
                .blocks(35, Blocks.CRIMSON_STEM, Blocks.WARPED_STEM)
                .block(Blocks.SHROOMLIGHT, 100)
                .block(Blocks.MUSHROOM_STEM, 80)
                .blocks(70, Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK)
                .block(Blocks.MANGROVE_ROOTS, 10)
                .tag(ConventionalBlockTags.NATURAL_LOGS, 70)   // fallback: any other (modded) log
                .tag(ConventionalBlockTags.STRIPPED_LOGS, 70)
                .build();
    }
}