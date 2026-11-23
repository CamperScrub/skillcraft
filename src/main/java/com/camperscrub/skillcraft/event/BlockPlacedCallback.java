package com.camperscrub.skillcraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Fired on the server after a block item successfully places a block.
 */
public interface BlockPlacedCallback {

    Event<BlockPlacedCallback> EVENT = EventFactory.createArrayBacked(BlockPlacedCallback.class,
            listeners -> (level, pos) -> {
                for (BlockPlacedCallback listener : listeners) {
                    listener.onBlockPlaced(level, pos);
                }
            });

    void onBlockPlaced(Level level, BlockPos pos);
}