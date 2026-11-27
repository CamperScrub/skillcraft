package com.camperscrub.skillcraft.yield;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Anything that gives Yield: a passive, an active ability, later a skill tree node. The drop
 * handler adds every source together, so a new source never means editing the handler.
 */
@FunctionalInterface
public interface YieldSource {

    /** The Yield this source gives {@code player} when they break {@code state}. 0 if it doesn't apply. */
    double yieldFor(ServerPlayer player, BlockState state);
}