package com.camperscrub.skillcraft.yield;

import com.camperscrub.skillcraft.data.PlacedBlockTracker;
import com.camperscrub.skillcraft.proc.ProcKey;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

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
        // MODIFY_DROPS fires for every loot table (chests, mobs, fishing...). Keep block drops
        // caused by a player.
        BlockState state = context.getOptional(LootContextParams.BLOCK_STATE);
        Vec3 origin = context.getOptional(LootContextParams.ORIGIN);
        if (state == null || origin == null
                || !(context.getOptional(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player)) {
            return;
        }
        // Only the block's own table, not one it pulls in, so the same drops are never copied twice.
        Optional<ResourceKey<LootTable>> blockTable = state.getBlock().getLootTable();
        if (blockTable.isEmpty() || !table.is(blockTable.get())) {
            return;
        }
        // Natural blocks only. Without this, silk-touched ore could be placed and re-mined forever.
        if (player.isCreative() || placedBlocks.isPlaced(context.getLevel(), BlockPos.containing(origin))) {
            return;
        }

        double yield = 0;
        for (YieldSource source : sources) {
            yield += source.yieldFor(player, state);
        }
        if (yield <= 0) {
            return;
        }

        // One counter per kind of drop, so cheap ores never build up the chance for rare ones.
        String dropId = BuiltInRegistries.ITEM.getKey(drops.getFirst().getItem()).toString();
        int copies = roller.extraCopies(player.getUUID(), new ProcKey("yield", dropId), yield);
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