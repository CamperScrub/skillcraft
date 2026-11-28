package com.camperscrub.skillcraft.drops;

import com.camperscrub.skillcraft.data.PlacedBlockTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * A natural block broken by a player, worked out from a loot roll. Every passive that changes
 * block drops starts with {@link #from}, so the rules for "does this count" live in one place.
 */
public record NaturalBreak(ServerPlayer player, BlockState state, BlockPos pos) {

    /**
     * The break behind this loot roll, or empty if it doesn't count: not a block's drops, not
     * broken by a player in survival, a table the block pulls in rather than its own, or a block
     * a player placed.
     */
    public static Optional<NaturalBreak> from(Holder<LootTable> table, LootContext context,
                                              PlacedBlockTracker placedBlocks) {
        BlockState state = context.getOptional(LootContextParams.BLOCK_STATE);
        Vec3 origin = context.getOptional(LootContextParams.ORIGIN);
        if (state == null || origin == null
                || !(context.getOptional(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player)
                || player.isCreative()) {
            return Optional.empty();
        }
        // Only the block's own table, not one it pulls in (or one of ours), so nothing runs twice.
        Optional<ResourceKey<LootTable>> blockTable = state.getBlock().getLootTable();
        if (blockTable.isEmpty() || !table.is(blockTable.get())) {
            return Optional.empty();
        }
        BlockPos pos = BlockPos.containing(origin);
        if (placedBlocks.isPlaced(context.getLevel(), pos)) {
            return Optional.empty();
        }
        return Optional.of(new NaturalBreak(player, state, pos));
    }
}