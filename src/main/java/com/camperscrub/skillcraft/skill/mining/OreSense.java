package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.ability.AbilityService;
import com.camperscrub.skillcraft.ability.SuperAbility;
import com.camperscrub.skillcraft.data.PlacedBlockTracker;
import com.camperscrub.skillcraft.network.OreSensePayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Super Breaker's Ore Sense: while the ability is active, natural ores within 16 blocks glow
 * through walls.
 *
 * <p>The server finds the ores, so a modified client can't see further than the ability allows.
 * It rescans once a second as the player moves and sends the positions; the client only draws
 * them. When the ability ends it sends an empty list, which clears the glow.
 */
public final class OreSense {

    public static final int RADIUS = 16;
    public static final int MAX_ORES = 200;
    public static final int REFRESH_TICKS = 20;

    private final AbilityService abilities;
    private final SuperAbility superBreaker;
    private final PlacedBlockTracker placedBlocks;
    private final BiConsumer<ServerPlayer, List<BlockPos>> send;
    private final Set<UUID> showing = new HashSet<>(); // server thread only

    public OreSense(AbilityService abilities, SuperAbility superBreaker, PlacedBlockTracker placedBlocks,
                    BiConsumer<ServerPlayer, List<BlockPos>> send) {
        this.abilities = abilities;
        this.superBreaker = superBreaker;
        this.placedBlocks = placedBlocks;
        this.send = send;
    }

    public void register() {
        // Registered in common code, so both sides know the payload.
        PayloadTypeRegistry.clientboundPlay().register(OreSensePayload.TYPE, OreSensePayload.CODEC);
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        boolean refresh = server.getTickCount() % REFRESH_TICKS == 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID id = player.getUUID();
            if (abilities.isActive(player, superBreaker)) {
                // Scan straight away when it starts, then once a second.
                if (showing.add(id) || refresh) {
                    send.accept(player, scan(player));
                }
            } else if (showing.remove(id)) {
                send.accept(player, List.of());
            }
        }
    }

    private List<BlockPos> scan(ServerPlayer player) {
        Level level = player.level();
        BlockPos centre = player.blockPosition();
        List<BlockPos> found = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(centre.offset(-RADIUS, -RADIUS, -RADIUS), centre.offset(RADIUS, RADIUS, RADIUS))) {
            if (pos.distSqr(centre) <= RADIUS * RADIUS
                    && level.getBlockState(pos).is(MiningTags.MINING_YIELD)
                    && !placedBlocks.isPlaced(level, pos)) {
                found.add(pos.immutable()); // betweenClosed reuses one mutable position
            }
        }
        return OreSenseScan.nearest(centre, found, MAX_ORES);
    }
}