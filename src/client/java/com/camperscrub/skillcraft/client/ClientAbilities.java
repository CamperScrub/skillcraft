package com.camperscrub.skillcraft.client;

import com.camperscrub.skillcraft.ability.AbilityMirror;
import com.camperscrub.skillcraft.ability.BreakSpeedBoosts;
import com.camperscrub.skillcraft.ability.SuperAbility;
import com.camperscrub.skillcraft.network.AbilityStatusPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * The client side of super abilities: keeps the {@link AbilityMirror} in sync with the server,
 * starts a readied ability the moment the player starts mining, and applies the break speed
 * boost to the player's own mining so the client and server agree.
 */
public final class ClientAbilities {

    private final List<SuperAbility> abilities;
    private final AbilityMirror mirror = new AbilityMirror();

    public ClientAbilities(List<SuperAbility> abilities) {
        this.abilities = List.copyOf(abilities);
    }

    public void register() {
        // Handlers run on the client thread.
        ClientPlayNetworking.registerGlobalReceiver(AbilityStatusPayload.TYPE, (payload, context) ->
                mirror.update(payload.abilityId(), payload.phase(), context.client().level.getGameTime(),
                        payload.ticksLeft(), payload.activeTicks()));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> mirror.clear());

        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (level.isClientSide()) {
                BlockState state = level.getBlockState(pos);
                for (SuperAbility ability : abilities) {
                    if (ability.isTool(player.getMainHandItem()) && ability.activatesOn(state)) {
                        mirror.predictActivation(ability.id(), level.getGameTime());
                    }
                }
            }
            return InteractionResult.PASS;
        });

        BreakSpeedBoosts.register(this::breakSpeedMultiplier);
    }

    private float breakSpeedMultiplier(Player player, BlockState state) {
        if (!(player instanceof LocalPlayer)) {
            return 1;
        }
        long now = player.level().getGameTime();
        float multiplier = 1;
        for (SuperAbility ability : abilities) {
            if (mirror.isActive(ability.id(), now)) {
                multiplier *= ability.breakSpeedMultiplier(state);
            }
        }
        return multiplier;
    }
}
