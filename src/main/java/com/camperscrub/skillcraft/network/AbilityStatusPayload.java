package com.camperscrub.skillcraft.network;

import com.camperscrub.skillcraft.SkillCraft;
import com.camperscrub.skillcraft.ability.AbilityPhase;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server → client: one of your abilities changed phase. See {@code AbilityStatusListener}. */
public record AbilityStatusPayload(String abilityId, AbilityPhase phase, int ticksLeft, int activeTicks)
        implements CustomPacketPayload {

    private static final StreamCodec<ByteBuf, AbilityPhase> PHASE_CODEC =
            ByteBufCodecs.VAR_INT.map(i -> AbilityPhase.values()[i], AbilityPhase::ordinal);

    public static final Type<AbilityStatusPayload> TYPE = new Type<>(SkillCraft.id("ability_status"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityStatusPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AbilityStatusPayload::abilityId,
            PHASE_CODEC, AbilityStatusPayload::phase,
            ByteBufCodecs.VAR_INT, AbilityStatusPayload::ticksLeft,
            ByteBufCodecs.VAR_INT, AbilityStatusPayload::activeTicks,
            AbilityStatusPayload::new);

    @Override
    public Type<AbilityStatusPayload> type() {
        return TYPE;
    }
}
