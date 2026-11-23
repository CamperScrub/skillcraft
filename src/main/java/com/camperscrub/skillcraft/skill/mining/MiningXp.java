package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.BlockXpTable;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.world.level.block.Blocks;

public final class MiningXp {

    private MiningXp() {}

    public static BlockXpTable table() {
        return BlockXpTable.builder(Skill.MINING)
                .requiresCorrectTool()
                // Ores: deepslate versions first because they're worth more
                .tag(ConventionalBlockTags.NETHERITE_SCRAP_ORES, 7777)  // ancient debris
                .block(Blocks.DEEPSLATE_DIAMOND_ORE, 3600)
                .tag(ConventionalBlockTags.DIAMOND_ORES, 2400)
                .block(Blocks.DEEPSLATE_GOLD_ORE, 1900)
                .tag(ConventionalBlockTags.GOLD_ORES, 1300)             // includes nether gold ore
                .block(Blocks.DEEPSLATE_EMERALD_ORE, 1700)
                .tag(ConventionalBlockTags.EMERALD_ORES, 1000)
                .block(Blocks.DEEPSLATE_LAPIS_ORE, 1400)
                .tag(ConventionalBlockTags.LAPIS_ORES, 800)
                .block(Blocks.DEEPSLATE_IRON_ORE, 1300)
                .tag(ConventionalBlockTags.IRON_ORES, 900)
                .block(Blocks.DEEPSLATE_REDSTONE_ORE, 900)
                .tag(ConventionalBlockTags.REDSTONE_ORES, 600)
                .block(Blocks.DEEPSLATE_COPPER_ORE, 750)
                .tag(ConventionalBlockTags.COPPER_ORES, 500)
                .block(Blocks.DEEPSLATE_COAL_ORE, 700)
                .tag(ConventionalBlockTags.COAL_ORES, 400)
                .tag(ConventionalBlockTags.QUARTZ_ORES, 300)
                .tag(ConventionalBlockTags.ORES, 300)                   // SkillCraft extra: modded ores
                // Rarer natural blocks
                .block(Blocks.CRYING_OBSIDIAN, 3000)
                .block(Blocks.AMETHYST_BLOCK, 500)
                .block(Blocks.BONE_BLOCK, 500)                          // fossils
                .block(Blocks.CALCITE, 400)
                .block(Blocks.SMOOTH_BASALT, 300)
                .block(Blocks.GILDED_BLACKSTONE, 200)
                .block(Blocks.OBSIDIAN, 150)
                .block(Blocks.RED_SANDSTONE, 100)
                .block(Blocks.BLACKSTONE, 55)
                .block(Blocks.BASALT, 40)
                .block(Blocks.DRIPSTONE_BLOCK, 35)
                .block(Blocks.MAGMA_BLOCK, 30)
                .block(Blocks.SANDSTONE, 30)
                .block(Blocks.TERRACOTTA, 30)
                // Common stone
                .block(Blocks.DEEPSLATE, 30)
                .block(Blocks.TUFF, 10)
                .tag(ConventionalBlockTags.STONES, 15)                  // stone, granite, diorite, andesite
                .tag(ConventionalBlockTags.NETHERRACKS, 15)
                .tag(ConventionalBlockTags.END_STONES, 15)
                .build();
    }
}