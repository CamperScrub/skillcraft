package com.camperscrub.skillcraft.data;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * Stores each non-natural mob's origin in a persistent attachment on the mob.
 */
public final class AttachmentSpawnOriginTracker implements SpawnOriginTracker {

    @Override
    public void record(Mob mob, EntitySpawnReason reason) {
        SpawnOrigin origin = classify(mob, reason);
        if (origin != SpawnOrigin.NATURAL) {
            mob.setAttached(SkillAttachments.SPAWN_ORIGIN, origin);
        }
    }

    @Override
    public SpawnOrigin originOf(LivingEntity entity) {
        return entity.getAttachedOrElse(SkillAttachments.SPAWN_ORIGIN, SpawnOrigin.NATURAL);
    }

    private static SpawnOrigin classify(Mob mob, EntitySpawnReason reason) {
        return switch (reason) {
            case SPAWNER -> SpawnOrigin.SPAWNER;
            case SPAWN_ITEM_USE, DISPENSER -> SpawnOrigin.SPAWN_EGG;
            // Nether portals spawn zombified piglins with the STRUCTURE reason; nothing else spawns them that way
            case STRUCTURE ->
                    mob.getType() == EntityTypes.ZOMBIFIED_PIGLIN ? SpawnOrigin.NETHER_PORTAL : SpawnOrigin.NATURAL;
            default -> SpawnOrigin.NATURAL; // natural, breeding, trial spawners, commands…
        };
    }
}