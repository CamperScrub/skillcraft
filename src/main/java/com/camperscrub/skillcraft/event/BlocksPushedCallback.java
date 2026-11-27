package com.camperscrub.skillcraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Fired on the server when a piston is about to move blocks. {@code from.get(i)} ends up at
 * {@code to.get(i)}. {@code destroyed} are blocks the piston breaks instead (like torches).
 */
public interface BlocksPushedCallback {

    Event<BlocksPushedCallback> EVENT = EventFactory.createArrayBacked(BlocksPushedCallback.class,
            listeners -> (level, from, to, destroyed) -> {
                for (BlocksPushedCallback listener : listeners) {
                    listener.onBlocksPushed(level, from, to, destroyed);
                }
            });

    void onBlocksPushed(Level level, List<BlockPos> from, List<BlockPos> to, List<BlockPos> destroyed);
}
