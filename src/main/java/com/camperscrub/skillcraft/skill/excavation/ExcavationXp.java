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
                .block(Blocks.CLAY, 60)
                .tag(BlockTags.DIRT, 40)                 // dirt, grass, podzol, mycelium...
                .tag(BlockTags.SAND, 40)
                .tag(ConventionalBlockTags.GRAVELS, 40)
                .block(Blocks.SOUL_SAND, 40)
                .block(Blocks.SOUL_SOIL, 40)
                .build();
    }
}