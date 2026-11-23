package com.camperscrub.skillcraft.skill.excavation;

import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.BlockXpTable;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

public final class ExcavationXp {

    private ExcavationXp() {}

    public static BlockXpTable table() {
        return BlockXpTable.builder(Skill.EXCAVATION)
                .block(Blocks.MUDDY_MANGROVE_ROOTS, 90)
                .block(Blocks.MUD, 80)
                .block(Blocks.ROOTED_DIRT, 60)
                .block(Blocks.DIRT, 40)
                .block(Blocks.GRASS_BLOCK, 40)
                .block(Blocks.COARSE_DIRT, 40)
                .block(Blocks.PODZOL, 40)
                .block(Blocks.MYCELIUM, 40)
                .block(Blocks.CLAY, 40)
                .block(Blocks.SNOW_BLOCK, 40)
                .block(Blocks.SOUL_SAND, 40)
                .block(Blocks.SOUL_SOIL, 40)
                .tag(BlockTags.SAND, 40)                                // sand and red sand
                .tag(ConventionalBlockTags.GRAVELS, 40)
                .tag(ConventionalBlockTags.SANDS, 40)
                .block(Blocks.SNOW, 20)                                 // snow layer
                .block(Blocks.SNOW, 20)
                .exclude(Blocks.MOSS_BLOCK)
                .exclude(Blocks.PALE_MOSS_BLOCK)
                .tag(BlockTags.DIRT, 40)            // fallback: modded dirt
                .build();
    }
}