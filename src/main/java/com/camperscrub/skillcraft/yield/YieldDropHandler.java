package com.camperscrub.skillcraft.yield;

import com.camperscrub.skillcraft.data.PlacedBlockTracker;
import com.camperscrub.skillcraft.drops.NaturalBreak;
import com.camperscrub.skillcraft.proc.ProcKey;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

/**
 * Adds extra copies of a block's drops when the player who broke it has Yield.
 *
 * <p>Uses Fabric's MODIFY_DROPS event, which runs after the block's loot table has rolled. Fortune
 * and Silk Touch are already applied by then, so we copy exactly what dropped.
 */
public final class YieldDropHandler {

    private final List<YieldSource> sources;
    private final YieldRoller roller;
    private final PlacedBlockTracker placedBlocks;

    public YieldDropHandler(List<YieldSource> sources, YieldRoller roller, PlacedBlockTracker placedBlocks) {
        this.sources = List.copyOf(sources);
        this.roller = roller;
        this.placedBlocks = placedBlocks;
    }

    public void register() {
        LootTableEvents.MODIFY_DROPS.register(this::onDrops);
    }

    private void onDrops(Holder<LootTable> table, LootContext context, List<ItemStack> drops) {
        if (drops.isEmpty()) {
            return;
        }
        Optional<NaturalBreak> found = NaturalBreak.from(table, context, placedBlocks);
        if (found.isEmpty()) {
            return;
        }
        NaturalBreak broken = found.get();

        double yield = 0;
        for (YieldSource source : sources) {
            yield += source.yieldFor(broken.player(), broken.state());
        }
        if (yield <= 0) {
            return;
        }

        // One counter per kind of drop, so cheap ores never build up the chance for rare ones.
        String dropId = BuiltInRegistries.ITEM.getKey(drops.getFirst().getItem()).toString();
        int copies = roller.extraCopies(broken.player().getUUID(), new ProcKey("yield", dropId), yield);
        addCopies(drops, copies);
    }

    private static void addCopies(List<ItemStack> drops, int copies) {
        List<ItemStack> original = List.copyOf(drops);
        for (int i = 0; i < copies; i++) {
            for (ItemStack stack : original) {
                drops.add(stack.copy());
            }
        }
    }
}