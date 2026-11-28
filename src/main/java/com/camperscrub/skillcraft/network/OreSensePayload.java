package com.camperscrub.skillcraft.network;

import com.camperscrub.skillcraft.SkillCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

/** Server → client: the ores Ore Sense should show right now. An empty list clears them. */
public record OreSensePayload(List<BlockPos> ores) implements CustomPacketPayload {

    public static final Type<OreSensePayload> TYPE = new Type<>(SkillCraft.id("ore_sense"));
    public static final StreamCodec<ByteBuf, OreSensePayload> CODEC =
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()).map(OreSensePayload::new, OreSensePayload::ores);

    @Override
    public Type<OreSensePayload> type() {
        return TYPE;
    }
}