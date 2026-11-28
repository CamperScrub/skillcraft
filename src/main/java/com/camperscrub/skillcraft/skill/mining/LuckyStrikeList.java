package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.SkillCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * One Lucky Strike list: which blocks use it, and the weighted loot table it rolls.
 * Both live in datapack files, so modpacks can change the blocks, items and weights.
 *
 * @param id        also the proc counter's tier, so each list has its own counter
 * @param blocks    {@code data/skillcraft/tags/block/lucky_strike/<id>.json}
 * @param lootTable {@code data/skillcraft/loot_table/lucky_strike/<id>.json}
 */
public record LuckyStrikeList(String id, TagKey<Block> blocks, ResourceKey<LootTable> lootTable) {

    public static LuckyStrikeList of(String id) {
        return new LuckyStrikeList(id,
                TagKey.create(Registries.BLOCK, SkillCraft.id("lucky_strike/" + id)),
                ResourceKey.create(Registries.LOOT_TABLE, SkillCraft.id("lucky_strike/" + id)));
    }
}