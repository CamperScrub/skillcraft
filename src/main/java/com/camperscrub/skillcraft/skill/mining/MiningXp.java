package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.BlockXpTable;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;

public final class MiningXp {

    private MiningXp() {}

    public static BlockXpTable table() {
        return BlockXpTable.builder(Skill.MINING)
                .requiresCorrectTool()
                .tag(ConventionalBlockTags.NETHERITE_SCRAP_ORES, 7777) // ancient debris
                .tag(ConventionalBlockTags.DIAMOND_ORES, 2400)
                .tag(ConventionalBlockTags.EMERALD_ORES, 1000)
                .tag(ConventionalBlockTags.GOLD_ORES, 900)
                .tag(ConventionalBlockTags.LAPIS_ORES, 800)
                .tag(ConventionalBlockTags.REDSTONE_ORES, 600)
                .tag(ConventionalBlockTags.IRON_ORES, 600)
                .tag(ConventionalBlockTags.COAL_ORES, 400)
                .tag(ConventionalBlockTags.COPPER_ORES, 300)
                .tag(ConventionalBlockTags.QUARTZ_ORES, 300)
                .tag(ConventionalBlockTags.ORES, 300)        // any other ore, including modded ones
                .tag(ConventionalBlockTags.END_STONES, 50)
                .tag(ConventionalBlockTags.STONES, 30)
                .tag(ConventionalBlockTags.NETHERRACKS, 30)
                .build();
    }
}