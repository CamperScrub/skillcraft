package com.camperscrub.skillcraft.ability;

import net.minecraft.server.level.ServerPlayer;

/**
 * Told every time one of a player's abilities changes phase. The network layer uses it to keep the
 * client's copy up to date, which the client needs for break speed now and the HUD later.
 */
@FunctionalInterface
public interface AbilityStatusListener {

    /**
     * @param ticksLeft   ticks until this phase ends (0 for READY)
     * @param activeTicks how long the ability would run if activated now, so the client can
     *                    start it on its side the moment the player starts mining
     */
    void onStatus(ServerPlayer player, SuperAbility ability, AbilityPhase phase, long ticksLeft, long activeTicks);
}
