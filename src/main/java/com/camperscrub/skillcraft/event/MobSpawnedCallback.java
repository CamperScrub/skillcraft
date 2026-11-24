package com.camperscrub.skillcraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;

/**
 * Fired on the server when a mob finishes spawning, with the reason it spawned.
 */
public interface MobSpawnedCallback {

    Event<MobSpawnedCallback> EVENT = EventFactory.createArrayBacked(MobSpawnedCallback.class,
            listeners -> (mob, reason) -> {
                for (MobSpawnedCallback listener : listeners) {
                    listener.onMobSpawned(mob, reason);
                }
            });

    void onMobSpawned(Mob mob, EntitySpawnReason reason);
}