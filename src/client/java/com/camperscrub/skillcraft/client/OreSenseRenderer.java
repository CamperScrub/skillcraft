package com.camperscrub.skillcraft.client;

import com.camperscrub.skillcraft.network.OreSensePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;

import java.util.List;

/**
 * Draws Ore Sense: an outline through walls around each ore the server sent, in that ore's colour.
 *
 * <p>Uses vanilla's gizmos (the same system as the debug outlines). Fabric's BEFORE_GIZMOS event
 * runs just before the frame's gizmos are gathered, so anything added there is drawn this frame.
 */
public final class OreSenseRenderer {

    /** If the server goes quiet for 3 seconds (lag, a missed message), stop drawing. */
    private static final long STALE_TICKS = 60;
    private static final float LINE_WIDTH = 2.0f;

    private List<BlockPos> ores = List.of();
    private long receivedAt;

    public void register() {
        // Handlers run on the client thread.
        ClientPlayNetworking.registerGlobalReceiver(OreSensePayload.TYPE, (payload, context) -> {
            ores = payload.ores();
            receivedAt = context.client().level.getGameTime();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ores = List.of());
        LevelRenderEvents.BEFORE_GIZMOS.register(context -> draw());
    }

    private void draw() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || ores.isEmpty()) {
            return;
        }
        if (level.getGameTime() - receivedAt > STALE_TICKS) {
            ores = List.of();
            return;
        }
        for (BlockPos pos : ores) {
            int colour = OreColours.colourOf(level.getBlockState(pos));
            if (colour != 0) { // 0: mined since the last scan
                Gizmos.cuboid(pos, GizmoStyle.stroke(colour, LINE_WIDTH)).setAlwaysOnTop();
            }
        }
    }
}