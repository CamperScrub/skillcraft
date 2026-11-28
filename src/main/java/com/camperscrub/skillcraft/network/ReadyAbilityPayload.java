package com.camperscrub.skillcraft.network;

import com.camperscrub.skillcraft.SkillCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client → server: "the player pressed the ready-ability key". It carries no data; the server
 * looks at what the player is holding and decides everything else.
 */
public record ReadyAbilityPayload() implements CustomPacketPayload {

    public static final ReadyAbilityPayload INSTANCE = new ReadyAbilityPayload();
    public static final Type<ReadyAbilityPayload> TYPE = new Type<>(SkillCraft.id("ready_ability"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReadyAbilityPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<ReadyAbilityPayload> type() {
        return TYPE;
    }
}