package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.data.PlacedBlockTracker;
import com.camperscrub.skillcraft.drops.NaturalBreak;
import com.camperscrub.skillcraft.proc.ProcKey;
import com.camperscrub.skillcraft.proc.ProcRoller;
import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.SkillLevels;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

/**
 * Mining's level 400 passive: a chance for natural stone, deepslate or netherrack to drop one
 * bonus item from that block's list.
 *
 * <p>Register this after {@code YieldDropHandler}, so Yield never copies the bonus item.
 */
public final class LuckyStrikeDropHandler {

    private final List<LuckyStrikeList> lists;
    private final SkillLevels levels;
    private final ProcRoller procs;
    private final PlacedBlockTracker placedBlocks;

    public LuckyStrikeDropHandler(List<LuckyStrikeList> lists, SkillLevels levels, ProcRoller procs,
                                  PlacedBlockTracker placedBlocks) {
        this.lists = List.copyOf(lists);
        this.levels = levels;
        this.procs = procs;
        this.placedBlocks = placedBlocks;
    }

    public void register() {
        LootTableEvents.MODIFY_DROPS.register(this::onDrops);
    }

    private void onDrops(Holder<LootTable> table, LootContext context, List<ItemStack> drops) {
        // Empty drops means the wrong tool (stone needs a pickaxe), so no bonus either.
        if (drops.isEmpty()) {
            return;
        }
        Optional<NaturalBreak> found = NaturalBreak.from(table, context, placedBlocks);
        if (found.isEmpty()) {
            return;
        }
        NaturalBreak broken = found.get();
        Optional<LuckyStrikeList> list = listFor(broken);
        if (list.isEmpty()) {
            return;
        }

        double chance = LuckyStrike.chanceAtLevel(levels.level(broken.player(), Skill.MINING));
        ProcKey key = new ProcKey("lucky_strike", list.get().id());
        if (!procs.roll(broken.player().getUUID(), key, chance)) {
            return;
        }
        LootTable bonus = context.getLevel().getServer().reloadableRegistries().getLootTable(list.get().lootTable());
        bonus.getRandomItems(context, drops::add);
    }

    /** The first list whose block tag contains the broken block. */
    private Optional<LuckyStrikeList> listFor(NaturalBreak broken) {
        for (LuckyStrikeList list : lists) {
            if (broken.state().is(list.blocks())) {
                return Optional.of(list);
            }
        }
        return Optional.empty();
    }
}