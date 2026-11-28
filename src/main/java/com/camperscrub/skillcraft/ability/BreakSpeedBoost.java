package com.camperscrub.skillcraft.ability;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

/** Something that speeds up how fast a player breaks a block. 1 means no change. */
@FunctionalInterface
public interface BreakSpeedBoost {

    float multiplier(Player player, BlockState state);
}
