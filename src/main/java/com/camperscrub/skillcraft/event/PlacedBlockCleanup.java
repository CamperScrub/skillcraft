package com.camperscrub.skillcraft.event;

import com.camperscrub.skillcraft.data.PlacedBlockTracker;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Forgets a player-placed block once everything about breaking it has finished.
 *
 * <p>Breaking a block happens in steps: Fabric's AFTER event fires when the block is removed, and
 * the drops are rolled a moment later. Yield checks the tracker while the drops roll, so the mark
 * has to last until then. Broken positions are queued and forgotten at the end of the tick.
 */
public final class PlacedBlockCleanup {

    private record Broken(Level level, BlockPos pos) {
    }

    private final PlacedBlockTracker placedBlocks;
    private final List<Broken> toForget = new ArrayList<>(); // server thread only

    public PlacedBlockCleanup(PlacedBlockTracker placedBlocks) {
        this.placedBlocks = placedBlocks;
    }

    public void register() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) ->
                toForget.add(new Broken(level, pos.immutable())));
        // A block placed in the same spot before the tick ends is a new placed block: keep its mark.
        BlockPlacedCallback.EVENT.register((level, pos) -> toForget.remove(new Broken(level, pos)));
        ServerTickEvents.END_SERVER_TICK.register(server -> forgetQueued());
    }

    private void forgetQueued() {
        for (Broken broken : toForget) {
            placedBlocks.unmark(broken.level(), broken.pos());
        }
        toForget.clear();
    }
}