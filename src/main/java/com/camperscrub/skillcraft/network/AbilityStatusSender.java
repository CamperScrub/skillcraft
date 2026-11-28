package com.camperscrub.skillcraft.network;

import com.camperscrub.skillcraft.ability.AbilityPhase;
import com.camperscrub.skillcraft.ability.AbilityStatusListener;
import com.camperscrub.skillcraft.ability.SuperAbility;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

/** Sends each ability phase change to that player's client. */
public final class AbilityStatusSender implements AbilityStatusListener {

    @Override
    public void onStatus(ServerPlayer player, SuperAbility ability, AbilityPhase phase, long ticksLeft, long activeTicks) {
        ServerPlayNetworking.send(player, new AbilityStatusPayload(ability.id(), phase, (int) ticksLeft, (int) activeTicks));
    }
}
