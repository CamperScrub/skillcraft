package com.camperscrub.skillcraft.client;

import com.camperscrub.skillcraft.skill.mining.MiningTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Ore Sense outline colours (ARGB), one per kind of ore. Modded ores in these tags get the same colours. */
final class OreColours {

    private record Entry(TagKey<Block> ores, int colour) {
    }

    private static final List<Entry> BY_TAG = List.of(
            new Entry(ConventionalBlockTags.DIAMOND_ORES, 0xFF4FE3E8),
            new Entry(ConventionalBlockTags.EMERALD_ORES, 0xFF3CD26B),
            new Entry(ConventionalBlockTags.GOLD_ORES, 0xFFF2C12E),
            new Entry(ConventionalBlockTags.REDSTONE_ORES, 0xFFE0322C),
            new Entry(ConventionalBlockTags.LAPIS_ORES, 0xFF2F5BD9),
            new Entry(ConventionalBlockTags.IRON_ORES, 0xFFD8AF93),
            new Entry(ConventionalBlockTags.COPPER_ORES, 0xFFE0773C),
            new Entry(ConventionalBlockTags.COAL_ORES, 0xFF5A5A5A),
            new Entry(ConventionalBlockTags.QUARTZ_ORES, 0xFFEDE6DA),
            new Entry(ConventionalBlockTags.NETHERITE_SCRAP_ORES, 0xFF8A5A44));

    private static final int AMETHYST = 0xFFB57CF0;
    private static final int OTHER_ORE = 0xFFFFFFFF;

    private OreColours() {
    }

    /** The outline colour for {@code state}, or 0 if it isn't an Ore Sense block (for example, already mined). */
    static int colourOf(BlockState state) {
        if (!state.is(MiningTags.MINING_YIELD)) {
            return 0;
        }
        for (Entry entry : BY_TAG) {
            if (state.is(entry.ores())) {
                return entry.colour();
            }
        }
        return state.is(Blocks.AMETHYST_CLUSTER) ? AMETHYST : OTHER_ORE;
    }
}