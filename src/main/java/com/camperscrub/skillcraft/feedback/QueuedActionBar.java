package com.camperscrub.skillcraft.feedback;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An {@link ActionBar} that queues messages per player, so level-up, unlock and "improved"
 * messages that arrive together are shown one after another, 2 seconds each. Server thread only.
 */
public final class QueuedActionBar implements ActionBar {

    private static final long DISPLAY_TICKS = 40;

    private final Map<UUID, MessageQueue<Component>> queues = new HashMap<>();

    public void register() {
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    @Override
    public void show(Player player, Component message) {
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return; // client side: the server sends its own messages
        }
        queues.computeIfAbsent(player.getUUID(), id -> new MessageQueue<>(DISPLAY_TICKS))
                .offer(message, server.getTickCount())
                .ifPresent(player::sendOverlayMessage);
    }

    private void tick(MinecraftServer server) {
        long now = server.getTickCount();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            MessageQueue<Component> queue = queues.get(player.getUUID());
            if (queue != null) {
                queue.poll(now).ifPresent(player::sendOverlayMessage);
            }
        }
        // Forget players who have left.
        queues.keySet().removeIf(id -> server.getPlayerList().getPlayer(id) == null);
    }
}