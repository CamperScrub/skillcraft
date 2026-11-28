package com.camperscrub.skillcraft.ability;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The boosts that {@code PlayerMixin} applies to break speed.
 *
 * <p>This is the one static class in the mod, and only because a mixin can't be given its
 * dependencies through a constructor. The composition roots register into it and nothing else
 * reads it. The server registers a boost for server players and the client one for its own
 * player, so both sides work out the same speed.
 */
public final class BreakSpeedBoosts {

    private static final List<BreakSpeedBoost> BOOSTS = new CopyOnWriteArrayList<>();

    private BreakSpeedBoosts() {
    }

    public static void register(BreakSpeedBoost boost) {
        BOOSTS.add(boost);
    }

    public static float multiplier(Player player, BlockState state) {
        float multiplier = 1;
        for (BreakSpeedBoost boost : BOOSTS) {
            multiplier *= boost.multiplier(player, state);
        }
        return multiplier;
    }
}
