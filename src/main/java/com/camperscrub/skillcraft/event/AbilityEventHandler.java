package com.camperscrub.skillcraft.event;

import com.camperscrub.skillcraft.ability.AbilityService;
import com.camperscrub.skillcraft.network.ReadyAbilityPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

/** Connects the ability service to Minecraft: the key packet, starting to break a block, and the server tick. */
public final class AbilityEventHandler {

    private final AbilityService abilities;

    public AbilityEventHandler(AbilityService abilities) {
        this.abilities = abilities;
    }

    public void register() {
        PayloadTypeRegistry.serverboundPlay().register(ReadyAbilityPayload.TYPE, ReadyAbilityPayload.CODEC);
        // Fabric runs play payload handlers on the server thread.
        ServerPlayNetworking.registerGlobalReceiver(ReadyAbilityPayload.TYPE,
                (payload, context) -> abilities.pressReadyKey(context.player()));

        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                abilities.onStartBreaking(serverPlayer, level.getBlockState(pos));
            }
            return InteractionResult.PASS;
        });

        ServerTickEvents.END_SERVER_TICK.register(abilities::tick);
    }
}