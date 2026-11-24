package com.camperscrub.skillcraft.data;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * Remembers where mobs came from.
 */
public interface SpawnOriginTracker {

    void record(Mob mob, EntitySpawnReason reason);

    SpawnOrigin originOf(LivingEntity entity);
}